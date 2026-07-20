package software.sava.idl.clients.marginfi.v2;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.marginfi.v2.gen.types.Bank;
import software.sava.idl.clients.marginfi.v2.gen.types.OracleSetup;

import java.util.ArrayList;
import java.util.List;

/// Helper for assembling the "extra" (non-IDL) accounts that marginfi
/// instructions read from `ctx.remaining_accounts`. The generated
/// `MarginfiProgram` builders only carry IDL accounts, so these must be appended
/// to the built instruction via `Instruction.extraAccounts(..)`.
///
/// Ordering and counts are taken from the program's Rust
/// (`marginfi/src/utils/general.rs::maybe_take_bank_mint` and
/// `marginfi/src/state/marginfi_account.rs::get_remaining_accounts_per_bank`).
///
/// The payload has two parts, in this order:
///
/// 1. **The bank mint — Token-2022 banks only.** Token-moving instructions
///    ([MarginfiClient#deposit], [MarginfiClient#repay],
///    [MarginfiClient#withdraw], [MarginfiClient#borrow]) call
///    `maybe_take_bank_mint`, which splits the *first* remaining account off and
///    requires it to equal `bank.mint`. For an SPL Token bank it consumes
///    nothing and the mint must **not** be present. Getting this wrong fails with
///    `T22MintRequired`.
/// 2. **The risk-engine accounts**, one group per active balance on the marginfi
///    account. **The group size is not fixed** — a common mistake is to assume
///    `<bank, oracle>` pairs throughout, which only holds for ordinary banks:
///
///    | Bank | Accounts | Group |
///    |---|---|---|
///    | `OracleSetup.Fixed` | 1 | bank |
///    | `FixedKamino` / `FixedDrift` / `FixedJuplend` | 2 | bank, venue state |
///    | asset tag `DEFAULT` (0) or `SOL` (1) | 2 | bank, oracle |
///    | asset tag `KAMINO`/`DRIFT`/`SOLEND`/`JUPLEND` (3-6) | 3 | bank, oracle, reserve |
///    | asset tag `STAKED` (2) | 5 | bank, oracle, lst mint, stake pool, onramp |
///
///    A wrong count fails with `WrongNumberOfOracleAccounts`.
///
/// Token-2022 **transfer-hook** accounts, where the mint has one, are appended
/// after the risk-engine groups: the program forwards the whole remaining slice
/// to the transfer CPI (`state/bank.rs::invoke_client_token_transfer`).
public final class MarginfiRemainingAccounts {

  /// `ASSET_TAG_DEFAULT` — an ordinary SPL asset.
  public static final int ASSET_TAG_DEFAULT = 0;
  /// `ASSET_TAG_SOL` — native SOL.
  public static final int ASSET_TAG_SOL = 1;
  /// `ASSET_TAG_STAKED` — a staked-SOL bank, which carries three extra accounts.
  public static final int ASSET_TAG_STAKED = 2;
  /// `ASSET_TAG_KAMINO` — a Kamino integration bank.
  public static final int ASSET_TAG_KAMINO = 3;
  /// `ASSET_TAG_DRIFT` — a Drift integration bank.
  public static final int ASSET_TAG_DRIFT = 4;
  /// `ASSET_TAG_SOLEND` — a Solend integration bank.
  public static final int ASSET_TAG_SOLEND = 5;
  /// `ASSET_TAG_JUPLEND` — a JupLend integration bank.
  public static final int ASSET_TAG_JUPLEND = 6;

  /// How many remaining accounts a bank contributes to the risk-engine payload.
  /// Mirrors `get_remaining_accounts_per_bank`.
  public static int accountsPerBank(final OracleSetup oracleSetup, final int assetTag) {
    return switch (oracleSetup) {
      case Fixed -> 1;
      case FixedKamino, FixedDrift, FixedJuplend -> 2;
      default -> switch (assetTag) {
        case ASSET_TAG_STAKED -> 5;
        case ASSET_TAG_KAMINO, ASSET_TAG_DRIFT, ASSET_TAG_SOLEND, ASSET_TAG_JUPLEND -> 3;
        default -> 2;
      };
    };
  }

  /// Convenience overload reading the setup and tag off a fetched [Bank].
  public static int accountsPerBank(final Bank bank) {
    return accountsPerBank(bank.config().oracleSetup(), bank.config().assetTag());
  }

  public static Builder builder() {
    return new Builder();
  }

  /// Accumulates the payload in program order, validating each group's size
  /// against the bank it describes so a miscount fails here rather than on chain.
  public static final class Builder {

    private PublicKey bankMint;
    private final List<AccountMeta> riskEngine = new ArrayList<>();
    private final List<AccountMeta> transferHook = new ArrayList<>();

    private Builder() {
    }

    /// Token-2022 banks only: the bank's own mint, which the program splits off
    /// the front of the list. Omit entirely for SPL Token banks.
    public Builder bankMint(final PublicKey mint) {
      this.bankMint = mint;
      return this;
    }

    /// Append one balance's risk-engine group. `venueAccounts` are the accounts
    /// that follow the bank — the oracle and, depending on the bank, its reserve
    /// or staked-pool accounts. The count is checked against
    /// [#accountsPerBank(OracleSetup, int)].
    public Builder bank(final PublicKey bankKey,
                        final OracleSetup oracleSetup,
                        final int assetTag,
                        final PublicKey... venueAccounts) {
      final int expected = accountsPerBank(oracleSetup, assetTag);
      final int actual = 1 + venueAccounts.length;
      if (actual != expected) {
        throw new IllegalArgumentException(String.format(
            "bank %s with oracle setup %s and asset tag %d needs %d accounts, got %d",
            bankKey, oracleSetup, assetTag, expected, actual));
      }
      riskEngine.add(AccountMeta.createRead(bankKey));
      for (final var account : venueAccounts) {
        riskEngine.add(AccountMeta.createRead(account));
      }
      return this;
    }

    /// Overload for a fetched [Bank], which carries its own address, setup and tag.
    public Builder bank(final Bank bank, final PublicKey... venueAccounts) {
      return bank(bank._address(), bank.config().oracleSetup(), bank.config().assetTag(), venueAccounts);
    }

    /// The ordinary case: a bank whose group is `<bank, oracle>`.
    public Builder bankWithOracle(final PublicKey bankKey, final PublicKey oracleKey) {
      return bank(bankKey, OracleSetup.PythLegacy, ASSET_TAG_DEFAULT, oracleKey);
    }

    /// Token-2022 transfer-hook accounts, forwarded to the transfer CPI. These
    /// go last, after every risk-engine group.
    public Builder transferHookAccounts(final List<AccountMeta> accounts) {
      this.transferHook.addAll(accounts);
      return this;
    }

    public List<AccountMeta> build() {
      final var accounts = new ArrayList<AccountMeta>(
          (bankMint == null ? 0 : 1) + riskEngine.size() + transferHook.size());
      if (bankMint != null) {
        accounts.add(AccountMeta.createRead(bankMint));
      }
      accounts.addAll(riskEngine);
      accounts.addAll(transferHook);
      return List.copyOf(accounts);
    }
  }

  private MarginfiRemainingAccounts() {
  }
}

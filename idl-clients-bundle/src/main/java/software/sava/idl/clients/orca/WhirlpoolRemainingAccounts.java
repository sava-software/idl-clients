package software.sava.idl.clients.orca;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.orca.whirlpools.gen.types.AccountsType;
import software.sava.idl.clients.orca.whirlpools.gen.types.RemainingAccountsInfo;
import software.sava.idl.clients.orca.whirlpools.gen.types.RemainingAccountsSlice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/// Helper for assembling the "extra" accounts that Whirlpool V2 instructions
/// read from `ctx.remaining_accounts` but which are not present in the IDL
/// account list (and therefore not emitted by the generated `WhirlpoolProgram`
/// builders).
///
/// Two categories exist on chain:
///
/// 1. Token-2022 **transfer-hook** accounts. For every token mint passed to a
///    V2 instruction that carries the `TransferHook` extension, the caller
///    must append, in order:
///      - the transfer-hook program id (read-only),
///      - the per-mint `extra-account-metas` PDA owned by the hook program
///        (seeds: `["extra-account-metas", mint]`, read-only),
///      - every account listed by that PDA (with the writable/signer flags
///        declared there, after resolving any seed-based metas against the
///        current transfer context).
///    The slice kind identifies *which* mint the hook accounts belong to:
///    `TransferHookA`/`TransferHookB` for liquidity/collect, `TransferHookReward`
///    for `collectRewardV2`, `TransferHookInput`/`TransferHookIntermediate`/
///    `TransferHookOutput` for `twoHopSwapV2`, and `TransferHookDepositA/B` /
///    `TransferHookWithdrawalA/B` for `repositionLiquidityV2`.
///
/// 2. **Supplemental tick arrays** for swaps. `swapV2` accepts up to three
///    extra tick array accounts (slice `SupplementalTickArrays`); `twoHopSwapV2`
///    accepts the same per hop via `SupplementalTickArraysOne` /
///    `SupplementalTickArraysTwo`. These let a single swap traverse more than
///    the three tick arrays declared in the IDL.
///
/// Usage:
///
/// ```
/// var extras = WhirlpoolRemainingAccounts.builder()
///     .addSlice(AccountsType.TransferHookA, hookProgramA, extraAccountMetasPdaA, ...resolvedMetasA)
///     .addSlice(AccountsType.TransferHookB, hookProgramB, extraAccountMetasPdaB, ...resolvedMetasB)
///     .addSupplementalTickArrays(supTa0, supTa1, supTa2)
///     .build();
///
/// var ix = client.swapV2(...args..., extras.info());
/// ix = WhirlpoolRemainingAccounts.append(ix, extras);
/// ```
///
/// `RemainingAccountsInfo` (slice kinds + lengths) MUST be passed to the V2
/// builder so the on-chain program knows how to partition the appended
/// remaining accounts. The flat account list returned by `accounts()` MUST be
/// appended to the produced `Instruction` in the same slice order.
public final class WhirlpoolRemainingAccounts {

  private final RemainingAccountsInfo info;
  private final List<AccountMeta> accounts;

  private WhirlpoolRemainingAccounts(final RemainingAccountsInfo info, final List<AccountMeta> accounts) {
    this.info = info;
    this.accounts = accounts;
  }

  public RemainingAccountsInfo info() {
    return info;
  }

  public List<AccountMeta> accounts() {
    return accounts;
  }

  /// Appends the assembled remaining accounts to `instruction`. The caller is
  /// responsible for passing the matching `info()` to the V2 builder when the
  /// instruction was produced.
  public static Instruction append(final Instruction instruction, final WhirlpoolRemainingAccounts extras) {
    if (extras == null || extras.accounts.isEmpty()) {
      return instruction;
    }
    return instruction.extraAccounts(extras.accounts);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private final List<RemainingAccountsSlice> slices = new ArrayList<>();
    private final List<AccountMeta> accounts = new ArrayList<>();

    private Builder() {
    }

    /// Adds a slice of remaining accounts. The accounts are appended in the
    /// order given; their count is recorded against `type` in the
    /// `RemainingAccountsInfo`. Pass each account as a read-only `AccountMeta`
    /// unless the transfer-hook's `ExtraAccountMetaList` declares it
    /// writable/signer — in that case use `AccountMeta.createWrite(...)` /
    /// `createWritableSigner(...)` accordingly.
    public Builder addSlice(final AccountsType type, final List<AccountMeta> sliceAccounts) {
      if (sliceAccounts.isEmpty()) {
        return this;
      }
      slices.add(new RemainingAccountsSlice(type, sliceAccounts.size()));
      accounts.addAll(sliceAccounts);
      return this;
    }

    public Builder addSlice(final AccountsType type, final AccountMeta... sliceAccounts) {
      return addSlice(type, Arrays.asList(sliceAccounts));
    }

    /// Convenience for transfer-hook slices. Appends, in order: the hook
    /// program id, the per-mint `extra-account-metas` PDA, and each resolved
    /// extra account meta. All are added as read-only by default; pass an
    /// explicit `AccountMeta` via `addSlice(AccountsType, List)` if the hook's
    /// `ExtraAccountMetaList` marks any of them writable or signer.
    public Builder addTransferHook(final AccountsType type,
                                   final PublicKey hookProgramId,
                                   final PublicKey extraAccountMetasPda,
                                   final List<PublicKey> resolvedExtraAccounts) {
      final var metas = new ArrayList<AccountMeta>(2 + resolvedExtraAccounts.size());
      metas.add(AccountMeta.createRead(hookProgramId));
      metas.add(AccountMeta.createRead(extraAccountMetasPda));
      for (final var extra : resolvedExtraAccounts) {
        metas.add(AccountMeta.createRead(extra));
      }
      return addSlice(type, metas);
    }

    /// Convenience for `SupplementalTickArrays` (used by `swapV2`).
    public Builder addSupplementalTickArrays(final PublicKey... tickArrays) {
      return addSupplementalTickArrays(AccountsType.SupplementalTickArrays, tickArrays);
    }

    /// Convenience for `SupplementalTickArraysOne` / `SupplementalTickArraysTwo`
    /// (used by `twoHopSwapV2`). Pick the matching slice kind for the hop.
    public Builder addSupplementalTickArrays(final AccountsType type, final PublicKey... tickArrays) {
      if (tickArrays.length == 0) {
        return this;
      }
      final var metas = new ArrayList<AccountMeta>(tickArrays.length);
      for (final var ta : tickArrays) {
        metas.add(AccountMeta.createRead(ta));
      }
      return addSlice(type, metas);
    }

    public WhirlpoolRemainingAccounts build() {
      final var info = slices.isEmpty()
          ? null
          : new RemainingAccountsInfo(slices.toArray(RemainingAccountsSlice[]::new));
      return new WhirlpoolRemainingAccounts(info, List.copyOf(accounts));
    }
  }
}

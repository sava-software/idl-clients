package software.sava.idl.clients.kamino.vaults;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;

import java.util.Collection;

/// Helper for appending the "extra" (non-IDL) accounts that several KaminoVault
/// instructions read from `ctx.remaining_accounts`. The generated
/// `KaminoVaultProgram` builders only carry IDL accounts, so callers must append
/// these extras via `Instruction.extraAccounts(...)` after building the instruction.
///
/// Account ordering and writability are derived from the upstream Rust handlers
/// under `kvault/programs/kvault/src/handlers/`:
///
/// - `deposit`, `withdraw`, `redeemInKind`: the handler iterates the first
///   `vault_state.get_reserves_count()` accounts in `remaining_accounts` and treats
///   each one as a `Reserve` to refresh via a klend CPI. The order must match the
///   vault's allocation order in `VaultState` and each entry must be writable
///   (the reserve account is written to by `RefreshReserve`). The associated
///   `lending_market` key is loaded from each reserve's state by the program and
///   is **not** passed as a separate remaining account.
/// - `withdrawFromAvailable`: no `remaining_accounts`.
public final class KaminoVaultsRemainingAccounts {

  private KaminoVaultsRemainingAccounts() {
  }

  /// Append the vault's reserve accounts (in allocation order) used by `deposit`,
  /// `withdraw`, and `redeemInKind`. Each reserve is appended as writable since
  /// the inner `RefreshReservesBatch` CPI writes back to it.
  public static Instruction appendVaultReserves(final Instruction instruction,
                                                final Collection<PublicKey> reserves) {
    return instruction.extraAccounts(reserves, AccountMeta.CREATE_WRITE);
  }
}

package software.sava.idl.clients.kamino.lend;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/// Helper for appending the "extra" (non-IDL) accounts that several KaminoLending
/// instructions read from `ctx.remaining_accounts`. The generated `KaminoLendingProgram`
/// builders only carry IDL accounts, so callers must append these extras via
/// `Instruction.extraAccounts(...)` after building the instruction.
///
/// Account ordering and writability are derived from the upstream Rust handlers under
/// `klend/programs/klend/src/handlers/`.
///
/// - `refreshObligation`:
///   `[deposit_reserves(N_d) , borrow_reserves(N_b) , referrer_token_states(N_b if has_referrer)]`.
///   All reserve and referrer-token-state accounts are written to during refresh.
/// - `borrowObligationLiquidity[V2]` / `requestElevationGroup`:
///   `[deposit_reserves , (optional) permission_account]`.
/// - `repayObligationLiquidity[V2]` / `depositReserveLiquidity[V2]` /
///   `depositObligationCollateral[V2]` /
///   `depositReserveLiquidityAndObligationCollateral[V2]`:
///   trailing optional `[permission_account]` only.
///
/// The permission account is read-only.
public final class KaminoLendingRemainingAccounts {

  private KaminoLendingRemainingAccounts() {
  }

  /// Append the remaining accounts required by `refreshObligation`.
  ///
  /// The order is strict: all deposit reserves, then all borrow reserves, then (only when
  /// the obligation has a referrer) one `ReferrerTokenState` per borrow reserve in the
  /// same order as the borrows.
  public static Instruction appendObligationRefreshAccounts(final Instruction instruction,
                                                            final List<PublicKey> depositReserves,
                                                            final List<PublicKey> borrowReserves,
                                                            final List<PublicKey> referrerTokenStates) {
    final int n = depositReserves.size()
        + borrowReserves.size()
        + (referrerTokenStates == null ? 0 : referrerTokenStates.size());
    final var extras = new ArrayList<AccountMeta>(n);
    for (final var key : depositReserves) {
      extras.add(AccountMeta.createWrite(key));
    }
    for (final var key : borrowReserves) {
      extras.add(AccountMeta.createWrite(key));
    }
    if (referrerTokenStates != null) {
      for (final var key : referrerTokenStates) {
        extras.add(AccountMeta.createWrite(key));
      }
    }
    return instruction.extraAccounts(extras);
  }

  /// Append the deposit-reserve keys used by `borrowObligationLiquidity[V2]` and
  /// `requestElevationGroup`. Reserves are passed as writable since the handlers may
  /// refresh them.
  public static Instruction appendDepositReserves(final Instruction instruction,
                                                  final Collection<PublicKey> depositReserves) {
    return instruction.extraAccounts(depositReserves, AccountMeta.CREATE_WRITE);
  }

  /// Append the trailing optional permission account that gates `PermissionedOp::*`
  /// when the market enforces it.
  public static Instruction appendPermissionAccount(final Instruction instruction,
                                                    final PublicKey permission) {
    return instruction.extraAccount(permission, AccountMeta.CREATE_READ);
  }
}

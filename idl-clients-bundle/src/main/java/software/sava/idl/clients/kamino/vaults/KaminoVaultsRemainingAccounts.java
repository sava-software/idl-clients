package software.sava.idl.clients.kamino.vaults;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.kamino.vaults.gen.types.VaultAllocation;
import software.sava.idl.clients.kamino.vaults.gen.types.VaultState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SequencedCollection;

/// Helper for appending the "extra" (non-IDL) accounts that several KaminoVault
/// instructions read from `ctx.remaining_accounts`. The generated
/// `KaminoVaultProgram` builders only carry IDL accounts, so callers must append
/// these extras via `Instruction.extraAccounts(...)` after building the instruction.
///
/// Ordering and writability follow the upstream Rust. `deposit`, `withdraw`,
/// `redeemInKind` and `withdrawFromAvailable` all reach
/// `refresh_allocation_reserve_accounts`, which takes the **first**
/// `vault_state.get_reserves_count()` entries as the vault's reserves — in the vault's
/// own allocation order, which it then verifies — and refreshes them through a klend
/// CPI. That CPI builds `[reserve(writable), lending_market(readonly)]` pairs, reading
/// each market key out of its reserve, so both accounts must be present for the runtime
/// to resolve them.
///
/// They are supplied as two slot-ordered blocks — every reserve, then every lending
/// market — matching the layout the official kvault interface library builds. Note this
/// is not the interleaved pair order the CPI itself uses internally.
public final class KaminoVaultsRemainingAccounts {

  private KaminoVaultsRemainingAccounts() {
  }

  /// Append the vault's reserves and their lending markets, taking each market from the
  /// reserve that names it so the two blocks cannot disagree.
  ///
  /// The reserves must be in the vault's allocation order; the program checks them
  /// against `VaultState` and rejects a mismatch.
  public static Instruction appendVaultReserves(final Instruction instruction,
                                                final SequencedCollection<Reserve> reserves) {
    final var metas = new ArrayList<AccountMeta>(reserves.size() * 2);
    for (final var reserve : reserves) {
      metas.add(AccountMeta.createWrite(reserve._address()));
    }
    for (final var reserve : reserves) {
      metas.add(AccountMeta.createRead(reserve.lendingMarket()));
    }
    return instruction.extraAccounts(metas);
  }

  /// The same two blocks, for a caller that already holds the keys.
  ///
  /// `reserves` and `lendingMarkets` are positional: the market at index `i` must be the
  /// one reserve `i` stores. Prefer [#appendVaultReserves(Instruction, SequencedCollection)],
  /// which reads each market off its reserve and cannot be mispaired.
  public static Instruction appendVaultReserves(final Instruction instruction,
                                                final SequencedCollection<PublicKey> reserves,
                                                final SequencedCollection<PublicKey> lendingMarkets) {
    if (reserves.size() != lendingMarkets.size()) {
      throw new IllegalArgumentException("Each reserve needs its lending market: "
          + reserves.size() + " reserves, " + lendingMarkets.size() + " markets.");
    }
    final var metas = new ArrayList<AccountMeta>(reserves.size() * 2);
    for (final var reserve : reserves) {
      metas.add(AccountMeta.createWrite(reserve));
    }
    for (final var market : lendingMarkets) {
      metas.add(AccountMeta.createRead(market));
    }
    return instruction.extraAccounts(metas);
  }

  /// The vault's reserves, in the order the program expects them: `vaultAllocationStrategy`
  /// slot order with the empty slots dropped.
  ///
  /// The strategy array is fixed-length and mostly unused — a vault with two reserves
  /// still has 25 slots — and `check_allocation_reserve_accounts_match` skips the empty
  /// ones while walking the accounts you passed. Sending all 25, or compacting them in a
  /// different order, fails as `ReserveAccountAndKeyMismatch`.
  ///
  /// Empty means the all-zero key specifically, which is what the program tests for —
  /// not `KaminoAccounts.isNullKey`, whose `nu111…` sentinel would drop a slot the
  /// program still counts.
  public static List<PublicKey> allocatedReserves(final VaultState vaultState) {
    return Arrays.stream(vaultState.vaultAllocationStrategy())
        .map(VaultAllocation::reserve)
        .filter(reserve -> !PublicKey.NONE.equals(reserve))
        .toList();
  }
}

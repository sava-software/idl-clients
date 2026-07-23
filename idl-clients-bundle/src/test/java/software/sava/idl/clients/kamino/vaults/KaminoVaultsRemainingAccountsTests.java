package software.sava.idl.clients.kamino.vaults;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// The vault's reserves ride `remaining_accounts` in allocation order and the
/// inner `RefreshReservesBatch` CPI writes back to each, so order and the
/// writable flag are the contract.
final class KaminoVaultsRemainingAccountsTests {

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  @Test
  void vaultReservesAppendWritableInAllocationOrder() {
    final var reserveA = key(0x21);
    final var reserveB = key(0x22);
    final var base = Instruction.createInstruction(
        AccountMeta.createInvoked(key(0x10)),
        List.of(AccountMeta.createWrite(key(0x11))),
        new byte[]{1});

    final var appended = KaminoVaultsRemainingAccounts.appendVaultReserves(
        base, List.of(reserveA, reserveB));
    final var accounts = appended.accounts();
    assertEquals(3, accounts.size());
    assertEquals(reserveA, accounts.get(1).publicKey());
    assertEquals(reserveB, accounts.get(2).publicKey());
    assertTrue(accounts.get(1).write());
    assertTrue(accounts.get(2).write());
    assertFalse(accounts.get(1).signer());
  }
}

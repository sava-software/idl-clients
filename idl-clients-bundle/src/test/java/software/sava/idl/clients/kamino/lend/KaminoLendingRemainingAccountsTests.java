package software.sava.idl.clients.kamino.lend;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// `refreshObligation`'s tail order is strict — deposits, then borrows, then
/// one referrer token state per borrow — and klend fails the whole refresh on
/// a misordered list with nothing pointing at the caller. The tests pin order,
/// flags, and the null-referrer shape.
final class KaminoLendingRemainingAccountsTests {

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static Instruction ix() {
    return Instruction.createInstruction(
        AccountMeta.createInvoked(key(0x10)),
        List.of(AccountMeta.createWrite(key(0x11))),
        new byte[]{1});
  }

  private static List<PublicKey> keys(final List<AccountMeta> metas, final int from) {
    return metas.subList(from, metas.size()).stream().map(AccountMeta::publicKey).toList();
  }

  @Test
  void obligationRefreshOrdersDepositsBorrowsThenReferrers() {
    final var deposit = key(0x21);
    final var borrowA = key(0x22);
    final var borrowB = key(0x23);
    final var referrerA = key(0x24);
    final var referrerB = key(0x25);

    final var appended = KaminoLendingRemainingAccounts.appendObligationRefreshAccounts(
        ix(), List.of(deposit), List.of(borrowA, borrowB), List.of(referrerA, referrerB));
    final var accounts = appended.accounts();
    assertEquals(List.of(deposit, borrowA, borrowB, referrerA, referrerB), keys(accounts, 1));
    assertTrue(accounts.subList(1, accounts.size()).stream().allMatch(AccountMeta::write));

    // no referrer: deposits then borrows only
    final var noReferrer = KaminoLendingRemainingAccounts.appendObligationRefreshAccounts(
        ix(), List.of(deposit), List.of(borrowA), null);
    assertEquals(List.of(deposit, borrowA), keys(noReferrer.accounts(), 1));
  }

  @Test
  void depositReservesAppendWritableAndPermissionAppendsReadOnly() {
    final var reserveA = key(0x31);
    final var reserveB = key(0x32);
    final var withReserves = KaminoLendingRemainingAccounts.appendDepositReserves(
        ix(), List.of(reserveA, reserveB));
    final var accounts = withReserves.accounts();
    assertEquals(List.of(reserveA, reserveB), keys(accounts, 1));
    assertTrue(accounts.get(1).write());
    assertTrue(accounts.get(2).write());

    final var permission = key(0x33);
    final var withPermission = KaminoLendingRemainingAccounts.appendPermissionAccount(ix(), permission);
    final var tail = withPermission.accounts().getLast();
    assertEquals(permission, tail.publicKey());
    assertFalse(tail.write());
    assertFalse(tail.signer());
  }
}

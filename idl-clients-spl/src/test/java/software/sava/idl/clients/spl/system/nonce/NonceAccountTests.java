package software.sava.idl.clients.spl.system.nonce;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.encoding.ByteUtil;
import software.sava.core.rpc.Filter;
import software.sava.core.tx.Transaction;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class NonceAccountTests {

  private static PublicKey key(final int fill) {
    final byte[] k = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(k, (byte) fill);
    return PublicKey.createPubKey(k);
  }

  /// Builds an 80-byte durable-nonce account: u32 version, u32 state, 32-byte authority,
  /// 32-byte nonce (recent blockhash), u64 lamports-per-signature.
  private static byte[] build(final int version, final NonceAccount.State state,
                              final PublicKey authority, final byte[] nonce, final long lamports) {
    final byte[] data = new byte[NonceAccount.BYTES];
    ByteUtil.putInt32LE(data, NonceAccount.VERSION_OFFSET, version);
    ByteUtil.putInt32LE(data, NonceAccount.STATE_OFFSET, state.ordinal());
    authority.write(data, NonceAccount.AUTHORITY_OFFSET);
    System.arraycopy(nonce, 0, data, NonceAccount.NONCE_OFFSET, nonce.length);
    ByteUtil.putInt64LE(data, NonceAccount.LAMPORTS_PER_SIG_OFFSET, lamports);
    return data;
  }

  @Test
  void parseInitializedNonce() {
    final var authority = key(7);
    final byte[] nonce = new byte[32];
    Arrays.fill(nonce, (byte) 0x5A);
    final byte[] data = build(1, NonceAccount.State.Initialized, authority, nonce, 5_000L);

    final var account = NonceAccount.read(PublicKey.NONE, data);
    assertEquals(1, account.version());
    assertEquals(NonceAccount.State.Initialized, account.state());
    assertEquals(authority, account.authority());
    assertArrayEquals(nonce, account.nonce());
    assertEquals(5_000L, account.lamportsPerSignature());
  }

  @Test
  void parseUninitializedNonce() {
    final byte[] data = build(0, NonceAccount.State.Uninitialized, PublicKey.NONE, new byte[32], 0L);
    final var account = NonceAccount.read(PublicKey.NONE, data);
    assertEquals(NonceAccount.State.Uninitialized, account.state());
    assertEquals(PublicKey.NONE, account.authority());
  }

  @Test
  void malformedInputRejected() {
    final byte[] data = build(1, NonceAccount.State.Initialized, key(1), new byte[32], 0L);
    // State enum has only two values; anything else indexes past it.
    ByteUtil.putInt32LE(data, NonceAccount.STATE_OFFSET, 2);
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> NonceAccount.read(PublicKey.NONE, data));
    ByteUtil.putInt32LE(data, NonceAccount.STATE_OFFSET, -1);
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> NonceAccount.read(PublicKey.NONE, data));
    assertThrows(IndexOutOfBoundsException.class,
        () -> NonceAccount.read(PublicKey.NONE, new byte[NonceAccount.BYTES - 1]));
  }

  @Test
  void lamportsPerSignatureFilterTargetsTheRightOffset() {
    // Regression: this filter was mis-named createVersionFilter and pointed at offset 0.
    // A getProgramAccounts memcmp must target the lamports field at offset 72.
    final var filter = NonceAccount.createLamportsPerSignatureFilter(5_000L);
    final byte[] expected = new byte[Long.BYTES];
    ByteUtil.putInt64LE(expected, 0, 5_000L);
    final var reference = Filter.createMemCompFilter(NonceAccount.LAMPORTS_PER_SIG_OFFSET, expected);
    assertEquals(reference, filter);
    assertEquals(72, NonceAccount.LAMPORTS_PER_SIG_OFFSET);
  }

  /// Each filter must encode its value and target its own field. The offsets are all distinct,
  /// so a filter pointed at the wrong one silently matches the wrong accounts.
  @Test
  void filtersTargetTheirOwnFields() {
    final byte[] versionBytes = new byte[Integer.BYTES];
    ByteUtil.putInt32LE(versionBytes, 0, 3);
    assertEquals(
        Filter.createMemCompFilter(NonceAccount.VERSION_OFFSET, versionBytes),
        NonceAccount.createVersionFilter(3));
    // the value is encoded, not left zeroed
    assertArrayEquals(new byte[]{3, 0, 0, 0}, versionBytes);
    assertNotEquals(NonceAccount.createVersionFilter(3), NonceAccount.createVersionFilter(4));

    final byte[] stateBytes = new byte[Integer.BYTES];
    ByteUtil.putInt32LE(stateBytes, 0, NonceAccount.State.Initialized.ordinal());
    assertEquals(
        Filter.createMemCompFilter(NonceAccount.STATE_OFFSET, stateBytes),
        NonceAccount.createStateFilter(NonceAccount.State.Initialized));
    assertNotEquals(
        NonceAccount.createStateFilter(NonceAccount.State.Initialized),
        NonceAccount.createStateFilter(NonceAccount.State.Uninitialized));

    final var authority = key(9);
    assertEquals(
        Filter.createMemCompFilter(NonceAccount.AUTHORITY_OFFSET, authority),
        NonceAccount.createAuthorityFilter(authority));

    final byte[] nonce = new byte[32];
    Arrays.fill(nonce, (byte) 0x3C);
    assertEquals(
        Filter.createMemCompFilter(NonceAccount.NONCE_OFFSET, nonce),
        NonceAccount.createNonceFilter(nonce));

    assertEquals(NonceAccount.BYTES, 80);
    assertEquals(Filter.createDataSizeFilter(NonceAccount.BYTES), NonceAccount.DATA_SIZE_FILTER);

    // the five field filters address five distinct offsets
    assertEquals(5, java.util.Set.of(
        NonceAccount.VERSION_OFFSET,
        NonceAccount.STATE_OFFSET,
        NonceAccount.AUTHORITY_OFFSET,
        NonceAccount.NONCE_OFFSET,
        NonceAccount.LAMPORTS_PER_SIG_OFFSET).size());
  }

  @Test
  void readOverloads() {
    final var authority = key(7);
    final byte[] nonce = new byte[32];
    Arrays.fill(nonce, (byte) 0x5A);
    final byte[] record = build(1, NonceAccount.State.Initialized, authority, nonce, 5_000L);

    // the address-less overload leaves the address null but parses everything else
    final var anonymous = NonceAccount.read(record, 0);
    assertNull(anonymous.address());
    assertEquals(authority, anonymous.authority());
    assertEquals(5_000L, anonymous.lamportsPerSignature());

    // the offset overload reads a record embedded in a larger buffer
    final int offset = 11;
    final byte[] framed = new byte[offset + record.length];
    System.arraycopy(record, 0, framed, offset, record.length);
    final var atOffset = NonceAccount.read(framed, offset);
    assertEquals(authority, atOffset.authority());
    assertArrayEquals(nonce, atOffset.nonce());
    // the record holds a byte[], so compare field-wise rather than by record equality
    assertEquals(anonymous.version(), atOffset.version());
    assertEquals(anonymous.state(), atOffset.state());
    assertEquals(anonymous.lamportsPerSignature(), atOffset.lamportsPerSignature());

    // the FACTORY is the address-carrying read
    final var viaFactory = NonceAccount.FACTORY.apply(PublicKey.NONE, record);
    assertEquals(PublicKey.NONE, viaFactory.address());
    assertEquals(authority, viaFactory.authority());

    // the AccountInfo overload takes the address from the account's own key
    final var accountKey = key(3);
    final var accountInfo = new software.sava.rpc.json.http.response.AccountInfo<>(
        accountKey, null, false, 0L, PublicKey.NONE, java.math.BigInteger.ZERO, 0, record);
    final var fromAccountInfo = NonceAccount.read(accountInfo);
    assertEquals(accountKey, fromAccountInfo.address());
    assertEquals(authority, fromAccountInfo.authority());
    assertArrayEquals(nonce, fromAccountInfo.nonce());
  }

  /// `advanceNonceAccount` must reference the account and its authority, and the authority is
  /// the party that has to sign.
  @Test
  void advanceNonceAccountReferencesTheAuthority() {
    final var address = key(4);
    final var authority = key(5);
    final byte[] nonce = new byte[32];
    Arrays.fill(nonce, (byte) 0x11);
    final var account = new NonceAccount(address, 1, NonceAccount.State.Initialized, authority, nonce, 5_000L);

    final var accounts = SolanaAccounts.MAIN_NET;
    final var ix = account.advanceNonceAccount(accounts);

    assertEquals(accounts.invokedSystemProgram(), ix.programId());
    final var keys = ix.accounts().stream().map(AccountMeta::publicKey).toList();
    assertEquals(java.util.List.of(address, accounts.recentBlockhashesSysVar(), authority), keys);
    // the nonce account is advanced, and only the authority signs
    assertTrue(ix.accounts().getFirst().write());
    assertEquals(
        java.util.List.of(authority),
        ix.accounts().stream().filter(AccountMeta::signer).map(AccountMeta::publicKey).toList());

    // the no-argument overload defaults to main net
    assertEquals(keys, account.advanceNonceAccount().accounts().stream().map(AccountMeta::publicKey).toList());
  }

  /// `setNonce` rewrites the transaction's blockhash to the stored nonce *and* prepends the
  /// advance instruction — a durable-nonce transaction is invalid without both.
  ///
  /// Regression: `prependIx` returns a new transaction rather than mutating in place, and the
  /// returned value was being discarded, so the advance instruction never made it into the
  /// transaction. Asserting on the *returned* transaction is the point of this test.
  @Test
  void setNoncePrependsAdvanceAndSetsTheBlockhash() {
    final var address = key(4);
    final var authority = key(5);
    final byte[] nonce = new byte[32];
    Arrays.fill(nonce, (byte) 0x11);
    final var account = new NonceAccount(address, 1, NonceAccount.State.Initialized, authority, nonce, 5_000L);

    final var accounts = SolanaAccounts.MAIN_NET;
    final var memo = software.sava.idl.clients.spl.memo.MemoProgram.createMemo(
        accounts.invokedMemoProgramV2(),
        java.util.List.of(authority),
        "hello".getBytes(java.nio.charset.StandardCharsets.UTF_8));

    final var withNonce = account.setNonce(accounts, Transaction.createTx(authority, memo));

    assertArrayEquals(nonce, withNonce.recentBlockHash());
    // the advance instruction leads the transaction, ahead of the original memo
    assertEquals(2, withNonce.instructions().size());
    final var first = withNonce.instructions().getFirst();
    assertEquals(accounts.invokedSystemProgram(), first.programId());
    assertEquals(address, first.accounts().getFirst().publicKey());
    assertEquals(accounts.invokedMemoProgramV2(), withNonce.instructions().getLast().programId());

    // the default-accounts overload behaves the same way
    final var viaDefaults = account.setNonce(Transaction.createTx(authority, memo));
    assertArrayEquals(nonce, viaDefaults.recentBlockHash());
    assertEquals(2, viaDefaults.instructions().size());
    assertEquals(accounts.invokedSystemProgram(), viaDefaults.instructions().getFirst().programId());
  }
}

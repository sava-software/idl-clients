package software.sava.idl.clients.spl.system.nonce;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;
import software.sava.core.rpc.Filter;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}

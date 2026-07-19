package software.sava.idl.clients.spl.stake;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class LockUpTests {

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  /// Regression: `write` put `unixTimestamp` into the epoch slot, so the epoch was dropped and
  /// the timestamp duplicated. `read` was always correct, so the bug only showed on a round
  /// trip — and `write` had no test at all. The two fields must be distinct here, and both
  /// distinct from zero, or the swap is invisible.
  @Test
  void writeThenReadRoundTrips() {
    final var lockUp = new LockUp(1_700_000_000L, 42L, key(0x7A));

    final byte[] data = new byte[LockUp.BYTES];
    assertEquals(LockUp.BYTES, lockUp.write(data, 0));
    assertEquals(LockUp.BYTES, lockUp.l());

    // the epoch occupies its own 8-byte slot rather than repeating the timestamp
    assertEquals(1_700_000_000L, ByteUtil.getInt64LE(data, 0));
    assertEquals(42L, ByteUtil.getInt64LE(data, Long.BYTES));

    assertEquals(lockUp, LockUp.read(data, 0));
  }

  @Test
  void writeAtOffset() {
    final var lockUp = new LockUp(-9L, 7L, key(0x5C));

    final int offset = 13;
    final byte[] data = new byte[offset + LockUp.BYTES];
    assertEquals(LockUp.BYTES, lockUp.write(data, offset));

    assertEquals(lockUp, LockUp.read(data, offset));
    // nothing was written before the offset
    assertArrayEquals(new byte[offset], Arrays.copyOf(data, offset));
  }

  /// A null custodian serializes as the all-zero key rather than throwing.
  @Test
  void nullCustodianWritesNone() {
    final var lockUp = new LockUp(1L, 2L, null);

    final byte[] data = new byte[LockUp.BYTES];
    assertEquals(LockUp.BYTES, lockUp.write(data, 0));

    final var read = LockUp.read(data, 0);
    assertEquals(PublicKey.NONE, read.custodian());
    assertEquals(1L, read.unixTimestamp());
    assertEquals(2L, read.epoch());
  }

  @Test
  void noLockupIsAllZeroes() {
    final byte[] data = new byte[LockUp.BYTES];
    assertEquals(LockUp.BYTES, LockUp.NO_LOCKUP.write(data, 0));

    assertArrayEquals(new byte[LockUp.BYTES], data);
    assertEquals(LockUp.NO_LOCKUP, LockUp.read(data, 0));
    assertEquals(PublicKey.NONE, LockUp.NO_LOCKUP.custodian());
  }
}

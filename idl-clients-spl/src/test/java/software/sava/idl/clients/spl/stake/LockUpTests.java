package software.sava.idl.clients.spl.stake;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.sysvar.Clock;
import software.sava.core.encoding.ByteUtil;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

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

  private static Clock clock(final long epoch, final long unixTimestamp) {
    return new Clock(null, 7L, 11L, epoch, epoch + 1, unixTimestamp);
  }

  /// Stake's `Lockup::is_in_force`: `unix_timestamp > clock.unix_timestamp || epoch > clock.epoch`.
  /// Each half holds the lockup on its own, and neither holds once the clock has reached it.
  @Test
  void isInForceFollowsTheProgramsComparison() {
    final var lockUp = new LockUp(1_700L, 9L, key(0x7A));
    assertTrue(lockUp.isInForce(clock(9L, 1_699L)), "the timestamp alone holds it");
    assertTrue(lockUp.isInForce(clock(8L, 1_700L)), "the epoch alone holds it");
    assertFalse(lockUp.isInForce(clock(9L, 1_700L)), "both reached: expired");
    assertFalse(lockUp.isInForce(clock(10L, 2_000L)));
  }

  /// The timestamp is the program's `i64` and the epoch its `u64`, so they compare differently.
  @Test
  void isInForceComparesTheTimestampSignedAndTheEpochUnsigned() {
    assertFalse(new LockUp(-1L, 0L, key(0x7A)).isInForce(clock(0L, 1_700L)), "a negative timestamp is in the past");
    assertTrue(new LockUp(0L, -1L, key(0x7A)).isInForce(clock(1_026L, 1_700L)), "u64::MAX is in the future");
    assertFalse(new LockUp(0L, 9L, key(0x7A)).isInForce(clock(-1L, 0L)), "no epoch is past u64::MAX");
  }
}

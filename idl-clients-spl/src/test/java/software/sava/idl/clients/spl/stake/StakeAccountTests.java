package software.sava.idl.clients.spl.stake;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;
import software.sava.core.rpc.Filter;

import java.util.Arrays;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class StakeAccountTests {

  // Real mainnet stake account 116vkzEjoTpFEd3x12XL9HbYJ5EpoC4cZ9a1N5A5mUt (200 bytes).
  private static final String REAL_STAKE = "AgAAAIDVIgAAAAAA38SSBHT+SIx7E/Shx/X+7KEWkJ6omw2LpsSD5FCdYiLfxJIEdP5IjHsT9KHH9f7soRaQnqibDYumxIPkUJ1iIgAAAAAAAAAAAAAAAAAAAADfxJIEdP5IjHsT9KHH9f7soRaQnqibDYumxIPkUJ1iIpuVY8wEUsEJOqo7S2X5FIq829PyGliKziw5wjngwbLFg+GYDAAAAACvAwAAAAAAAP//////////AAAAAAAA0D8RFnw1AAAAAAAAAAA=";

  @Test
  void parseRealStakeAccount() {
    final byte[] data = Base64.getDecoder().decode(REAL_STAKE);
    assertEquals(StakeAccount.BYTES, data.length);
    final var addr = PublicKey.fromBase58Encoded("116vkzEjoTpFEd3x12XL9HbYJ5EpoC4cZ9a1N5A5mUt");
    final var account = StakeAccount.read(addr, data);

    assertEquals(addr, account.address());
    assertEquals(StakeState.Stake, account.state());
    assertEquals(2_282_880L, account.rentExemptReserve());
    final var auth = PublicKey.fromBase58Encoded("G4VmLamEV6h15gndx7nHn8TTcu7fJkBBNVN3wTugMNtH");
    assertEquals(auth, account.stakeAuthority());
    assertEquals(auth, account.withdrawAuthority());
    assertEquals(PublicKey.fromBase58Encoded("BULKEEKf9Hjy4nwCthjzheEk4joH23LLXttAHjqEZmB2"), account.voterPublicKey());
    assertEquals(211_345_795L, account.stake());
    assertEquals(943L, account.activationEpoch());
    assertEquals(-1L, account.deActivationEpoch()); // u64::MAX == "never deactivating"

    // deActivationEpoch < 0 and activationEpoch (943) < currentEpoch -> ACTIVE.
    assertEquals(StakeAccount.State.ACTIVE, account.state(1_000L));
    // before the activation epoch completes -> ACTIVATING.
    assertEquals(StakeAccount.State.ACTIVATING, account.state(900L));
  }

  @Test
  void derivedStateMachine() {
    // deActivationEpoch >= 0 branch: deActivationEpoch < currentEpoch -> INACTIVE, else DE_ACTIVATING.
    final var deactivating = stakeAccount(100L, 200L);
    assertEquals(StakeAccount.State.DE_ACTIVATING, deactivating.state(150L));
    assertEquals(StakeAccount.State.INACTIVE, deactivating.state(250L));
    assertEquals(StakeAccount.State.DE_ACTIVATING, deactivating.state(200L)); // == boundary is still deactivating

    // deActivationEpoch < 0 branch with activationEpoch == 0 -> ACTIVATING (not > 0).
    final var justActivated = stakeAccount(0L, -1L);
    assertEquals(StakeAccount.State.ACTIVATING, justActivated.state(10L));
  }

  @Test
  void stakeFlagsBit() {
    final byte[] data = buildMinimal(StakeState.Stake, (byte) StakeAccount.MUST_FULLY_ACTIVATE_BEFORE_DEACTIVATION_IS_PERMITTED);
    final var account = StakeAccount.read(null, data);
    assertEquals(StakeState.Stake, account.state());
    org.junit.jupiter.api.Assertions.assertTrue(
        account.isSet(StakeAccount.MUST_FULLY_ACTIVATE_BEFORE_DEACTIVATION_IS_PERMITTED));
  }

  @Test
  void malformedInputRejected() {
    // A state discriminant outside [0, 3] indexes past the StakeState enum.
    final byte[] badState = buildMinimal(StakeState.Stake, (byte) 0);
    ByteUtil.putInt32LE(badState, StakeAccount.STATE_OFFSET, 4);
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> StakeAccount.read(null, badState));
    ByteUtil.putInt32LE(badState, StakeAccount.STATE_OFFSET, -1);
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> StakeAccount.read(null, badState));
    // A short buffer walks off the end (the voter pubkey read at offset 124).
    assertThrows(IndexOutOfBoundsException.class,
        () -> StakeAccount.read(null, new byte[100]));
  }

  /// `isSet` asks whether *every* bit of the mask is present, not whether any is. A
  /// single-bit mask cannot tell those two readings apart, so the multi-bit cases are the
  /// ones that matter.
  @Test
  void isSetRequiresEveryMaskBit() {
    final int bit0 = 0b0000_0001;
    final int bit1 = 0b0000_0010;
    final int both = bit0 | bit1;

    final var onlyBit0 = accountWithFlags((byte) bit0);
    assertTrue(onlyBit0.isSet(bit0));
    assertFalse(onlyBit0.isSet(bit1));
    // a partial match is not a match
    assertFalse(onlyBit0.isSet(both));

    final var bothBits = accountWithFlags((byte) both);
    assertTrue(bothBits.isSet(bit0));
    assertTrue(bothBits.isSet(bit1));
    assertTrue(bothBits.isSet(both));

    final var noFlags = accountWithFlags((byte) 0);
    assertFalse(noFlags.isSet(bit0));
    assertFalse(noFlags.isSet(both));
    // an empty mask is trivially present
    assertTrue(noFlags.isSet(0));
  }

  /// The offset-taking overload reads a record embedded partway into a larger buffer.
  @Test
  void readAtOffset() {
    final byte[] record = buildMinimal(StakeState.Stake, (byte) 3);
    ByteUtil.putInt64LE(record, StakeAccount.STAKE_OFFSET, 12345L);

    final int offset = 17;
    final byte[] framed = new byte[offset + record.length];
    System.arraycopy(record, 0, framed, offset, record.length);

    final var account = StakeAccount.read(framed, offset);
    assertNull(account.address());
    assertEquals(StakeState.Stake, account.state());
    assertEquals(12345L, account.stake());
    assertEquals((byte) 3, account.stakeFlags());
    // reading the same bytes from zero yields the same record, minus the framing
    assertEquals(StakeAccount.read(null, record, 0), account);
  }

  /// Each filter must target the offset of the field it names — a memcmp filter pointed at the
  /// wrong offset silently matches nothing (or the wrong accounts).
  @Test
  void accountFilters() {
    // The state discriminant is encoded little-endian at offset 0, not left as zeroes.
    final byte[] stakeBytes = new byte[Integer.BYTES];
    ByteUtil.putInt32LE(stakeBytes, 0, StakeState.Stake.ordinal());
    assertArrayEquals(new byte[]{2, 0, 0, 0}, stakeBytes);
    assertEquals(
        Filter.createMemCompFilter(StakeAccount.STATE_OFFSET, stakeBytes),
        StakeAccount.createStateFilter(StakeState.Stake));

    // A different state must produce a different filter.
    final byte[] initializedBytes = new byte[Integer.BYTES];
    ByteUtil.putInt32LE(initializedBytes, 0, StakeState.Initialized.ordinal());
    assertEquals(
        Filter.createMemCompFilter(StakeAccount.STATE_OFFSET, initializedBytes),
        StakeAccount.createStateFilter(StakeState.Initialized));
    assertNotEquals(
        StakeAccount.createStateFilter(StakeState.Stake),
        StakeAccount.createStateFilter(StakeState.Initialized));

    // Each key filter must target the offset of the field it names — a memcmp pointed at the
    // wrong offset silently matches nothing, or the wrong accounts.
    final var key = PublicKey.fromBase58Encoded("3ntfH5pyhTGePb2cv2gqhyBmZHVW3EggCnbq1ND7YmgX");
    assertEquals(
        Filter.createMemCompFilter(StakeAccount.STAKE_AUTHORITY_OFFSET, key),
        StakeAccount.createStakeAuthorityFilter(key));
    assertEquals(
        Filter.createMemCompFilter(StakeAccount.WITHDRAW_AUTHORITY_OFFSET, key),
        StakeAccount.createWithdrawAuthorityFilter(key));
    assertEquals(
        Filter.createMemCompFilter(StakeAccount.LOCKUP_CUSTODIAN_OFFSET, key),
        StakeAccount.createCustodianFilter(key));
    assertEquals(
        Filter.createMemCompFilter(StakeAccount.VOTER_PUBLIC_KEY_OFFSET, key),
        StakeAccount.createVoterFilter(key));

    // The four key filters address four distinct fields, so none is a copy of another.
    assertEquals(4, java.util.Set.of(
        StakeAccount.createStakeAuthorityFilter(key),
        StakeAccount.createWithdrawAuthorityFilter(key),
        StakeAccount.createCustodianFilter(key),
        StakeAccount.createVoterFilter(key)).size());

    assertEquals(Filter.createDataSizeFilter(StakeAccount.BYTES), StakeAccount.DATA_SIZE_FILTER);
  }

  private static StakeAccount accountWithFlags(final byte stakeFlags) {
    return StakeAccount.read(null, buildMinimal(StakeState.Stake, stakeFlags));
  }

  private static StakeAccount stakeAccount(final long activationEpoch, final long deActivationEpoch) {
    final byte[] data = buildMinimal(StakeState.Stake, (byte) 0);
    ByteUtil.putInt64LE(data, StakeAccount.ACTIVATION_EPOCH_OFFSET, activationEpoch);
    ByteUtil.putInt64LE(data, StakeAccount.DE_ACTIVATION_EPOCH_OFFSET, deActivationEpoch);
    return StakeAccount.read(null, data);
  }

  private static byte[] buildMinimal(final StakeState state, final byte stakeFlags) {
    final byte[] data = new byte[StakeAccount.BYTES];
    ByteUtil.putInt32LE(data, StakeAccount.STATE_OFFSET, state.ordinal());
    data[StakeAccount.STAKE_FLAGS_OFFSET] = stakeFlags;
    // leave pubkeys as all-zero (PublicKey.NONE); readPubKey handles them
    Arrays.fill(data, StakeAccount.STAKE_AUTHORITY_OFFSET, StakeAccount.STAKE_AUTHORITY_OFFSET + 1, (byte) 0);
    return data;
  }
}

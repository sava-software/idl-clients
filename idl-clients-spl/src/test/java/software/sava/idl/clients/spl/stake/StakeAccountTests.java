package software.sava.idl.clients.spl.stake;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;

import java.util.Arrays;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

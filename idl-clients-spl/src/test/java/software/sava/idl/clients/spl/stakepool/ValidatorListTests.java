package software.sava.idl.clients.spl.stakepool;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

final class ValidatorListTests {

  private static ValidatorStakeInfo stakeInfo(final int seed, final StakeStatus status) {
    final byte[] voteAccount = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    for (int i = 0; i < voteAccount.length; ++i) {
      voteAccount[i] = (byte) (seed + i);
    }
    return new ValidatorStakeInfo(
        1_000_000_000L + seed,
        2_000_000_000L + seed,
        700 + seed,
        Long.MIN_VALUE + seed,
        0,
        seed,
        status,
        PublicKey.createPubKey(voteAccount)
    );
  }

  @Test
  void validatorStakeInfoRoundTrip() {
    final var info = stakeInfo(3, StakeStatus.DeactivatingTransient);
    assertEquals(73, ValidatorStakeInfo.BYTES);
    assertEquals(ValidatorStakeInfo.BYTES, info.l());

    final byte[] data = new byte[7 + ValidatorStakeInfo.BYTES];
    assertEquals(ValidatorStakeInfo.BYTES, info.write(data, 7));

    assertEquals(1_000_000_003L, ByteUtil.getInt64LE(data, 7));
    assertEquals(2_000_000_003L, ByteUtil.getInt64LE(data, 15));
    assertEquals(703L, ByteUtil.getInt64LE(data, 23));
    assertEquals(Long.MIN_VALUE + 3, ByteUtil.getInt64LE(data, 31));
    assertEquals(0, ByteUtil.getInt32LE(data, 39));
    assertEquals(3, ByteUtil.getInt32LE(data, 43));
    assertEquals(StakeStatus.DeactivatingTransient.ordinal(), data[47]);

    assertEquals(info, ValidatorStakeInfo.read(data, 7));
  }

  /// The `unused` padding word is zero in every fixture above, and a zero write into a
  /// zeroed buffer is indistinguishable from no write at all. A non-zero value pins that the
  /// field is actually serialized — otherwise it would silently drop out of the record and
  /// shift nothing, corrupting a round trip only for pools that populate it.
  @Test
  void unusedWordIsSerialized() {
    final var base = stakeInfo(3, StakeStatus.Active);
    final var info = new ValidatorStakeInfo(
        base.activeStakeLamports(),
        base.transientStakeLamports(),
        base.lastUpdateEpoch(),
        base.transientSeedSuffix(),
        0x0BADF00D,
        base.validatorSeedSuffix(),
        base.podStakeStatus(),
        base.voteAccountAddress()
    );

    final byte[] data = new byte[7 + ValidatorStakeInfo.BYTES];
    assertEquals(ValidatorStakeInfo.BYTES, info.write(data, 7));

    // 4 u64 fields precede it, so it lands 32 bytes into the record
    assertEquals(0x0BADF00D, ByteUtil.getInt32LE(data, 7 + 32));
    assertEquals(0x0BADF00D, ValidatorStakeInfo.read(data, 7).unused());
    assertEquals(info, ValidatorStakeInfo.read(data, 7));
  }

  /// The address-less overload parses the same bytes and leaves the address null.
  @Test
  void readOverloads() {
    final var validators = new ValidatorStakeInfo[]{stakeInfo(1, StakeStatus.Active)};
    final var list = new ValidatorList(null, AccountType.ValidatorList, 5_000, validators);

    final byte[] data = new byte[list.l()];
    list.write(data, 0);

    final var anonymous = ValidatorList.read(data, 0);
    assertNull(anonymous.address());
    assertEquals(AccountType.ValidatorList, anonymous.accountType());
    assertEquals(5_000, anonymous.maxValidators());
    assertArrayEquals(validators, anonymous.validators());

    // the address-carrying overloads agree on everything else
    final var key = PublicKey.createPubKey(new byte[PublicKey.PUBLIC_KEY_LENGTH]);
    final var addressed = ValidatorList.read(key, data);
    assertEquals(key, addressed.address());
    assertArrayEquals(validators, addressed.validators());
    assertEquals(key, ValidatorList.FACTORY.apply(key, data).address());
  }

  @Test
  void validatorListRoundTrip() {
    final var validators = new ValidatorStakeInfo[]{
        stakeInfo(1, StakeStatus.Active),
        stakeInfo(2, StakeStatus.ReadyForRemoval),
        stakeInfo(3, StakeStatus.DeactivatingAll)
    };
    final var list = new ValidatorList(null, AccountType.ValidatorList, 5_000, validators);

    final int expectedLength = 1 + Integer.BYTES + Integer.BYTES + (3 * ValidatorStakeInfo.BYTES);
    assertEquals(expectedLength, list.l());

    final byte[] data = new byte[expectedLength];
    assertEquals(expectedLength, list.write(data, 0));

    assertEquals(AccountType.ValidatorList.ordinal(), data[0]);
    assertEquals(5_000, ByteUtil.getInt32LE(data, 1));
    assertEquals(3, ByteUtil.getInt32LE(data, 5));

    final var read = ValidatorList.read(data, 0);
    assertEquals(AccountType.ValidatorList, read.accountType());
    assertEquals(5_000, read.maxValidators());
    assertArrayEquals(validators, read.validators());
  }

  @Test
  void validatorListWriteAtOffset() {
    final var list = new ValidatorList(
        null,
        AccountType.ValidatorList,
        100,
        new ValidatorStakeInfo[]{stakeInfo(9, StakeStatus.DeactivatingValidator)}
    );
    final byte[] atZero = new byte[list.l()];
    assertEquals(list.l(), list.write(atZero, 0));

    final byte[] shifted = new byte[list.l() + 11];
    assertEquals(list.l(), list.write(shifted, 11));

    final byte[] shiftedSlice = new byte[list.l()];
    System.arraycopy(shifted, 11, shiftedSlice, 0, list.l());
    assertArrayEquals(atZero, shiftedSlice);

    final var read = ValidatorList.read(shifted, 11);
    assertArrayEquals(list.validators(), read.validators());
  }

  @Test
  void emptyValidatorListRoundTrip() {
    final var list = new ValidatorList(null, AccountType.ValidatorList, 0, new ValidatorStakeInfo[0]);
    assertEquals(9, list.l());

    final byte[] data = new byte[9];
    assertEquals(9, list.write(data, 0));
    assertEquals(0, ByteUtil.getInt32LE(data, 5));

    final var read = ValidatorList.read(data, 0);
    assertEquals(0, read.validators().length);
  }
}

package software.sava.idl.clients.marinade.stake_pool.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record InitializeData(PublicKey adminAuthority,
                             PublicKey validatorManagerAuthority,
                             long minStake,
                             Fee rewardsFee,
                             LiqPoolInitializeData liqPool,
                             int additionalStakeRecordSpace,
                             int additionalValidatorRecordSpace,
                             long slotsForStakeDelta,
                             PublicKey pauseAuthority) implements SerDe {

  public static final int BYTES = 144;

  public static final int ADMIN_AUTHORITY_OFFSET = 0;
  public static final int VALIDATOR_MANAGER_AUTHORITY_OFFSET = 32;
  public static final int MIN_STAKE_OFFSET = 64;
  public static final int REWARDS_FEE_OFFSET = 72;
  public static final int LIQ_POOL_OFFSET = 76;
  public static final int ADDITIONAL_STAKE_RECORD_SPACE_OFFSET = 96;
  public static final int ADDITIONAL_VALIDATOR_RECORD_SPACE_OFFSET = 100;
  public static final int SLOTS_FOR_STAKE_DELTA_OFFSET = 104;
  public static final int PAUSE_AUTHORITY_OFFSET = 112;

  public static InitializeData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var adminAuthority = readPubKey(_data, i);
    i += 32;
    final var validatorManagerAuthority = readPubKey(_data, i);
    i += 32;
    final var minStake = getInt64LE(_data, i);
    i += 8;
    final var rewardsFee = Fee.read(_data, i);
    i += rewardsFee.l();
    final var liqPool = LiqPoolInitializeData.read(_data, i);
    i += liqPool.l();
    final var additionalStakeRecordSpace = getInt32LE(_data, i);
    i += 4;
    final var additionalValidatorRecordSpace = getInt32LE(_data, i);
    i += 4;
    final var slotsForStakeDelta = getInt64LE(_data, i);
    i += 8;
    final var pauseAuthority = readPubKey(_data, i);
    return new InitializeData(adminAuthority,
                              validatorManagerAuthority,
                              minStake,
                              rewardsFee,
                              liqPool,
                              additionalStakeRecordSpace,
                              additionalValidatorRecordSpace,
                              slotsForStakeDelta,
                              pauseAuthority);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    adminAuthority.write(_data, i);
    i += 32;
    validatorManagerAuthority.write(_data, i);
    i += 32;
    putInt64LE(_data, i, minStake);
    i += 8;
    i += rewardsFee.write(_data, i);
    i += liqPool.write(_data, i);
    putInt32LE(_data, i, additionalStakeRecordSpace);
    i += 4;
    putInt32LE(_data, i, additionalValidatorRecordSpace);
    i += 4;
    putInt64LE(_data, i, slotsForStakeDelta);
    i += 8;
    pauseAuthority.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.jupiter.merkle_distributor.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record NewDistributorParams(long version,
                                   byte[] root,
                                   long totalClaim,
                                   long maxNumNodes,
                                   long startVestingTs,
                                   long endVestingTs,
                                   long clawbackStartTs,
                                   long activationPoint,
                                   int activationType,
                                   boolean closable,
                                   long totalBonus,
                                   long bonusVestingDuration,
                                   int claimType,
                                   PublicKey operator,
                                   PublicKey locker) implements SerDe {

  public static final int BYTES = 171;
  public static final int ROOT_LEN = 32;

  public static final int VERSION_OFFSET = 0;
  public static final int ROOT_OFFSET = 8;
  public static final int TOTAL_CLAIM_OFFSET = 40;
  public static final int MAX_NUM_NODES_OFFSET = 48;
  public static final int START_VESTING_TS_OFFSET = 56;
  public static final int END_VESTING_TS_OFFSET = 64;
  public static final int CLAWBACK_START_TS_OFFSET = 72;
  public static final int ACTIVATION_POINT_OFFSET = 80;
  public static final int ACTIVATION_TYPE_OFFSET = 88;
  public static final int CLOSABLE_OFFSET = 89;
  public static final int TOTAL_BONUS_OFFSET = 90;
  public static final int BONUS_VESTING_DURATION_OFFSET = 98;
  public static final int CLAIM_TYPE_OFFSET = 106;
  public static final int OPERATOR_OFFSET = 107;
  public static final int LOCKER_OFFSET = 139;

  public static NewDistributorParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var version = getInt64LE(_data, i);
    i += 8;
    final var root = new byte[32];
    i += SerDeUtil.readArray(root, _data, i);
    final var totalClaim = getInt64LE(_data, i);
    i += 8;
    final var maxNumNodes = getInt64LE(_data, i);
    i += 8;
    final var startVestingTs = getInt64LE(_data, i);
    i += 8;
    final var endVestingTs = getInt64LE(_data, i);
    i += 8;
    final var clawbackStartTs = getInt64LE(_data, i);
    i += 8;
    final var activationPoint = getInt64LE(_data, i);
    i += 8;
    final var activationType = _data[i] & 0xFF;
    ++i;
    final var closable = _data[i] == 1;
    ++i;
    final var totalBonus = getInt64LE(_data, i);
    i += 8;
    final var bonusVestingDuration = getInt64LE(_data, i);
    i += 8;
    final var claimType = _data[i] & 0xFF;
    ++i;
    final var operator = readPubKey(_data, i);
    i += 32;
    final var locker = readPubKey(_data, i);
    return new NewDistributorParams(version,
                                    root,
                                    totalClaim,
                                    maxNumNodes,
                                    startVestingTs,
                                    endVestingTs,
                                    clawbackStartTs,
                                    activationPoint,
                                    activationType,
                                    closable,
                                    totalBonus,
                                    bonusVestingDuration,
                                    claimType,
                                    operator,
                                    locker);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, version);
    i += 8;
    i += SerDeUtil.writeArrayChecked(root, 32, _data, i);
    putInt64LE(_data, i, totalClaim);
    i += 8;
    putInt64LE(_data, i, maxNumNodes);
    i += 8;
    putInt64LE(_data, i, startVestingTs);
    i += 8;
    putInt64LE(_data, i, endVestingTs);
    i += 8;
    putInt64LE(_data, i, clawbackStartTs);
    i += 8;
    putInt64LE(_data, i, activationPoint);
    i += 8;
    _data[i] = (byte) activationType;
    ++i;
    _data[i] = (byte) (closable ? 1 : 0);
    ++i;
    putInt64LE(_data, i, totalBonus);
    i += 8;
    putInt64LE(_data, i, bonusVestingDuration);
    i += 8;
    _data[i] = (byte) claimType;
    ++i;
    operator.write(_data, i);
    i += 32;
    locker.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

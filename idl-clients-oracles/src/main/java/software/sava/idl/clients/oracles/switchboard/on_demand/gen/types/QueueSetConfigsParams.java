package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record QueueSetConfigsParams(PublicKey authority,
                                    OptionalInt reward,
                                    OptionalLong nodeTimeout,
                                    OptionalInt oracleFeeProportionBps) implements SerDe {

  public static final int AUTHORITY_OFFSET = 1;

  public static QueueSetConfigsParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final PublicKey authority;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      authority = null;
      ++i;
    } else {
      ++i;
      authority = readPubKey(_data, i);
      i += 32;
    }
    final OptionalInt reward;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      reward = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      reward = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalLong nodeTimeout;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      nodeTimeout = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      nodeTimeout = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalInt oracleFeeProportionBps;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      oracleFeeProportionBps = OptionalInt.empty();
    } else {
      ++i;
      oracleFeeProportionBps = OptionalInt.of(getInt32LE(_data, i));
    }
    return new QueueSetConfigsParams(authority,
                                     reward,
                                     nodeTimeout,
                                     oracleFeeProportionBps);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeOptional(1, authority, _data, i);
    i += SerDeUtil.writeOptional(1, reward, _data, i);
    i += SerDeUtil.writeOptional(1, nodeTimeout, _data, i);
    i += SerDeUtil.writeOptional(1, oracleFeeProportionBps, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (authority == null ? 1 : (1 + 32)) + (reward == null || reward.isEmpty() ? 1 : (1 + 4)) + (nodeTimeout == null || nodeTimeout.isEmpty() ? 1 : (1 + 8)) + (oracleFeeProportionBps == null || oracleFeeProportionBps.isEmpty() ? 1 : (1 + 4));
  }
}

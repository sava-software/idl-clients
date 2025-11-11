package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record QueueSetConfigsParams(PublicKey authority,
                                    OptionalInt reward,
                                    OptionalLong nodeTimeout) implements Borsh {

  public static QueueSetConfigsParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final PublicKey authority;
    if (_data[i] == 0) {
      authority = null;
      ++i;
    } else {
      ++i;
      authority = readPubKey(_data, i);
      i += 32;
    }
    final OptionalInt reward;
    if (_data[i] == 0) {
      reward = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      reward = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalLong nodeTimeout;
    if (_data[i] == 0) {
      nodeTimeout = OptionalLong.empty();
    } else {
      ++i;
      nodeTimeout = OptionalLong.of(getInt64LE(_data, i));
    }
    return new QueueSetConfigsParams(authority, reward, nodeTimeout);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeOptional(authority, _data, i);
    i += Borsh.writeOptional(reward, _data, i);
    i += Borsh.writeOptional(nodeTimeout, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (authority == null ? 1 : (1 + 32)) + (reward == null || reward.isEmpty() ? 1 : (1 + 4)) + (nodeTimeout == null || nodeTimeout.isEmpty() ? 1 : (1 + 8));
  }
}

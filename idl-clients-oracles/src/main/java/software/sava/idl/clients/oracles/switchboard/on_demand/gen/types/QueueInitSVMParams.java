package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record QueueInitSVMParams(int allowAuthorityOverrideAfter,
                                 boolean requireAuthorityHeartbeatPermission,
                                 boolean requireUsagePermissions,
                                 int maxQuoteVerificationAge,
                                 int reward,
                                 int nodeTimeout,
                                 long recentSlot,
                                 PublicKey sourceQueueKey) implements SerDe {

  public static final int BYTES = 58;

  public static QueueInitSVMParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var allowAuthorityOverrideAfter = getInt32LE(_data, i);
    i += 4;
    final var requireAuthorityHeartbeatPermission = _data[i] == 1;
    ++i;
    final var requireUsagePermissions = _data[i] == 1;
    ++i;
    final var maxQuoteVerificationAge = getInt32LE(_data, i);
    i += 4;
    final var reward = getInt32LE(_data, i);
    i += 4;
    final var nodeTimeout = getInt32LE(_data, i);
    i += 4;
    final var recentSlot = getInt64LE(_data, i);
    i += 8;
    final var sourceQueueKey = readPubKey(_data, i);
    return new QueueInitSVMParams(allowAuthorityOverrideAfter,
                                  requireAuthorityHeartbeatPermission,
                                  requireUsagePermissions,
                                  maxQuoteVerificationAge,
                                  reward,
                                  nodeTimeout,
                                  recentSlot,
                                  sourceQueueKey);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, allowAuthorityOverrideAfter);
    i += 4;
    _data[i] = (byte) (requireAuthorityHeartbeatPermission ? 1 : 0);
    ++i;
    _data[i] = (byte) (requireUsagePermissions ? 1 : 0);
    ++i;
    putInt32LE(_data, i, maxQuoteVerificationAge);
    i += 4;
    putInt32LE(_data, i, reward);
    i += 4;
    putInt32LE(_data, i, nodeTimeout);
    i += 4;
    putInt64LE(_data, i, recentSlot);
    i += 8;
    sourceQueueKey.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

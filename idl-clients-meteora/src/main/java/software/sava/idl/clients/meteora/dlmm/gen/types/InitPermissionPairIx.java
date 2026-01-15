package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record InitPermissionPairIx(int activeId,
                                   int binStep,
                                   int baseFactor,
                                   int baseFeePowerFactor,
                                   int activationType,
                                   int protocolShare) implements SerDe {

  public static final int BYTES = 12;

  public static final int ACTIVE_ID_OFFSET = 0;
  public static final int BIN_STEP_OFFSET = 4;
  public static final int BASE_FACTOR_OFFSET = 6;
  public static final int BASE_FEE_POWER_FACTOR_OFFSET = 8;
  public static final int ACTIVATION_TYPE_OFFSET = 9;
  public static final int PROTOCOL_SHARE_OFFSET = 10;

  public static InitPermissionPairIx read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var activeId = getInt32LE(_data, i);
    i += 4;
    final var binStep = getInt16LE(_data, i);
    i += 2;
    final var baseFactor = getInt16LE(_data, i);
    i += 2;
    final var baseFeePowerFactor = _data[i] & 0xFF;
    ++i;
    final var activationType = _data[i] & 0xFF;
    ++i;
    final var protocolShare = getInt16LE(_data, i);
    return new InitPermissionPairIx(activeId,
                                    binStep,
                                    baseFactor,
                                    baseFeePowerFactor,
                                    activationType,
                                    protocolShare);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, activeId);
    i += 4;
    putInt16LE(_data, i, binStep);
    i += 2;
    putInt16LE(_data, i, baseFactor);
    i += 2;
    _data[i] = (byte) baseFeePowerFactor;
    ++i;
    _data[i] = (byte) activationType;
    ++i;
    putInt16LE(_data, i, protocolShare);
    i += 2;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

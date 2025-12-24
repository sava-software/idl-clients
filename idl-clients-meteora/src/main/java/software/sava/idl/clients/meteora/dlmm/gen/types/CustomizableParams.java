package software.sava.idl.clients.meteora.dlmm.gen.types;

import java.util.OptionalLong;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

/// @param activeId Pool price
/// @param binStep Bin step
/// @param baseFactor Base factor
/// @param activationType Activation type. 0 = Slot, 1 = Time. Check ActivationType enum
/// @param hasAlphaVault Whether the pool has an alpha vault
/// @param activationPoint Decide when does the pool start trade. None = Now
/// @param creatorPoolOnOffControl Pool creator have permission to enable/disable pool with restricted program validation. Only applicable for customizable permissionless pool.
/// @param baseFeePowerFactor Base fee power factor
/// @param padding Padding, for future use
public record CustomizableParams(int activeId,
                                 int binStep,
                                 int baseFactor,
                                 int activationType,
                                 boolean hasAlphaVault,
                                 OptionalLong activationPoint,
                                 boolean creatorPoolOnOffControl,
                                 int baseFeePowerFactor,
                                 byte[] padding) implements SerDe {

  public static final int PADDING_LEN = 62;
  public static CustomizableParams read(final byte[] _data, final int _offset) {
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
    final var activationType = _data[i] & 0xFF;
    ++i;
    final var hasAlphaVault = _data[i] == 1;
    ++i;
    final OptionalLong activationPoint;
    if (_data[i] == 0) {
      activationPoint = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      activationPoint = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final var creatorPoolOnOffControl = _data[i] == 1;
    ++i;
    final var baseFeePowerFactor = _data[i] & 0xFF;
    ++i;
    final var padding = new byte[62];
    SerDeUtil.readArray(padding, _data, i);
    return new CustomizableParams(activeId,
                                  binStep,
                                  baseFactor,
                                  activationType,
                                  hasAlphaVault,
                                  activationPoint,
                                  creatorPoolOnOffControl,
                                  baseFeePowerFactor,
                                  padding);
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
    _data[i] = (byte) activationType;
    ++i;
    _data[i] = (byte) (hasAlphaVault ? 1 : 0);
    ++i;
    i += SerDeUtil.writeOptional(1, activationPoint, _data, i);
    _data[i] = (byte) (creatorPoolOnOffControl ? 1 : 0);
    ++i;
    _data[i] = (byte) baseFeePowerFactor;
    ++i;
    i += SerDeUtil.writeArrayChecked(padding, 62, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 4
         + 2
         + 2
         + 1
         + 1
         + (activationPoint == null || activationPoint.isEmpty() ? 1 : (1 + 8))
         + 1
         + 1
         + SerDeUtil.lenArray(padding);
  }
}

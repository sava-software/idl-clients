package software.sava.idl.clients.drift.vaults.gen.types;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record UpdateVaultProtocolParams(OptionalLong protocolFee, OptionalInt protocolProfitShare) implements SerDe {

  public static UpdateVaultProtocolParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final OptionalLong protocolFee;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      protocolFee = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      protocolFee = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalInt protocolProfitShare;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      protocolProfitShare = OptionalInt.empty();
    } else {
      ++i;
      protocolProfitShare = OptionalInt.of(getInt32LE(_data, i));
    }
    return new UpdateVaultProtocolParams(protocolFee, protocolProfitShare);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeOptional(1, protocolFee, _data, i);
    i += SerDeUtil.writeOptional(1, protocolProfitShare, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (protocolFee == null || protocolFee.isEmpty() ? 1 : (1 + 8)) + (protocolProfitShare == null || protocolProfitShare.isEmpty() ? 1 : (1 + 4));
  }
}

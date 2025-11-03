package software.sava.idl.clients.drift.vaults.gen.types;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record UpdateVaultProtocolParams(OptionalLong protocolFee, OptionalInt protocolProfitShare) implements Borsh {

  public static UpdateVaultProtocolParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final OptionalLong protocolFee;
    if (_data[i] == 0) {
      protocolFee = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      protocolFee = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalInt protocolProfitShare;
    if (_data[i] == 0) {
      protocolProfitShare = OptionalInt.empty();
    } else {
      ++i;
    ;
      protocolProfitShare = OptionalInt.of(getInt32LE(_data, i));
    }
    return new UpdateVaultProtocolParams(protocolFee, protocolProfitShare);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeOptional(protocolFee, _data, i);
    i += Borsh.writeOptional(protocolProfitShare, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (protocolFee == null || protocolFee.isEmpty() ? 1 : (1 + 8)) + (protocolProfitShare == null || protocolProfitShare.isEmpty() ? 1 : (1 + 4));
  }
}

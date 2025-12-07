package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record AddLiquiditySingleSidePreciseParameter(CompressedBinDepositAmount[] bins, long decompressMultiplier) implements Borsh {

  public static AddLiquiditySingleSidePreciseParameter read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var bins = Borsh.readVector(CompressedBinDepositAmount.class, CompressedBinDepositAmount::read, _data, i);
    i += Borsh.lenVector(bins);
    final var decompressMultiplier = getInt64LE(_data, i);
    return new AddLiquiditySingleSidePreciseParameter(bins, decompressMultiplier);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeVector(bins, _data, i);
    putInt64LE(_data, i, decompressMultiplier);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return Borsh.lenVector(bins) + 8;
  }
}

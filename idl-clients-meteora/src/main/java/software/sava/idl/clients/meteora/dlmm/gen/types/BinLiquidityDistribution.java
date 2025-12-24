package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

/// @param binId Define the bin ID wish to deposit to.
/// @param distributionX DistributionX (or distributionY) is the percentages of amountX (or amountY) you want to add to each bin.
/// @param distributionY DistributionX (or distributionY) is the percentages of amountX (or amountY) you want to add to each bin.
public record BinLiquidityDistribution(int binId,
                                       int distributionX,
                                       int distributionY) implements SerDe {

  public static final int BYTES = 8;

  public static BinLiquidityDistribution read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var binId = getInt32LE(_data, i);
    i += 4;
    final var distributionX = getInt16LE(_data, i);
    i += 2;
    final var distributionY = getInt16LE(_data, i);
    return new BinLiquidityDistribution(binId, distributionX, distributionY);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, binId);
    i += 4;
    putInt16LE(_data, i, distributionX);
    i += 2;
    putInt16LE(_data, i, distributionY);
    i += 2;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

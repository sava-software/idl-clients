package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

/// @param binId Define the bin ID wish to deposit to.
/// @param weight weight of liquidity distributed for this bin id
public record BinLiquidityDistributionByWeight(int binId, int weight) implements Borsh {

  public static final int BYTES = 6;

  public static BinLiquidityDistributionByWeight read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var binId = getInt32LE(_data, i);
    i += 4;
    final var weight = getInt16LE(_data, i);
    return new BinLiquidityDistributionByWeight(binId, weight);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, binId);
    i += 4;
    putInt16LE(_data, i, weight);
    i += 2;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

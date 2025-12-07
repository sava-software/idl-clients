package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param amountX Amount of X token to deposit
/// @param amountY Amount of Y token to deposit
/// @param binLiquidityDist Liquidity distribution to each bins
public record LiquidityParameter(long amountX,
                                 long amountY,
                                 BinLiquidityDistribution[] binLiquidityDist) implements Borsh {

  public static LiquidityParameter read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var amountX = getInt64LE(_data, i);
    i += 8;
    final var amountY = getInt64LE(_data, i);
    i += 8;
    final var binLiquidityDist = Borsh.readVector(BinLiquidityDistribution.class, BinLiquidityDistribution::read, _data, i);
    return new LiquidityParameter(amountX, amountY, binLiquidityDist);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, amountX);
    i += 8;
    putInt64LE(_data, i, amountY);
    i += 8;
    i += Borsh.writeVector(binLiquidityDist, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 8 + Borsh.lenVector(binLiquidityDist);
  }
}

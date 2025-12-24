package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param amountX Amount of X token to deposit
/// @param amountY Amount of Y token to deposit
/// @param activeId Active bin that integrator observe off-chain
/// @param maxActiveBinSlippage max active bin slippage allowed
/// @param binLiquidityDist Liquidity distribution to each bins
public record LiquidityParameterByWeight(long amountX,
                                         long amountY,
                                         int activeId,
                                         int maxActiveBinSlippage,
                                         BinLiquidityDistributionByWeight[] binLiquidityDist) implements SerDe {

  public static LiquidityParameterByWeight read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var amountX = getInt64LE(_data, i);
    i += 8;
    final var amountY = getInt64LE(_data, i);
    i += 8;
    final var activeId = getInt32LE(_data, i);
    i += 4;
    final var maxActiveBinSlippage = getInt32LE(_data, i);
    i += 4;
    final var binLiquidityDist = SerDeUtil.readVector(4, BinLiquidityDistributionByWeight.class, BinLiquidityDistributionByWeight::read, _data, i);
    return new LiquidityParameterByWeight(amountX,
                                          amountY,
                                          activeId,
                                          maxActiveBinSlippage,
                                          binLiquidityDist);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, amountX);
    i += 8;
    putInt64LE(_data, i, amountY);
    i += 8;
    putInt32LE(_data, i, activeId);
    i += 4;
    putInt32LE(_data, i, maxActiveBinSlippage);
    i += 4;
    i += SerDeUtil.writeVector(4, binLiquidityDist, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8
         + 8
         + 4
         + 4
         + SerDeUtil.lenVector(4, binLiquidityDist);
  }
}

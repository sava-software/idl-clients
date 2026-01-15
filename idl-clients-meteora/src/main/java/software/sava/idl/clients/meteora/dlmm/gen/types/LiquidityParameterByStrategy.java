package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param amountX Amount of X token to deposit
/// @param amountY Amount of Y token to deposit
/// @param activeId Active bin that integrator observe off-chain
/// @param maxActiveBinSlippage max active bin slippage allowed
/// @param strategyParameters strategy parameters
public record LiquidityParameterByStrategy(long amountX,
                                           long amountY,
                                           int activeId,
                                           int maxActiveBinSlippage,
                                           StrategyParameters strategyParameters) implements SerDe {

  public static final int BYTES = 97;

  public static final int AMOUNT_X_OFFSET = 0;
  public static final int AMOUNT_Y_OFFSET = 8;
  public static final int ACTIVE_ID_OFFSET = 16;
  public static final int MAX_ACTIVE_BIN_SLIPPAGE_OFFSET = 20;
  public static final int STRATEGY_PARAMETERS_OFFSET = 24;

  public static LiquidityParameterByStrategy read(final byte[] _data, final int _offset) {
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
    final var strategyParameters = StrategyParameters.read(_data, i);
    return new LiquidityParameterByStrategy(amountX,
                                            amountY,
                                            activeId,
                                            maxActiveBinSlippage,
                                            strategyParameters);
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
    i += strategyParameters.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

public record LiquidityParams(int slippageToleranceBps) implements SerDe {

  public static final int BYTES = 2;

  public static final int SLIPPAGE_TOLERANCE_BPS_OFFSET = 0;

  public static LiquidityParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var slippageToleranceBps = getInt16LE(_data, _offset);
    return new LiquidityParams(slippageToleranceBps);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt16LE(_data, i, slippageToleranceBps);
    i += 2;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

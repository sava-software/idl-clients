package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

/// Borsh payload for updating cancel-order risk factor for a perp asset.
///
public record UpdatePerpCancelRiskFactorInstruction(Symbol perpAssetSymbol, int cancelOrderRiskFactor) implements SerDe {

  public static final int BYTES = 18;

  public static final int PERP_ASSET_SYMBOL_OFFSET = 0;
  public static final int CANCEL_ORDER_RISK_FACTOR_OFFSET = 16;

  public static UpdatePerpCancelRiskFactorInstruction read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var perpAssetSymbol = Symbol.read(_data, i);
    i += perpAssetSymbol.l();
    final var cancelOrderRiskFactor = getInt16LE(_data, i);
    return new UpdatePerpCancelRiskFactorInstruction(perpAssetSymbol, cancelOrderRiskFactor);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += perpAssetSymbol.write(_data, i);
    putInt16LE(_data, i, cancelOrderRiskFactor);
    i += 2;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

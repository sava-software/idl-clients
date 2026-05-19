package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

/// Borsh payload for updating uPnL risk factor for a perp asset.
///
public record UpdatePerpUPnlRiskFactorInstruction(Symbol perpAssetSymbol, UPnlRiskFactor upnlRiskFactor) implements SerDe {

  public static final int BYTES = 24;

  public static final int PERP_ASSET_SYMBOL_OFFSET = 0;
  public static final int UPNL_RISK_FACTOR_OFFSET = 16;

  public static UpdatePerpUPnlRiskFactorInstruction read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var perpAssetSymbol = Symbol.read(_data, i);
    i += perpAssetSymbol.l();
    final var upnlRiskFactor = UPnlRiskFactor.read(_data, i);
    return new UpdatePerpUPnlRiskFactorInstruction(perpAssetSymbol, upnlRiskFactor);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += perpAssetSymbol.write(_data, i);
    i += upnlRiskFactor.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

/// Borsh payload for updating leverage tiers for a perp asset.
///
public record UpdatePerpLeverageTiersInstruction(Symbol perpAssetSymbol, LeverageTiers leverageTiers) implements SerDe {

  public static final int BYTES = 112;

  public static final int PERP_ASSET_SYMBOL_OFFSET = 0;
  public static final int LEVERAGE_TIERS_OFFSET = 16;

  public static UpdatePerpLeverageTiersInstruction read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var perpAssetSymbol = Symbol.read(_data, i);
    i += perpAssetSymbol.l();
    final var leverageTiers = LeverageTiers.read(_data, i);
    return new UpdatePerpLeverageTiersInstruction(perpAssetSymbol, leverageTiers);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += perpAssetSymbol.write(_data, i);
    i += leverageTiers.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

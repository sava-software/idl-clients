package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

/// Borsh payload for updating isolated-only metadata for a perp asset.
///
public record UpdatePerpIsolatedOnlyInstruction(Symbol perpAssetSymbol, int isolatedOnly) implements SerDe {

  public static final int BYTES = 17;

  public static final int PERP_ASSET_SYMBOL_OFFSET = 0;
  public static final int ISOLATED_ONLY_OFFSET = 16;

  public static UpdatePerpIsolatedOnlyInstruction read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var perpAssetSymbol = Symbol.read(_data, i);
    i += perpAssetSymbol.l();
    final var isolatedOnly = _data[i] & 0xFF;
    return new UpdatePerpIsolatedOnlyInstruction(perpAssetSymbol, isolatedOnly);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += perpAssetSymbol.write(_data, i);
    _data[i] = (byte) isolatedOnly;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

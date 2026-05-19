package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

/// Borsh payload for removing all oracle keys for a perp asset.
///
public record RemoveAllOraclesInstruction(Symbol perpAssetSymbol) implements SerDe {

  public static final int BYTES = 16;

  public static final int PERP_ASSET_SYMBOL_OFFSET = 0;

  public static RemoveAllOraclesInstruction read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var perpAssetSymbol = Symbol.read(_data, _offset);
    return new RemoveAllOraclesInstruction(perpAssetSymbol);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += perpAssetSymbol.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

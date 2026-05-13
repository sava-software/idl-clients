package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;

/// Parameters for liquidating a trader's position via market order.
///
public record LiquidationParams(PublicKey assetMint,
                                BaseLots liquidationSize,
                                Ticks liquidationPrice,
                                boolean fillOrKill) implements SerDe {

  public static final int BYTES = 49;

  public static final int ASSET_MINT_OFFSET = 0;
  public static final int LIQUIDATION_SIZE_OFFSET = 32;
  public static final int LIQUIDATION_PRICE_OFFSET = 40;
  public static final int FILL_OR_KILL_OFFSET = 48;

  public static LiquidationParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var assetMint = readPubKey(_data, i);
    i += 32;
    final var liquidationSize = BaseLots.read(_data, i);
    i += liquidationSize.l();
    final var liquidationPrice = Ticks.read(_data, i);
    i += liquidationPrice.l();
    final var fillOrKill = _data[i] == 1;
    return new LiquidationParams(assetMint,
                                 liquidationSize,
                                 liquidationPrice,
                                 fillOrKill);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    assetMint.write(_data, i);
    i += 32;
    i += liquidationSize.write(_data, i);
    i += liquidationPrice.write(_data, i);
    _data[i] = (byte) (fillOrKill ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

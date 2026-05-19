package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.BaseLots;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.QuoteLots;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.Ticks;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::Liquidation Borsh variant 32.
/// Payload type: LiquidationEvent.
///
public record LiquidationEvent(Discriminator discriminator,
                               PublicKey liquidator,
                               PublicKey liquidatedTrader,
                               int assetId,
                               BaseLots liquidationSize,
                               Ticks markPrice,
                               BaseLots baseLotsFilled,
                               QuoteLots quoteLotsFilled,
                               boolean positionClosed) implements EternalEvent {

  public static final int BYTES = 109;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(32, 0, 0, 0, 0, 0, 0, 0);

  public static final int LIQUIDATOR_OFFSET = 8;
  public static final int LIQUIDATED_TRADER_OFFSET = 40;
  public static final int ASSET_ID_OFFSET = 72;
  public static final int LIQUIDATION_SIZE_OFFSET = 76;
  public static final int MARK_PRICE_OFFSET = 84;
  public static final int BASE_LOTS_FILLED_OFFSET = 92;
  public static final int QUOTE_LOTS_FILLED_OFFSET = 100;
  public static final int POSITION_CLOSED_OFFSET = 108;

  public static LiquidationEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var liquidator = readPubKey(_data, i);
    i += 32;
    final var liquidatedTrader = readPubKey(_data, i);
    i += 32;
    final var assetId = getInt32LE(_data, i);
    i += 4;
    final var liquidationSize = BaseLots.read(_data, i);
    i += liquidationSize.l();
    final var markPrice = Ticks.read(_data, i);
    i += markPrice.l();
    final var baseLotsFilled = BaseLots.read(_data, i);
    i += baseLotsFilled.l();
    final var quoteLotsFilled = QuoteLots.read(_data, i);
    i += quoteLotsFilled.l();
    final var positionClosed = _data[i] == 1;
    return new LiquidationEvent(discriminator,
                                liquidator,
                                liquidatedTrader,
                                assetId,
                                liquidationSize,
                                markPrice,
                                baseLotsFilled,
                                quoteLotsFilled,
                                positionClosed);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    liquidator.write(_data, i);
    i += 32;
    liquidatedTrader.write(_data, i);
    i += 32;
    putInt32LE(_data, i, assetId);
    i += 4;
    i += liquidationSize.write(_data, i);
    i += markPrice.write(_data, i);
    i += baseLotsFilled.write(_data, i);
    i += quoteLotsFilled.write(_data, i);
    _data[i] = (byte) (positionClosed ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

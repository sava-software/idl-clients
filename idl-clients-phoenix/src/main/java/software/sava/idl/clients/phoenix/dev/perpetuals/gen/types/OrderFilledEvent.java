package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.BaseLots;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.QuoteLots;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.Side;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.SignedBaseLots;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.SignedFeeRateMicro;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.SignedQuoteLots;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.SignedQuoteLotsPerBaseLot;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.Ticks;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::OrderFilled Borsh variant 3.
/// Payload type: OrderFilledEvent.
///
public record OrderFilledEvent(Discriminator discriminator,
                               long orderSequenceNumber,
                               Side side,
                               Ticks price,
                               BaseLots baseLotsFilled,
                               QuoteLots quoteLotsFilled,
                               BaseLots quantityRemaining,
                               PublicKey maker,
                               SignedFeeRateMicro makerFeeRate,
                               SignedBaseLots makerBaseLotPosition,
                               SignedQuoteLots makerVirtualQuoteLotPosition,
                               SignedQuoteLots makerQuoteLotCollateral,
                               SignedQuoteLotsPerBaseLot makerCumulativeFundingSnapshot) implements EternalEvent {

  public static final int BYTES = 117;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(3, 0, 0, 0, 0, 0, 0, 0);

  public static final int ORDER_SEQUENCE_NUMBER_OFFSET = 8;
  public static final int SIDE_OFFSET = 16;
  public static final int PRICE_OFFSET = 17;
  public static final int BASE_LOTS_FILLED_OFFSET = 25;
  public static final int QUOTE_LOTS_FILLED_OFFSET = 33;
  public static final int QUANTITY_REMAINING_OFFSET = 41;
  public static final int MAKER_OFFSET = 49;
  public static final int MAKER_FEE_RATE_OFFSET = 81;
  public static final int MAKER_BASE_LOT_POSITION_OFFSET = 85;
  public static final int MAKER_VIRTUAL_QUOTE_LOT_POSITION_OFFSET = 93;
  public static final int MAKER_QUOTE_LOT_COLLATERAL_OFFSET = 101;
  public static final int MAKER_CUMULATIVE_FUNDING_SNAPSHOT_OFFSET = 109;

  public static OrderFilledEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var orderSequenceNumber = getInt64LE(_data, i);
    i += 8;
    final var side = Side.read(_data, i);
    i += side.l();
    final var price = Ticks.read(_data, i);
    i += price.l();
    final var baseLotsFilled = BaseLots.read(_data, i);
    i += baseLotsFilled.l();
    final var quoteLotsFilled = QuoteLots.read(_data, i);
    i += quoteLotsFilled.l();
    final var quantityRemaining = BaseLots.read(_data, i);
    i += quantityRemaining.l();
    final var maker = readPubKey(_data, i);
    i += 32;
    final var makerFeeRate = SignedFeeRateMicro.read(_data, i);
    i += makerFeeRate.l();
    final var makerBaseLotPosition = SignedBaseLots.read(_data, i);
    i += makerBaseLotPosition.l();
    final var makerVirtualQuoteLotPosition = SignedQuoteLots.read(_data, i);
    i += makerVirtualQuoteLotPosition.l();
    final var makerQuoteLotCollateral = SignedQuoteLots.read(_data, i);
    i += makerQuoteLotCollateral.l();
    final var makerCumulativeFundingSnapshot = SignedQuoteLotsPerBaseLot.read(_data, i);
    return new OrderFilledEvent(discriminator,
                                orderSequenceNumber,
                                side,
                                price,
                                baseLotsFilled,
                                quoteLotsFilled,
                                quantityRemaining,
                                maker,
                                makerFeeRate,
                                makerBaseLotPosition,
                                makerVirtualQuoteLotPosition,
                                makerQuoteLotCollateral,
                                makerCumulativeFundingSnapshot);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, orderSequenceNumber);
    i += 8;
    i += side.write(_data, i);
    i += price.write(_data, i);
    i += baseLotsFilled.write(_data, i);
    i += quoteLotsFilled.write(_data, i);
    i += quantityRemaining.write(_data, i);
    maker.write(_data, i);
    i += 32;
    i += makerFeeRate.write(_data, i);
    i += makerBaseLotPosition.write(_data, i);
    i += makerVirtualQuoteLotPosition.write(_data, i);
    i += makerQuoteLotCollateral.write(_data, i);
    i += makerCumulativeFundingSnapshot.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.SignedBaseLots;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.SignedQuoteLots;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.Symbol;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::PnL Borsh variant 37.
/// Payload type: PnLEvent.
///
public record PnLEvent(Discriminator discriminator,
                       PublicKey trader,
                       int assetId,
                       Symbol assetSymbol,
                       SignedQuoteLots realizedPnl,
                       SignedQuoteLots fundingPayment,
                       SignedBaseLots baseLotsBefore,
                       SignedBaseLots baseLotsAfter,
                       SignedQuoteLots virtualQuoteLotsBefore,
                       SignedQuoteLots virtualQuoteLotsAfter) implements EternalEvent {

  public static final int BYTES = 108;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(37, 0, 0, 0, 0, 0, 0, 0);

  public static final int TRADER_OFFSET = 8;
  public static final int ASSET_ID_OFFSET = 40;
  public static final int ASSET_SYMBOL_OFFSET = 44;
  public static final int REALIZED_PNL_OFFSET = 60;
  public static final int FUNDING_PAYMENT_OFFSET = 68;
  public static final int BASE_LOTS_BEFORE_OFFSET = 76;
  public static final int BASE_LOTS_AFTER_OFFSET = 84;
  public static final int VIRTUAL_QUOTE_LOTS_BEFORE_OFFSET = 92;
  public static final int VIRTUAL_QUOTE_LOTS_AFTER_OFFSET = 100;

  public static PnLEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var trader = readPubKey(_data, i);
    i += 32;
    final var assetId = getInt32LE(_data, i);
    i += 4;
    final var assetSymbol = Symbol.read(_data, i);
    i += assetSymbol.l();
    final var realizedPnl = SignedQuoteLots.read(_data, i);
    i += realizedPnl.l();
    final var fundingPayment = SignedQuoteLots.read(_data, i);
    i += fundingPayment.l();
    final var baseLotsBefore = SignedBaseLots.read(_data, i);
    i += baseLotsBefore.l();
    final var baseLotsAfter = SignedBaseLots.read(_data, i);
    i += baseLotsAfter.l();
    final var virtualQuoteLotsBefore = SignedQuoteLots.read(_data, i);
    i += virtualQuoteLotsBefore.l();
    final var virtualQuoteLotsAfter = SignedQuoteLots.read(_data, i);
    return new PnLEvent(discriminator,
                        trader,
                        assetId,
                        assetSymbol,
                        realizedPnl,
                        fundingPayment,
                        baseLotsBefore,
                        baseLotsAfter,
                        virtualQuoteLotsBefore,
                        virtualQuoteLotsAfter);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    trader.write(_data, i);
    i += 32;
    putInt32LE(_data, i, assetId);
    i += 4;
    i += assetSymbol.write(_data, i);
    i += realizedPnl.write(_data, i);
    i += fundingPayment.write(_data, i);
    i += baseLotsBefore.write(_data, i);
    i += baseLotsAfter.write(_data, i);
    i += virtualQuoteLotsBefore.write(_data, i);
    i += virtualQuoteLotsAfter.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

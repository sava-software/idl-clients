package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.SignedQuoteLots;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.SignedQuoteLotsPerBaseLot;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.Symbol;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::TraderFundingSettled Borsh variant 18.
/// Payload type: TraderFundingSettledEvent.
///
public record TraderFundingSettledEvent(Discriminator discriminator,
                                        PublicKey trader,
                                        Symbol assetSymbol,
                                        int assetId,
                                        SignedQuoteLots fundingPayment,
                                        SignedQuoteLotsPerBaseLot cumulativeFundingSnapshot,
                                        SignedQuoteLots newCollateralBalance) implements EternalEvent {

  public static final int BYTES = 84;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(18, 0, 0, 0, 0, 0, 0, 0);

  public static final int TRADER_OFFSET = 8;
  public static final int ASSET_SYMBOL_OFFSET = 40;
  public static final int ASSET_ID_OFFSET = 56;
  public static final int FUNDING_PAYMENT_OFFSET = 60;
  public static final int CUMULATIVE_FUNDING_SNAPSHOT_OFFSET = 68;
  public static final int NEW_COLLATERAL_BALANCE_OFFSET = 76;

  public static TraderFundingSettledEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var trader = readPubKey(_data, i);
    i += 32;
    final var assetSymbol = Symbol.read(_data, i);
    i += assetSymbol.l();
    final var assetId = getInt32LE(_data, i);
    i += 4;
    final var fundingPayment = SignedQuoteLots.read(_data, i);
    i += fundingPayment.l();
    final var cumulativeFundingSnapshot = SignedQuoteLotsPerBaseLot.read(_data, i);
    i += cumulativeFundingSnapshot.l();
    final var newCollateralBalance = SignedQuoteLots.read(_data, i);
    return new TraderFundingSettledEvent(discriminator,
                                         trader,
                                         assetSymbol,
                                         assetId,
                                         fundingPayment,
                                         cumulativeFundingSnapshot,
                                         newCollateralBalance);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    trader.write(_data, i);
    i += 32;
    i += assetSymbol.write(_data, i);
    putInt32LE(_data, i, assetId);
    i += 4;
    i += fundingPayment.write(_data, i);
    i += cumulativeFundingSnapshot.write(_data, i);
    i += newCollateralBalance.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

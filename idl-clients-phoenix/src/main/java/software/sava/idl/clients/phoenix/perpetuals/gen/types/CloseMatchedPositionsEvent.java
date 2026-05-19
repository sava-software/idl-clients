package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.SignedBaseLots;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.SignedQuoteLots;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::CloseMatchedPositions Borsh variant 33.
/// Payload type: CloseMatchedPositionsEvent.
///
public record CloseMatchedPositionsEvent(Discriminator discriminator,
                                         PublicKey caller,
                                         PublicKey closedShort,
                                         PublicKey closedLong,
                                         PublicKey inProfitAccount,
                                         long assetId,
                                         SignedBaseLots baseLotsClosed,
                                         SignedQuoteLots atLossCloseValue,
                                         SignedQuoteLots inProfitCloseValue,
                                         SignedQuoteLots atLossCollateralChange,
                                         SignedQuoteLots inProfitCollateralChange) implements EternalEvent {

  public static final int BYTES = 184;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(33, 0, 0, 0, 0, 0, 0, 0);

  public static final int CALLER_OFFSET = 8;
  public static final int CLOSED_SHORT_OFFSET = 40;
  public static final int CLOSED_LONG_OFFSET = 72;
  public static final int IN_PROFIT_ACCOUNT_OFFSET = 104;
  public static final int ASSET_ID_OFFSET = 136;
  public static final int BASE_LOTS_CLOSED_OFFSET = 144;
  public static final int AT_LOSS_CLOSE_VALUE_OFFSET = 152;
  public static final int IN_PROFIT_CLOSE_VALUE_OFFSET = 160;
  public static final int AT_LOSS_COLLATERAL_CHANGE_OFFSET = 168;
  public static final int IN_PROFIT_COLLATERAL_CHANGE_OFFSET = 176;

  public static CloseMatchedPositionsEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var caller = readPubKey(_data, i);
    i += 32;
    final var closedShort = readPubKey(_data, i);
    i += 32;
    final var closedLong = readPubKey(_data, i);
    i += 32;
    final var inProfitAccount = readPubKey(_data, i);
    i += 32;
    final var assetId = getInt64LE(_data, i);
    i += 8;
    final var baseLotsClosed = SignedBaseLots.read(_data, i);
    i += baseLotsClosed.l();
    final var atLossCloseValue = SignedQuoteLots.read(_data, i);
    i += atLossCloseValue.l();
    final var inProfitCloseValue = SignedQuoteLots.read(_data, i);
    i += inProfitCloseValue.l();
    final var atLossCollateralChange = SignedQuoteLots.read(_data, i);
    i += atLossCollateralChange.l();
    final var inProfitCollateralChange = SignedQuoteLots.read(_data, i);
    return new CloseMatchedPositionsEvent(discriminator,
                                          caller,
                                          closedShort,
                                          closedLong,
                                          inProfitAccount,
                                          assetId,
                                          baseLotsClosed,
                                          atLossCloseValue,
                                          inProfitCloseValue,
                                          atLossCollateralChange,
                                          inProfitCollateralChange);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    caller.write(_data, i);
    i += 32;
    closedShort.write(_data, i);
    i += 32;
    closedLong.write(_data, i);
    i += 32;
    inProfitAccount.write(_data, i);
    i += 32;
    putInt64LE(_data, i, assetId);
    i += 8;
    i += baseLotsClosed.write(_data, i);
    i += atLossCloseValue.write(_data, i);
    i += inProfitCloseValue.write(_data, i);
    i += atLossCollateralChange.write(_data, i);
    i += inProfitCollateralChange.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

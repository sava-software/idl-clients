package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::TraderFeesUpdated Borsh variant 49.
/// Payload type: TraderFeesUpdatedEvent.
///
public record TraderFeesUpdatedEvent(Discriminator discriminator,
                                     PublicKey trader,
                                     PublicKey authority,
                                     int previousMakerFeeOverrideMultiplier,
                                     int newMakerFeeOverrideMultiplier,
                                     int previousTakerFeeOverrideMultiplier,
                                     int newTakerFeeOverrideMultiplier,
                                     boolean isHotTrader) implements EternalEvent {

  public static final int BYTES = 77;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(49, 0, 0, 0, 0, 0, 0, 0);

  public static final int TRADER_OFFSET = 8;
  public static final int AUTHORITY_OFFSET = 40;
  public static final int PREVIOUS_MAKER_FEE_OVERRIDE_MULTIPLIER_OFFSET = 72;
  public static final int NEW_MAKER_FEE_OVERRIDE_MULTIPLIER_OFFSET = 73;
  public static final int PREVIOUS_TAKER_FEE_OVERRIDE_MULTIPLIER_OFFSET = 74;
  public static final int NEW_TAKER_FEE_OVERRIDE_MULTIPLIER_OFFSET = 75;
  public static final int IS_HOT_TRADER_OFFSET = 76;

  public static TraderFeesUpdatedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var trader = readPubKey(_data, i);
    i += 32;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var previousMakerFeeOverrideMultiplier = _data[i];
    ++i;
    final var newMakerFeeOverrideMultiplier = _data[i];
    ++i;
    final var previousTakerFeeOverrideMultiplier = _data[i];
    ++i;
    final var newTakerFeeOverrideMultiplier = _data[i];
    ++i;
    final var isHotTrader = _data[i] == 1;
    return new TraderFeesUpdatedEvent(discriminator,
                                      trader,
                                      authority,
                                      previousMakerFeeOverrideMultiplier,
                                      newMakerFeeOverrideMultiplier,
                                      previousTakerFeeOverrideMultiplier,
                                      newTakerFeeOverrideMultiplier,
                                      isHotTrader);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    trader.write(_data, i);
    i += 32;
    authority.write(_data, i);
    i += 32;
    _data[i] = (byte) previousMakerFeeOverrideMultiplier;
    ++i;
    _data[i] = (byte) newMakerFeeOverrideMultiplier;
    ++i;
    _data[i] = (byte) previousTakerFeeOverrideMultiplier;
    ++i;
    _data[i] = (byte) newTakerFeeOverrideMultiplier;
    ++i;
    _data[i] = (byte) (isHotTrader ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

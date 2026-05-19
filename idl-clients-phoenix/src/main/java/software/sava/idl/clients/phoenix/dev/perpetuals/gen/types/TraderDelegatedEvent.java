package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::TraderDelegated Borsh variant 47.
/// Payload type: TraderDelegatedEvent.
///
public record TraderDelegatedEvent(Discriminator discriminator,
                                   PublicKey trader,
                                   PublicKey authority,
                                   PublicKey oldPositionAuthority,
                                   PublicKey newPositionAuthority) implements EternalEvent {

  public static final int BYTES = 136;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(47, 0, 0, 0, 0, 0, 0, 0);

  public static final int TRADER_OFFSET = 8;
  public static final int AUTHORITY_OFFSET = 40;
  public static final int OLD_POSITION_AUTHORITY_OFFSET = 72;
  public static final int NEW_POSITION_AUTHORITY_OFFSET = 104;

  public static TraderDelegatedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var trader = readPubKey(_data, i);
    i += 32;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var oldPositionAuthority = readPubKey(_data, i);
    i += 32;
    final var newPositionAuthority = readPubKey(_data, i);
    return new TraderDelegatedEvent(discriminator,
                                    trader,
                                    authority,
                                    oldPositionAuthority,
                                    newPositionAuthority);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    trader.write(_data, i);
    i += 32;
    authority.write(_data, i);
    i += 32;
    oldPositionAuthority.write(_data, i);
    i += 32;
    newPositionAuthority.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

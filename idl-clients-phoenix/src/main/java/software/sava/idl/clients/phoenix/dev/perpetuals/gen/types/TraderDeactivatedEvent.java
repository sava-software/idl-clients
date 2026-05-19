package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::TraderDeactivated Borsh variant 12.
/// Payload type: TraderDeactivatedEvent.
///
public record TraderDeactivatedEvent(Discriminator discriminator, int prevGlobalTraderIndex, PublicKey authority) implements EternalEvent {

  public static final int BYTES = 44;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(12, 0, 0, 0, 0, 0, 0, 0);

  public static final int PREV_GLOBAL_TRADER_INDEX_OFFSET = 8;
  public static final int AUTHORITY_OFFSET = 12;

  public static TraderDeactivatedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var prevGlobalTraderIndex = getInt32LE(_data, i);
    i += 4;
    final var authority = readPubKey(_data, i);
    return new TraderDeactivatedEvent(discriminator, prevGlobalTraderIndex, authority);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt32LE(_data, i, prevGlobalTraderIndex);
    i += 4;
    authority.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::TraderActivated Borsh variant 11.
/// Payload type: TraderActivatedEvent.
///
public record TraderActivatedEvent(Discriminator discriminator, int globalTraderIndex, PublicKey authority) implements EternalEvent {

  public static final int BYTES = 44;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(11, 0, 0, 0, 0, 0, 0, 0);

  public static final int GLOBAL_TRADER_INDEX_OFFSET = 8;
  public static final int AUTHORITY_OFFSET = 12;

  public static TraderActivatedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var globalTraderIndex = getInt32LE(_data, i);
    i += 4;
    final var authority = readPubKey(_data, i);
    return new TraderActivatedEvent(discriminator, globalTraderIndex, authority);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt32LE(_data, i, globalTraderIndex);
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

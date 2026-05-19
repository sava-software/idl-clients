package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::EscrowAccountCreated Borsh variant 52.
/// Payload type: EscrowAccountCreatedEvent.
///
public record EscrowAccountCreatedEvent(Discriminator discriminator, PublicKey authority, long capacity) implements EternalEvent {

  public static final int BYTES = 48;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(52, 0, 0, 0, 0, 0, 0, 0);

  public static final int AUTHORITY_OFFSET = 8;
  public static final int CAPACITY_OFFSET = 40;

  public static EscrowAccountCreatedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var authority = readPubKey(_data, i);
    i += 32;
    final var capacity = getInt64LE(_data, i);
    return new EscrowAccountCreatedEvent(discriminator, authority, capacity);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    authority.write(_data, i);
    i += 32;
    putInt64LE(_data, i, capacity);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

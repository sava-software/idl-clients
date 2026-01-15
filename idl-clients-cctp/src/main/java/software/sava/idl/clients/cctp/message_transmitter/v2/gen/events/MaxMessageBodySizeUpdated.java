package software.sava.idl.clients.cctp.message_transmitter.v2.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record MaxMessageBodySizeUpdated(Discriminator discriminator, long newMaxMessageBodySize) implements MessageTransmitterV2Event {

  public static final int BYTES = 16;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(134, 206, 151, 111, 137, 11, 160, 225);

  public static final int NEW_MAX_MESSAGE_BODY_SIZE_OFFSET = 8;

  public static MaxMessageBodySizeUpdated read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var newMaxMessageBodySize = getInt64LE(_data, i);
    return new MaxMessageBodySizeUpdated(discriminator, newMaxMessageBodySize);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, newMaxMessageBodySize);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

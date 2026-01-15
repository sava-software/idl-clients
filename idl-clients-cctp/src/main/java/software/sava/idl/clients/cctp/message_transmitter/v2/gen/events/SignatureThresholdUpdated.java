package software.sava.idl.clients.cctp.message_transmitter.v2.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record SignatureThresholdUpdated(Discriminator discriminator, int oldSignatureThreshold, int newSignatureThreshold) implements MessageTransmitterV2Event {

  public static final int BYTES = 16;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(156, 99, 103, 200, 15, 38, 122, 189);

  public static final int OLD_SIGNATURE_THRESHOLD_OFFSET = 8;
  public static final int NEW_SIGNATURE_THRESHOLD_OFFSET = 12;

  public static SignatureThresholdUpdated read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var oldSignatureThreshold = getInt32LE(_data, i);
    i += 4;
    final var newSignatureThreshold = getInt32LE(_data, i);
    return new SignatureThresholdUpdated(discriminator, oldSignatureThreshold, newSignatureThreshold);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt32LE(_data, i, oldSignatureThreshold);
    i += 4;
    putInt32LE(_data, i, newSignatureThreshold);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

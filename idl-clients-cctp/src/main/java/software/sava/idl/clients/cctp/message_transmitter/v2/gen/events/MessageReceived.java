package software.sava.idl.clients.cctp.message_transmitter.v2.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record MessageReceived(Discriminator discriminator,
                              PublicKey caller,
                              int sourceDomain,
                              byte[] nonce,
                              PublicKey sender,
                              int finalityThresholdExecuted,
                              byte[] messageBody) implements MessageTransmitterV2Event {

  public static final int NONCE_LEN = 32;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(231, 68, 47, 77, 173, 241, 157, 166);

  public static MessageReceived read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var caller = readPubKey(_data, i);
    i += 32;
    final var sourceDomain = getInt32LE(_data, i);
    i += 4;
    final var nonce = new byte[32];
    i += SerDeUtil.readArray(nonce, _data, i);
    final var sender = readPubKey(_data, i);
    i += 32;
    final var finalityThresholdExecuted = getInt32LE(_data, i);
    i += 4;
    final var messageBody = SerDeUtil.readbyteVector(4, _data, i);
    return new MessageReceived(discriminator,
                               caller,
                               sourceDomain,
                               nonce,
                               sender,
                               finalityThresholdExecuted,
                               messageBody);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    caller.write(_data, i);
    i += 32;
    putInt32LE(_data, i, sourceDomain);
    i += 4;
    i += SerDeUtil.writeArrayChecked(nonce, 32, _data, i);
    sender.write(_data, i);
    i += 32;
    putInt32LE(_data, i, finalityThresholdExecuted);
    i += 4;
    i += SerDeUtil.writeVector(4, messageBody, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32
         + 4
         + SerDeUtil.lenArray(nonce)
         + 32
         + 4
         + SerDeUtil.lenVector(4, messageBody);
  }
}

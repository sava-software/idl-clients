package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record ReceiveMessageParams(byte[] message, byte[] attestation) implements SerDe {

  public static final int MESSAGE_OFFSET = 0;

  public static ReceiveMessageParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var message = SerDeUtil.readbyteVector(4, _data, i);
    i += SerDeUtil.lenVector(4, message);
    final var attestation = SerDeUtil.readbyteVector(4, _data, i);
    return new ReceiveMessageParams(message, attestation);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeVector(4, message, _data, i);
    i += SerDeUtil.writeVector(4, attestation, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return SerDeUtil.lenVector(4, message) + SerDeUtil.lenVector(4, attestation);
  }
}

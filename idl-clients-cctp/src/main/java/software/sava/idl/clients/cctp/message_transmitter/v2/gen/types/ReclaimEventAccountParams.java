package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record ReclaimEventAccountParams(byte[] attestation, byte[] destinationMessage) implements SerDe {

  public static ReclaimEventAccountParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var attestation = SerDeUtil.readbyteVector(4, _data, i);
    i += SerDeUtil.lenVector(4, attestation);
    final var destinationMessage = SerDeUtil.readbyteVector(4, _data, i);
    return new ReclaimEventAccountParams(attestation, destinationMessage);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeVector(4, attestation, _data, i);
    i += SerDeUtil.writeVector(4, destinationMessage, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return SerDeUtil.lenVector(4, attestation) + SerDeUtil.lenVector(4, destinationMessage);
  }
}

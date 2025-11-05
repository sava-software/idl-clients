package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import software.sava.core.borsh.Borsh;

public record ReceiveMessageParams(byte[] message, byte[] attestation) implements Borsh {

  public static ReceiveMessageParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var message = Borsh.readbyteVector(_data, i);
    i += Borsh.lenVector(message);
    final var attestation = Borsh.readbyteVector(_data, i);
    return new ReceiveMessageParams(message, attestation);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeVector(message, _data, i);
    i += Borsh.writeVector(attestation, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return Borsh.lenVector(message) + Borsh.lenVector(attestation);
  }
}

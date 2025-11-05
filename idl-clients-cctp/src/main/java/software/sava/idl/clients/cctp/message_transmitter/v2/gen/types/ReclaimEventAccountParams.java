package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import software.sava.core.borsh.Borsh;

public record ReclaimEventAccountParams(byte[] attestation, byte[] destinationMessage) implements Borsh {

  public static ReclaimEventAccountParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var attestation = Borsh.readbyteVector(_data, i);
    i += Borsh.lenVector(attestation);
    final var destinationMessage = Borsh.readbyteVector(_data, i);
    return new ReclaimEventAccountParams(attestation, destinationMessage);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeVector(attestation, _data, i);
    i += Borsh.writeVector(destinationMessage, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return Borsh.lenVector(attestation) + Borsh.lenVector(destinationMessage);
  }
}

package software.sava.idl.clients.oracles.pyth.push.gen.types;

import software.sava.core.borsh.Borsh;

public record MerklePriceUpdate(byte[] message, byte[][] proof) implements Borsh {

  public static MerklePriceUpdate read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var message = Borsh.readbyteVector(_data, i);
    i += Borsh.lenVector(message);
    final var proof = Borsh.readMultiDimensionbyteVectorArray(20, _data, i);
    return new MerklePriceUpdate(message, proof);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeVector(message, _data, i);
    i += Borsh.writeVectorArrayChecked(proof, 20, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return Borsh.lenVector(message) + Borsh.lenVectorArray(proof);
  }
}

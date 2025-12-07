package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

public record ProofInfo(byte[][] proof) implements Borsh {

  public static ProofInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var proof = Borsh.readMultiDimensionbyteVectorArray(32, _data, _offset);
    return new ProofInfo(proof);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeVectorArrayChecked(proof, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return Borsh.lenVectorArray(proof);
  }
}

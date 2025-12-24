package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record ProofInfo(byte[][] proof) implements SerDe {

  public static ProofInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var proof = SerDeUtil.readMultiDimensionbyteVectorArray(4, 32, _data, _offset);
    return new ProofInfo(proof);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeVectorArrayChecked(4, proof, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return SerDeUtil.lenVectorArray(4, proof);
  }
}

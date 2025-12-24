package software.sava.idl.clients.oracles.pyth.push.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record MerklePriceUpdate(byte[] message, byte[][] proof) implements SerDe {

  public static MerklePriceUpdate read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var message = SerDeUtil.readbyteVector(4, _data, i);
    i += SerDeUtil.lenVector(4, message);
    final var proof = SerDeUtil.readMultiDimensionbyteVectorArray(4, 20, _data, i);
    return new MerklePriceUpdate(message, proof);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeVector(4, message, _data, i);
    i += SerDeUtil.writeVectorArrayChecked(4, proof, 20, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return SerDeUtil.lenVector(4, message) + SerDeUtil.lenVectorArray(4, proof);
  }
}

package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record MultiSubmission(BigInteger[] values,
                              byte[] signature,
                              int recoveryId) implements SerDe {

  public static final int SIGNATURE_LEN = 64;
  public static MultiSubmission read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var values = SerDeUtil.read128Vector(4, _data, i);
    i += SerDeUtil.len128Vector(4, values);
    final var signature = new byte[64];
    i += SerDeUtil.readArray(signature, _data, i);
    final var recoveryId = _data[i] & 0xFF;
    return new MultiSubmission(values, signature, recoveryId);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.write128Vector(4, values, _data, i);
    i += SerDeUtil.writeArrayChecked(signature, 64, _data, i);
    _data[i] = (byte) recoveryId;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return SerDeUtil.len128Vector(4, values) + SerDeUtil.lenArray(signature) + 1;
  }
}

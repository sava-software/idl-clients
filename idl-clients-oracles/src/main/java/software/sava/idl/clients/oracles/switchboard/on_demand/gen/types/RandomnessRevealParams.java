package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record RandomnessRevealParams(byte[] signature,
                                     int recoveryId,
                                     byte[] value) implements SerDe {

  public static final int BYTES = 97;
  public static final int SIGNATURE_LEN = 64;
  public static final int VALUE_LEN = 32;

  public static RandomnessRevealParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var signature = new byte[64];
    i += SerDeUtil.readArray(signature, _data, i);
    final var recoveryId = _data[i] & 0xFF;
    ++i;
    final var value = new byte[32];
    SerDeUtil.readArray(value, _data, i);
    return new RandomnessRevealParams(signature, recoveryId, value);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(signature, 64, _data, i);
    _data[i] = (byte) recoveryId;
    ++i;
    i += SerDeUtil.writeArrayChecked(value, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

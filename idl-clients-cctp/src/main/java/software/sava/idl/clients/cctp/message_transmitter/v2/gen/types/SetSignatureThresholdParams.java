package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record SetSignatureThresholdParams(int newSignatureThreshold) implements SerDe {

  public static final int BYTES = 4;

  public static SetSignatureThresholdParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var newSignatureThreshold = getInt32LE(_data, _offset);
    return new SetSignatureThresholdParams(newSignatureThreshold);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, newSignatureThreshold);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

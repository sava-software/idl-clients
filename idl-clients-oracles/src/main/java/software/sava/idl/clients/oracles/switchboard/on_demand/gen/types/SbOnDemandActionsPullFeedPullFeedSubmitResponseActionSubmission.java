package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;

public record SbOnDemandActionsPullFeedPullFeedSubmitResponseActionSubmission(BigInteger value,
                                                                              byte[] signature,
                                                                              int recoveryId,
                                                                              int offset) implements SerDe {

  public static final int BYTES = 82;
  public static final int SIGNATURE_LEN = 64;

  public static SbOnDemandActionsPullFeedPullFeedSubmitResponseActionSubmission read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var value = getInt128LE(_data, i);
    i += 16;
    final var signature = new byte[64];
    i += SerDeUtil.readArray(signature, _data, i);
    final var recoveryId = _data[i] & 0xFF;
    ++i;
    final var offset = _data[i] & 0xFF;
    return new SbOnDemandActionsPullFeedPullFeedSubmitResponseActionSubmission(value,
                                                                               signature,
                                                                               recoveryId,
                                                                               offset);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt128LE(_data, i, value);
    i += 16;
    i += SerDeUtil.writeArrayChecked(signature, 64, _data, i);
    _data[i] = (byte) recoveryId;
    ++i;
    _data[i] = (byte) offset;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

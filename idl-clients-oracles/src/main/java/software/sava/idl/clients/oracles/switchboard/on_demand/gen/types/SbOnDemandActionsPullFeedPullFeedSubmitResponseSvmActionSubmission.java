package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;

public record SbOnDemandActionsPullFeedPullFeedSubmitResponseSvmActionSubmission(BigInteger value,
                                                                                 byte[] signature,
                                                                                 int recoveryId) implements SerDe {

  public static final int BYTES = 81;
  public static final int SIGNATURE_LEN = 64;

  public static final int VALUE_OFFSET = 0;
  public static final int SIGNATURE_OFFSET = 16;
  public static final int RECOVERY_ID_OFFSET = 80;

  public static SbOnDemandActionsPullFeedPullFeedSubmitResponseSvmActionSubmission read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var value = getInt128LE(_data, i);
    i += 16;
    final var signature = new byte[64];
    i += SerDeUtil.readArray(signature, _data, i);
    final var recoveryId = _data[i] & 0xFF;
    return new SbOnDemandActionsPullFeedPullFeedSubmitResponseSvmActionSubmission(value, signature, recoveryId);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt128LE(_data, i, value);
    i += 16;
    i += SerDeUtil.writeArrayChecked(signature, 64, _data, i);
    _data[i] = (byte) recoveryId;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record PullFeedSubmitResponseManyParams(long slot, MultiSubmission[] submissions) implements SerDe {

  public static final int SLOT_OFFSET = 0;
  public static final int SUBMISSIONS_OFFSET = 8;

  public static PullFeedSubmitResponseManyParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var slot = getInt64LE(_data, i);
    i += 8;
    final var submissions = SerDeUtil.readVector(4, MultiSubmission.class, MultiSubmission::read, _data, i);
    return new PullFeedSubmitResponseManyParams(slot, submissions);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, slot);
    i += 8;
    i += SerDeUtil.writeVector(4, submissions, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + SerDeUtil.lenVector(4, submissions);
  }
}

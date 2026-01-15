package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record PullFeedSubmitResponseSVMParams(long slot,
                                              SbOnDemandActionsPullFeedPullFeedSubmitResponseSvmActionSubmission[] submissions,
                                              PublicKey sourceQueueKey,
                                              int queueBump) implements SerDe {

  public static final int SLOT_OFFSET = 0;
  public static final int SUBMISSIONS_OFFSET = 8;

  public static PullFeedSubmitResponseSVMParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var slot = getInt64LE(_data, i);
    i += 8;
    final var submissions = SerDeUtil.readVector(4, SbOnDemandActionsPullFeedPullFeedSubmitResponseSvmActionSubmission.class, SbOnDemandActionsPullFeedPullFeedSubmitResponseSvmActionSubmission::read, _data, i);
    i += SerDeUtil.lenVector(4, submissions);
    final var sourceQueueKey = readPubKey(_data, i);
    i += 32;
    final var queueBump = _data[i] & 0xFF;
    return new PullFeedSubmitResponseSVMParams(slot,
                                               submissions,
                                               sourceQueueKey,
                                               queueBump);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, slot);
    i += 8;
    i += SerDeUtil.writeVector(4, submissions, _data, i);
    sourceQueueKey.write(_data, i);
    i += 32;
    _data[i] = (byte) queueBump;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + SerDeUtil.lenVector(4, submissions) + 32 + 1;
  }
}

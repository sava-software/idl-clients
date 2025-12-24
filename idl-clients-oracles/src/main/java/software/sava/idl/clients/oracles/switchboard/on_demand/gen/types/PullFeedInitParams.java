package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import java.lang.Boolean;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record PullFeedInitParams(byte[] feedHash,
                                 long maxVariance,
                                 int minResponses,
                                 byte[] name,
                                 long recentSlot,
                                 byte[] ipfsHash,
                                 int minSampleSize,
                                 int maxStaleness,
                                 Boolean permitWriteByAuthority) implements SerDe {

  public static final int FEED_HASH_LEN = 32;
  public static final int NAME_LEN = 32;
  public static final int IPFS_HASH_LEN = 32;
  public static PullFeedInitParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var feedHash = new byte[32];
    i += SerDeUtil.readArray(feedHash, _data, i);
    final var maxVariance = getInt64LE(_data, i);
    i += 8;
    final var minResponses = getInt32LE(_data, i);
    i += 4;
    final var name = new byte[32];
    i += SerDeUtil.readArray(name, _data, i);
    final var recentSlot = getInt64LE(_data, i);
    i += 8;
    final var ipfsHash = new byte[32];
    i += SerDeUtil.readArray(ipfsHash, _data, i);
    final var minSampleSize = _data[i] & 0xFF;
    ++i;
    final var maxStaleness = getInt32LE(_data, i);
    i += 4;
    final Boolean permitWriteByAuthority;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      permitWriteByAuthority = null;
    } else {
      ++i;
      permitWriteByAuthority = _data[i] == 1;
    }
    return new PullFeedInitParams(feedHash,
                                  maxVariance,
                                  minResponses,
                                  name,
                                  recentSlot,
                                  ipfsHash,
                                  minSampleSize,
                                  maxStaleness,
                                  permitWriteByAuthority);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(feedHash, 32, _data, i);
    putInt64LE(_data, i, maxVariance);
    i += 8;
    putInt32LE(_data, i, minResponses);
    i += 4;
    i += SerDeUtil.writeArrayChecked(name, 32, _data, i);
    putInt64LE(_data, i, recentSlot);
    i += 8;
    i += SerDeUtil.writeArrayChecked(ipfsHash, 32, _data, i);
    _data[i] = (byte) minSampleSize;
    ++i;
    putInt32LE(_data, i, maxStaleness);
    i += 4;
    i += SerDeUtil.writeOptional(1, permitWriteByAuthority, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return SerDeUtil.lenArray(feedHash)
         + 8
         + 4
         + SerDeUtil.lenArray(name)
         + 8
         + SerDeUtil.lenArray(ipfsHash)
         + 1
         + 4
         + (permitWriteByAuthority == null ? 1 : (1 + 1));
  }
}

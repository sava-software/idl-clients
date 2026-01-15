package software.sava.idl.clients.meteora.dlmm.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record UserRewardInfo(BigInteger[] rewardPerTokenCompletes, long[] rewardPendings) implements SerDe {

  public static final int BYTES = 48;
  public static final int REWARD_PER_TOKEN_COMPLETES_LEN = 2;
  public static final int REWARD_PENDINGS_LEN = 2;

  public static final int REWARD_PER_TOKEN_COMPLETES_OFFSET = 0;
  public static final int REWARD_PENDINGS_OFFSET = 32;

  public static UserRewardInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var rewardPerTokenCompletes = new BigInteger[2];
    i += SerDeUtil.read128Array(rewardPerTokenCompletes, _data, i);
    final var rewardPendings = new long[2];
    SerDeUtil.readArray(rewardPendings, _data, i);
    return new UserRewardInfo(rewardPerTokenCompletes, rewardPendings);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.write128ArrayChecked(rewardPerTokenCompletes, 2, _data, i);
    i += SerDeUtil.writeArrayChecked(rewardPendings, 2, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

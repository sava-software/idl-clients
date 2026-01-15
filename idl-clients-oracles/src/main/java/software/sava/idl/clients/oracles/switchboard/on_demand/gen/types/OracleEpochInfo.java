package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record OracleEpochInfo(long id,
                              long reserved1,
                              long reserved,
                              long slashScore,
                              long rewardScore,
                              long stakeScore) implements SerDe {

  public static final int BYTES = 48;

  public static final int ID_OFFSET = 0;
  public static final int RESERVED_1_OFFSET = 8;
  public static final int RESERVED_OFFSET = 16;
  public static final int SLASH_SCORE_OFFSET = 24;
  public static final int REWARD_SCORE_OFFSET = 32;
  public static final int STAKE_SCORE_OFFSET = 40;

  public static OracleEpochInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var id = getInt64LE(_data, i);
    i += 8;
    final var reserved1 = getInt64LE(_data, i);
    i += 8;
    final var reserved = getInt64LE(_data, i);
    i += 8;
    final var slashScore = getInt64LE(_data, i);
    i += 8;
    final var rewardScore = getInt64LE(_data, i);
    i += 8;
    final var stakeScore = getInt64LE(_data, i);
    return new OracleEpochInfo(id,
                               reserved1,
                               reserved,
                               slashScore,
                               rewardScore,
                               stakeScore);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, id);
    i += 8;
    putInt64LE(_data, i, reserved1);
    i += 8;
    putInt64LE(_data, i, reserved);
    i += 8;
    putInt64LE(_data, i, slashScore);
    i += 8;
    putInt64LE(_data, i, rewardScore);
    i += 8;
    putInt64LE(_data, i, stakeScore);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

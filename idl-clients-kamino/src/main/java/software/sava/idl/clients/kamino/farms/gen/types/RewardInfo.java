package software.sava.idl.clients.kamino.farms.gen.types;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record RewardInfo(TokenInfo token,
                         PublicKey rewardsVault,
                         long rewardsAvailable,
                         RewardScheduleCurve rewardScheduleCurve,
                         long minClaimDurationSeconds,
                         long lastIssuanceTs,
                         long rewardsIssuedUnclaimed,
                         long rewardsIssuedCumulative,
                         BigInteger rewardPerShareScaled,
                         long placeholder0,
                         int rewardType,
                         int rewardsPerSecondDecimals,
                         byte[] padding0,
                         long[] padding1) implements SerDe {

  public static final int BYTES = 704;
  public static final int PADDING_0_LEN = 6;
  public static final int PADDING_1_LEN = 20;

  public static final int TOKEN_OFFSET = 0;
  public static final int REWARDS_VAULT_OFFSET = 120;
  public static final int REWARDS_AVAILABLE_OFFSET = 152;
  public static final int REWARD_SCHEDULE_CURVE_OFFSET = 160;
  public static final int MIN_CLAIM_DURATION_SECONDS_OFFSET = 480;
  public static final int LAST_ISSUANCE_TS_OFFSET = 488;
  public static final int REWARDS_ISSUED_UNCLAIMED_OFFSET = 496;
  public static final int REWARDS_ISSUED_CUMULATIVE_OFFSET = 504;
  public static final int REWARD_PER_SHARE_SCALED_OFFSET = 512;
  public static final int PLACEHOLDER_0_OFFSET = 528;
  public static final int REWARD_TYPE_OFFSET = 536;
  public static final int REWARDS_PER_SECOND_DECIMALS_OFFSET = 537;
  public static final int PADDING_0_OFFSET = 538;
  public static final int PADDING_1_OFFSET = 544;

  public static RewardInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var token = TokenInfo.read(_data, i);
    i += token.l();
    final var rewardsVault = readPubKey(_data, i);
    i += 32;
    final var rewardsAvailable = getInt64LE(_data, i);
    i += 8;
    final var rewardScheduleCurve = RewardScheduleCurve.read(_data, i);
    i += rewardScheduleCurve.l();
    final var minClaimDurationSeconds = getInt64LE(_data, i);
    i += 8;
    final var lastIssuanceTs = getInt64LE(_data, i);
    i += 8;
    final var rewardsIssuedUnclaimed = getInt64LE(_data, i);
    i += 8;
    final var rewardsIssuedCumulative = getInt64LE(_data, i);
    i += 8;
    final var rewardPerShareScaled = getInt128LE(_data, i);
    i += 16;
    final var placeholder0 = getInt64LE(_data, i);
    i += 8;
    final var rewardType = _data[i] & 0xFF;
    ++i;
    final var rewardsPerSecondDecimals = _data[i] & 0xFF;
    ++i;
    final var padding0 = new byte[6];
    i += SerDeUtil.readArray(padding0, _data, i);
    final var padding1 = new long[20];
    SerDeUtil.readArray(padding1, _data, i);
    return new RewardInfo(token,
                          rewardsVault,
                          rewardsAvailable,
                          rewardScheduleCurve,
                          minClaimDurationSeconds,
                          lastIssuanceTs,
                          rewardsIssuedUnclaimed,
                          rewardsIssuedCumulative,
                          rewardPerShareScaled,
                          placeholder0,
                          rewardType,
                          rewardsPerSecondDecimals,
                          padding0,
                          padding1);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += token.write(_data, i);
    rewardsVault.write(_data, i);
    i += 32;
    putInt64LE(_data, i, rewardsAvailable);
    i += 8;
    i += rewardScheduleCurve.write(_data, i);
    putInt64LE(_data, i, minClaimDurationSeconds);
    i += 8;
    putInt64LE(_data, i, lastIssuanceTs);
    i += 8;
    putInt64LE(_data, i, rewardsIssuedUnclaimed);
    i += 8;
    putInt64LE(_data, i, rewardsIssuedCumulative);
    i += 8;
    putInt128LE(_data, i, rewardPerShareScaled);
    i += 16;
    putInt64LE(_data, i, placeholder0);
    i += 8;
    _data[i] = (byte) rewardType;
    ++i;
    _data[i] = (byte) rewardsPerSecondDecimals;
    ++i;
    i += SerDeUtil.writeArrayChecked(padding0, 6, _data, i);
    i += SerDeUtil.writeArrayChecked(padding1, 20, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

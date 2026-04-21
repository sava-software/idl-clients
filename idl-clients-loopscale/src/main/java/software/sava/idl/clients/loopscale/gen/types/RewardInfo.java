package software.sava.idl.clients.loopscale.gen.types;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param rewardState Reward state
/// @param openTime Reward open time
/// @param endTime Reward end time
/// @param lastUpdateTime Reward last update time
/// @param emissionsPerSecondX64 Q64.64 number indicates how many tokens per second are earned per unit of liquidity.
/// @param rewardTotalEmissioned The total amount of reward emissioned
/// @param rewardClaimed The total amount of claimed reward
/// @param tokenMint Reward token mint.
/// @param tokenVault Reward vault token account.
/// @param authority The owner that has permission to set reward param
/// @param rewardGrowthGlobalX64 Q64.64 number that tracks the total tokens earned per unit of liquidity since the reward
///                              emissions were turned on.
public record RewardInfo(int rewardState,
                         long openTime,
                         long endTime,
                         long lastUpdateTime,
                         BigInteger emissionsPerSecondX64,
                         long rewardTotalEmissioned,
                         long rewardClaimed,
                         PublicKey tokenMint,
                         PublicKey tokenVault,
                         PublicKey authority,
                         BigInteger rewardGrowthGlobalX64) implements SerDe {

  public static final int BYTES = 169;

  public static final int REWARD_STATE_OFFSET = 0;
  public static final int OPEN_TIME_OFFSET = 1;
  public static final int END_TIME_OFFSET = 9;
  public static final int LAST_UPDATE_TIME_OFFSET = 17;
  public static final int EMISSIONS_PER_SECOND_X_66_OFFSET = 25;
  public static final int REWARD_TOTAL_EMISSIONED_OFFSET = 41;
  public static final int REWARD_CLAIMED_OFFSET = 49;
  public static final int TOKEN_MINT_OFFSET = 57;
  public static final int TOKEN_VAULT_OFFSET = 89;
  public static final int AUTHORITY_OFFSET = 121;
  public static final int REWARD_GROWTH_GLOBAL_X_66_OFFSET = 153;

  public static RewardInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var rewardState = _data[i] & 0xFF;
    ++i;
    final var openTime = getInt64LE(_data, i);
    i += 8;
    final var endTime = getInt64LE(_data, i);
    i += 8;
    final var lastUpdateTime = getInt64LE(_data, i);
    i += 8;
    final var emissionsPerSecondX64 = getInt128LE(_data, i);
    i += 16;
    final var rewardTotalEmissioned = getInt64LE(_data, i);
    i += 8;
    final var rewardClaimed = getInt64LE(_data, i);
    i += 8;
    final var tokenMint = readPubKey(_data, i);
    i += 32;
    final var tokenVault = readPubKey(_data, i);
    i += 32;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var rewardGrowthGlobalX64 = getInt128LE(_data, i);
    return new RewardInfo(rewardState,
                          openTime,
                          endTime,
                          lastUpdateTime,
                          emissionsPerSecondX64,
                          rewardTotalEmissioned,
                          rewardClaimed,
                          tokenMint,
                          tokenVault,
                          authority,
                          rewardGrowthGlobalX64);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) rewardState;
    ++i;
    putInt64LE(_data, i, openTime);
    i += 8;
    putInt64LE(_data, i, endTime);
    i += 8;
    putInt64LE(_data, i, lastUpdateTime);
    i += 8;
    putInt128LE(_data, i, emissionsPerSecondX64);
    i += 16;
    putInt64LE(_data, i, rewardTotalEmissioned);
    i += 8;
    putInt64LE(_data, i, rewardClaimed);
    i += 8;
    tokenMint.write(_data, i);
    i += 32;
    tokenVault.write(_data, i);
    i += 32;
    authority.write(_data, i);
    i += 32;
    putInt128LE(_data, i, rewardGrowthGlobalX64);
    i += 16;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

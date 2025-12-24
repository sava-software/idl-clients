package software.sava.idl.clients.meteora.dlmm.gen.types;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Stores the state relevant for tracking liquidity mining rewards
///
/// @param mint Reward token mint.
/// @param vault Reward vault token account.
/// @param funder Authority account that allows to fund rewards
/// @param rewardDuration LM reward duration in seconds.
/// @param rewardDurationEnd LM reward duration end time.
/// @param rewardRate LM reward rate
/// @param lastUpdateTime The last time reward states were updated.
/// @param cumulativeSecondsWithEmptyLiquidityReward Accumulated seconds where when farm distribute rewards, but the bin is empty. The reward will be accumulated for next reward time window.
public record RewardInfo(PublicKey mint,
                         PublicKey vault,
                         PublicKey funder,
                         long rewardDuration,
                         long rewardDurationEnd,
                         BigInteger rewardRate,
                         long lastUpdateTime,
                         long cumulativeSecondsWithEmptyLiquidityReward) implements SerDe {

  public static final int BYTES = 144;

  public static RewardInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var mint = readPubKey(_data, i);
    i += 32;
    final var vault = readPubKey(_data, i);
    i += 32;
    final var funder = readPubKey(_data, i);
    i += 32;
    final var rewardDuration = getInt64LE(_data, i);
    i += 8;
    final var rewardDurationEnd = getInt64LE(_data, i);
    i += 8;
    final var rewardRate = getInt128LE(_data, i);
    i += 16;
    final var lastUpdateTime = getInt64LE(_data, i);
    i += 8;
    final var cumulativeSecondsWithEmptyLiquidityReward = getInt64LE(_data, i);
    return new RewardInfo(mint,
                          vault,
                          funder,
                          rewardDuration,
                          rewardDurationEnd,
                          rewardRate,
                          lastUpdateTime,
                          cumulativeSecondsWithEmptyLiquidityReward);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    mint.write(_data, i);
    i += 32;
    vault.write(_data, i);
    i += 32;
    funder.write(_data, i);
    i += 32;
    putInt64LE(_data, i, rewardDuration);
    i += 8;
    putInt64LE(_data, i, rewardDurationEnd);
    i += 8;
    putInt128LE(_data, i, rewardRate);
    i += 16;
    putInt64LE(_data, i, lastUpdateTime);
    i += 8;
    putInt64LE(_data, i, cumulativeSecondsWithEmptyLiquidityReward);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.jupiter.lend_borrow.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// @param mint @dev mint address
/// @param startTvl @dev tvl below which rewards rate is 0. If current TVL is below this value, triggering `update_rate()` on the fToken
///                 might bring the total TVL above this cut-off.
/// @param duration @dev for how long current rewards should run
/// @param startTime @dev when current rewards got started
/// @param yearlyReward @dev current annualized reward based on input params (duration, rewardAmount)
/// @param nextDuration @dev Duration for the next rewards phase
/// @param nextRewardAmount @dev Amount of rewards for the next phase
public record LendingRewardsRateModel(PublicKey _address,
                                      Discriminator discriminator,
                                      PublicKey mint,
                                      long startTvl,
                                      long duration,
                                      long startTime,
                                      long yearlyReward,
                                      long nextDuration,
                                      long nextRewardAmount,
                                      int bump) implements Borsh {

  public static final int BYTES = 89;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(166, 72, 71, 131, 172, 74, 166, 181);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int MINT_OFFSET = 8;
  public static final int START_TVL_OFFSET = 40;
  public static final int DURATION_OFFSET = 48;
  public static final int START_TIME_OFFSET = 56;
  public static final int YEARLY_REWARD_OFFSET = 64;
  public static final int NEXT_DURATION_OFFSET = 72;
  public static final int NEXT_REWARD_AMOUNT_OFFSET = 80;
  public static final int BUMP_OFFSET = 88;

  public static Filter createMintFilter(final PublicKey mint) {
    return Filter.createMemCompFilter(MINT_OFFSET, mint);
  }

  public static Filter createStartTvlFilter(final long startTvl) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, startTvl);
    return Filter.createMemCompFilter(START_TVL_OFFSET, _data);
  }

  public static Filter createDurationFilter(final long duration) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, duration);
    return Filter.createMemCompFilter(DURATION_OFFSET, _data);
  }

  public static Filter createStartTimeFilter(final long startTime) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, startTime);
    return Filter.createMemCompFilter(START_TIME_OFFSET, _data);
  }

  public static Filter createYearlyRewardFilter(final long yearlyReward) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, yearlyReward);
    return Filter.createMemCompFilter(YEARLY_REWARD_OFFSET, _data);
  }

  public static Filter createNextDurationFilter(final long nextDuration) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, nextDuration);
    return Filter.createMemCompFilter(NEXT_DURATION_OFFSET, _data);
  }

  public static Filter createNextRewardAmountFilter(final long nextRewardAmount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, nextRewardAmount);
    return Filter.createMemCompFilter(NEXT_REWARD_AMOUNT_OFFSET, _data);
  }

  public static Filter createBumpFilter(final int bump) {
    return Filter.createMemCompFilter(BUMP_OFFSET, new byte[]{(byte) bump});
  }

  public static LendingRewardsRateModel read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static LendingRewardsRateModel read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static LendingRewardsRateModel read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], LendingRewardsRateModel> FACTORY = LendingRewardsRateModel::read;

  public static LendingRewardsRateModel read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var mint = readPubKey(_data, i);
    i += 32;
    final var startTvl = getInt64LE(_data, i);
    i += 8;
    final var duration = getInt64LE(_data, i);
    i += 8;
    final var startTime = getInt64LE(_data, i);
    i += 8;
    final var yearlyReward = getInt64LE(_data, i);
    i += 8;
    final var nextDuration = getInt64LE(_data, i);
    i += 8;
    final var nextRewardAmount = getInt64LE(_data, i);
    i += 8;
    final var bump = _data[i] & 0xFF;
    return new LendingRewardsRateModel(_address,
                                       discriminator,
                                       mint,
                                       startTvl,
                                       duration,
                                       startTime,
                                       yearlyReward,
                                       nextDuration,
                                       nextRewardAmount,
                                       bump);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    mint.write(_data, i);
    i += 32;
    putInt64LE(_data, i, startTvl);
    i += 8;
    putInt64LE(_data, i, duration);
    i += 8;
    putInt64LE(_data, i, startTime);
    i += 8;
    putInt64LE(_data, i, yearlyReward);
    i += 8;
    putInt64LE(_data, i, nextDuration);
    i += 8;
    putInt64LE(_data, i, nextRewardAmount);
    i += 8;
    _data[i] = (byte) bump;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

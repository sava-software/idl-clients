package software.sava.idl.clients.meteora.dlmm.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// @param lbPair The LB pair of this position
/// @param owner Owner of the position. Client rely on this to to fetch their positions.
/// @param liquidityShares Liquidity shares of this position in bins (lower_bin_id <-> upper_bin_id). This is the same as LP concept.
/// @param rewardInfos Farming reward information
/// @param feeInfos Swap fee to claim information
/// @param lowerBinId Lower bin ID
/// @param upperBinId Upper bin ID
/// @param lastUpdatedAt Last updated timestamp
/// @param totalClaimedFeeXAmount Total claimed token fee X
/// @param totalClaimedFeeYAmount Total claimed token fee Y
/// @param totalClaimedRewards Total claimed rewards
/// @param reserved Reserved space for future use
public record Position(PublicKey _address,
                       Discriminator discriminator,
                       PublicKey lbPair,
                       PublicKey owner,
                       long[] liquidityShares,
                       UserRewardInfo[] rewardInfos,
                       FeeInfo[] feeInfos,
                       int lowerBinId,
                       int upperBinId,
                       long lastUpdatedAt,
                       long totalClaimedFeeXAmount,
                       long totalClaimedFeeYAmount,
                       long[] totalClaimedRewards,
                       byte[] reserved) implements SerDe {

  public static final int BYTES = 7560;
  public static final int LIQUIDITY_SHARES_LEN = 70;
  public static final int REWARD_INFOS_LEN = 70;
  public static final int FEE_INFOS_LEN = 70;
  public static final int TOTAL_CLAIMED_REWARDS_LEN = 2;
  public static final int RESERVED_LEN = 160;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(170, 188, 143, 228, 122, 64, 247, 208);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int LB_PAIR_OFFSET = 8;
  public static final int OWNER_OFFSET = 40;
  public static final int LIQUIDITY_SHARES_OFFSET = 72;
  public static final int REWARD_INFOS_OFFSET = 632;
  public static final int FEE_INFOS_OFFSET = 3992;
  public static final int LOWER_BIN_ID_OFFSET = 7352;
  public static final int UPPER_BIN_ID_OFFSET = 7356;
  public static final int LAST_UPDATED_AT_OFFSET = 7360;
  public static final int TOTAL_CLAIMED_FEE_X_AMOUNT_OFFSET = 7368;
  public static final int TOTAL_CLAIMED_FEE_Y_AMOUNT_OFFSET = 7376;
  public static final int TOTAL_CLAIMED_REWARDS_OFFSET = 7384;
  public static final int RESERVED_OFFSET = 7400;

  public static Filter createLbPairFilter(final PublicKey lbPair) {
    return Filter.createMemCompFilter(LB_PAIR_OFFSET, lbPair);
  }

  public static Filter createOwnerFilter(final PublicKey owner) {
    return Filter.createMemCompFilter(OWNER_OFFSET, owner);
  }

  public static Filter createLowerBinIdFilter(final int lowerBinId) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, lowerBinId);
    return Filter.createMemCompFilter(LOWER_BIN_ID_OFFSET, _data);
  }

  public static Filter createUpperBinIdFilter(final int upperBinId) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, upperBinId);
    return Filter.createMemCompFilter(UPPER_BIN_ID_OFFSET, _data);
  }

  public static Filter createLastUpdatedAtFilter(final long lastUpdatedAt) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, lastUpdatedAt);
    return Filter.createMemCompFilter(LAST_UPDATED_AT_OFFSET, _data);
  }

  public static Filter createTotalClaimedFeeXAmountFilter(final long totalClaimedFeeXAmount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalClaimedFeeXAmount);
    return Filter.createMemCompFilter(TOTAL_CLAIMED_FEE_X_AMOUNT_OFFSET, _data);
  }

  public static Filter createTotalClaimedFeeYAmountFilter(final long totalClaimedFeeYAmount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalClaimedFeeYAmount);
    return Filter.createMemCompFilter(TOTAL_CLAIMED_FEE_Y_AMOUNT_OFFSET, _data);
  }

  public static Position read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Position read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Position read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Position> FACTORY = Position::read;

  public static Position read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var owner = readPubKey(_data, i);
    i += 32;
    final var liquidityShares = new long[70];
    i += SerDeUtil.readArray(liquidityShares, _data, i);
    final var rewardInfos = new UserRewardInfo[70];
    i += SerDeUtil.readArray(rewardInfos, UserRewardInfo::read, _data, i);
    final var feeInfos = new FeeInfo[70];
    i += SerDeUtil.readArray(feeInfos, FeeInfo::read, _data, i);
    final var lowerBinId = getInt32LE(_data, i);
    i += 4;
    final var upperBinId = getInt32LE(_data, i);
    i += 4;
    final var lastUpdatedAt = getInt64LE(_data, i);
    i += 8;
    final var totalClaimedFeeXAmount = getInt64LE(_data, i);
    i += 8;
    final var totalClaimedFeeYAmount = getInt64LE(_data, i);
    i += 8;
    final var totalClaimedRewards = new long[2];
    i += SerDeUtil.readArray(totalClaimedRewards, _data, i);
    final var reserved = new byte[160];
    SerDeUtil.readArray(reserved, _data, i);
    return new Position(_address,
                        discriminator,
                        lbPair,
                        owner,
                        liquidityShares,
                        rewardInfos,
                        feeInfos,
                        lowerBinId,
                        upperBinId,
                        lastUpdatedAt,
                        totalClaimedFeeXAmount,
                        totalClaimedFeeYAmount,
                        totalClaimedRewards,
                        reserved);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    owner.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(liquidityShares, 70, _data, i);
    i += SerDeUtil.writeArrayChecked(rewardInfos, 70, _data, i);
    i += SerDeUtil.writeArrayChecked(feeInfos, 70, _data, i);
    putInt32LE(_data, i, lowerBinId);
    i += 4;
    putInt32LE(_data, i, upperBinId);
    i += 4;
    putInt64LE(_data, i, lastUpdatedAt);
    i += 8;
    putInt64LE(_data, i, totalClaimedFeeXAmount);
    i += 8;
    putInt64LE(_data, i, totalClaimedFeeYAmount);
    i += 8;
    i += SerDeUtil.writeArrayChecked(totalClaimedRewards, 2, _data, i);
    i += SerDeUtil.writeArrayChecked(reserved, 160, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

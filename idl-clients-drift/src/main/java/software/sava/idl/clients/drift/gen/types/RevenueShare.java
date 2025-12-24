package software.sava.idl.clients.drift.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// @param authority the owner of this account, a builder or referrer
public record RevenueShare(PublicKey _address,
                           Discriminator discriminator,
                           PublicKey authority,
                           long totalReferrerRewards,
                           long totalBuilderRewards,
                           byte[] padding) implements SerDe {

  public static final int BYTES = 74;
  public static final int PADDING_LEN = 18;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(55, 40, 228, 7, 139, 52, 180, 110);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int AUTHORITY_OFFSET = 8;
  public static final int TOTAL_REFERRER_REWARDS_OFFSET = 40;
  public static final int TOTAL_BUILDER_REWARDS_OFFSET = 48;
  public static final int PADDING_OFFSET = 56;

  public static Filter createAuthorityFilter(final PublicKey authority) {
    return Filter.createMemCompFilter(AUTHORITY_OFFSET, authority);
  }

  public static Filter createTotalReferrerRewardsFilter(final long totalReferrerRewards) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalReferrerRewards);
    return Filter.createMemCompFilter(TOTAL_REFERRER_REWARDS_OFFSET, _data);
  }

  public static Filter createTotalBuilderRewardsFilter(final long totalBuilderRewards) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalBuilderRewards);
    return Filter.createMemCompFilter(TOTAL_BUILDER_REWARDS_OFFSET, _data);
  }

  public static RevenueShare read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static RevenueShare read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static RevenueShare read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], RevenueShare> FACTORY = RevenueShare::read;

  public static RevenueShare read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var authority = readPubKey(_data, i);
    i += 32;
    final var totalReferrerRewards = getInt64LE(_data, i);
    i += 8;
    final var totalBuilderRewards = getInt64LE(_data, i);
    i += 8;
    final var padding = new byte[18];
    SerDeUtil.readArray(padding, _data, i);
    return new RevenueShare(_address,
                            discriminator,
                            authority,
                            totalReferrerRewards,
                            totalBuilderRewards,
                            padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    authority.write(_data, i);
    i += 32;
    putInt64LE(_data, i, totalReferrerRewards);
    i += 8;
    putInt64LE(_data, i, totalBuilderRewards);
    i += 8;
    i += SerDeUtil.writeArrayChecked(padding, 18, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

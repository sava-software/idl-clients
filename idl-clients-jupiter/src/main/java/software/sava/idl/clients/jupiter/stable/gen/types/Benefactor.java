package software.sava.idl.clients.jupiter.stable.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record Benefactor(PublicKey _address,
                         Discriminator discriminator,
                         PublicKey authority,
                         BenefactorStatus status,
                         byte[] padding0,
                         int mintFeeRate,
                         int redeemFeeRate,
                         byte[] padding1,
                         PeriodLimit[] periodLimits,
                         byte[] totalMinted,
                         byte[] totalRedeemed,
                         FeeOverride[] feeOverrides,
                         byte[] reserved) implements SerDe {

  public static final int BYTES = 536;
  public static final int PADDING_0_LEN = 7;
  public static final int PADDING_1_LEN = 4;
  public static final int PERIOD_LIMITS_LEN = 4;
  public static final int TOTAL_MINTED_LEN = 16;
  public static final int TOTAL_REDEEMED_LEN = 16;
  public static final int FEE_OVERRIDES_LEN = 4;
  public static final int RESERVED_LEN = 96;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(98, 159, 41, 233, 19, 232, 104, 12);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int AUTHORITY_OFFSET = 8;
  public static final int STATUS_OFFSET = 40;
  public static final int PADDING_0_OFFSET = 41;
  public static final int MINT_FEE_RATE_OFFSET = 48;
  public static final int REDEEM_FEE_RATE_OFFSET = 50;
  public static final int PADDING_1_OFFSET = 52;
  public static final int PERIOD_LIMITS_OFFSET = 56;
  public static final int TOTAL_MINTED_OFFSET = 248;
  public static final int TOTAL_REDEEMED_OFFSET = 264;
  public static final int FEE_OVERRIDES_OFFSET = 280;
  public static final int RESERVED_OFFSET = 440;

  public static Filter createAuthorityFilter(final PublicKey authority) {
    return Filter.createMemCompFilter(AUTHORITY_OFFSET, authority);
  }

  public static Filter createStatusFilter(final BenefactorStatus status) {
    return Filter.createMemCompFilter(STATUS_OFFSET, status.write());
  }

  public static Filter createMintFeeRateFilter(final int mintFeeRate) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, mintFeeRate);
    return Filter.createMemCompFilter(MINT_FEE_RATE_OFFSET, _data);
  }

  public static Filter createRedeemFeeRateFilter(final int redeemFeeRate) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, redeemFeeRate);
    return Filter.createMemCompFilter(REDEEM_FEE_RATE_OFFSET, _data);
  }

  public static Benefactor read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Benefactor read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Benefactor read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Benefactor> FACTORY = Benefactor::read;

  public static Benefactor read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var authority = readPubKey(_data, i);
    i += 32;
    final var status = BenefactorStatus.read(_data, i);
    i += status.l();
    final var padding0 = new byte[7];
    i += SerDeUtil.readArray(padding0, _data, i);
    final var mintFeeRate = getInt16LE(_data, i);
    i += 2;
    final var redeemFeeRate = getInt16LE(_data, i);
    i += 2;
    final var padding1 = new byte[4];
    i += SerDeUtil.readArray(padding1, _data, i);
    final var periodLimits = new PeriodLimit[4];
    i += SerDeUtil.readArray(periodLimits, PeriodLimit::read, _data, i);
    final var totalMinted = new byte[16];
    i += SerDeUtil.readArray(totalMinted, _data, i);
    final var totalRedeemed = new byte[16];
    i += SerDeUtil.readArray(totalRedeemed, _data, i);
    final var feeOverrides = new FeeOverride[4];
    i += SerDeUtil.readArray(feeOverrides, FeeOverride::read, _data, i);
    final var reserved = new byte[96];
    SerDeUtil.readArray(reserved, _data, i);
    return new Benefactor(_address,
                          discriminator,
                          authority,
                          status,
                          padding0,
                          mintFeeRate,
                          redeemFeeRate,
                          padding1,
                          periodLimits,
                          totalMinted,
                          totalRedeemed,
                          feeOverrides,
                          reserved);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    authority.write(_data, i);
    i += 32;
    i += status.write(_data, i);
    i += SerDeUtil.writeArrayChecked(padding0, 7, _data, i);
    putInt16LE(_data, i, mintFeeRate);
    i += 2;
    putInt16LE(_data, i, redeemFeeRate);
    i += 2;
    i += SerDeUtil.writeArrayChecked(padding1, 4, _data, i);
    i += SerDeUtil.writeArrayChecked(periodLimits, 4, _data, i);
    i += SerDeUtil.writeArrayChecked(totalMinted, 16, _data, i);
    i += SerDeUtil.writeArrayChecked(totalRedeemed, 16, _data, i);
    i += SerDeUtil.writeArrayChecked(feeOverrides, 4, _data, i);
    i += SerDeUtil.writeArrayChecked(reserved, 96, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

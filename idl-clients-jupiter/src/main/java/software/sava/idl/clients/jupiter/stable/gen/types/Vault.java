package software.sava.idl.clients.jupiter.stable.gen.types;

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

public record Vault(PublicKey _address,
                    Discriminator discriminator,
                    PublicKey mint,
                    PublicKey custodian,
                    PublicKey tokenAccount,
                    PublicKey tokenProgram,
                    long stalesnessThreshold,
                    long minOraclePriceUsd,
                    long maxOraclePriceUsd,
                    VaultStatus status,
                    byte[] padding1,
                    int bump,
                    int decimals,
                    byte[] padding2,
                    OracleType[] oracles,
                    byte[] padding3,
                    PeriodLimit[] periodLimits,
                    byte[] reserved1,
                    byte[] totalMinted,
                    byte[] totalRedeemed,
                    byte[] reserved) implements SerDe {

  public static final int PADDING_1_LEN = 7;
  public static final int PADDING_2_LEN = 6;
  public static final int ORACLES_LEN = 5;
  public static final int PADDING_3_LEN = 3;
  public static final int PERIOD_LIMITS_LEN = 4;
  public static final int RESERVED_1_LEN = 32;
  public static final int TOTAL_MINTED_LEN = 16;
  public static final int TOTAL_REDEEMED_LEN = 16;
  public static final int RESERVED_LEN = 256;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(211, 8, 232, 43, 2, 152, 117, 119);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int MINT_OFFSET = 8;
  public static final int CUSTODIAN_OFFSET = 40;
  public static final int TOKEN_ACCOUNT_OFFSET = 72;
  public static final int TOKEN_PROGRAM_OFFSET = 104;
  public static final int STALESNESS_THRESHOLD_OFFSET = 136;
  public static final int MIN_ORACLE_PRICE_USD_OFFSET = 144;
  public static final int MAX_ORACLE_PRICE_USD_OFFSET = 152;
  public static final int STATUS_OFFSET = 160;
  public static final int PADDING_1_OFFSET = 161;
  public static final int BUMP_OFFSET = 168;
  public static final int DECIMALS_OFFSET = 169;
  public static final int PADDING_2_OFFSET = 170;
  public static final int ORACLES_OFFSET = 176;

  public static Filter createMintFilter(final PublicKey mint) {
    return Filter.createMemCompFilter(MINT_OFFSET, mint);
  }

  public static Filter createCustodianFilter(final PublicKey custodian) {
    return Filter.createMemCompFilter(CUSTODIAN_OFFSET, custodian);
  }

  public static Filter createTokenAccountFilter(final PublicKey tokenAccount) {
    return Filter.createMemCompFilter(TOKEN_ACCOUNT_OFFSET, tokenAccount);
  }

  public static Filter createTokenProgramFilter(final PublicKey tokenProgram) {
    return Filter.createMemCompFilter(TOKEN_PROGRAM_OFFSET, tokenProgram);
  }

  public static Filter createStalesnessThresholdFilter(final long stalesnessThreshold) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, stalesnessThreshold);
    return Filter.createMemCompFilter(STALESNESS_THRESHOLD_OFFSET, _data);
  }

  public static Filter createMinOraclePriceUsdFilter(final long minOraclePriceUsd) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, minOraclePriceUsd);
    return Filter.createMemCompFilter(MIN_ORACLE_PRICE_USD_OFFSET, _data);
  }

  public static Filter createMaxOraclePriceUsdFilter(final long maxOraclePriceUsd) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, maxOraclePriceUsd);
    return Filter.createMemCompFilter(MAX_ORACLE_PRICE_USD_OFFSET, _data);
  }

  public static Filter createStatusFilter(final VaultStatus status) {
    return Filter.createMemCompFilter(STATUS_OFFSET, status.write());
  }

  public static Filter createBumpFilter(final int bump) {
    return Filter.createMemCompFilter(BUMP_OFFSET, new byte[]{(byte) bump});
  }

  public static Filter createDecimalsFilter(final int decimals) {
    return Filter.createMemCompFilter(DECIMALS_OFFSET, new byte[]{(byte) decimals});
  }

  public static Vault read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Vault read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Vault read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Vault> FACTORY = Vault::read;

  public static Vault read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var mint = readPubKey(_data, i);
    i += 32;
    final var custodian = readPubKey(_data, i);
    i += 32;
    final var tokenAccount = readPubKey(_data, i);
    i += 32;
    final var tokenProgram = readPubKey(_data, i);
    i += 32;
    final var stalesnessThreshold = getInt64LE(_data, i);
    i += 8;
    final var minOraclePriceUsd = getInt64LE(_data, i);
    i += 8;
    final var maxOraclePriceUsd = getInt64LE(_data, i);
    i += 8;
    final var status = VaultStatus.read(_data, i);
    i += status.l();
    final var padding1 = new byte[7];
    i += SerDeUtil.readArray(padding1, _data, i);
    final var bump = _data[i] & 0xFF;
    ++i;
    final var decimals = _data[i] & 0xFF;
    ++i;
    final var padding2 = new byte[6];
    i += SerDeUtil.readArray(padding2, _data, i);
    final var oracles = new OracleType[5];
    i += SerDeUtil.readArray(oracles, OracleType::read, _data, i);
    final var padding3 = new byte[3];
    i += SerDeUtil.readArray(padding3, _data, i);
    final var periodLimits = new PeriodLimit[4];
    i += SerDeUtil.readArray(periodLimits, PeriodLimit::read, _data, i);
    final var reserved1 = new byte[32];
    i += SerDeUtil.readArray(reserved1, _data, i);
    final var totalMinted = new byte[16];
    i += SerDeUtil.readArray(totalMinted, _data, i);
    final var totalRedeemed = new byte[16];
    i += SerDeUtil.readArray(totalRedeemed, _data, i);
    final var reserved = new byte[256];
    SerDeUtil.readArray(reserved, _data, i);
    return new Vault(_address,
                     discriminator,
                     mint,
                     custodian,
                     tokenAccount,
                     tokenProgram,
                     stalesnessThreshold,
                     minOraclePriceUsd,
                     maxOraclePriceUsd,
                     status,
                     padding1,
                     bump,
                     decimals,
                     padding2,
                     oracles,
                     padding3,
                     periodLimits,
                     reserved1,
                     totalMinted,
                     totalRedeemed,
                     reserved);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    mint.write(_data, i);
    i += 32;
    custodian.write(_data, i);
    i += 32;
    tokenAccount.write(_data, i);
    i += 32;
    tokenProgram.write(_data, i);
    i += 32;
    putInt64LE(_data, i, stalesnessThreshold);
    i += 8;
    putInt64LE(_data, i, minOraclePriceUsd);
    i += 8;
    putInt64LE(_data, i, maxOraclePriceUsd);
    i += 8;
    i += status.write(_data, i);
    i += SerDeUtil.writeArrayChecked(padding1, 7, _data, i);
    _data[i] = (byte) bump;
    ++i;
    _data[i] = (byte) decimals;
    ++i;
    i += SerDeUtil.writeArrayChecked(padding2, 6, _data, i);
    i += SerDeUtil.writeArrayChecked(oracles, 5, _data, i);
    i += SerDeUtil.writeArrayChecked(padding3, 3, _data, i);
    i += SerDeUtil.writeArrayChecked(periodLimits, 4, _data, i);
    i += SerDeUtil.writeArrayChecked(reserved1, 32, _data, i);
    i += SerDeUtil.writeArrayChecked(totalMinted, 16, _data, i);
    i += SerDeUtil.writeArrayChecked(totalRedeemed, 16, _data, i);
    i += SerDeUtil.writeArrayChecked(reserved, 256, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32
         + 32
         + 32
         + 32
         + 8
         + 8
         + 8
         + status.l()
         + SerDeUtil.lenArray(padding1)
         + 1
         + 1
         + SerDeUtil.lenArray(padding2)
         + SerDeUtil.lenArray(oracles)
         + SerDeUtil.lenArray(padding3)
         + SerDeUtil.lenArray(periodLimits)
         + SerDeUtil.lenArray(reserved1)
         + SerDeUtil.lenArray(totalMinted)
         + SerDeUtil.lenArray(totalRedeemed)
         + SerDeUtil.lenArray(reserved);
  }
}

package software.sava.idl.clients.jupiter.lend_borrow.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// @param decimals @dev number of decimals for the fToken, same as ASSET
/// @param rewardsRateModel @dev To read PDA of rewards rate model to get_rate instruction
/// @param liquidityExchangePrice @dev exchange price for the underlying asset in the liquidity protocol (without rewards)
/// @param tokenExchangePrice @dev exchange price between fToken and the underlying asset (with rewards)
/// @param lastUpdateTimestamp @dev timestamp when exchange prices were updated the last time
public record Lending(PublicKey _address,
                      Discriminator discriminator,
                      PublicKey mint,
                      PublicKey fTokenMint,
                      int lendingId,
                      int decimals,
                      PublicKey rewardsRateModel,
                      long liquidityExchangePrice,
                      long tokenExchangePrice,
                      long lastUpdateTimestamp,
                      PublicKey tokenReservesLiquidity,
                      PublicKey supplyPositionOnLiquidity,
                      int bump) implements Borsh {

  public static final int BYTES = 196;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(135, 199, 82, 16, 249, 131, 182, 241);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int MINT_OFFSET = 8;
  public static final int F_TOKEN_MINT_OFFSET = 40;
  public static final int LENDING_ID_OFFSET = 72;
  public static final int DECIMALS_OFFSET = 74;
  public static final int REWARDS_RATE_MODEL_OFFSET = 75;
  public static final int LIQUIDITY_EXCHANGE_PRICE_OFFSET = 107;
  public static final int TOKEN_EXCHANGE_PRICE_OFFSET = 115;
  public static final int LAST_UPDATE_TIMESTAMP_OFFSET = 123;
  public static final int TOKEN_RESERVES_LIQUIDITY_OFFSET = 131;
  public static final int SUPPLY_POSITION_ON_LIQUIDITY_OFFSET = 163;
  public static final int BUMP_OFFSET = 195;

  public static Filter createMintFilter(final PublicKey mint) {
    return Filter.createMemCompFilter(MINT_OFFSET, mint);
  }

  public static Filter createFTokenMintFilter(final PublicKey fTokenMint) {
    return Filter.createMemCompFilter(F_TOKEN_MINT_OFFSET, fTokenMint);
  }

  public static Filter createLendingIdFilter(final int lendingId) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, lendingId);
    return Filter.createMemCompFilter(LENDING_ID_OFFSET, _data);
  }

  public static Filter createDecimalsFilter(final int decimals) {
    return Filter.createMemCompFilter(DECIMALS_OFFSET, new byte[]{(byte) decimals});
  }

  public static Filter createRewardsRateModelFilter(final PublicKey rewardsRateModel) {
    return Filter.createMemCompFilter(REWARDS_RATE_MODEL_OFFSET, rewardsRateModel);
  }

  public static Filter createLiquidityExchangePriceFilter(final long liquidityExchangePrice) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, liquidityExchangePrice);
    return Filter.createMemCompFilter(LIQUIDITY_EXCHANGE_PRICE_OFFSET, _data);
  }

  public static Filter createTokenExchangePriceFilter(final long tokenExchangePrice) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, tokenExchangePrice);
    return Filter.createMemCompFilter(TOKEN_EXCHANGE_PRICE_OFFSET, _data);
  }

  public static Filter createLastUpdateTimestampFilter(final long lastUpdateTimestamp) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, lastUpdateTimestamp);
    return Filter.createMemCompFilter(LAST_UPDATE_TIMESTAMP_OFFSET, _data);
  }

  public static Filter createTokenReservesLiquidityFilter(final PublicKey tokenReservesLiquidity) {
    return Filter.createMemCompFilter(TOKEN_RESERVES_LIQUIDITY_OFFSET, tokenReservesLiquidity);
  }

  public static Filter createSupplyPositionOnLiquidityFilter(final PublicKey supplyPositionOnLiquidity) {
    return Filter.createMemCompFilter(SUPPLY_POSITION_ON_LIQUIDITY_OFFSET, supplyPositionOnLiquidity);
  }

  public static Filter createBumpFilter(final int bump) {
    return Filter.createMemCompFilter(BUMP_OFFSET, new byte[]{(byte) bump});
  }

  public static Lending read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Lending read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Lending read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Lending> FACTORY = Lending::read;

  public static Lending read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var mint = readPubKey(_data, i);
    i += 32;
    final var fTokenMint = readPubKey(_data, i);
    i += 32;
    final var lendingId = getInt16LE(_data, i);
    i += 2;
    final var decimals = _data[i] & 0xFF;
    ++i;
    final var rewardsRateModel = readPubKey(_data, i);
    i += 32;
    final var liquidityExchangePrice = getInt64LE(_data, i);
    i += 8;
    final var tokenExchangePrice = getInt64LE(_data, i);
    i += 8;
    final var lastUpdateTimestamp = getInt64LE(_data, i);
    i += 8;
    final var tokenReservesLiquidity = readPubKey(_data, i);
    i += 32;
    final var supplyPositionOnLiquidity = readPubKey(_data, i);
    i += 32;
    final var bump = _data[i] & 0xFF;
    return new Lending(_address,
                       discriminator,
                       mint,
                       fTokenMint,
                       lendingId,
                       decimals,
                       rewardsRateModel,
                       liquidityExchangePrice,
                       tokenExchangePrice,
                       lastUpdateTimestamp,
                       tokenReservesLiquidity,
                       supplyPositionOnLiquidity,
                       bump);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    mint.write(_data, i);
    i += 32;
    fTokenMint.write(_data, i);
    i += 32;
    putInt16LE(_data, i, lendingId);
    i += 2;
    _data[i] = (byte) decimals;
    ++i;
    rewardsRateModel.write(_data, i);
    i += 32;
    putInt64LE(_data, i, liquidityExchangePrice);
    i += 8;
    putInt64LE(_data, i, tokenExchangePrice);
    i += 8;
    putInt64LE(_data, i, lastUpdateTimestamp);
    i += 8;
    tokenReservesLiquidity.write(_data, i);
    i += 32;
    supplyPositionOnLiquidity.write(_data, i);
    i += 32;
    _data[i] = (byte) bump;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

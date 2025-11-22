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

/// Token configuration and exchange prices
///
public record TokenReserve(PublicKey _address,
                           Discriminator discriminator,
                           PublicKey mint,
                           PublicKey vault,
                           int borrowRate,
                           int feeOnInterest,
                           int lastUtilization,
                           long lastUpdateTimestamp,
                           long supplyExchangePrice,
                           long borrowExchangePrice,
                           int maxUtilization,
                           long totalSupplyWithInterest,
                           long totalSupplyInterestFree,
                           long totalBorrowWithInterest,
                           long totalBorrowInterestFree,
                           long totalClaimAmount,
                           PublicKey interactingProtocol,
                           long interactingTimestamp,
                           long interactingBalance) implements Borsh {

  public static final int BYTES = 192;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(21, 18, 59, 135, 120, 20, 31, 12);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int MINT_OFFSET = 8;
  public static final int VAULT_OFFSET = 40;
  public static final int BORROW_RATE_OFFSET = 72;
  public static final int FEE_ON_INTEREST_OFFSET = 74;
  public static final int LAST_UTILIZATION_OFFSET = 76;
  public static final int LAST_UPDATE_TIMESTAMP_OFFSET = 78;
  public static final int SUPPLY_EXCHANGE_PRICE_OFFSET = 86;
  public static final int BORROW_EXCHANGE_PRICE_OFFSET = 94;
  public static final int MAX_UTILIZATION_OFFSET = 102;
  public static final int TOTAL_SUPPLY_WITH_INTEREST_OFFSET = 104;
  public static final int TOTAL_SUPPLY_INTEREST_FREE_OFFSET = 112;
  public static final int TOTAL_BORROW_WITH_INTEREST_OFFSET = 120;
  public static final int TOTAL_BORROW_INTEREST_FREE_OFFSET = 128;
  public static final int TOTAL_CLAIM_AMOUNT_OFFSET = 136;
  public static final int INTERACTING_PROTOCOL_OFFSET = 144;
  public static final int INTERACTING_TIMESTAMP_OFFSET = 176;
  public static final int INTERACTING_BALANCE_OFFSET = 184;

  public static Filter createMintFilter(final PublicKey mint) {
    return Filter.createMemCompFilter(MINT_OFFSET, mint);
  }

  public static Filter createVaultFilter(final PublicKey vault) {
    return Filter.createMemCompFilter(VAULT_OFFSET, vault);
  }

  public static Filter createBorrowRateFilter(final int borrowRate) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, borrowRate);
    return Filter.createMemCompFilter(BORROW_RATE_OFFSET, _data);
  }

  public static Filter createFeeOnInterestFilter(final int feeOnInterest) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, feeOnInterest);
    return Filter.createMemCompFilter(FEE_ON_INTEREST_OFFSET, _data);
  }

  public static Filter createLastUtilizationFilter(final int lastUtilization) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, lastUtilization);
    return Filter.createMemCompFilter(LAST_UTILIZATION_OFFSET, _data);
  }

  public static Filter createLastUpdateTimestampFilter(final long lastUpdateTimestamp) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, lastUpdateTimestamp);
    return Filter.createMemCompFilter(LAST_UPDATE_TIMESTAMP_OFFSET, _data);
  }

  public static Filter createSupplyExchangePriceFilter(final long supplyExchangePrice) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, supplyExchangePrice);
    return Filter.createMemCompFilter(SUPPLY_EXCHANGE_PRICE_OFFSET, _data);
  }

  public static Filter createBorrowExchangePriceFilter(final long borrowExchangePrice) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, borrowExchangePrice);
    return Filter.createMemCompFilter(BORROW_EXCHANGE_PRICE_OFFSET, _data);
  }

  public static Filter createMaxUtilizationFilter(final int maxUtilization) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, maxUtilization);
    return Filter.createMemCompFilter(MAX_UTILIZATION_OFFSET, _data);
  }

  public static Filter createTotalSupplyWithInterestFilter(final long totalSupplyWithInterest) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalSupplyWithInterest);
    return Filter.createMemCompFilter(TOTAL_SUPPLY_WITH_INTEREST_OFFSET, _data);
  }

  public static Filter createTotalSupplyInterestFreeFilter(final long totalSupplyInterestFree) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalSupplyInterestFree);
    return Filter.createMemCompFilter(TOTAL_SUPPLY_INTEREST_FREE_OFFSET, _data);
  }

  public static Filter createTotalBorrowWithInterestFilter(final long totalBorrowWithInterest) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalBorrowWithInterest);
    return Filter.createMemCompFilter(TOTAL_BORROW_WITH_INTEREST_OFFSET, _data);
  }

  public static Filter createTotalBorrowInterestFreeFilter(final long totalBorrowInterestFree) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalBorrowInterestFree);
    return Filter.createMemCompFilter(TOTAL_BORROW_INTEREST_FREE_OFFSET, _data);
  }

  public static Filter createTotalClaimAmountFilter(final long totalClaimAmount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalClaimAmount);
    return Filter.createMemCompFilter(TOTAL_CLAIM_AMOUNT_OFFSET, _data);
  }

  public static Filter createInteractingProtocolFilter(final PublicKey interactingProtocol) {
    return Filter.createMemCompFilter(INTERACTING_PROTOCOL_OFFSET, interactingProtocol);
  }

  public static Filter createInteractingTimestampFilter(final long interactingTimestamp) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, interactingTimestamp);
    return Filter.createMemCompFilter(INTERACTING_TIMESTAMP_OFFSET, _data);
  }

  public static Filter createInteractingBalanceFilter(final long interactingBalance) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, interactingBalance);
    return Filter.createMemCompFilter(INTERACTING_BALANCE_OFFSET, _data);
  }

  public static TokenReserve read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static TokenReserve read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static TokenReserve read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], TokenReserve> FACTORY = TokenReserve::read;

  public static TokenReserve read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var mint = readPubKey(_data, i);
    i += 32;
    final var vault = readPubKey(_data, i);
    i += 32;
    final var borrowRate = getInt16LE(_data, i);
    i += 2;
    final var feeOnInterest = getInt16LE(_data, i);
    i += 2;
    final var lastUtilization = getInt16LE(_data, i);
    i += 2;
    final var lastUpdateTimestamp = getInt64LE(_data, i);
    i += 8;
    final var supplyExchangePrice = getInt64LE(_data, i);
    i += 8;
    final var borrowExchangePrice = getInt64LE(_data, i);
    i += 8;
    final var maxUtilization = getInt16LE(_data, i);
    i += 2;
    final var totalSupplyWithInterest = getInt64LE(_data, i);
    i += 8;
    final var totalSupplyInterestFree = getInt64LE(_data, i);
    i += 8;
    final var totalBorrowWithInterest = getInt64LE(_data, i);
    i += 8;
    final var totalBorrowInterestFree = getInt64LE(_data, i);
    i += 8;
    final var totalClaimAmount = getInt64LE(_data, i);
    i += 8;
    final var interactingProtocol = readPubKey(_data, i);
    i += 32;
    final var interactingTimestamp = getInt64LE(_data, i);
    i += 8;
    final var interactingBalance = getInt64LE(_data, i);
    return new TokenReserve(_address,
                            discriminator,
                            mint,
                            vault,
                            borrowRate,
                            feeOnInterest,
                            lastUtilization,
                            lastUpdateTimestamp,
                            supplyExchangePrice,
                            borrowExchangePrice,
                            maxUtilization,
                            totalSupplyWithInterest,
                            totalSupplyInterestFree,
                            totalBorrowWithInterest,
                            totalBorrowInterestFree,
                            totalClaimAmount,
                            interactingProtocol,
                            interactingTimestamp,
                            interactingBalance);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    mint.write(_data, i);
    i += 32;
    vault.write(_data, i);
    i += 32;
    putInt16LE(_data, i, borrowRate);
    i += 2;
    putInt16LE(_data, i, feeOnInterest);
    i += 2;
    putInt16LE(_data, i, lastUtilization);
    i += 2;
    putInt64LE(_data, i, lastUpdateTimestamp);
    i += 8;
    putInt64LE(_data, i, supplyExchangePrice);
    i += 8;
    putInt64LE(_data, i, borrowExchangePrice);
    i += 8;
    putInt16LE(_data, i, maxUtilization);
    i += 2;
    putInt64LE(_data, i, totalSupplyWithInterest);
    i += 8;
    putInt64LE(_data, i, totalSupplyInterestFree);
    i += 8;
    putInt64LE(_data, i, totalBorrowWithInterest);
    i += 8;
    putInt64LE(_data, i, totalBorrowInterestFree);
    i += 8;
    putInt64LE(_data, i, totalClaimAmount);
    i += 8;
    interactingProtocol.write(_data, i);
    i += 32;
    putInt64LE(_data, i, interactingTimestamp);
    i += 8;
    putInt64LE(_data, i, interactingBalance);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

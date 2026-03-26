package software.sava.idl.clients.kamino.lend.gen.types;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Obligation liquidity state
///
/// @param borrowReserve Reserve liquidity is borrowed from
/// @param cumulativeBorrowRateBsf Borrow rate used for calculating interest (big scaled fraction)
/// @param lastBorrowedAtTimestamp The timestamp at which this debt was taken.
///                                
///                                Conceptually, every borrow can be interpreted as "closing the previous loan and starting a
///                                new one" (which would make a plain ` borrowed_at ` an even better name). But in terms of
///                                implementation, this fields records when the *last* borrow operation from this reserve
///                                happened (i.e. adding debt of the same reserve *does* move this timestamp).
///                                
///                                Note: this field is *not* only metadata: it is used in the logic, e.g. for enforcing the
///                                fixed-term borrows (i.e. those induced by ReserveConfig::debt_term_seconds).
/// @param borrowedAmountSf Amount of liquidity borrowed plus interest (scaled fraction)
/// @param marketValueSf Liquidity market value in quote currency (scaled fraction)
/// @param borrowFactorAdjustedMarketValueSf Risk adjusted liquidity market value in quote currency - DEBUG ONLY - use market_value instead
/// @param borrowedAmountOutsideElevationGroups Amount of liquidity borrowed outside of an elevation group
/// @param fixedTermBorrowRolloverConfig The user's auto-rollover/migration opt-ins. Some settings are effective only for fixed-term
///                                      borrows, while others only for open-term borrows - see individual field docs.
/// @param borrowedAmountAtExpiration An amount of liquidity that was borrowed when this fixed-term borrow expired (i.e. zeroed if
///                                   this borrow is not fixed-term, or if it did not yet expire).
///                                   
///                                   Needed to honor the LendingMarket::term_based_full_liquidation_duration_secs.
///                                   
///                                   This value is captured by Self::capture_borrowed_amount_at_expiration during obligation's
///                                   refresh - please see the method's docs for gotchas.
///                                   
///                                   Note on precision: we use a `u64` field, since the remaining space within this struct is
///                                   rather scarce, and we do not need sub-lamport precision for the liquidation throttling rate.
public record ObligationLiquidity(PublicKey borrowReserve,
                                  BigFractionBytes cumulativeBorrowRateBsf,
                                  long lastBorrowedAtTimestamp,
                                  BigInteger borrowedAmountSf,
                                  BigInteger marketValueSf,
                                  BigInteger borrowFactorAdjustedMarketValueSf,
                                  long borrowedAmountOutsideElevationGroups,
                                  FixedTermBorrowRolloverConfig fixedTermBorrowRolloverConfig,
                                  long borrowedAmountAtExpiration,
                                  long[] padding2) implements SerDe {

  public static final int BYTES = 200;
  public static final int PADDING_2_LEN = 4;

  public static final int BORROW_RESERVE_OFFSET = 0;
  public static final int CUMULATIVE_BORROW_RATE_BSF_OFFSET = 32;
  public static final int LAST_BORROWED_AT_TIMESTAMP_OFFSET = 80;
  public static final int BORROWED_AMOUNT_SF_OFFSET = 88;
  public static final int MARKET_VALUE_SF_OFFSET = 104;
  public static final int BORROW_FACTOR_ADJUSTED_MARKET_VALUE_SF_OFFSET = 120;
  public static final int BORROWED_AMOUNT_OUTSIDE_ELEVATION_GROUPS_OFFSET = 136;
  public static final int FIXED_TERM_BORROW_ROLLOVER_CONFIG_OFFSET = 144;
  public static final int BORROWED_AMOUNT_AT_EXPIRATION_OFFSET = 160;
  public static final int PADDING_2_OFFSET = 168;

  public static ObligationLiquidity read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var borrowReserve = readPubKey(_data, i);
    i += 32;
    final var cumulativeBorrowRateBsf = BigFractionBytes.read(_data, i);
    i += cumulativeBorrowRateBsf.l();
    final var lastBorrowedAtTimestamp = getInt64LE(_data, i);
    i += 8;
    final var borrowedAmountSf = getInt128LE(_data, i);
    i += 16;
    final var marketValueSf = getInt128LE(_data, i);
    i += 16;
    final var borrowFactorAdjustedMarketValueSf = getInt128LE(_data, i);
    i += 16;
    final var borrowedAmountOutsideElevationGroups = getInt64LE(_data, i);
    i += 8;
    final var fixedTermBorrowRolloverConfig = FixedTermBorrowRolloverConfig.read(_data, i);
    i += fixedTermBorrowRolloverConfig.l();
    final var borrowedAmountAtExpiration = getInt64LE(_data, i);
    i += 8;
    final var padding2 = new long[4];
    SerDeUtil.readArray(padding2, _data, i);
    return new ObligationLiquidity(borrowReserve,
                                   cumulativeBorrowRateBsf,
                                   lastBorrowedAtTimestamp,
                                   borrowedAmountSf,
                                   marketValueSf,
                                   borrowFactorAdjustedMarketValueSf,
                                   borrowedAmountOutsideElevationGroups,
                                   fixedTermBorrowRolloverConfig,
                                   borrowedAmountAtExpiration,
                                   padding2);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    borrowReserve.write(_data, i);
    i += 32;
    i += cumulativeBorrowRateBsf.write(_data, i);
    putInt64LE(_data, i, lastBorrowedAtTimestamp);
    i += 8;
    putInt128LE(_data, i, borrowedAmountSf);
    i += 16;
    putInt128LE(_data, i, marketValueSf);
    i += 16;
    putInt128LE(_data, i, borrowFactorAdjustedMarketValueSf);
    i += 16;
    putInt64LE(_data, i, borrowedAmountOutsideElevationGroups);
    i += 8;
    i += fixedTermBorrowRolloverConfig.write(_data, i);
    putInt64LE(_data, i, borrowedAmountAtExpiration);
    i += 8;
    i += SerDeUtil.writeArrayChecked(padding2, 4, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

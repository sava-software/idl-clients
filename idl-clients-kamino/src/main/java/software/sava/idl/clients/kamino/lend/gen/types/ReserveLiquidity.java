package software.sava.idl.clients.kamino.lend.gen.types;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Reserve liquidity
///
/// @param mintPubkey Reserve liquidity mint address
/// @param supplyVault Reserve liquidity supply address
/// @param feeVault Reserve liquidity fee collection address
/// @param availableAmount Reserve liquidity available
/// @param borrowedAmountSf Reserve liquidity borrowed (scaled fraction)
/// @param marketPriceSf Reserve liquidity market price in quote currency (scaled fraction)
/// @param marketPriceLastUpdatedTs Unix timestamp of the market price (from the oracle)
/// @param mintDecimals Reserve liquidity mint decimals
/// @param depositLimitCrossedTimestamp Timestamp when the last refresh reserve detected that the liquidity amount is above the deposit cap. When this threshold is crossed, then redemptions (auto-deleverage) are enabled.
///                                     If the threshold is not crossed, then the timestamp is set to 0
/// @param borrowLimitCrossedTimestamp Timestamp when the last refresh reserve detected that the borrowed amount is above the borrow cap. When this threshold is crossed, then redemptions (auto-deleverage) are enabled.
///                                    If the threshold is not crossed, then the timestamp is set to 0
/// @param cumulativeBorrowRateBsf Reserve liquidity cumulative borrow rate (scaled fraction)
/// @param accumulatedProtocolFeesSf Reserve cumulative protocol fees (scaled fraction)
/// @param accumulatedReferrerFeesSf Reserve cumulative referrer fees (scaled fraction)
/// @param pendingReferrerFeesSf Reserve pending referrer fees, to be claimed in refresh_obligation by referrer or protocol (scaled fraction)
/// @param absoluteReferralRateSf Reserve referrer fee absolute rate calculated at each refresh_reserve operation (scaled fraction)
/// @param tokenProgram Token program of the liquidity mint
public record ReserveLiquidity(PublicKey mintPubkey,
                               PublicKey supplyVault,
                               PublicKey feeVault,
                               long availableAmount,
                               BigInteger borrowedAmountSf,
                               BigInteger marketPriceSf,
                               long marketPriceLastUpdatedTs,
                               long mintDecimals,
                               long depositLimitCrossedTimestamp,
                               long borrowLimitCrossedTimestamp,
                               BigFractionBytes cumulativeBorrowRateBsf,
                               BigInteger accumulatedProtocolFeesSf,
                               BigInteger accumulatedReferrerFeesSf,
                               BigInteger pendingReferrerFeesSf,
                               BigInteger absoluteReferralRateSf,
                               PublicKey tokenProgram,
                               long[] padding2,
                               BigInteger[] padding3) implements Borsh {

  public static final int BYTES = 1232;
  public static final int PADDING_2_LEN = 51;
  public static final int PADDING_3_LEN = 32;

  public static ReserveLiquidity read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var mintPubkey = readPubKey(_data, i);
    i += 32;
    final var supplyVault = readPubKey(_data, i);
    i += 32;
    final var feeVault = readPubKey(_data, i);
    i += 32;
    final var availableAmount = getInt64LE(_data, i);
    i += 8;
    final var borrowedAmountSf = getInt128LE(_data, i);
    i += 16;
    final var marketPriceSf = getInt128LE(_data, i);
    i += 16;
    final var marketPriceLastUpdatedTs = getInt64LE(_data, i);
    i += 8;
    final var mintDecimals = getInt64LE(_data, i);
    i += 8;
    final var depositLimitCrossedTimestamp = getInt64LE(_data, i);
    i += 8;
    final var borrowLimitCrossedTimestamp = getInt64LE(_data, i);
    i += 8;
    final var cumulativeBorrowRateBsf = BigFractionBytes.read(_data, i);
    i += Borsh.len(cumulativeBorrowRateBsf);
    final var accumulatedProtocolFeesSf = getInt128LE(_data, i);
    i += 16;
    final var accumulatedReferrerFeesSf = getInt128LE(_data, i);
    i += 16;
    final var pendingReferrerFeesSf = getInt128LE(_data, i);
    i += 16;
    final var absoluteReferralRateSf = getInt128LE(_data, i);
    i += 16;
    final var tokenProgram = readPubKey(_data, i);
    i += 32;
    final var padding2 = new long[51];
    i += Borsh.readArray(padding2, _data, i);
    final var padding3 = new BigInteger[32];
    Borsh.read128Array(padding3, _data, i);
    return new ReserveLiquidity(mintPubkey,
                                supplyVault,
                                feeVault,
                                availableAmount,
                                borrowedAmountSf,
                                marketPriceSf,
                                marketPriceLastUpdatedTs,
                                mintDecimals,
                                depositLimitCrossedTimestamp,
                                borrowLimitCrossedTimestamp,
                                cumulativeBorrowRateBsf,
                                accumulatedProtocolFeesSf,
                                accumulatedReferrerFeesSf,
                                pendingReferrerFeesSf,
                                absoluteReferralRateSf,
                                tokenProgram,
                                padding2,
                                padding3);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    mintPubkey.write(_data, i);
    i += 32;
    supplyVault.write(_data, i);
    i += 32;
    feeVault.write(_data, i);
    i += 32;
    putInt64LE(_data, i, availableAmount);
    i += 8;
    putInt128LE(_data, i, borrowedAmountSf);
    i += 16;
    putInt128LE(_data, i, marketPriceSf);
    i += 16;
    putInt64LE(_data, i, marketPriceLastUpdatedTs);
    i += 8;
    putInt64LE(_data, i, mintDecimals);
    i += 8;
    putInt64LE(_data, i, depositLimitCrossedTimestamp);
    i += 8;
    putInt64LE(_data, i, borrowLimitCrossedTimestamp);
    i += 8;
    i += Borsh.write(cumulativeBorrowRateBsf, _data, i);
    putInt128LE(_data, i, accumulatedProtocolFeesSf);
    i += 16;
    putInt128LE(_data, i, accumulatedReferrerFeesSf);
    i += 16;
    putInt128LE(_data, i, pendingReferrerFeesSf);
    i += 16;
    putInt128LE(_data, i, absoluteReferralRateSf);
    i += 16;
    tokenProgram.write(_data, i);
    i += 32;
    i += Borsh.writeArrayChecked(padding2, 51, _data, i);
    i += Borsh.write128ArrayChecked(padding3, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

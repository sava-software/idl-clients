package software.sava.idl.clients.kamino.vaults.gen.types;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.kamino.lend.gen.types.BigFractionBytes;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

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
                               BigInteger[] padding3) implements SerDe {

  public static final int BYTES = 1232;
  public static final int PADDING_2_LEN = 51;
  public static final int PADDING_3_LEN = 32;

  public static final int MINT_PUBKEY_OFFSET = 0;
  public static final int SUPPLY_VAULT_OFFSET = 32;
  public static final int FEE_VAULT_OFFSET = 64;
  public static final int AVAILABLE_AMOUNT_OFFSET = 96;
  public static final int BORROWED_AMOUNT_SF_OFFSET = 104;
  public static final int MARKET_PRICE_SF_OFFSET = 120;
  public static final int MARKET_PRICE_LAST_UPDATED_TS_OFFSET = 136;
  public static final int MINT_DECIMALS_OFFSET = 144;
  public static final int DEPOSIT_LIMIT_CROSSED_TIMESTAMP_OFFSET = 152;
  public static final int BORROW_LIMIT_CROSSED_TIMESTAMP_OFFSET = 160;
  public static final int CUMULATIVE_BORROW_RATE_BSF_OFFSET = 168;
  public static final int ACCUMULATED_PROTOCOL_FEES_SF_OFFSET = 216;
  public static final int ACCUMULATED_REFERRER_FEES_SF_OFFSET = 232;
  public static final int PENDING_REFERRER_FEES_SF_OFFSET = 248;
  public static final int ABSOLUTE_REFERRAL_RATE_SF_OFFSET = 264;
  public static final int TOKEN_PROGRAM_OFFSET = 280;
  public static final int PADDING_2_OFFSET = 312;
  public static final int PADDING_3_OFFSET = 720;

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
    i += cumulativeBorrowRateBsf.l();
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
    i += SerDeUtil.readArray(padding2, _data, i);
    final var padding3 = new BigInteger[32];
    SerDeUtil.read128Array(padding3, _data, i);
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
    i += cumulativeBorrowRateBsf.write(_data, i);
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
    i += SerDeUtil.writeArrayChecked(padding2, 51, _data, i);
    i += SerDeUtil.write128ArrayChecked(padding3, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

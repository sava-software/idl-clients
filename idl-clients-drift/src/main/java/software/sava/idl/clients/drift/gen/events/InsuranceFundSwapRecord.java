package software.sava.idl.clients.drift.gen.events;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record InsuranceFundSwapRecord(Discriminator discriminator,
                                      PublicKey rebalanceConfig,
                                      BigInteger inIfTotalSharesBefore,
                                      BigInteger outIfTotalSharesBefore,
                                      BigInteger inIfUserSharesBefore,
                                      BigInteger outIfUserSharesBefore,
                                      BigInteger inIfTotalSharesAfter,
                                      BigInteger outIfTotalSharesAfter,
                                      BigInteger inIfUserSharesAfter,
                                      BigInteger outIfUserSharesAfter,
                                      long ts,
                                      long inAmount,
                                      long outAmount,
                                      long outOraclePrice,
                                      long outOraclePriceTwap,
                                      long inVaultAmountBefore,
                                      long outVaultAmountBefore,
                                      long inFundVaultAmountAfter,
                                      long outFundVaultAmountAfter,
                                      int inMarketIndex,
                                      int outMarketIndex) implements DriftEvent {

  public static final int BYTES = 244;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static final int REBALANCE_CONFIG_OFFSET = 8;
  public static final int IN_IF_TOTAL_SHARES_BEFORE_OFFSET = 40;
  public static final int OUT_IF_TOTAL_SHARES_BEFORE_OFFSET = 56;
  public static final int IN_IF_USER_SHARES_BEFORE_OFFSET = 72;
  public static final int OUT_IF_USER_SHARES_BEFORE_OFFSET = 88;
  public static final int IN_IF_TOTAL_SHARES_AFTER_OFFSET = 104;
  public static final int OUT_IF_TOTAL_SHARES_AFTER_OFFSET = 120;
  public static final int IN_IF_USER_SHARES_AFTER_OFFSET = 136;
  public static final int OUT_IF_USER_SHARES_AFTER_OFFSET = 152;
  public static final int TS_OFFSET = 168;
  public static final int IN_AMOUNT_OFFSET = 176;
  public static final int OUT_AMOUNT_OFFSET = 184;
  public static final int OUT_ORACLE_PRICE_OFFSET = 192;
  public static final int OUT_ORACLE_PRICE_TWAP_OFFSET = 200;
  public static final int IN_VAULT_AMOUNT_BEFORE_OFFSET = 208;
  public static final int OUT_VAULT_AMOUNT_BEFORE_OFFSET = 216;
  public static final int IN_FUND_VAULT_AMOUNT_AFTER_OFFSET = 224;
  public static final int OUT_FUND_VAULT_AMOUNT_AFTER_OFFSET = 232;
  public static final int IN_MARKET_INDEX_OFFSET = 240;
  public static final int OUT_MARKET_INDEX_OFFSET = 242;

  public static InsuranceFundSwapRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var rebalanceConfig = readPubKey(_data, i);
    i += 32;
    final var inIfTotalSharesBefore = getInt128LE(_data, i);
    i += 16;
    final var outIfTotalSharesBefore = getInt128LE(_data, i);
    i += 16;
    final var inIfUserSharesBefore = getInt128LE(_data, i);
    i += 16;
    final var outIfUserSharesBefore = getInt128LE(_data, i);
    i += 16;
    final var inIfTotalSharesAfter = getInt128LE(_data, i);
    i += 16;
    final var outIfTotalSharesAfter = getInt128LE(_data, i);
    i += 16;
    final var inIfUserSharesAfter = getInt128LE(_data, i);
    i += 16;
    final var outIfUserSharesAfter = getInt128LE(_data, i);
    i += 16;
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var inAmount = getInt64LE(_data, i);
    i += 8;
    final var outAmount = getInt64LE(_data, i);
    i += 8;
    final var outOraclePrice = getInt64LE(_data, i);
    i += 8;
    final var outOraclePriceTwap = getInt64LE(_data, i);
    i += 8;
    final var inVaultAmountBefore = getInt64LE(_data, i);
    i += 8;
    final var outVaultAmountBefore = getInt64LE(_data, i);
    i += 8;
    final var inFundVaultAmountAfter = getInt64LE(_data, i);
    i += 8;
    final var outFundVaultAmountAfter = getInt64LE(_data, i);
    i += 8;
    final var inMarketIndex = getInt16LE(_data, i);
    i += 2;
    final var outMarketIndex = getInt16LE(_data, i);
    return new InsuranceFundSwapRecord(discriminator,
                                       rebalanceConfig,
                                       inIfTotalSharesBefore,
                                       outIfTotalSharesBefore,
                                       inIfUserSharesBefore,
                                       outIfUserSharesBefore,
                                       inIfTotalSharesAfter,
                                       outIfTotalSharesAfter,
                                       inIfUserSharesAfter,
                                       outIfUserSharesAfter,
                                       ts,
                                       inAmount,
                                       outAmount,
                                       outOraclePrice,
                                       outOraclePriceTwap,
                                       inVaultAmountBefore,
                                       outVaultAmountBefore,
                                       inFundVaultAmountAfter,
                                       outFundVaultAmountAfter,
                                       inMarketIndex,
                                       outMarketIndex);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    rebalanceConfig.write(_data, i);
    i += 32;
    putInt128LE(_data, i, inIfTotalSharesBefore);
    i += 16;
    putInt128LE(_data, i, outIfTotalSharesBefore);
    i += 16;
    putInt128LE(_data, i, inIfUserSharesBefore);
    i += 16;
    putInt128LE(_data, i, outIfUserSharesBefore);
    i += 16;
    putInt128LE(_data, i, inIfTotalSharesAfter);
    i += 16;
    putInt128LE(_data, i, outIfTotalSharesAfter);
    i += 16;
    putInt128LE(_data, i, inIfUserSharesAfter);
    i += 16;
    putInt128LE(_data, i, outIfUserSharesAfter);
    i += 16;
    putInt64LE(_data, i, ts);
    i += 8;
    putInt64LE(_data, i, inAmount);
    i += 8;
    putInt64LE(_data, i, outAmount);
    i += 8;
    putInt64LE(_data, i, outOraclePrice);
    i += 8;
    putInt64LE(_data, i, outOraclePriceTwap);
    i += 8;
    putInt64LE(_data, i, inVaultAmountBefore);
    i += 8;
    putInt64LE(_data, i, outVaultAmountBefore);
    i += 8;
    putInt64LE(_data, i, inFundVaultAmountAfter);
    i += 8;
    putInt64LE(_data, i, outFundVaultAmountAfter);
    i += 8;
    putInt16LE(_data, i, inMarketIndex);
    i += 2;
    putInt16LE(_data, i, outMarketIndex);
    i += 2;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

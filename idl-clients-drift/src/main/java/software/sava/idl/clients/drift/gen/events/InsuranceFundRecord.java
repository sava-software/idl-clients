package software.sava.idl.clients.drift.gen.events;

import java.math.BigInteger;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record InsuranceFundRecord(Discriminator discriminator,
                                  long ts,
                                  int spotMarketIndex,
                                  int perpMarketIndex,
                                  int userIfFactor,
                                  int totalIfFactor,
                                  long vaultAmountBefore,
                                  long insuranceVaultAmountBefore,
                                  BigInteger totalIfSharesBefore,
                                  BigInteger totalIfSharesAfter,
                                  long amount) implements DriftEvent {

  public static final int BYTES = 84;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static final int TS_OFFSET = 8;
  public static final int SPOT_MARKET_INDEX_OFFSET = 16;
  public static final int PERP_MARKET_INDEX_OFFSET = 18;
  public static final int USER_IF_FACTOR_OFFSET = 20;
  public static final int TOTAL_IF_FACTOR_OFFSET = 24;
  public static final int VAULT_AMOUNT_BEFORE_OFFSET = 28;
  public static final int INSURANCE_VAULT_AMOUNT_BEFORE_OFFSET = 36;
  public static final int TOTAL_IF_SHARES_BEFORE_OFFSET = 44;
  public static final int TOTAL_IF_SHARES_AFTER_OFFSET = 60;
  public static final int AMOUNT_OFFSET = 76;

  public static InsuranceFundRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var spotMarketIndex = getInt16LE(_data, i);
    i += 2;
    final var perpMarketIndex = getInt16LE(_data, i);
    i += 2;
    final var userIfFactor = getInt32LE(_data, i);
    i += 4;
    final var totalIfFactor = getInt32LE(_data, i);
    i += 4;
    final var vaultAmountBefore = getInt64LE(_data, i);
    i += 8;
    final var insuranceVaultAmountBefore = getInt64LE(_data, i);
    i += 8;
    final var totalIfSharesBefore = getInt128LE(_data, i);
    i += 16;
    final var totalIfSharesAfter = getInt128LE(_data, i);
    i += 16;
    final var amount = getInt64LE(_data, i);
    return new InsuranceFundRecord(discriminator,
                                   ts,
                                   spotMarketIndex,
                                   perpMarketIndex,
                                   userIfFactor,
                                   totalIfFactor,
                                   vaultAmountBefore,
                                   insuranceVaultAmountBefore,
                                   totalIfSharesBefore,
                                   totalIfSharesAfter,
                                   amount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    putInt16LE(_data, i, spotMarketIndex);
    i += 2;
    putInt16LE(_data, i, perpMarketIndex);
    i += 2;
    putInt32LE(_data, i, userIfFactor);
    i += 4;
    putInt32LE(_data, i, totalIfFactor);
    i += 4;
    putInt64LE(_data, i, vaultAmountBefore);
    i += 8;
    putInt64LE(_data, i, insuranceVaultAmountBefore);
    i += 8;
    putInt128LE(_data, i, totalIfSharesBefore);
    i += 16;
    putInt128LE(_data, i, totalIfSharesAfter);
    i += 16;
    putInt64LE(_data, i, amount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

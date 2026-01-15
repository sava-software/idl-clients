package software.sava.idl.clients.drift.gen.events;

import java.math.BigInteger;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record SpotMarketVaultDepositRecord(Discriminator discriminator,
                                           long ts,
                                           int marketIndex,
                                           BigInteger depositBalance,
                                           BigInteger cumulativeDepositInterestBefore,
                                           BigInteger cumulativeDepositInterestAfter,
                                           long depositTokenAmountBefore,
                                           long amount) implements DriftEvent {

  public static final int BYTES = 82;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static final int TS_OFFSET = 8;
  public static final int MARKET_INDEX_OFFSET = 16;
  public static final int DEPOSIT_BALANCE_OFFSET = 18;
  public static final int CUMULATIVE_DEPOSIT_INTEREST_BEFORE_OFFSET = 34;
  public static final int CUMULATIVE_DEPOSIT_INTEREST_AFTER_OFFSET = 50;
  public static final int DEPOSIT_TOKEN_AMOUNT_BEFORE_OFFSET = 66;
  public static final int AMOUNT_OFFSET = 74;

  public static SpotMarketVaultDepositRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var depositBalance = getInt128LE(_data, i);
    i += 16;
    final var cumulativeDepositInterestBefore = getInt128LE(_data, i);
    i += 16;
    final var cumulativeDepositInterestAfter = getInt128LE(_data, i);
    i += 16;
    final var depositTokenAmountBefore = getInt64LE(_data, i);
    i += 8;
    final var amount = getInt64LE(_data, i);
    return new SpotMarketVaultDepositRecord(discriminator,
                                            ts,
                                            marketIndex,
                                            depositBalance,
                                            cumulativeDepositInterestBefore,
                                            cumulativeDepositInterestAfter,
                                            depositTokenAmountBefore,
                                            amount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt128LE(_data, i, depositBalance);
    i += 16;
    putInt128LE(_data, i, cumulativeDepositInterestBefore);
    i += 16;
    putInt128LE(_data, i, cumulativeDepositInterestAfter);
    i += 16;
    putInt64LE(_data, i, depositTokenAmountBefore);
    i += 8;
    putInt64LE(_data, i, amount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

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

public record SpotInterestRecord(Discriminator discriminator,
                                 long ts,
                                 int marketIndex,
                                 BigInteger depositBalance,
                                 BigInteger cumulativeDepositInterest,
                                 BigInteger borrowBalance,
                                 BigInteger cumulativeBorrowInterest,
                                 int optimalUtilization,
                                 int optimalBorrowRate,
                                 int maxBorrowRate) implements DriftEvent {

  public static final int BYTES = 94;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static SpotInterestRecord read(final byte[] _data, final int _offset) {
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
    final var cumulativeDepositInterest = getInt128LE(_data, i);
    i += 16;
    final var borrowBalance = getInt128LE(_data, i);
    i += 16;
    final var cumulativeBorrowInterest = getInt128LE(_data, i);
    i += 16;
    final var optimalUtilization = getInt32LE(_data, i);
    i += 4;
    final var optimalBorrowRate = getInt32LE(_data, i);
    i += 4;
    final var maxBorrowRate = getInt32LE(_data, i);
    return new SpotInterestRecord(discriminator,
                                  ts,
                                  marketIndex,
                                  depositBalance,
                                  cumulativeDepositInterest,
                                  borrowBalance,
                                  cumulativeBorrowInterest,
                                  optimalUtilization,
                                  optimalBorrowRate,
                                  maxBorrowRate);
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
    putInt128LE(_data, i, cumulativeDepositInterest);
    i += 16;
    putInt128LE(_data, i, borrowBalance);
    i += 16;
    putInt128LE(_data, i, cumulativeBorrowInterest);
    i += 16;
    putInt32LE(_data, i, optimalUtilization);
    i += 4;
    putInt32LE(_data, i, optimalBorrowRate);
    i += 4;
    putInt32LE(_data, i, maxBorrowRate);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.drift.gen.events;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record FuelSweepRecord(Discriminator discriminator,
                              long ts,
                              PublicKey authority,
                              int userStatsFuelInsurance,
                              int userStatsFuelDeposits,
                              int userStatsFuelBorrows,
                              int userStatsFuelPositions,
                              int userStatsFuelTaker,
                              int userStatsFuelMaker,
                              BigInteger fuelOverflowFuelInsurance,
                              BigInteger fuelOverflowFuelDeposits,
                              BigInteger fuelOverflowFuelBorrows,
                              BigInteger fuelOverflowFuelPositions,
                              BigInteger fuelOverflowFuelTaker,
                              BigInteger fuelOverflowFuelMaker) implements DriftEvent {

  public static final int BYTES = 168;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static FuelSweepRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var userStatsFuelInsurance = getInt32LE(_data, i);
    i += 4;
    final var userStatsFuelDeposits = getInt32LE(_data, i);
    i += 4;
    final var userStatsFuelBorrows = getInt32LE(_data, i);
    i += 4;
    final var userStatsFuelPositions = getInt32LE(_data, i);
    i += 4;
    final var userStatsFuelTaker = getInt32LE(_data, i);
    i += 4;
    final var userStatsFuelMaker = getInt32LE(_data, i);
    i += 4;
    final var fuelOverflowFuelInsurance = getInt128LE(_data, i);
    i += 16;
    final var fuelOverflowFuelDeposits = getInt128LE(_data, i);
    i += 16;
    final var fuelOverflowFuelBorrows = getInt128LE(_data, i);
    i += 16;
    final var fuelOverflowFuelPositions = getInt128LE(_data, i);
    i += 16;
    final var fuelOverflowFuelTaker = getInt128LE(_data, i);
    i += 16;
    final var fuelOverflowFuelMaker = getInt128LE(_data, i);
    return new FuelSweepRecord(discriminator,
                               ts,
                               authority,
                               userStatsFuelInsurance,
                               userStatsFuelDeposits,
                               userStatsFuelBorrows,
                               userStatsFuelPositions,
                               userStatsFuelTaker,
                               userStatsFuelMaker,
                               fuelOverflowFuelInsurance,
                               fuelOverflowFuelDeposits,
                               fuelOverflowFuelBorrows,
                               fuelOverflowFuelPositions,
                               fuelOverflowFuelTaker,
                               fuelOverflowFuelMaker);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    authority.write(_data, i);
    i += 32;
    putInt32LE(_data, i, userStatsFuelInsurance);
    i += 4;
    putInt32LE(_data, i, userStatsFuelDeposits);
    i += 4;
    putInt32LE(_data, i, userStatsFuelBorrows);
    i += 4;
    putInt32LE(_data, i, userStatsFuelPositions);
    i += 4;
    putInt32LE(_data, i, userStatsFuelTaker);
    i += 4;
    putInt32LE(_data, i, userStatsFuelMaker);
    i += 4;
    putInt128LE(_data, i, fuelOverflowFuelInsurance);
    i += 16;
    putInt128LE(_data, i, fuelOverflowFuelDeposits);
    i += 16;
    putInt128LE(_data, i, fuelOverflowFuelBorrows);
    i += 16;
    putInt128LE(_data, i, fuelOverflowFuelPositions);
    i += 16;
    putInt128LE(_data, i, fuelOverflowFuelTaker);
    i += 16;
    putInt128LE(_data, i, fuelOverflowFuelMaker);
    i += 16;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

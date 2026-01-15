package software.sava.idl.clients.drift.gen.events;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record FuelSeasonRecord(Discriminator discriminator,
                               long ts,
                               PublicKey authority,
                               BigInteger fuelInsurance,
                               BigInteger fuelDeposits,
                               BigInteger fuelBorrows,
                               BigInteger fuelPositions,
                               BigInteger fuelTaker,
                               BigInteger fuelMaker,
                               BigInteger fuelTotal) implements DriftEvent {

  public static final int BYTES = 160;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static final int TS_OFFSET = 8;
  public static final int AUTHORITY_OFFSET = 16;
  public static final int FUEL_INSURANCE_OFFSET = 48;
  public static final int FUEL_DEPOSITS_OFFSET = 64;
  public static final int FUEL_BORROWS_OFFSET = 80;
  public static final int FUEL_POSITIONS_OFFSET = 96;
  public static final int FUEL_TAKER_OFFSET = 112;
  public static final int FUEL_MAKER_OFFSET = 128;
  public static final int FUEL_TOTAL_OFFSET = 144;

  public static FuelSeasonRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var fuelInsurance = getInt128LE(_data, i);
    i += 16;
    final var fuelDeposits = getInt128LE(_data, i);
    i += 16;
    final var fuelBorrows = getInt128LE(_data, i);
    i += 16;
    final var fuelPositions = getInt128LE(_data, i);
    i += 16;
    final var fuelTaker = getInt128LE(_data, i);
    i += 16;
    final var fuelMaker = getInt128LE(_data, i);
    i += 16;
    final var fuelTotal = getInt128LE(_data, i);
    return new FuelSeasonRecord(discriminator,
                                ts,
                                authority,
                                fuelInsurance,
                                fuelDeposits,
                                fuelBorrows,
                                fuelPositions,
                                fuelTaker,
                                fuelMaker,
                                fuelTotal);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    authority.write(_data, i);
    i += 32;
    putInt128LE(_data, i, fuelInsurance);
    i += 16;
    putInt128LE(_data, i, fuelDeposits);
    i += 16;
    putInt128LE(_data, i, fuelBorrows);
    i += 16;
    putInt128LE(_data, i, fuelPositions);
    i += 16;
    putInt128LE(_data, i, fuelTaker);
    i += 16;
    putInt128LE(_data, i, fuelMaker);
    i += 16;
    putInt128LE(_data, i, fuelTotal);
    i += 16;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

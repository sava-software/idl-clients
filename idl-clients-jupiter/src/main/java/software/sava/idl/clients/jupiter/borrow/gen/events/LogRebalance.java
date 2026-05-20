package software.sava.idl.clients.jupiter.borrow.gen.events;

import java.math.BigInteger;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogRebalance(Discriminator discriminator, BigInteger supplyAmt, BigInteger borrowAmt) implements VaultsEvent {

  public static final int BYTES = 40;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(90, 67, 219, 41, 181, 118, 132, 9);

  public static final int SUPPLY_AMT_OFFSET = 8;
  public static final int BORROW_AMT_OFFSET = 24;

  public static LogRebalance read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var supplyAmt = getInt128LE(_data, i);
    i += 16;
    final var borrowAmt = getInt128LE(_data, i);
    return new LogRebalance(discriminator, supplyAmt, borrowAmt);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt128LE(_data, i, supplyAmt);
    i += 16;
    putInt128LE(_data, i, borrowAmt);
    i += 16;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

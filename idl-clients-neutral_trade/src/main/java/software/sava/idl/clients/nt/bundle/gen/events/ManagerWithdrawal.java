package software.sava.idl.clients.nt.bundle.gen.events;

import java.math.BigInteger;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ManagerWithdrawal(Discriminator discriminator, BigInteger managerShares, long redemptionAmount) implements NtbundleEvent {

  public static final int BYTES = 32;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(167, 6, 24, 193, 128, 74, 94, 207);

  public static final int MANAGER_SHARES_OFFSET = 8;
  public static final int REDEMPTION_AMOUNT_OFFSET = 24;

  public static ManagerWithdrawal read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var managerShares = getInt128LE(_data, i);
    i += 16;
    final var redemptionAmount = getInt64LE(_data, i);
    return new ManagerWithdrawal(discriminator, managerShares, redemptionAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt128LE(_data, i, managerShares);
    i += 16;
    putInt64LE(_data, i, redemptionAmount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

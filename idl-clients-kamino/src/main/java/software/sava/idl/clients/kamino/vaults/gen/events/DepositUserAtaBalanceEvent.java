package software.sava.idl.clients.kamino.vaults.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record DepositUserAtaBalanceEvent(Discriminator discriminator, long userAtaBalance) implements KaminoVaultEvent {

  public static final int BYTES = 16;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(149, 244, 122, 223, 193, 62, 199, 31);

  public static final int USER_ATA_BALANCE_OFFSET = 8;

  public static DepositUserAtaBalanceEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var userAtaBalance = getInt64LE(_data, i);
    return new DepositUserAtaBalanceEvent(discriminator, userAtaBalance);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, userAtaBalance);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

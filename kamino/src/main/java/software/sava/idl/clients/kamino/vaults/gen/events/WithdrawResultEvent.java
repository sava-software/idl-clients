package software.sava.idl.clients.kamino.vaults.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record WithdrawResultEvent(Discriminator discriminator,
                                  long sharesToBurn,
                                  long availableToSendToUser,
                                  long investedToDisinvestCtokens,
                                  long investedLiquidityToSendToUser) implements KaminoVaultEvent {

  public static final int BYTES = 40;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(149, 244, 122, 223, 193, 62, 199, 31);

  public static WithdrawResultEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var sharesToBurn = getInt64LE(_data, i);
    i += 8;
    final var availableToSendToUser = getInt64LE(_data, i);
    i += 8;
    final var investedToDisinvestCtokens = getInt64LE(_data, i);
    i += 8;
    final var investedLiquidityToSendToUser = getInt64LE(_data, i);
    return new WithdrawResultEvent(discriminator,
                                   sharesToBurn,
                                   availableToSendToUser,
                                   investedToDisinvestCtokens,
                                   investedLiquidityToSendToUser);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, sharesToBurn);
    i += 8;
    putInt64LE(_data, i, availableToSendToUser);
    i += 8;
    putInt64LE(_data, i, investedToDisinvestCtokens);
    i += 8;
    putInt64LE(_data, i, investedLiquidityToSendToUser);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

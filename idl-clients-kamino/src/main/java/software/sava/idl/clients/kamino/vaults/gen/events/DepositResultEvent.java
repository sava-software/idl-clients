package software.sava.idl.clients.kamino.vaults.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record DepositResultEvent(Discriminator discriminator,
                                 long sharesToMint,
                                 long tokenToDeposit,
                                 long crankFundsToDeposit) implements KaminoVaultEvent {

  public static final int BYTES = 32;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(149, 244, 122, 223, 193, 62, 199, 31);

  public static final int SHARES_TO_MINT_OFFSET = 8;
  public static final int TOKEN_TO_DEPOSIT_OFFSET = 16;
  public static final int CRANK_FUNDS_TO_DEPOSIT_OFFSET = 24;

  public static DepositResultEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var sharesToMint = getInt64LE(_data, i);
    i += 8;
    final var tokenToDeposit = getInt64LE(_data, i);
    i += 8;
    final var crankFundsToDeposit = getInt64LE(_data, i);
    return new DepositResultEvent(discriminator, sharesToMint, tokenToDeposit, crankFundsToDeposit);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, sharesToMint);
    i += 8;
    putInt64LE(_data, i, tokenToDeposit);
    i += 8;
    putInt64LE(_data, i, crankFundsToDeposit);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

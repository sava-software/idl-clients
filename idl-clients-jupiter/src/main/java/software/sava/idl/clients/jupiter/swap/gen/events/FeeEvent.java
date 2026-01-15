package software.sava.idl.clients.jupiter.swap.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record FeeEvent(Discriminator discriminator,
                       PublicKey account,
                       PublicKey mint,
                       long amount) implements JupiterEvent {

  public static final int BYTES = 80;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(73, 79, 78, 127, 184, 213, 13, 220);

  public static final int ACCOUNT_OFFSET = 8;
  public static final int MINT_OFFSET = 40;
  public static final int AMOUNT_OFFSET = 72;

  public static FeeEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var account = readPubKey(_data, i);
    i += 32;
    final var mint = readPubKey(_data, i);
    i += 32;
    final var amount = getInt64LE(_data, i);
    return new FeeEvent(discriminator, account, mint, amount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    account.write(_data, i);
    i += 32;
    mint.write(_data, i);
    i += 32;
    putInt64LE(_data, i, amount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

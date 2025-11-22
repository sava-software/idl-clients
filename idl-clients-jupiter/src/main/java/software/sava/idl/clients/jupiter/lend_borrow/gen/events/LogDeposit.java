package software.sava.idl.clients.jupiter.lend_borrow.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogDeposit(Discriminator discriminator,
                         PublicKey sender,
                         PublicKey receiver,
                         long assets,
                         long sharesMinted) implements LendingEvent {

  public static final int BYTES = 88;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(176, 243, 1, 56, 142, 206, 1, 106);

  public static LogDeposit read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var sender = readPubKey(_data, i);
    i += 32;
    final var receiver = readPubKey(_data, i);
    i += 32;
    final var assets = getInt64LE(_data, i);
    i += 8;
    final var sharesMinted = getInt64LE(_data, i);
    return new LogDeposit(discriminator,
                          sender,
                          receiver,
                          assets,
                          sharesMinted);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    sender.write(_data, i);
    i += 32;
    receiver.write(_data, i);
    i += 32;
    putInt64LE(_data, i, assets);
    i += 8;
    putInt64LE(_data, i, sharesMinted);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

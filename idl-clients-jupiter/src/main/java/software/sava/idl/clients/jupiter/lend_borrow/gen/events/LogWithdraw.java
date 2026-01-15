package software.sava.idl.clients.jupiter.lend_borrow.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogWithdraw(Discriminator discriminator,
                          PublicKey sender,
                          PublicKey receiver,
                          PublicKey owner,
                          long assets,
                          long sharesBurned) implements LendingEvent {

  public static final int BYTES = 120;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(49, 9, 176, 179, 222, 190, 6, 117);

  public static final int SENDER_OFFSET = 8;
  public static final int RECEIVER_OFFSET = 40;
  public static final int OWNER_OFFSET = 72;
  public static final int ASSETS_OFFSET = 104;
  public static final int SHARES_BURNED_OFFSET = 112;

  public static LogWithdraw read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var sender = readPubKey(_data, i);
    i += 32;
    final var receiver = readPubKey(_data, i);
    i += 32;
    final var owner = readPubKey(_data, i);
    i += 32;
    final var assets = getInt64LE(_data, i);
    i += 8;
    final var sharesBurned = getInt64LE(_data, i);
    return new LogWithdraw(discriminator,
                           sender,
                           receiver,
                           owner,
                           assets,
                           sharesBurned);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    sender.write(_data, i);
    i += 32;
    receiver.write(_data, i);
    i += 32;
    owner.write(_data, i);
    i += 32;
    putInt64LE(_data, i, assets);
    i += 8;
    putInt64LE(_data, i, sharesBurned);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

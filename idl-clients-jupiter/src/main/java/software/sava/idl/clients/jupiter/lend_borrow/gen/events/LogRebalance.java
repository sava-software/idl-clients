package software.sava.idl.clients.jupiter.lend_borrow.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogRebalance(Discriminator discriminator, long assets) implements LendingEvent {

  public static final int BYTES = 16;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(90, 67, 219, 41, 181, 118, 132, 9);

  public static final int ASSETS_OFFSET = 8;

  public static LogRebalance read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var assets = getInt64LE(_data, i);
    return new LogRebalance(discriminator, assets);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, assets);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

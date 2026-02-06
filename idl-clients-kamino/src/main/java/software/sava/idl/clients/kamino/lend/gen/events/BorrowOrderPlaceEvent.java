package software.sava.idl.clients.kamino.lend.gen.events;

import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.kamino.lend.gen.types.BorrowOrder;

import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record BorrowOrderPlaceEvent(Discriminator discriminator, BorrowOrder after) implements KaminoLendingEvent {

  public static final int BYTES = 168;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(178, 214, 116, 30, 59, 81, 228, 72);

  public static final int AFTER_OFFSET = 8;

  public static BorrowOrderPlaceEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var after = BorrowOrder.read(_data, i);
    return new BorrowOrderPlaceEvent(discriminator, after);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += after.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

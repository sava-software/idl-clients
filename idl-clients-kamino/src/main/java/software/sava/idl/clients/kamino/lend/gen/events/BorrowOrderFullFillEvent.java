package software.sava.idl.clients.kamino.lend.gen.events;

import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.kamino.lend.gen.types.BorrowOrder;

import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record BorrowOrderFullFillEvent(Discriminator discriminator, BorrowOrder before) implements KaminoLendingEvent {

  public static final int BYTES = 168;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(178, 214, 116, 30, 59, 81, 228, 72);

  public static final int BEFORE_OFFSET = 8;

  public static BorrowOrderFullFillEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var before = BorrowOrder.read(_data, i);
    return new BorrowOrderFullFillEvent(discriminator, before);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += before.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

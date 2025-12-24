package software.sava.idl.clients.jupiter.lend_borrow.gen.events;

import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.jupiter.lend_borrow.gen.types.AddressBool;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogUpdateAuths(Discriminator discriminator, AddressBool[] authStatus) implements LendingEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(88, 80, 109, 48, 111, 203, 76, 251);

  public static LogUpdateAuths read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var authStatus = SerDeUtil.readVector(4, AddressBool.class, AddressBool::read, _data, i);
    return new LogUpdateAuths(discriminator, authStatus);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += SerDeUtil.writeVector(4, authStatus, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + SerDeUtil.lenVector(4, authStatus);
  }
}

package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.core.borsh.Borsh;

public record QueueAllowSubsidiesParams(int allowSubsidies) implements Borsh {

  public static final int BYTES = 1;

  public static QueueAllowSubsidiesParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var allowSubsidies = _data[_offset] & 0xFF;
    return new QueueAllowSubsidiesParams(allowSubsidies);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) allowSubsidies;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

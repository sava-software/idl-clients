package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record CreateConditionalOrdersAccountInstruction(int capacity) implements SerDe {

  public static final int BYTES = 1;

  public static final int CAPACITY_OFFSET = 0;

  public static CreateConditionalOrdersAccountInstruction read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var capacity = _data[_offset] & 0xFF;
    return new CreateConditionalOrdersAccountInstruction(capacity);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) capacity;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

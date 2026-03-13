package software.sava.idl.clients.jupiter.stable.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record EmptyOracle(byte[] reserved,
                          byte[] reserved1,
                          byte[] reserved2,
                          byte[] reserved3) implements SerDe {

  public static final int BYTES = 120;
  public static final int RESERVED_LEN = 32;
  public static final int RESERVED_1_LEN = 32;
  public static final int RESERVED_2_LEN = 32;
  public static final int RESERVED_3_LEN = 24;

  public static final int RESERVED_OFFSET = 0;
  public static final int RESERVED_1_OFFSET = 32;
  public static final int RESERVED_2_OFFSET = 64;
  public static final int RESERVED_3_OFFSET = 96;

  public static EmptyOracle read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var reserved = new byte[32];
    i += SerDeUtil.readArray(reserved, _data, i);
    final var reserved1 = new byte[32];
    i += SerDeUtil.readArray(reserved1, _data, i);
    final var reserved2 = new byte[32];
    i += SerDeUtil.readArray(reserved2, _data, i);
    final var reserved3 = new byte[24];
    SerDeUtil.readArray(reserved3, _data, i);
    return new EmptyOracle(reserved,
                           reserved1,
                           reserved2,
                           reserved3);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(reserved, 32, _data, i);
    i += SerDeUtil.writeArrayChecked(reserved1, 32, _data, i);
    i += SerDeUtil.writeArrayChecked(reserved2, 32, _data, i);
    i += SerDeUtil.writeArrayChecked(reserved3, 24, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

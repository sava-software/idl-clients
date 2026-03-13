package software.sava.idl.clients.jupiter.stable.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record PythV2Oracle(byte[] feedId,
                           PublicKey account,
                           byte[] reserved1,
                           byte[] reserved2) implements SerDe {

  public static final int BYTES = 120;
  public static final int FEED_ID_LEN = 32;
  public static final int RESERVED_1_LEN = 32;
  public static final int RESERVED_2_LEN = 24;

  public static final int FEED_ID_OFFSET = 0;
  public static final int ACCOUNT_OFFSET = 32;
  public static final int RESERVED_1_OFFSET = 64;
  public static final int RESERVED_2_OFFSET = 96;

  public static PythV2Oracle read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var feedId = new byte[32];
    i += SerDeUtil.readArray(feedId, _data, i);
    final var account = readPubKey(_data, i);
    i += 32;
    final var reserved1 = new byte[32];
    i += SerDeUtil.readArray(reserved1, _data, i);
    final var reserved2 = new byte[24];
    SerDeUtil.readArray(reserved2, _data, i);
    return new PythV2Oracle(feedId,
                            account,
                            reserved1,
                            reserved2);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(feedId, 32, _data, i);
    account.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(reserved1, 32, _data, i);
    i += SerDeUtil.writeArrayChecked(reserved2, 24, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

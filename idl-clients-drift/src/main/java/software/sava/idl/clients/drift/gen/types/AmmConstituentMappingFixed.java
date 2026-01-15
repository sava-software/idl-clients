package software.sava.idl.clients.drift.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record AmmConstituentMappingFixed(PublicKey lpPool,
                                         int bump,
                                         byte[] pad,
                                         int len) implements SerDe {

  public static final int BYTES = 40;
  public static final int PAD_LEN = 3;

  public static final int LP_POOL_OFFSET = 0;
  public static final int BUMP_OFFSET = 32;
  public static final int PAD_OFFSET = 33;
  public static final int LEN_OFFSET = 36;

  public static AmmConstituentMappingFixed read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var lpPool = readPubKey(_data, i);
    i += 32;
    final var bump = _data[i] & 0xFF;
    ++i;
    final var pad = new byte[3];
    i += SerDeUtil.readArray(pad, _data, i);
    final var len = getInt32LE(_data, i);
    return new AmmConstituentMappingFixed(lpPool,
                                          bump,
                                          pad,
                                          len);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    lpPool.write(_data, i);
    i += 32;
    _data[i] = (byte) bump;
    ++i;
    i += SerDeUtil.writeArrayChecked(pad, 3, _data, i);
    putInt32LE(_data, i, len);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

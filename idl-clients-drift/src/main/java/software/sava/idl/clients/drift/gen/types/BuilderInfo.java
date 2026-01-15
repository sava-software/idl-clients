package software.sava.idl.clients.drift.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

public record BuilderInfo(PublicKey authority,
                          int maxFeeTenthBps,
                          byte[] padding) implements SerDe {

  public static final int BYTES = 40;
  public static final int PADDING_LEN = 6;

  public static final int AUTHORITY_OFFSET = 0;
  public static final int MAX_FEE_TENTH_BPS_OFFSET = 32;
  public static final int PADDING_OFFSET = 34;

  public static BuilderInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var maxFeeTenthBps = getInt16LE(_data, i);
    i += 2;
    final var padding = new byte[6];
    SerDeUtil.readArray(padding, _data, i);
    return new BuilderInfo(authority, maxFeeTenthBps, padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    authority.write(_data, i);
    i += 32;
    putInt16LE(_data, i, maxFeeTenthBps);
    i += 2;
    i += SerDeUtil.writeArrayChecked(padding, 6, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

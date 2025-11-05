package software.sava.idl.clients.pyth.receiver.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

public record DataSource(int chain, PublicKey emitter) implements Borsh {

  public static final int BYTES = 34;

  public static DataSource read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var chain = getInt16LE(_data, i);
    i += 2;
    final var emitter = readPubKey(_data, i);
    return new DataSource(chain, emitter);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt16LE(_data, i, chain);
    i += 2;
    emitter.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.stake.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record Lockup(long unixTimestamp,
                     long epoch,
                     PublicKey custodian) implements Borsh {

  public static final int BYTES = 48;

  public static Lockup read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var unixTimestamp = getInt64LE(_data, i);
    i += 8;
    final var epoch = getInt64LE(_data, i);
    i += 8;
    final var custodian = readPubKey(_data, i);
    return new Lockup(unixTimestamp, epoch, custodian);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, unixTimestamp);
    i += 8;
    putInt64LE(_data, i, epoch);
    i += 8;
    custodian.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

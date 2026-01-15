package software.sava.idl.clients.oracles.pyth.lazer.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record TrustedSignerInfo(PublicKey pubkey, long expiresAt) implements SerDe {

  public static final int BYTES = 40;

  public static final int PUBKEY_OFFSET = 0;
  public static final int EXPIRES_AT_OFFSET = 32;

  public static TrustedSignerInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var pubkey = readPubKey(_data, i);
    i += 32;
    final var expiresAt = getInt64LE(_data, i);
    return new TrustedSignerInfo(pubkey, expiresAt);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    pubkey.write(_data, i);
    i += 32;
    putInt64LE(_data, i, expiresAt);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

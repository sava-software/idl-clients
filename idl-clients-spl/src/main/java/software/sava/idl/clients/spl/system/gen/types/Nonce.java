package software.sava.idl.clients.spl.system.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record Nonce(PublicKey _address,
                    NonceVersion version,
                    NonceState state,
                    PublicKey authority,
                    PublicKey blockhash,
                    long lamportsPerSignature) implements Borsh {

  public static final int BYTES = 80;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final int VERSION_OFFSET = 0;
  public static final int STATE_OFFSET = 4;
  public static final int AUTHORITY_OFFSET = 8;
  public static final int BLOCKHASH_OFFSET = 40;
  public static final int LAMPORTS_PER_SIGNATURE_OFFSET = 72;

  public static Filter createVersionFilter(final NonceVersion version) {
    return Filter.createMemCompFilter(VERSION_OFFSET, version.write());
  }

  public static Filter createStateFilter(final NonceState state) {
    return Filter.createMemCompFilter(STATE_OFFSET, state.write());
  }

  public static Filter createAuthorityFilter(final PublicKey authority) {
    return Filter.createMemCompFilter(AUTHORITY_OFFSET, authority);
  }

  public static Filter createBlockhashFilter(final PublicKey blockhash) {
    return Filter.createMemCompFilter(BLOCKHASH_OFFSET, blockhash);
  }

  public static Filter createLamportsPerSignatureFilter(final long lamportsPerSignature) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, lamportsPerSignature);
    return Filter.createMemCompFilter(LAMPORTS_PER_SIGNATURE_OFFSET, _data);
  }

  public static Nonce read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Nonce read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Nonce read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Nonce> FACTORY = Nonce::read;

  public static Nonce read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var version = NonceVersion.read(_data, i);
    i += Borsh.len(version);
    final var state = NonceState.read(_data, i);
    i += Borsh.len(state);
    final var authority = readPubKey(_data, i);
    i += 32;
    final var blockhash = readPubKey(_data, i);
    i += 32;
    final var lamportsPerSignature = getInt64LE(_data, i);
    return new Nonce(_address,
                     version,
                     state,
                     authority,
                     blockhash,
                     lamportsPerSignature);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.write(version, _data, i);
    i += Borsh.write(state, _data, i);
    authority.write(_data, i);
    i += 32;
    blockhash.write(_data, i);
    i += 32;
    putInt64LE(_data, i, lamportsPerSignature);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

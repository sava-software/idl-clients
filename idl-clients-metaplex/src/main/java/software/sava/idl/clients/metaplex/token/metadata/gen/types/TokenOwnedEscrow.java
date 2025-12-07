package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record TokenOwnedEscrow(PublicKey _address,
                               Key key,
                               PublicKey baseToken,
                               EscrowAuthority authority,
                               int bump) implements Borsh {

  public static final int KEY_OFFSET = 0;
  public static final int BASE_TOKEN_OFFSET = 1;
  public static final int AUTHORITY_OFFSET = 33;

  public static Filter createKeyFilter(final Key key) {
    return Filter.createMemCompFilter(KEY_OFFSET, key.write());
  }

  public static Filter createBaseTokenFilter(final PublicKey baseToken) {
    return Filter.createMemCompFilter(BASE_TOKEN_OFFSET, baseToken);
  }

  public static Filter createAuthorityFilter(final EscrowAuthority authority) {
    return Filter.createMemCompFilter(AUTHORITY_OFFSET, authority.write());
  }

  public static TokenOwnedEscrow read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static TokenOwnedEscrow read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static TokenOwnedEscrow read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], TokenOwnedEscrow> FACTORY = TokenOwnedEscrow::read;

  public static TokenOwnedEscrow read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var key = Key.read(_data, i);
    i += key.l();
    final var baseToken = readPubKey(_data, i);
    i += 32;
    final var authority = EscrowAuthority.read(_data, i);
    i += authority.l();
    final var bump = _data[i] & 0xFF;
    return new TokenOwnedEscrow(_address,
                                key,
                                baseToken,
                                authority,
                                bump);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += key.write(_data, i);
    baseToken.write(_data, i);
    i += 32;
    i += authority.write(_data, i);
    _data[i] = (byte) bump;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return key.l() + 32 + authority.l() + 1;
  }
}

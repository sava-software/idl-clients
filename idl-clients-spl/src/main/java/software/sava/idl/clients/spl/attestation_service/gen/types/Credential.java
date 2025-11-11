package software.sava.idl.clients.spl.attestation_service.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record Credential(PublicKey _address,
                         Discriminator discriminator,
                         PublicKey authority,
                         byte[] name,
                         PublicKey[] authorizedSigners) implements Borsh {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(145, 44, 68, 220, 67, 46, 100, 135);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int AUTHORITY_OFFSET = 8;
  public static final int NAME_OFFSET = 40;

  public static Filter createAuthorityFilter(final PublicKey authority) {
    return Filter.createMemCompFilter(AUTHORITY_OFFSET, authority);
  }

  public static Credential read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Credential read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Credential read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Credential> FACTORY = Credential::read;

  public static Credential read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var authority = readPubKey(_data, i);
    i += 32;
    final var name = Borsh.readbyteVector(_data, i);
    i += Borsh.lenVector(name);
    final var authorizedSigners = Borsh.readPublicKeyVector(_data, i);
    return new Credential(_address, discriminator, authority, name, authorizedSigners);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    authority.write(_data, i);
    i += 32;
    i += Borsh.writeVector(name, _data, i);
    i += Borsh.writeVector(authorizedSigners, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32 + Borsh.lenVector(name) + Borsh.lenVector(authorizedSigners);
  }
}

package software.sava.idl.clients.attestation_service.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record Attestation(PublicKey _address,
                          Discriminator discriminator,
                          PublicKey nonce,
                          PublicKey credential,
                          PublicKey schema,
                          byte[] data,
                          PublicKey signer,
                          long expiry,
                          PublicKey tokenAccount) implements Borsh {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(152, 125, 183, 86, 36, 146, 121, 73);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int NONCE_OFFSET = 8;
  public static final int CREDENTIAL_OFFSET = 40;
  public static final int SCHEMA_OFFSET = 72;
  public static final int DATA_OFFSET = 104;

  public static Filter createNonceFilter(final PublicKey nonce) {
    return Filter.createMemCompFilter(NONCE_OFFSET, nonce);
  }

  public static Filter createCredentialFilter(final PublicKey credential) {
    return Filter.createMemCompFilter(CREDENTIAL_OFFSET, credential);
  }

  public static Filter createSchemaFilter(final PublicKey schema) {
    return Filter.createMemCompFilter(SCHEMA_OFFSET, schema);
  }

  public static Attestation read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Attestation read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Attestation read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Attestation> FACTORY = Attestation::read;

  public static Attestation read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var nonce = readPubKey(_data, i);
    i += 32;
    final var credential = readPubKey(_data, i);
    i += 32;
    final var schema = readPubKey(_data, i);
    i += 32;
    final var data = Borsh.readbyteVector(_data, i);
    i += Borsh.lenVector(data);
    final var signer = readPubKey(_data, i);
    i += 32;
    final var expiry = getInt64LE(_data, i);
    i += 8;
    final var tokenAccount = readPubKey(_data, i);
    return new Attestation(_address,
                           discriminator,
                           nonce,
                           credential,
                           schema,
                           data,
                           signer,
                           expiry,
                           tokenAccount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    nonce.write(_data, i);
    i += 32;
    credential.write(_data, i);
    i += 32;
    schema.write(_data, i);
    i += 32;
    i += Borsh.writeVector(data, _data, i);
    signer.write(_data, i);
    i += 32;
    putInt64LE(_data, i, expiry);
    i += 8;
    tokenAccount.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32
         + 32
         + 32
         + Borsh.lenVector(data)
         + 32
         + 8
         + 32;
  }
}

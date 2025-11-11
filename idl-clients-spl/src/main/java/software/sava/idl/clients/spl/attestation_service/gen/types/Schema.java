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

public record Schema(PublicKey _address,
                     Discriminator discriminator,
                     PublicKey credential,
                     byte[] name,
                     byte[] description,
                     byte[] layout,
                     byte[] fieldNames,
                     boolean isPaused,
                     int version) implements Borsh {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(197, 41, 118, 109, 215, 189, 52, 105);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int CREDENTIAL_OFFSET = 8;
  public static final int NAME_OFFSET = 40;

  public static Filter createCredentialFilter(final PublicKey credential) {
    return Filter.createMemCompFilter(CREDENTIAL_OFFSET, credential);
  }

  public static Schema read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Schema read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Schema read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Schema> FACTORY = Schema::read;

  public static Schema read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var credential = readPubKey(_data, i);
    i += 32;
    final var name = Borsh.readbyteVector(_data, i);
    i += Borsh.lenVector(name);
    final var description = Borsh.readbyteVector(_data, i);
    i += Borsh.lenVector(description);
    final var layout = Borsh.readbyteVector(_data, i);
    i += Borsh.lenVector(layout);
    final var fieldNames = Borsh.readbyteVector(_data, i);
    i += Borsh.lenVector(fieldNames);
    final var isPaused = _data[i] == 1;
    ++i;
    final var version = _data[i] & 0xFF;
    return new Schema(_address,
                      discriminator,
                      credential,
                      name,
                      description,
                      layout,
                      fieldNames,
                      isPaused,
                      version);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    credential.write(_data, i);
    i += 32;
    i += Borsh.writeVector(name, _data, i);
    i += Borsh.writeVector(description, _data, i);
    i += Borsh.writeVector(layout, _data, i);
    i += Borsh.writeVector(fieldNames, _data, i);
    _data[i] = (byte) (isPaused ? 1 : 0);
    ++i;
    _data[i] = (byte) version;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32
         + Borsh.lenVector(name)
         + Borsh.lenVector(description)
         + Borsh.lenVector(layout)
         + Borsh.lenVector(fieldNames)
         + 1
         + 1;
  }
}

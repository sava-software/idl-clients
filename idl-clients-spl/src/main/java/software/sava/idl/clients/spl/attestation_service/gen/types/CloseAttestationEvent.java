package software.sava.idl.clients.spl.attestation_service.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record CloseAttestationEvent(int discriminator,
                                    PublicKey schema,
                                    byte[] attestationData) implements SerDe {

  public static CloseAttestationEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var discriminator = _data[i] & 0xFF;
    ++i;
    final var schema = readPubKey(_data, i);
    i += 32;
    final var attestationData = SerDeUtil.readbyteVector(4, _data, i);
    return new CloseAttestationEvent(discriminator, schema, attestationData);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) discriminator;
    ++i;
    schema.write(_data, i);
    i += 32;
    i += SerDeUtil.writeVector(4, attestationData, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 1 + 32 + SerDeUtil.lenVector(4, attestationData);
  }
}

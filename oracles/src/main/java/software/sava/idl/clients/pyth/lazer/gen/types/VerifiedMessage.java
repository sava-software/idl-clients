package software.sava.idl.clients.pyth.lazer.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;

/// A message with a verified ed25519 signature.
///
/// @param publicKey Public key that signed the message.
/// @param payload Signed message payload.
public record VerifiedMessage(PublicKey publicKey, byte[] payload) implements Borsh {

  public static VerifiedMessage read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var publicKey = readPubKey(_data, i);
    i += 32;
    final var payload = Borsh.readbyteVector(_data, i);
    return new VerifiedMessage(publicKey, payload);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    publicKey.write(_data, i);
    i += 32;
    i += Borsh.writeVector(payload, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 32 + Borsh.lenVector(payload);
  }
}

package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record AuthorizationData(Payload payload) implements SerDe {

  public static final int PAYLOAD_OFFSET = 0;

  public static AuthorizationData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var payload = Payload.read(_data, _offset);
    return new AuthorizationData(payload);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += payload.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return payload.l();
  }
}

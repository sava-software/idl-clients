package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import java.lang.String;

import java.util.Arrays;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.encoding.ByteUtil.getInt32LE;

public record Payload(String key, byte[] _key, PayloadType payloadType) implements SerDe {

  public static final int KEY_OFFSET = 0;

  public static Payload createRecord(final String key, final PayloadType payloadType) {
    return new Payload(key, key == null ? null : key.getBytes(UTF_8), payloadType);
  }

  public static Payload read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final int _keyLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _key = Arrays.copyOfRange(_data, i, i + _keyLength);
    final var key = new String(_key, UTF_8);
    i += _key.length;
    final var payloadType = PayloadType.read(_data, i);
    return new Payload(key, key == null ? null : key.getBytes(UTF_8), payloadType);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeVector(4, _key, _data, i);
    i += payloadType.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return _key.length + payloadType.l();
  }
}

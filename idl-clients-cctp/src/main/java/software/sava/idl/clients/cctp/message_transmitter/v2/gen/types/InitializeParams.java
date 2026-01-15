package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record InitializeParams(int localDomain,
                               PublicKey attester,
                               long maxMessageBodySize,
                               int version) implements SerDe {

  public static final int BYTES = 48;

  public static final int LOCAL_DOMAIN_OFFSET = 0;
  public static final int ATTESTER_OFFSET = 4;
  public static final int MAX_MESSAGE_BODY_SIZE_OFFSET = 36;
  public static final int VERSION_OFFSET = 44;

  public static InitializeParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var localDomain = getInt32LE(_data, i);
    i += 4;
    final var attester = readPubKey(_data, i);
    i += 32;
    final var maxMessageBodySize = getInt64LE(_data, i);
    i += 8;
    final var version = getInt32LE(_data, i);
    return new InitializeParams(localDomain,
                                attester,
                                maxMessageBodySize,
                                version);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, localDomain);
    i += 4;
    attester.write(_data, i);
    i += 32;
    putInt64LE(_data, i, maxMessageBodySize);
    i += 8;
    putInt32LE(_data, i, version);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

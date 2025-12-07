package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

public record EditionMarker(PublicKey _address, Key key, byte[] ledger) implements Borsh {

  public static final int BYTES = 32;
  public static final int LEDGER_LEN = 31;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final int KEY_OFFSET = 0;
  public static final int LEDGER_OFFSET = 1;

  public static Filter createKeyFilter(final Key key) {
    return Filter.createMemCompFilter(KEY_OFFSET, key.write());
  }

  public static EditionMarker read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static EditionMarker read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static EditionMarker read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], EditionMarker> FACTORY = EditionMarker::read;

  public static EditionMarker read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var key = Key.read(_data, i);
    i += key.l();
    final var ledger = new byte[31];
    Borsh.readArray(ledger, _data, i);
    return new EditionMarker(_address, key, ledger);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += key.write(_data, i);
    i += Borsh.writeArrayChecked(ledger, 31, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

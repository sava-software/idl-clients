package software.sava.idl.clients.loopscale.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record CollateralTerms(PublicKey assetIdentifier, long[] terms) implements SerDe {

  public static final int BYTES = 72;
  public static final int TERMS_LEN = 5;

  public static final int ASSET_IDENTIFIER_OFFSET = 0;
  public static final int TERMS_OFFSET = 32;

  public static CollateralTerms read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var assetIdentifier = readPubKey(_data, i);
    i += 32;
    final var terms = new long[5];
    SerDeUtil.readArray(terms, _data, i);
    return new CollateralTerms(assetIdentifier, terms);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    assetIdentifier.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(terms, 5, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

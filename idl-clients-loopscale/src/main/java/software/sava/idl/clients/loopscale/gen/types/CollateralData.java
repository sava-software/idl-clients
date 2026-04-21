package software.sava.idl.clients.loopscale.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record CollateralData(PublicKey assetMint,
                             PodU64 amount,
                             int assetType,
                             PublicKey assetIdentifier) implements SerDe {

  public static final int BYTES = 73;

  public static final int ASSET_MINT_OFFSET = 0;
  public static final int AMOUNT_OFFSET = 32;
  public static final int ASSET_TYPE_OFFSET = 40;
  public static final int ASSET_IDENTIFIER_OFFSET = 41;

  public static CollateralData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var assetMint = readPubKey(_data, i);
    i += 32;
    final var amount = PodU64.read(_data, i);
    i += amount.l();
    final var assetType = _data[i] & 0xFF;
    ++i;
    final var assetIdentifier = readPubKey(_data, i);
    return new CollateralData(assetMint,
                              amount,
                              assetType,
                              assetIdentifier);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    assetMint.write(_data, i);
    i += 32;
    i += amount.write(_data, i);
    _data[i] = (byte) assetType;
    ++i;
    assetIdentifier.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

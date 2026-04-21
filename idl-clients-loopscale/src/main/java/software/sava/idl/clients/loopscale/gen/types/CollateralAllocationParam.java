package software.sava.idl.clients.loopscale.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record CollateralAllocationParam(PublicKey assetIdentifier, long currentAllocationAmount) implements SerDe {

  public static final int BYTES = 40;

  public static final int ASSET_IDENTIFIER_OFFSET = 0;
  public static final int CURRENT_ALLOCATION_AMOUNT_OFFSET = 32;

  public static CollateralAllocationParam read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var assetIdentifier = readPubKey(_data, i);
    i += 32;
    final var currentAllocationAmount = getInt64LE(_data, i);
    return new CollateralAllocationParam(assetIdentifier, currentAllocationAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    assetIdentifier.write(_data, i);
    i += 32;
    putInt64LE(_data, i, currentAllocationAmount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

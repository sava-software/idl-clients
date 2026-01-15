package software.sava.idl.clients.oracles.pyth.push.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record PostUpdateParams(MerklePriceUpdate merklePriceUpdate, int treasuryId) implements SerDe {

  public static final int MERKLE_PRICE_UPDATE_OFFSET = 0;

  public static PostUpdateParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var merklePriceUpdate = MerklePriceUpdate.read(_data, i);
    i += merklePriceUpdate.l();
    final var treasuryId = _data[i] & 0xFF;
    return new PostUpdateParams(merklePriceUpdate, treasuryId);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += merklePriceUpdate.write(_data, i);
    _data[i] = (byte) treasuryId;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return merklePriceUpdate.l() + 1;
  }
}

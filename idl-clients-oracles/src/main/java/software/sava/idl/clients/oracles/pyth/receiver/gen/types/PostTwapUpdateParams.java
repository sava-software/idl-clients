package software.sava.idl.clients.oracles.pyth.receiver.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.oracles.pyth.push.gen.types.MerklePriceUpdate;

public record PostTwapUpdateParams(MerklePriceUpdate startMerklePriceUpdate,
                                   MerklePriceUpdate endMerklePriceUpdate,
                                   int treasuryId) implements SerDe {

  public static PostTwapUpdateParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var startMerklePriceUpdate = MerklePriceUpdate.read(_data, i);
    i += startMerklePriceUpdate.l();
    final var endMerklePriceUpdate = MerklePriceUpdate.read(_data, i);
    i += endMerklePriceUpdate.l();
    final var treasuryId = _data[i] & 0xFF;
    return new PostTwapUpdateParams(startMerklePriceUpdate, endMerklePriceUpdate, treasuryId);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += startMerklePriceUpdate.write(_data, i);
    i += endMerklePriceUpdate.write(_data, i);
    _data[i] = (byte) treasuryId;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return startMerklePriceUpdate.l() + endMerklePriceUpdate.l() + 1;
  }
}

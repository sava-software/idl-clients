package software.sava.idl.clients.pyth.receiver.gen.types;

import software.sava.core.borsh.Borsh;
import software.sava.idl.clients.pyth.push.gen.types.MerklePriceUpdate;

public record PostTwapUpdateParams(MerklePriceUpdate startMerklePriceUpdate,
                                   MerklePriceUpdate endMerklePriceUpdate,
                                   int treasuryId) implements Borsh {

  public static PostTwapUpdateParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var startMerklePriceUpdate = MerklePriceUpdate.read(_data, i);
    i += Borsh.len(startMerklePriceUpdate);
    final var endMerklePriceUpdate = MerklePriceUpdate.read(_data, i);
    i += Borsh.len(endMerklePriceUpdate);
    final var treasuryId = _data[i] & 0xFF;
    return new PostTwapUpdateParams(startMerklePriceUpdate, endMerklePriceUpdate, treasuryId);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.write(startMerklePriceUpdate, _data, i);
    i += Borsh.write(endMerklePriceUpdate, _data, i);
    _data[i] = (byte) treasuryId;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return Borsh.len(startMerklePriceUpdate) + Borsh.len(endMerklePriceUpdate) + 1;
  }
}

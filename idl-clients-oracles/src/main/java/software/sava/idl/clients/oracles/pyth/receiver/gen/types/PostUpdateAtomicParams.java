package software.sava.idl.clients.oracles.pyth.receiver.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.oracles.pyth.push.gen.types.MerklePriceUpdate;

public record PostUpdateAtomicParams(byte[] vaa,
                                     MerklePriceUpdate merklePriceUpdate,
                                     int treasuryId) implements SerDe {

  public static PostUpdateAtomicParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var vaa = SerDeUtil.readbyteVector(4, _data, i);
    i += SerDeUtil.lenVector(4, vaa);
    final var merklePriceUpdate = MerklePriceUpdate.read(_data, i);
    i += merklePriceUpdate.l();
    final var treasuryId = _data[i] & 0xFF;
    return new PostUpdateAtomicParams(vaa, merklePriceUpdate, treasuryId);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeVector(4, vaa, _data, i);
    i += merklePriceUpdate.write(_data, i);
    _data[i] = (byte) treasuryId;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return SerDeUtil.lenVector(4, vaa) + merklePriceUpdate.l() + 1;
  }
}

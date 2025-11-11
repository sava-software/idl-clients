package software.sava.idl.clients.oracles.pyth.receiver.gen.types;

import software.sava.core.borsh.Borsh;
import software.sava.idl.clients.oracles.pyth.push.gen.types.MerklePriceUpdate;

public record PostUpdateAtomicParams(byte[] vaa,
                                     MerklePriceUpdate merklePriceUpdate,
                                     int treasuryId) implements Borsh {

  public static PostUpdateAtomicParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var vaa = Borsh.readbyteVector(_data, i);
    i += Borsh.lenVector(vaa);
    final var merklePriceUpdate = MerklePriceUpdate.read(_data, i);
    i += Borsh.len(merklePriceUpdate);
    final var treasuryId = _data[i] & 0xFF;
    return new PostUpdateAtomicParams(vaa, merklePriceUpdate, treasuryId);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeVector(vaa, _data, i);
    i += Borsh.write(merklePriceUpdate, _data, i);
    _data[i] = (byte) treasuryId;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return Borsh.lenVector(vaa) + Borsh.len(merklePriceUpdate) + 1;
  }
}

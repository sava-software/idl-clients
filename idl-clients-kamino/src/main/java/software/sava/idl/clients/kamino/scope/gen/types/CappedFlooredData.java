package software.sava.idl.clients.kamino.scope.gen.types;

import java.util.OptionalInt;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

public record CappedFlooredData(int sourceEntry,
                                OptionalInt capEntry,
                                OptionalInt floorEntry) implements SerDe {

  public static CappedFlooredData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var sourceEntry = getInt16LE(_data, i);
    i += 2;
    final OptionalInt capEntry;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      capEntry = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      capEntry = OptionalInt.of(getInt16LE(_data, i));
      i += 2;
    }
    final OptionalInt floorEntry;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      floorEntry = OptionalInt.empty();
    } else {
      ++i;
      floorEntry = OptionalInt.of(getInt16LE(_data, i));
    }
    return new CappedFlooredData(sourceEntry, capEntry, floorEntry);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt16LE(_data, i, sourceEntry);
    i += 2;
    i += SerDeUtil.writeOptionalshort(1, capEntry, _data, i);
    i += SerDeUtil.writeOptionalshort(1, floorEntry, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 2 + (capEntry == null || capEntry.isEmpty() ? 1 : (1 + 2)) + (floorEntry == null || floorEntry.isEmpty() ? 1 : (1 + 2));
  }
}

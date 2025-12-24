package software.sava.idl.clients.drift.gen.types;

import java.util.OptionalLong;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

public record PrelaunchOracleParams(int perpMarketIndex,
                                    OptionalLong price,
                                    OptionalLong maxPrice) implements SerDe {

  public static PrelaunchOracleParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var perpMarketIndex = getInt16LE(_data, i);
    i += 2;
    final OptionalLong price;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      price = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      price = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong maxPrice;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      maxPrice = OptionalLong.empty();
    } else {
      ++i;
      maxPrice = OptionalLong.of(getInt64LE(_data, i));
    }
    return new PrelaunchOracleParams(perpMarketIndex, price, maxPrice);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt16LE(_data, i, perpMarketIndex);
    i += 2;
    i += SerDeUtil.writeOptional(1, price, _data, i);
    i += SerDeUtil.writeOptional(1, maxPrice, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 2 + (price == null || price.isEmpty() ? 1 : (1 + 8)) + (maxPrice == null || maxPrice.isEmpty() ? 1 : (1 + 8));
  }
}

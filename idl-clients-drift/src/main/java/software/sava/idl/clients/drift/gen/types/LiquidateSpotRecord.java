package software.sava.idl.clients.drift.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param liabilityTransfer precision: token mint precision
/// @param ifFee precision: token mint precision
public record LiquidateSpotRecord(int assetMarketIndex,
                                  long assetPrice,
                                  BigInteger assetTransfer,
                                  int liabilityMarketIndex,
                                  long liabilityPrice,
                                  BigInteger liabilityTransfer,
                                  long ifFee) implements SerDe {

  public static final int BYTES = 60;

  public static LiquidateSpotRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var assetMarketIndex = getInt16LE(_data, i);
    i += 2;
    final var assetPrice = getInt64LE(_data, i);
    i += 8;
    final var assetTransfer = getInt128LE(_data, i);
    i += 16;
    final var liabilityMarketIndex = getInt16LE(_data, i);
    i += 2;
    final var liabilityPrice = getInt64LE(_data, i);
    i += 8;
    final var liabilityTransfer = getInt128LE(_data, i);
    i += 16;
    final var ifFee = getInt64LE(_data, i);
    return new LiquidateSpotRecord(assetMarketIndex,
                                   assetPrice,
                                   assetTransfer,
                                   liabilityMarketIndex,
                                   liabilityPrice,
                                   liabilityTransfer,
                                   ifFee);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt16LE(_data, i, assetMarketIndex);
    i += 2;
    putInt64LE(_data, i, assetPrice);
    i += 8;
    putInt128LE(_data, i, assetTransfer);
    i += 16;
    putInt16LE(_data, i, liabilityMarketIndex);
    i += 2;
    putInt64LE(_data, i, liabilityPrice);
    i += 8;
    putInt128LE(_data, i, liabilityTransfer);
    i += 16;
    putInt64LE(_data, i, ifFee);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

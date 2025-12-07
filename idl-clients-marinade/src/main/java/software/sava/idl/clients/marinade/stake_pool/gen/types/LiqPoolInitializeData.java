package software.sava.idl.clients.marinade.stake_pool.gen.types;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record LiqPoolInitializeData(long lpLiquidityTarget,
                                    Fee lpMaxFee,
                                    Fee lpMinFee,
                                    Fee lpTreasuryCut) implements Borsh {

  public static final int BYTES = 20;

  public static LiqPoolInitializeData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var lpLiquidityTarget = getInt64LE(_data, i);
    i += 8;
    final var lpMaxFee = Fee.read(_data, i);
    i += lpMaxFee.l();
    final var lpMinFee = Fee.read(_data, i);
    i += lpMinFee.l();
    final var lpTreasuryCut = Fee.read(_data, i);
    return new LiqPoolInitializeData(lpLiquidityTarget,
                                     lpMaxFee,
                                     lpMinFee,
                                     lpTreasuryCut);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, lpLiquidityTarget);
    i += 8;
    i += lpMaxFee.write(_data, i);
    i += lpMinFee.write(_data, i);
    i += lpTreasuryCut.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

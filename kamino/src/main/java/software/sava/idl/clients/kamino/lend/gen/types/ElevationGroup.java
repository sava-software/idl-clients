package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

/// @param debtReserve Mandatory debt reserve for this elevation group
public record ElevationGroup(int maxLiquidationBonusBps,
                             int id,
                             int ltvPct,
                             int liquidationThresholdPct,
                             int allowNewLoans,
                             int maxReservesAsCollateral,
                             int padding0,
                             PublicKey debtReserve,
                             long[] padding1) implements Borsh {

  public static final int BYTES = 72;
  public static final int PADDING_1_LEN = 4;

  public static ElevationGroup read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var maxLiquidationBonusBps = getInt16LE(_data, i);
    i += 2;
    final var id = _data[i] & 0xFF;
    ++i;
    final var ltvPct = _data[i] & 0xFF;
    ++i;
    final var liquidationThresholdPct = _data[i] & 0xFF;
    ++i;
    final var allowNewLoans = _data[i] & 0xFF;
    ++i;
    final var maxReservesAsCollateral = _data[i] & 0xFF;
    ++i;
    final var padding0 = _data[i] & 0xFF;
    ++i;
    final var debtReserve = readPubKey(_data, i);
    i += 32;
    final var padding1 = new long[4];
    Borsh.readArray(padding1, _data, i);
    return new ElevationGroup(maxLiquidationBonusBps,
                              id,
                              ltvPct,
                              liquidationThresholdPct,
                              allowNewLoans,
                              maxReservesAsCollateral,
                              padding0,
                              debtReserve,
                              padding1);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt16LE(_data, i, maxLiquidationBonusBps);
    i += 2;
    _data[i] = (byte) id;
    ++i;
    _data[i] = (byte) ltvPct;
    ++i;
    _data[i] = (byte) liquidationThresholdPct;
    ++i;
    _data[i] = (byte) allowNewLoans;
    ++i;
    _data[i] = (byte) maxReservesAsCollateral;
    ++i;
    _data[i] = (byte) padding0;
    ++i;
    debtReserve.write(_data, i);
    i += 32;
    i += Borsh.writeArrayChecked(padding1, 4, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.drift.vaults.gen.types;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record ManagerUpdateFeesParams(long timelockDuration,
                                      OptionalLong newManagementFee,
                                      OptionalInt newProfitShare,
                                      OptionalInt newHurdleRate) implements Borsh {

  public static ManagerUpdateFeesParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var timelockDuration = getInt64LE(_data, i);
    i += 8;
    final OptionalLong newManagementFee;
    if (_data[i] == 0) {
      newManagementFee = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      newManagementFee = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalInt newProfitShare;
    if (_data[i] == 0) {
      newProfitShare = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      newProfitShare = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalInt newHurdleRate;
    if (_data[i] == 0) {
      newHurdleRate = OptionalInt.empty();
    } else {
      ++i;
      newHurdleRate = OptionalInt.of(getInt32LE(_data, i));
    }
    return new ManagerUpdateFeesParams(timelockDuration,
                                       newManagementFee,
                                       newProfitShare,
                                       newHurdleRate);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, timelockDuration);
    i += 8;
    i += Borsh.writeOptional(newManagementFee, _data, i);
    i += Borsh.writeOptional(newProfitShare, _data, i);
    i += Borsh.writeOptional(newHurdleRate, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + (newManagementFee == null || newManagementFee.isEmpty() ? 1 : (1 + 8)) + (newProfitShare == null || newProfitShare.isEmpty() ? 1 : (1 + 4)) + (newHurdleRate == null || newHurdleRate.isEmpty() ? 1 : (1 + 4));
  }
}

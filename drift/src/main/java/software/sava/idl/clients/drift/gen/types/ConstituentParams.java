package software.sava.idl.clients.drift.gen.types;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record ConstituentParams(OptionalLong maxWeightDeviation,
                                OptionalLong swapFeeMin,
                                OptionalLong swapFeeMax,
                                OptionalLong maxBorrowTokenAmount,
                                OptionalLong oracleStalenessThreshold,
                                OptionalInt costToTradeBps,
                                OptionalInt constituentDerivativeIndex,
                                OptionalLong derivativeWeight,
                                OptionalLong volatility,
                                OptionalInt gammaExecution,
                                OptionalInt gammaInventory,
                                OptionalInt xi) implements Borsh {

  public static ConstituentParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final OptionalLong maxWeightDeviation;
    if (_data[i] == 0) {
      maxWeightDeviation = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      maxWeightDeviation = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong swapFeeMin;
    if (_data[i] == 0) {
      swapFeeMin = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      swapFeeMin = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong swapFeeMax;
    if (_data[i] == 0) {
      swapFeeMax = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      swapFeeMax = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong maxBorrowTokenAmount;
    if (_data[i] == 0) {
      maxBorrowTokenAmount = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      maxBorrowTokenAmount = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong oracleStalenessThreshold;
    if (_data[i] == 0) {
      oracleStalenessThreshold = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      oracleStalenessThreshold = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalInt costToTradeBps;
    if (_data[i] == 0) {
      costToTradeBps = OptionalInt.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      costToTradeBps = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalInt constituentDerivativeIndex;
    if (_data[i] == 0) {
      constituentDerivativeIndex = OptionalInt.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      constituentDerivativeIndex = OptionalInt.of(getInt16LE(_data, i));
      i += 2;
    }
    final OptionalLong derivativeWeight;
    if (_data[i] == 0) {
      derivativeWeight = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      derivativeWeight = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong volatility;
    if (_data[i] == 0) {
      volatility = OptionalLong.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      volatility = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalInt gammaExecution;
    if (_data[i] == 0) {
      gammaExecution = OptionalInt.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      gammaExecution = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalInt gammaInventory;
    if (_data[i] == 0) {
      gammaInventory = OptionalInt.empty();
      ++i;
    ;
    } else {
      ++i;
    ;
      gammaInventory = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalInt xi;
    if (_data[i] == 0) {
      xi = OptionalInt.empty();
    } else {
      ++i;
    ;
      xi = OptionalInt.of(_data[i] & 0xFF);
    }
    return new ConstituentParams(maxWeightDeviation,
                                 swapFeeMin,
                                 swapFeeMax,
                                 maxBorrowTokenAmount,
                                 oracleStalenessThreshold,
                                 costToTradeBps,
                                 constituentDerivativeIndex,
                                 derivativeWeight,
                                 volatility,
                                 gammaExecution,
                                 gammaInventory,
                                 xi);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeOptional(maxWeightDeviation, _data, i);
    i += Borsh.writeOptional(swapFeeMin, _data, i);
    i += Borsh.writeOptional(swapFeeMax, _data, i);
    i += Borsh.writeOptional(maxBorrowTokenAmount, _data, i);
    i += Borsh.writeOptional(oracleStalenessThreshold, _data, i);
    i += Borsh.writeOptional(costToTradeBps, _data, i);
    i += Borsh.writeOptionalshort(constituentDerivativeIndex, _data, i);
    i += Borsh.writeOptional(derivativeWeight, _data, i);
    i += Borsh.writeOptional(volatility, _data, i);
    i += Borsh.writeOptionalbyte(gammaExecution, _data, i);
    i += Borsh.writeOptionalbyte(gammaInventory, _data, i);
    i += Borsh.writeOptionalbyte(xi, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (maxWeightDeviation == null || maxWeightDeviation.isEmpty() ? 1 : (1 + 8))
         + (swapFeeMin == null || swapFeeMin.isEmpty() ? 1 : (1 + 8))
         + (swapFeeMax == null || swapFeeMax.isEmpty() ? 1 : (1 + 8))
         + (maxBorrowTokenAmount == null || maxBorrowTokenAmount.isEmpty() ? 1 : (1 + 8))
         + (oracleStalenessThreshold == null || oracleStalenessThreshold.isEmpty() ? 1 : (1 + 8))
         + (costToTradeBps == null || costToTradeBps.isEmpty() ? 1 : (1 + 4))
         + (constituentDerivativeIndex == null || constituentDerivativeIndex.isEmpty() ? 1 : (1 + 2))
         + (derivativeWeight == null || derivativeWeight.isEmpty() ? 1 : (1 + 8))
         + (volatility == null || volatility.isEmpty() ? 1 : (1 + 8))
         + (gammaExecution == null || gammaExecution.isEmpty() ? 1 : (1 + 1))
         + (gammaInventory == null || gammaInventory.isEmpty() ? 1 : (1 + 1))
         + (xi == null || xi.isEmpty() ? 1 : (1 + 1));
  }
}

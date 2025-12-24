package software.sava.idl.clients.drift.gen.types;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

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
                                OptionalInt xi) implements SerDe {

  public static ConstituentParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final OptionalLong maxWeightDeviation;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      maxWeightDeviation = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      maxWeightDeviation = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong swapFeeMin;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      swapFeeMin = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      swapFeeMin = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong swapFeeMax;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      swapFeeMax = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      swapFeeMax = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong maxBorrowTokenAmount;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      maxBorrowTokenAmount = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      maxBorrowTokenAmount = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong oracleStalenessThreshold;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      oracleStalenessThreshold = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      oracleStalenessThreshold = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalInt costToTradeBps;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      costToTradeBps = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      costToTradeBps = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalInt constituentDerivativeIndex;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      constituentDerivativeIndex = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      constituentDerivativeIndex = OptionalInt.of(getInt16LE(_data, i));
      i += 2;
    }
    final OptionalLong derivativeWeight;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      derivativeWeight = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      derivativeWeight = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong volatility;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      volatility = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      volatility = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalInt gammaExecution;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      gammaExecution = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      gammaExecution = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalInt gammaInventory;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      gammaInventory = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      gammaInventory = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalInt xi;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      xi = OptionalInt.empty();
    } else {
      ++i;
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
    i += SerDeUtil.writeOptional(1, maxWeightDeviation, _data, i);
    i += SerDeUtil.writeOptional(1, swapFeeMin, _data, i);
    i += SerDeUtil.writeOptional(1, swapFeeMax, _data, i);
    i += SerDeUtil.writeOptional(1, maxBorrowTokenAmount, _data, i);
    i += SerDeUtil.writeOptional(1, oracleStalenessThreshold, _data, i);
    i += SerDeUtil.writeOptional(1, costToTradeBps, _data, i);
    i += SerDeUtil.writeOptionalshort(1, constituentDerivativeIndex, _data, i);
    i += SerDeUtil.writeOptional(1, derivativeWeight, _data, i);
    i += SerDeUtil.writeOptional(1, volatility, _data, i);
    i += SerDeUtil.writeOptionalbyte(1, gammaExecution, _data, i);
    i += SerDeUtil.writeOptionalbyte(1, gammaInventory, _data, i);
    i += SerDeUtil.writeOptionalbyte(1, xi, _data, i);
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

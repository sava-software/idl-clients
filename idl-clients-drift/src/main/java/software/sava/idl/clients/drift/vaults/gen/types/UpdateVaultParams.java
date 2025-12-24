package software.sava.idl.clients.drift.vaults.gen.types;

import java.lang.Boolean;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record UpdateVaultParams(OptionalLong redeemPeriod,
                                OptionalLong maxTokens,
                                OptionalLong managementFee,
                                OptionalLong minDepositAmount,
                                OptionalInt profitShare,
                                OptionalInt hurdleRate,
                                Boolean permissioned) implements SerDe {

  public static UpdateVaultParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final OptionalLong redeemPeriod;
    if (_data[i] == 0) {
      redeemPeriod = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      redeemPeriod = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong maxTokens;
    if (_data[i] == 0) {
      maxTokens = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      maxTokens = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong managementFee;
    if (_data[i] == 0) {
      managementFee = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      managementFee = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong minDepositAmount;
    if (_data[i] == 0) {
      minDepositAmount = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      minDepositAmount = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalInt profitShare;
    if (_data[i] == 0) {
      profitShare = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      profitShare = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalInt hurdleRate;
    if (_data[i] == 0) {
      hurdleRate = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      hurdleRate = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final Boolean permissioned;
    if (_data[i] == 0) {
      permissioned = null;
    } else {
      ++i;
      permissioned = _data[i] == 1;
    }
    return new UpdateVaultParams(redeemPeriod,
                                 maxTokens,
                                 managementFee,
                                 minDepositAmount,
                                 profitShare,
                                 hurdleRate,
                                 permissioned);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeOptional(1, redeemPeriod, _data, i);
    i += SerDeUtil.writeOptional(1, maxTokens, _data, i);
    i += SerDeUtil.writeOptional(1, managementFee, _data, i);
    i += SerDeUtil.writeOptional(1, minDepositAmount, _data, i);
    i += SerDeUtil.writeOptional(1, profitShare, _data, i);
    i += SerDeUtil.writeOptional(1, hurdleRate, _data, i);
    i += SerDeUtil.writeOptional(1, permissioned, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (redeemPeriod == null || redeemPeriod.isEmpty() ? 1 : (1 + 8))
         + (maxTokens == null || maxTokens.isEmpty() ? 1 : (1 + 8))
         + (managementFee == null || managementFee.isEmpty() ? 1 : (1 + 8))
         + (minDepositAmount == null || minDepositAmount.isEmpty() ? 1 : (1 + 8))
         + (profitShare == null || profitShare.isEmpty() ? 1 : (1 + 4))
         + (hurdleRate == null || hurdleRate.isEmpty() ? 1 : (1 + 4))
         + (permissioned == null ? 1 : (1 + 1));
  }
}

package software.sava.idl.clients.drift.gen.types;

import java.math.BigInteger;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record LpPoolParams(OptionalLong maxSettleQuoteAmount,
                           OptionalLong volatility,
                           OptionalInt gammaExecution,
                           OptionalInt xi,
                           BigInteger maxAum,
                           PublicKey whitelistMint) implements SerDe {

  public static LpPoolParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final OptionalLong maxSettleQuoteAmount;
    if (_data[i] == 0) {
      maxSettleQuoteAmount = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      maxSettleQuoteAmount = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalLong volatility;
    if (_data[i] == 0) {
      volatility = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      volatility = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalInt gammaExecution;
    if (_data[i] == 0) {
      gammaExecution = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      gammaExecution = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalInt xi;
    if (_data[i] == 0) {
      xi = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      xi = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final BigInteger maxAum;
    if (_data[i] == 0) {
      maxAum = null;
      ++i;
    } else {
      ++i;
      maxAum = getInt128LE(_data, i);
      i += 16;
    }
    final PublicKey whitelistMint;
    if (_data[i] == 0) {
      whitelistMint = null;
    } else {
      ++i;
      whitelistMint = readPubKey(_data, i);
    }
    return new LpPoolParams(maxSettleQuoteAmount,
                            volatility,
                            gammaExecution,
                            xi,
                            maxAum,
                            whitelistMint);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeOptional(1, maxSettleQuoteAmount, _data, i);
    i += SerDeUtil.writeOptional(1, volatility, _data, i);
    i += SerDeUtil.writeOptionalbyte(1, gammaExecution, _data, i);
    i += SerDeUtil.writeOptionalbyte(1, xi, _data, i);
    i += SerDeUtil.write128Optional(1, maxAum, _data, i);
    i += SerDeUtil.writeOptional(1, whitelistMint, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (maxSettleQuoteAmount == null || maxSettleQuoteAmount.isEmpty() ? 1 : (1 + 8))
         + (volatility == null || volatility.isEmpty() ? 1 : (1 + 8))
         + (gammaExecution == null || gammaExecution.isEmpty() ? 1 : (1 + 1))
         + (xi == null || xi.isEmpty() ? 1 : (1 + 1))
         + (maxAum == null ? 1 : (1 + 16))
         + (whitelistMint == null ? 1 : (1 + 32));
  }
}

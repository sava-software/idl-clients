package software.sava.idl.clients.drift.gen.types;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

public record PerpBankruptcyRecord(int marketIndex,
                                   BigInteger pnl,
                                   BigInteger ifPayment,
                                   PublicKey clawbackUser,
                                   BigInteger clawbackUserPayment,
                                   BigInteger cumulativeFundingRateDelta) implements Borsh {

  public static PerpBankruptcyRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var pnl = getInt128LE(_data, i);
    i += 16;
    final var ifPayment = getInt128LE(_data, i);
    i += 16;
    final PublicKey clawbackUser;
    if (_data[i] == 0) {
      clawbackUser = null;
      ++i;
    } else {
      ++i;
      clawbackUser = readPubKey(_data, i);
      i += 32;
    }
    final BigInteger clawbackUserPayment;
    if (_data[i] == 0) {
      clawbackUserPayment = null;
      ++i;
    } else {
      ++i;
      clawbackUserPayment = getInt128LE(_data, i);
      i += 16;
    }
    final var cumulativeFundingRateDelta = getInt128LE(_data, i);
    return new PerpBankruptcyRecord(marketIndex,
                                    pnl,
                                    ifPayment,
                                    clawbackUser,
                                    clawbackUserPayment,
                                    cumulativeFundingRateDelta);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt128LE(_data, i, pnl);
    i += 16;
    putInt128LE(_data, i, ifPayment);
    i += 16;
    i += Borsh.writeOptional(clawbackUser, _data, i);
    i += Borsh.write128Optional(clawbackUserPayment, _data, i);
    putInt128LE(_data, i, cumulativeFundingRateDelta);
    i += 16;
    return i - _offset;
  }

  @Override
  public int l() {
    return 2
         + 16
         + 16
         + (clawbackUser == null ? 1 : (1 + 32))
         + (clawbackUserPayment == null ? 1 : (1 + 16))
         + 16;
  }
}

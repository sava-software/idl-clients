package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;

/// Upcasted signed quote lots per base lot wrapper.
///
public record SignedQuoteLotsPerBaseLotUpcasted(BigInteger inner) implements SerDe {

  public static final int BYTES = 16;

  public static final int INNER_OFFSET = 0;

  public static SignedQuoteLotsPerBaseLotUpcasted read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var inner = getInt128LE(_data, _offset);
    return new SignedQuoteLotsPerBaseLotUpcasted(inner);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt128LE(_data, i, inner);
    i += 16;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

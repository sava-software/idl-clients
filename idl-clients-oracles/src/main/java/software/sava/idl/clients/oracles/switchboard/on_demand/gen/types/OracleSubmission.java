package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param oracle The public key of the oracle that submitted this value.
/// @param slot The slot at which this value was signed.
/// @param landedAt The slot at which this value was landed on chain.
/// @param value The value that was submitted.
public record OracleSubmission(PublicKey oracle,
                               long slot,
                               long landedAt,
                               BigInteger value) implements SerDe {

  public static final int BYTES = 64;

  public static OracleSubmission read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var oracle = readPubKey(_data, i);
    i += 32;
    final var slot = getInt64LE(_data, i);
    i += 8;
    final var landedAt = getInt64LE(_data, i);
    i += 8;
    final var value = getInt128LE(_data, i);
    return new OracleSubmission(oracle,
                                slot,
                                landedAt,
                                value);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    oracle.write(_data, i);
    i += 32;
    putInt64LE(_data, i, slot);
    i += 8;
    putInt64LE(_data, i, landedAt);
    i += 8;
    putInt128LE(_data, i, value);
    i += 16;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

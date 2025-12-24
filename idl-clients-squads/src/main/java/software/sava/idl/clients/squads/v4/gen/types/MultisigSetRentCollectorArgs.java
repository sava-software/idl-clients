package software.sava.idl.clients.squads.v4.gen.types;

import java.lang.String;

import java.util.Arrays;

import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;

/// @param memo Memo is used for indexing only.
public record MultisigSetRentCollectorArgs(PublicKey rentCollector, String memo, byte[] _memo) implements SerDe {

  public static MultisigSetRentCollectorArgs createRecord(final PublicKey rentCollector, final String memo) {
    return new MultisigSetRentCollectorArgs(rentCollector, memo, memo == null ? null : memo.getBytes(UTF_8));
  }

  public static MultisigSetRentCollectorArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final PublicKey rentCollector;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      rentCollector = null;
      ++i;
    } else {
      ++i;
      rentCollector = readPubKey(_data, i);
      i += 32;
    }
    final byte[] _memo;
    final String memo;
    if (_data[i] == 0) {
      _memo = null;
      memo = null;
    } else {
      int _from = i + 1;
      final int _memoLength = ByteUtil.getInt32LE(_data, _from);
      _from += 4;
      _memo = Arrays.copyOfRange(_data, _from, _from + _memoLength);
      memo = new String(_memo);
    }

    return new MultisigSetRentCollectorArgs(rentCollector, memo, _memo);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeOptional(1, rentCollector, _data, i);
    i += SerDeUtil.writeOptionalVector(1, 4, _memo, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (rentCollector == null ? 1 : (1 + 32)) + (_memo == null || _memo.length == 0 ? 1 : (1 + _memo.length));
  }
}

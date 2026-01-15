package software.sava.idl.clients.squads.v4.gen.types;

import java.lang.String;

import java.util.Arrays;

import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

/// @param memo Memo is used for indexing only.
public record MultisigSetTimeLockArgs(int timeLock, String memo, byte[] _memo) implements SerDe {

  public static final int TIME_LOCK_OFFSET = 0;
  public static final int MEMO_OFFSET = 5;

  public static MultisigSetTimeLockArgs createRecord(final int timeLock, final String memo) {
    return new MultisigSetTimeLockArgs(timeLock, memo, memo == null ? null : memo.getBytes(UTF_8));
  }

  public static MultisigSetTimeLockArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var timeLock = getInt32LE(_data, i);
    i += 4;
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

    return new MultisigSetTimeLockArgs(timeLock, memo, _memo);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, timeLock);
    i += 4;
    i += SerDeUtil.writeOptionalVector(1, 4, _memo, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 4 + (_memo == null || _memo.length == 0 ? 1 : (1 + _memo.length));
  }
}

package software.sava.idl.clients.squads.v4.gen.types;

import java.lang.String;

import java.util.Arrays;

import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.encoding.ByteUtil.getInt32LE;

/// @param memo Memo is used for indexing only.
public record MultisigAddMemberArgs(Member newMember, String memo, byte[] _memo) implements SerDe {

  public static MultisigAddMemberArgs createRecord(final Member newMember, final String memo) {
    return new MultisigAddMemberArgs(newMember, memo, memo == null ? null : memo.getBytes(UTF_8));
  }

  public static MultisigAddMemberArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var newMember = Member.read(_data, i);
    i += newMember.l();
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

    return new MultisigAddMemberArgs(newMember, memo, _memo);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += newMember.write(_data, i);
    i += SerDeUtil.writeOptionalVector(1, 4, _memo, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return newMember.l() + (_memo == null || _memo.length == 0 ? 1 : (1 + _memo.length));
  }
}

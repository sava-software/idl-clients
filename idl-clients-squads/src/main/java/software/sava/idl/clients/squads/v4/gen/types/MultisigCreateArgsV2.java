package software.sava.idl.clients.squads.v4.gen.types;

import java.lang.String;

import java.util.Arrays;

import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

/// @param configAuthority The authority that can configure the multisig: add/remove members, change the threshold, etc.
///                        Should be set to `None` for autonomous multisigs.
/// @param threshold The number of signatures required to execute a transaction.
/// @param members The members of the multisig.
/// @param timeLock How many seconds must pass between transaction voting, settlement, and execution.
/// @param rentCollector The address where the rent for the accounts related to executed, rejected, or cancelled
///                      transactions can be reclaimed. If set to `None`, the rent reclamation feature is turned off.
/// @param memo Memo is used for indexing only.
public record MultisigCreateArgsV2(PublicKey configAuthority,
                                   int threshold,
                                   Member[] members,
                                   int timeLock,
                                   PublicKey rentCollector,
                                   String memo, byte[] _memo) implements SerDe {

  public static final int CONFIG_AUTHORITY_OFFSET = 1;

  public static MultisigCreateArgsV2 createRecord(final PublicKey configAuthority,
                                                  final int threshold,
                                                  final Member[] members,
                                                  final int timeLock,
                                                  final PublicKey rentCollector,
                                                  final String memo) {
    return new MultisigCreateArgsV2(configAuthority,
                                    threshold,
                                    members,
                                    timeLock,
                                    rentCollector,
                                    memo, memo == null ? null : memo.getBytes(UTF_8));
  }

  public static MultisigCreateArgsV2 read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final PublicKey configAuthority;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      configAuthority = null;
      ++i;
    } else {
      ++i;
      configAuthority = readPubKey(_data, i);
      i += 32;
    }
    final var threshold = getInt16LE(_data, i);
    i += 2;
    final var members = SerDeUtil.readVector(4, Member.class, Member::read, _data, i);
    i += SerDeUtil.lenVector(4, members);
    final var timeLock = getInt32LE(_data, i);
    i += 4;
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

    return new MultisigCreateArgsV2(configAuthority,
                                    threshold,
                                    members,
                                    timeLock,
                                    rentCollector,
                                    memo, _memo);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeOptional(1, configAuthority, _data, i);
    putInt16LE(_data, i, threshold);
    i += 2;
    i += SerDeUtil.writeVector(4, members, _data, i);
    putInt32LE(_data, i, timeLock);
    i += 4;
    i += SerDeUtil.writeOptional(1, rentCollector, _data, i);
    i += SerDeUtil.writeOptionalVector(1, 4, _memo, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (configAuthority == null ? 1 : (1 + 32))
         + 2
         + SerDeUtil.lenVector(4, members)
         + 4
         + (rentCollector == null ? 1 : (1 + 32))
         + (_memo == null || _memo.length == 0 ? 1 : (1 + _memo.length));
  }
}

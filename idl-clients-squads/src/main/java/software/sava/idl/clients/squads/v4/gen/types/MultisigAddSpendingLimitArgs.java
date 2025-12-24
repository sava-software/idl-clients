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
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param createKey Key that is used to seed the SpendingLimit PDA.
/// @param vaultIndex The index of the vault that the spending limit is for.
/// @param mint The token mint the spending limit is for.
/// @param amount The amount of tokens that can be spent in a period.
///               This amount is in decimals of the mint,
///               so 1 SOL would be `1_000_000_000` and 1 USDC would be `1_000_000`.
/// @param period The reset period of the spending limit.
///               When it passes, the remaining amount is reset, unless it's `Period::OneTime`.
/// @param members Members of the multisig that can use the spending limit.
///                In case a member is removed from the multisig, the spending limit will remain existent
///                (until explicitly deleted), but the removed member will not be able to use it anymore.
/// @param destinations The destination addresses the spending limit is allowed to sent funds to.
///                     If empty, funds can be sent to any address.
/// @param memo Memo is used for indexing only.
public record MultisigAddSpendingLimitArgs(PublicKey createKey,
                                           int vaultIndex,
                                           PublicKey mint,
                                           long amount,
                                           Period period,
                                           PublicKey[] members,
                                           PublicKey[] destinations,
                                           String memo, byte[] _memo) implements SerDe {

  public static MultisigAddSpendingLimitArgs createRecord(final PublicKey createKey,
                                                          final int vaultIndex,
                                                          final PublicKey mint,
                                                          final long amount,
                                                          final Period period,
                                                          final PublicKey[] members,
                                                          final PublicKey[] destinations,
                                                          final String memo) {
    return new MultisigAddSpendingLimitArgs(createKey,
                                            vaultIndex,
                                            mint,
                                            amount,
                                            period,
                                            members,
                                            destinations,
                                            memo, memo == null ? null : memo.getBytes(UTF_8));
  }

  public static MultisigAddSpendingLimitArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var createKey = readPubKey(_data, i);
    i += 32;
    final var vaultIndex = _data[i] & 0xFF;
    ++i;
    final var mint = readPubKey(_data, i);
    i += 32;
    final var amount = getInt64LE(_data, i);
    i += 8;
    final var period = Period.read(_data, i);
    i += period.l();
    final var members = SerDeUtil.readPublicKeyVector(4, _data, i);
    i += SerDeUtil.lenVector(4, members);
    final var destinations = SerDeUtil.readPublicKeyVector(4, _data, i);
    i += SerDeUtil.lenVector(4, destinations);
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

    return new MultisigAddSpendingLimitArgs(createKey,
                                            vaultIndex,
                                            mint,
                                            amount,
                                            period,
                                            members,
                                            destinations,
                                            memo, _memo);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    createKey.write(_data, i);
    i += 32;
    _data[i] = (byte) vaultIndex;
    ++i;
    mint.write(_data, i);
    i += 32;
    putInt64LE(_data, i, amount);
    i += 8;
    i += period.write(_data, i);
    i += SerDeUtil.writeVector(4, members, _data, i);
    i += SerDeUtil.writeVector(4, destinations, _data, i);
    i += SerDeUtil.writeOptionalVector(1, 4, _memo, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 32
         + 1
         + 32
         + 8
         + period.l()
         + SerDeUtil.lenVector(4, members)
         + SerDeUtil.lenVector(4, destinations)
         + (_memo == null || _memo.length == 0 ? 1 : (1 + _memo.length));
  }
}

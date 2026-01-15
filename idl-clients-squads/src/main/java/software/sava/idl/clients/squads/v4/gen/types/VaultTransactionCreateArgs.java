package software.sava.idl.clients.squads.v4.gen.types;

import java.lang.String;

import java.util.Arrays;

import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.encoding.ByteUtil.getInt32LE;

/// @param vaultIndex Index of the vault this transaction belongs to.
/// @param ephemeralSigners Number of ephemeral signing PDAs required by the transaction.
public record VaultTransactionCreateArgs(int vaultIndex,
                                         int ephemeralSigners,
                                         byte[] transactionMessage,
                                         String memo, byte[] _memo) implements SerDe {

  public static final int VAULT_INDEX_OFFSET = 0;
  public static final int EPHEMERAL_SIGNERS_OFFSET = 1;
  public static final int TRANSACTION_MESSAGE_OFFSET = 2;

  public static VaultTransactionCreateArgs createRecord(final int vaultIndex,
                                                        final int ephemeralSigners,
                                                        final byte[] transactionMessage,
                                                        final String memo) {
    return new VaultTransactionCreateArgs(vaultIndex,
                                          ephemeralSigners,
                                          transactionMessage,
                                          memo, memo == null ? null : memo.getBytes(UTF_8));
  }

  public static VaultTransactionCreateArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var vaultIndex = _data[i] & 0xFF;
    ++i;
    final var ephemeralSigners = _data[i] & 0xFF;
    ++i;
    final var transactionMessage = SerDeUtil.readbyteVector(4, _data, i);
    i += SerDeUtil.lenVector(4, transactionMessage);
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

    return new VaultTransactionCreateArgs(vaultIndex,
                                          ephemeralSigners,
                                          transactionMessage,
                                          memo, _memo);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) vaultIndex;
    ++i;
    _data[i] = (byte) ephemeralSigners;
    ++i;
    i += SerDeUtil.writeVector(4, transactionMessage, _data, i);
    i += SerDeUtil.writeOptionalVector(1, 4, _memo, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 1 + 1 + SerDeUtil.lenVector(4, transactionMessage) + (_memo == null || _memo.length == 0 ? 1 : (1 + _memo.length));
  }
}

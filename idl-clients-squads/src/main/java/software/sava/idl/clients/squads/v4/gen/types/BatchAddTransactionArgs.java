package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// @param ephemeralSigners Number of ephemeral signing PDAs required by the transaction.
public record BatchAddTransactionArgs(int ephemeralSigners, byte[] transactionMessage) implements SerDe {

  public static final int EPHEMERAL_SIGNERS_OFFSET = 0;
  public static final int TRANSACTION_MESSAGE_OFFSET = 1;

  public static BatchAddTransactionArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var ephemeralSigners = _data[i] & 0xFF;
    ++i;
    final var transactionMessage = SerDeUtil.readbyteVector(4, _data, i);
    return new BatchAddTransactionArgs(ephemeralSigners, transactionMessage);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) ephemeralSigners;
    ++i;
    i += SerDeUtil.writeVector(4, transactionMessage, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 1 + SerDeUtil.lenVector(4, transactionMessage);
  }
}

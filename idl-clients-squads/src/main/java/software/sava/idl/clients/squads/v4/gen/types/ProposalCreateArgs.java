package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param transactionIndex Index of the multisig transaction this proposal is associated with.
/// @param draft Whether the proposal should be initialized with status `Draft`.
public record ProposalCreateArgs(long transactionIndex, boolean draft) implements SerDe {

  public static final int BYTES = 9;

  public static final int TRANSACTION_INDEX_OFFSET = 0;
  public static final int DRAFT_OFFSET = 8;

  public static ProposalCreateArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var transactionIndex = getInt64LE(_data, i);
    i += 8;
    final var draft = _data[i] == 1;
    return new ProposalCreateArgs(transactionIndex, draft);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, transactionIndex);
    i += 8;
    _data[i] = (byte) (draft ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

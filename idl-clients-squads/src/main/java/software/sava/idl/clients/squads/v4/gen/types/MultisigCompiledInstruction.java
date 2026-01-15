package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Concise serialization schema for instructions that make up a transaction.
/// Closely mimics the Solana transaction wire format.
///
/// @param accountIndexes Indices into the tx's `account_keys` list indicating which accounts to pass to the instruction.
/// @param data Instruction data.
public record MultisigCompiledInstruction(int programIdIndex,
                                          byte[] accountIndexes,
                                          byte[] data) implements SerDe {

  public static final int PROGRAM_ID_INDEX_OFFSET = 0;
  public static final int ACCOUNT_INDEXES_OFFSET = 1;

  public static MultisigCompiledInstruction read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var programIdIndex = _data[i] & 0xFF;
    ++i;
    final var accountIndexes = SerDeUtil.readbyteVector(4, _data, i);
    i += SerDeUtil.lenVector(4, accountIndexes);
    final var data = SerDeUtil.readbyteVector(4, _data, i);
    return new MultisigCompiledInstruction(programIdIndex, accountIndexes, data);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) programIdIndex;
    ++i;
    i += SerDeUtil.writeVector(4, accountIndexes, _data, i);
    i += SerDeUtil.writeVector(4, data, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 1 + SerDeUtil.lenVector(4, accountIndexes) + SerDeUtil.lenVector(4, data);
  }
}

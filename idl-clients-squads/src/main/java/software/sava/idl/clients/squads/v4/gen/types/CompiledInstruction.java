package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// @param accountIndexes Indices into the tx's `account_keys` list indicating which accounts to pass to the instruction.
/// @param data Instruction data.
public record CompiledInstruction(int programIdIndex,
                                  byte[] accountIndexes,
                                  byte[] data) implements SerDe {

  public static CompiledInstruction read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var programIdIndex = _data[i] & 0xFF;
    ++i;
    final var accountIndexes = SerDeUtil.readbyteVector(1, _data, i);
    i += SerDeUtil.lenVector(1, accountIndexes);
    final var data = SerDeUtil.readbyteVector(2, _data, i);
    return new CompiledInstruction(programIdIndex, accountIndexes, data);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) programIdIndex;
    ++i;
    i += SerDeUtil.writeVector(1, accountIndexes, _data, i);
    i += SerDeUtil.writeVector(2, data, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 1 + SerDeUtil.lenVector(1, accountIndexes) + SerDeUtil.lenVector(2, data);
  }
}

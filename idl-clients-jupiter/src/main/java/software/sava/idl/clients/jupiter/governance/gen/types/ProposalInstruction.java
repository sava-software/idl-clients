package software.sava.idl.clients.jupiter.governance.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;

/// Instruction.
///
/// @param programId Pubkey of the instruction processor that executes this instruction
/// @param keys Metadata for what accounts should be passed to the instruction processor
/// @param data Opaque data passed to the instruction processor
public record ProposalInstruction(PublicKey programId,
                                  ProposalAccountMeta[] keys,
                                  byte[] data) implements SerDe {

  public static ProposalInstruction read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var programId = readPubKey(_data, i);
    i += 32;
    final var keys = SerDeUtil.readVector(4, ProposalAccountMeta.class, ProposalAccountMeta::read, _data, i);
    i += SerDeUtil.lenVector(4, keys);
    final var data = SerDeUtil.readbyteVector(4, _data, i);
    return new ProposalInstruction(programId, keys, data);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    programId.write(_data, i);
    i += 32;
    i += SerDeUtil.writeVector(4, keys, _data, i);
    i += SerDeUtil.writeVector(4, data, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 32 + SerDeUtil.lenVector(4, keys) + SerDeUtil.lenVector(4, data);
  }
}

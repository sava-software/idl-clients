package software.sava.idl.clients.jupiter.governance.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.jupiter.governance.gen.types.ProposalInstruction;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ProposalCreateEvent(Discriminator discriminator,
                                  PublicKey governor,
                                  PublicKey proposal,
                                  PublicKey proposer,
                                  int proposalType,
                                  int maxOption,
                                  long index,
                                  ProposalInstruction[] instructions) implements GovernEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(234, 121, 246, 143, 42, 244, 8, 229);

  public static ProposalCreateEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var governor = readPubKey(_data, i);
    i += 32;
    final var proposal = readPubKey(_data, i);
    i += 32;
    final var proposer = readPubKey(_data, i);
    i += 32;
    final var proposalType = _data[i] & 0xFF;
    ++i;
    final var maxOption = _data[i] & 0xFF;
    ++i;
    final var index = getInt64LE(_data, i);
    i += 8;
    final var instructions = SerDeUtil.readVector(4, ProposalInstruction.class, ProposalInstruction::read, _data, i);
    return new ProposalCreateEvent(discriminator,
                                   governor,
                                   proposal,
                                   proposer,
                                   proposalType,
                                   maxOption,
                                   index,
                                   instructions);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    governor.write(_data, i);
    i += 32;
    proposal.write(_data, i);
    i += 32;
    proposer.write(_data, i);
    i += 32;
    _data[i] = (byte) proposalType;
    ++i;
    _data[i] = (byte) maxOption;
    ++i;
    putInt64LE(_data, i, index);
    i += 8;
    i += SerDeUtil.writeVector(4, instructions, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 32
         + 32
         + 32
         + 1
         + 1
         + 8
         + SerDeUtil.lenVector(4, instructions);
  }
}

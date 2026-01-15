package software.sava.idl.clients.jupiter.governance.gen.events;

import java.lang.String;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record OptionProposalMetaCreateEvent(Discriminator discriminator,
                                            PublicKey governor,
                                            PublicKey proposal,
                                            String[] optionDescriptions) implements GovernEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(234, 121, 246, 143, 42, 244, 8, 229);

  public static final int GOVERNOR_OFFSET = 8;
  public static final int PROPOSAL_OFFSET = 40;
  public static final int OPTION_DESCRIPTIONS_OFFSET = 72;

  public static OptionProposalMetaCreateEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var governor = readPubKey(_data, i);
    i += 32;
    final var proposal = readPubKey(_data, i);
    i += 32;
    final var optionDescriptions = SerDeUtil.readStringVector(4, 4, _data, i);
    return new OptionProposalMetaCreateEvent(discriminator, governor, proposal, optionDescriptions);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    governor.write(_data, i);
    i += 32;
    proposal.write(_data, i);
    i += 32;
    i += SerDeUtil.writeVector(4, 4, optionDescriptions, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 32 + 32 + SerDeUtil.lenVector(4, 4, optionDescriptions);
  }
}

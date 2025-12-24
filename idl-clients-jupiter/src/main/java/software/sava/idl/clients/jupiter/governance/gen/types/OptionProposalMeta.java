package software.sava.idl.clients.jupiter.governance.gen.types;

import java.lang.String;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Metadata about an option proposal.
///
/// @param proposal The Proposal.
/// @param optionDescriptions description for options
public record OptionProposalMeta(PublicKey _address, Discriminator discriminator, PublicKey proposal, String[] optionDescriptions) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(200, 56, 229, 124, 113, 154, 32, 26);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int PROPOSAL_OFFSET = 8;
  public static final int OPTION_DESCRIPTIONS_OFFSET = 40;

  public static Filter createProposalFilter(final PublicKey proposal) {
    return Filter.createMemCompFilter(PROPOSAL_OFFSET, proposal);
  }

  public static OptionProposalMeta read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static OptionProposalMeta read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static OptionProposalMeta read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], OptionProposalMeta> FACTORY = OptionProposalMeta::read;

  public static OptionProposalMeta read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var proposal = readPubKey(_data, i);
    i += 32;
    final var optionDescriptions = SerDeUtil.readStringVector(4, 4, _data, i);
    return new OptionProposalMeta(_address, discriminator, proposal, optionDescriptions);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    proposal.write(_data, i);
    i += 32;
    i += SerDeUtil.writeVector(4, 4, optionDescriptions, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32 + SerDeUtil.lenVector(4, 4, optionDescriptions);
  }
}

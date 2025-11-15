package software.sava.idl.clients.jupiter.governance.gen.types;

import java.lang.String;

import java.util.Arrays;
import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Metadata about a proposal.
///
/// @param proposal The Proposal.
/// @param title Title of the proposal.
/// @param descriptionLink Link to a description of the proposal.
public record ProposalMeta(PublicKey _address,
                           Discriminator discriminator,
                           PublicKey proposal,
                           String title, byte[] _title,
                           String descriptionLink, byte[] _descriptionLink) implements Borsh {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(50, 100, 46, 24, 151, 174, 216, 78);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int PROPOSAL_OFFSET = 8;
  public static final int TITLE_OFFSET = 40;

  public static Filter createProposalFilter(final PublicKey proposal) {
    return Filter.createMemCompFilter(PROPOSAL_OFFSET, proposal);
  }

  public static ProposalMeta createRecord(final PublicKey _address,
                                          final Discriminator discriminator,
                                          final PublicKey proposal,
                                          final String title,
                                          final String descriptionLink) {
    return new ProposalMeta(_address, discriminator, proposal, title, title.getBytes(UTF_8), descriptionLink, descriptionLink.getBytes(UTF_8));
  }

  public static ProposalMeta read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static ProposalMeta read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static ProposalMeta read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], ProposalMeta> FACTORY = ProposalMeta::read;

  public static ProposalMeta read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var proposal = readPubKey(_data, i);
    i += 32;
    final int _titleLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _title = Arrays.copyOfRange(_data, i, i + _titleLength);
    final var title = new String(_title, UTF_8);
    i += _title.length;
    final int _descriptionLinkLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _descriptionLink = Arrays.copyOfRange(_data, i, i + _descriptionLinkLength);
    final var descriptionLink = new String(_descriptionLink, UTF_8);
    return new ProposalMeta(_address, discriminator, proposal, title, _title, descriptionLink, _descriptionLink);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    proposal.write(_data, i);
    i += 32;
    i += Borsh.writeVector(_title, _data, i);
    i += Borsh.writeVector(_descriptionLink, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32 + _title.length + _descriptionLink.length;
  }
}

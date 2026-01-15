package software.sava.idl.clients.jupiter.governance.gen.events;

import java.lang.String;

import java.util.Arrays;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ProposalMetaCreateEvent(Discriminator discriminator,
                                      PublicKey governor,
                                      PublicKey proposal,
                                      String title, byte[] _title,
                                      String descriptionLink, byte[] _descriptionLink) implements GovernEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(234, 121, 246, 143, 42, 244, 8, 229);

  public static final int GOVERNOR_OFFSET = 8;
  public static final int PROPOSAL_OFFSET = 40;
  public static final int TITLE_OFFSET = 72;

  public static ProposalMetaCreateEvent createRecord(final Discriminator discriminator,
                                                     final PublicKey governor,
                                                     final PublicKey proposal,
                                                     final String title,
                                                     final String descriptionLink) {
    return new ProposalMetaCreateEvent(discriminator,
                                       governor,
                                       proposal,
                                       title, title == null ? null : title.getBytes(UTF_8),
                                       descriptionLink, descriptionLink == null ? null : descriptionLink.getBytes(UTF_8));
  }

  public static ProposalMetaCreateEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var governor = readPubKey(_data, i);
    i += 32;
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
    return new ProposalMetaCreateEvent(discriminator,
                                       governor,
                                       proposal,
                                       title, title == null ? null : title.getBytes(UTF_8),
                                       descriptionLink, descriptionLink == null ? null : descriptionLink.getBytes(UTF_8));
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    governor.write(_data, i);
    i += 32;
    proposal.write(_data, i);
    i += 32;
    i += SerDeUtil.writeVector(4, _title, _data, i);
    i += SerDeUtil.writeVector(4, _descriptionLink, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 32 + 32 + _title.length + _descriptionLink.length;
  }
}

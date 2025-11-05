package software.sava.idl.clients.jupiter.governance.gen.events;

import java.lang.String;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;

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

  public static ProposalMetaCreateEvent createRecord(final Discriminator discriminator,
                                                     final PublicKey governor,
                                                     final PublicKey proposal,
                                                     final String title,
                                                     final String descriptionLink) {
    return new ProposalMetaCreateEvent(discriminator,
                                       governor,
                                       proposal,
                                       title, title.getBytes(UTF_8),
                                       descriptionLink, descriptionLink.getBytes(UTF_8));
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
    final var title = Borsh.string(_data, i);
    i += (Integer.BYTES + getInt32LE(_data, i));
    final var descriptionLink = Borsh.string(_data, i);
    return new ProposalMetaCreateEvent(discriminator,
                                       governor,
                                       proposal,
                                       title, title.getBytes(UTF_8),
                                       descriptionLink, descriptionLink.getBytes(UTF_8));
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    governor.write(_data, i);
    i += 32;
    proposal.write(_data, i);
    i += 32;
    i += Borsh.writeVector(_title, _data, i);
    i += Borsh.writeVector(_descriptionLink, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 32 + 32 + Borsh.lenVector(_title) + Borsh.lenVector(_descriptionLink);
  }
}

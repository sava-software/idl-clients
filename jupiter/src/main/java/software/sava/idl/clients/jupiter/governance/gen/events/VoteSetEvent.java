package software.sava.idl.clients.jupiter.governance.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record VoteSetEvent(Discriminator discriminator,
                           PublicKey governor,
                           PublicKey proposal,
                           PublicKey voter,
                           PublicKey vote,
                           int side,
                           long votingPower) implements GovernEvent {

  public static final int BYTES = 145;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(234, 121, 246, 143, 42, 244, 8, 229);

  public static VoteSetEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var governor = readPubKey(_data, i);
    i += 32;
    final var proposal = readPubKey(_data, i);
    i += 32;
    final var voter = readPubKey(_data, i);
    i += 32;
    final var vote = readPubKey(_data, i);
    i += 32;
    final var side = _data[i] & 0xFF;
    ++i;
    final var votingPower = getInt64LE(_data, i);
    return new VoteSetEvent(discriminator,
                            governor,
                            proposal,
                            voter,
                            vote,
                            side,
                            votingPower);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    governor.write(_data, i);
    i += 32;
    proposal.write(_data, i);
    i += 32;
    voter.write(_data, i);
    i += 32;
    vote.write(_data, i);
    i += 32;
    _data[i] = (byte) side;
    ++i;
    putInt64LE(_data, i, votingPower);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

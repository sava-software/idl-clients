package software.sava.idl.clients.jupiter.governance.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// A Vote is a vote made by a `voter`
///
/// @param proposal The proposal being voted on.
/// @param voter The voter.
/// @param bump Bump seed
/// @param side The side of the vote taken.
/// @param votingPower The number of votes this vote holds.
/// @param claimed Flag to check whether voter has claim the reward or not
/// @param buffers buffers for future use
public record Vote(PublicKey _address,
                   Discriminator discriminator,
                   PublicKey proposal,
                   PublicKey voter,
                   int bump,
                   int side,
                   long votingPower,
                   boolean claimed,
                   byte[] buffers) implements SerDe {

  public static final int BYTES = 115;
  public static final int BUFFERS_LEN = 32;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(96, 91, 104, 57, 145, 35, 172, 155);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int PROPOSAL_OFFSET = 8;
  public static final int VOTER_OFFSET = 40;
  public static final int BUMP_OFFSET = 72;
  public static final int SIDE_OFFSET = 73;
  public static final int VOTING_POWER_OFFSET = 74;
  public static final int CLAIMED_OFFSET = 82;
  public static final int BUFFERS_OFFSET = 83;

  public static Filter createProposalFilter(final PublicKey proposal) {
    return Filter.createMemCompFilter(PROPOSAL_OFFSET, proposal);
  }

  public static Filter createVoterFilter(final PublicKey voter) {
    return Filter.createMemCompFilter(VOTER_OFFSET, voter);
  }

  public static Filter createBumpFilter(final int bump) {
    return Filter.createMemCompFilter(BUMP_OFFSET, new byte[]{(byte) bump});
  }

  public static Filter createSideFilter(final int side) {
    return Filter.createMemCompFilter(SIDE_OFFSET, new byte[]{(byte) side});
  }

  public static Filter createVotingPowerFilter(final long votingPower) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, votingPower);
    return Filter.createMemCompFilter(VOTING_POWER_OFFSET, _data);
  }

  public static Filter createClaimedFilter(final boolean claimed) {
    return Filter.createMemCompFilter(CLAIMED_OFFSET, new byte[]{(byte) (claimed ? 1 : 0)});
  }

  public static Vote read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Vote read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Vote read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Vote> FACTORY = Vote::read;

  public static Vote read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var proposal = readPubKey(_data, i);
    i += 32;
    final var voter = readPubKey(_data, i);
    i += 32;
    final var bump = _data[i] & 0xFF;
    ++i;
    final var side = _data[i] & 0xFF;
    ++i;
    final var votingPower = getInt64LE(_data, i);
    i += 8;
    final var claimed = _data[i] == 1;
    ++i;
    final var buffers = new byte[32];
    SerDeUtil.readArray(buffers, _data, i);
    return new Vote(_address,
                    discriminator,
                    proposal,
                    voter,
                    bump,
                    side,
                    votingPower,
                    claimed,
                    buffers);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    proposal.write(_data, i);
    i += 32;
    voter.write(_data, i);
    i += 32;
    _data[i] = (byte) bump;
    ++i;
    _data[i] = (byte) side;
    ++i;
    putInt64LE(_data, i, votingPower);
    i += 8;
    _data[i] = (byte) (claimed ? 1 : 0);
    ++i;
    i += SerDeUtil.writeArrayChecked(buffers, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

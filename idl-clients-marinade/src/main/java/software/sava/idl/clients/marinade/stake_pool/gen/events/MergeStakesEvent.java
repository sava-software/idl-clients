package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record MergeStakesEvent(Discriminator discriminator,
                               PublicKey state,
                               long epoch,
                               int destinationStakeIndex,
                               PublicKey destinationStakeAccount,
                               long lastUpdateDestinationStakeDelegation,
                               int sourceStakeIndex,
                               PublicKey sourceStakeAccount,
                               long lastUpdateSourceStakeDelegation,
                               int validatorIndex,
                               PublicKey validatorVote,
                               long extraDelegated,
                               long returnedStakeRent,
                               long validatorActiveBalance,
                               long totalActiveBalance,
                               long operationalSolBalance) implements MarinadeFinanceEvent {

  public static final int BYTES = 212;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static final int STATE_OFFSET = 8;
  public static final int EPOCH_OFFSET = 40;
  public static final int DESTINATION_STAKE_INDEX_OFFSET = 48;
  public static final int DESTINATION_STAKE_ACCOUNT_OFFSET = 52;
  public static final int LAST_UPDATE_DESTINATION_STAKE_DELEGATION_OFFSET = 84;
  public static final int SOURCE_STAKE_INDEX_OFFSET = 92;
  public static final int SOURCE_STAKE_ACCOUNT_OFFSET = 96;
  public static final int LAST_UPDATE_SOURCE_STAKE_DELEGATION_OFFSET = 128;
  public static final int VALIDATOR_INDEX_OFFSET = 136;
  public static final int VALIDATOR_VOTE_OFFSET = 140;
  public static final int EXTRA_DELEGATED_OFFSET = 172;
  public static final int RETURNED_STAKE_RENT_OFFSET = 180;
  public static final int VALIDATOR_ACTIVE_BALANCE_OFFSET = 188;
  public static final int TOTAL_ACTIVE_BALANCE_OFFSET = 196;
  public static final int OPERATIONAL_SOL_BALANCE_OFFSET = 204;

  public static MergeStakesEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var state = readPubKey(_data, i);
    i += 32;
    final var epoch = getInt64LE(_data, i);
    i += 8;
    final var destinationStakeIndex = getInt32LE(_data, i);
    i += 4;
    final var destinationStakeAccount = readPubKey(_data, i);
    i += 32;
    final var lastUpdateDestinationStakeDelegation = getInt64LE(_data, i);
    i += 8;
    final var sourceStakeIndex = getInt32LE(_data, i);
    i += 4;
    final var sourceStakeAccount = readPubKey(_data, i);
    i += 32;
    final var lastUpdateSourceStakeDelegation = getInt64LE(_data, i);
    i += 8;
    final var validatorIndex = getInt32LE(_data, i);
    i += 4;
    final var validatorVote = readPubKey(_data, i);
    i += 32;
    final var extraDelegated = getInt64LE(_data, i);
    i += 8;
    final var returnedStakeRent = getInt64LE(_data, i);
    i += 8;
    final var validatorActiveBalance = getInt64LE(_data, i);
    i += 8;
    final var totalActiveBalance = getInt64LE(_data, i);
    i += 8;
    final var operationalSolBalance = getInt64LE(_data, i);
    return new MergeStakesEvent(discriminator,
                                state,
                                epoch,
                                destinationStakeIndex,
                                destinationStakeAccount,
                                lastUpdateDestinationStakeDelegation,
                                sourceStakeIndex,
                                sourceStakeAccount,
                                lastUpdateSourceStakeDelegation,
                                validatorIndex,
                                validatorVote,
                                extraDelegated,
                                returnedStakeRent,
                                validatorActiveBalance,
                                totalActiveBalance,
                                operationalSolBalance);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    state.write(_data, i);
    i += 32;
    putInt64LE(_data, i, epoch);
    i += 8;
    putInt32LE(_data, i, destinationStakeIndex);
    i += 4;
    destinationStakeAccount.write(_data, i);
    i += 32;
    putInt64LE(_data, i, lastUpdateDestinationStakeDelegation);
    i += 8;
    putInt32LE(_data, i, sourceStakeIndex);
    i += 4;
    sourceStakeAccount.write(_data, i);
    i += 32;
    putInt64LE(_data, i, lastUpdateSourceStakeDelegation);
    i += 8;
    putInt32LE(_data, i, validatorIndex);
    i += 4;
    validatorVote.write(_data, i);
    i += 32;
    putInt64LE(_data, i, extraDelegated);
    i += 8;
    putInt64LE(_data, i, returnedStakeRent);
    i += 8;
    putInt64LE(_data, i, validatorActiveBalance);
    i += 8;
    putInt64LE(_data, i, totalActiveBalance);
    i += 8;
    putInt64LE(_data, i, operationalSolBalance);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

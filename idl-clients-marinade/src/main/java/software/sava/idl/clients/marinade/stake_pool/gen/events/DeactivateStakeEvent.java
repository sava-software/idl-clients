package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.marinade.stake_pool.gen.types.SplitStakeAccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record DeactivateStakeEvent(Discriminator discriminator,
                                   PublicKey state,
                                   long epoch,
                                   int stakeIndex,
                                   PublicKey stakeAccount,
                                   long lastUpdateStakeDelegation,
                                   SplitStakeAccountInfo splitStakeAccount,
                                   int validatorIndex,
                                   PublicKey validatorVote,
                                   long totalStakeTarget,
                                   long validatorStakeTarget,
                                   long totalActiveBalance,
                                   long delayedUnstakeCoolingDown,
                                   long validatorActiveBalance,
                                   long totalUnstakeDelta,
                                   long unstakedAmount) implements MarinadeFinanceEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static final int STATE_OFFSET = 8;
  public static final int EPOCH_OFFSET = 40;
  public static final int STAKE_INDEX_OFFSET = 48;
  public static final int STAKE_ACCOUNT_OFFSET = 52;
  public static final int LAST_UPDATE_STAKE_DELEGATION_OFFSET = 84;
  public static final int SPLIT_STAKE_ACCOUNT_OFFSET = 93;

  public static DeactivateStakeEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var state = readPubKey(_data, i);
    i += 32;
    final var epoch = getInt64LE(_data, i);
    i += 8;
    final var stakeIndex = getInt32LE(_data, i);
    i += 4;
    final var stakeAccount = readPubKey(_data, i);
    i += 32;
    final var lastUpdateStakeDelegation = getInt64LE(_data, i);
    i += 8;
    final SplitStakeAccountInfo splitStakeAccount;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      splitStakeAccount = null;
      ++i;
    } else {
      ++i;
      splitStakeAccount = SplitStakeAccountInfo.read(_data, i);
      i += splitStakeAccount.l();
    }
    final var validatorIndex = getInt32LE(_data, i);
    i += 4;
    final var validatorVote = readPubKey(_data, i);
    i += 32;
    final var totalStakeTarget = getInt64LE(_data, i);
    i += 8;
    final var validatorStakeTarget = getInt64LE(_data, i);
    i += 8;
    final var totalActiveBalance = getInt64LE(_data, i);
    i += 8;
    final var delayedUnstakeCoolingDown = getInt64LE(_data, i);
    i += 8;
    final var validatorActiveBalance = getInt64LE(_data, i);
    i += 8;
    final var totalUnstakeDelta = getInt64LE(_data, i);
    i += 8;
    final var unstakedAmount = getInt64LE(_data, i);
    return new DeactivateStakeEvent(discriminator,
                                    state,
                                    epoch,
                                    stakeIndex,
                                    stakeAccount,
                                    lastUpdateStakeDelegation,
                                    splitStakeAccount,
                                    validatorIndex,
                                    validatorVote,
                                    totalStakeTarget,
                                    validatorStakeTarget,
                                    totalActiveBalance,
                                    delayedUnstakeCoolingDown,
                                    validatorActiveBalance,
                                    totalUnstakeDelta,
                                    unstakedAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    state.write(_data, i);
    i += 32;
    putInt64LE(_data, i, epoch);
    i += 8;
    putInt32LE(_data, i, stakeIndex);
    i += 4;
    stakeAccount.write(_data, i);
    i += 32;
    putInt64LE(_data, i, lastUpdateStakeDelegation);
    i += 8;
    i += SerDeUtil.writeOptional(1, splitStakeAccount, _data, i);
    putInt32LE(_data, i, validatorIndex);
    i += 4;
    validatorVote.write(_data, i);
    i += 32;
    putInt64LE(_data, i, totalStakeTarget);
    i += 8;
    putInt64LE(_data, i, validatorStakeTarget);
    i += 8;
    putInt64LE(_data, i, totalActiveBalance);
    i += 8;
    putInt64LE(_data, i, delayedUnstakeCoolingDown);
    i += 8;
    putInt64LE(_data, i, validatorActiveBalance);
    i += 8;
    putInt64LE(_data, i, totalUnstakeDelta);
    i += 8;
    putInt64LE(_data, i, unstakedAmount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 32
         + 8
         + 4
         + 32
         + 8
         + (splitStakeAccount == null ? 1 : (1 + splitStakeAccount.l()))
         + 4
         + 32
         + 8
         + 8
         + 8
         + 8
         + 8
         + 8
         + 8;
  }
}

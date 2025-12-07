package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.marinade.stake_pool.gen.types.SplitStakeAccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record RedelegateEvent(Discriminator discriminator,
                              PublicKey state,
                              long epoch,
                              int stakeIndex,
                              PublicKey stakeAccount,
                              long lastUpdateDelegation,
                              int sourceValidatorIndex,
                              PublicKey sourceValidatorVote,
                              int sourceValidatorScore,
                              long sourceValidatorBalance,
                              long sourceValidatorStakeTarget,
                              int destValidatorIndex,
                              PublicKey destValidatorVote,
                              int destValidatorScore,
                              long destValidatorBalance,
                              long destValidatorStakeTarget,
                              long redelegateAmount,
                              SplitStakeAccountInfo splitStakeAccount,
                              int redelegateStakeIndex,
                              PublicKey redelegateStakeAccount) implements MarinadeFinanceEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static RedelegateEvent read(final byte[] _data, final int _offset) {
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
    final var lastUpdateDelegation = getInt64LE(_data, i);
    i += 8;
    final var sourceValidatorIndex = getInt32LE(_data, i);
    i += 4;
    final var sourceValidatorVote = readPubKey(_data, i);
    i += 32;
    final var sourceValidatorScore = getInt32LE(_data, i);
    i += 4;
    final var sourceValidatorBalance = getInt64LE(_data, i);
    i += 8;
    final var sourceValidatorStakeTarget = getInt64LE(_data, i);
    i += 8;
    final var destValidatorIndex = getInt32LE(_data, i);
    i += 4;
    final var destValidatorVote = readPubKey(_data, i);
    i += 32;
    final var destValidatorScore = getInt32LE(_data, i);
    i += 4;
    final var destValidatorBalance = getInt64LE(_data, i);
    i += 8;
    final var destValidatorStakeTarget = getInt64LE(_data, i);
    i += 8;
    final var redelegateAmount = getInt64LE(_data, i);
    i += 8;
    final SplitStakeAccountInfo splitStakeAccount;
    if (_data[i] == 0) {
      splitStakeAccount = null;
      ++i;
    } else {
      ++i;
      splitStakeAccount = SplitStakeAccountInfo.read(_data, i);
      i += splitStakeAccount.l();
    }
    final var redelegateStakeIndex = getInt32LE(_data, i);
    i += 4;
    final var redelegateStakeAccount = readPubKey(_data, i);
    return new RedelegateEvent(discriminator,
                               state,
                               epoch,
                               stakeIndex,
                               stakeAccount,
                               lastUpdateDelegation,
                               sourceValidatorIndex,
                               sourceValidatorVote,
                               sourceValidatorScore,
                               sourceValidatorBalance,
                               sourceValidatorStakeTarget,
                               destValidatorIndex,
                               destValidatorVote,
                               destValidatorScore,
                               destValidatorBalance,
                               destValidatorStakeTarget,
                               redelegateAmount,
                               splitStakeAccount,
                               redelegateStakeIndex,
                               redelegateStakeAccount);
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
    putInt64LE(_data, i, lastUpdateDelegation);
    i += 8;
    putInt32LE(_data, i, sourceValidatorIndex);
    i += 4;
    sourceValidatorVote.write(_data, i);
    i += 32;
    putInt32LE(_data, i, sourceValidatorScore);
    i += 4;
    putInt64LE(_data, i, sourceValidatorBalance);
    i += 8;
    putInt64LE(_data, i, sourceValidatorStakeTarget);
    i += 8;
    putInt32LE(_data, i, destValidatorIndex);
    i += 4;
    destValidatorVote.write(_data, i);
    i += 32;
    putInt32LE(_data, i, destValidatorScore);
    i += 4;
    putInt64LE(_data, i, destValidatorBalance);
    i += 8;
    putInt64LE(_data, i, destValidatorStakeTarget);
    i += 8;
    putInt64LE(_data, i, redelegateAmount);
    i += 8;
    i += Borsh.writeOptional(splitStakeAccount, _data, i);
    putInt32LE(_data, i, redelegateStakeIndex);
    i += 4;
    redelegateStakeAccount.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 32
         + 8
         + 4
         + 32
         + 8
         + 4
         + 32
         + 4
         + 8
         + 8
         + 4
         + 32
         + 4
         + 8
         + 8
         + 8
         + (splitStakeAccount == null ? 1 : (1 + splitStakeAccount.l()))
         + 4
         + 32;
  }
}

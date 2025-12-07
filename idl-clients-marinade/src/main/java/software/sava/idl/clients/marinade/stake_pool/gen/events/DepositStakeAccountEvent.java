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

public record DepositStakeAccountEvent(Discriminator discriminator,
                                       PublicKey state,
                                       PublicKey stake,
                                       long delegated,
                                       PublicKey withdrawer,
                                       int stakeIndex,
                                       PublicKey validator,
                                       int validatorIndex,
                                       long validatorActiveBalance,
                                       long totalActiveBalance,
                                       long userMsolBalance,
                                       long msolMinted,
                                       long totalVirtualStakedLamports,
                                       long msolSupply) implements MarinadeFinanceEvent {

  public static final int BYTES = 200;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static DepositStakeAccountEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var state = readPubKey(_data, i);
    i += 32;
    final var stake = readPubKey(_data, i);
    i += 32;
    final var delegated = getInt64LE(_data, i);
    i += 8;
    final var withdrawer = readPubKey(_data, i);
    i += 32;
    final var stakeIndex = getInt32LE(_data, i);
    i += 4;
    final var validator = readPubKey(_data, i);
    i += 32;
    final var validatorIndex = getInt32LE(_data, i);
    i += 4;
    final var validatorActiveBalance = getInt64LE(_data, i);
    i += 8;
    final var totalActiveBalance = getInt64LE(_data, i);
    i += 8;
    final var userMsolBalance = getInt64LE(_data, i);
    i += 8;
    final var msolMinted = getInt64LE(_data, i);
    i += 8;
    final var totalVirtualStakedLamports = getInt64LE(_data, i);
    i += 8;
    final var msolSupply = getInt64LE(_data, i);
    return new DepositStakeAccountEvent(discriminator,
                                        state,
                                        stake,
                                        delegated,
                                        withdrawer,
                                        stakeIndex,
                                        validator,
                                        validatorIndex,
                                        validatorActiveBalance,
                                        totalActiveBalance,
                                        userMsolBalance,
                                        msolMinted,
                                        totalVirtualStakedLamports,
                                        msolSupply);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    state.write(_data, i);
    i += 32;
    stake.write(_data, i);
    i += 32;
    putInt64LE(_data, i, delegated);
    i += 8;
    withdrawer.write(_data, i);
    i += 32;
    putInt32LE(_data, i, stakeIndex);
    i += 4;
    validator.write(_data, i);
    i += 32;
    putInt32LE(_data, i, validatorIndex);
    i += 4;
    putInt64LE(_data, i, validatorActiveBalance);
    i += 8;
    putInt64LE(_data, i, totalActiveBalance);
    i += 8;
    putInt64LE(_data, i, userMsolBalance);
    i += 8;
    putInt64LE(_data, i, msolMinted);
    i += 8;
    putInt64LE(_data, i, totalVirtualStakedLamports);
    i += 8;
    putInt64LE(_data, i, msolSupply);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

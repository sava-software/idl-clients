package software.sava.idl.clients.marinade.stake_pool.gen.events;

import java.util.OptionalLong;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.marinade.stake_pool.gen.types.Fee;
import software.sava.idl.clients.marinade.stake_pool.gen.types.U64ValueChange;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record UpdateDeactivatedEvent(Discriminator discriminator,
                                     PublicKey state,
                                     long epoch,
                                     int stakeIndex,
                                     PublicKey stakeAccount,
                                     long balanceWithoutRentExempt,
                                     long lastUpdateDelegatedLamports,
                                     OptionalLong msolFees,
                                     U64ValueChange msolPriceChange,
                                     Fee rewardFeeUsed,
                                     long operationalSolBalance,
                                     long totalVirtualStakedLamports,
                                     long msolSupply) implements MarinadeFinanceEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static final int STATE_OFFSET = 8;
  public static final int EPOCH_OFFSET = 40;
  public static final int STAKE_INDEX_OFFSET = 48;
  public static final int STAKE_ACCOUNT_OFFSET = 52;
  public static final int BALANCE_WITHOUT_RENT_EXEMPT_OFFSET = 84;
  public static final int LAST_UPDATE_DELEGATED_LAMPORTS_OFFSET = 92;
  public static final int MSOL_FEES_OFFSET = 101;

  public static UpdateDeactivatedEvent read(final byte[] _data, final int _offset) {
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
    final var balanceWithoutRentExempt = getInt64LE(_data, i);
    i += 8;
    final var lastUpdateDelegatedLamports = getInt64LE(_data, i);
    i += 8;
    final OptionalLong msolFees;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      msolFees = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      msolFees = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final var msolPriceChange = U64ValueChange.read(_data, i);
    i += msolPriceChange.l();
    final var rewardFeeUsed = Fee.read(_data, i);
    i += rewardFeeUsed.l();
    final var operationalSolBalance = getInt64LE(_data, i);
    i += 8;
    final var totalVirtualStakedLamports = getInt64LE(_data, i);
    i += 8;
    final var msolSupply = getInt64LE(_data, i);
    return new UpdateDeactivatedEvent(discriminator,
                                      state,
                                      epoch,
                                      stakeIndex,
                                      stakeAccount,
                                      balanceWithoutRentExempt,
                                      lastUpdateDelegatedLamports,
                                      msolFees,
                                      msolPriceChange,
                                      rewardFeeUsed,
                                      operationalSolBalance,
                                      totalVirtualStakedLamports,
                                      msolSupply);
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
    putInt64LE(_data, i, balanceWithoutRentExempt);
    i += 8;
    putInt64LE(_data, i, lastUpdateDelegatedLamports);
    i += 8;
    i += SerDeUtil.writeOptional(1, msolFees, _data, i);
    i += msolPriceChange.write(_data, i);
    i += rewardFeeUsed.write(_data, i);
    putInt64LE(_data, i, operationalSolBalance);
    i += 8;
    putInt64LE(_data, i, totalVirtualStakedLamports);
    i += 8;
    putInt64LE(_data, i, msolSupply);
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
         + 8
         + (msolFees == null || msolFees.isEmpty() ? 1 : (1 + 8))
         + msolPriceChange.l()
         + rewardFeeUsed.l()
         + 8
         + 8
         + 8;
  }
}

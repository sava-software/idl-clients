package software.sava.idl.clients.marinade.stake_pool.gen.events;

import java.util.OptionalLong;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.marinade.stake_pool.gen.types.Fee;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LiquidUnstakeEvent(Discriminator discriminator,
                                 PublicKey state,
                                 PublicKey msolOwner,
                                 long liqPoolSolBalance,
                                 long liqPoolMsolBalance,
                                 OptionalLong treasuryMsolBalance,
                                 long userMsolBalance,
                                 long userSolBalance,
                                 long msolAmount,
                                 long msolFee,
                                 long treasuryMsolCut,
                                 long solAmount,
                                 long lpLiquidityTarget,
                                 Fee lpMaxFee,
                                 Fee lpMinFee,
                                 Fee treasuryCut) implements MarinadeFinanceEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static LiquidUnstakeEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var state = readPubKey(_data, i);
    i += 32;
    final var msolOwner = readPubKey(_data, i);
    i += 32;
    final var liqPoolSolBalance = getInt64LE(_data, i);
    i += 8;
    final var liqPoolMsolBalance = getInt64LE(_data, i);
    i += 8;
    final OptionalLong treasuryMsolBalance;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      treasuryMsolBalance = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      treasuryMsolBalance = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final var userMsolBalance = getInt64LE(_data, i);
    i += 8;
    final var userSolBalance = getInt64LE(_data, i);
    i += 8;
    final var msolAmount = getInt64LE(_data, i);
    i += 8;
    final var msolFee = getInt64LE(_data, i);
    i += 8;
    final var treasuryMsolCut = getInt64LE(_data, i);
    i += 8;
    final var solAmount = getInt64LE(_data, i);
    i += 8;
    final var lpLiquidityTarget = getInt64LE(_data, i);
    i += 8;
    final var lpMaxFee = Fee.read(_data, i);
    i += lpMaxFee.l();
    final var lpMinFee = Fee.read(_data, i);
    i += lpMinFee.l();
    final var treasuryCut = Fee.read(_data, i);
    return new LiquidUnstakeEvent(discriminator,
                                  state,
                                  msolOwner,
                                  liqPoolSolBalance,
                                  liqPoolMsolBalance,
                                  treasuryMsolBalance,
                                  userMsolBalance,
                                  userSolBalance,
                                  msolAmount,
                                  msolFee,
                                  treasuryMsolCut,
                                  solAmount,
                                  lpLiquidityTarget,
                                  lpMaxFee,
                                  lpMinFee,
                                  treasuryCut);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    state.write(_data, i);
    i += 32;
    msolOwner.write(_data, i);
    i += 32;
    putInt64LE(_data, i, liqPoolSolBalance);
    i += 8;
    putInt64LE(_data, i, liqPoolMsolBalance);
    i += 8;
    i += SerDeUtil.writeOptional(1, treasuryMsolBalance, _data, i);
    putInt64LE(_data, i, userMsolBalance);
    i += 8;
    putInt64LE(_data, i, userSolBalance);
    i += 8;
    putInt64LE(_data, i, msolAmount);
    i += 8;
    putInt64LE(_data, i, msolFee);
    i += 8;
    putInt64LE(_data, i, treasuryMsolCut);
    i += 8;
    putInt64LE(_data, i, solAmount);
    i += 8;
    putInt64LE(_data, i, lpLiquidityTarget);
    i += 8;
    i += lpMaxFee.write(_data, i);
    i += lpMinFee.write(_data, i);
    i += treasuryCut.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 32
         + 32
         + 8
         + 8
         + (treasuryMsolBalance == null || treasuryMsolBalance.isEmpty() ? 1 : (1 + 8))
         + 8
         + 8
         + 8
         + 8
         + 8
         + 8
         + 8
         + lpMaxFee.l()
         + lpMinFee.l()
         + treasuryCut.l();
  }
}

package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record RemoveLiquidityEvent(Discriminator discriminator,
                                   PublicKey state,
                                   long solLegBalance,
                                   long msolLegBalance,
                                   long userLpBalance,
                                   long userSolBalance,
                                   long userMsolBalance,
                                   long lpMintSupply,
                                   long lpBurned,
                                   long solOutAmount,
                                   long msolOutAmount) implements MarinadeFinanceEvent {

  public static final int BYTES = 112;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static final int STATE_OFFSET = 8;
  public static final int SOL_LEG_BALANCE_OFFSET = 40;
  public static final int MSOL_LEG_BALANCE_OFFSET = 48;
  public static final int USER_LP_BALANCE_OFFSET = 56;
  public static final int USER_SOL_BALANCE_OFFSET = 64;
  public static final int USER_MSOL_BALANCE_OFFSET = 72;
  public static final int LP_MINT_SUPPLY_OFFSET = 80;
  public static final int LP_BURNED_OFFSET = 88;
  public static final int SOL_OUT_AMOUNT_OFFSET = 96;
  public static final int MSOL_OUT_AMOUNT_OFFSET = 104;

  public static RemoveLiquidityEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var state = readPubKey(_data, i);
    i += 32;
    final var solLegBalance = getInt64LE(_data, i);
    i += 8;
    final var msolLegBalance = getInt64LE(_data, i);
    i += 8;
    final var userLpBalance = getInt64LE(_data, i);
    i += 8;
    final var userSolBalance = getInt64LE(_data, i);
    i += 8;
    final var userMsolBalance = getInt64LE(_data, i);
    i += 8;
    final var lpMintSupply = getInt64LE(_data, i);
    i += 8;
    final var lpBurned = getInt64LE(_data, i);
    i += 8;
    final var solOutAmount = getInt64LE(_data, i);
    i += 8;
    final var msolOutAmount = getInt64LE(_data, i);
    return new RemoveLiquidityEvent(discriminator,
                                    state,
                                    solLegBalance,
                                    msolLegBalance,
                                    userLpBalance,
                                    userSolBalance,
                                    userMsolBalance,
                                    lpMintSupply,
                                    lpBurned,
                                    solOutAmount,
                                    msolOutAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    state.write(_data, i);
    i += 32;
    putInt64LE(_data, i, solLegBalance);
    i += 8;
    putInt64LE(_data, i, msolLegBalance);
    i += 8;
    putInt64LE(_data, i, userLpBalance);
    i += 8;
    putInt64LE(_data, i, userSolBalance);
    i += 8;
    putInt64LE(_data, i, userMsolBalance);
    i += 8;
    putInt64LE(_data, i, lpMintSupply);
    i += 8;
    putInt64LE(_data, i, lpBurned);
    i += 8;
    putInt64LE(_data, i, solOutAmount);
    i += 8;
    putInt64LE(_data, i, msolOutAmount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

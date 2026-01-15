package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param lpLiquidityTarget Liquidity target. If the Liquidity reach this amount, the fee reaches lp_min_discount_fee
/// @param lpMaxFee Liquidity pool max fee
/// @param lpMinFee SOL/mSOL Liquidity pool min fee
/// @param treasuryCut Treasury cut
public record LiqPool(PublicKey lpMint,
                      int lpMintAuthorityBumpSeed,
                      int solLegBumpSeed,
                      int msolLegAuthorityBumpSeed,
                      PublicKey msolLeg,
                      long lpLiquidityTarget,
                      Fee lpMaxFee,
                      Fee lpMinFee,
                      Fee treasuryCut,
                      long lpSupply,
                      long lentFromSolLeg,
                      long liquiditySolCap) implements SerDe {

  public static final int BYTES = 111;

  public static final int LP_MINT_OFFSET = 0;
  public static final int LP_MINT_AUTHORITY_BUMP_SEED_OFFSET = 32;
  public static final int SOL_LEG_BUMP_SEED_OFFSET = 33;
  public static final int MSOL_LEG_AUTHORITY_BUMP_SEED_OFFSET = 34;
  public static final int MSOL_LEG_OFFSET = 35;
  public static final int LP_LIQUIDITY_TARGET_OFFSET = 67;
  public static final int LP_MAX_FEE_OFFSET = 75;
  public static final int LP_MIN_FEE_OFFSET = 79;
  public static final int TREASURY_CUT_OFFSET = 83;
  public static final int LP_SUPPLY_OFFSET = 87;
  public static final int LENT_FROM_SOL_LEG_OFFSET = 95;
  public static final int LIQUIDITY_SOL_CAP_OFFSET = 103;

  public static LiqPool read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var lpMint = readPubKey(_data, i);
    i += 32;
    final var lpMintAuthorityBumpSeed = _data[i] & 0xFF;
    ++i;
    final var solLegBumpSeed = _data[i] & 0xFF;
    ++i;
    final var msolLegAuthorityBumpSeed = _data[i] & 0xFF;
    ++i;
    final var msolLeg = readPubKey(_data, i);
    i += 32;
    final var lpLiquidityTarget = getInt64LE(_data, i);
    i += 8;
    final var lpMaxFee = Fee.read(_data, i);
    i += lpMaxFee.l();
    final var lpMinFee = Fee.read(_data, i);
    i += lpMinFee.l();
    final var treasuryCut = Fee.read(_data, i);
    i += treasuryCut.l();
    final var lpSupply = getInt64LE(_data, i);
    i += 8;
    final var lentFromSolLeg = getInt64LE(_data, i);
    i += 8;
    final var liquiditySolCap = getInt64LE(_data, i);
    return new LiqPool(lpMint,
                       lpMintAuthorityBumpSeed,
                       solLegBumpSeed,
                       msolLegAuthorityBumpSeed,
                       msolLeg,
                       lpLiquidityTarget,
                       lpMaxFee,
                       lpMinFee,
                       treasuryCut,
                       lpSupply,
                       lentFromSolLeg,
                       liquiditySolCap);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    lpMint.write(_data, i);
    i += 32;
    _data[i] = (byte) lpMintAuthorityBumpSeed;
    ++i;
    _data[i] = (byte) solLegBumpSeed;
    ++i;
    _data[i] = (byte) msolLegAuthorityBumpSeed;
    ++i;
    msolLeg.write(_data, i);
    i += 32;
    putInt64LE(_data, i, lpLiquidityTarget);
    i += 8;
    i += lpMaxFee.write(_data, i);
    i += lpMinFee.write(_data, i);
    i += treasuryCut.write(_data, i);
    putInt64LE(_data, i, lpSupply);
    i += 8;
    putInt64LE(_data, i, lentFromSolLeg);
    i += 8;
    putInt64LE(_data, i, liquiditySolCap);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

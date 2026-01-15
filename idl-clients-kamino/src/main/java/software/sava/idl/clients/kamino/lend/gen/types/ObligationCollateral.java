package software.sava.idl.clients.kamino.lend.gen.types;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Obligation collateral state
///
/// @param depositReserve Reserve collateral is deposited to
/// @param depositedAmount Amount of collateral deposited
/// @param marketValueSf Collateral market value in quote currency (scaled fraction)
/// @param borrowedAmountAgainstThisCollateralInElevationGroup Debt amount (lamport) taken against this collateral.
///                                                            (only meaningful if this obligation is part of an elevation group, otherwise 0)
///                                                            This is only indicative of the debt computed on the last refresh obligation.
///                                                            If the obligation have multiple collateral this value is the same for all of them.
public record ObligationCollateral(PublicKey depositReserve,
                                   long depositedAmount,
                                   BigInteger marketValueSf,
                                   long borrowedAmountAgainstThisCollateralInElevationGroup,
                                   long[] padding) implements SerDe {

  public static final int BYTES = 136;
  public static final int PADDING_LEN = 9;

  public static final int DEPOSIT_RESERVE_OFFSET = 0;
  public static final int DEPOSITED_AMOUNT_OFFSET = 32;
  public static final int MARKET_VALUE_SF_OFFSET = 40;
  public static final int BORROWED_AMOUNT_AGAINST_THIS_COLLATERAL_IN_ELEVATION_GROUP_OFFSET = 56;
  public static final int PADDING_OFFSET = 64;

  public static ObligationCollateral read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var depositReserve = readPubKey(_data, i);
    i += 32;
    final var depositedAmount = getInt64LE(_data, i);
    i += 8;
    final var marketValueSf = getInt128LE(_data, i);
    i += 16;
    final var borrowedAmountAgainstThisCollateralInElevationGroup = getInt64LE(_data, i);
    i += 8;
    final var padding = new long[9];
    SerDeUtil.readArray(padding, _data, i);
    return new ObligationCollateral(depositReserve,
                                    depositedAmount,
                                    marketValueSf,
                                    borrowedAmountAgainstThisCollateralInElevationGroup,
                                    padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    depositReserve.write(_data, i);
    i += 32;
    putInt64LE(_data, i, depositedAmount);
    i += 8;
    putInt128LE(_data, i, marketValueSf);
    i += 16;
    putInt64LE(_data, i, borrowedAmountAgainstThisCollateralInElevationGroup);
    i += 8;
    i += SerDeUtil.writeArrayChecked(padding, 9, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

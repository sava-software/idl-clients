package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// A definition of optional customizations that should be applied after cloning the config.
///
/// @param overrideFixedRateBps A gate for Self::fixed_borrow_rate_bps.
/// @param fixedBorrowRateBps If Self::override_fixed_rate_bps is non-zero, this borrow rate will be used to override
///                           the ReserveConfig::borrow_rate_curve with a fixed one.
/// @param overrideDebtTermSeconds A gate for Self::debt_term_seconds.
/// @param debtTermSeconds If Self::override_debt_term_seconds is non-zero, this value will be used to override the
///                        ReserveConfig::debt_term_seconds.
/// @param clearElevationGroups Whether the target reserve should have zeroed ReserveConfig::elevation_groups (i.e. not
///                             cloned from source).
///                             
///                             This customization is mandatory when cloning a reserve (with some elevation groups) into a
///                             different market (where those elevation group indices would have different meaning).
public record ReserveConfigCustomizationArgs(int overrideFixedRateBps,
                                             int fixedBorrowRateBps,
                                             int overrideDebtTermSeconds,
                                             long debtTermSeconds,
                                             int clearElevationGroups) implements SerDe {

  public static final int BYTES = 15;

  public static final int OVERRIDE_FIXED_RATE_BPS_OFFSET = 0;
  public static final int FIXED_BORROW_RATE_BPS_OFFSET = 1;
  public static final int OVERRIDE_DEBT_TERM_SECONDS_OFFSET = 5;
  public static final int DEBT_TERM_SECONDS_OFFSET = 6;
  public static final int CLEAR_ELEVATION_GROUPS_OFFSET = 14;

  public static ReserveConfigCustomizationArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var overrideFixedRateBps = _data[i] & 0xFF;
    ++i;
    final var fixedBorrowRateBps = getInt32LE(_data, i);
    i += 4;
    final var overrideDebtTermSeconds = _data[i] & 0xFF;
    ++i;
    final var debtTermSeconds = getInt64LE(_data, i);
    i += 8;
    final var clearElevationGroups = _data[i] & 0xFF;
    return new ReserveConfigCustomizationArgs(overrideFixedRateBps,
                                              fixedBorrowRateBps,
                                              overrideDebtTermSeconds,
                                              debtTermSeconds,
                                              clearElevationGroups);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) overrideFixedRateBps;
    ++i;
    putInt32LE(_data, i, fixedBorrowRateBps);
    i += 4;
    _data[i] = (byte) overrideDebtTermSeconds;
    ++i;
    putInt64LE(_data, i, debtTermSeconds);
    i += 8;
    _data[i] = (byte) clearElevationGroups;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

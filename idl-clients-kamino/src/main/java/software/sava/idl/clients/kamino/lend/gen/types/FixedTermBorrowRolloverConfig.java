package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Settings driving the auto-rollover of an ObligationLiquidity that uses a fixed-term Reserve
/// and approaches the end of its ReserveConfig::debt_term_seconds.
/// 
/// By its nature (not a special case), the zeroed struct mean "no auto-rollover".
///
/// @param autoRolloverEnabled Whether the borrow can be permissionlessly prolonged under the following *joint* conditions:
///                            
///                            The reserve used to re-borrow the liquidity must have:
///                            A) the exact same maximum borrow rate as the current one,
///                            B) the exact same debt term as the current one,
///                            C) sufficient available liquidity (including no withdraw tickets waiting in its queue).
///                            
///                            The time left until the current debt term expires must be:
///                            D) less than LendingMarket::fixed_rollover_window_duration_seconds.
///                            
///                            Note: the other settings are only effective when this one is `1`.
/// @param openTermAllowed When `1`, partially lifts the condition *B* from Self::auto_rollover_enabled: additionally
///                        allows to use a variable (indefinite) debt term if less than
///                        LendingMarket::variable_rollover_window_duration_seconds is left until expiration.
///                        
///                        Note: typically this flag should be used together with Self::max_borrow_rate_bps set to
///                        `u32::MAX` (to denote a variable-rate reserve).
/// @param alignmentPadding Internal alignment padding (free to reuse).
/// @param maxBorrowRateBps A customization setting that can lift the condition *A* from Self::auto_rollover_enabled:
///                         when not zeroed, the rollover may use a reserve with a maximum borrow rate equal or lower
///                         than the given one.
/// @param minDebtTermSeconds A customization setting that can lift the condition *B* from Self::auto_rollover_enabled:
///                           when not zeroed, the rollover may use a reserve with a *fixed* debt term equal or longer
///                           than the given one.
public record FixedTermBorrowRolloverConfig(int autoRolloverEnabled,
                                            int openTermAllowed,
                                            byte[] alignmentPadding,
                                            int maxBorrowRateBps,
                                            long minDebtTermSeconds) implements SerDe {

  public static final int BYTES = 16;
  public static final int ALIGNMENT_PADDING_LEN = 2;

  public static final int AUTO_ROLLOVER_ENABLED_OFFSET = 0;
  public static final int OPEN_TERM_ALLOWED_OFFSET = 1;
  public static final int ALIGNMENT_PADDING_OFFSET = 2;
  public static final int MAX_BORROW_RATE_BPS_OFFSET = 4;
  public static final int MIN_DEBT_TERM_SECONDS_OFFSET = 8;

  public static FixedTermBorrowRolloverConfig read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var autoRolloverEnabled = _data[i] & 0xFF;
    ++i;
    final var openTermAllowed = _data[i] & 0xFF;
    ++i;
    final var alignmentPadding = new byte[2];
    i += SerDeUtil.readArray(alignmentPadding, _data, i);
    final var maxBorrowRateBps = getInt32LE(_data, i);
    i += 4;
    final var minDebtTermSeconds = getInt64LE(_data, i);
    return new FixedTermBorrowRolloverConfig(autoRolloverEnabled,
                                             openTermAllowed,
                                             alignmentPadding,
                                             maxBorrowRateBps,
                                             minDebtTermSeconds);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) autoRolloverEnabled;
    ++i;
    _data[i] = (byte) openTermAllowed;
    ++i;
    i += SerDeUtil.writeArrayChecked(alignmentPadding, 2, _data, i);
    putInt32LE(_data, i, maxBorrowRateBps);
    i += 4;
    putInt64LE(_data, i, minDebtTermSeconds);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

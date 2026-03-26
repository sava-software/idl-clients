package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Settings driving the auto-rollover (or migration) of an ObligationLiquidity's borrow.
/// 
/// This covers three flavors:
/// - *fixed-to-fixed*: a fixed-term borrow rolling into another fixed-term reserve,
/// - *fixed-to-open*: a fixed-term borrow rolling into an open-term reserve,
/// - *open-to-fixed*: an open-term borrow migrating into a fixed-term reserve.
/// 
/// By its nature (not a special case), the zeroed struct means "no auto-rollover/migration".
///
/// @param autoRolloverEnabled Whether this *fixed-term* borrow can be permissionlessly prolonged. The funds used to roll
///                            over can come:
///                            - either from a *fixed-term* reserve (same or a different one):
///                            - This can only happen within LendingMarket::fixed_term_rollover_window_duration_seconds.
///                            - The target reserve must meet all the criteria defined in this config (see
///                            Self::max_borrow_rate_bps and Self::min_debt_term_seconds).
///                            - Note: not possible when Self::min_debt_term_seconds is `0` (open-term only).
///                            - or from an *open-term* reserve:
///                            - This can only happen within LendingMarket::open_term_rollover_window_duration_seconds.
///                            - The user must explicitly set Self::open_term_allowed here.
///                            
///                            This setting is not effective when the borrow is currently using an *open-term* reserve.
/// @param openTermAllowed When `1`, then Self::auto_rollover_enabled is allowed to roll this borrow over into any
///                        open-term reserve.
///                        
///                        Please note that if such rollover actually happens, then Self::max_borrow_rate_bps
///                        condition does not apply - technically, it could be evaluated, but open-term reserves
///                        typically use float-rate (utilization-driven borrow rate curve) which has very high maximum
///                        (when at 100% utilization) that would not meet any practical criteria here.
/// @param migrationToFixedEnabled Whether this *open-term* borrow can be permissionlessly migrated into a fixed-term reserve:
///                                - This can happen at any moment (as soon as liquidity becomes available).
///                                - The target fixed-term reserve must meet all the criteria defined in this config (see
///                                Self::max_borrow_rate_bps and Self::min_debt_term_seconds).
///                                
///                                This setting is not effective when the borrow is currently using a *fixed-term* reserve.
///                                
///                                Cannot be enabled when Self::min_debt_term_seconds is `0` (open-term only), because
///                                migrating into a fixed-term reserve contradicts the open-term-only intent.
/// @param alignmentPadding Internal alignment padding (free to reuse).
/// @param maxBorrowRateBps A maximum allowed borrow rate of a reserve that can be used for a rollover/migration.
///                         
///                         Note: this must be set (i.e. non-zero) when enabling any rollover/migration flavor, but is
///                         of course not effective when rollover/migration is not enabled.
/// @param minDebtTermSeconds A minimum debt term (in seconds) of a fixed-term reserve that can be used for a
///                           rollover/migration.
///                           
///                           When `0`, the owner only accepts open-term reserves as rollover targets — i.e. rolling over
///                           (or migrating) into a fixed-term reserve is not allowed. This is consistent with the
///                           semantics of BorrowOrder::min_debt_term_seconds.
///                           
///                           This means that `0` is incompatible with Self::migration_to_fixed_enabled (which requires
///                           a fixed-term target) — this combination is rejected at configuration time.
public record FixedTermBorrowRolloverConfig(int autoRolloverEnabled,
                                            int openTermAllowed,
                                            int migrationToFixedEnabled,
                                            byte[] alignmentPadding,
                                            int maxBorrowRateBps,
                                            long minDebtTermSeconds) implements SerDe {

  public static final int BYTES = 16;
  public static final int ALIGNMENT_PADDING_LEN = 1;

  public static final int AUTO_ROLLOVER_ENABLED_OFFSET = 0;
  public static final int OPEN_TERM_ALLOWED_OFFSET = 1;
  public static final int MIGRATION_TO_FIXED_ENABLED_OFFSET = 2;
  public static final int ALIGNMENT_PADDING_OFFSET = 3;
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
    final var migrationToFixedEnabled = _data[i] & 0xFF;
    ++i;
    final var alignmentPadding = new byte[1];
    i += SerDeUtil.readArray(alignmentPadding, _data, i);
    final var maxBorrowRateBps = getInt32LE(_data, i);
    i += 4;
    final var minDebtTermSeconds = getInt64LE(_data, i);
    return new FixedTermBorrowRolloverConfig(autoRolloverEnabled,
                                             openTermAllowed,
                                             migrationToFixedEnabled,
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
    _data[i] = (byte) migrationToFixedEnabled;
    ++i;
    i += SerDeUtil.writeArrayChecked(alignmentPadding, 1, _data, i);
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

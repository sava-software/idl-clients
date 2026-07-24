package software.sava.idl.clients.orca.quote;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.orca.whirlpools.gen.types.Position;
import software.sava.idl.clients.orca.whirlpools.gen.types.Tick;
import software.sava.idl.clients.orca.whirlpools.gen.types.Whirlpool;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.sava.idl.clients.orca.quote.WhirlpoolQuoteTestSupport.*;

/// Ported from `rust-sdk/core/src/quote/fees.rs#tests`.
final class CollectFeesQuoteTests {

  private static final BigInteger LIQ = bi("10000000000000000000");

  private static Whirlpool pool(final int tickCurrent) {
    return whirlpool(tickCurrent, bi(800), bi(1000));
  }

  private static Position pos() {
    return position(LIQ, 5, 10, ZERO, 400L, ZERO, 600L);
  }

  private static Tick t() {
    return tick(bi(50), bi(20));
  }

  @Test
  void collectOutOfRangeLower() {
    final var r = WhirlpoolQuote.collectFeesQuote(pool(0), pos(), t(), t(), null, null);
    assertEquals(400L, r.feeOwedA());
    assertEquals(600L, r.feeOwedB());
  }

  @Test
  void inRange() {
    final var r = WhirlpoolQuote.collectFeesQuote(pool(7), pos(), t(), t(), null, null);
    assertEquals(779L, r.feeOwedA());
    assertEquals(1120L, r.feeOwedB());
  }

  @Test
  void collectOutOfRangeUpper() {
    final var r = WhirlpoolQuote.collectFeesQuote(pool(15), pos(), t(), t(), null, null);
    assertEquals(400L, r.feeOwedA());
    assertEquals(600L, r.feeOwedB());
  }

  @Test
  void collectOnRangeLower() {
    final var r = WhirlpoolQuote.collectFeesQuote(pool(5), pos(), t(), t(), null, null);
    assertEquals(779L, r.feeOwedA());
    assertEquals(1120L, r.feeOwedB());
  }

  @Test
  void collectOnUpper() {
    final var r = WhirlpoolQuote.collectFeesQuote(pool(10), pos(), t(), t(), null, null);
    assertEquals(400L, r.feeOwedA());
    assertEquals(600L, r.feeOwedB());
  }

  @Test
  void collectTransferFee() {
    final var r = WhirlpoolQuote.collectFeesQuote(pool(7), pos(), t(), t(),
        TransferFee.of(2000), TransferFee.of(5000));
    assertEquals(623L, r.feeOwedA());
    assertEquals(560L, r.feeOwedB());
  }

  /// The u64 range boundary, both sides. Regression: `toU64` used
  /// `longValueExact`, which rejects the valid u64 range above
  /// `Long.MAX_VALUE` — a position owed more than 2^63-1 lamports of fees
  /// threw instead of quoting. Exactly u64::MAX must come back as unsigned
  /// bits; one past it must throw.
  @Test
  void feeOwedCoversTheFullU64Range() {
    // no growth: the withdrawable fee is exactly the stored feeOwed
    final var maxOwed = position(ZERO, 5, 10, ZERO, -1L, ZERO, 0L);
    final var flat = whirlpool(0, ZERO, ZERO);
    final var quote = WhirlpoolQuote.collectFeesQuote(flat, maxOwed, tick(ZERO, ZERO), tick(ZERO, ZERO), null, null);
    assertEquals(-1L, quote.feeOwedA(), "u64::MAX survives as unsigned long bits");
    assertEquals(0L, quote.feeOwedB());

    // an accrued delta of 1 on top of u64::MAX overflows and must throw:
    // in range, feeGrowthInsideA = 1, liquidity = 2^64 -> delta = 1
    final var growth = whirlpool(7, BigInteger.ONE, ZERO);
    final var overflowing = position(BigInteger.ONE.shiftLeft(64), 5, 10, ZERO, -1L, ZERO, 0L);
    assertThrows(ArithmeticException.class, () ->
        WhirlpoolQuote.collectFeesQuote(growth, overflowing, tick(ZERO, ZERO), tick(ZERO, ZERO), null, null));
  }

  @Test
  void cyclicGrowthCheckpoint() {
    final var p = position(bi(91354442895L), 15168, 19648,
        bi("340282366920938463463368367551765494643"), 0L,
        bi("340282366920938463463235752370561182038"), 0L);
    final var pool = whirlpool(18158, bi(388775621815491196L), bi(2114651338550574490L));
    final var tl = tick(bi(334295763697402279L), bi(1816428862338027402L));
    final var tu = tick(bi(48907059211668900L), bi(369439434559592375L));
    final var r = WhirlpoolQuote.collectFeesQuote(pool, p, tl, tu, null, null);
    assertEquals(58500334L, r.feeOwedA());
    assertEquals(334966494L, r.feeOwedB());
  }
}

package software.sava.idl.clients.orca.quote;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/// Ported from `rust-sdk/core/src/quote/liquidity.rs#tests`.
final class LiquidityQuoteTests {

  private static final BigInteger BELOW = new BigInteger("18354745142194483561");
  private static final BigInteger IN = new BigInteger("18446744073709551616");
  private static final BigInteger ABOVE = new BigInteger("18539204128674405812");

  // ----- decrease_liquidity_quote -----

  @Test
  void decreaseLiquidityQuote_below() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuote(BigInteger.valueOf(1_000_000),
        100, BELOW, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_000), r.liquidityDelta());
    assertEquals(999L, r.tokenEstA());
    assertEquals(0L, r.tokenEstB());
    assertEquals(989L, r.tokenMinA());
    assertEquals(0L, r.tokenMinB());
  }

  @Test
  void decreaseLiquidityQuote_inRange() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuote(BigInteger.valueOf(1_000_000),
        100, IN, -10, 10, null, null);
    assertEquals(499L, r.tokenEstA());
    assertEquals(499L, r.tokenEstB());
    assertEquals(494L, r.tokenMinA());
    assertEquals(494L, r.tokenMinB());
  }

  @Test
  void decreaseLiquidityQuote_above() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuote(BigInteger.valueOf(1_000_000),
        100, ABOVE, -10, 10, null, null);
    assertEquals(0L, r.tokenEstA());
    assertEquals(999L, r.tokenEstB());
    assertEquals(0L, r.tokenMinA());
    assertEquals(989L, r.tokenMinB());
  }

  @Test
  void decreaseLiquidityQuote_zero() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuote(BigInteger.ZERO,
        100, IN, -10, 10, null, null);
    assertEquals(BigInteger.ZERO, r.liquidityDelta());
    assertEquals(0L, r.tokenEstA());
    assertEquals(0L, r.tokenEstB());
  }

  // ----- decrease_liquidity_quote_a -----

  @Test
  void decreaseLiquidityQuoteA_below() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuoteA(1000L, 100, BELOW, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_049L), r.liquidityDelta());
    assertEquals(999L, r.tokenEstA());
  }

  @Test
  void decreaseLiquidityQuoteA_inRange() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuoteA(500L, 100, IN, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_300L), r.liquidityDelta());
    assertEquals(499L, r.tokenEstA());
    assertEquals(499L, r.tokenEstB());
  }

  @Test
  void decreaseLiquidityQuoteA_above() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuoteA(1000L, 100, ABOVE, -10, 10, null, null);
    assertEquals(BigInteger.ZERO, r.liquidityDelta());
  }

  // ----- decrease_liquidity_quote_b -----

  @Test
  void decreaseLiquidityQuoteB_below() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuoteB(1000L, 100, BELOW, -10, 10, null, null);
    assertEquals(BigInteger.ZERO, r.liquidityDelta());
  }

  @Test
  void decreaseLiquidityQuoteB_inRange() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuoteB(500L, 100, IN, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_300L), r.liquidityDelta());
  }

  @Test
  void decreaseLiquidityQuoteB_above() {
    final var r = WhirlpoolQuote.decreaseLiquidityQuoteB(1000L, 100, ABOVE, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_049L), r.liquidityDelta());
    assertEquals(999L, r.tokenEstB());
  }

  // ----- increase_liquidity_quote -----

  @Test
  void increaseLiquidityQuote_below() {
    final var r = WhirlpoolQuote.increaseLiquidityQuote(BigInteger.valueOf(1_000_000),
        100, BELOW, -10, 10, null, null);
    assertEquals(1000L, r.tokenEstA());
    assertEquals(0L, r.tokenEstB());
    assertEquals(1010L, r.tokenMaxA());
    assertEquals(0L, r.tokenMaxB());
  }

  @Test
  void increaseLiquidityQuote_inRange() {
    final var r = WhirlpoolQuote.increaseLiquidityQuote(BigInteger.valueOf(1_000_000),
        100, IN, -10, 10, null, null);
    assertEquals(500L, r.tokenEstA());
    assertEquals(500L, r.tokenEstB());
    assertEquals(505L, r.tokenMaxA());
    assertEquals(505L, r.tokenMaxB());
  }

  @Test
  void increaseLiquidityQuote_above() {
    final var r = WhirlpoolQuote.increaseLiquidityQuote(BigInteger.valueOf(1_000_000),
        100, ABOVE, -10, 10, null, null);
    assertEquals(0L, r.tokenEstA());
    assertEquals(1000L, r.tokenEstB());
    assertEquals(0L, r.tokenMaxA());
    assertEquals(1010L, r.tokenMaxB());
  }

  // ----- increase_liquidity_quote_a -----

  @Test
  void increaseLiquidityQuoteA_below() {
    final var r = WhirlpoolQuote.increaseLiquidityQuoteA(1000L, 100, BELOW, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_049L), r.liquidityDelta());
    assertEquals(1000L, r.tokenEstA());
    assertEquals(1010L, r.tokenMaxA());
  }

  @Test
  void increaseLiquidityQuoteA_inRange() {
    final var r = WhirlpoolQuote.increaseLiquidityQuoteA(500L, 100, IN, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_300L), r.liquidityDelta());
    assertEquals(500L, r.tokenEstA());
    assertEquals(500L, r.tokenEstB());
  }

  @Test
  void increaseLiquidityQuoteA_above() {
    final var r = WhirlpoolQuote.increaseLiquidityQuoteA(1000L, 100, ABOVE, -10, 10, null, null);
    assertEquals(BigInteger.ZERO, r.liquidityDelta());
  }

  // ----- increase_liquidity_quote_b -----

  @Test
  void increaseLiquidityQuoteB_below() {
    final var r = WhirlpoolQuote.increaseLiquidityQuoteB(1000L, 100, BELOW, -10, 10, null, null);
    assertEquals(BigInteger.ZERO, r.liquidityDelta());
  }

  @Test
  void increaseLiquidityQuoteB_inRange() {
    final var r = WhirlpoolQuote.increaseLiquidityQuoteB(500L, 100, IN, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_300L), r.liquidityDelta());
  }

  @Test
  void increaseLiquidityQuoteB_above() {
    final var r = WhirlpoolQuote.increaseLiquidityQuoteB(1000L, 100, ABOVE, -10, 10, null, null);
    assertEquals(BigInteger.valueOf(1_000_049L), r.liquidityDelta());
    assertEquals(1000L, r.tokenEstB());
  }
}

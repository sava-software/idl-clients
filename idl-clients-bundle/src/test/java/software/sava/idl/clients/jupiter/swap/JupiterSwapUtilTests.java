package software.sava.idl.clients.jupiter.swap;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class JupiterSwapUtilTests {

  // 1 SOL in lamports (9 decimals).
  private static final long ONE_SOL = 1_000_000_000L;
  // 150 USDC in micro-USDC (6 decimals).
  private static final long USDC_OUT = 150_000_000L;

  @Test
  void quotePriceExactDivision() {
    // 150 / 1 at scale outDecimals = 6.
    assertEquals(
        new BigDecimal("150.000000"),
        JupiterSwapUtil.quotePrice(ONE_SOL, 9, USDC_OUT, 6, RoundingMode.HALF_EVEN));
    // 0.5 SOL in for 80 USDC out: 80 / 0.5 = 160.
    assertEquals(
        new BigDecimal("160.000000"),
        JupiterSwapUtil.quotePrice(500_000_000L, 9, 80_000_000L, 6, RoundingMode.DOWN));
  }

  @Test
  void quotePriceRoundingModes() {
    // in = 3, out = 1: 1/3 at scale outDecimals = 9.
    assertEquals(
        new BigDecimal("0.333333333"),
        JupiterSwapUtil.quotePrice(3_000_000L, 6, ONE_SOL, 9, RoundingMode.DOWN));
    assertEquals(
        new BigDecimal("0.333333334"),
        JupiterSwapUtil.quotePrice(3_000_000L, 6, ONE_SOL, 9, RoundingMode.UP));
    assertEquals(
        new BigDecimal("0.333333333"),
        JupiterSwapUtil.quotePrice(3_000_000L, 6, ONE_SOL, 9, RoundingMode.HALF_EVEN));
  }

  @Test
  void quotePriceTieBreaking() {
    // in = 2, out = 3: 1.5 at scale outDecimals = 0.
    assertEquals(new BigDecimal("2"), JupiterSwapUtil.quotePrice(2_000_000L, 6, 3L, 0, RoundingMode.HALF_EVEN));
    assertEquals(BigDecimal.ONE, JupiterSwapUtil.quotePrice(2_000_000L, 6, 3L, 0, RoundingMode.HALF_DOWN));
    assertEquals(BigDecimal.ONE, JupiterSwapUtil.quotePrice(2_000_000L, 6, 3L, 0, RoundingMode.DOWN));
    assertEquals(new BigDecimal("2"), JupiterSwapUtil.quotePrice(2_000_000L, 6, 3L, 0, RoundingMode.UP));
    // in = 2, out = 5: 2.5 ties to the even neighbor 2 under HALF_EVEN, up to 3 under HALF_UP.
    assertEquals(new BigDecimal("2"), JupiterSwapUtil.quotePrice(2_000_000L, 6, 5L, 0, RoundingMode.HALF_EVEN));
    assertEquals(new BigDecimal("3"), JupiterSwapUtil.quotePrice(2_000_000L, 6, 5L, 0, RoundingMode.HALF_UP));
  }

  @Test
  void quotePriceMathContext() {
    // 1/3 to 5 significant digits.
    assertEquals(
        new BigDecimal("0.33333"),
        JupiterSwapUtil.quotePrice(3_000_000L, 6, ONE_SOL, 9, new MathContext(5, RoundingMode.HALF_EVEN)));
    assertEquals(
        new BigDecimal("0.33334"),
        JupiterSwapUtil.quotePrice(3_000_000L, 6, ONE_SOL, 9, new MathContext(5, RoundingMode.UP)));
    // Exact division keeps the preferred (stripped) scale: 150 comes back as 1.5E+2.
    assertEquals(
        new BigDecimal("1.5E+2"),
        JupiterSwapUtil.quotePrice(ONE_SOL, 9, USDC_OUT, 6, new MathContext(10, RoundingMode.HALF_EVEN)));
  }

  @Test
  void inverseQuotePriceExactDivision() {
    // 0.5 / 80 at scale inDecimals = 9.
    assertEquals(
        new BigDecimal("0.006250000"),
        JupiterSwapUtil.inverseQuotePrice(500_000_000L, 9, 80_000_000L, 6, RoundingMode.HALF_EVEN));
  }

  @Test
  void inverseQuotePriceRoundingModes() {
    // 1 / 150 = 0.00666... at scale inDecimals = 9.
    assertEquals(
        new BigDecimal("0.006666667"),
        JupiterSwapUtil.inverseQuotePrice(ONE_SOL, 9, USDC_OUT, 6, RoundingMode.HALF_EVEN));
    assertEquals(
        new BigDecimal("0.006666666"),
        JupiterSwapUtil.inverseQuotePrice(ONE_SOL, 9, USDC_OUT, 6, RoundingMode.DOWN));
    assertEquals(
        new BigDecimal("0.006666667"),
        JupiterSwapUtil.inverseQuotePrice(ONE_SOL, 9, USDC_OUT, 6, RoundingMode.UP));
    // in = 3, out = 2: 1.5 at scale inDecimals = 0 ties to even.
    assertEquals(new BigDecimal("2"), JupiterSwapUtil.inverseQuotePrice(3L, 0, 2_000_000L, 6, RoundingMode.HALF_EVEN));
    assertEquals(BigDecimal.ONE, JupiterSwapUtil.inverseQuotePrice(3L, 0, 2_000_000L, 6, RoundingMode.HALF_DOWN));
  }

  @Test
  void inverseQuotePriceMathContext() {
    // 1 / 150 to 5 significant digits.
    assertEquals(
        new BigDecimal("0.0066667"),
        JupiterSwapUtil.inverseQuotePrice(ONE_SOL, 9, USDC_OUT, 6, new MathContext(5, RoundingMode.HALF_EVEN)));
    assertEquals(
        new BigDecimal("0.0066666"),
        JupiterSwapUtil.inverseQuotePrice(ONE_SOL, 9, USDC_OUT, 6, new MathContext(5, RoundingMode.DOWN)));
    // Exact division: 0.5 / 80 = 0.00625.
    assertEquals(
        new BigDecimal("0.00625"),
        JupiterSwapUtil.inverseQuotePrice(500_000_000L, 9, 80_000_000L, 6, new MathContext(10, RoundingMode.HALF_EVEN)));
  }

  @Test
  void negativeAmountsAreTreatedAsUnsigned() {
    // -1L reinterpreted as u64 is 2^64 - 1 (DecimalInteger.toDecimal unsigned path).
    assertEquals(
        new BigDecimal("18446744073709551615"),
        JupiterSwapUtil.inverseQuotePrice(-1L, 0, 1L, 0, RoundingMode.DOWN));
    assertEquals(
        new BigDecimal("18446744073709551615"),
        JupiterSwapUtil.quotePrice(1L, 0, -1L, 0, RoundingMode.DOWN));
  }

  @Test
  void zeroOutAmount() {
    assertEquals(
        new BigDecimal("0.000000"),
        JupiterSwapUtil.quotePrice(ONE_SOL, 9, 0L, 6, RoundingMode.DOWN));
  }
}

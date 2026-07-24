package software.sava.idl.clients.jupiter.swap;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.swap.gen.JupiterProgram;
import software.sava.idl.clients.jupiter.swap.gen.types.RoutePlanStepV2;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterQuote;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapIx;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static software.sava.idl.clients.jupiter.swap.gen.JupiterProgram.ROUTE_V_2_DISCRIMINATOR;
import static software.sava.idl.clients.jupiter.swap.gen.JupiterProgram.SHARED_ACCOUNTS_ROUTE_V_2_DISCRIMINATOR;

/// Deterministic companion to `RouteV2DataFuzz`: `readData` must accept both
/// v2 discriminators — and only those — and carry each generated-record field
/// into its own slot; `amountsMatch` compares the on-chain route against the
/// REST quote on all three amounts; the price helpers must delegate with the
/// in/out amounts in the right order, since a transposition still yields a
/// plausible price.
final class RouteV2DataTests {

  private static final RoutePlanStepV2[] NO_STEPS = new RoutePlanStepV2[0];

  private static JupiterQuote quote(final long inAmount, final long outAmount) {
    return new JupiterQuote(null, inAmount, null, outAmount, 0L, null, 0, null, null, List.of(), 0L, 0d, null);
  }

  private static byte[] routeData(final boolean shared) {
    if (shared) {
      final var ixData = new JupiterProgram.SharedAccountsRouteV2IxData(
          SHARED_ACCOUNTS_ROUTE_V_2_DISCRIMINATOR, 1, 100L, 300L, 50, 25, 10, NO_STEPS);
      final byte[] data = new byte[ixData.l()];
      ixData.write(data, 0);
      return data;
    }
    final var ixData = new JupiterProgram.RouteV2IxData(
        ROUTE_V_2_DISCRIMINATOR, 100L, 300L, 50, 25, 10, NO_STEPS);
    final byte[] data = new byte[ixData.l()];
    ixData.write(data, 0);
    return data;
  }

  @Test
  void readDataAcceptsBothV2Discriminators() {
    for (final boolean shared : new boolean[]{false, true}) {
      final var route = RouteV2Data.readData(routeData(shared), 0);
      assertEquals(100L, route.inAmount(), "shared=" + shared);
      assertEquals(300L, route.quotedOutAmount());
      assertEquals(50, route.slippageBps());
      assertEquals(25, route.platformFeeBps());
      assertEquals(10, route.positiveSlippageBps());
      assertEquals(0, route.routePlan().length);
    }
  }

  @Test
  void readDataRejectsOtherDiscriminators() {
    final byte[] data = routeData(false);
    JupiterProgram.SHARED_ACCOUNTS_EXACT_OUT_ROUTE_V_2_DISCRIMINATOR.write(data, 0);
    assertThrows(UnsupportedOperationException.class, () -> RouteV2Data.readData(data, 0));
  }

  /// All three comparisons participate: the expected input against the route,
  /// the expected input against the quote, and the route's quoted output
  /// against the quote's output.
  @Test
  void amountsMatchComparesAllThreeAmounts() {
    final var route = RouteV2Data.readData(routeData(false), 0);

    assertTrue(route.amountsMatch(100L, quote(100L, 300L)));
    assertFalse(route.amountsMatch(101L, quote(101L, 300L)), "expectedIn must match the route's inAmount");
    assertFalse(route.amountsMatch(100L, quote(101L, 300L)), "expectedIn must match the quote's inAmount");
    assertFalse(route.amountsMatch(100L, quote(100L, 301L)), "quoted out must match the quote's outAmount");
    assertFalse(route.amountsMatch(101L, quote(100L, 300L)));
  }

  /// The price helpers delegate with `(inAmount, inDecimals, quotedOutAmount,
  /// outDecimals)` in that order — asymmetric decimals make a transposition
  /// visible, and the inverse must be the reciprocal orientation.
  @Test
  void priceHelpersDelegateWithTheAmountsInOrder() {
    final var route = RouteV2Data.readData(routeData(false), 0);
    final var mathContext = MathContext.DECIMAL64;

    assertEquals(
        JupiterSwapUtil.quotePrice(100L, 2, 300L, 5, RoundingMode.HALF_EVEN),
        route.quotePrice(2, 5, RoundingMode.HALF_EVEN)
    );
    assertEquals(
        JupiterSwapUtil.quotePrice(100L, 2, 300L, 5, mathContext),
        route.quotePrice(2, 5, mathContext)
    );
    assertEquals(
        JupiterSwapUtil.inverseQuotePrice(100L, 2, 300L, 5, RoundingMode.HALF_EVEN),
        route.inverseQuotePrice(2, 5, RoundingMode.HALF_EVEN)
    );
    assertEquals(
        JupiterSwapUtil.inverseQuotePrice(100L, 2, 300L, 5, mathContext),
        route.inverseQuotePrice(2, 5, mathContext)
    );
    assertNotEquals(route.quotePrice(2, 5, mathContext), route.inverseQuotePrice(2, 5, mathContext));
  }

  /// The REST quote exposes the same four helpers over its own amounts.
  @Test
  void jupiterQuotePriceHelpersDelegateWithTheAmountsInOrder() {
    final var quote = quote(100L, 300L);
    final var mathContext = MathContext.DECIMAL64;

    assertEquals(
        JupiterSwapUtil.quotePrice(100L, 2, 300L, 5, RoundingMode.HALF_EVEN),
        quote.quotePrice(2, 5, RoundingMode.HALF_EVEN)
    );
    assertEquals(
        JupiterSwapUtil.quotePrice(100L, 2, 300L, 5, mathContext),
        quote.quotePrice(2, 5, mathContext)
    );
    assertEquals(
        JupiterSwapUtil.inverseQuotePrice(100L, 2, 300L, 5, RoundingMode.HALF_EVEN),
        quote.inverseQuotePrice(2, 5, RoundingMode.HALF_EVEN)
    );
    assertEquals(
        JupiterSwapUtil.inverseQuotePrice(100L, 2, 300L, 5, mathContext),
        quote.inverseQuotePrice(2, 5, mathContext)
    );
  }

  @Test
  void toStringCarriesEveryField() {
    final var route = RouteV2Data.readData(routeData(false), 0);
    final var rendered = route.toString();
    assertTrue(rendered.contains("inAmount=100"), rendered);
    assertTrue(rendered.contains("quotedOutAmount=300"), rendered);
    assertTrue(rendered.contains("slippageBps=50"), rendered);
    assertTrue(rendered.contains("platformFeeBps=25"), rendered);
    assertTrue(rendered.contains("positiveSlippageBps=10"), rendered);
  }

  /// `JupiterSwapIx.readData` parses the route out of the instruction it
  /// wraps, honouring the instruction's own data offset.
  @Test
  void swapIxReadsItsRouteData() {
    final var program = AccountMeta.createInvoked(PublicKey.NONE);
    final var swapIx = new JupiterSwapIx(
        Instruction.createInstruction(program, List.of(), routeData(true)),
        List.of()
    );
    final var route = swapIx.readData();
    assertEquals(100L, route.inAmount());
    assertEquals(300L, route.quotedOutAmount());
  }
}

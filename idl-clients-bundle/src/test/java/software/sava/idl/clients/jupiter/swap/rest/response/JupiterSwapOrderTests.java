package software.sava.idl.clients.jupiter.swap.rest.response;

import org.junit.jupiter.api.Test;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapV2TestFixtures.*;

/// Parsing `GET /swap/v2/order` responses into [JupiterSwapOrder]. Every `BigDecimal` expectation
/// is written as the JSON spells it, because `BigDecimal#equals` compares scale: `5000` is
/// `new BigDecimal("5000")` and `5000.0` is `new BigDecimal("5000.0")`.
final class JupiterSwapOrderTests {

  private static JupiterSwapOrder parse(final String json) {
    return JupiterSwapOrder.parse(JsonIterator.parse(json.getBytes(UTF_8)));
  }

  /// `ORDER_METIS` with one fragment replaced, failing if the fragment is not in the fixture.
  private static String metisWith(final String fragment, final String replacement) {
    assertTrue(ORDER_METIS.contains(fragment), fragment);
    return ORDER_METIS.replace(fragment, replacement);
  }

  @Test
  void parsesEveryFieldOfTheMetisOrder() {
    final var order = parse(ORDER_METIS);

    assertEquals("ultra", order.mode());
    assertEquals(USDC_KEY, order.inputMint());
    assertEquals(WSOL_KEY, order.outputMint());
    assertEquals(100_000_000L, order.inAmount());
    assertEquals(461_208_958L, order.outAmount());
    assertEquals(new BigDecimal("99.96761068334662"), order.inUsdValue());
    assertEquals(new BigDecimal("99.95449893632635"), order.outUsdValue());
    assertEquals(new BigDecimal("-0.013115995201493341"), order.priceImpact());
    assertEquals(new BigDecimal("99.96761068334662"), order.swapUsdValue());
    assertEquals(460_024_271L, order.otherAmountThreshold());
    assertEquals("ExactIn", order.swapMode());
    assertEquals(new BigDecimal("26"), order.slippageBps());

    assertEquals(2, order.routePlan().size());
    final var meteora = order.routePlan().getFirst();
    assertEquals("HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR", meteora.ammKey());
    assertEquals("MeteoraDLMM", meteora.label());
    assertEquals(USDC_KEY, meteora.inputMint());
    assertEquals(WSOL_KEY, meteora.outputMint());
    assertEquals(52_000_000L, meteora.inAmount());
    assertEquals(239_879_552L, meteora.outAmount());
    assertEquals(new BigDecimal("52"), meteora.percent());
    assertEquals(new BigDecimal("5200"), meteora.bps());
    assertNull(meteora.usdValue());
    final var solFi = order.routePlan().getLast();
    assertEquals("3EKkiwNLWqoUbzFkPrmKbtUB4EweE6f4STzevYUmezeL", solFi.ammKey());
    assertEquals("SolFi", solFi.label());
    assertEquals(USDC_KEY, solFi.inputMint());
    assertEquals(WSOL_KEY, solFi.outputMint());
    assertEquals(48_000_000L, solFi.inAmount());
    assertEquals(221_329_406L, solFi.outAmount());
    assertEquals(new BigDecimal("48"), solFi.percent());
    assertEquals(new BigDecimal("4800"), solFi.bps());
    assertEquals(new BigDecimal("47.98"), solFi.usdValue());

    assertEquals(REFERRAL_KEY, order.referralAccount());
    assertEquals(WSOL_KEY, order.feeMint());
    assertEquals(new BigDecimal("2"), order.feeBps());
    assertEquals(new JupiterSwapPlatformFee(92_241L, new BigDecimal("2"), WSOL_KEY), order.platformFee());
    assertEquals(new BigDecimal("5000"), order.signatureFeeLamports());
    assertEquals(TAKER_KEY, order.signatureFeePayer());
    assertEquals(new BigDecimal("696237"), order.prioritizationFeeLamports());
    assertEquals(TAKER_KEY, order.prioritizationFeePayer());
    assertEquals(new BigDecimal("0"), order.rentFeeLamports());
    assertEquals(TAKER_KEY, order.rentFeePayer());
    assertEquals("metis", order.router());
    assertArrayEquals(new byte[]{1, 2, 3}, order.transaction());
    assertEquals(279_000_150L, order.lastValidBlockHeight());
    assertFalse(order.gasless());
    assertEquals("019974a8-5fbb-7395-9355-9ebf8f844884", order.requestId());
    assertEquals(new BigDecimal("359"), order.totalTime());
    assertEquals(TAKER_KEY, order.taker());
    assertNull(order.quoteId());
    assertNull(order.maker());
    assertNull(order.expireAt());
    assertEquals(0, order.errorCode());
    assertNull(order.errorMessage());

    assertTrue(order.hasTransaction());
    assertFalse(order.quoteOnly());
    assertFalse(order.transactionBuildFailed());
    assertNull(order.expireAtInstant());
  }

  @Test
  void anEmptyTransactionMeansTheBuildFailed() {
    final var order = parse(ORDER_RFQ_BUILD_FAILED);

    assertArrayEquals(new byte[0], order.transaction());
    assertTrue(order.transactionBuildFailed());
    assertFalse(order.hasTransaction());
    assertFalse(order.quoteOnly());
    assertEquals("jupiterz", order.router());
    assertEquals(3, order.errorCode());
    assertEquals("Quote could not be built into a transaction", order.errorMessage());

    assertEquals("ultra", order.mode());
    assertEquals(USDC_KEY, order.inputMint());
    assertEquals(WSOL_KEY, order.outputMint());
    assertEquals(100_000_000L, order.inAmount());
    assertEquals(460_250_418L, order.outAmount());
    assertEquals(460_250_418L, order.otherAmountThreshold());
    assertEquals("ExactIn", order.swapMode());
    assertEquals(new BigDecimal("0"), order.slippageBps());
    assertEquals(1, order.routePlan().size());
    final var step = order.routePlan().getFirst();
    assertEquals("CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48", step.ammKey());
    assertEquals("JupiterZ", step.label());
    assertEquals(100_000_000L, step.inAmount());
    assertEquals(460_250_418L, step.outAmount());
    assertEquals(new BigDecimal("100"), step.percent());
    assertEquals(new BigDecimal("10000"), step.bps());

    assertEquals(new BigDecimal("2.5"), order.feeBps());
    assertEquals(new JupiterSwapPlatformFee(92_050L, new BigDecimal("0.5"), null), order.platformFee());
    assertEquals(new BigDecimal("489.5"), order.totalTime());
    assertEquals(new BigDecimal("0"), order.signatureFeeLamports());
    assertEquals(new BigDecimal("0"), order.prioritizationFeeLamports());
    assertEquals(new BigDecimal("0"), order.rentFeeLamports());
    assertNull(order.signatureFeePayer());
    assertNull(order.prioritizationFeePayer());
    assertNull(order.rentFeePayer());
    assertNull(order.feeMint());
    assertNull(order.referralAccount());
    assertTrue(order.gasless());

    assertEquals("1758598698", order.expireAt());
    assertEquals(Instant.ofEpochSecond(1_758_598_698L), order.expireAtInstant());
    assertEquals(0L, order.lastValidBlockHeight());

    assertEquals("ff63982b-9140-9b0e-e525-44f7246a79b2", order.requestId());
    assertEquals("5852f88e-525b-5400-ab97-abe5e409ebfd", order.quoteId());
    assertEquals(FEE_ACCOUNT_KEY, order.maker());
    assertEquals(TAKER_KEY, order.taker());
  }

  @Test
  void aNullTransactionMeansNoTakerWasGiven() {
    final var order = parse(ORDER_QUOTE_ONLY);

    assertNull(order.transaction());
    assertTrue(order.quoteOnly());
    assertFalse(order.hasTransaction());
    assertFalse(order.transactionBuildFailed());

    assertEquals(new JupiterSwapPlatformFee(0L, new BigDecimal("2"), WSOL_KEY), order.platformFee());
    assertNull(order.signatureFeePayer());
    assertNull(order.prioritizationFeePayer());
    assertNull(order.rentFeePayer());
    assertNull(order.taker());
    assertEquals(new BigDecimal("25"), order.slippageBps());
    assertEquals(new BigDecimal("5000"), order.signatureFeeLamports());
    assertEquals(new BigDecimal("0"), order.prioritizationFeeLamports());
    assertEquals(new BigDecimal("0"), order.rentFeeLamports());
    assertEquals(0L, order.lastValidBlockHeight());

    assertEquals(List.of(), order.routePlan());
    assertEquals(WSOL_KEY, order.inputMint());
    assertEquals(USDC_KEY, order.outputMint());
    assertEquals(1_000_000L, order.inAmount());
    assertEquals(4_612_089L, order.outAmount());
    assertEquals(4_600_559L, order.otherAmountThreshold());
    assertEquals(WSOL_KEY, order.feeMint());
    assertEquals(new BigDecimal("2"), order.feeBps());
    assertEquals("metis", order.router());
    assertFalse(order.gasless());
    assertEquals("01997500-0000-7000-8000-000000000001", order.requestId());
    assertEquals(new BigDecimal("120"), order.totalTime());
    assertEquals(0, order.errorCode());
    assertNull(order.errorMessage());
  }

  @Test
  void eachTransactionStateSetsExactlyOnePredicate() {
    final var absent = parse("{\"requestId\": \"r\"}");
    assertTrue(absent.quoteOnly());
    assertFalse(absent.transactionBuildFailed());
    assertFalse(absent.hasTransaction());
    // an absent route plan is normalized to an empty, unmodifiable list
    assertEquals(List.of(), absent.routePlan());
    assertThrows(UnsupportedOperationException.class, () -> absent.routePlan().add(null));

    final var nullTransaction = parse("{\"transaction\": null}");
    assertTrue(nullTransaction.quoteOnly());
    assertFalse(nullTransaction.transactionBuildFailed());
    assertFalse(nullTransaction.hasTransaction());

    final var empty = parse("{\"transaction\": \"\"}");
    assertFalse(empty.quoteOnly());
    assertTrue(empty.transactionBuildFailed());
    assertFalse(empty.hasTransaction());

    final var oneByte = parse("{\"transaction\": \"AQ==\"}");
    assertArrayEquals(new byte[]{1}, oneByte.transaction());
    assertFalse(oneByte.quoteOnly());
    assertFalse(oneByte.transactionBuildFailed());
    assertTrue(oneByte.hasTransaction());

    final var threeBytes = parse("{\"transaction\": \"AQID\"}");
    assertFalse(threeBytes.quoteOnly());
    assertFalse(threeBytes.transactionBuildFailed());
    assertTrue(threeBytes.hasTransaction());
  }

  @Test
  void lastValidBlockHeightAcceptsAStringOrANumber() {
    assertEquals(279_000_150L, parse("{\"lastValidBlockHeight\": \"279000150\"}").lastValidBlockHeight());
    assertEquals(279_000_150L, parse("{\"lastValidBlockHeight\": 279000150}").lastValidBlockHeight());
    assertEquals(0L, parse("{\"lastValidBlockHeight\": \"\"}").lastValidBlockHeight());
    assertEquals(0L, parse("{\"lastValidBlockHeight\": null}").lastValidBlockHeight());
    assertEquals(0L, parse("{\"requestId\": \"r\"}").lastValidBlockHeight());
    // the field after it still reads
    final var order = parse("{\"lastValidBlockHeight\": 279000150.0, \"requestId\": \"r\"}");
    assertEquals(279_000_150L, order.lastValidBlockHeight());
    assertEquals("r", order.requestId());
  }

  /// JSON null or a non-boolean `gasless` reads as false and costs neither the fields after it nor
  /// the transaction; a strict boolean read would throw and lose the whole order.
  @Test
  void aNullOrNonBooleanGaslessReadsFalseWithoutCostingTheOrder() {
    for (final var value : List.of("null", "\"true\"", "1", "{\"a\": [true]}")) {
      final var order = parse(metisWith("\"gasless\": false,", "\"gasless\": " + value + ","));
      assertFalse(order.gasless(), value);
      assertTrue(order.hasTransaction(), value);
      assertArrayEquals(new byte[]{1, 2, 3}, order.transaction(), value);
      assertEquals("019974a8-5fbb-7395-9355-9ebf8f844884", order.requestId(), value);
      assertEquals(new BigDecimal("359"), order.totalTime(), value);
      assertEquals(TAKER_KEY, order.taker(), value);
    }
    // a JSON boolean is still read as sent
    assertTrue(parse(metisWith("\"gasless\": false,", "\"gasless\": true,")).gasless());
  }

  /// JSON null or a non-object `platformFee` reads as null and costs neither the fields after it nor
  /// the transaction; a strict object read would turn null into a zero fee and throw on anything else.
  @Test
  void aNullOrNonObjectPlatformFeeReadsNullWithoutCostingTheOrder() {
    final var fee = "\"platformFee\": {\"amount\": \"92241\", \"feeBps\": 2, \"feeMint\": \"" + WSOL + "\"},";
    for (final var value : List.of("null", "\"\"", "[]", "5")) {
      final var order = parse(metisWith(fee, "\"platformFee\": " + value + ","));
      assertNull(order.platformFee(), value);
      assertEquals(new BigDecimal("5000"), order.signatureFeeLamports(), value);
      assertTrue(order.hasTransaction(), value);
      assertArrayEquals(new byte[]{1, 2, 3}, order.transaction(), value);
      assertEquals("019974a8-5fbb-7395-9355-9ebf8f844884", order.requestId(), value);
      assertEquals(279_000_150L, order.lastValidBlockHeight(), value);
    }
  }

  @Test
  void feeEstimatesAndSlippageAreKeptAsSent() {
    assertEquals(new BigDecimal("5000"), parse("{\"signatureFeeLamports\": 5000}").signatureFeeLamports());
    assertEquals(new BigDecimal("5000.0"), parse("{\"signatureFeeLamports\": 5000.0}").signatureFeeLamports());
    assertEquals(new BigDecimal("5000"), parse("{\"signatureFeeLamports\": \"5000\"}").signatureFeeLamports());
    assertEquals(new BigDecimal("5e3"), parse("{\"signatureFeeLamports\": 5e3}").signatureFeeLamports());

    final var fractional = parse(metisWith(
        "\"prioritizationFeeLamports\": 696237,", "\"prioritizationFeeLamports\": 696237.25,"));
    assertEquals(new BigDecimal("696237.25"), fractional.prioritizationFeeLamports());
    assertArrayEquals(new byte[]{1, 2, 3}, fractional.transaction());
    assertTrue(fractional.hasTransaction());
    assertEquals(279_000_150L, fractional.lastValidBlockHeight());

    assertNull(parse("{\"rentFeeLamports\": null}").rentFeeLamports());
    assertNull(parse("{\"rentFeeLamports\": \"\"}").rentFeeLamports());
    assertNull(parse("{\"requestId\": \"r\"}").rentFeeLamports());

    assertEquals(new BigDecimal("26.5"), parse(metisWith("\"slippageBps\": 26,", "\"slippageBps\": 26.5,")).slippageBps());
    assertEquals(new BigDecimal("2.5"), parse("{\"feeBps\": 2.5}").feeBps());
    assertEquals(new BigDecimal("359.25"), parse("{\"totalTime\": 359.25}").totalTime());
  }

  /// These two stay strict: a block height and an enumerated code cannot be fractional, and
  /// `errorCode` is present only when there is no transaction to lose.
  @Test
  void integralByProtocolFieldsAcceptIntegralSpellingsAndRejectFractions() {
    assertEquals(3, parse("{\"errorCode\": 3.0}").errorCode());
    assertEquals(3, parse("{\"errorCode\": \"3\"}").errorCode());
    assertThrows(ArithmeticException.class, () -> parse("{\"errorCode\": 3.5}"));
    assertEquals(279_000_150L, parse("{\"lastValidBlockHeight\": \"279000150.0\"}").lastValidBlockHeight());
    assertThrows(ArithmeticException.class, () -> parse("{\"lastValidBlockHeight\": \"279000150.5\"}"));
  }

  @Test
  void aNonBase58AmmKeyIsKeptAsSent() {
    final var order = parse("""
        {"router":"okx","routePlan":[{"swapInfo":{"ammKey":"okx-pool","label":"OKX",
          "inputMint":"%s","outputMint":"%s","inAmount":"1000000","outAmount":"4612089"},
          "percent":100,"bps":10000}],"transaction":"AQID","requestId":"r"}""".formatted(WSOL, USDC));
    final var step = order.routePlan().getFirst();
    assertEquals("okx-pool", step.ammKey());
    assertEquals("OKX", step.label());
    assertEquals(WSOL_KEY, step.inputMint());
    assertEquals(USDC_KEY, step.outputMint());
    assertEquals(1_000_000L, step.inAmount());
    assertEquals(4_612_089L, step.outAmount());
    assertTrue(order.hasTransaction());
    assertEquals("okx", order.router());
    assertEquals("r", order.requestId());
  }

  @Test
  void expireAtIsKeptRawAndConvertedOnRequest() {
    final var absent = parse("{\"requestId\": \"r\"}");
    assertNull(absent.expireAt());
    assertNull(absent.expireAtInstant());

    final var nullExpiry = parse("{\"expireAt\": null}");
    assertNull(nullExpiry.expireAt());
    assertNull(nullExpiry.expireAtInstant());

    final var empty = parse("{\"expireAt\": \"\"}");
    assertEquals("", empty.expireAt());
    assertNull(empty.expireAtInstant());

    final var blank = parse("{\"expireAt\": \"  \"}");
    assertEquals("  ", blank.expireAt());
    assertNull(blank.expireAtInstant());

    final var soon = parse("{\"expireAt\": \"soon\"}");
    assertEquals("soon", soon.expireAt());
    assertThrows(NumberFormatException.class, soon::expireAtInstant);

    final var number = parse("{\"expireAt\": 1758598698, \"requestId\": \"r\"}");
    assertEquals("1758598698", number.expireAt());
    assertEquals(Instant.ofEpochSecond(1_758_598_698L), number.expireAtInstant());
    assertEquals("r", number.requestId());
  }

  @Test
  void emptyStringKeysReadAsAbsent() {
    final var order = parse("""
        {"referralAccount": "", "maker": "", "inputMint": "", "outputMint": "", "feeMint": "", "taker": "",
         "signatureFeePayer": "", "prioritizationFeePayer": "", "rentFeePayer": "",
         "platformFee": {"amount": "", "feeMint": ""}, "requestId": "r"}""");
    assertNull(order.referralAccount());
    assertNull(order.maker());
    assertNull(order.inputMint());
    assertNull(order.outputMint());
    assertNull(order.feeMint());
    assertNull(order.taker());
    assertNull(order.signatureFeePayer());
    assertNull(order.prioritizationFeePayer());
    assertNull(order.rentFeePayer());
    assertEquals(new JupiterSwapPlatformFee(0L, null, null), order.platformFee());
    assertEquals("r", order.requestId());
  }

  @Test
  void errorFillsErrorMessageOnlyWhenItIsMissing() {
    assertEquals("x", parse("{\"error\": \"x\"}").errorMessage());
    assertEquals("m", parse("{\"errorMessage\": \"m\"}").errorMessage());
    assertEquals("m", parse("{\"errorMessage\": \"m\", \"error\": \"x\"}").errorMessage());
    assertEquals("m", parse("{\"error\": \"x\", \"errorMessage\": \"m\"}").errorMessage());
    assertEquals("x", parse("{\"errorMessage\": \"\", \"error\": \"x\"}").errorMessage());
    assertEquals("x", parse("{\"error\": \"x\", \"errorMessage\": \"\"}").errorMessage());
    assertEquals("x", parse("{\"errorMessage\": null, \"error\": \"x\"}").errorMessage());
    assertNull(parse("{\"requestId\": \"r\"}").errorMessage());
    assertNull(parse("{\"errorMessage\": \"\"}").errorMessage());
  }
}

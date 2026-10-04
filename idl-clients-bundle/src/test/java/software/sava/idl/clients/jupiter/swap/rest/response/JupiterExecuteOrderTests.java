package software.sava.idl.clients.jupiter.swap.rest.response;

import org.junit.jupiter.api.Test;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapV2TestFixtures.USDC_KEY;
import static software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapV2TestFixtures.WSOL_KEY;

/// The `/execute` result parser that Swap API V2's `execute` and the Ultra `executeOrder` share.
/// Jupiter may already have landed the transaction a result reports on, and a failed parse costs
/// the caller `status` and `signature`, so an amount must parse as any u64 in decimal digits, the
/// slot as any integer in decimal digits, a swap event mint as any 32-byte base58 key, and each as
/// absent when it is JSON null or `""`. A minus sign, a decimal point or an exponent in an amount,
/// a decimal point or an exponent in the slot, and any other mint still fail it. So does a `code`
/// that is JSON null or `""`, because reading that as 0 would report success; a body with no
/// `code` key reads as 0, and every body Jupiter documents carries one except Swap API V2's 500
/// (`signature`, `error`), which fails the client's future before this parser sees it.
final class JupiterExecuteOrderTests {

  /// Amounts at and above 2^63, which only an unsigned read can hold.
  private static final String EXECUTE_ABOVE_LONG_MAX = """
      {"status": "Success", "signature": "large-amount-signature", "slot": 368661931, "code": 0,
       "totalInputAmount": "18446744073709551615", "totalOutputAmount": "9223372036854775808",
       "inputAmountResult": "18446744073709551615", "outputAmountResult": "9223372036854775808",
       "swapEvents": [{"inputMint": "So11111111111111111111111111111111111111112", "inputAmount": "18446744073709551615",
         "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", "outputAmount": "9223372036854775808"}]}
      """;

  /// Jupiter's documented aggregator failure body with the totals written as JSON null and `""`
  /// and the slot as `""`.
  private static final String EXECUTE_FAILED_NULL_TOTALS = """
      {"status": "Failed", "signature": "failed-signature", "slot": "", "code": -1005, "error": "Transaction expired",
       "totalInputAmount": null, "totalOutputAmount": ""}
      """;

  private static JupiterExecuteOrder parse(final String json) {
    final byte[] body = json.getBytes(UTF_8);
    return JupiterExecuteOrder.parse(body, JsonIterator.parse(body));
  }

  /// A Success body carrying `status` and `signature` ahead of `fields`, so a value in `fields`
  /// that fails the parse is seen to cost the caller both.
  private static String successBody(final String fields) {
    return "{\"status\": \"Success\", \"signature\": \"landed-signature\", \"code\": 0, " + fields + "}";
  }

  @Test
  void amountsAboveLongMaxValueReadAsUnsignedBits() {
    final byte[] body = EXECUTE_ABOVE_LONG_MAX.getBytes(UTF_8);
    final var result = JupiterExecuteOrder.parse(body, JsonIterator.parse(body));

    assertEquals("Success", result.status());
    assertEquals("large-amount-signature", result.signature());
    assertEquals(BigInteger.valueOf(368_661_931L), result.slot());
    assertEquals(0L, result.code());
    assertNull(result.error());
    assertEquals(-1L, result.totalInputAmount());
    assertEquals(Long.MIN_VALUE, result.totalOutputAmount());
    assertEquals("18446744073709551615", result.inputAmountResult());
    assertEquals("9223372036854775808", result.outputAmountResult());
    assertSame(body, result.responseJson());

    assertEquals(1, result.swapEvents().size());
    final var event = result.swapEvents().getFirst();
    assertEquals(WSOL_KEY, event.inputMint());
    assertEquals(-1L, event.inputAmount());
    assertEquals(USDC_KEY, event.outputMint());
    assertEquals(Long.MIN_VALUE, event.outputAmount());
  }

  @Test
  void nullAndEmptyTotalsKeepStatusAndSignature() {
    final var result = parse(EXECUTE_FAILED_NULL_TOTALS);

    assertEquals("Failed", result.status());
    assertEquals("failed-signature", result.signature());
    assertEquals(-1_005L, result.code());
    assertEquals("Transaction expired", result.error());
    assertNull(result.slot());
    assertEquals(0L, result.totalInputAmount());
    assertEquals(0L, result.totalOutputAmount());
    assertNull(result.inputAmountResult());
    assertNull(result.outputAmountResult());
    // absent, read as an empty list
    assertEquals(List.of(), result.swapEvents());
  }

  /// Three of the four failure bodies Jupiter documents for `/execute` (`ultra/response.mdx`), the
  /// RFQ one without its trailing comma. None carries `swapEvents`; the fourth, a program error,
  /// does.
  @Test
  void theDocumentedFailureBodiesParse() {
    final var ultra = parse("""
        {
            "code": -1,
            "error": "Order not found, it might have expired"
        }""");
    assertNull(ultra.status());
    assertNull(ultra.signature());
    assertEquals(-1L, ultra.code());
    assertEquals("Order not found, it might have expired", ultra.error());
    assertNull(ultra.slot());
    assertEquals(0L, ultra.totalInputAmount());
    assertEquals(0L, ultra.totalOutputAmount());
    assertEquals(List.of(), ultra.swapEvents());

    final var aggregator = parse("""
        {
            "status": "Failed",
            "slot": "0",
            "signature": "transaction-signature",
            "code": -1005,
            "error": "Transaction expired"
        }""");
    assertEquals("Failed", aggregator.status());
    assertEquals("transaction-signature", aggregator.signature());
    assertEquals(-1_005L, aggregator.code());
    assertEquals("Transaction expired", aggregator.error());
    assertEquals(BigInteger.ZERO, aggregator.slot());
    assertEquals(0L, aggregator.totalInputAmount());
    assertEquals(0L, aggregator.totalOutputAmount());
    assertEquals(List.of(), aggregator.swapEvents());

    final var rfq = parse("""
        {
            "status": "Failed",
            "slot": "0",
            "code": -2005,
            "error": "Internal error"
        }""");
    assertEquals("Failed", rfq.status());
    assertNull(rfq.signature());
    assertEquals(-2_005L, rfq.code());
    assertEquals("Internal error", rfq.error());
    assertEquals(BigInteger.ZERO, rfq.slot());
    assertEquals(0L, rfq.totalInputAmount());
    assertEquals(0L, rfq.totalOutputAmount());
    assertEquals(List.of(), rfq.swapEvents());
  }

  @Test
  void slotReadsNullAndEmptyAsNull() {
    assertNull(parse("{\"slot\": null, \"code\": 0, \"signature\": \"s\"}").slot());
    final var empty = parse("{\"slot\": \"\", \"code\": 0, \"signature\": \"s\"}");
    assertNull(empty.slot());
    assertEquals("s", empty.signature());
    assertEquals(BigInteger.ZERO, parse("{\"slot\": \"0\", \"code\": 0}").slot());
    assertEquals(BigInteger.valueOf(368_661_931L), parse("{\"slot\": 368661931, \"code\": 0}").slot());
    assertEquals(BigInteger.valueOf(368_661_931L), parse("{\"slot\": \"368661931\", \"code\": 0}").slot());
    assertEquals(new BigInteger("18446744073709551616"), parse("{\"slot\": \"18446744073709551616\"}").slot());
    assertEquals(BigInteger.valueOf(-5L), parse("{\"slot\": \"-5\", \"code\": 0}").slot());
    assertEquals(BigInteger.valueOf(-5L), parse("{\"slot\": -5, \"code\": 0}").slot());
    assertThrows(NumberFormatException.class, () -> parse("{\"slot\": \"1.5\", \"code\": 0}"));
  }

  /// The second event mirrors the first, so each mint and each amount is read once from `""` and
  /// once from JSON null: a reader that accepts only one of the two spellings fails this test.
  @Test
  void swapEventReadsEmptyMintsAsNullAndEmptyAmountsAsZero() {
    final var result = parse("""
        {"status": "Success", "signature": "s", "code": 0,
         "swapEvents": [{"inputMint": "", "inputAmount": null, "outputMint": null, "outputAmount": ""},
                        {"inputMint": null, "inputAmount": "", "outputMint": "", "outputAmount": null}]}""");
    assertEquals("Success", result.status());
    assertEquals("s", result.signature());
    assertEquals(List.of(new SwapEvent(null, 0L, null, 0L), new SwapEvent(null, 0L, null, 0L)), result.swapEvents());
  }

  /// Three of the four documented failure bodies omit `swapEvents`, and an absent key, a JSON null
  /// and `[]` must all leave the caller an empty list rather than a null one. No list a parse
  /// returns can be changed.
  @Test
  void swapEventsIsNeverNullAndCannotBeChanged() {
    final var event = new SwapEvent(WSOL_KEY, 1L, USDC_KEY, 2L);
    for (final var fields : List.of(
        "\"error\": \"failed to land\"",
        "\"error\": \"failed to land\", \"swapEvents\": null",
        "\"error\": \"failed to land\", \"swapEvents\": []")) {
      final var result = parse("{\"status\": \"Failed\", \"signature\": \"sig\", \"code\": -1000, " + fields + "}");
      assertEquals("sig", result.signature(), fields);
      assertEquals(-1_000L, result.code(), fields);
      assertEquals(List.of(), result.swapEvents(), fields);
      assertThrows(UnsupportedOperationException.class, () -> result.swapEvents().add(event), fields);
    }

    final var one = parse(successBody("""
        "swapEvents": [{"inputMint": "So11111111111111111111111111111111111111112", "inputAmount": "1",
          "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", "outputAmount": "2"}]"""));
    assertEquals(List.of(event), one.swapEvents());
    assertThrows(UnsupportedOperationException.class, () -> one.swapEvents().add(event));
    assertThrows(UnsupportedOperationException.class, () -> one.swapEvents().remove(0));
  }

  /// A regression pin for the record's `List.copyOf`, which rejects a null element: a JSON-null
  /// element reads as an event with every field absent, so it cannot fail the parse and cost the
  /// caller `status` and `signature`.
  @Test
  void aJsonNullSwapEventReadsAsAnEventWithEveryFieldAbsent() {
    final var result = parse(successBody("\"swapEvents\": [null]"));
    assertEquals("Success", result.status());
    assertEquals("landed-signature", result.signature());
    assertEquals(List.of(new SwapEvent(null, 0L, null, 0L)), result.swapEvents());
  }

  @Test
  void constructionNormalizesAndCopiesSwapEvents() {
    final var event = new SwapEvent(WSOL_KEY, 1L, USDC_KEY, 2L);
    final var absent = new JupiterExecuteOrder("Failed", "sig", null, "e", -1_000L, 0L, 0L, null, null, null, null);
    assertEquals(List.of(), absent.swapEvents());
    assertThrows(UnsupportedOperationException.class, () -> absent.swapEvents().add(event));

    // a list passed in is copied: changing it afterwards never changes the record
    final var events = new ArrayList<SwapEvent>();
    events.add(event);
    final var copied = new JupiterExecuteOrder("Success", "sig", null, null, 0L, 1L, 2L, null, null, events, null);
    events.add(new SwapEvent(USDC_KEY, 2L, WSOL_KEY, 1L));
    assertEquals(List.of(event), copied.swapEvents());
    assertThrows(UnsupportedOperationException.class, () -> copied.swapEvents().add(event));

    final var withNull = new ArrayList<SwapEvent>();
    withNull.add(null);
    assertThrows(NullPointerException.class,
        () -> new JupiterExecuteOrder("Success", "sig", null, null, 0L, 0L, 0L, null, null, withNull, null));
  }

  /// A negative amount parsed, as a negative long, before amounts became unsigned; a u64 cannot be negative.
  @Test
  void aNegativeAmountIsRejected() {
    assertThrows(NumberFormatException.class,
        () -> parse("{\"status\": \"Success\", \"code\": 0, \"totalInputAmount\": \"-1\"}"));
    assertThrows(NumberFormatException.class,
        () -> parse("{\"status\": \"Success\", \"code\": 0, \"totalOutputAmount\": \"-1\"}"));
    assertThrows(NumberFormatException.class, () -> parse("""
        {"status": "Success", "code": 0,
         "swapEvents": [{"inputMint": "So11111111111111111111111111111111111111112", "outputAmount": "-1"}]}"""));
    assertThrows(NumberFormatException.class, () -> parse("""
        {"status": "Success", "code": 0, "swapEvents": [{"inputAmount": "-1"}]}"""));
  }

  /// An amount or the slot is read from its decimal digits alone, so a decimal point or an exponent
  /// fails the parse even where the value is whole.
  @Test
  void aDecimalPointOrAnExponentFailsEvenInAWholeValue() {
    for (final var value : List.of("\"1000.0\"", "\"1e3\"", "1000.0", "1e3")) {
      for (final var fields : List.of(
          "\"totalInputAmount\": " + value,
          "\"totalOutputAmount\": " + value,
          "\"swapEvents\": [{\"inputAmount\": " + value + "}]",
          "\"swapEvents\": [{\"outputAmount\": " + value + "}]",
          "\"slot\": " + value)) {
        assertThrowsExactly(NumberFormatException.class, () -> parse(successBody(fields)), fields);
      }
    }
  }

  /// The largest u64 reads, in `amountsAboveLongMaxValueReadAsUnsignedBits`; one more does not.
  @Test
  void anAmountAboveTheLargestU64IsRejected() {
    for (final var fields : List.of(
        "\"totalInputAmount\": \"18446744073709551616\"",
        "\"totalOutputAmount\": 18446744073709551616",
        "\"swapEvents\": [{\"inputAmount\": \"18446744073709551616\"}]",
        "\"swapEvents\": [{\"outputAmount\": 18446744073709551616}]")) {
      assertThrowsExactly(NumberFormatException.class, () -> parse(successBody(fields)), fields);
    }
  }

  /// `""` reads as absent; any other string that is not a number fails the parse.
  @Test
  void aNonNumericStringFailsWhereAnEmptyOneReadsAsAbsent() {
    final var empty = parse(successBody("\"totalInputAmount\": \"\", \"slot\": \"\""));
    assertEquals("Success", empty.status());
    assertEquals("landed-signature", empty.signature());
    assertEquals(0L, empty.totalInputAmount());
    assertNull(empty.slot());

    for (final var fields : List.of(
        "\"totalInputAmount\": \"abc\"",
        "\"swapEvents\": [{\"outputAmount\": \"abc\"}]",
        "\"slot\": \"abc\"")) {
      assertThrowsExactly(NumberFormatException.class, () -> parse(successBody(fields)), fields);
    }
    assertThrowsExactly(NumberFormatException.class,
        () -> parse("{\"status\": \"Failed\", \"signature\": \"s\", \"code\": \"abc\"}"));
  }

  /// `assertThrowsExactly`, because `NumberFormatException` is an `IllegalArgumentException` too and
  /// would pass a looser check. The mints carry an illegal character, decode to too few bytes and
  /// decode to too many.
  @Test
  void aMintThatIsNotA32ByteBase58KeyIsRejected() {
    for (final var mint : List.of("0OIl", "1111", "z".repeat(45))) {
      for (final var field : List.of("inputMint", "outputMint")) {
        final var fields = "\"swapEvents\": [{\"" + field + "\": \"" + mint + "\"}]";
        assertThrowsExactly(IllegalArgumentException.class, () -> parse(successBody(fields)), fields);
      }
    }
  }

  @Test
  void codeIsNeverDefaulted() {
    for (final var code : List.of("null", "\"\"")) {
      final var e = assertThrowsExactly(IllegalStateException.class,
          () -> parse("{\"status\": \"Failed\", \"signature\": \"s\", \"code\": " + code + "}"), code);
      assertEquals("code is required", e.getMessage());
    }
  }

  /// Swap API V2's documented 500 body (`openapi-spec/swap/v2/swap.yaml`) is `signature` and `error`,
  /// the one body of the two APIs this parser serves with no `code` property. The client fails its
  /// future on a 5xx before this parser sees it, but a body recovered from the failure's message
  /// parses as `code` 0 with no `status`, so `code` 0 alone is not success. A `code` that is present
  /// and null or `""` fails the parse instead (`codeIsNeverDefaulted`).
  @Test
  void anAbsentCodeKeyReadsAsZeroWithNoStatus() {
    final var result = parse("{\"signature\": \"s\", \"error\": \"Internal server error\"}");

    assertEquals(0L, result.code());
    assertNull(result.status());
    assertEquals("s", result.signature());
    assertEquals("Internal server error", result.error());
    assertEquals(List.of(), result.swapEvents());
  }

  @Test
  void codeAcceptsIntegralSpellingsWithoutMisaligning() {
    for (final var code : List.of("-1000.0", "-1e3", "-1000", "\"-1000\"")) {
      final var result = parse("{\"code\": " + code + ", \"error\": \"e\", \"signature\": \"s\"}");
      assertEquals(-1_000L, result.code(), code);
      assertEquals("e", result.error(), code);
      assertEquals("s", result.signature(), code);
    }
    assertThrows(ArithmeticException.class, () -> parse("{\"code\": -1000.5, \"error\": \"e\"}"));
  }

  /// `BigDecimal` represents every exponent in the first list, so each value reaches the range
  /// check of a long and fails it; it cannot represent those in the second, which fail first.
  @Test
  void codeOutsideALongIsAnArithmeticErrorUnlessBigDecimalCannotRepresentItsExponent() {
    assertEquals(Long.MAX_VALUE,
        parse("{\"status\": \"Failed\", \"signature\": \"s\", \"code\": 9223372036854775807}").code());
    assertEquals(Long.MIN_VALUE,
        parse("{\"status\": \"Failed\", \"signature\": \"s\", \"code\": -9223372036854775808}").code());
    for (final var code : List.of(
        "9223372036854775808", "-9223372036854775809", "1e999999999", "\"1e999999999\"", "1e-999999999")) {
      assertThrowsExactly(ArithmeticException.class,
          () -> parse("{\"status\": \"Failed\", \"signature\": \"s\", \"code\": " + code + "}"), code);
    }
    for (final var code : List.of("1e-2147483648", "\"1e-2147483648\"", "1e99999999999")) {
      assertThrowsExactly(NumberFormatException.class,
          () -> parse("{\"status\": \"Failed\", \"signature\": \"s\", \"code\": " + code + "}"), code);
    }
  }
}

package software.sava.idl.clients.jupiter.swap.rest;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletionException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

/// Covers the Jupiter REST client against an in-JVM HTTP server.
///
/// Two things here are logic rather than plumbing, and both are invisible to a
/// test that only checks the happy path: the raw `swap-instructions` response is
/// gated on an explicit status-code range rather than on the HTTP client's own
/// error handling, and the dex-label map rejects labels that collide
/// case-insensitively — a silent overwrite there would map a DEX to the wrong
/// program id.
final class JupiterSwapApiClientTests extends JupiterRestTests {

  /// The *local* client is used because it serves unprefixed paths
  /// (`/quote`, `/swap`, ...) rather than the hosted API's `/swap/v1` prefix.
  private final JupiterSwapApiClient client = buildClient();

  private JupiterSwapApiClient buildClient() {
    final var builder = JupiterSwapApiClient.build();
    builder.httpClient(HTTP_CLIENT);
    builder.endpoint(endpoint);
    builder.apiKey("test-api-key");
    return builder.createLocalClient();
  }

  private static final String WSOL = "So11111111111111111111111111111111111111112";
  private static final String USDC = "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v";
  private static final Duration TIMEOUT = Duration.ofSeconds(30);

  // ---------------------------------------------------------------------------
  // swap-instructions: the explicit status-code gate
  // ---------------------------------------------------------------------------

  /// The success range is `200 <= status < 300`, checked by hand so the raw body
  /// can be returned unparsed. The boundaries are what the mutants move, so all
  /// four are asserted: 199 and 300 must fail, 200 and 299 must succeed.
  @Test
  void swapInstructionsAcceptsExactlyTheTwoHundredRange() {
    final String body = """
        {"swapInstruction":{"programId":"%s","accounts":[],"data":"AQ=="}}""".formatted(WSOL);

    // 200 — the low boundary, inclusive
    expectPost("/swap-instructions", null, 200, body);
    assertArrayEquals(body.getBytes(UTF_8),
        client.swapInstructions("{\"userPublicKey\":\"x\",", new byte[]{'{', '}'}).join());

    // 299 — the high boundary, inclusive
    expectPost("/swap-instructions", null, 299, body);
    assertArrayEquals(body.getBytes(UTF_8),
        client.swapInstructions("{\"userPublicKey\":\"x\",", new byte[]{'{', '}'}).join());
  }

  /// Outside the range the client raises rather than handing back an error page
  /// as if it were instruction data — the failure carries the status code and
  /// body so a caller can tell a rate limit from a bad request.
  @Test
  void swapInstructionsRejectsAnythingOutsideTheTwoHundredRange() {
    // 300 is the exclusive high end, and the boundary worth pinning: a `>= 300`
    // relaxed to `> 300` accepts an HTTP 300 as instruction data, and only a
    // test at exactly 300 catches it.
    //
    // The low boundary (199) cannot be exercised through a real socket — the
    // JDK's HttpClient treats 1xx as interim responses and never surfaces one as
    // a final status, so the server hangs up and the exchange fails with an
    // EOFException before the client's own check runs. The `< 200` half of the
    // guard is therefore unreachable here by construction, not untested.
    for (final int status : new int[]{300, 400, 429, 500}) {
      expectPost("/swap-instructions", null, status, "{\"error\":\"nope\"}");
      final var failure = assertThrows(CompletionException.class,
          () -> client.swapInstructions("{\"userPublicKey\":\"x\",", new byte[]{'{', '}'}).join());
      final var message = failure.getCause().getMessage();
      assertTrue(message.contains(Integer.toString(status)),
          "the status code must reach the caller, got: " + message);
      assertTrue(message.contains("nope"), "the response body must reach the caller: " + message);
    }
  }

  /// The body is assembled as `prefix + quoteJson + '}'`, so the quote is
  /// embedded rather than sent as a separate field. Pinning it here means a
  /// change to that concatenation shows up as a request mismatch.
  @Test
  void swapInstructionsEmbedsTheQuoteInTheRequestBody() {
    final String prefix = "{\"userPublicKey\":\"" + WSOL + "\",";
    final String quoteJson = "\"quoteResponse\":{\"inAmount\":\"1\"}";

    expectPost("/swap-instructions", prefix + quoteJson + '}', 200, "{}");
    client.swapInstructions(prefix, quoteJson.getBytes(UTF_8)).join();

    // the StringBuilder overload produces the same body
    expectPost("/swap-instructions", prefix + quoteJson + '}', 200, "{}");
    client.swapInstructions(new StringBuilder(prefix), quoteJson.getBytes(UTF_8)).join();
  }

  // ---------------------------------------------------------------------------
  // program-id-to-label
  // ---------------------------------------------------------------------------

  /// The response is keyed by program id with the DEX label as the value, and
  /// the client inverts it into label -> program id.
  @Test
  void dexLabelMapIsInvertedAndCaseInsensitive() {
    expectGet("/program-id-to-label", """
        {"%s":"Orca","%s":"Raydium"}""".formatted(WSOL, USDC));

    final var labels = client.dexLabelToProgramIdMap().join();

    assertEquals(2, labels.size());
    assertEquals(WSOL, labels.get("Orca").toBase58());
    assertEquals(USDC, labels.get("Raydium").toBase58());

    // lookups ignore case, which is the point of the comparator
    assertEquals(WSOL, labels.get("orca").toBase58());
    assertEquals(WSOL, labels.get("ORCA").toBase58());
    assertNull(labels.get("Meteora"));
  }

  /// Two program ids whose labels differ only by case would silently overwrite
  /// each other in a case-insensitive map, leaving one DEX pointing at the
  /// other's program. The client refuses instead.
  @Test
  void collidingDexLabelsAreRejectedRatherThanOverwritten() {
    expectGet("/program-id-to-label", """
        {"%s":"Orca","%s":"orca"}""".formatted(WSOL, USDC));

    final var failure = assertThrows(CompletionException.class,
        () -> client.dexLabelToProgramIdMap().join());
    assertInstanceOf(IllegalStateException.class, failure.getCause());
    assertTrue(failure.getCause().getMessage().toLowerCase().contains("duplicate"),
        "the failure should name the duplication: " + failure.getCause().getMessage());

    // an empty response is not an error, just an empty map
    expectGet("/program-id-to-label", "{}");
    assertTrue(client.dexLabelToProgramIdMap().join().isEmpty());
  }

  // ---------------------------------------------------------------------------
  // request construction
  // ---------------------------------------------------------------------------

  /// `quote` has two shapes: one that formats the amount into the path and one
  /// that takes a fully-built query. Both must land on `/quote` with the query
  /// intact — a swapped format argument would put the amount in the wrong place.
  @Test
  void quoteBuildsItsPathAndQuery() {
    final String quote = """
        {"inputMint":"%s","inAmount":"1000","outputMint":"%s","outAmount":"25",\
        "otherAmountThreshold":"24","swapMode":"ExactIn","slippageBps":50,\
        "priceImpactPct":"0","routePlan":[],"contextSlot":1,"timeTaken":0.1}"""
        .formatted(WSOL, USDC);

    expectGet("/quote?amount=1000&inputMint=" + WSOL + "&outputMint=" + USDC, quote);
    final var parsed = client.quote(
        BigInteger.valueOf(1_000L), "inputMint=" + WSOL + "&outputMint=" + USDC).join();

    assertEquals(1_000L, parsed.inAmount());
    assertEquals(25L, parsed.outAmount());
    assertEquals(WSOL, parsed.inputMint().toBase58());
    assertEquals(USDC, parsed.outputMint().toBase58());
    assertEquals(50, parsed.slippageBps());
    // the raw JSON is retained so it can be embedded in the swap request
    assertArrayEquals(quote.getBytes(UTF_8), parsed.quoteResponseJson());

    // the query-only overload appends to the same path
    expectGet("/quote?amount=1000&inputMint=" + WSOL, quote);
    assertNotNull(client.quote("amount=1000&inputMint=" + WSOL).join());
  }

  /// `swap` posts to its own endpoint and returns the serialized transaction.
  @Test
  void swapPostsTheQuoteAndReturnsTheTransaction() {
    final String prefix = "{\"userPublicKey\":\"" + WSOL + "\",";
    final String quoteJson = "\"quoteResponse\":{}";
    final String response = """
        {"swapTransaction":"AQID","lastValidBlockHeight":123}""";

    expectPost("/swap", prefix + quoteJson + '}', 200, response);
    final var tx = client.swap(prefix, quoteJson.getBytes(UTF_8)).join();

    assertNotNull(tx);
    assertArrayEquals(new byte[]{1, 2, 3}, tx.swapTransaction());
  }

  /// The ultra endpoints live under a different path prefix than the swap ones,
  /// and `executeOrder` sends both of its arguments in the body.
  @Test
  void ultraOrderAndExecuteUseTheUltraPaths() {
    expectGet("/ultra/v1/order?amount=500&inputMint=" + WSOL, """
        {"requestId":"abc","transaction":"AQID"}""");
    final var order = client.ultraOrder(BigInteger.valueOf(500L), "inputMint=" + WSOL, null).join();
    assertNotNull(order);
    assertEquals("abc", order.requestId());

    expectPost("/ultra/v1/execute", """
        {
         "signedTransaction": "AQID",
         "requestId": "abc"
        }""", 200, """
        {"status":"Success","signature":"sig"}""");
    final var executed = client.executeOrder("AQID", "abc").join();
    assertNotNull(executed);
  }

  /// `swap` and `swapInstructions` each come in eight shapes: `String` or
  /// `StringBuilder` prefix, a `JupiterQuote` or its raw JSON, and with or
  /// without an explicit timeout. All eight must build the identical body — the
  /// quote embedded between the prefix and a closing brace — so this drives each
  /// one and lets the harness assert the body it produced.
  @Test
  void everySwapOverloadBuildsTheSameRequestBody() {
    final String quoteJson =
        """
        {"inputMint":"%s","inAmount":"1000","outputMint":"%s","outAmount":"25",\
        "otherAmountThreshold":"24","swapMode":"ExactIn","slippageBps":50,\
        "priceImpactPct":"0","routePlan":[],"contextSlot":1,"timeTaken":0.1}"""
            .formatted(WSOL, USDC);

    // fetch a quote so the JupiterQuote-taking overloads have a real one; it
    // retains its own response JSON, which is what gets embedded
    expectGet("/quote?amount=1000&inputMint=" + WSOL, quoteJson);
    final var quote = client.quote(BigInteger.valueOf(1_000L), "inputMint=" + WSOL).join();
    assertArrayEquals(quoteJson.getBytes(UTF_8), quote.quoteResponseJson());

    final String prefix = "{\"userPublicKey\":\"" + WSOL + "\",";
    final String expectedBody = prefix + quoteJson + '}';
    final String swapResponse = """
        {"swapTransaction":"AQID","lastValidBlockHeight":123}""";

    record Call(String label, java.util.function.Supplier<Object> invoke) {
    }

    final var swaps = List.of(
        new Call("String + quote", () -> client.swap(prefix, quote).join()),
        new Call("String + json", () -> client.swap(prefix, quote.quoteResponseJson()).join()),
        new Call("Builder + quote", () -> client.swap(new StringBuilder(prefix), quote).join()),
        new Call("Builder + json", () -> client.swap(new StringBuilder(prefix), quote.quoteResponseJson()).join()),
        new Call("String + quote + timeout", () -> client.swap(prefix, quote, TIMEOUT).join()),
        new Call("String + json + timeout", () -> client.swap(prefix, quote.quoteResponseJson(), TIMEOUT).join()),
        new Call("Builder + quote + timeout", () -> client.swap(new StringBuilder(prefix), quote, TIMEOUT).join()),
        new Call("Builder + json + timeout", () -> client.swap(new StringBuilder(prefix), quote.quoteResponseJson(), TIMEOUT).join()));

    for (final var call : swaps) {
      expectPost("/swap", expectedBody, 200, swapResponse);
      assertNotNull(call.invoke().get(), call.label());
    }

    final String instructionsResponse = "{}";
    final var instructions = List.of(
        new Call("String + quote", () -> client.swapInstructions(prefix, quote).join()),
        new Call("Builder + quote", () -> client.swapInstructions(new StringBuilder(prefix), quote).join()),
        new Call("String + quote + timeout", () -> client.swapInstructions(prefix, quote, TIMEOUT).join()),
        new Call("Builder + quote + timeout", () -> client.swapInstructions(new StringBuilder(prefix), quote, TIMEOUT).join()),
        new Call("String + json + timeout", () -> client.swapInstructions(prefix, quote.quoteResponseJson(), TIMEOUT).join()),
        new Call("Builder + json + timeout", () -> client.swapInstructions(new StringBuilder(prefix), quote.quoteResponseJson(), TIMEOUT).join()));

    for (final var call : instructions) {
      expectPost("/swap-instructions", expectedBody, 200, instructionsResponse);
      assertNotNull(call.invoke().get(), call.label());
    }
  }

  /// The query-only `ultraOrder` overload resolves against the ultra path
  /// without formatting an amount into it.
  @Test
  void ultraOrderQueryOnlyOverloadResolvesTheUltraPath() {
    expectGet("/ultra/v1/order?inputMint=" + WSOL + "&amount=7", """
        {"requestId":"xyz","transaction":"AQID"}""");
    final var order = client.ultraOrder("inputMint=" + WSOL + "&amount=7", TIMEOUT).join();
    assertEquals("xyz", order.requestId());
  }
}

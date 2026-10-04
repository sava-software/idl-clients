package software.sava.idl.clients.jupiter.swap.rest;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.swap.rest.request.BroadcastFeeType;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapBuildRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapExecuteRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapOrderRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.SwapMode;
import software.sava.idl.clients.jupiter.swap.rest.response.SwapEvent;
import systems.comodal.jsoniter.JsonIterator;

import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.UnknownServiceException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapV2TestFixtures.*;

/// Covers [JupiterSwapV2Client] against an in-JVM HTTP server: the four paths it resolves, the query or body each
/// call sends, the timeout each call applies, how `/program-id-to-label` is read, what a non-2xx answer carries, what
/// a `testResponse` predicate does, and the headers a built client keeps.
///
/// Every client is built inside the test that uses it. A client built in a field initializer under `PER_CLASS`
/// attaches its URL wiring to whichever test runs first, so a mutant in that wiring could never be paired with the
/// request that exercises it. Expected paths and bodies are literals written out by hand, never the output of the
/// request under test.
final class JupiterSwapV2ClientTests extends JupiterRestTests {

  /// B1: the four required `/build` parameters.
  private static final String MINIMAL_BUILD_QUERY = "inputMint=So11111111111111111111111111111111111111112&outputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&amount=100000000&taker=GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ";
  /// O1: every `/order` parameter.
  private static final String FULL_ORDER_QUERY = "inputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&outputMint=So11111111111111111111111111111111111111112&amount=100000000&taker=GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ&receiver=9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn&swapMode=ExactIn&slippageBps=0&referralAccount=3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT&referralFee=50&payer=gasTzr94Pmp4Gf8vknQnqxeYxdgwFjbgdJa4msYRpnB&priorityFeeLamports=0&jitoTipLamports=10000&broadcastFeeType=exactFee&excludeRouters=jupiterz%2Cdflow&excludeDexes=Orca+V2";
  /// O2: a quote-only `/order`, without a taker.
  private static final String QUOTE_ONLY_ORDER_QUERY = "inputMint=So11111111111111111111111111111111111111112&outputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&amount=1000000";

  private static final String REQUEST_ID = "019974a8-5fbb-7395-9355-9ebf8f844884";
  /// E1: the `/execute` body of `create("AQID", REQUEST_ID)`.
  private static final String EXECUTE_BODY = "{\"signedTransaction\":\"AQID\",\"requestId\":\"019974a8-5fbb-7395-9355-9ebf8f844884\"}";
  /// E2: E1 with a `lastValidBlockHeight` of 279000150, sent as a JSON string.
  private static final String EXECUTE_BODY_WITH_HEIGHT = "{\"signedTransaction\":\"AQID\",\"requestId\":\"019974a8-5fbb-7395-9355-9ebf8f844884\",\"lastValidBlockHeight\":\"279000150\"}";

  private static final String LABELS_PATH = "/swap/v2/program-id-to-label";

  /// The B1 request.
  private static JupiterSwapBuildRequest.Builder minimalBuild() {
    return JupiterSwapBuildRequest.buildRequest()
        .inputMint(WSOL_KEY)
        .outputMint(USDC_KEY)
        .amount(100_000_000)
        .taker(TAKER_KEY);
  }

  /// The O1 request.
  private static JupiterSwapOrderRequest fullOrder() {
    return JupiterSwapOrderRequest.buildRequest()
        .inputMint(USDC_KEY)
        .outputMint(WSOL_KEY)
        .amount(100_000_000)
        .taker(TAKER_KEY)
        .receiver(OTHER_KEY)
        .swapMode(SwapMode.ExactIn)
        .slippageBps(0)
        .referralAccount(REFERRAL_KEY)
        .referralFee(50)
        .payer(PAYER_KEY)
        .priorityFeeLamports(0)
        .jitoTipLamports(10_000)
        .broadcastFeeType(BroadcastFeeType.exactFee)
        .excludeRouters(List.of("jupiterz", "dflow"))
        .excludeDexes(List.of("Orca V2"))
        .createRequest();
  }

  /// The O2 request.
  private static JupiterSwapOrderRequest quoteOnlyOrder() {
    return JupiterSwapOrderRequest.buildRequest()
        .inputMint(WSOL_KEY)
        .outputMint(USDC_KEY)
        .amount(1_000_000)
        .createRequest();
  }

  /// Joins `call`'s future and asserts it failed as sava-rpc fails a non-2xx answer: a `CompletionException` around an
  /// `UncheckedIOException` around an `UnknownServiceException`, whose message carries `status` and `body`.
  private static void assertStatusFailure(final int status,
                                          final String body,
                                          final Supplier<CompletableFuture<?>> call) {
    final var future = call.get();
    final var failure = assertThrows(CompletionException.class, future::join);
    final var cause = assertInstanceOf(UncheckedIOException.class, failure.getCause());
    assertInstanceOf(UnknownServiceException.class, cause.getCause());
    final var message = cause.getMessage();
    assertTrue(message.contains("[httpCode:" + status + ']'), "the status must reach the caller: " + message);
    assertTrue(message.contains("[body=" + body + ']'), "the body must reach the caller: " + message);
  }

  /// With no endpoint and no key the client builds, against `https://api.jup.ag`, and the four paths resolve under
  /// `/swap/v2` when `createClient()` runs: a dropped default-endpoint guard fails here without any request.
  @Test
  void defaultEndpointsResolveUnderSwapV2AtBuildTime() {
    final var builder = JupiterSwapV2Client.build();
    final var client = builder.createClient();

    assertEquals(URI.create("https://api.jup.ag"), client.endpoint());
    assertEquals(URI.create("https://api.jup.ag/swap/v2/build"), builder.buildURI);
    assertEquals(URI.create("https://api.jup.ag/swap/v2/order"), builder.orderURI);
    assertEquals(URI.create("https://api.jup.ag/swap/v2/execute"), builder.executeURI);
    assertEquals(URI.create("https://api.jup.ag/swap/v2/program-id-to-label"), builder.programIdToLabelURI);
  }

  /// One fresh client issues all four calls, each against the path it resolved at build time. It is built with the
  /// chain the `Builder` javadoc shows, so the compiler checks that claim, and the configured key reaches every path,
  /// so a call that stops going through the client's request extension fails here.
  @Test
  void urlWiringIsCoveredFromInsideTheTest() {
    final var client = JupiterSwapV2Client.build()
        .apiKey("test-api-key")
        .httpClient(HTTP_CLIENT)
        .endpoint(endpoint)
        .createClient();
    // checked before any request: had the builder replaced the endpoint, every call below would leave the JVM
    assertEquals(endpoint, client.endpoint());

    // buildURI = endpoint.resolve("/swap/v2/build")
    expectGet("/swap/v2/build?" + MINIMAL_BUILD_QUERY, BUILD);
    lastRequestHeaders = null;
    assertEquals(100_000_000L, client.buildSwap(minimalBuild()).join().inAmount());
    assertEquals(List.of("test-api-key"), lastRequestHeaders.allValues("x-api-key"), "/build");

    // orderURI = endpoint.resolve("/swap/v2/order")
    expectGet("/swap/v2/order?" + QUOTE_ONLY_ORDER_QUERY, ORDER_QUOTE_ONLY);
    lastRequestHeaders = null;
    assertTrue(client.order(quoteOnlyOrder()).join().quoteOnly());
    assertEquals(List.of("test-api-key"), lastRequestHeaders.allValues("x-api-key"), "/order");

    // executeURI = endpoint.resolve("/swap/v2/execute")
    expectPost("/swap/v2/execute", EXECUTE_BODY, EXECUTE_SUCCESS);
    lastRequestHeaders = null;
    assertEquals("Success", client.execute(JupiterSwapExecuteRequest.create("AQID", REQUEST_ID)).join().status());
    assertEquals(List.of("test-api-key"), lastRequestHeaders.allValues("x-api-key"), "/execute");

    // programIdToLabelURI = endpoint.resolve("/swap/v2/program-id-to-label")
    expectGet(LABELS_PATH, PROGRAM_ID_TO_LABEL);
    lastRequestHeaders = null;
    assertEquals(7, client.programIdToLabel().join().size());
    assertEquals(List.of("test-api-key"), lastRequestHeaders.allValues("x-api-key"), "/program-id-to-label");
  }

  /// `buildSwap` sends `GET /swap/v2/build?` followed by the request's query. The expected path is the B1 literal,
  /// never `request.serialize()`, so a dropped `build` segment or a reordered parameter shows up as a mismatch.
  @Test
  void buildSwapSendsTheSerializedQueryAndParsesTheResponse() {
    final var client = JupiterSwapV2Client.build().httpClient(HTTP_CLIENT).endpoint(endpoint).createClient();

    expectGet("/swap/v2/build?" + MINIMAL_BUILD_QUERY, BUILD);
    final var build = client.buildSwap(minimalBuild().createRequest()).join();

    assertEquals(WSOL_KEY, build.inputMint());
    assertEquals(USDC_KEY, build.outputMint());
    assertEquals(100_000_000L, build.inAmount());
    assertEquals(461_208_958L, build.outAmount());
    assertEquals(460_024_271L, build.otherAmountThreshold());
    assertEquals("ExactIn", build.swapMode());
    assertEquals(50, build.slippageBps());
    assertEquals(new BigDecimal("0.0001"), build.priceImpactPct());
    assertEquals(2, build.routePlan().size());
    assertEquals(JUP_KEY, build.swapInstruction().programId().publicKey());
    assertEquals(6, build.instructions().size());
    assertEquals(5, build.instructionsWithoutComputeBudget().size());
    assertEquals(OptionalLong.of(1_234_567L), build.computeUnitPriceMicroLamports());
    assertEquals(14, build.distinctAccountCount(TAKER_KEY));
    assertEquals(279_000_150L, build.blockhashWithMetadata().lastValidBlockHeight());

    // a Builder handed straight to the client is a request too, serialized the same way
    expectGet("/swap/v2/build?" + MINIMAL_BUILD_QUERY, BUILD);
    assertEquals(461_208_958L, client.buildSwap(minimalBuild()).join().outAmount());
  }

  /// An invalid request throws `IllegalStateException` from `buildSwap` itself: `serialize()` runs before the
  /// future exists, so nothing is sent. A valid exchange is queued first and consumed last, which proves neither
  /// rejected call reached the server.
  @Test
  void buildSwapRejectsAnInvalidRequestBeforeSending() {
    final var client = JupiterSwapV2Client.build().httpClient(HTTP_CLIENT).endpoint(endpoint).createClient();
    expectGet("/swap/v2/build?" + MINIMAL_BUILD_QUERY, BUILD);
    lastRequestHeaders = null;

    final var contradictory = minimalBuild().dexes(List.of("Orca V2")).excludeDexes(List.of("SolFi"));
    final var contradiction = assertThrows(IllegalStateException.class, () -> client.buildSwap(contradictory));
    assertEquals("dexes and excludeDexes are mutually exclusive", contradiction.getMessage());

    final var withoutTaker = JupiterSwapBuildRequest.buildRequest()
        .inputMint(WSOL_KEY)
        .outputMint(USDC_KEY)
        .amount(100_000_000);
    final var missing = assertThrows(IllegalStateException.class, () -> client.buildSwap(withoutTaker));
    assertEquals("/swap/v2/build requires taker", missing.getMessage());

    assertNull(lastRequestHeaders, "a rejected request must not reach the server");
    assertEquals(100_000_000L, client.buildSwap(minimalBuild()).join().inAmount());
  }

  /// `order` sends `GET /swap/v2/order?` followed by the request's query, here every parameter (O1), and parses the
  /// Metis response.
  @Test
  void orderSendsTheSerializedQueryAndParsesTheResponse() {
    final var client = JupiterSwapV2Client.build().httpClient(HTTP_CLIENT).endpoint(endpoint).createClient();

    expectGet("/swap/v2/order?" + FULL_ORDER_QUERY, ORDER_METIS);
    final var order = client.order(fullOrder()).join();

    assertTrue(order.hasTransaction());
    assertArrayEquals(new byte[]{1, 2, 3}, order.transaction());
    assertEquals("metis", order.router());
    assertEquals("019974a8-5fbb-7395-9355-9ebf8f844884", order.requestId());
    assertEquals(279_000_150L, order.lastValidBlockHeight());
    assertEquals(USDC_KEY, order.inputMint());
    assertEquals(WSOL_KEY, order.outputMint());
    assertEquals(100_000_000L, order.inAmount());
    assertEquals(461_208_958L, order.outAmount());
    assertEquals(new BigDecimal("26"), order.slippageBps());
    assertEquals(TAKER_KEY, order.taker());
    assertEquals(2, order.routePlan().size());
  }

  /// `execute` posts the request's JSON body as `application/json` and parses the result. The second exchange
  /// carries a request id that needs escaping, and a non-ASCII letter that only survives if the body goes out as
  /// UTF-8.
  @Test
  void executePostsTheEscapedBodyAndParsesTheResult() {
    final var client = JupiterSwapV2Client.build().httpClient(HTTP_CLIENT).endpoint(endpoint).createClient();

    expectPost("/swap/v2/execute", EXECUTE_BODY, EXECUTE_SUCCESS);
    final var result = client.execute(JupiterSwapExecuteRequest.create("AQID", REQUEST_ID)).join();

    assertEquals("Success", result.status());
    assertEquals("transaction-signature", result.signature());
    assertEquals(BigInteger.valueOf(323598314), result.slot());
    assertEquals(0L, result.code());
    assertEquals(10_000_000L, result.totalInputAmount());
    assertEquals(1_274_698L, result.totalOutputAmount());
    assertEquals(List.of(new SwapEvent(WSOL_KEY, 9_995_000L, USDC_KEY, 1_274_698L)), result.swapEvents());
    assertArrayEquals(EXECUTE_SUCCESS.getBytes(UTF_8), result.responseJson());
    assertEquals(List.of("application/json"), lastRequestHeaders.allValues("Content-Type"));

    // E3: a quote, a backslash, two control characters, a space and a non-ASCII letter
    final var requestId = "a\"b\\c" + (char) 0x01 + (char) 0x1f + " é";
    expectPost("/swap/v2/execute",
        "{\"signedTransaction\":\"AQID\",\"requestId\":\"a\\\"b\\\\c\\u0001\\u001f é\"}",
        EXECUTE_SUCCESS);
    assertEquals("Success", client.execute(JupiterSwapExecuteRequest.create("AQID", requestId)).join().status());
  }

  /// A positive `lastValidBlockHeight` reaches the wire as a JSON string (E2).
  @Test
  void executeSendsAPositiveLastValidBlockHeightAsAString() {
    final var client = JupiterSwapV2Client.build().httpClient(HTTP_CLIENT).endpoint(endpoint).createClient();

    expectPost("/swap/v2/execute", EXECUTE_BODY_WITH_HEIGHT, EXECUTE_SUCCESS);
    final var result = client.execute(new JupiterSwapExecuteRequest("AQID", REQUEST_ID, 279000150)).join();
    assertEquals("transaction-signature", result.signature());
  }

  /// `execute` without a timeout, or with a null one, uses the client's default; a given timeout replaces it. The
  /// request extension records the timeout of every request built, so the exact list also proves no request is
  /// built in the constructor.
  @Test
  void executeAppliesTheGivenTimeoutOrTheClientDefault() {
    final var seen = new ArrayList<Duration>();
    final var builder = JupiterSwapV2Client.build();
    builder.httpClient(HTTP_CLIENT);
    builder.endpoint(endpoint);
    builder.requestTimeout(Duration.ofSeconds(7));
    builder.extendRequest(r -> {
      seen.add(r.copy().build().timeout().orElseThrow());
      return r;
    });
    final var client = builder.createClient();
    final var request = JupiterSwapExecuteRequest.create("AQID", REQUEST_ID);

    expectPost("/swap/v2/execute", EXECUTE_BODY, EXECUTE_SUCCESS);
    assertEquals("Success", client.execute(request).join().status());
    expectPost("/swap/v2/execute", EXECUTE_BODY, EXECUTE_SUCCESS);
    assertEquals("Success", client.execute(request, null).join().status());
    expectPost("/swap/v2/execute", EXECUTE_BODY, EXECUTE_SUCCESS);
    assertEquals("Success", client.execute(request, Duration.ofSeconds(90)).join().status());

    assertEquals(List.of(Duration.ofSeconds(7), Duration.ofSeconds(7), Duration.ofSeconds(90)), seen);
  }

  /// `execute` rejects a timeout that is zero or negative with an `IllegalArgumentException` from the call itself,
  /// raised by the JDK's request builder before the request extension runs and before anything is sent. One
  /// nanosecond, the smallest positive timeout, is accepted for the same request, so the rejection is the timeout's
  /// alone. Sent as it is, that request would expire before the mock answered, so the extension swaps in a longer
  /// timeout once it has recorded the one the builder accepted. A valid exchange is queued first and consumed last
  /// by that accepted call, which proves neither rejected call reached the server.
  @Test
  void executeRejectsANonPositiveTimeoutBeforeSending() {
    final var seen = new ArrayList<Duration>();
    final var client = JupiterSwapV2Client.build()
        .httpClient(HTTP_CLIENT)
        .endpoint(endpoint)
        .extendRequest(r -> {
          seen.add(r.copy().build().timeout().orElseThrow());
          return r.timeout(Duration.ofSeconds(30));
        })
        .createClient();
    final var request = JupiterSwapExecuteRequest.create("AQID", REQUEST_ID);
    expectPost("/swap/v2/execute", EXECUTE_BODY, EXECUTE_SUCCESS);
    lastRequestHeaders = null;

    assertThrows(IllegalArgumentException.class, () -> client.execute(request, Duration.ZERO));
    assertThrows(IllegalArgumentException.class, () -> client.execute(request, Duration.ofNanos(-1)));
    assertNull(lastRequestHeaders, "a rejected timeout must not reach the server");
    assertEquals(List.of(), seen, "a rejected timeout must not reach the request extension");

    assertEquals("Success", client.execute(request, Duration.ofNanos(1)).join().status());
    assertEquals(List.of(Duration.ofNanos(1)), seen);
  }

  /// A zero or negative timeout set on the builder passes `createClient()`, and every call that applies it throws
  /// `IllegalArgumentException` from the call itself, before the request extension runs and before anything is sent:
  /// `buildSwap`, `order`, `programIdToLabel`, and `execute` without a timeout or with a null one. A valid exchange is
  /// queued first and consumed last by an `execute` that brings its own timeout, which proves no rejected call reached
  /// the server and that a per-call timeout still replaces the builder's.
  @Test
  void aNonPositiveBuilderTimeoutRejectsEveryCallThatAppliesItBeforeSending() {
    final var request = JupiterSwapExecuteRequest.create("AQID", REQUEST_ID);
    for (final var timeout : List.of(Duration.ZERO, Duration.ofNanos(-1))) {
      final var seen = new ArrayList<Duration>();
      final var client = JupiterSwapV2Client.build()
          .httpClient(HTTP_CLIENT)
          .endpoint(endpoint)
          .requestTimeout(timeout)
          .extendRequest(r -> {
            seen.add(r.copy().build().timeout().orElseThrow());
            return r;
          })
          .createClient();
      expectPost("/swap/v2/execute", EXECUTE_BODY, EXECUTE_SUCCESS);
      lastRequestHeaders = null;

      assertThrows(IllegalArgumentException.class, () -> client.buildSwap(minimalBuild()), timeout::toString);
      assertThrows(IllegalArgumentException.class, () -> client.order(quoteOnlyOrder()), timeout::toString);
      assertThrows(IllegalArgumentException.class, () -> client.execute(request), timeout::toString);
      assertThrows(IllegalArgumentException.class, () -> client.execute(request, null), timeout::toString);
      assertThrows(IllegalArgumentException.class, client::programIdToLabel, timeout::toString);
      assertNull(lastRequestHeaders, "a rejected timeout must not reach the server");
      assertEquals(List.of(), seen, "a rejected timeout must not reach the request extension");

      assertEquals("Success", client.execute(request, Duration.ofSeconds(30)).join().status());
      assertEquals(List.of(Duration.ofSeconds(30)), seen);
    }
  }

  /// The map runs program id to label, as the endpoint does, in response order: labels keep their case, two
  /// programs may share one, and nothing is inverted or rejected. It cannot be modified, and an empty or JSON null
  /// body reads as an empty map.
  @Test
  void programIdToLabelKeepsTheEndpointsDirectionCaseAndDuplicates() {
    final var client = JupiterSwapV2Client.build().httpClient(HTTP_CLIENT).endpoint(endpoint).createClient();

    expectGet(LABELS_PATH, PROGRAM_ID_TO_LABEL);
    final var labels = client.programIdToLabel().join();

    assertEquals(List.of(
        PublicKey.fromBase58Encoded("SoLFiHG9TfgtdUXUjWAxi3LtvYuFyDLVhBWxdMZxyCe"),
        PublicKey.fromBase58Encoded("whirLbMiicVdio4qvUfM5KAg6Ct8VwpYzGff3uctyCc"),
        PublicKey.fromBase58Encoded("LBUZKhRxPF3XUpBCjp4YzTKgLccjZhTSDM9YuVaPwxo"),
        PublicKey.fromBase58Encoded("675kPX9MHTjS2zt1qfr1NYHuzeLXfQM9H24wFSUt1Mp8"),
        PublicKey.fromBase58Encoded("3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT"),
        PublicKey.fromBase58Encoded("9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn"),
        PublicKey.fromBase58Encoded("D8cy77BBepLMngZx6ZukaTff5hCt1HrWyKk3Hnd9oitf")
    ), new ArrayList<>(labels.keySet()));
    assertEquals(
        List.of("SolFi", "Whirlpool", "Meteora DLMM", "Raydium", "Sanctum", "Sanctum", "solfi"),
        new ArrayList<>(labels.values())
    );
    assertThrows(UnsupportedOperationException.class, () -> labels.put(WSOL_KEY, "Wrapped SOL"));

    expectGet(LABELS_PATH, "{}");
    assertTrue(client.programIdToLabel().join().isEmpty());

    expectGet(LABELS_PATH, "null");
    assertTrue(client.programIdToLabel().join().isEmpty());
  }

  /// A program id the body repeats keeps the label it was given last, at the position it first appeared.
  @Test
  void aRepeatedProgramIdKeepsItsLastLabel() {
    final var labels = JupiterSwapV2ClientImpl.parseProgramIdToLabel(JsonIterator.parse("""
        {"SoLFiHG9TfgtdUXUjWAxi3LtvYuFyDLVhBWxdMZxyCe":"first","whirLbMiicVdio4qvUfM5KAg6Ct8VwpYzGff3uctyCc":"Whirlpool","SoLFiHG9TfgtdUXUjWAxi3LtvYuFyDLVhBWxdMZxyCe":"SolFi"}"""
    ));

    final var solFi = PublicKey.fromBase58Encoded("SoLFiHG9TfgtdUXUjWAxi3LtvYuFyDLVhBWxdMZxyCe");
    final var whirlpool = PublicKey.fromBase58Encoded("whirLbMiicVdio4qvUfM5KAg6Ct8VwpYzGff3uctyCc");
    assertEquals(2, labels.size());
    assertEquals("SolFi", labels.get(solFi));
    assertEquals("Whirlpool", labels.get(whirlpool));
    assertEquals(List.of(solFi, whirlpool), new ArrayList<>(labels.keySet()));
  }

  /// A non-2xx answer fails the future with its status and body in the message, on every endpoint. For `/execute`
  /// that message is the only place a 500's signature survives.
  @Test
  void nonSuccessStatusesCarryTheStatusAndBody() {
    final var client = JupiterSwapV2Client.build().httpClient(HTTP_CLIENT).endpoint(endpoint).createClient();

    expectGet("/swap/v2/build?" + MINIMAL_BUILD_QUERY, 400, "{\"error\":\"No routes found\"}");
    assertStatusFailure(400, "{\"error\":\"No routes found\"}", () -> client.buildSwap(minimalBuild()));

    expectGet("/swap/v2/order?" + QUOTE_ONLY_ORDER_QUERY, 429, "{\"error\":\"Too many requests\"}");
    assertStatusFailure(429, "{\"error\":\"Too many requests\"}", () -> client.order(quoteOnlyOrder()));

    expectPost("/swap/v2/execute", EXECUTE_BODY, 500, "{\"signature\":\"s\",\"error\":\"e\"}");
    assertStatusFailure(500, "{\"signature\":\"s\",\"error\":\"e\"}",
        () -> client.execute(JupiterSwapExecuteRequest.create("AQID", REQUEST_ID)));

    expectGet(LABELS_PATH, 503, "upstream unavailable");
    assertStatusFailure(503, "upstream unavailable", client::programIdToLabel);
  }

  /// A `testResponse` predicate that returns false completes every call with null, never a parsed value or a status
  /// failure, as the `Builder` javadoc promises. The predicate records the status and body it was shown, which proves
  /// each call reached the server and got the answer queued for it: the mock answers a mismatched request with a 400,
  /// which an always-false predicate would turn into null as well. A predicate that accepts only 200 turns a 429 into
  /// null and still parses the 200 that follows.
  @Test
  void aRejectingTestResponseCompletesEveryCallWithNull() {
    final var seen = new ArrayList<String>();
    final var rejecting = JupiterSwapV2Client.build();
    rejecting.httpClient(HTTP_CLIENT);
    rejecting.endpoint(endpoint);
    assertSame(rejecting, rejecting.testResponse((response, body) -> {
      seen.add(response.statusCode() + " " + new String(body, UTF_8));
      return false;
    }));
    final var client = rejecting.createClient();

    expectGet("/swap/v2/build?" + MINIMAL_BUILD_QUERY, BUILD);
    assertNull(client.buildSwap(minimalBuild()).join());
    expectGet("/swap/v2/build?" + MINIMAL_BUILD_QUERY, 400, "{\"error\":\"No routes found\"}");
    assertNull(client.buildSwap(minimalBuild()).join());

    expectGet("/swap/v2/order?" + QUOTE_ONLY_ORDER_QUERY, ORDER_QUOTE_ONLY);
    assertNull(client.order(quoteOnlyOrder()).join());
    expectGet("/swap/v2/order?" + QUOTE_ONLY_ORDER_QUERY, 429, "{\"error\":\"Too many requests\"}");
    assertNull(client.order(quoteOnlyOrder()).join());

    expectPost("/swap/v2/execute", EXECUTE_BODY, EXECUTE_SUCCESS);
    assertNull(client.execute(JupiterSwapExecuteRequest.create("AQID", REQUEST_ID)).join());
    expectPost("/swap/v2/execute", EXECUTE_BODY, 500, "{\"signature\":\"s\",\"error\":\"e\"}");
    assertNull(client.execute(JupiterSwapExecuteRequest.create("AQID", REQUEST_ID)).join());

    expectGet(LABELS_PATH, PROGRAM_ID_TO_LABEL);
    assertNull(client.programIdToLabel().join());
    expectGet(LABELS_PATH, 503, "upstream unavailable");
    assertNull(client.programIdToLabel().join());

    assertEquals(List.of(
        "200 " + BUILD,
        "400 {\"error\":\"No routes found\"}",
        "200 " + ORDER_QUOTE_ONLY,
        "429 {\"error\":\"Too many requests\"}",
        "200 " + EXECUTE_SUCCESS,
        "500 {\"signature\":\"s\",\"error\":\"e\"}",
        "200 " + PROGRAM_ID_TO_LABEL,
        "503 upstream unavailable"
    ), seen);

    final var statusAware = JupiterSwapV2Client.build();
    statusAware.httpClient(HTTP_CLIENT);
    statusAware.endpoint(endpoint);
    assertSame(statusAware, statusAware.testResponse((response, body) -> response.statusCode() == 200));
    final var only200 = statusAware.createClient();

    expectGet(LABELS_PATH, 429, "{\"error\":\"Too many requests\"}");
    assertNull(only200.programIdToLabel().join());
    expectGet(LABELS_PATH, PROGRAM_ID_TO_LABEL);
    assertEquals(7, only200.programIdToLabel().join().size());
  }

  /// A key is optional. Without one, or with a blank one, the client builds and its requests carry no `x-api-key`
  /// header, which Jupiter serves as keyless traffic; a configured key is sent as given; and a caller's
  /// `extendRequest` applies either way. Each scenario builds a fresh client inside the test and reads the headers
  /// of its one request.
  @Test
  void keylessAcceptanceOnTheV2Client() {
    record Scenario(String name,
                    Consumer<JupiterClientBuilder<?>> configure,
                    List<String> apiKey,
                    List<String> caller) {
    }
    final var scenarios = List.of(
        new Scenario("apiKey never called", builder -> {
        }, List.of(), List.of()),
        new Scenario("blank apiKey", builder -> builder.apiKey("   "), List.of(), List.of()),
        new Scenario("apiKey", builder -> builder.apiKey("real-key"), List.of("real-key"), List.of()),
        new Scenario("extendRequest without a key",
            builder -> builder.extendRequest(r -> r.header("x-caller", "1")),
            List.of(), List.of("1")),
        new Scenario("extendRequest and apiKey", builder -> {
          builder.extendRequest(r -> r.header("x-caller", "1"));
          builder.apiKey("real-key");
        }, List.of("real-key"), List.of("1")));

    for (final var scenario : scenarios) {
      final var builder = JupiterSwapV2Client.build();
      builder.httpClient(HTTP_CLIENT);
      builder.endpoint(endpoint);
      scenario.configure().accept(builder);
      final var client = assertDoesNotThrow(builder::createClient, scenario.name());

      expectGet(LABELS_PATH, "{}");
      lastRequestHeaders = null;
      assertTrue(client.programIdToLabel().join().isEmpty(), scenario.name());

      final var headers = lastRequestHeaders;
      assertNotNull(headers, scenario.name());
      assertEquals(scenario.apiKey(), headers.allValues("x-api-key"), scenario.name());
      assertEquals(scenario.caller(), headers.allValues("x-caller"), scenario.name());
    }
  }

  /// A built client keeps the key, request extension and endpoint its builder held at `createClient()`: changing
  /// all three afterwards leaves the next request on the mock server with the original headers.
  @Test
  void aBuiltClientIgnoresLaterBuilderChanges() {
    final var builder = JupiterSwapV2Client.build();
    builder.httpClient(HTTP_CLIENT);
    builder.endpoint(endpoint);
    builder.apiKey("first-key");
    builder.extendRequest(r -> r.header("x-caller", "1"));
    final var client = builder.createClient();

    builder.apiKey("second-key");
    builder.extendRequest(r -> r.header("x-late", "1"));
    // loopback only: a client that followed the builder here would fail at once rather than reach the network
    builder.endpoint(URI.create("http://127.0.0.1:1"));

    assertEquals(endpoint, client.endpoint());
    expectGet(LABELS_PATH, "{}");
    lastRequestHeaders = null;
    assertTrue(client.programIdToLabel().join().isEmpty());

    final var headers = lastRequestHeaders;
    assertNotNull(headers, "the request must reach the mock server");
    assertEquals(List.of("first-key"), headers.allValues("x-api-key"));
    assertEquals(List.of("1"), headers.allValues("x-caller"));
    assertEquals(List.of(), headers.allValues("x-late"));
  }
}

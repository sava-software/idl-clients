package software.sava.idl.clients.jupiter.swap.rest;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import software.sava.core.accounts.PublicKey;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class JupiterPriceClientTest {

  private static final String SOL_MINT = "So11111111111111111111111111111111111111112";
  private static final String USDC_MINT = "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v";

  private static final String RESPONSE_BODY = """
      {
        "So11111111111111111111111111111111111111112": {
          "createdAt": "2024-06-05T08:55:25.527Z",
          "liquidity": 621679197.67,
          "usdPrice": 147.48,
          "blockId": 348004023,
          "decimals": 9,
          "priceChange24h": 1.29
        },
        "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v": {
          "createdAt": "2024-06-05T08:55:25.527Z",
          "liquidity": 522175174.66,
          "usdPrice": 0.9999,
          "blockId": 348004025,
          "decimals": 6,
          "priceChange24h": -0.003
        }
      }""";

  private final HttpServer httpServer;
  private final URI endpoint;
  private final JupiterPriceClient priceClient;
  private volatile String capturedQuery;
  private volatile String capturedPath;
  private volatile String capturedMethod;
  /// Every value of every header on the most recent request: a header sent
  /// twice shows up twice in `allValues`, where `getFirst` would hide it.
  private volatile HttpHeaders capturedHeaders;

  JupiterPriceClientTest() {
    try {
      this.httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
    httpServer.createContext("/price/v3", exchange -> {
          capturedMethod = exchange.getRequestMethod();
          capturedPath = exchange.getRequestURI().getPath();
          capturedQuery = exchange.getRequestURI().getQuery();
          capturedHeaders = HttpHeaders.of(exchange.getRequestHeaders(), (name, value) -> true);
          final var bytes = RESPONSE_BODY.getBytes(UTF_8);
          exchange.getResponseHeaders().add("Content-Type", "application/json");
          exchange.sendResponseHeaders(200, bytes.length);
          try (final var os = exchange.getResponseBody()) {
            os.write(bytes);
          }
        }
    );
    httpServer.start();

    final var address = httpServer.getAddress();
    this.endpoint = URI.create("http://" + address.getHostString() + ":" + address.getPort());
    final var builder = JupiterPriceClient.build();
    builder.apiKey("test-key");
    builder.endpoint(endpoint);
    this.priceClient = builder.createClient();
  }

  @AfterAll
  void shutdown() {
    httpServer.stop(0);
  }

  /// `priceForKeys` renders each `PublicKey` as base58 before joining — the
  /// mapping step is the mutant target: dropped, the joiner is handed raw
  /// `PublicKey` objects instead of strings.
  @Test
  void priceForKeysJoinsBase58Keys() {
    final var prices = priceClient.priceForKeys(List.of(
        PublicKey.fromBase58Encoded(SOL_MINT),
        PublicKey.fromBase58Encoded(USDC_MINT))).join();

    assertEquals("ids=" + SOL_MINT + "," + USDC_MINT, capturedQuery);
    assertEquals(2, prices.size());
  }

  @Test
  void testPriceRequestAndResponse() {
    final var prices = priceClient.price(List.of(SOL_MINT, USDC_MINT)).join();

    assertEquals("GET", capturedMethod);
    assertEquals("/price/v3", capturedPath);
    assertEquals("ids=" + SOL_MINT + "," + USDC_MINT, capturedQuery);

    assertNotNull(prices);
    assertEquals(2, prices.size());

    final var sol = prices.get(PublicKey.fromBase58Encoded(SOL_MINT));
    assertNotNull(sol);
    assertEquals(PublicKey.fromBase58Encoded(SOL_MINT), sol.mint());
    assertEquals(Instant.parse("2024-06-05T08:55:25.527Z"), sol.createdAt());
    assertEquals(621679197.67, sol.liquidity());
    assertEquals(147.48, sol.usdPrice());
    assertEquals(BigInteger.valueOf(348004023L), sol.blockId());
    assertEquals(9, sol.decimals());
    assertEquals(1.29, sol.priceChange24h());

    final var usdc = prices.get(PublicKey.fromBase58Encoded(USDC_MINT));
    assertNotNull(usdc);
    assertEquals(PublicKey.fromBase58Encoded(USDC_MINT), usdc.mint());
    assertEquals(Instant.parse("2024-06-05T08:55:25.527Z"), usdc.createdAt());
    assertEquals(522175174.66, usdc.liquidity());
    assertEquals(0.9999, usdc.usdPrice());
    assertEquals(BigInteger.valueOf(348004025L), usdc.blockId());
    assertEquals(6, usdc.decimals());
    assertEquals(-0.003, usdc.priceChange24h());
  }

  /// A configured key reaches the wire as `x-api-key`. The client is built here
  /// rather than taken from the field: the header is wired through
  /// `extendRequest`, and coverage attributed to a constructor is unstable
  /// under PIT, so the field's client could never pair it with this assertion.
  @Test
  void theConfiguredApiKeyIsSent() {
    final var builder = JupiterPriceClient.build();
    builder.apiKey("test-key");
    builder.endpoint(endpoint);
    final var client = builder.createClient();

    capturedHeaders = null;
    final var prices = client.price(SOL_MINT).join();

    assertEquals(2, prices.size());
    assertEquals("ids=" + SOL_MINT, capturedQuery);
    final var headers = capturedHeaders;
    assertNotNull(headers, "the server must have recorded the request");
    assertEquals(List.of("test-key"), headers.allValues("x-api-key"));
  }

  /// A key is optional. Without one — or with a blank one — the client builds
  /// and its requests carry no `x-api-key` header, which Jupiter serves as
  /// keyless traffic; a configured key is sent as given; and a caller's
  /// `extendRequest` applies either way. Each scenario builds a fresh client
  /// inside the test and reads the headers of its one request.
  @Test
  void keylessAcceptanceOnThePriceClient() {
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
      final var builder = JupiterPriceClient.build();
      builder.endpoint(endpoint);
      scenario.configure().accept(builder);
      final var client = assertDoesNotThrow(builder::createClient, scenario.name());

      capturedHeaders = null;
      final var prices = client.price(SOL_MINT).join();

      assertEquals(2, prices.size(), scenario.name());
      assertEquals("ids=" + SOL_MINT, capturedQuery, scenario.name());
      final var headers = capturedHeaders;
      assertNotNull(headers, scenario.name());
      assertEquals(scenario.apiKey(), headers.allValues("x-api-key"), scenario.name());
      assertEquals(scenario.caller(), headers.allValues("x-caller"), scenario.name());
    }
  }
}

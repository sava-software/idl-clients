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
import java.time.Instant;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
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
  private final JupiterPriceClient priceClient;
  private volatile String capturedQuery;
  private volatile String capturedPath;
  private volatile String capturedMethod;

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
    final var endpoint = URI.create("http://" + address.getHostString() + ":" + address.getPort());
    final var builder = JupiterPriceClient.build();
    builder.apiKey("test-key");
    builder.endpoint(endpoint);
    this.priceClient = builder.createClient();
  }

  @AfterAll
  void shutdown() {
    httpServer.stop(0);
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
}

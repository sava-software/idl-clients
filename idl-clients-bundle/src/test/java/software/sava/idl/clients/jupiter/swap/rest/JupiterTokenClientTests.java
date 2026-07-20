package software.sava.idl.clients.jupiter.swap.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the Jupiter token API client against an in-JVM HTTP server.
///
/// Every method here is a path builder: the response parsing is shared, so what
/// distinguishes `search` from `forTag` from `forCategory` is only the URL each
/// one resolves. A wrong segment or a query parameter attached to the wrong path
/// still returns a well-formed token map — from the wrong endpoint — so the
/// tests assert the exact path and query rather than just the parsed result.
/// `@Execution` and `@TestInstance` are not `@Inherited`, so the abstract
/// base's copies do not reach this class — it shares one mock server and one
/// expectation queue, which interleaving would corrupt.
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class JupiterTokenClientTests extends JupiterRestTests {

  private final JupiterTokenClient client = buildClient();

  private JupiterTokenClient buildClient() {
    final var builder = JupiterTokenClient.build();
    builder.httpClient(HTTP_CLIENT);
    builder.endpoint(endpoint);
    builder.apiKey("test-api-key");
    return builder.createClient();
  }

  private static final String WSOL = "So11111111111111111111111111111111111111112";
  private static final String USDC = "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v";

  private static final String TOKENS = """
      [
        {"id":"%s","name":"Wrapped SOL","symbol":"SOL","decimals":9,
         "usdPrice":147.48,"holderCount":100,"liquidity":621679197.67},
        {"id":"%s","name":"USD Coin","symbol":"USDC","decimals":6,
         "usdPrice":0.9999,"holderCount":200,"liquidity":522175174.66}
      ]""".formatted(WSOL, USDC);

  /// `search` takes either a single query string or a collection, which is
  /// joined with commas onto the same `search` path.
  @Test
  void searchJoinsACollectionOntoTheSearchPath() {
    expectGet("/tokens/v2/search?query=SOL", TOKENS);
    final var single = client.search("SOL").join();
    assertEquals(2, single.size());

    // the collection overload comma-joins rather than issuing one request each
    expectGet("/tokens/v2/search?query=SOL,USDC", TOKENS);
    final var multiple = client.search(List.of("SOL", "USDC")).join();
    assertEquals(2, multiple.size());

    // an empty collection still resolves the search path, with an empty query
    expectGet("/tokens/v2/search?query=", "[]");
    assertTrue(client.search(List.<String>of()).join().isEmpty());
  }

  /// `forTag` shares `search`'s query-parameter shape but a different final
  /// segment — swapping the two would silently return tag results for a search.
  @Test
  void forTagUsesTheTagSegmentNotSearch() {
    expectGet("/tokens/v2/tag?query=verified", TOKENS);
    final var tagged = client.forTag("verified").join();

    assertEquals(2, tagged.size());
    // the parsed map is keyed by mint
    assertTrue(tagged.keySet().stream().anyMatch(k -> k.toBase58().equals(WSOL)));
    assertTrue(tagged.keySet().stream().anyMatch(k -> k.toBase58().equals(USDC)));

    final var sol = tagged.entrySet().stream()
        .filter(e -> e.getKey().toBase58().equals(WSOL))
        .findFirst().orElseThrow().getValue();
    assertEquals("Wrapped SOL", sol.name());
    assertEquals("SOL", sol.symbol());
    assertEquals(9, sol.decimals());
  }

  /// `forCategory` is the odd one out: category and interval are *path*
  /// segments and the limit is a query parameter, so its three arguments each
  /// land somewhere different. Transposing category and interval yields a valid
  /// URL for a different listing.
  @Test
  void forCategoryPutsCategoryAndIntervalInThePathAndLimitInTheQuery() {
    expectGet("/tokens/v2/toporganicscore/24h?limit=50", TOKENS);
    assertEquals(2, client.forCategory("toporganicscore", "24h", 50).join().size());

    // each argument moves independently
    expectGet("/tokens/v2/toptraded/1h?limit=10", TOKENS);
    assertEquals(2, client.forCategory("toptraded", "1h", 10).join().size());

    // the limit really is rendered as a decimal, not padded or reordered
    expectGet("/tokens/v2/toporganicscore/24h?limit=1", TOKENS);
    assertNotNull(client.forCategory("toporganicscore", "24h", 1).join());
  }

  /// `recentTokens` hits its own fixed endpoint rather than resolving against
  /// the shared token path, so it takes no query at all.
  @Test
  void recentTokensUsesItsOwnEndpoint() {
    expectGet("/tokens/v2/recent", TOKENS);
    assertEquals(2, client.recentTokens().join().size());

    // an empty listing parses to an empty map rather than null
    expectGet("/tokens/v2/recent", "[]");
    assertTrue(client.recentTokens().join().isEmpty());
  }
}

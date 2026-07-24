package software.sava.idl.clients.jupiter.swap.rest;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the shared HTTP client builder base directly, through a minimal test
/// subclass — the concrete client builders each layer their own null handling
/// on top, which otherwise masks the base's `setDefaults` behavior entirely.
final class ClientBuildersTests {

  private static final class TestBuilder extends HttpClientBuilder<Object> {

    @Override
    public Object createClient() {
      setDefaults();
      return new Object();
    }

    void applyDefaults() {
      setDefaults();
    }

    UnaryOperator<HttpRequest.Builder> extendRequestField() {
      return extendRequest;
    }

    HttpClient httpClientField() {
      return httpClient;
    }

    Duration requestTimeoutField() {
      return requestTimeout;
    }
  }

  private static final class TestJupiterBuilder extends JupiterClientBuilder<Object> {

    @Override
    public Object createClient() {
      return new Object();
    }

    UnaryOperator<HttpRequest.Builder> resolveExtendRequest() {
      return extendRequest();
    }
  }

  private static java.util.List<String> headerValues(final HttpRequest.Builder request, final String name) {
    return request.uri(URI.create("http://localhost/")).build().headers().allValues(name);
  }

  @Test
  void settersReturnTheBuilderForChaining() {
    final var builder = new TestBuilder();
    final var httpClient = HttpClient.newHttpClient();
    assertSame(builder, builder.endpoint(URI.create("http://localhost:1/")));
    assertSame(builder, builder.endpoint("http://localhost:2/"));
    assertSame(builder, builder.httpClient(httpClient));
    assertSame(builder, builder.requestTimeout(Duration.ofSeconds(5)));
    assertSame(builder, builder.extendRequest(UnaryOperator.identity()));
    assertSame(builder, builder.testResponse((response, body) -> true));
    assertThrows(IllegalArgumentException.class, () -> builder.endpoint("::not a uri::"));

    final var jupiterBuilder = new TestJupiterBuilder();
    assertSame(jupiterBuilder, jupiterBuilder.apiKey("key"));
  }

  /// `setDefaults` fills only what is unset: a no-op request extension, a fresh
  /// HTTP client, and a 30 second timeout — and must not overwrite what the
  /// caller configured.
  @Test
  void setDefaultsFillsOnlyUnsetFields() {
    final var defaults = new TestBuilder();
    defaults.applyDefaults();
    assertNotNull(defaults.httpClientField());
    assertEquals(Duration.ofSeconds(30), defaults.requestTimeoutField());
    final var request = HttpRequest.newBuilder();
    assertSame(request, defaults.extendRequestField().apply(request), "the default extension is the identity");

    final var configured = new TestBuilder();
    final var httpClient = HttpClient.newHttpClient();
    final UnaryOperator<HttpRequest.Builder> marker = r -> r.header("x-marker", "1");
    configured.httpClient(httpClient);
    configured.requestTimeout(Duration.ofSeconds(5));
    configured.extendRequest(marker);
    configured.applyDefaults();
    assertSame(httpClient, configured.httpClientField(), "a configured client survives setDefaults");
    assertEquals(Duration.ofSeconds(5), configured.requestTimeoutField());
    assertSame(marker, configured.extendRequestField());
  }

  /// Every Jupiter client resolves its endpoint URLs at *build* time, so a
  /// dropped default-endpoint guard NPEs during construction — no request has
  /// to be issued to observe it. The swap builder's resolved URLs are
  /// package-visible, so the hosted and local defaults are pinned exactly.
  @Test
  void defaultEndpointsResolveAtBuildTime() {
    final var remote = JupiterSwapApiClient.build();
    remote.apiKey("k");
    assertNotNull(remote.createClient());
    assertEquals(URI.create("https://api.jup.ag/swap/v1/swap"), remote.swapURI);
    assertEquals(URI.create("https://api.jup.ag/swap/v1/swap-instructions"), remote.swapInstructionsURI);
    assertEquals("/swap/v1/quote?", remote.quotePath);

    final var local = JupiterSwapApiClient.build();
    local.apiKey("k");
    assertNotNull(local.createLocalClient());
    assertEquals(URI.create("https://localhost:8899/swap"), local.swapURI);
    assertEquals("/quote?", local.quotePath);

    final var price = JupiterPriceClient.build();
    price.apiKey("k");
    assertNotNull(price.createClient(), "the price client defaults its endpoint");
    final var token = JupiterTokenClient.build();
    token.apiKey("k");
    assertNotNull(token.createClient());
    assertEquals(URI.create("https://api.jup.ag/tokens/v2/recent"), token.v2RecentTokenPath);
  }

  /// The Jupiter builder's request extension always attaches the `x-api-key`
  /// header, composed with the caller's own extension when one is set.
  @Test
  void jupiterExtendRequestAttachesTheApiKey() {
    final var bare = new TestJupiterBuilder();
    bare.apiKey("secret");
    final var bareRequest = bare.resolveExtendRequest().apply(HttpRequest.newBuilder());
    assertEquals(java.util.List.of("secret"), headerValues(bareRequest, "x-api-key"));

    final var composed = new TestJupiterBuilder();
    composed.apiKey("secret");
    composed.extendRequest(r -> r.header("x-marker", "1"));
    final var composedRequest = composed.resolveExtendRequest().apply(HttpRequest.newBuilder());
    assertEquals(java.util.List.of("secret"), headerValues(composedRequest, "x-api-key"));
    assertEquals(java.util.List.of("1"), headerValues(composedRequest, "x-marker"),
        "the caller's extension composes with the api key header");
  }
}

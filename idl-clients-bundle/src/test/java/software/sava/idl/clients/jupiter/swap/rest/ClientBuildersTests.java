package software.sava.idl.clients.jupiter.swap.rest;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.List;
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

    String apiKeyField() {
      return apiKey;
    }

    /// Writes the protected field directly, as a subclass may, bypassing the setter.
    void assignApiKeyField(final String apiKey) {
      this.apiKey = apiKey;
    }
  }

  /// The fixed tail of the message a refused API key throws with.
  private static final String CANNOT_CARRY = ", a character an HTTP header value cannot carry";

  private static List<String> headerValues(final HttpRequest.Builder request, final String name) {
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
    assertSame(jupiterBuilder, jupiterBuilder.apiKey(" key\n"));
    assertSame(jupiterBuilder, jupiterBuilder.apiKey("   "));
    assertSame(jupiterBuilder, jupiterBuilder.apiKey(null));
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
  /// No key is set, so this also pins that the remote, local, price and token
  /// clients all build keyless.
  @Test
  void defaultEndpointsResolveAtBuildTime() {
    final var remote = JupiterSwapApiClient.build();
    assertNotNull(remote.createClient());
    assertEquals(URI.create("https://api.jup.ag/swap/v1/swap"), remote.swapURI);
    assertEquals(URI.create("https://api.jup.ag/swap/v1/swap-instructions"), remote.swapInstructionsURI);
    assertEquals("/swap/v1/quote?", remote.quotePath);

    final var local = JupiterSwapApiClient.build();
    assertNotNull(local.createLocalClient());
    assertEquals(URI.create("https://localhost:8899/swap"), local.swapURI);
    assertEquals("/quote?", local.quotePath);

    final var price = JupiterPriceClient.build();
    assertNotNull(price.createClient(), "the price client defaults its endpoint");
    final var token = JupiterTokenClient.build();
    assertNotNull(token.createClient());
    assertEquals(URI.create("https://api.jup.ag/tokens/v2/recent"), token.v2RecentTokenPath);
  }

  /// The Jupiter builder's request extension attaches the `x-api-key` header
  /// only for a non-blank key, composed with the caller's own extension when
  /// one is set. Without a key it is the caller's extension itself, or the
  /// identity when there is none: a null value would throw inside `header`, and
  /// a blank one would go out as an empty `x-api-key` rather than as a keyless
  /// request.
  ///
  /// Only this test builder reaches a null extension — every real builder's
  /// `setDefaults` replaces it with the identity first — so the two
  /// `assertSame` cases are what pin the null-extension normalisation.
  @Test
  void jupiterExtendRequestAttachesOnlyANonBlankApiKey() {
    final var bare = new TestJupiterBuilder();
    bare.apiKey("secret");
    final var bareRequest = bare.resolveExtendRequest().apply(HttpRequest.newBuilder());
    assertEquals(List.of("secret"), headerValues(bareRequest, "x-api-key"));

    final UnaryOperator<HttpRequest.Builder> marker = r -> r.header("x-marker", "1");
    final var composed = new TestJupiterBuilder();
    composed.apiKey("secret");
    composed.extendRequest(marker);
    final var composedRequest = composed.resolveExtendRequest().apply(HttpRequest.newBuilder());
    assertEquals(List.of("secret"), headerValues(composedRequest, "x-api-key"));
    assertEquals(List.of("1"), headerValues(composedRequest, "x-marker"),
        "the caller's extension composes with the api key header");

    // the key header goes on before the caller's extension runs, so the
    // extension can replace it; every other case that pairs a key with an
    // extension adds a header of another name, where the order cannot show
    final var overridden = new TestJupiterBuilder();
    overridden.apiKey("secret");
    overridden.extendRequest(r -> r.setHeader("x-api-key", "override"));
    final var overriddenRequest = overridden.resolveExtendRequest().apply(HttpRequest.newBuilder());
    assertEquals(List.of("override"), headerValues(overriddenRequest, "x-api-key"),
        "the caller's extension runs after the api key header, so it can replace it");

    // a null key and no extension: the request passes through untouched
    final var keyless = new TestJupiterBuilder();
    keyless.apiKey(null);
    final var request = HttpRequest.newBuilder();
    assertSame(request, keyless.resolveExtendRequest().apply(request));
    assertEquals(List.of(), headerValues(request, "x-api-key"));

    // a null key and an extension: the caller's extension is the whole of it
    final var keylessComposed = new TestJupiterBuilder();
    keylessComposed.apiKey(null);
    keylessComposed.extendRequest(marker);
    assertSame(marker, keylessComposed.resolveExtendRequest());

    // a line break is whitespace too, so a key file holding only a newline
    // is keyless; the setter stores null for every blank key
    for (final var blank : List.of("", "   ", "\t", "\n", " \t\r\n ")) {
      final var blankKey = new TestJupiterBuilder();
      blankKey.apiKey(blank);
      assertNull(blankKey.apiKeyField(), "a blank key is stored as none: [" + blank + ']');
      final var blankRequest = blankKey.resolveExtendRequest().apply(HttpRequest.newBuilder());
      assertEquals(List.of(), headerValues(blankRequest, "x-api-key"),
          "a blank key sends no header: [" + blank + ']');
    }
  }

  /// The setter strips the key (`String#strip()`) before storing it, so a key
  /// read from a file that ends in a newline is sent as the key alone. The
  /// stored field is asserted as well as the header: the JDK trims spaces and
  /// tabs from a header value itself when the request is built, so through the
  /// header only a line break shows the strip, and the field shows the rest.
  /// Whitespace above U+0020, such as U+3000 and U+2028, is stripped too;
  /// `String#trim()` would keep it, so that case pins `strip()`.
  @Test
  void jupiterApiKeyIsSentWithoutLeadingOrTrailingWhitespace() {
    final var padded = List.of(
        " S3CR3T-KEY ",
        "\tS3CR3T-KEY\t",
        "S3CR3T-KEY\n",
        " \t S3CR3T-KEY \t\n",
        "\r\nS3CR3T-KEY\r\n",
        // whitespace above U+0020, which strip() removes and trim() would not
        Character.toString(0x3000) + "S3CR3T-KEY" + Character.toString(0x2028)
    );
    for (final var key : padded) {
      final var builder = new TestJupiterBuilder();
      assertSame(builder, builder.apiKey(key));
      assertEquals("S3CR3T-KEY", builder.apiKeyField(), "stored stripped: [" + key + ']');
      final var request = builder.resolveExtendRequest().apply(HttpRequest.newBuilder());
      assertEquals(List.of("S3CR3T-KEY"), headerValues(request, "x-api-key"), "sent stripped: [" + key + ']');
    }
  }

  /// A character an HTTP header value cannot carry is refused by the setter,
  /// rather than by the JDK on every request with the whole key quoted in its
  /// message. The message names the code point and its index in the key as
  /// given, never the key, and a refused key leaves the previous one in place.
  ///
  /// The JDK 25 rule is that a header value carries tab, space, U+0021 to
  /// U+007E and U+0080 to U+00FF. Each refused boundary sits inside the key:
  /// a line break or U+001F at either end is whitespace the strip removes
  /// first. U+0001 and the byte-order mark are not whitespace, so a leading
  /// one is refused; `String#trim()` would remove the U+0001.
  @Test
  void jupiterApiKeyRefusesACharacterAHeaderCannotCarryWithoutEchoingTheKey() {
    record Refusal(String key, String message) {
    }
    final var refusals = List.of(
        new Refusal("S3CR3T\n-KEY", "apiKey holds U+000A at index 6" + CANNOT_CARRY),
        new Refusal("S3CR3T" + Character.toString(0x00) + "-KEY", "apiKey holds U+0000 at index 6" + CANNOT_CARRY),
        new Refusal("S3CR3T" + Character.toString(0x08) + "-KEY", "apiKey holds U+0008 at index 6" + CANNOT_CARRY),
        new Refusal("S3CR3T" + Character.toString(0x1F) + "-KEY", "apiKey holds U+001F at index 6" + CANNOT_CARRY),
        new Refusal("S3CR3T" + Character.toString(0x7F) + "-KEY", "apiKey holds U+007F at index 6" + CANNOT_CARRY),
        new Refusal("S3CR3T" + Character.toString(0x100) + "-KEY", "apiKey holds U+0100 at index 6" + CANNOT_CARRY),
        new Refusal(Character.toString(0xFEFF) + "S3CR3T-KEY", "apiKey holds U+FEFF at index 0" + CANNOT_CARRY),
        // a control character that is not whitespace, which strip() keeps and trim() would remove
        new Refusal(Character.toString(0x01) + "S3CR3T-KEY", "apiKey holds U+0001 at index 0" + CANNOT_CARRY),
        // the code point, not the first half of its surrogate pair
        new Refusal("S3CR3T" + Character.toString(0x1F600) + "-KEY", "apiKey holds U+1F600 at index 6" + CANNOT_CARRY),
        // the index counts the leading whitespace the strip removes
        new Refusal(" \tS3CR3T\r-KEY\n", "apiKey holds U+000D at index 8" + CANNOT_CARRY)
    );
    for (final var refusal : refusals) {
      final var builder = new TestJupiterBuilder();
      builder.apiKey("previous-key");
      final var e = assertThrows(IllegalArgumentException.class, () -> builder.apiKey(refusal.key()));
      assertEquals(refusal.message(), e.getMessage());
      assertFalse(e.getMessage().contains("S3CR3T"), "the message never contains the key: " + refusal.message());
      assertEquals("previous-key", builder.apiKeyField(), "a refused key leaves the previous one in place");
    }
  }

  /// The accepted side of each boundary of the JDK 25 rule reaches the header
  /// unchanged: tab, the one character below U+0020 a header carries; space;
  /// U+0021, where the JDK's visible range starts; U+007E and U+0080, either
  /// side of DEL; and U+00FF, the last character a header carries.
  @Test
  void jupiterApiKeyKeepsEveryCharacterAHeaderCanCarry() {
    final var keys = List.of(
        "S3CR3T\t-KEY",
        "S3CR3T -KEY",
        "S3CR3T!-KEY",
        "S3CR3T~-KEY",
        "S3CR3T" + Character.toString(0x80) + "-KEY",
        "S3CR3T" + Character.toString(0xFF) + "-KEY"
    );
    for (final var key : keys) {
      final var builder = new TestJupiterBuilder();
      assertSame(builder, builder.apiKey(key));
      assertEquals(key, builder.apiKeyField());
      final var request = builder.resolveExtendRequest().apply(HttpRequest.newBuilder());
      assertEquals(List.of(key), headerValues(request, "x-api-key"));
    }
  }

  /// A subclass can assign the protected `apiKey` field without the setter, so
  /// `extendRequest()` normalizes what it captures the same way: the field is
  /// stripped, a blank one is keyless, and a character a header cannot carry
  /// is refused when the client is built, not on every request it sends.
  @Test
  void jupiterExtendRequestNormalizesAKeyAssignedToTheField() {
    final var padded = new TestJupiterBuilder();
    padded.assignApiKeyField(" \tS3CR3T-KEY\r\n");
    final var paddedRequest = padded.resolveExtendRequest().apply(HttpRequest.newBuilder());
    assertEquals(List.of("S3CR3T-KEY"), headerValues(paddedRequest, "x-api-key"));

    final var blank = new TestJupiterBuilder();
    blank.assignApiKeyField(" \t\r\n");
    final var request = HttpRequest.newBuilder();
    assertSame(request, blank.resolveExtendRequest().apply(request), "a blank field is keyless");
    assertEquals(List.of(), headerValues(request, "x-api-key"));

    final var refused = new TestJupiterBuilder();
    refused.assignApiKeyField("S3CR3T\n-KEY");
    final var e = assertThrows(IllegalArgumentException.class, refused::resolveExtendRequest);
    assertEquals("apiKey holds U+000A at index 6" + CANNOT_CARRY, e.getMessage());
    assertFalse(e.getMessage().contains("S3CR3T"), "the message never contains the key");
  }

  /// The extension is resolved from the builder's state when `createClient()`
  /// asks for it, and keeps that state: changing the key or the extension
  /// afterwards — to build a second client with another key, say — does not
  /// reach an operator, or a client, already built.
  @Test
  void jupiterExtendRequestCapturesTheBuilderStateWhenResolved() {
    final UnaryOperator<HttpRequest.Builder> marker = r -> r.header("x-marker", "1");
    final UnaryOperator<HttpRequest.Builder> late = r -> r.header("x-late", "1");
    final var keyed = new TestJupiterBuilder();
    keyed.apiKey("first");
    keyed.extendRequest(marker);
    final var resolved = keyed.resolveExtendRequest();
    keyed.apiKey("second");
    keyed.extendRequest(late);
    final var request = resolved.apply(HttpRequest.newBuilder());
    assertEquals(List.of("first"), headerValues(request, "x-api-key"));
    assertEquals(List.of("1"), headerValues(request, "x-marker"));
    assertEquals(List.of(), headerValues(request, "x-late"));

    final var keyless = new TestJupiterBuilder();
    final var resolvedKeyless = keyless.resolveExtendRequest();
    keyless.apiKey("k");
    final var keylessRequest = resolvedKeyless.apply(HttpRequest.newBuilder());
    assertEquals(List.of(), headerValues(keylessRequest, "x-api-key"));
  }
}

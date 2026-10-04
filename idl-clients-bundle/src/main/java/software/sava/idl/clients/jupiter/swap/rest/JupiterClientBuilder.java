package software.sava.idl.clients.jupiter.swap.rest;

import java.net.http.HttpRequest;
import java.util.function.UnaryOperator;

/// The base of every Jupiter REST client builder, adding an optional API key to the shared HTTP settings. A client
/// built without a key sends no `x-api-key` header, and Jupiter serves its requests as keyless.
public abstract class JupiterClientBuilder<C> extends HttpClientBuilder<C> {

  protected String apiKey;

  /// Optional. Leading and trailing whitespace is removed ([String#strip()]), so a key read from a file that ends in a
  /// newline works, and what remains is sent as `x-api-key`. A null, empty or blank key counts as none, and without
  /// one requests are keyless: Jupiter serves them at its keyless limit of 0.5 requests per second instead of
  /// rejecting them. An invalid or unknown key is rejected with 401, and a key whose permissions exclude the endpoint
  /// with 403.
  ///
  /// @throws IllegalArgumentException if the stripped key holds a character an HTTP header value cannot carry: one
  /// below U+0020 other than tab, DEL (U+007F), or one above U+00FF. The message names the character's code point and
  /// its index in `apiKey`, and never contains the key. The builder keeps the key it held before.
  public JupiterClientBuilder<C> apiKey(final String apiKey) {
    this.apiKey = normalizeApiKey(apiKey);
    return this;
  }

  /// The request extension a client is built with, captured now: an `x-api-key` header carrying the key, normalized
  /// as [#apiKey(String)] normalizes it, then the caller's extension. Without a key no header is sent, which Jupiter
  /// serves as keyless. Changing the builder afterwards does not change a client it already built.
  ///
  /// @throws IllegalArgumentException as [#apiKey(String)] does, for a key a subclass assigned to the field directly
  protected UnaryOperator<HttpRequest.Builder> extendRequest() {
    final var apiKey = normalizeApiKey(this.apiKey);
    final UnaryOperator<HttpRequest.Builder> extendRequest = this.extendRequest == null
        ? UnaryOperator.identity()
        : this.extendRequest;
    if (apiKey == null) {
      return extendRequest;
    } else {
      return request -> extendRequest.apply(request.header("x-api-key", apiKey));
    }
  }

  /// `null` for a null, empty or blank key, otherwise the key without its leading and trailing whitespace. The check
  /// is the complement of the JDK's own (`jdk.internal.net.http.common.Utils#isValidValue`), under which a header
  /// value carries tab, space, U+0021 to U+007E and U+0080 to U+00FF. Each of those is a single `char`, so the loop
  /// stops at the first `char` of a supplementary code point and reports the whole code point.
  private static String normalizeApiKey(final String apiKey) {
    if (apiKey == null) {
      return null;
    }
    final var key = apiKey.strip();
    if (key.isEmpty()) {
      return null;
    }
    for (int i = 0; i < key.length(); i++) {
      final int c = key.codePointAt(i);
      if (c > 0xFF || c == 0x7F || (c < 0x20 && c != '\t')) {
        final int index = apiKey.length() - apiKey.stripLeading().length() + i;
        throw new IllegalArgumentException(String.format(
            "apiKey holds U+%04X at index %d, a character an HTTP header value cannot carry", c, index
        ));
      }
    }
    return key;
  }
}

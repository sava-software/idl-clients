package software.sava.idl.clients.jupiter.swap.rest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;

/// Serves the Jupiter REST API from an in-JVM [HttpServer] on an ephemeral port,
/// so the client's request construction and response handling are exercised over
/// a real socket without reaching the network.
///
/// Adapted from sava-rpc's `RpcRequestTests`. The difference is that Jupiter is
/// REST rather than JSON-RPC: requests are matched on method and path (and body,
/// for `POST`) instead of on a JSON-RPC envelope, and the status code is part of
/// what a test wants to control, since the client's own error handling keys off
/// it.
///
/// Each expected exchange is queued; [#verifyEmptyRequestQueue] fails the test if
/// any goes unconsumed, so a client that silently stops issuing a request cannot
/// pass.
///
/// Subclasses build their own client against [#endpoint]. Note that the Jupiter
/// builders' setters are declared on the shared `HttpClientBuilder` base and
/// return *that* type, so they have to be called statement-by-statement —
/// chaining loses the concrete `Builder` that declares `createClient()` /
/// `createLocalClient()`. Every Jupiter builder also sets an `x-api-key` header
/// unconditionally, so an api key must be supplied.
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class JupiterRestTests implements HttpHandler {

  static {
    System.setProperty("com.sun.net.httpserver.HttpServerProvider", "sun.net.httpserver.DefaultHttpServerProvider");
  }

  private static final ExecutorService HTTP_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

  /// Shared by every subclass; the servers are per-instance, the client is not.
  protected static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().executor(HTTP_EXECUTOR).build();

  private final Queue<Exchange> expected;
  private final HttpServer httpServer;

  /// The mock server's base URI. Subclasses build whichever Jupiter client they
  /// cover against this.
  protected final URI endpoint;

  protected JupiterRestTests() {
    try {
      this.httpServer = HttpServer.create(new InetSocketAddress(0), 0);
      httpServer.setExecutor(HTTP_EXECUTOR);
      httpServer.start();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
    this.expected = new ArrayDeque<>();

    final var address = httpServer.getAddress();
    this.endpoint = URI.create(
        String.format("http://[%s]:%d", address.getHostString(), address.getPort()));

    httpServer.createContext("/", this);
  }

  private record Exchange(String method, String pathAndQuery, String requestBody,
                          int responseCode, String responseBody) {
  }

  /// Queue a `GET` that the client is expected to issue, with the response it
  /// should receive.
  protected final void expectGet(final String pathAndQuery, final String responseBody) {
    expected.add(new Exchange("GET", pathAndQuery, null, 200, responseBody));
  }

  protected final void expectGet(final String pathAndQuery, final int responseCode, final String responseBody) {
    expected.add(new Exchange("GET", pathAndQuery, null, responseCode, responseBody));
  }

  /// Queue a `POST`. A null `requestBody` skips the body assertion.
  protected final void expectPost(final String pathAndQuery, final String requestBody, final String responseBody) {
    expected.add(new Exchange("POST", pathAndQuery, requestBody, 200, responseBody));
  }

  protected final void expectPost(final String pathAndQuery,
                                  final String requestBody,
                                  final int responseCode,
                                  final String responseBody) {
    expected.add(new Exchange("POST", pathAndQuery, requestBody, responseCode, responseBody));
  }

  private static void write(final HttpExchange exchange, final int code, final String body) {
    final byte[] bytes = body.getBytes(UTF_8);
    try {
      exchange.sendResponseHeaders(code, bytes.length);
      try (final var os = exchange.getResponseBody()) {
        os.write(bytes);
      }
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public final void handle(final HttpExchange exchange) throws IOException {
    final var uri = exchange.getRequestURI();
    final var actualPath = uri.getRawQuery() == null
        ? uri.getRawPath()
        : uri.getRawPath() + '?' + uri.getRawQuery();
    final var actualBody = new String(exchange.getRequestBody().readAllBytes(), UTF_8);
    final var actualMethod = exchange.getRequestMethod();

    final var next = expected.poll();
    if (next == null) {
      write(exchange, 400, "Unexpected request: " + actualMethod + ' ' + actualPath);
      return;
    }
    if (!next.method().equals(actualMethod) || !next.pathAndQuery().equals(actualPath)) {
      write(exchange, 400, String.format(
          "Request mismatch.%n expected: %s %s%n actual:   %s %s",
          next.method(), next.pathAndQuery(), actualMethod, actualPath));
      return;
    }
    if (next.requestBody() != null && !next.requestBody().equals(actualBody)) {
      write(exchange, 400, String.format(
          "Body mismatch.%n expected: %s%n actual:   %s", next.requestBody(), actualBody));
      return;
    }
    write(exchange, next.responseCode(), next.responseBody());
  }

  @AfterEach
  final void verifyEmptyRequestQueue() {
    final int remaining = expected.size();
    if (remaining > 0) {
      final var pending = expected.stream()
          .map(e -> e.method() + ' ' + e.pathAndQuery())
          .collect(java.util.stream.Collectors.joining(
              "\n * ", String.format("Expected zero remaining requests. %d remaining:%n * ", remaining), ""));
      expected.clear();
      Assertions.fail(pending);
    }
  }

  @AfterAll
  protected void shutdown() {
    httpServer.stop(0);
  }
}

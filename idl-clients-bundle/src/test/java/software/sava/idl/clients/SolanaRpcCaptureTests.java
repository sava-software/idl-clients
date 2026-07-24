package software.sava.idl.clients;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import software.sava.rpc.json.http.client.SolanaRpcClient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// In-JVM JSON-RPC harness for the `SolanaRpcClient`-taking fetchers —
/// adapted from sava-rpc's `RpcRequestTests` the same way `JupiterRestTests`
/// was for REST. The property under test is the *request*: which RPC method,
/// which program, which filters — a fetcher with a wrong filter offset or a
/// transposed program id returns a well-formed empty list rather than an
/// error, so only asserting the emitted request catches it. Tests register
/// canned responses, drive a fetcher, then assert fragments of the captured
/// body (filters via `Filter.toJson()`, keys via base58); `@AfterEach` fails
/// on any unconsumed response, so a fetcher that silently stops issuing its
/// request cannot pass.
///
/// Build the client under test with [#rpcClient()] *inside the test body* —
/// construction coverage attributed to a field initializer is unstable under
/// PIT (see AGENTS.md).
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class SolanaRpcCaptureTests implements HttpHandler {

  static {
    System.setProperty("com.sun.net.httpserver.HttpServerProvider", "sun.net.httpserver.DefaultHttpServerProvider");
  }

  private static final ExecutorService HTTP_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
  protected static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().executor(HTTP_EXECUTOR).build();

  private final HttpServer httpServer;
  private final URI endpoint;
  private final Queue<String> cannedResponses = new ArrayDeque<>();
  private final List<String> capturedRequests = new ArrayList<>();

  protected SolanaRpcCaptureTests() {
    try {
      this.httpServer = HttpServer.create(new InetSocketAddress(0), 0);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
    httpServer.setExecutor(HTTP_EXECUTOR);
    httpServer.start();
    final var address = httpServer.getAddress();
    this.endpoint = URI.create(String.format("http://[%s]:%d", address.getHostString(), address.getPort()));
    httpServer.createContext("/", this);
  }

  protected final SolanaRpcClient rpcClient() {
    return SolanaRpcClient.build()
        .httpClient(HTTP_CLIENT)
        .endpoint(endpoint)
        .createClient();
  }

  @Override
  public final void handle(final HttpExchange exchange) throws IOException {
    final byte[] requestBody = exchange.getRequestBody().readAllBytes();
    final String response;
    synchronized (this) {
      capturedRequests.add(new String(requestBody, UTF_8));
      response = cannedResponses.poll();
    }
    final byte[] responseBytes =
        (response == null ? "{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-32600,\"message\":\"no canned response registered\"},\"id\":1}" : response)
            .getBytes(UTF_8);
    exchange.sendResponseHeaders(response == null ? 400 : 200, responseBytes.length);
    try (final var os = exchange.getResponseBody()) {
      os.write(responseBytes);
    }
  }

  @AfterEach
  final void expectAllResponsesConsumed() {
    synchronized (this) {
      assertTrue(cannedResponses.isEmpty(),
          () -> cannedResponses.size() + " canned response(s) never requested — a fetcher stopped issuing its call");
      capturedRequests.clear();
    }
  }

  @AfterAll
  final void stopServer() {
    httpServer.stop(0);
  }

  protected final synchronized void respondWith(final String jsonRpcResponse) {
    cannedResponses.add(jsonRpcResponse);
  }

  /// The most recent captured request body; call after `join()`ing the fetch.
  protected final synchronized String lastRequest() {
    return capturedRequests.getLast();
  }

  protected final void assertLastRequestContains(final String... fragments) {
    final var request = lastRequest();
    for (final var fragment : fragments) {
      assertTrue(request.contains(fragment),
          () -> "request missing fragment <" + fragment + ">: " + request);
    }
  }

  // ---------------------------------------------------------------------------
  // canned response builders
  // ---------------------------------------------------------------------------

  // sava-rpc requests account scans withContext, so results arrive wrapped
  protected static final String EMPTY_PROGRAM_ACCOUNTS =
      "{\"jsonrpc\":\"2.0\",\"result\":{\"context\":{\"apiVersion\":\"2.1.0\",\"slot\":123},\"value\":[]},\"id\":1}";
  protected static final String EMPTY_TOKEN_ACCOUNTS = EMPTY_PROGRAM_ACCOUNTS;

  protected static String numberResult(final long value) {
    return "{\"jsonrpc\":\"2.0\",\"result\":" + value + ",\"id\":1}";
  }

  protected static String accountValueJson(final byte[] data, final String ownerBase58) {
    return String.format(
        "{\"data\":[\"%s\",\"base64\"],\"executable\":false,\"lamports\":1000000000,\"owner\":\"%s\",\"rentEpoch\":18446744073709551615,\"space\":%d}",
        Base64.getEncoder().encodeToString(data), ownerBase58, data.length);
  }

  protected static String accountInfoResult(final byte[] data, final String ownerBase58) {
    return "{\"jsonrpc\":\"2.0\",\"result\":{\"context\":{\"apiVersion\":\"2.1.0\",\"slot\":123},\"value\":"
        + accountValueJson(data, ownerBase58) + "},\"id\":1}";
  }

  protected static String programAccountsResult(final String pubKeyBase58, final byte[] data, final String ownerBase58) {
    return "{\"jsonrpc\":\"2.0\",\"result\":{\"context\":{\"apiVersion\":\"2.1.0\",\"slot\":123},\"value\":"
        + "[{\"pubkey\":\"" + pubKeyBase58 + "\",\"account\":" + accountValueJson(data, ownerBase58) + "}]},\"id\":1}";
  }
}

package software.sava.idl.clients.spl.stakepool;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import software.sava.core.accounts.PublicKey;
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
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

/// Covers the two stake-pool RPC fetchers against an in-JVM JSON-RPC capture
/// server (the compact sibling of the bundle's `SolanaRpcCaptureTests`).
/// `fetchProgramState` must request the pool account it was given, and
/// `fetchValidatorList` must request the list account *stored in the fetched
/// state* — a stale or transposed key returns a plausible wrong account, so
/// the request is the assertion. The state response is the committed real
/// Jito account, so the parse threads real bytes.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class StakePoolRpcFetcherTests {

  static {
    System.setProperty("com.sun.net.httpserver.HttpServerProvider", "sun.net.httpserver.DefaultHttpServerProvider");
  }

  private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
      .executor(Executors.newVirtualThreadPerTaskExecutor())
      .build();

  private final HttpServer httpServer;
  private final URI endpoint;
  private final Queue<String> cannedResponses = new ArrayDeque<>();
  private final List<String> capturedRequests = new ArrayList<>();

  StakePoolRpcFetcherTests() {
    try {
      this.httpServer = HttpServer.create(new InetSocketAddress(0), 0);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
    httpServer.setExecutor(HTTP_CLIENT.executor().orElseThrow());
    httpServer.start();
    final var address = httpServer.getAddress();
    this.endpoint = URI.create(String.format("http://[%s]:%d", address.getHostString(), address.getPort()));
    httpServer.createContext("/", exchange -> {
      final byte[] requestBody = exchange.getRequestBody().readAllBytes();
      final String response;
      synchronized (this) {
        capturedRequests.add(new String(requestBody, UTF_8));
        response = cannedResponses.poll();
      }
      final byte[] responseBytes = (response == null ? "{}" : response).getBytes(UTF_8);
      exchange.sendResponseHeaders(response == null ? 400 : 200, responseBytes.length);
      try (final var os = exchange.getResponseBody()) {
        os.write(responseBytes);
      }
    });
  }

  @AfterEach
  void expectAllResponsesConsumed() {
    synchronized (this) {
      assertTrue(cannedResponses.isEmpty(), "a canned response was never requested");
      capturedRequests.clear();
    }
  }

  @AfterAll
  void stopServer() {
    httpServer.stop(0);
  }

  private SolanaRpcClient rpcClient() {
    return SolanaRpcClient.build().httpClient(HTTP_CLIENT).endpoint(endpoint).createClient();
  }

  private synchronized void assertLastRequestContains(final String... fragments) {
    final var request = capturedRequests.getLast();
    for (final var fragment : fragments) {
      assertTrue(request.contains(fragment), () -> "request missing fragment <" + fragment + ">: " + request);
    }
  }

  private static String accountInfoResult(final byte[] data, final String ownerBase58) {
    return String.format(
        "{\"jsonrpc\":\"2.0\",\"result\":{\"context\":{\"apiVersion\":\"2.1.0\",\"slot\":123},\"value\":"
            + "{\"data\":[\"%s\",\"base64\"],\"executable\":false,\"lamports\":1000000000,\"owner\":\"%s\","
            + "\"rentEpoch\":18446744073709551615,\"space\":%d}},\"id\":1}",
        Base64.getEncoder().encodeToString(data), ownerBase58, data.length);
  }

  private static byte[] jitoStakePool() {
    try (final var in = StakePoolRpcFetcherTests.class.getResourceAsStream("/fuzz/stakePoolState/jito")) {
      return in.readAllBytes();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Test
  void programStateAndItsValidatorListAreFetchedByStoredKeys() {
    final var poolKey = PublicKey.fromBase58Encoded("Jito4APyf642JPZPx3hGc6WWJ8zPKtRbRs4P815Awbb");
    final var owner = StakePoolAccounts.MAIN_NET.stakePoolProgram().toBase58();

    respondWith(accountInfoResult(jitoStakePool(), owner));
    final var stateInfo = StakePoolProgramClient.fetchProgramState(rpcClient(), poolKey).join();
    final var state = stateInfo.data();
    assertEquals(AccountType.StakePool, state.accountType(), "the real Jito account parses through the fetch");
    assertLastRequestContains("getAccountInfo", poolKey.toBase58());

    // the list request must carry the account key the fetched state stores —
    // an empty list (count 0) is a valid response
    final byte[] emptyList = new byte[9];
    emptyList[0] = (byte) AccountType.ValidatorList.ordinal();
    respondWith(accountInfoResult(emptyList, owner));
    final var listInfo = StakePoolProgramClient.fetchValidatorList(rpcClient(), state).join();
    assertEquals(0, listInfo.data().validators().length);
    assertLastRequestContains("getAccountInfo", state.validatorList().toBase58());
    assertNotEquals(poolKey, state.validatorList(), "the two fetches target different accounts");
  }

  private synchronized void respondWith(final String jsonRpcResponse) {
    cannedResponses.add(jsonRpcResponse);
  }
}

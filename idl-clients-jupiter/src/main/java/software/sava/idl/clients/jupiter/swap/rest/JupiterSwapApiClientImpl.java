package software.sava.idl.clients.jupiter.swap.rest;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterExecuteOrder;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterQuote;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapTx;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterUltraOrder;
import software.sava.rpc.json.http.client.JsonHttpClient;

import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.UnknownServiceException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static java.net.http.HttpResponse.BodyHandlers.ofByteArray;
import static java.util.Objects.requireNonNullElse;
import static software.sava.rpc.json.PublicKeyEncoding.PARSE_BASE58_PUBLIC_KEY;

final class JupiterSwapApiClientImpl extends JsonHttpClient implements JupiterSwapApiClient {

  // Legacy Swap
  private static final Function<HttpResponse<?>, JupiterQuote> QUOTE_PARSER = applyGenericResponse(JupiterQuote::parse);
  private static final Function<HttpResponse<?>, JupiterSwapTx> SWAP_TX = applyGenericResponse(JupiterSwapTx::parse);
  private static final Function<HttpResponse<?>, byte[]> SWAP_INSTRUCTIONS_TX = response -> {
    final var body = readBody(response);
    final int statusCode = response.statusCode();
    if (statusCode < 200 || statusCode >= 300) {
      throw new UncheckedIOException(new UnknownServiceException(String.format(
          "HTTP request failed with [httpCode:%d], [body=%s]", statusCode, new String(body))));
    } else {
      return body;
    }
  };
  private static final Function<HttpResponse<?>, Map<String, PublicKey>> PROGRAM_LABEL_PARSER = applyGenericResponse(ji -> {
    final var programLabels = new TreeMap<String, PublicKey>(String.CASE_INSENSITIVE_ORDER);
    for (PublicKey program; (program = ji.applyObjField(PARSE_BASE58_PUBLIC_KEY)) != null; ) {
      final var dex = ji.readString();
      final var previousDex = programLabels.put(dex, program);
      if (previousDex != null) {
        throw new IllegalStateException(String.format("Duplicate case insensitive dexes: [%s] [%s]", previousDex, dex));
      }
    }
    return programLabels;
  });

  // Ultra
  private static final Function<HttpResponse<?>, JupiterUltraOrder> ULTRA_ORDER_PARSER = applyGenericResponse(JupiterUltraOrder::parse);
  private static final Function<HttpResponse<?>, JupiterExecuteOrder> EXECUTE_ULTRA_ORDER_PARSER = applyGenericResponse(JupiterExecuteOrder::parse);

  private final String quotePathFormat;
  private final String quotePath;
  private final URI swapURI;
  private final URI swapInstructionsURI;
  private final URI executeUltraOrderURI;
  private final String ultraAmountOrderPathFormat;
  private final HttpRequest programLabelsRequest;
  private final Function<HttpResponse<?>, JupiterQuote> quoteParser;
  private final Function<HttpResponse<?>, JupiterUltraOrder> ultraOrderParser;
  private final Function<HttpResponse<?>, JupiterExecuteOrder> executeUltraOrderParser;

  JupiterSwapApiClientImpl(final URI endpoint,
                           final HttpClient httpClient,
                           final Duration requestTimeout,
                           final UnaryOperator<HttpRequest.Builder> extendRequest,
                           final BiPredicate<HttpResponse<?>, byte[]> testResponse,
                           final String quotePathFormat,
                           final String quotePath,
                           final URI swapURI,
                           final URI swapInstructionsURI,
                           final URI programIdToLabelURI) {
    super(endpoint, httpClient, requestTimeout, extendRequest, null, testResponse);
    this.quotePathFormat = quotePathFormat;
    this.quotePath = quotePath;
    this.swapURI = swapURI;
    this.swapInstructionsURI = swapInstructionsURI;
    this.ultraAmountOrderPathFormat = "/ultra/v1/order?amount=%s&%s";
    this.executeUltraOrderURI = endpoint.resolve("/ultra/v1/execute");
    this.programLabelsRequest = newRequest(programIdToLabelURI).build();
    this.quoteParser = wrapResponseParser(QUOTE_PARSER);
    this.ultraOrderParser = wrapResponseParser(ULTRA_ORDER_PARSER);
    this.executeUltraOrderParser = wrapResponseParser(EXECUTE_ULTRA_ORDER_PARSER);
  }

  @Override
  public CompletableFuture<Map<String, PublicKey>> dexLabelToProgramIdMap() {
    return httpClient.sendAsync(programLabelsRequest, ofByteArray()).thenApply(wrapResponseParser(PROGRAM_LABEL_PARSER));
  }

  @Override
  public CompletableFuture<JupiterQuote> quote(final BigInteger amount, final String query) {
    return quote(amount, query, requestTimeout);
  }

  @Override
  public CompletableFuture<JupiterQuote> quote(final String query) {
    return quote(query, requestTimeout);
  }

  @Override
  public CompletableFuture<JupiterQuote> quote(final BigInteger amount,
                                               final String query,
                                               final Duration requestTimeout) {
    final var pathAndQuery = String.format(quotePathFormat, amount, query);
    final var request = newRequest(pathAndQuery, requestTimeout).GET().build();
    return this.httpClient.sendAsync(request, ofByteArray()).thenApply(quoteParser);
  }

  @Override
  public CompletableFuture<JupiterQuote> quote(final String query, final Duration requestTimeout) {
    final var request = newRequest(quotePath + query, requestTimeout).GET().build();
    return this.httpClient.sendAsync(request, ofByteArray()).thenApply(quoteParser);
  }

  @Override
  public CompletableFuture<JupiterSwapTx> swap(final StringBuilder jsonBodyBuilder, final JupiterQuote jupiterQuote) {
    return swap(jsonBodyBuilder, jupiterQuote, requestTimeout);
  }

  @Override
  public CompletableFuture<JupiterSwapTx> swap(final StringBuilder jsonBodyBuilder, final byte[] quoteResponseJson) {
    return swap(jsonBodyBuilder, quoteResponseJson, requestTimeout);
  }

  @Override
  public CompletableFuture<JupiterSwapTx> swap(final String jsonBodyPrefix, final JupiterQuote jupiterQuote) {
    return swap(jsonBodyPrefix, jupiterQuote, requestTimeout);
  }

  @Override
  public CompletableFuture<JupiterSwapTx> swap(final String jsonBodyPrefix, final byte[] quoteResponseJson) {
    return swap(jsonBodyPrefix, quoteResponseJson, requestTimeout);
  }

  @Override
  public CompletableFuture<JupiterSwapTx> swap(final StringBuilder jsonBodyBuilder,
                                               final JupiterQuote jupiterQuote,
                                               final Duration requestTimeout) {
    return swap(jsonBodyBuilder, jupiterQuote.quoteResponseJson(), requestTimeout);
  }

  @Override
  public CompletableFuture<JupiterSwapTx> swap(final StringBuilder jsonBodyBuilder,
                                               final byte[] quoteResponseJson,
                                               final Duration requestTimeout) {
    return swap(jsonBodyBuilder.append(new String(quoteResponseJson)).append('}').toString(), requestTimeout);
  }

  @Override
  public CompletableFuture<JupiterSwapTx> swap(final String jsonBodyPrefix,
                                               final JupiterQuote jupiterQuote,
                                               final Duration requestTimeout) {
    return swap(jsonBodyPrefix, jupiterQuote.quoteResponseJson(), requestTimeout);
  }

  @Override
  public CompletableFuture<JupiterSwapTx> swap(final String jsonBodyPrefix,
                                               final byte[] quoteResponseJson,
                                               final Duration requestTimeout) {
    return swap(jsonBodyPrefix + new String(quoteResponseJson) + '}', requestTimeout);
  }

  private CompletableFuture<JupiterSwapTx> swap(final String jsonBody, final Duration requestTimeout) {
    return sendPostRequest(swapURI, SWAP_TX, requestTimeout, jsonBody);
  }

  @Override
  public CompletableFuture<byte[]> swapInstructions(final StringBuilder jsonBodyBuilder,
                                                    final JupiterQuote jupiterQuote) {
    return swapInstructions(jsonBodyBuilder, jupiterQuote, requestTimeout);
  }

  @Override
  public CompletableFuture<byte[]> swapInstructions(final StringBuilder jsonBodyBuilder,
                                                    final byte[] quoteResponseJson) {
    return swapInstructions(jsonBodyBuilder, quoteResponseJson, requestTimeout);
  }

  @Override
  public CompletableFuture<byte[]> swapInstructions(final String jsonBodyPrefix, final JupiterQuote jupiterQuote) {
    return swapInstructions(jsonBodyPrefix, jupiterQuote, requestTimeout);
  }

  @Override
  public CompletableFuture<byte[]> swapInstructions(final String jsonBodyPrefix, final byte[] quoteResponseJson) {
    return swapInstructions(jsonBodyPrefix, quoteResponseJson, requestTimeout);
  }

  @Override
  public CompletableFuture<byte[]> swapInstructions(final StringBuilder jsonBodyBuilder,
                                                    final JupiterQuote jupiterQuote,
                                                    final Duration requestTimeout) {
    return swapInstructions(jsonBodyBuilder, jupiterQuote.quoteResponseJson(), requestTimeout);
  }

  @Override
  public CompletableFuture<byte[]> swapInstructions(final StringBuilder jsonBodyBuilder,
                                                    final byte[] quoteResponseJson,
                                                    final Duration requestTimeout) {
    return swapInstructions(jsonBodyBuilder.append(new String(quoteResponseJson)).append('}').toString(), requestTimeout);
  }

  @Override
  public CompletableFuture<byte[]> swapInstructions(final String jsonBodyPrefix,
                                                    final JupiterQuote jupiterQuote,
                                                    final Duration requestTimeout) {
    return swapInstructions(jsonBodyPrefix, jupiterQuote.quoteResponseJson(), requestTimeout);
  }

  @Override
  public CompletableFuture<byte[]> swapInstructions(final String jsonBodyPrefix,
                                                    final byte[] quoteResponseJson,
                                                    final Duration requestTimeout) {
    return swapInstructions(jsonBodyPrefix + new String(quoteResponseJson) + '}', requestTimeout);
  }

  @Override
  public CompletableFuture<byte[]> swapInstructions(final String jsonBody, final Duration requestTimeout) {
    return sendPostRequest(swapInstructionsURI, SWAP_INSTRUCTIONS_TX, requestTimeout, jsonBody);
  }

  @Override
  public CompletableFuture<JupiterUltraOrder> ultraOrder(final BigInteger amount,
                                                         final String query,
                                                         final Duration requestTimeout) {
    final var pathAndQuery = String.format(ultraAmountOrderPathFormat, amount, query);
    final var request = newRequest(endpoint.resolve(pathAndQuery), requireNonNullElse(requestTimeout, this.requestTimeout)).GET().build();
    return this.httpClient.sendAsync(request, ofByteArray()).thenApply(ultraOrderParser);
  }

  @Override
  public CompletableFuture<JupiterUltraOrder> ultraOrder(final String query, final Duration requestTimeout) {
    final var url = endpoint.resolve("/ultra/v1/order?" + query);
    final var request = newRequest(url, requireNonNullElse(requestTimeout, this.requestTimeout)).GET().build();
    return this.httpClient.sendAsync(request, ofByteArray()).thenApply(ultraOrderParser);
  }

  @Override
  public CompletableFuture<JupiterExecuteOrder> executeOrder(final String base64SignedTx, final String requestId) {
    return sendPostRequest(executeUltraOrderURI, executeUltraOrderParser, String.format("""
                {
                 "signedTransaction": "%s",
                 "requestId": "%s"
                }""",
            base64SignedTx, requestId
        )
    );
  }
}

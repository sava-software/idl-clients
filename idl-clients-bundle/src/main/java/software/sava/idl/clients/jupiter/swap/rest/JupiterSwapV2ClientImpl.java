package software.sava.idl.clients.jupiter.swap.rest;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapBuildRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapExecuteRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapOrderRequest;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterExecuteOrder;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapBuild;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapOrder;
import software.sava.rpc.json.http.client.JsonHttpClient;
import systems.comodal.jsoniter.JsonIterator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNullElse;
import static software.sava.rpc.json.PublicKeyEncoding.PARSE_BASE58_PUBLIC_KEY;

/// Every call goes through sava-rpc's deadline-armed `sendGetRequest`/`sendPostRequest`; no request is built in the constructor and no `.GET()` is called.
///
/// A query is appended to its resolved path by concatenation, `URI.create(path + "?" + query)`: `URI.resolve("?…")`
/// would drop the path's last segment. The request is serialized before the future exists, so an invalid one throws
/// from the call itself and nothing is sent.
final class JupiterSwapV2ClientImpl extends JsonHttpClient implements JupiterSwapV2Client {

  private static final Function<HttpResponse<?>, JupiterSwapBuild> BUILD_PARSER = applyGenericResponse(JupiterSwapBuild::parse);
  private static final Function<HttpResponse<?>, JupiterSwapOrder> ORDER_PARSER = applyGenericResponse(JupiterSwapOrder::parse);
  private static final Function<HttpResponse<?>, JupiterExecuteOrder> EXECUTE_PARSER = applyGenericResponse(JupiterExecuteOrder::parse);
  private static final Function<HttpResponse<?>, Map<PublicKey, String>> PROGRAM_ID_TO_LABEL_PARSER =
      applyGenericResponse(JupiterSwapV2ClientImpl::parseProgramIdToLabel);

  private final URI buildURI;
  private final URI orderURI;
  private final URI executeURI;
  private final URI programIdToLabelURI;

  JupiterSwapV2ClientImpl(final URI endpoint,
                          final HttpClient httpClient,
                          final Duration requestTimeout,
                          final UnaryOperator<HttpRequest.Builder> extendRequest,
                          final BiPredicate<HttpResponse<?>, byte[]> testResponse,
                          final URI buildURI,
                          final URI orderURI,
                          final URI executeURI,
                          final URI programIdToLabelURI) {
    super(endpoint, httpClient, requestTimeout, extendRequest, testResponse);
    this.buildURI = buildURI;
    this.orderURI = orderURI;
    this.executeURI = executeURI;
    this.programIdToLabelURI = programIdToLabelURI;
  }

  /// The `/program-id-to-label` body as an unmodifiable `LinkedHashMap` in response order; a duplicated key keeps the last label.
  ///
  /// Labels keep their case and may repeat, since several programs can share one: the map is not inverted and
  /// nothing is checked for duplicates. A program id that appears twice keeps its first position and its last label
  /// (`Map#put`), and a JSON null body reads as an empty map.
  static Map<PublicKey, String> parseProgramIdToLabel(final JsonIterator ji) {
    return Collections.unmodifiableMap(
        ji.readMap(new LinkedHashMap<>(), PARSE_BASE58_PUBLIC_KEY, (_, j) -> j.readString())
    );
  }

  @Override
  public CompletableFuture<JupiterSwapBuild> buildSwap(final JupiterSwapBuildRequest request) {
    return sendGetRequest(URI.create(buildURI + "?" + request.serialize()), BUILD_PARSER);
  }

  @Override
  public CompletableFuture<JupiterSwapOrder> order(final JupiterSwapOrderRequest request) {
    return sendGetRequest(URI.create(orderURI + "?" + request.serialize()), ORDER_PARSER);
  }

  @Override
  public CompletableFuture<JupiterExecuteOrder> execute(final JupiterSwapExecuteRequest request,
                                                        final Duration requestTimeout) {
    return sendPostRequest(
        executeURI,
        EXECUTE_PARSER,
        requireNonNullElse(requestTimeout, this.requestTimeout),
        request.toJson()
    );
  }

  @Override
  public CompletableFuture<Map<PublicKey, String>> programIdToLabel() {
    return sendGetRequest(programIdToLabelURI, PROGRAM_ID_TO_LABEL_PARSER);
  }
}

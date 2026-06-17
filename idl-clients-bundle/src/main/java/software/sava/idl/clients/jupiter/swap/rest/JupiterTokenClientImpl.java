package software.sava.idl.clients.jupiter.swap.rest;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterTokenV2;
import software.sava.rpc.json.http.client.JsonHttpClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;

final class JupiterTokenClientImpl extends JsonHttpClient implements JupiterTokenClient {

  private static final Function<HttpResponse<?>, Map<PublicKey, JupiterTokenV2>> TOKENS_V2 = applyGenericResponse(JupiterTokenV2::parseTokens);

  private final URI v2TokenPath;
  private final URI v2RecentTokenPath;

  JupiterTokenClientImpl(final URI endpoint,
                         final HttpClient httpClient,
                         final Duration requestTimeout,
                         final UnaryOperator<HttpRequest.Builder> extendRequest,
                         final BiPredicate<HttpResponse<?>, byte[]> testResponse,
                         final URI v2TokenPath,
                         final URI v2RecentTokenPath) {
    super(endpoint, httpClient, requestTimeout, extendRequest, null, testResponse);
    this.v2TokenPath = v2TokenPath;
    this.v2RecentTokenPath = v2RecentTokenPath;
  }

  private CompletableFuture<Map<PublicKey, JupiterTokenV2>> queryTokens(final String finalPathSegment,
                                                                        final String query) {
    final var url = v2TokenPath.resolve(finalPathSegment + "?query=" + query);
    return sendGetRequest(url, TOKENS_V2);
  }

  private CompletableFuture<Map<PublicKey, JupiterTokenV2>> queryTokens(final String finalPathSegment,
                                                                        final Collection<String> query) {
    return queryTokens(finalPathSegment, String.join(",", query));
  }

  @Override
  public CompletableFuture<Map<PublicKey, JupiterTokenV2>> search(final String query) {
    return queryTokens("search", query);
  }

  @Override
  public CompletableFuture<Map<PublicKey, JupiterTokenV2>> search(final Collection<String> query) {
    return queryTokens("search", query);
  }

  @Override
  public CompletableFuture<Map<PublicKey, JupiterTokenV2>> forTag(final String tag) {
    return queryTokens("tag", tag);
  }

  @Override
  public CompletableFuture<Map<PublicKey, JupiterTokenV2>> forCategory(final String category,
                                                                       final String interval,
                                                                       final int limit) {
    final var url = v2TokenPath.resolve(String.format("%s/%s?limit=%d", category, interval, limit));
    return sendGetRequest(url, TOKENS_V2);
  }

  @Override
  public CompletableFuture<Map<PublicKey, JupiterTokenV2>> recentTokens() {
    return sendGetRequest(v2RecentTokenPath, TOKENS_V2);
  }
}

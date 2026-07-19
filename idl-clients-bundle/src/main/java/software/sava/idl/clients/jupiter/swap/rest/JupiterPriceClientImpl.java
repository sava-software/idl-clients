package software.sava.idl.clients.jupiter.swap.rest;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterPrice;
import software.sava.rpc.json.http.client.JsonHttpClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;

final class JupiterPriceClientImpl extends JsonHttpClient implements JupiterPriceClient {

  private static final Function<HttpResponse<?>, Map<PublicKey, JupiterPrice>> PRICES = applyGenericResponse(JupiterPrice::parsePrices);

  private final URI v3PricePath;

  JupiterPriceClientImpl(final URI endpoint,
                         final HttpClient httpClient,
                         final Duration requestTimeout,
                         final UnaryOperator<HttpRequest.Builder> extendRequest,
                         final BiPredicate<HttpResponse<?>, byte[]> testResponse) {
    super(endpoint, httpClient, requestTimeout, extendRequest, testResponse);
    this.v3PricePath = endpoint.resolve("/price/v3");
  }

  @Override
  public CompletableFuture<Map<PublicKey, JupiterPrice>> price(final String ids) {
    final var url = URI.create(v3PricePath + "?ids=" + ids);
    return sendGetRequest(url, PRICES);
  }
}

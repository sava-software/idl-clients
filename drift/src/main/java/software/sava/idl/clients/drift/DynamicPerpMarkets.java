package software.sava.idl.clients.drift;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import static software.sava.idl.clients.drift.DynamicSpotMarkets.convertJson;
import static software.sava.idl.clients.drift.DynamicSpotMarkets.parseConfigs;

public record DynamicPerpMarkets(PerpMarkets mainNet, PerpMarkets devNet) {

  private static final String DEV_NET_PERP_KEY = "DevnetPerpMarkets: PerpMarketConfig[]";
  private static final String MAIN_NET_PERP_KEY = "MainnetPerpMarkets: PerpMarketConfig[]";

  public static CompletableFuture<DynamicPerpMarkets> fetchMarkets(final HttpClient httpClient) {
    final var responseFuture = fetchPerpMarkets(httpClient);

    return responseFuture.thenApply(response -> {
      final var responseJson = convertJson(response.body());
      final var configs = parseConfigs(
          DEV_NET_PERP_KEY,
          MAIN_NET_PERP_KEY,
          PerpMarketConfig::parseConfigs,
          responseJson
      );
      return new DynamicPerpMarkets(
          PerpMarkets.createRecord(configs.mainNet()),
          PerpMarkets.createRecord(configs.devNet())
      );
    });
  }

  private static CompletableFuture<HttpResponse<String>> fetchPerpMarkets(final HttpClient httpClient) {
    final var marketConstantsURI = URI.create("https://raw.githubusercontent.com/drift-labs/protocol-v2/master/sdk/src/constants/perpMarkets.ts");
    final var request = HttpRequest.newBuilder(marketConstantsURI).GET().build();
    return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
  }
}

package software.sava.idl.clients.drift;

import systems.comodal.jsoniter.JsonIterator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public record DynamicSpotMarkets(SpotMarkets mainNet, SpotMarkets devNet) {

  private static final String DEV_NET_SPOT_KEY = "DevnetSpotMarkets: SpotMarketConfig[]";
  private static final String MAIN_NET_SPOT_KEY = "MainnetSpotMarkets: SpotMarketConfig[]";

  public static CompletableFuture<DynamicSpotMarkets> fetchMarkets(final HttpClient httpClient) {
    final var responseFuture = fetchSpotMarkets(httpClient);
    return responseFuture.thenApply(response -> {
      final var responseJson = convertJson(response.body());
      final var configs = parseConfigs(
          DEV_NET_SPOT_KEY,
          MAIN_NET_SPOT_KEY,
          SpotMarketConfig::parseConfigs,
          responseJson
      );
      return new DynamicSpotMarkets(
          SpotMarkets.createRecord(configs.mainNet()),
          SpotMarkets.createRecord(configs.devNet())
      );
    });
  }

  private static CompletableFuture<HttpResponse<String>> fetchSpotMarkets(final HttpClient httpClient) {
    final var marketConstantsURI = URI.create("https://raw.githubusercontent.com/drift-labs/protocol-v2/master/sdk/src/constants/spotMarkets.ts");
    final var request = HttpRequest.newBuilder(marketConstantsURI).GET().build();
    return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
  }

  static String convertJson(final String javascript) {
    return javascript
        .replace('\'', '"')
        .replaceAll("\\s+.*precision.*", "")
        .replaceAll("\\s+.*precisionExp.*", "")
        .replaceAll(",\n\\s+}", "}")
        .replaceAll(",\n\\s*]", "]")
        .replaceAll("//.*", "")
        .replaceAll("\\s+", " ")
        .replaceAll("OracleSource\\.(\\w+)", "\"$1\"")
        .replace("WRAPPED_SOL_MINT", "\"So11111111111111111111111111111111111111112\"")
        .replaceAll("new PublicKey\\( *\"(\\w+)\" *\\)", "\"$1\"");
  }

  private static String quoteJson(final String javascript) {
    return javascript.replaceAll("(\\w+):\\s+", "\"$1\": ");
  }

  static <T extends MarketConfig> MarketConfigs<T> parseConfigs(final String devNetKey,
                                                                final String mainNetKey,
                                                                final BiFunction<JsonIterator, DriftAccounts, List<T>> configParser,
                                                                final String response) {
    int from = response.indexOf(devNetKey);
    from = response.indexOf('[', from + devNetKey.length());
    int to = response.indexOf("];", from) + 1;

    final var devNetJson = quoteJson(response.substring(from, to));
    var ji = JsonIterator.parse(devNetJson);
    final var devNetConfigs = configParser.apply(ji, DriftAccounts.DEV_NET);

    from = response.indexOf(mainNetKey, to);
    from = response.indexOf('[', from + mainNetKey.length());
    to = response.indexOf("];", from) + 1;

    final var mainNetJson = quoteJson(response.substring(from, to));

    ji = JsonIterator.parse(mainNetJson);
    final var mainNetConfigs = configParser.apply(ji, DriftAccounts.MAIN_NET);

    return new MarketConfigs<>(mainNetConfigs, devNetConfigs);
  }
}

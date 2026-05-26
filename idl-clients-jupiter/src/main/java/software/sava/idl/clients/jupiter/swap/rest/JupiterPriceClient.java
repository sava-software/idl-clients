package software.sava.idl.clients.jupiter.swap.rest;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterPrice;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface JupiterPriceClient {

  int PRICE_LIMIT = 50;

  static JupiterPriceClient.Builder build() {
    return new Builder();
  }

  URI endpoint();

  CompletableFuture<Map<PublicKey, JupiterPrice>> price(final String ids);

  default CompletableFuture<Map<PublicKey, JupiterPrice>> price(final Collection<String> ids) {
    return price(String.join(",", ids));
  }

  default CompletableFuture<Map<PublicKey, JupiterPrice>> priceForKeys(final Collection<PublicKey> ids) {
    return price(ids.stream().map(PublicKey::toBase58).collect(Collectors.joining(",")));
  }

  class Builder extends JupiterClientBuilder<JupiterPriceClient> {

    protected void setURLs() {
      if (endpoint == null) {
        endpoint = URI.create("https://api.jup.ag");
      }
    }

    @Override
    public JupiterPriceClient createClient() {
      setDefaults();
      setURLs();
      return new JupiterPriceClientImpl(
          endpoint,
          httpClient,
          requestTimeout,
          extendRequest(),
          testResponse
      );
    }
  }
}

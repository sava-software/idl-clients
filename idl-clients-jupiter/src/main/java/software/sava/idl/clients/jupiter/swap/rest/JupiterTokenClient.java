package software.sava.idl.clients.jupiter.swap.rest;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterTokenV2;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface JupiterTokenClient {

  static JupiterTokenClient.Builder build() {
    return new Builder();
  }

  URI endpoint();

  CompletableFuture<Map<PublicKey, JupiterTokenV2>> search(final String query);

  CompletableFuture<Map<PublicKey, JupiterTokenV2>> search(final Collection<String> query);

  CompletableFuture<Map<PublicKey, JupiterTokenV2>> forTag(final String tag);

  CompletableFuture<Map<PublicKey, JupiterTokenV2>> forCategory(final String category,
                                                                final String interval,
                                                                final int limit);

  CompletableFuture<Map<PublicKey, JupiterTokenV2>> recentTokens();

  class Builder extends HttpClientBuilder<JupiterTokenClient> {

    protected URI v2TokenPath;
    protected URI v2RecentTokenPath;

    protected void setURLs() {
      if (endpoint == null) {
        endpoint = URI.create("https://lite-api.jup.ag");
      }
      this.v2TokenPath = endpoint.resolve("/tokens/v2/");
      this.v2RecentTokenPath = endpoint.resolve("/tokens/v2/recent");
    }

    @Override
    public JupiterTokenClient createClient() {
      setDefaults();
      setURLs();
      return new JupiterTokenClientImpl(
          endpoint,
          httpClient,
          requestTimeout,
          extendRequest,
          testResponse,
          v2TokenPath,
          v2RecentTokenPath
      );
    }
  }
}

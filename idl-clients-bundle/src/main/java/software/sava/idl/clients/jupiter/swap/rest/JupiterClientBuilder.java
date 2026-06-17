package software.sava.idl.clients.jupiter.swap.rest;

import java.net.http.HttpRequest;
import java.util.function.UnaryOperator;

public abstract class JupiterClientBuilder<C> extends HttpClientBuilder<C> {

  protected String apiKey;

  public JupiterClientBuilder<C> apiKey(final String apiKey) {
    this.apiKey = apiKey;
    return this;
  }

  protected UnaryOperator<HttpRequest.Builder> extendRequest() {
    if (extendRequest == null) {
      return request -> request.header("x-api-key", apiKey);
    } else {
      return request -> extendRequest.apply(request.header("x-api-key", apiKey));
    }
  }
}

package software.sava.idl.clients.jupiter.swap.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.BiPredicate;
import java.util.function.UnaryOperator;

public abstract class HttpClientBuilder<C> {

  protected URI endpoint;
  protected HttpClient httpClient;
  protected Duration requestTimeout;
  protected UnaryOperator<HttpRequest.Builder> extendRequest;
  protected BiPredicate<HttpResponse<?>, byte[]> testResponse;

  public abstract C createClient();

  protected void setDefaults() {
    if (extendRequest == null) {
      extendRequest = UnaryOperator.identity();
    }
    if (httpClient == null) {
      httpClient = HttpClient.newHttpClient();
    }
    if (requestTimeout == null) {
      requestTimeout = Duration.ofSeconds(30);
    }
  }

  public HttpClientBuilder<C> endpoint(final URI endpoint) {
    this.endpoint = endpoint;
    return this;
  }

  public HttpClientBuilder<C> endpoint(final String endpoint) {
    try {
      return endpoint(new URI(endpoint));
    } catch (final URISyntaxException e) {
      throw new IllegalArgumentException("Invalid endpoint URI: " + endpoint, e);
    }
  }

  public HttpClientBuilder<C> httpClient(final HttpClient httpClient) {
    this.httpClient = httpClient;
    return this;
  }

  public HttpClientBuilder<C> requestTimeout(final Duration requestTimeout) {
    this.requestTimeout = requestTimeout;
    return this;
  }

  public HttpClientBuilder<C> extendRequest(final UnaryOperator<HttpRequest.Builder> extendRequest) {
    this.extendRequest = extendRequest;
    return this;
  }

  public HttpClientBuilder<C> testResponse(final BiPredicate<HttpResponse<?>, byte[]> testResponse) {
    this.testResponse = testResponse;
    return this;
  }
}

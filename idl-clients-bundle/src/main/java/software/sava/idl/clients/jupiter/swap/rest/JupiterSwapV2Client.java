package software.sava.idl.clients.jupiter.swap.rest;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapBuildRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapExecuteRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapOrderRequest;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterExecuteOrder;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapBuild;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapOrder;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/// Jupiter Swap API V2 at `https://api.jup.ag/swap/v2`: `/build`, `/order`, `/execute` and `/program-id-to-label`; works without an API key at 0.5 requests per second.
///
/// A non-2xx answer fails the returned future with an `UncheckedIOException` whose message carries the status and body.
/// A body that fails to parse fails it with the parser's exception, which sava-rpc also logs at ERROR.
public interface JupiterSwapV2Client {

  /// A builder whose endpoint defaults to `https://api.jup.ag`.
  static Builder build() {
    return new Builder();
  }

  /// The host the absolute `/swap/v2` paths were resolved against.
  URI endpoint();

  /// `GET /swap/v2/build`: a Metis-routed ExactIn quote with the raw instructions to compose into your own transaction; Jupiter charges no swap fee and `/execute` cannot land it.
  ///
  /// @throws IllegalArgumentException before anything is sent, when the client's request timeout is zero or negative
  /// @throws IllegalStateException    before anything is sent, when [JupiterSwapBuildRequest#serialize()] rejects
  ///                                  the request
  /// @throws NullPointerException     if `request` is null
  CompletableFuture<JupiterSwapBuild> buildSwap(final JupiterSwapBuildRequest request);

  /// `GET /swap/v2/order`: the best quote across Jupiter's routers and, when the request names a taker, an unsigned transaction to sign and pass to [#execute(JupiterSwapExecuteRequest)].
  ///
  /// @throws IllegalArgumentException before anything is sent, when the client's request timeout is zero or negative
  /// @throws IllegalStateException    before anything is sent, when [JupiterSwapOrderRequest#serialize()] rejects
  ///                                  the request
  /// @throws NullPointerException     if `request` is null
  CompletableFuture<JupiterSwapOrder> order(final JupiterSwapOrderRequest request);

  /// [#execute(JupiterSwapExecuteRequest, Duration)] with the client's default request timeout.
  ///
  /// @throws IllegalArgumentException before anything is sent, when the client's default request timeout is zero
  ///                                  or negative
  /// @throws NullPointerException     if `request` is null
  default CompletableFuture<JupiterExecuteOrder> execute(final JupiterSwapExecuteRequest request) {
    return execute(request, null);
  }

  /// `POST /swap/v2/execute`: lands a signed `/order` transaction, with `requestTimeout` or, when null, the client's default.
  ///
  /// It has its own rate-limit bucket and waits for confirmation, so give it a longer timeout than a quote. A timeout,
  /// a 5xx or a result that fails to parse does not prove the transaction failed to land. A new `/order` builds a new
  /// transaction that can land beside this one, so order again only once this one has not landed and can no longer
  /// land: it is still not found after the order's [JupiterSwapOrder#lastValidBlockHeight()], a block height, has
  /// passed, or after [JupiterSwapOrder#expireAt()] for a JupiterZ order. Sending the same request again cannot swap
  /// twice. A transaction's id is its first signature, the fee payer's, and the order names the fee payer as
  /// [JupiterSwapOrder#signatureFeePayer()]. When that is a key you do not hold (a JupiterZ market maker, or Jupiter's
  /// sponsor on an automatically sponsored order), Jupiter adds that signature during `/execute`, so neither your own
  /// signature nor your signed copy gives you the id: look for the fill under the taker's address or in its balances.
  /// A 5xx body may carry the signature, and then it reaches you only inside the failure's message.
  ///
  /// A `Failed` status is a result, not a failed future: read [JupiterExecuteOrder#status()], `code()` and `error()`.
  /// Jupiter answers some rejections before broadcast (its request-level codes -1, -2 and -3) with a 400 whose body
  /// carries `code` and `error` (-2 observed 2026-10-04); that fails the future like any non-2xx answer, so the code
  /// reaches you only inside the failure's message, never through `code()`.
  ///
  /// @throws IllegalArgumentException before anything is sent, when the timeout applied (`requestTimeout`, or the
  ///                                  client's default when it is null) is zero or negative
  /// @throws NullPointerException     if `request` is null
  CompletableFuture<JupiterExecuteOrder> execute(final JupiterSwapExecuteRequest request, final Duration requestTimeout);

  /// `GET /swap/v2/program-id-to-label`: each DEX program id mapped to its label, unmodifiable, in response order, labels exactly as `dexes`/`excludeDexes` take them (case-sensitive; several programs can share one).
  ///
  /// @throws IllegalArgumentException before anything is sent, when the client's request timeout is zero or negative
  CompletableFuture<Map<PublicKey, String>> programIdToLabel();

  /// Resolves the four absolute `/swap/v2` paths against the endpoint when the client is created.
  ///
  /// The setters are declared on the base builders and return those types, but `createClient()` is declared on the
  /// base too, so `JupiterSwapV2Client.build().apiKey(k).endpoint(e).createClient()` compiles. A client keeps the
  /// endpoint, key, request extension and every other setting its builder held when `createClient()` ran: changing
  /// the builder afterwards changes only the clients built after that.
  ///
  /// When a `testResponse` predicate is set and returns false for a response, the returned future completes with
  /// null rather than with a parsed value or a status failure.
  class Builder extends JupiterClientBuilder<JupiterSwapV2Client> {

    /// `/swap/v2/build` resolved against the endpoint by [#setURLs()].
    protected URI buildURI;
    /// `/swap/v2/order` resolved against the endpoint by [#setURLs()].
    protected URI orderURI;
    /// `/swap/v2/execute` resolved against the endpoint by [#setURLs()].
    protected URI executeURI;
    /// `/swap/v2/program-id-to-label` resolved against the endpoint by [#setURLs()].
    protected URI programIdToLabelURI;

    /// Defaults the endpoint to `https://api.jup.ag` and resolves `/swap/v2/build`, `/swap/v2/order`, `/swap/v2/execute` and `/swap/v2/program-id-to-label` against it.
    protected void setURLs() {
      if (endpoint == null) {
        endpoint = URI.create("https://api.jup.ag");
      }
      buildURI = endpoint.resolve("/swap/v2/build");
      orderURI = endpoint.resolve("/swap/v2/order");
      executeURI = endpoint.resolve("/swap/v2/execute");
      programIdToLabelURI = endpoint.resolve("/swap/v2/program-id-to-label");
    }

    /// Applies the defaults, resolves the URLs and returns a client that captured them and the request extension.
    @Override
    public JupiterSwapV2Client createClient() {
      setDefaults();
      setURLs();
      return new JupiterSwapV2ClientImpl(
          endpoint, httpClient, requestTimeout, extendRequest(), testResponse,
          buildURI, orderURI, executeURI, programIdToLabelURI
      );
    }
  }
}

package software.sava.idl.clients.jupiter.swap.rest;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterQuoteRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapBuildRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapExecuteRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapOrderRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterUltraOrderRequest;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterExecuteOrder;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterQuote;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapTx;
import software.sava.idl.clients.jupiter.swap.rest.response.JupiterUltraOrder;

import java.math.BigInteger;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/// @deprecated Metis Swap API v1 (`/swap/v1`) and Ultra (`/ultra/v1`) are unmaintained and superseded by Swap API V2:
/// use [JupiterSwapV2Client]. This client keeps working against the same default URLs.
@Deprecated
public interface JupiterSwapApiClient {

  static JupiterSwapApiClient.Builder build() {
    return new Builder();
  }

  URI endpoint();

  /// @deprecated Calls the unmaintained /swap/v1/program-id-to-label, whose current response fails the returned future with an IllegalStateException because several programs share one label. Use [JupiterSwapV2Client#programIdToLabel()], keyed by program id with labels as served.
  @Deprecated
  CompletableFuture<Map<String, PublicKey>> dexLabelToProgramIdMap();

  /// @deprecated Metis /swap/v1/quote is unmaintained. Use [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)], which returns the quote with its instructions, or [JupiterSwapV2Client#order(JupiterSwapOrderRequest)] without a taker for a price. buildSwap is ExactIn only; for an ExactOut quote either keep this method, or use order(...) with swapMode(SwapMode.ExactOut), which Jupiter answers although its spec documents only ExactIn.
  @Deprecated
  CompletableFuture<JupiterQuote> quote(final BigInteger amount, final String query);

  /// @deprecated Metis /swap/v1/quote is unmaintained. Use [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)], which returns the quote with its instructions, or [JupiterSwapV2Client#order(JupiterSwapOrderRequest)] without a taker for a price. buildSwap is ExactIn only; for an ExactOut quote either keep this method, or use order(...) with swapMode(SwapMode.ExactOut), which Jupiter answers although its spec documents only ExactIn.
  @Deprecated
  CompletableFuture<JupiterQuote> quote(final String query);

  /// @deprecated Metis /swap/v1/quote is unmaintained. Use [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)], which returns the quote with its instructions, or [JupiterSwapV2Client#order(JupiterSwapOrderRequest)] without a taker for a price. buildSwap is ExactIn only; for an ExactOut quote either keep this method, or use order(...) with swapMode(SwapMode.ExactOut), which Jupiter answers although its spec documents only ExactIn.
  @Deprecated
  default CompletableFuture<JupiterQuote> quote(final JupiterQuoteRequest request) {
    return quote(request.serialize());
  }

  /// @deprecated Metis /swap/v1/quote is unmaintained. Use [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)], which returns the quote with its instructions, or [JupiterSwapV2Client#order(JupiterSwapOrderRequest)] without a taker for a price. buildSwap is ExactIn only; for an ExactOut quote either keep this method, or use order(...) with swapMode(SwapMode.ExactOut), which Jupiter answers although its spec documents only ExactIn.
  @Deprecated
  CompletableFuture<JupiterQuote> quote(final BigInteger amount,
                                        final String query,
                                        final Duration requestTimeout);

  /// @deprecated Metis /swap/v1/quote is unmaintained. Use [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)], which returns the quote with its instructions, or [JupiterSwapV2Client#order(JupiterSwapOrderRequest)] without a taker for a price. buildSwap is ExactIn only; for an ExactOut quote either keep this method, or use order(...) with swapMode(SwapMode.ExactOut), which Jupiter answers although its spec documents only ExactIn.
  @Deprecated
  CompletableFuture<JupiterQuote> quote(final String query, Duration requestTimeout);

  /// @deprecated Metis /swap/v1/quote is unmaintained. Use [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)], which returns the quote with its instructions, or [JupiterSwapV2Client#order(JupiterSwapOrderRequest)] without a taker for a price. buildSwap is ExactIn only; for an ExactOut quote either keep this method, or use order(...) with swapMode(SwapMode.ExactOut), which Jupiter answers although its spec documents only ExactIn.
  @Deprecated
  default CompletableFuture<JupiterQuote> quote(final JupiterQuoteRequest request, final Duration requestTimeout) {
    return quote(request.serialize(), requestTimeout);
  }

  /// @deprecated Metis /swap/v1/swap is unmaintained. Use [JupiterSwapV2Client#order(JupiterSwapOrderRequest)] with a
  /// taker and [JupiterSwapV2Client#execute(JupiterSwapExecuteRequest)], or assemble
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]'s instructions yourself. buildSwap is ExactIn only; for
  /// an ExactOut quote either keep this method, or use order(...) with swapMode(SwapMode.ExactOut), which Jupiter
  /// answers although its spec documents only ExactIn.
  @Deprecated
  CompletableFuture<JupiterSwapTx> swap(final StringBuilder jsonBodyBuilder, final JupiterQuote jupiterQuote);

  /// @deprecated Metis /swap/v1/swap is unmaintained. Use [JupiterSwapV2Client#order(JupiterSwapOrderRequest)] with a
  /// taker and [JupiterSwapV2Client#execute(JupiterSwapExecuteRequest)], or assemble
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]'s instructions yourself. buildSwap is ExactIn only; for
  /// an ExactOut quote either keep this method, or use order(...) with swapMode(SwapMode.ExactOut), which Jupiter
  /// answers although its spec documents only ExactIn.
  @Deprecated
  CompletableFuture<JupiterSwapTx> swap(final StringBuilder jsonBodyBuilder, byte[] quoteResponseJson);

  /// @deprecated Metis /swap/v1/swap is unmaintained. Use [JupiterSwapV2Client#order(JupiterSwapOrderRequest)] with a
  /// taker and [JupiterSwapV2Client#execute(JupiterSwapExecuteRequest)], or assemble
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]'s instructions yourself. buildSwap is ExactIn only; for
  /// an ExactOut quote either keep this method, or use order(...) with swapMode(SwapMode.ExactOut), which Jupiter
  /// answers although its spec documents only ExactIn.
  @Deprecated
  CompletableFuture<JupiterSwapTx> swap(final String jsonBodyPrefix, final JupiterQuote jupiterQuote);

  /// @deprecated Metis /swap/v1/swap is unmaintained. Use [JupiterSwapV2Client#order(JupiterSwapOrderRequest)] with a
  /// taker and [JupiterSwapV2Client#execute(JupiterSwapExecuteRequest)], or assemble
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]'s instructions yourself. buildSwap is ExactIn only; for
  /// an ExactOut quote either keep this method, or use order(...) with swapMode(SwapMode.ExactOut), which Jupiter
  /// answers although its spec documents only ExactIn.
  @Deprecated
  CompletableFuture<JupiterSwapTx> swap(final String jsonBodyPrefix, byte[] quoteResponseJson);

  /// @deprecated Metis /swap/v1/swap is unmaintained. Use [JupiterSwapV2Client#order(JupiterSwapOrderRequest)] with a
  /// taker and [JupiterSwapV2Client#execute(JupiterSwapExecuteRequest)], or assemble
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]'s instructions yourself. buildSwap is ExactIn only; for
  /// an ExactOut quote either keep this method, or use order(...) with swapMode(SwapMode.ExactOut), which Jupiter
  /// answers although its spec documents only ExactIn.
  @Deprecated
  CompletableFuture<JupiterSwapTx> swap(final StringBuilder jsonBodyBuilder,
                                        final JupiterQuote jupiterQuote,
                                        final Duration requestTimeout);

  /// @deprecated Metis /swap/v1/swap is unmaintained. Use [JupiterSwapV2Client#order(JupiterSwapOrderRequest)] with a
  /// taker and [JupiterSwapV2Client#execute(JupiterSwapExecuteRequest)], or assemble
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]'s instructions yourself. buildSwap is ExactIn only; for
  /// an ExactOut quote either keep this method, or use order(...) with swapMode(SwapMode.ExactOut), which Jupiter
  /// answers although its spec documents only ExactIn.
  @Deprecated
  CompletableFuture<JupiterSwapTx> swap(final StringBuilder jsonBodyBuilder,
                                        final byte[] quoteResponseJson,
                                        final Duration requestTimeout);

  /// @deprecated Metis /swap/v1/swap is unmaintained. Use [JupiterSwapV2Client#order(JupiterSwapOrderRequest)] with a
  /// taker and [JupiterSwapV2Client#execute(JupiterSwapExecuteRequest)], or assemble
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]'s instructions yourself. buildSwap is ExactIn only; for
  /// an ExactOut quote either keep this method, or use order(...) with swapMode(SwapMode.ExactOut), which Jupiter
  /// answers although its spec documents only ExactIn.
  @Deprecated
  CompletableFuture<JupiterSwapTx> swap(final String jsonBodyPrefix,
                                        final JupiterQuote jupiterQuote,
                                        final Duration requestTimeout);

  /// @deprecated Metis /swap/v1/swap is unmaintained. Use [JupiterSwapV2Client#order(JupiterSwapOrderRequest)] with a
  /// taker and [JupiterSwapV2Client#execute(JupiterSwapExecuteRequest)], or assemble
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]'s instructions yourself. buildSwap is ExactIn only; for
  /// an ExactOut quote either keep this method, or use order(...) with swapMode(SwapMode.ExactOut), which Jupiter
  /// answers although its spec documents only ExactIn.
  @Deprecated
  CompletableFuture<JupiterSwapTx> swap(final String jsonBodyPrefix,
                                        final byte[] quoteResponseJson,
                                        final Duration requestTimeout);

  /// @deprecated Metis /swap/v1/swap-instructions is unmaintained. Use
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]. buildSwap is ExactIn only; for an ExactOut quote keep
  /// this method, or, if you add no instructions of your own, use order(...) with swapMode(SwapMode.ExactOut), which
  /// Jupiter answers although its spec documents only ExactIn, for a transaction Jupiter assembles and you
  /// cannot modify.
  @Deprecated
  CompletableFuture<byte[]> swapInstructions(final StringBuilder jsonBodyBuilder,
                                             JupiterQuote jupiterQuote);

  /// @deprecated Metis /swap/v1/swap-instructions is unmaintained. Use
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]. buildSwap is ExactIn only; for an ExactOut quote keep
  /// this method, or, if you add no instructions of your own, use order(...) with swapMode(SwapMode.ExactOut), which
  /// Jupiter answers although its spec documents only ExactIn, for a transaction Jupiter assembles and you
  /// cannot modify.
  @Deprecated
  CompletableFuture<byte[]> swapInstructions(final StringBuilder jsonBodyBuilder,
                                             byte[] quoteResponseJson);

  /// @deprecated Metis /swap/v1/swap-instructions is unmaintained. Use
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]. buildSwap is ExactIn only; for an ExactOut quote keep
  /// this method, or, if you add no instructions of your own, use order(...) with swapMode(SwapMode.ExactOut), which
  /// Jupiter answers although its spec documents only ExactIn, for a transaction Jupiter assembles and you
  /// cannot modify.
  @Deprecated
  CompletableFuture<byte[]> swapInstructions(final String jsonBodyPrefix, final JupiterQuote jupiterQuote);

  /// @deprecated Metis /swap/v1/swap-instructions is unmaintained. Use
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]. buildSwap is ExactIn only; for an ExactOut quote keep
  /// this method, or, if you add no instructions of your own, use order(...) with swapMode(SwapMode.ExactOut), which
  /// Jupiter answers although its spec documents only ExactIn, for a transaction Jupiter assembles and you
  /// cannot modify.
  @Deprecated
  CompletableFuture<byte[]> swapInstructions(final String jsonBodyPrefix, byte[] quoteResponseJson);

  /// @deprecated Metis /swap/v1/swap-instructions is unmaintained. Use
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]. buildSwap is ExactIn only; for an ExactOut quote keep
  /// this method, or, if you add no instructions of your own, use order(...) with swapMode(SwapMode.ExactOut), which
  /// Jupiter answers although its spec documents only ExactIn, for a transaction Jupiter assembles and you
  /// cannot modify.
  @Deprecated
  CompletableFuture<byte[]> swapInstructions(final StringBuilder jsonBodyBuilder,
                                             final JupiterQuote jupiterQuote,
                                             final Duration requestTimeout);

  /// @deprecated Metis /swap/v1/swap-instructions is unmaintained. Use
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]. buildSwap is ExactIn only; for an ExactOut quote keep
  /// this method, or, if you add no instructions of your own, use order(...) with swapMode(SwapMode.ExactOut), which
  /// Jupiter answers although its spec documents only ExactIn, for a transaction Jupiter assembles and you
  /// cannot modify.
  @Deprecated
  CompletableFuture<byte[]> swapInstructions(final StringBuilder jsonBodyBuilder,
                                             final byte[] quoteResponseJson,
                                             final Duration requestTimeout);

  /// @deprecated Metis /swap/v1/swap-instructions is unmaintained. Use
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]. buildSwap is ExactIn only; for an ExactOut quote keep
  /// this method, or, if you add no instructions of your own, use order(...) with swapMode(SwapMode.ExactOut), which
  /// Jupiter answers although its spec documents only ExactIn, for a transaction Jupiter assembles and you
  /// cannot modify.
  @Deprecated
  CompletableFuture<byte[]> swapInstructions(final String jsonBodyPrefix,
                                             final JupiterQuote jupiterQuote,
                                             final Duration requestTimeout);

  /// @deprecated Metis /swap/v1/swap-instructions is unmaintained. Use
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]. buildSwap is ExactIn only; for an ExactOut quote keep
  /// this method, or, if you add no instructions of your own, use order(...) with swapMode(SwapMode.ExactOut), which
  /// Jupiter answers although its spec documents only ExactIn, for a transaction Jupiter assembles and you
  /// cannot modify.
  @Deprecated
  CompletableFuture<byte[]> swapInstructions(final String jsonBodyPrefix,
                                             final byte[] quoteResponseJson,
                                             final Duration requestTimeout);

  /// @deprecated Metis /swap/v1/swap-instructions is unmaintained. Use
  /// [JupiterSwapV2Client#buildSwap(JupiterSwapBuildRequest)]. buildSwap is ExactIn only; for an ExactOut quote keep
  /// this method, or, if you add no instructions of your own, use order(...) with swapMode(SwapMode.ExactOut), which
  /// Jupiter answers although its spec documents only ExactIn, for a transaction Jupiter assembles and you
  /// cannot modify.
  @Deprecated
  CompletableFuture<byte[]> swapInstructions(final String jsonBody, final Duration requestTimeout);

  /// @deprecated Ultra /ultra/v1/order is unmaintained, and its route parser rejects a fractional percent or bps. Use [JupiterSwapV2Client#order(JupiterSwapOrderRequest)].
  @Deprecated
  CompletableFuture<JupiterUltraOrder> ultraOrder(final BigInteger amount,
                                                  final String query,
                                                  final Duration requestTimeout);

  /// @deprecated Ultra /ultra/v1/order is unmaintained, and its route parser rejects a fractional percent or bps. Use [JupiterSwapV2Client#order(JupiterSwapOrderRequest)].
  @Deprecated
  CompletableFuture<JupiterUltraOrder> ultraOrder(final String query, final Duration requestTimeout);

  /// @deprecated Ultra /ultra/v1/order is unmaintained, and its route parser rejects a fractional percent or bps. Use [JupiterSwapV2Client#order(JupiterSwapOrderRequest)].
  @Deprecated
  default CompletableFuture<JupiterUltraOrder> ultraOrder(final JupiterUltraOrderRequest request,
                                                          final Duration requestTimeout) {
    return ultraOrder(request.serialize(), requestTimeout);
  }

  /// @deprecated Ultra /ultra/v1/order is unmaintained, and its route parser rejects a fractional percent or bps. Use [JupiterSwapV2Client#order(JupiterSwapOrderRequest)].
  @Deprecated
  default CompletableFuture<JupiterUltraOrder> ultraOrder(final JupiterUltraOrderRequest request) {
    return ultraOrder(request, null);
  }

  /// @deprecated Ultra /ultra/v1/execute is unmaintained. Use [JupiterSwapV2Client#execute(JupiterSwapExecuteRequest)].
  @Deprecated
  CompletableFuture<JupiterExecuteOrder> executeOrder(final String base64SignedTx, final String requestId);

  class Builder extends JupiterClientBuilder<JupiterSwapApiClient> {

    protected String quotePathFormat;
    protected String quotePath;
    protected URI swapURI;
    protected URI swapInstructionsURI;
    protected URI programIdToLabelURI;

    protected void setLocalURLs() {
      if (endpoint == null) {
        endpoint = URI.create("https://localhost:8899");
      }
      this.quotePathFormat = "/quote?amount=%s&%s";
      this.quotePath = "/quote?";
      this.swapURI = endpoint.resolve("/swap");
      this.swapInstructionsURI = endpoint.resolve("/swap-instructions");
      this.programIdToLabelURI = endpoint.resolve("/program-id-to-label");
    }

    protected void setRemoteURLs() {
      if (endpoint == null) {
        endpoint = URI.create("https://api.jup.ag");
      }
      this.quotePathFormat = "/swap/v1/quote?amount=%s&%s";
      this.quotePath = "/swap/v1/quote?";
      this.swapURI = endpoint.resolve("/swap/v1/swap");
      this.swapInstructionsURI = endpoint.resolve("/swap/v1/swap-instructions");
      this.programIdToLabelURI = endpoint.resolve("/swap/v1/program-id-to-label");
    }

    private JupiterSwapApiClient create() {
      return new JupiterSwapApiClientImpl(
          endpoint,
          httpClient,
          requestTimeout,
          extendRequest(),
          testResponse,
          quotePathFormat, quotePath,
          swapURI,
          swapInstructionsURI,
          programIdToLabelURI
      );
    }

    @Override
    public JupiterSwapApiClient createClient() {
      setDefaults();
      setRemoteURLs();
      return create();
    }


    public JupiterSwapApiClient createLocalClient() {
      setDefaults();
      setLocalURLs();
      return create();
    }
  }
}

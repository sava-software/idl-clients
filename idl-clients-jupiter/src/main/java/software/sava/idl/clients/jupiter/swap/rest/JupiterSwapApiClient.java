package software.sava.idl.clients.jupiter.swap.rest;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterQuoteRequest;
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

public interface JupiterSwapApiClient {

  static JupiterSwapApiClient.Builder build() {
    return new Builder();
  }

  URI endpoint();

  CompletableFuture<Map<String, PublicKey>> dexLabelToProgramIdMap();

  CompletableFuture<JupiterQuote> quote(final BigInteger amount, final String query);

  CompletableFuture<JupiterQuote> quote(final String query);

  default CompletableFuture<JupiterQuote> quote(final JupiterQuoteRequest request) {
    return quote(request.serialize());
  }

  CompletableFuture<JupiterQuote> quote(final BigInteger amount,
                                        final String query,
                                        final Duration requestTimeout);

  CompletableFuture<JupiterQuote> quote(final String query, Duration requestTimeout);

  default CompletableFuture<JupiterQuote> quote(final JupiterQuoteRequest request, final Duration requestTimeout) {
    return quote(request.serialize(), requestTimeout);
  }

  CompletableFuture<JupiterSwapTx> swap(final StringBuilder jsonBodyBuilder, final JupiterQuote jupiterQuote);

  CompletableFuture<JupiterSwapTx> swap(final StringBuilder jsonBodyBuilder, byte[] quoteResponseJson);

  CompletableFuture<JupiterSwapTx> swap(final String jsonBodyPrefix, final JupiterQuote jupiterQuote);

  CompletableFuture<JupiterSwapTx> swap(final String jsonBodyPrefix, byte[] quoteResponseJson);

  CompletableFuture<JupiterSwapTx> swap(final StringBuilder jsonBodyBuilder,
                                        final JupiterQuote jupiterQuote,
                                        final Duration requestTimeout);

  CompletableFuture<JupiterSwapTx> swap(final StringBuilder jsonBodyBuilder,
                                        final byte[] quoteResponseJson,
                                        final Duration requestTimeout);

  CompletableFuture<JupiterSwapTx> swap(final String jsonBodyPrefix,
                                        final JupiterQuote jupiterQuote,
                                        final Duration requestTimeout);

  CompletableFuture<JupiterSwapTx> swap(final String jsonBodyPrefix,
                                        final byte[] quoteResponseJson,
                                        final Duration requestTimeout);

  CompletableFuture<byte[]> swapInstructions(final StringBuilder jsonBodyBuilder,
                                             JupiterQuote jupiterQuote);

  CompletableFuture<byte[]> swapInstructions(final StringBuilder jsonBodyBuilder,
                                             byte[] quoteResponseJson);

  CompletableFuture<byte[]> swapInstructions(final String jsonBodyPrefix, final JupiterQuote jupiterQuote);

  CompletableFuture<byte[]> swapInstructions(final String jsonBodyPrefix, byte[] quoteResponseJson);

  CompletableFuture<byte[]> swapInstructions(final StringBuilder jsonBodyBuilder,
                                             final JupiterQuote jupiterQuote,
                                             final Duration requestTimeout);

  CompletableFuture<byte[]> swapInstructions(final StringBuilder jsonBodyBuilder,
                                             final byte[] quoteResponseJson,
                                             final Duration requestTimeout);

  CompletableFuture<byte[]> swapInstructions(final String jsonBodyPrefix,
                                             final JupiterQuote jupiterQuote,
                                             final Duration requestTimeout);

  CompletableFuture<byte[]> swapInstructions(final String jsonBodyPrefix,
                                             final byte[] quoteResponseJson,
                                             final Duration requestTimeout);

  CompletableFuture<byte[]> swapInstructions(final String jsonBody, final Duration requestTimeout);

  CompletableFuture<JupiterUltraOrder> ultraOrder(final BigInteger amount,
                                                  final String query,
                                                  final Duration requestTimeout);

  CompletableFuture<JupiterUltraOrder> ultraOrder(final String query, final Duration requestTimeout);

  default CompletableFuture<JupiterUltraOrder> ultraOrder(final JupiterUltraOrderRequest request,
                                                          final Duration requestTimeout) {
    return ultraOrder(request.serialize(), requestTimeout);
  }

  default CompletableFuture<JupiterUltraOrder> ultraOrder(final JupiterUltraOrderRequest request) {
    return ultraOrder(request, null);
  }

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

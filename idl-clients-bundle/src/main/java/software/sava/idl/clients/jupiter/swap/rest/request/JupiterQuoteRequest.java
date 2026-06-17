package software.sava.idl.clients.jupiter.swap.rest.request;

import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.Collection;

import static java.nio.charset.StandardCharsets.US_ASCII;

public interface JupiterQuoteRequest {

  static Builder buildRequest() {
    return new JupiterQuoteRequestRecord.BuilderImpl();
  }

  static Builder buildRequest(final JupiterQuoteRequest prototype) {
    return prototype == null ? buildRequest() : new JupiterQuoteRequestRecord.BuilderImpl(prototype);
  }

  static JupiterQuoteRequest parseRequest(final JupiterQuoteRequest prototype,
                                          final JsonIterator ji) {
    final var builder = JupiterQuoteRequest.buildRequest(prototype);
    final var parser = new JupiterQuoteRequestRecord.Parser(builder);
    ji.testObject(parser);
    return parser.createRequest();
  }

  static JupiterQuoteRequest parseRequest(final JsonIterator ji) {
    return parseRequest(null, ji);
  }

  BigInteger amount();

  SwapMode swapMode();

  PublicKey inputTokenMint();

  PublicKey outputTokenMint();

  int slippageBps();

  Collection<String> dexes();

  Collection<String> excludeDexes();

  boolean restrictIntermediateTokens();

  boolean onlyDirectRoutes();

  boolean asLegacyTransaction();

  int platformFeeBps();

  int maxAccounts();

  String instructionVersion();

  default String serialize() {
    final var builder = new StringBuilder(256);
    builder.append("inputMint=").append(inputTokenMint().toBase58());
    builder.append("&outputMint=").append(outputTokenMint().toBase58());
    final var amount = amount();
    if (amount != null && amount.signum() > 0) {
      builder.append("&amount=").append(amount);
    }
    if (slippageBps() > 0) {
      builder.append("&slippageBps=").append(slippageBps());
    }
    if (swapMode() != null) {
      builder.append("&swapMode=").append(swapMode().name());
    }
    final var dexes = dexes();
    if (dexes != null && !dexes.isEmpty()) {
      builder.append("&dexes=").append(URLEncoder.encode(String.join(",", dexes), US_ASCII));
    } else {
      final var excludeDexes = excludeDexes();
      if (excludeDexes != null && !excludeDexes.isEmpty()) {
        builder.append("&excludeDexes=").append(URLEncoder.encode(String.join(",", excludeDexes), US_ASCII));
      }
    }
    if (restrictIntermediateTokens()) {
      builder.append("&restrictIntermediateTokens=true");
    }
    if (onlyDirectRoutes()) {
      builder.append("&onlyDirectRoutes=true");
    }
    if (asLegacyTransaction()) {
      builder.append("&asLegacyTransaction=true");
    }
    if (platformFeeBps() > 0) {
      builder.append("&platformFeeBps=").append(platformFeeBps());
    }
    if (maxAccounts() > 0) {
      builder.append("&maxAccounts=").append(maxAccounts());
    }
    final var instructionVersion = instructionVersion();
    if (instructionVersion != null && !instructionVersion.isBlank()) {
      builder.append("&instructionVersion=").append(instructionVersion);
    }
    return builder.toString();
  }

  interface Builder extends JupiterQuoteRequest {

    JupiterQuoteRequest create();

    default Builder amount(final long amount) {
      return amount(BigInteger.valueOf(amount));
    }

    Builder amount(final BigInteger inAmount);

    Builder swapMode(final SwapMode swapMode);

    Builder inputTokenMint(final PublicKey inputTokenMint);

    Builder outputTokenMint(final PublicKey outputTokenMint);

    Builder slippageBps(final int slippageBps);

    Builder dexes(final Collection<String> dexes);

    Builder excludeDexes(final Collection<String> excludeDexes);

    Builder restrictIntermediateTokens(boolean restrictIntermediateTokens);

    Builder onlyDirectRoutes(final boolean onlyDirectRoutes);

    Builder asLegacyTransaction(final boolean asLegacyTransaction);

    Builder platformFeeBps(final int platformFeeBps);

    Builder maxAccounts(final int maxAccounts);

    Builder instructionVersion(final String instructionVersion);
  }
}

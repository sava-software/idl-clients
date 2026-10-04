package software.sava.idl.clients.jupiter.swap.rest.request;

import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.Collection;

import static java.nio.charset.StandardCharsets.US_ASCII;

/// @deprecated Builds Metis /swap/v1/quote queries for the deprecated client. Use [JupiterSwapBuildRequest]:
/// inputTokenMint/outputTokenMint become inputMint/outputMint, create() becomes createRequest(), amount(BigInteger)
/// becomes amount(long), read as an unsigned u64, and taker is required. slippageBps(0), or `"slippageBps": 0` in a
/// parseRequest template, changes meaning: this type sends nothing for 0, so Jupiter's default of 50 applies, while V2
/// sends a 0 bps tolerance. To keep that default with JupiterSwapBuildRequest, leave it unset: call defaultSlippage(),
/// or use JSON null in a template. JupiterSwapBuildRequest is ExactIn only; for an ExactOut quote either keep this
/// type, or use [JupiterSwapOrderRequest] with swapMode(SwapMode.ExactOut), which Jupiter answers although its spec
/// documents only ExactIn. JupiterSwapOrderRequest has no dexes, maxAccounts or platformFeeBps, and no default of 50:
/// an empty OptionalInt or JSON null sends nothing and Jupiter estimates the slippage itself, so call slippageBps(50)
/// to keep 50. onlyDirectRoutes, restrictIntermediateTokens, asLegacyTransaction and instructionVersion have no V2
/// parameter, and JupiterSwapBuildRequest.parseRequest skips their JSON keys without error; a swapMode string other
/// than ExactIn throws IllegalArgumentException there, so an ExactOut template's amount is never sold. Unlike this
/// type, V2 throws IllegalStateException for dexes together with excludeDexes, of which this type sends only dexes,
/// and for a platformFeeBps without a feeAccount, which JupiterSwapRequest held.
@Deprecated
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
    return ji.parseObject(JupiterQuoteRequestRecord.Parser.FIELDS, new JupiterQuoteRequestRecord.Parser(builder));
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
      // Encoded like every other caller-supplied component here. Unencoded, a value such as
      // "V2&slippageBps=9999" appends a second slippageBps to the query and silently overrides
      // the one this request was built with.
      builder.append("&instructionVersion=").append(URLEncoder.encode(instructionVersion, US_ASCII));
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

package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.ContextFieldBufferPredicate;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigDecimal;
import java.util.function.Supplier;

import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

/// One Swap V2 `routePlan` step with `swapInfo` flattened; `percent`, `bps` and `usdValue` are BigDecimal because they can be fractional or null.
///
/// @param ammKey     the venue key exactly as sent, null when absent: a base58 pool address on `/build`, which is Metis-only; on `/order`, routers other than Metis do not promise base58, so use `PublicKey.fromBase58Encoded(ammKey())` only on a Metis step
/// @param label      the venue label exactly as sent, case kept; null when absent
/// @param inputMint  null when absent
/// @param outputMint null when absent
/// @param inAmount   a u64 read unsigned
/// @param outAmount  a u64 read unsigned
/// @param percent    the share in percent, possibly fractional (33.33); null when absent or null
/// @param bps        the canonical share in basis points (10000 = the whole input); null when absent or null
/// @param usdValue   null when absent (the docs' TypeScript type omits it)
public record JupiterSwapRouteStep(String ammKey,
                                   String label,
                                   PublicKey inputMint,
                                   PublicKey outputMint,
                                   long inAmount,
                                   long outAmount,
                                   BigDecimal percent,
                                   BigDecimal bps,
                                   BigDecimal usdValue) {

  /// Parses one `routePlan` element; unknown fields are skipped in the step and in `swapInfo`.
  ///
  /// @throws NumberFormatException    if an amount has a minus sign, a decimal point or an exponent, exceeds 2^64-1 or
  ///                                  is not a number, or `percent`, `bps` or `usdValue` is one `BigDecimal`
  ///                                  cannot parse
  /// @throws IllegalArgumentException if a mint is not a 32-byte base58 key (`""` reads as null)
  /// @throws systems.comodal.jsoniter.JsonException if a value has the wrong JSON type, such as a string `swapInfo`
  public static JupiterSwapRouteStep parse(final JsonIterator ji) {
    return ji.parseObject(new Parser());
  }

  private static final ContextFieldBufferPredicate<Parser> SWAP_INFO_PARSER = (parser, buf, offset, len, ji) -> {
    if (fieldEquals("ammKey", buf, offset, len)) {
      parser.ammKey = ji.readString();
    } else if (fieldEquals("label", buf, offset, len)) {
      parser.label = ji.readString();
    } else if (fieldEquals("inputMint", buf, offset, len)) {
      parser.inputMint = SwapV2Json.readOptionalKey(ji);
    } else if (fieldEquals("outputMint", buf, offset, len)) {
      parser.outputMint = SwapV2Json.readOptionalKey(ji);
    } else if (fieldEquals("inAmount", buf, offset, len)) {
      parser.inAmount = SwapV2Json.readU64(ji);
    } else if (fieldEquals("outAmount", buf, offset, len)) {
      parser.outAmount = SwapV2Json.readU64(ji);
    } else {
      ji.skip();
    }
    return true;
  };

  private static final class Parser implements FieldBufferPredicate, Supplier<JupiterSwapRouteStep> {

    private String ammKey;
    private String label;
    private PublicKey inputMint;
    private PublicKey outputMint;
    private long inAmount;
    private long outAmount;
    private BigDecimal percent;
    private BigDecimal bps;
    private BigDecimal usdValue;

    private Parser() {
    }

    @Override
    public JupiterSwapRouteStep get() {
      return new JupiterSwapRouteStep(ammKey, label, inputMint, outputMint, inAmount, outAmount, percent, bps, usdValue);
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("swapInfo", buf, offset, len)) {
        ji.testObject(this, SWAP_INFO_PARSER);
      } else if (fieldEquals("percent", buf, offset, len)) {
        percent = ji.readBigDecimal();
      } else if (fieldEquals("bps", buf, offset, len)) {
        bps = ji.readBigDecimal();
      } else if (fieldEquals("usdValue", buf, offset, len)) {
        usdValue = ji.readBigDecimal();
      } else {
        ji.skip();
      }
      return true;
    }
  }
}

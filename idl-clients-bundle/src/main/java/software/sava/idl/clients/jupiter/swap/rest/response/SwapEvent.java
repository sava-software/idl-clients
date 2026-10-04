package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.util.function.Supplier;

import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

/// One swap reported in an `/execute` result's `swapEvents`.
///
/// @param inputMint    null when absent, null or `""`
/// @param inputAmount  a u64 read unsigned (`Long.toUnsignedString` / `Long.compareUnsigned` above `Long.MAX_VALUE`); 0 when absent, null or `""`
/// @param outputMint   null when absent, null or `""`
/// @param outputAmount a u64 read unsigned (`Long.toUnsignedString` / `Long.compareUnsigned` above `Long.MAX_VALUE`); 0 when absent, null or `""`
public record SwapEvent(PublicKey inputMint,
                        long inputAmount,
                        PublicKey outputMint,
                        long outputAmount) {

  /// Parses one `swapEvents` element; unknown fields are skipped.
  ///
  /// @throws NumberFormatException    if an amount has a minus sign, a decimal point or an exponent, exceeds 2^64-1 or is a non-empty string that is not a number
  /// @throws IllegalArgumentException if a mint is a string that is neither `""` nor a 32-byte base58 key
  /// @throws systems.comodal.jsoniter.JsonException if an amount is a boolean, object or array, or a mint is neither a
  ///                                                string nor JSON null
  public static SwapEvent parse(final JsonIterator ji) {
    return ji.parseObject(new SwapEvent.Parser());
  }

  private static final class Parser implements FieldBufferPredicate, Supplier<SwapEvent> {

    private PublicKey inputMint;
    private long inputAmount;
    private PublicKey outputMint;
    private long outputAmount;

    @Override
    public SwapEvent get() {
      return new SwapEvent(inputMint, inputAmount, outputMint, outputAmount);
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("inputMint", buf, offset, len)) {
        inputMint = SwapV2Json.readOptionalKey(ji);
      } else if (fieldEquals("inputAmount", buf, offset, len)) {
        inputAmount = SwapV2Json.readU64(ji);
      } else if (fieldEquals("outputMint", buf, offset, len)) {
        outputMint = SwapV2Json.readOptionalKey(ji);
      } else if (fieldEquals("outputAmount", buf, offset, len)) {
        outputAmount = SwapV2Json.readU64(ji);
      } else {
        ji.skip();
      }
      return true;
    }
  }
}

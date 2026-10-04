package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigDecimal;
import java.util.function.Supplier;

import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

/// `/order`'s `platformFee`: the swap's fee rate before any gasless recoup, which the top-level `feeBps` may exceed.
///
/// Without a referral it is Jupiter's default fee for the pair; when your `referralAccount` and `referralFee` apply it
/// is your referral fee, of which Jupiter takes 20% (Ultra's spec: "the Ultra default fee or your integrator fee").
/// A referral whose token account for `feeMint` is not initialized falls back to the default fee; Jupiter's check is
/// that the top-level `feeBps` equals your `referralFee`.
///
/// @param amount  a u64 read unsigned; 0 when absent, null or `""` (quote-only responses omit it)
/// @param feeBps  possibly fractional; null when absent
/// @param feeMint null when absent
public record JupiterSwapPlatformFee(long amount, BigDecimal feeBps, PublicKey feeMint) {

  /// Parses the object; unknown fields are skipped.
  ///
  /// @throws NumberFormatException    if `amount` has a minus sign, a decimal point or an exponent, exceeds 2^64-1 or
  ///                                  is not a number, or `feeBps` is one `BigDecimal` cannot parse
  /// @throws IllegalArgumentException if `feeMint` is not a 32-byte base58 key (`""` reads as null)
  /// @throws systems.comodal.jsoniter.JsonException if a value has the wrong JSON type, such as a boolean `feeBps`
  public static JupiterSwapPlatformFee parse(final JsonIterator ji) {
    return ji.parseObject(new Parser());
  }

  private static final class Parser implements FieldBufferPredicate, Supplier<JupiterSwapPlatformFee> {

    private long amount;
    private BigDecimal feeBps;
    private PublicKey feeMint;

    private Parser() {
    }

    @Override
    public JupiterSwapPlatformFee get() {
      return new JupiterSwapPlatformFee(amount, feeBps, feeMint);
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("amount", buf, offset, len)) {
        amount = SwapV2Json.readU64(ji);
      } else if (fieldEquals("feeBps", buf, offset, len)) {
        feeBps = ji.readBigDecimal();
      } else if (fieldEquals("feeMint", buf, offset, len)) {
        feeMint = SwapV2Json.readOptionalKey(ji);
      } else {
        ji.skip();
      }
      return true;
    }
  }
}

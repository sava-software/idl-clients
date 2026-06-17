package software.sava.idl.clients.jupiter.swap;

import software.sava.core.util.DecimalInteger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public final class JupiterSwapUtil {

  public static BigDecimal quotePrice(final long inAmount, final int inDecimals,
                                      final long quoteOutAmount, final int outDecimals,
                                      final RoundingMode roundingMode) {
    final var in = DecimalInteger.toDecimal(inAmount, inDecimals);
    final var out = DecimalInteger.toDecimal(quoteOutAmount, outDecimals);
    return out.divide(in, outDecimals, roundingMode);
  }

  public static BigDecimal quotePrice(final long inAmount, final int inDecimals,
                                      final long quoteOutAmount, final int outDecimals,
                                      final MathContext mathContext) {
    final var in = DecimalInteger.toDecimal(inAmount, inDecimals);
    final var out = DecimalInteger.toDecimal(quoteOutAmount, outDecimals);
    return out.divide(in, mathContext);
  }

  public static BigDecimal inverseQuotePrice(final long inAmount, final int inDecimals,
                                             final long quoteOutAmount, final int outDecimals,
                                             final RoundingMode roundingMode) {
    final var in = DecimalInteger.toDecimal(inAmount, inDecimals);
    final var out = DecimalInteger.toDecimal(quoteOutAmount, outDecimals);
    return in.divide(out, inDecimals, roundingMode);
  }

  public static BigDecimal inverseQuotePrice(final long inAmount, final int inDecimals,
                                             final long quoteOutAmount, final int outDecimals,
                                             final MathContext mathContext) {
    final var in = DecimalInteger.toDecimal(inAmount, inDecimals);
    final var out = DecimalInteger.toDecimal(quoteOutAmount, outDecimals);
    return in.divide(out, mathContext);
  }

  private JupiterSwapUtil() {
  }
}

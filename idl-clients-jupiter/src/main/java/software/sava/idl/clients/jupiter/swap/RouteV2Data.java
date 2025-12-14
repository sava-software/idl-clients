package software.sava.idl.clients.jupiter.swap;

import software.sava.idl.clients.jupiter.swap.gen.JupiterProgram;
import software.sava.idl.clients.jupiter.swap.gen.types.RoutePlanStepV2;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

import static software.sava.idl.clients.jupiter.swap.gen.JupiterProgram.ROUTE_V_2_DISCRIMINATOR;
import static software.sava.idl.clients.jupiter.swap.gen.JupiterProgram.SHARED_ACCOUNTS_ROUTE_V_2_DISCRIMINATOR;

public record RouteV2Data(long inAmount,
                          long quotedOutAmount,
                          int slippageBps,
                          int platformFeeBps,
                          int positiveSlippageBps,
                          RoutePlanStepV2[] routePlan) {

  public BigDecimal quotePrice(final int inDecimals, final int outDecimals, final RoundingMode roundingMode) {
    return JupiterSwapUtil.quotePrice(
        inAmount, inDecimals,
        quotedOutAmount, outDecimals,
        roundingMode
    );
  }

  public BigDecimal quotePrice(final int inDecimals, final int outDecimals, final MathContext mathContext) {
    return JupiterSwapUtil.quotePrice(
        inAmount, inDecimals,
        quotedOutAmount, outDecimals,
        mathContext
    );
  }

  public BigDecimal inverseQuotePrice(final int inDecimals, final int outDecimals, final RoundingMode roundingMode) {
    return JupiterSwapUtil.inverseQuotePrice(
        inAmount, inDecimals,
        quotedOutAmount, outDecimals,
        roundingMode
    );
  }

  public BigDecimal inverseQuotePrice(final int inDecimals, final int outDecimals, final MathContext mathContext) {
    return JupiterSwapUtil.inverseQuotePrice(
        inAmount, inDecimals,
        quotedOutAmount, outDecimals,
        mathContext
    );
  }

  public static RouteV2Data readData(final byte[] data, final int offset) {
    if (SHARED_ACCOUNTS_ROUTE_V_2_DISCRIMINATOR.equals(data, offset)) {
      final var dataRecord = JupiterProgram.SharedAccountsRouteV2IxData.read(data, offset);
      return new RouteV2Data(
          dataRecord.inAmount(), dataRecord.quotedOutAmount(),
          dataRecord.slippageBps(), dataRecord.platformFeeBps(), dataRecord.positiveSlippageBps(),
          dataRecord.routePlan()
      );
    } else if (ROUTE_V_2_DISCRIMINATOR.equals(data, offset)) {
      final var dataRecord = JupiterProgram.RouteV2IxData.read(data, offset);
      return new RouteV2Data(
          dataRecord.inAmount(), dataRecord.quotedOutAmount(),
          dataRecord.slippageBps(), dataRecord.platformFeeBps(), dataRecord.positiveSlippageBps(),
          dataRecord.routePlan()
      );
    } else {
      throw new UnsupportedOperationException("Only exact in v2 routes are supported.");
    }
  }

  @Override
  public String toString() {
    return "RouteV2Data{inAmount=" + inAmount +
        ", quotedOutAmount=" + quotedOutAmount +
        ", slippageBps=" + slippageBps +
        ", platformFeeBps=" + platformFeeBps +
        ", positiveSlippageBps=" + positiveSlippageBps +
        ", routePlan=" + Arrays.toString(routePlan) +
        '}';
  }
}

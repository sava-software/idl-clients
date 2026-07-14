package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.swap.JupiterSwapUtil;
import systems.comodal.jsoniter.FieldIndexPredicate;
import systems.comodal.jsoniter.FieldMatcher;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Supplier;

import static software.sava.rpc.json.PublicKeyEncoding.parseBase58Encoded;

public record JupiterQuote(PublicKey inputMint,
                           long inAmount,
                           PublicKey outputMint,
                           long outAmount,
                           long otherAmountThreshold,
                           String swapMode,
                           int slippageBps,
                           PlatformFee platformFee,
                           BigDecimal priceImpactPct,
                           List<JupiterRoute> routePlan,
                           long contextSlot,
                           double timeTaken,
                           byte[] quoteResponseJson) {

  public BigDecimal quotePrice(final int inDecimals, final int outDecimals, final RoundingMode roundingMode) {
    return JupiterSwapUtil.quotePrice(
        inAmount, inDecimals,
        outAmount, outDecimals,
        roundingMode
    );
  }

  public BigDecimal quotePrice(final int inDecimals, final int outDecimals, final MathContext mathContext) {
    return JupiterSwapUtil.quotePrice(
        inAmount, inDecimals,
        outAmount, outDecimals,
        mathContext
    );
  }

  public BigDecimal inverseQuotePrice(final int inDecimals, final int outDecimals, final RoundingMode roundingMode) {
    return JupiterSwapUtil.inverseQuotePrice(
        inAmount, inDecimals,
        outAmount, outDecimals,
        roundingMode
    );
  }

  public BigDecimal inverseQuotePrice(final int inDecimals, final int outDecimals, final MathContext mathContext) {
    return JupiterSwapUtil.inverseQuotePrice(
        inAmount, inDecimals,
        outAmount, outDecimals,
        mathContext
    );
  }

  public static JupiterQuote parse(final byte[] quoteResponseJson, final JsonIterator ji) {
    return ji.parseObject(Parser.FIELDS, new Parser(quoteResponseJson));
  }

  private static final class Parser implements FieldIndexPredicate, Supplier<JupiterQuote> {

    private final byte[] quoteResponseJson;
    private PublicKey inputMint;
    private long inAmount;
    private PublicKey outputMint;
    private long outAmount;
    private long otherAmountThreshold;
    private String swapMode;
    private int slippageBps;
    private PlatformFee platformFee;
    private BigDecimal priceImpactPct;
    private List<JupiterRoute> routePlan;
    private long contextSlot;
    private double timeTaken;

    private Parser(final byte[] quoteResponseJson) {
      this.quoteResponseJson = quoteResponseJson;
    }

    @Override
    public JupiterQuote get() {
      return new JupiterQuote(
          inputMint, inAmount,
          outputMint, outAmount,
          otherAmountThreshold,
          swapMode,
          slippageBps,
          platformFee,
          priceImpactPct,
          routePlan,
          contextSlot,
          timeTaken,
          quoteResponseJson
      );
    }

    private static final FieldMatcher FIELDS = FieldMatcher.of(
        "inputMint",
        "inAmount",
        "outputMint",
        "outAmount",
        "otherAmountThreshold",
        "swapMode",
        "slippageBps",
        "platformFee",
        "priceImpactPct",
        "routePlan",
        "contextSlot",
        "timeTaken"
    );

    @Override
    public boolean test(final int fieldIndex, final JsonIterator ji) {
      switch (fieldIndex) {
        case 0 -> inputMint = parseBase58Encoded(ji);
        case 1 -> inAmount = ji.readLong();
        case 2 -> outputMint = parseBase58Encoded(ji);
        case 3 -> outAmount = ji.readLong();
        case 4 -> otherAmountThreshold = ji.readLong();
        case 5 -> swapMode = ji.readString();
        case 6 -> slippageBps = ji.readInt();
        case 7 -> platformFee = PlatformFee.parse(ji);
        case 8 -> priceImpactPct = ji.readBigDecimalDropZeroes();
        case 9 -> routePlan = ji.readList(JupiterRoute::parse);
        case 10 -> contextSlot = ji.readLong();
        case 11 -> timeTaken = ji.readDouble();
        default -> ji.skip();
      }
      return true;
    }
  }
}

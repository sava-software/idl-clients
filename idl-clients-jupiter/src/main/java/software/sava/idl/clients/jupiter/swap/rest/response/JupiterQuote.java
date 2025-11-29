package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static software.sava.rpc.json.PublicKeyEncoding.parseBase58Encoded;
import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

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

  public static JupiterQuote parse(final byte[] quoteResponseJson, final JsonIterator ji) {
    final var parser = new Parser(quoteResponseJson);
    ji.testObject(parser);
    return parser.create();
  }

  private static final class Parser implements FieldBufferPredicate {

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

    private JupiterQuote create() {
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

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("inputMint", buf, offset, len)) {
        inputMint = parseBase58Encoded(ji);
      } else if (fieldEquals("inAmount", buf, offset, len)) {
        inAmount = ji.readLong();
      } else if (fieldEquals("outputMint", buf, offset, len)) {
        outputMint = parseBase58Encoded(ji);
      } else if (fieldEquals("outAmount", buf, offset, len)) {
        outAmount = ji.readLong();
      } else if (fieldEquals("otherAmountThreshold", buf, offset, len)) {
        otherAmountThreshold = ji.readLong();
      } else if (fieldEquals("swapMode", buf, offset, len)) {
        swapMode = ji.readString();
      } else if (fieldEquals("slippageBps", buf, offset, len)) {
        slippageBps = ji.readInt();
      } else if (fieldEquals("platformFee", buf, offset, len)) {
        platformFee = PlatformFee.parse(ji);
      } else if (fieldEquals("priceImpactPct", buf, offset, len)) {
        priceImpactPct = ji.readBigDecimalDropZeroes();
      } else if (fieldEquals("routePlan", buf, offset, len)) {
        this.routePlan = new ArrayList<>();
        while (ji.readArray()) {
          routePlan.add(JupiterRoute.parse(ji));
        }
      } else if (fieldEquals("contextSlot", buf, offset, len)) {
        contextSlot = ji.readLong();
      } else if (fieldEquals("timeTaken", buf, offset, len)) {
        timeTaken = ji.readDouble();
      } else {
        ji.skip();
      }
      return true;
    }
  }
}

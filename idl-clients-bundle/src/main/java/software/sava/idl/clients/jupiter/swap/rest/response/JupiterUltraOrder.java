package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static software.sava.rpc.json.PublicKeyEncoding.parseBase58Encoded;
import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

public record JupiterUltraOrder(String mode,
                                String router,
                                String swapType,
                                String requestId,
                                String quoteId,
                                long inAmount,
                                long outAmount,
                                long otherAmountThreshold,
                                String swapMode,
                                int slippageBps,
                                BigDecimal priceImpactPct,
                                BigDecimal priceImpact,
                                List<JupiterRoute> routePlan,
                                PublicKey inputMint,
                                PublicKey outputMint,
                                int feeBps,
                                PublicKey feeMint,
                                PublicKey maker,
                                PublicKey taker,
                                boolean gasless,
                                byte[] transaction,
                                long prioritizationFeeLamports,
                                PublicKey prioritizationFeePayer,
                                long signatureFeeLamports,
                                PublicKey signatureFeePayer,
                                long rentFeeLamports,
                                PublicKey rentFeePayer,
                                PlatformFee platformFee,
                                BigDecimal inUsdValue,
                                BigDecimal outUsdValue,
                                BigDecimal swapUsdValue,
                                long totalTime,
                                Instant expireAt) {

  public static JupiterUltraOrder parse(final JsonIterator ji) {
    final var parser = new Parser();
    ji.testObject(parser);
    return parser.create();
  }

  private static final class Parser implements FieldBufferPredicate {

    private String mode;
    private String router;
    private String swapType;
    private String requestId;
    private String quoteId;
    private long inAmount;
    private long outAmount;
    private long otherAmountThreshold;
    private String swapMode;
    private int slippageBps;
    private BigDecimal priceImpactPct;
    private List<JupiterRoute> routePlan;
    private PublicKey inputMint;
    private PublicKey outputMint;
    private int feeBps;
    private PublicKey feeMint;
    private PublicKey maker;
    private PublicKey taker;
    private boolean gasless;
    private byte[] transaction;
    private long prioritizationFeeLamports;
    private PublicKey prioritizationFeePayer;
    private long signatureFeeLamports;
    private PublicKey signatureFeePayer;
    private long rentFeeLamports;
    private PublicKey rentFeePayer;
    private PlatformFee platformFee;
    private BigDecimal inUsdValue;
    private BigDecimal outUsdValue;
    private BigDecimal swapUsdValue;
    private BigDecimal priceImpact;
    private long totalTime;
    private Instant expireAt;

    private Parser() {
    }

    private JupiterUltraOrder create() {
      return new JupiterUltraOrder(
          mode, router, swapType, requestId, quoteId, inAmount, outAmount, otherAmountThreshold,
          swapMode, slippageBps, priceImpactPct, priceImpact, routePlan, inputMint, outputMint, feeBps, feeMint,
          maker, taker, gasless, transaction, prioritizationFeeLamports, prioritizationFeePayer,
          signatureFeeLamports, signatureFeePayer, rentFeeLamports, rentFeePayer, platformFee,
          inUsdValue, outUsdValue, swapUsdValue,
          totalTime, expireAt
      );
    }


    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("mode", buf, offset, len)) {
        this.mode = ji.readString();
      } else if (fieldEquals("inputMint", buf, offset, len)) {
        this.inputMint = parseBase58Encoded(ji);
      } else if (fieldEquals("outputMint", buf, offset, len)) {
        this.outputMint = parseBase58Encoded(ji);
      } else if (fieldEquals("requestId", buf, offset, len)) {
        this.requestId = ji.readString();
      } else if (fieldEquals("quoteId", buf, offset, len)) {
        this.quoteId = ji.readString();
      } else if (fieldEquals("inAmount", buf, offset, len)) {
        this.inAmount = ji.readLong();
      } else if (fieldEquals("outAmount", buf, offset, len)) {
        this.outAmount = ji.readLong();
      } else if (fieldEquals("otherAmountThreshold", buf, offset, len)) {
        this.otherAmountThreshold = ji.readLong();
      } else if (fieldEquals("swapMode", buf, offset, len)) {
        this.swapMode = ji.readString();
      } else if (fieldEquals("slippageBps", buf, offset, len)) {
        this.slippageBps = ji.readInt();
      } else if (fieldEquals("priceImpactPct", buf, offset, len)) {
        this.priceImpactPct = ji.readBigDecimal();
      } else if (fieldEquals("priceImpact", buf, offset, len)) {
        this.priceImpact = ji.readBigDecimal();
      } else if (fieldEquals("routePlan", buf, offset, len)) {
        final var routePlan = new ArrayList<JupiterRoute>();
        while (ji.readArray()) {
          routePlan.add(JupiterRoute.parse(ji));
        }
        this.routePlan = routePlan;
      } else if (fieldEquals("feeBps", buf, offset, len)) {
        this.feeBps = ji.readInt();
      } else if (fieldEquals("feeMint", buf, offset, len)) {
        this.feeMint = parseBase58Encoded(ji);
      } else if (fieldEquals("maker", buf, offset, len)) {
        this.maker = parseBase58Encoded(ji);
      } else if (fieldEquals("taker", buf, offset, len)) {
        this.taker = parseBase58Encoded(ji);
      } else if (fieldEquals("gasless", buf, offset, len)) {
        this.gasless = ji.readBoolean();
      } else if (fieldEquals("transaction", buf, offset, len)) {
        this.transaction = ji.readNull() ? null : ji.decodeBase64String();
      } else if (fieldEquals("prioritizationFeeLamports", buf, offset, len)) {
        this.prioritizationFeeLamports = ji.readLong();
      } else if (fieldEquals("prioritizationFeePayer", buf, offset, len)) {
        this.prioritizationFeePayer = parseBase58Encoded(ji);
      } else if (fieldEquals("signatureFeeLamports", buf, offset, len)) {
        this.signatureFeeLamports = ji.readLong();
      } else if (fieldEquals("signatureFeePayer", buf, offset, len)) {
        this.signatureFeePayer = parseBase58Encoded(ji);
      } else if (fieldEquals("rentFeeLamports", buf, offset, len)) {
        this.rentFeeLamports = ji.readLong();
      } else if (fieldEquals("rentFeePayer", buf, offset, len)) {
        this.rentFeePayer = parseBase58Encoded(ji);
      } else if (fieldEquals("platformFee", buf, offset, len)) {
        this.platformFee = PlatformFee.parse(ji);
      } else if (fieldEquals("inUsdValue", buf, offset, len)) {
        this.inUsdValue = ji.readBigDecimal();
      } else if (fieldEquals("outUsdValue", buf, offset, len)) {
        this.outUsdValue = ji.readBigDecimal();
      } else if (fieldEquals("swapUsdValue", buf, offset, len)) {
        this.swapUsdValue = ji.readBigDecimal();
      } else if (fieldEquals("totalTime", buf, offset, len)) {
        this.totalTime = ji.readLong();
      } else if (fieldEquals("swapType", buf, offset, len)) {
        this.swapType = ji.readString();
      } else if (fieldEquals("router", buf, offset, len)) {
        this.router = ji.readString();
      } else if (fieldEquals("expireAt", buf, offset, len)) {
        this.expireAt = Instant.ofEpochSecond(ji.readLong());
      } else {
        ji.skip();
      }
      return true;
    }
  }
}

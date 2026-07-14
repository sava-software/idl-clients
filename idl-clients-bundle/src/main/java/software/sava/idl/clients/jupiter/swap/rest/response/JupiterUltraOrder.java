package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.FieldIndexPredicate;
import systems.comodal.jsoniter.FieldMatcher;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

import static software.sava.rpc.json.PublicKeyEncoding.parseBase58Encoded;

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
    return ji.parseObject(Parser.FIELDS, new Parser());
  }

  private static final class Parser implements FieldIndexPredicate, Supplier<JupiterUltraOrder> {

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

    @Override
    public JupiterUltraOrder get() {
      return new JupiterUltraOrder(
          mode, router, swapType, requestId, quoteId, inAmount, outAmount, otherAmountThreshold,
          swapMode, slippageBps, priceImpactPct, priceImpact, routePlan, inputMint, outputMint, feeBps, feeMint,
          maker, taker, gasless, transaction, prioritizationFeeLamports, prioritizationFeePayer,
          signatureFeeLamports, signatureFeePayer, rentFeeLamports, rentFeePayer, platformFee,
          inUsdValue, outUsdValue, swapUsdValue,
          totalTime, expireAt
      );
    }

    private static final FieldMatcher FIELDS = FieldMatcher.of(
        "mode",
        "inputMint",
        "outputMint",
        "requestId",
        "quoteId",
        "inAmount",
        "outAmount",
        "otherAmountThreshold",
        "swapMode",
        "slippageBps",
        "priceImpactPct",
        "priceImpact",
        "routePlan",
        "feeBps",
        "feeMint",
        "maker",
        "taker",
        "gasless",
        "transaction",
        "prioritizationFeeLamports",
        "prioritizationFeePayer",
        "signatureFeeLamports",
        "signatureFeePayer",
        "rentFeeLamports",
        "rentFeePayer",
        "platformFee",
        "inUsdValue",
        "outUsdValue",
        "swapUsdValue",
        "totalTime",
        "swapType",
        "router",
        "expireAt"
    );

    @Override
    public boolean test(final int fieldIndex, final JsonIterator ji) {
      switch (fieldIndex) {
        case 0 -> this.mode = ji.readString();
        case 1 -> this.inputMint = parseBase58Encoded(ji);
        case 2 -> this.outputMint = parseBase58Encoded(ji);
        case 3 -> this.requestId = ji.readString();
        case 4 -> this.quoteId = ji.readString();
        case 5 -> this.inAmount = ji.readLong();
        case 6 -> this.outAmount = ji.readLong();
        case 7 -> this.otherAmountThreshold = ji.readLong();
        case 8 -> this.swapMode = ji.readString();
        case 9 -> this.slippageBps = ji.readInt();
        case 10 -> this.priceImpactPct = ji.readBigDecimal();
        case 11 -> this.priceImpact = ji.readBigDecimal();
        case 12 -> this.routePlan = ji.readList(JupiterRoute::parse);
        case 13 -> this.feeBps = ji.readInt();
        case 14 -> this.feeMint = parseBase58Encoded(ji);
        case 15 -> this.maker = parseBase58Encoded(ji);
        case 16 -> this.taker = parseBase58Encoded(ji);
        case 17 -> this.gasless = ji.readBoolean();
        case 18 -> this.transaction = ji.readOrNull(JsonIterator::decodeBase64String);
        case 19 -> this.prioritizationFeeLamports = ji.readLong();
        case 20 -> this.prioritizationFeePayer = parseBase58Encoded(ji);
        case 21 -> this.signatureFeeLamports = ji.readLong();
        case 22 -> this.signatureFeePayer = parseBase58Encoded(ji);
        case 23 -> this.rentFeeLamports = ji.readLong();
        case 24 -> this.rentFeePayer = parseBase58Encoded(ji);
        case 25 -> this.platformFee = PlatformFee.parse(ji);
        case 26 -> this.inUsdValue = ji.readBigDecimal();
        case 27 -> this.outUsdValue = ji.readBigDecimal();
        case 28 -> this.swapUsdValue = ji.readBigDecimal();
        case 29 -> this.totalTime = ji.readLong();
        case 30 -> this.swapType = ji.readString();
        case 31 -> this.router = ji.readString();
        case 32 -> this.expireAt = Instant.ofEpochSecond(ji.readLong());
        default -> ji.skip();
      }
      return true;
    }
  }
}

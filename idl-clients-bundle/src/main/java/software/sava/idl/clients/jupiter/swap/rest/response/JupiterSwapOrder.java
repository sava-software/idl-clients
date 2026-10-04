package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.FieldIndexPredicate;
import systems.comodal.jsoniter.FieldMatcher;
import systems.comodal.jsoniter.JsonIterator;
import systems.comodal.jsoniter.ValueType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNullElse;

/// A `GET /swap/v2/order` response: the best quote across Jupiter's routers and, when a taker was given, the transaction to sign.
///
/// Exactly one of [#quoteOnly()], [#transactionBuildFailed()] and [#hasTransaction()] is true.
/// Every number Jupiter does not promise to be whole is a `BigDecimal` kept exactly as sent, scale
/// included: `/order` can be won by routers other than Metis, and nothing promises them whole
/// numbers.
///
/// @param mode                      `ultra` or `manual`, as sent; null when absent
/// @param inputMint                 null when absent
/// @param outputMint                null when absent
/// @param inAmount                  a u64 read unsigned
/// @param outAmount                 a u64 read unsigned
/// @param inUsdValue                null when absent
/// @param outUsdValue               null when absent
/// @param priceImpact               price impact in percentage points (-0.1 = -0.1%); null when absent
/// @param swapUsdValue              null when absent
/// @param otherAmountThreshold      the slippage bound, a u64 read unsigned: for ExactIn the minimum output after
///                                  slippage, in output-mint base units; for ExactOut the maximum input after
///                                  slippage, in input-mint base units
/// @param swapMode                  as sent; null when absent
/// @param slippageBps               the slippage the winning router applied, in basis points, as sent; possibly fractional, since only a Metis route is known to carry it as an integer field; null when absent
/// @param routePlan                 the route steps; never null
/// @param referralAccount           null when absent
/// @param feeMint                   null when absent
/// @param feeBps                    the total fee rate including any gasless recoup, possibly fractional; null when absent
/// @param platformFee               null when absent, JSON null or not an object
/// @param signatureFeeLamports      Jupiter's estimate in lamports, as sent; possibly fractional (a JSON number); null when absent
/// @param signatureFeePayer         null when absent
/// @param prioritizationFeeLamports Jupiter's estimate of priority fees and tips in lamports, as sent; possibly fractional; null when absent
/// @param prioritizationFeePayer    null when absent
/// @param rentFeeLamports           Jupiter's rent estimate in lamports, as sent; possibly fractional; null when absent
/// @param rentFeePayer              null when absent
/// @param router                    the router that won the quote, such as `metis` or `jupiterz`; null when absent
/// @param transaction               null without a taker; empty when a taker was given but no transaction could be built; otherwise the unsigned transaction
/// @param lastValidBlockHeight      the hard expiry of aggregator routes; 0 when absent or `""`
/// @param gasless                   whether a wallet other than the taker pays the signature and priority fees; false
///                                  when absent, JSON null or not a boolean
/// @param requestId                 the id to pass to `/execute`; null when absent
/// @param totalTime                 Jupiter's response time in milliseconds, possibly fractional; null when absent
/// @param taker                     null when absent
/// @param quoteId                   the RFQ quote id; null when absent
/// @param maker                     the RFQ market maker; null when absent
/// @param expireAt                  the RFQ quote expiry exactly as sent (epoch seconds as a string in Jupiter's example); null when absent
/// @param errorCode                 0 unless the build failed; match on `router` plus `errorCode`, never on `errorMessage`
/// @param errorMessage              Jupiter's `errorMessage`, or its duplicate `error` field when `errorMessage` is absent, null or `""`; null when neither is present
public record JupiterSwapOrder(String mode,
                               PublicKey inputMint,
                               PublicKey outputMint,
                               long inAmount,
                               long outAmount,
                               BigDecimal inUsdValue,
                               BigDecimal outUsdValue,
                               BigDecimal priceImpact,
                               BigDecimal swapUsdValue,
                               long otherAmountThreshold,
                               String swapMode,
                               BigDecimal slippageBps,
                               List<JupiterSwapRouteStep> routePlan,
                               PublicKey referralAccount,
                               PublicKey feeMint,
                               BigDecimal feeBps,
                               JupiterSwapPlatformFee platformFee,
                               BigDecimal signatureFeeLamports,
                               PublicKey signatureFeePayer,
                               BigDecimal prioritizationFeeLamports,
                               PublicKey prioritizationFeePayer,
                               BigDecimal rentFeeLamports,
                               PublicKey rentFeePayer,
                               String router,
                               byte[] transaction,
                               long lastValidBlockHeight,
                               boolean gasless,
                               String requestId,
                               BigDecimal totalTime,
                               PublicKey taker,
                               String quoteId,
                               PublicKey maker,
                               String expireAt,
                               int errorCode,
                               String errorMessage) {

  /// Turns an absent route plan into an empty unmodifiable list.
  ///
  /// @throws NullPointerException if the route plan holds a null step
  public JupiterSwapOrder {
    routePlan = List.copyOf(requireNonNullElse(routePlan, List.of()));
  }

  /// Parses an `/order` body; unknown fields and the deprecated `swapType` and `priceImpactPct` are skipped, and `error` fills `errorMessage` only when `errorMessage` is absent, null or `""`.
  ///
  /// The amounts are u64 strings read unsigned, `""` and JSON null reading as 0, and `""` reads as
  /// null for a key. `lastValidBlockHeight` and `errorCode` must be integral (`3.0` is accepted);
  /// every other number is kept as sent.
  ///
  /// @throws ArithmeticException      if `lastValidBlockHeight` is fractional or beyond a long, or `errorCode` is
  ///                                  fractional or beyond an int
  /// @throws NumberFormatException    if an amount (top level, route step or `platformFee`) has a minus sign, a decimal
  ///                                  point or an exponent, exceeds 2^64-1 or is not a number, or any other number but
  ///                                  `expireAt`, which is kept as a string, is one `BigDecimal` cannot parse
  /// @throws IllegalArgumentException if a key is not a 32-byte base58 key (`""` reads as null), or `transaction` is
  ///                                  not base64
  /// @throws systems.comodal.jsoniter.JsonException if a value has the wrong JSON type, such as a numeric `mode` or an
  ///                                                object `routePlan`; a `gasless` that is not a boolean or a
  ///                                                `platformFee` that is not an object reads as false or null instead
  public static JupiterSwapOrder parse(final JsonIterator ji) {
    return ji.parseObject(Parser.FIELDS, new Parser());
  }

  /// No taker was given, so there is no transaction (`transaction` was null).
  public boolean quoteOnly() {
    return transaction == null;
  }

  /// A taker was given but the router could not build a transaction (`transaction` was `""`); see [#router()] and [#errorCode()].
  public boolean transactionBuildFailed() {
    return transaction != null && transaction.length == 0;
  }

  /// An unsigned transaction is ready to sign and pass to `/execute`.
  public boolean hasTransaction() {
    return transaction != null && transaction.length > 0;
  }

  /// [#expireAt()] as epoch seconds; null when it is null or blank.
  ///
  /// @throws NumberFormatException if Jupiter sends another format (the raw string stays available)
  /// @throws java.time.DateTimeException if the seconds lie outside the range of `Instant`
  public Instant expireAtInstant() {
    return expireAt == null || expireAt.isBlank() ? null : Instant.ofEpochSecond(Long.parseLong(expireAt));
  }

  private static final class Parser implements FieldIndexPredicate, Supplier<JupiterSwapOrder> {

    private static final FieldMatcher FIELDS = FieldMatcher.of(
        "mode",
        "inputMint",
        "outputMint",
        "inAmount",
        "outAmount",
        "inUsdValue",
        "outUsdValue",
        "priceImpact",
        "swapUsdValue",
        "otherAmountThreshold",
        "swapMode",
        "slippageBps",
        "routePlan",
        "referralAccount",
        "feeMint",
        "feeBps",
        "platformFee",
        "signatureFeeLamports",
        "signatureFeePayer",
        "prioritizationFeeLamports",
        "prioritizationFeePayer",
        "rentFeeLamports",
        "rentFeePayer",
        "router",
        "transaction",
        "lastValidBlockHeight",
        "gasless",
        "requestId",
        "totalTime",
        "taker",
        "quoteId",
        "maker",
        "expireAt",
        "errorCode",
        "errorMessage",
        "error"
    );

    private String mode;
    private PublicKey inputMint;
    private PublicKey outputMint;
    private long inAmount;
    private long outAmount;
    private BigDecimal inUsdValue;
    private BigDecimal outUsdValue;
    private BigDecimal priceImpact;
    private BigDecimal swapUsdValue;
    private long otherAmountThreshold;
    private String swapMode;
    private BigDecimal slippageBps;
    private List<JupiterSwapRouteStep> routePlan;
    private PublicKey referralAccount;
    private PublicKey feeMint;
    private BigDecimal feeBps;
    private JupiterSwapPlatformFee platformFee;
    private BigDecimal signatureFeeLamports;
    private PublicKey signatureFeePayer;
    private BigDecimal prioritizationFeeLamports;
    private PublicKey prioritizationFeePayer;
    private BigDecimal rentFeeLamports;
    private PublicKey rentFeePayer;
    private String router;
    private byte[] transaction;
    private long lastValidBlockHeight;
    private boolean gasless;
    private String requestId;
    private BigDecimal totalTime;
    private PublicKey taker;
    private String quoteId;
    private PublicKey maker;
    private String expireAt;
    private int errorCode;
    private String errorMessage;
    private String error;

    private Parser() {
    }

    @Override
    public JupiterSwapOrder get() {
      return new JupiterSwapOrder(
          mode, inputMint, outputMint, inAmount, outAmount, inUsdValue, outUsdValue, priceImpact, swapUsdValue,
          otherAmountThreshold, swapMode, slippageBps, routePlan, referralAccount, feeMint, feeBps, platformFee,
          signatureFeeLamports, signatureFeePayer, prioritizationFeeLamports, prioritizationFeePayer,
          rentFeeLamports, rentFeePayer, router, transaction, lastValidBlockHeight, gasless, requestId, totalTime,
          taker, quoteId, maker, expireAt, errorCode,
          errorMessage == null || errorMessage.isEmpty() ? error : errorMessage
      );
    }

    @Override
    public boolean test(final int fieldIndex, final JsonIterator ji) {
      switch (fieldIndex) {
        case 0 -> mode = ji.readString();
        case 1 -> inputMint = SwapV2Json.readOptionalKey(ji);
        case 2 -> outputMint = SwapV2Json.readOptionalKey(ji);
        case 3 -> inAmount = SwapV2Json.readU64(ji);
        case 4 -> outAmount = SwapV2Json.readU64(ji);
        case 5 -> inUsdValue = ji.readBigDecimal();
        case 6 -> outUsdValue = ji.readBigDecimal();
        case 7 -> priceImpact = ji.readBigDecimal();
        case 8 -> swapUsdValue = ji.readBigDecimal();
        case 9 -> otherAmountThreshold = SwapV2Json.readU64(ji);
        case 10 -> swapMode = ji.readString();
        case 11 -> slippageBps = ji.readBigDecimal();
        case 12 -> routePlan = ji.readList(JupiterSwapRouteStep::parse);
        case 13 -> referralAccount = SwapV2Json.readOptionalKey(ji);
        case 14 -> feeMint = SwapV2Json.readOptionalKey(ji);
        case 15 -> feeBps = ji.readBigDecimal();
        case 16 -> platformFee = ji.readOrNull(ValueType.OBJECT, JupiterSwapPlatformFee::parse);
        case 17 -> signatureFeeLamports = ji.readBigDecimal();
        case 18 -> signatureFeePayer = SwapV2Json.readOptionalKey(ji);
        case 19 -> prioritizationFeeLamports = ji.readBigDecimal();
        case 20 -> prioritizationFeePayer = SwapV2Json.readOptionalKey(ji);
        case 21 -> rentFeeLamports = ji.readBigDecimal();
        case 22 -> rentFeePayer = SwapV2Json.readOptionalKey(ji);
        case 23 -> router = ji.readString();
        case 24 -> transaction = ji.decodeBase64String();
        case 25 -> lastValidBlockHeight = SwapV2Json.readIntegral(ji);
        case 26 -> gasless = ji.readBooleanOr(false);
        case 27 -> requestId = ji.readString();
        case 28 -> totalTime = ji.readBigDecimal();
        case 29 -> taker = SwapV2Json.readOptionalKey(ji);
        case 30 -> quoteId = ji.readString();
        case 31 -> maker = SwapV2Json.readOptionalKey(ji);
        case 32 -> expireAt = ji.readNumberOrNumberString();
        case 33 -> errorCode = SwapV2Json.readIntegralInt(ji);
        case 34 -> errorMessage = ji.readString();
        case 35 -> error = ji.readString();
        default -> ji.skip();
      }
      return true;
    }
  }
}

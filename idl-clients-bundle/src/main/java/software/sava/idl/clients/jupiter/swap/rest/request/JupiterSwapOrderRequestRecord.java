package software.sava.idl.clients.jupiter.swap.rest.request;

import software.sava.core.accounts.PublicKey;
import software.sava.rpc.json.PublicKeyEncoding;
import systems.comodal.jsoniter.CharBufferFunction;
import systems.comodal.jsoniter.FieldIndexPredicate;
import systems.comodal.jsoniter.FieldMatcher;
import systems.comodal.jsoniter.JsonIterator;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Supplier;

/// The immutable `/swap/v2/order` request, its builder, its JSON parser and its checks.
record JupiterSwapOrderRequestRecord(PublicKey inputMint,
                                     PublicKey outputMint,
                                     long amount,
                                     PublicKey taker,
                                     PublicKey receiver,
                                     SwapMode swapMode,
                                     OptionalInt slippageBps,
                                     PublicKey referralAccount,
                                     int referralFee,
                                     PublicKey payer,
                                     OptionalLong priorityFeeLamports,
                                     OptionalLong jitoTipLamports,
                                     BroadcastFeeType broadcastFeeType,
                                     List<String> excludeRouters,
                                     List<String> excludeDexes) implements JupiterSwapOrderRequest {

  private static final CharBufferFunction<SwapMode> PARSE_SWAP_MODE =
      FieldMatcher.enumMatcherIgnoreCase(SwapMode.values());
  private static final CharBufferFunction<BroadcastFeeType> PARSE_BROADCAST_FEE_TYPE =
      FieldMatcher.enumMatcherIgnoreCase(BroadcastFeeType.values());

  /// Throws IllegalStateException when `receiver` equals `taker`, when exactly one of `referralAccount` and a
  /// non-zero `referralFee` is set, or when `priorityFeeLamports` or `jitoTipLamports` is set, 0 included, without
  /// `broadcastFeeType`, which Jupiter would ignore (observed 2026-10-04; swap.yaml does not state the rule); checked
  /// in that order.
  static void requireConsistent(final JupiterSwapOrderRequest request) {
    final var receiver = request.receiver();
    if (receiver != null && receiver.equals(request.taker())) {
      throw new IllegalStateException("receiver must differ from taker");
    }
    if ((request.referralAccount() == null) != (request.referralFee() == 0)) {
      throw new IllegalStateException("referralAccount and referralFee must be set together");
    }
    if (request.broadcastFeeType() == null
        && (request.priorityFeeLamports().isPresent() || request.jitoTipLamports().isPresent())) {
      throw new IllegalStateException("priorityFeeLamports and jitoTipLamports require broadcastFeeType");
    }
  }

  /// Throws IllegalStateException naming the first missing required parameter, checked in parameter order
  /// (`inputMint`, `outputMint`, a non-zero `amount`), then applies [#requireConsistent(JupiterSwapOrderRequest)].
  static void requireComplete(final JupiterSwapOrderRequest request) {
    if (request.inputMint() == null) {
      throw missing("inputMint");
    }
    if (request.outputMint() == null) {
      throw missing("outputMint");
    }
    if (request.amount() == 0) {
      throw missing("amount");
    }
    requireConsistent(request);
  }

  private static IllegalStateException missing(final String parameter) {
    return new IllegalStateException("/swap/v2/order requires " + parameter);
  }

  private static <E extends Enum<E>> E requireConstant(final String parameter, final E constant, final E[] constants) {
    if (constant == null) {
      throw new IllegalArgumentException(parameter + " must be one of " + Arrays.toString(constants) + ", ignoring case");
    }
    return constant;
  }

  /// FieldMatcher of the 15 API names in the order [JupiterSwapOrderRequest#serialize()] emits them; values go
  /// through the builder's setters; unknown keys are skipped.
  record Parser(Builder builder) implements FieldIndexPredicate, Supplier<JupiterSwapOrderRequest> {

    static final FieldMatcher FIELDS = FieldMatcher.of(
        "inputMint",
        "outputMint",
        "amount",
        "taker",
        "receiver",
        "swapMode",
        "slippageBps",
        "referralAccount",
        "referralFee",
        "payer",
        "priorityFeeLamports",
        "jitoTipLamports",
        "broadcastFeeType",
        "excludeRouters",
        "excludeDexes"
    );

    /// The parsed request, checked for contradictions but not for required parameters.
    @Override
    public JupiterSwapOrderRequest get() {
      return builder.createRequest();
    }

    @Override
    public boolean test(final int fieldIndex, final JsonIterator ji) {
      switch (fieldIndex) {
        case 0 -> builder.inputMint(PublicKeyEncoding.parseBase58Encoded(ji));
        case 1 -> builder.outputMint(PublicKeyEncoding.parseBase58Encoded(ji));
        case 2 -> builder.amount(Long.parseUnsignedLong(ji.readNumberOrNumberString()));
        case 3 -> builder.taker(PublicKeyEncoding.parseBase58Encoded(ji));
        case 4 -> builder.receiver(PublicKeyEncoding.parseBase58Encoded(ji));
        case 5 -> builder.swapMode(ji.readNull() ? null : requireConstant(
            "swapMode", ji.applyChars(PARSE_SWAP_MODE), SwapMode.values()
        ));
        case 6 -> builder.slippageBps(ji.readNull() ? OptionalInt.empty() : OptionalInt.of(ji.readInt()));
        case 7 -> builder.referralAccount(PublicKeyEncoding.parseBase58Encoded(ji));
        case 8 -> builder.referralFee(ji.readInt());
        case 9 -> builder.payer(PublicKeyEncoding.parseBase58Encoded(ji));
        case 10 -> builder.priorityFeeLamports(ji.readNull() ? OptionalLong.empty() : OptionalLong.of(ji.readLong()));
        case 11 -> builder.jitoTipLamports(ji.readNull() ? OptionalLong.empty() : OptionalLong.of(ji.readLong()));
        case 12 -> builder.broadcastFeeType(ji.readNull() ? null : requireConstant(
            "broadcastFeeType", ji.applyChars(PARSE_BROADCAST_FEE_TYPE), BroadcastFeeType.values()
        ));
        case 13 -> builder.excludeRouters(ji.readList(JsonIterator::readString));
        case 14 -> builder.excludeDexes(ji.readList(JsonIterator::readString));
        default -> ji.skip();
      }
      return true;
    }
  }

  /// Mutable state behind [JupiterSwapOrderRequest.Builder]; optionals start empty, lists start as `List.of()`.
  static final class BuilderImpl implements Builder {

    private PublicKey inputMint;
    private PublicKey outputMint;
    private long amount;
    private PublicKey taker;
    private PublicKey receiver;
    private SwapMode swapMode;
    private OptionalInt slippageBps = OptionalInt.empty();
    private PublicKey referralAccount;
    private int referralFee;
    private PublicKey payer;
    private OptionalLong priorityFeeLamports = OptionalLong.empty();
    private OptionalLong jitoTipLamports = OptionalLong.empty();
    private BroadcastFeeType broadcastFeeType;
    private List<String> excludeRouters = List.of();
    private List<String> excludeDexes = List.of();

    BuilderImpl() {
    }

    BuilderImpl(final JupiterSwapOrderRequest prototype) {
      this.inputMint = prototype.inputMint();
      this.outputMint = prototype.outputMint();
      this.amount = prototype.amount();
      this.taker = prototype.taker();
      this.receiver = prototype.receiver();
      this.swapMode = prototype.swapMode();
      this.slippageBps = prototype.slippageBps();
      this.referralAccount = prototype.referralAccount();
      this.referralFee = prototype.referralFee();
      this.payer = prototype.payer();
      this.priorityFeeLamports = prototype.priorityFeeLamports();
      this.jitoTipLamports = prototype.jitoTipLamports();
      this.broadcastFeeType = prototype.broadcastFeeType();
      this.excludeRouters = SwapV2Query.labels("excludeRouters", prototype.excludeRouters());
      this.excludeDexes = SwapV2Query.labels("excludeDexes", prototype.excludeDexes());
    }

    @Override
    public JupiterSwapOrderRequest createRequest() {
      requireConsistent(this);
      return new JupiterSwapOrderRequestRecord(
          inputMint,
          outputMint,
          amount,
          taker,
          receiver,
          swapMode,
          slippageBps,
          referralAccount,
          referralFee,
          payer,
          priorityFeeLamports,
          jitoTipLamports,
          broadcastFeeType,
          excludeRouters,
          excludeDexes
      );
    }

    @Override
    public Builder inputMint(final PublicKey inputMint) {
      this.inputMint = inputMint;
      return this;
    }

    @Override
    public Builder outputMint(final PublicKey outputMint) {
      this.outputMint = outputMint;
      return this;
    }

    @Override
    public Builder amount(final long amount) {
      this.amount = amount;
      return this;
    }

    @Override
    public Builder taker(final PublicKey taker) {
      this.taker = taker;
      return this;
    }

    @Override
    public Builder receiver(final PublicKey receiver) {
      this.receiver = receiver;
      return this;
    }

    @Override
    public Builder swapMode(final SwapMode swapMode) {
      this.swapMode = swapMode;
      return this;
    }

    @Override
    public Builder slippageBps(final int slippageBps) {
      this.slippageBps = OptionalInt.of(SwapV2Query.requireNonNegative("slippageBps", slippageBps));
      return this;
    }

    @Override
    public Builder slippageBps(final OptionalInt slippageBps) {
      this.slippageBps = slippageBps == null || slippageBps.isEmpty()
          ? OptionalInt.empty()
          : OptionalInt.of(SwapV2Query.requireNonNegative("slippageBps", slippageBps.getAsInt()));
      return this;
    }

    @Override
    public Builder referralAccount(final PublicKey referralAccount) {
      this.referralAccount = referralAccount;
      return this;
    }

    @Override
    public Builder referralFee(final int referralFee) {
      this.referralFee = SwapV2Query.requireNonNegative("referralFee", referralFee);
      return this;
    }

    @Override
    public Builder payer(final PublicKey payer) {
      this.payer = payer;
      return this;
    }

    @Override
    public Builder priorityFeeLamports(final long priorityFeeLamports) {
      this.priorityFeeLamports = OptionalLong.of(SwapV2Query.requireNonNegative("priorityFeeLamports", priorityFeeLamports));
      return this;
    }

    @Override
    public Builder priorityFeeLamports(final OptionalLong priorityFeeLamports) {
      this.priorityFeeLamports = priorityFeeLamports == null || priorityFeeLamports.isEmpty()
          ? OptionalLong.empty()
          : OptionalLong.of(SwapV2Query.requireNonNegative("priorityFeeLamports", priorityFeeLamports.getAsLong()));
      return this;
    }

    @Override
    public Builder jitoTipLamports(final long jitoTipLamports) {
      this.jitoTipLamports = OptionalLong.of(SwapV2Query.requireNonNegative("jitoTipLamports", jitoTipLamports));
      return this;
    }

    @Override
    public Builder jitoTipLamports(final OptionalLong jitoTipLamports) {
      this.jitoTipLamports = jitoTipLamports == null || jitoTipLamports.isEmpty()
          ? OptionalLong.empty()
          : OptionalLong.of(SwapV2Query.requireNonNegative("jitoTipLamports", jitoTipLamports.getAsLong()));
      return this;
    }

    @Override
    public Builder broadcastFeeType(final BroadcastFeeType broadcastFeeType) {
      this.broadcastFeeType = broadcastFeeType;
      return this;
    }

    @Override
    public Builder excludeRouters(final Collection<String> excludeRouters) {
      this.excludeRouters = SwapV2Query.labels("excludeRouters", excludeRouters);
      return this;
    }

    @Override
    public Builder excludeDexes(final Collection<String> excludeDexes) {
      this.excludeDexes = SwapV2Query.labels("excludeDexes", excludeDexes);
      return this;
    }

    @Override
    public PublicKey inputMint() {
      return inputMint;
    }

    @Override
    public PublicKey outputMint() {
      return outputMint;
    }

    @Override
    public long amount() {
      return amount;
    }

    @Override
    public PublicKey taker() {
      return taker;
    }

    @Override
    public PublicKey receiver() {
      return receiver;
    }

    @Override
    public SwapMode swapMode() {
      return swapMode;
    }

    @Override
    public OptionalInt slippageBps() {
      return slippageBps;
    }

    @Override
    public PublicKey referralAccount() {
      return referralAccount;
    }

    @Override
    public int referralFee() {
      return referralFee;
    }

    @Override
    public PublicKey payer() {
      return payer;
    }

    @Override
    public OptionalLong priorityFeeLamports() {
      return priorityFeeLamports;
    }

    @Override
    public OptionalLong jitoTipLamports() {
      return jitoTipLamports;
    }

    @Override
    public BroadcastFeeType broadcastFeeType() {
      return broadcastFeeType;
    }

    @Override
    public List<String> excludeRouters() {
      return excludeRouters;
    }

    @Override
    public List<String> excludeDexes() {
      return excludeDexes;
    }
  }
}

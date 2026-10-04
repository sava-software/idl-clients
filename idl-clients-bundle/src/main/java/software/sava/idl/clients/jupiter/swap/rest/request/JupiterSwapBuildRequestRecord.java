package software.sava.idl.clients.jupiter.swap.rest.request;

import software.sava.core.accounts.PublicKey;
import software.sava.rpc.json.PublicKeyEncoding;
import systems.comodal.jsoniter.FieldIndexPredicate;
import systems.comodal.jsoniter.FieldMatcher;
import systems.comodal.jsoniter.JsonIterator;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/// The immutable `/swap/v2/build` request, its builder, its JSON parser and its checks.
record JupiterSwapBuildRequestRecord(PublicKey inputMint,
                                     PublicKey outputMint,
                                     long amount,
                                     PublicKey taker,
                                     String slippageBps,
                                     boolean fastMode,
                                     List<String> dexes,
                                     List<String> excludeDexes,
                                     int platformFeeBps,
                                     PublicKey feeAccount,
                                     int maxAccounts,
                                     PublicKey payer,
                                     boolean wrapAndUnwrapSol,
                                     PublicKey destinationTokenAccount,
                                     PublicKey nativeDestinationAccount,
                                     int blockhashSlotsToExpiry,
                                     long tipAmount,
                                     String computeUnitPricePercentile,
                                     boolean forJitoBundle) implements JupiterSwapBuildRequest {

  /// Throws IllegalStateException when `dexes` and `excludeDexes` are both non-empty, when both
  /// `destinationTokenAccount` and `nativeDestinationAccount` are set, or when a positive `platformFeeBps` has no
  /// `feeAccount`.
  static void requireConsistent(final JupiterSwapBuildRequest request) {
    if (!request.dexes().isEmpty() && !request.excludeDexes().isEmpty()) {
      throw new IllegalStateException("dexes and excludeDexes are mutually exclusive");
    }
    if (request.destinationTokenAccount() != null && request.nativeDestinationAccount() != null) {
      throw new IllegalStateException("destinationTokenAccount and nativeDestinationAccount are mutually exclusive");
    }
    if (request.platformFeeBps() > 0 && request.feeAccount() == null) {
      throw new IllegalStateException("platformFeeBps requires feeAccount");
    }
  }

  /// Throws IllegalStateException naming the first missing required parameter, checked in parameter order
  /// (`inputMint`, `outputMint`, a non-zero `amount`, `taker`), then applies
  /// [#requireConsistent(JupiterSwapBuildRequest)].
  static void requireComplete(final JupiterSwapBuildRequest request) {
    if (request.inputMint() == null) {
      throw missing("inputMint");
    }
    if (request.outputMint() == null) {
      throw missing("outputMint");
    }
    if (request.amount() == 0) {
      throw missing("amount");
    }
    if (request.taker() == null) {
      throw missing("taker");
    }
    requireConsistent(request);
  }

  private static IllegalStateException missing(final String parameter) {
    return new IllegalStateException("/swap/v2/build requires " + parameter);
  }

  /// FieldMatcher of the 19 API names in the order [JupiterSwapBuildRequest#serialize()] emits them, then
  /// `swapMode`, which `/build` does not take and which is read only to refuse a string other than ExactIn; the
  /// other values go through the builder's setters; unknown keys are skipped.
  record Parser(Builder builder) implements FieldIndexPredicate, Supplier<JupiterSwapBuildRequest> {

    static final FieldMatcher FIELDS = FieldMatcher.of(
        "inputMint",
        "outputMint",
        "amount",
        "taker",
        "slippageBps",
        "mode",
        "dexes",
        "excludeDexes",
        "platformFeeBps",
        "feeAccount",
        "maxAccounts",
        "payer",
        "wrapAndUnwrapSol",
        "destinationTokenAccount",
        "nativeDestinationAccount",
        "blockhashSlotsToExpiry",
        "tipAmount",
        "computeUnitPricePercentile",
        "forJitoBundle",
        "swapMode"
    );

    /// The parsed request, checked for contradictions but not for required parameters.
    @Override
    public JupiterSwapBuildRequest get() {
      return builder.createRequest();
    }

    @Override
    public boolean test(final int fieldIndex, final JsonIterator ji) {
      switch (fieldIndex) {
        case 0 -> builder.inputMint(PublicKeyEncoding.parseBase58Encoded(ji));
        case 1 -> builder.outputMint(PublicKeyEncoding.parseBase58Encoded(ji));
        case 2 -> builder.amount(Long.parseUnsignedLong(ji.readNumberOrNumberString()));
        case 3 -> builder.taker(PublicKeyEncoding.parseBase58Encoded(ji));
        case 4 -> readSlippageBps(ji.readNumberOrNumberString());
        case 5 -> readMode(ji.readString());
        case 6 -> builder.dexes(ji.readList(JsonIterator::readString));
        case 7 -> builder.excludeDexes(ji.readList(JsonIterator::readString));
        case 8 -> builder.platformFeeBps(ji.readInt());
        case 9 -> builder.feeAccount(PublicKeyEncoding.parseBase58Encoded(ji));
        case 10 -> builder.maxAccounts(ji.readInt());
        case 11 -> builder.payer(PublicKeyEncoding.parseBase58Encoded(ji));
        case 12 -> builder.wrapAndUnwrapSol(ji.readBoolean());
        case 13 -> builder.destinationTokenAccount(PublicKeyEncoding.parseBase58Encoded(ji));
        case 14 -> builder.nativeDestinationAccount(PublicKeyEncoding.parseBase58Encoded(ji));
        case 15 -> builder.blockhashSlotsToExpiry(ji.readInt());
        case 16 -> builder.tipAmount(ji.readLong());
        case 17 -> readComputeUnitPricePercentile(ji.readNumberOrNumberString());
        case 18 -> builder.forJitoBundle(ji.readBoolean());
        case 19 -> requireExactIn(ji.readString());
        default -> ji.skip();
      }
      return true;
    }

    private void readSlippageBps(final String value) {
      if (value == null) {
        builder.defaultSlippage();
      } else if ("rtse".equalsIgnoreCase(value)) {
        builder.rtseSlippage();
      } else {
        builder.slippageBps(Integer.parseInt(value));
      }
    }

    private void readMode(final String value) {
      if (value == null) {
        builder.fastMode(false);
      } else if ("fast".equalsIgnoreCase(value)) {
        builder.fastMode(true);
      } else {
        throw new IllegalArgumentException("mode must be fast, ignoring case: " + value);
      }
    }

    private void readComputeUnitPricePercentile(final String value) {
      if (value == null) {
        builder.computeUnitPricePercentile(null);
        return;
      }
      for (final var level : ComputeUnitPriceLevel.values()) {
        if (level.name().equalsIgnoreCase(value)) {
          builder.computeUnitPricePercentile(level);
          return;
        }
      }
      builder.computeUnitPricePercentileBps(Integer.parseInt(value));
    }

    /// An ExactOut amount is the output amount, which `/build` would sell, so only ExactIn or a JSON null passes.
    private static void requireExactIn(final String value) {
      if (value != null && !SwapMode.ExactIn.name().equalsIgnoreCase(value)) {
        throw new IllegalArgumentException(
            "swapMode must be ExactIn, ignoring case, since /swap/v2/build is ExactIn only: " + value
        );
      }
    }
  }

  /// Mutable state behind [JupiterSwapBuildRequest.Builder]; `wrapAndUnwrapSol` starts true, lists start as `List.of()`.
  static final class BuilderImpl implements Builder {

    private PublicKey inputMint;
    private PublicKey outputMint;
    private long amount;
    private PublicKey taker;
    private String slippageBps;
    private boolean fastMode;
    private List<String> dexes = List.of();
    private List<String> excludeDexes = List.of();
    private int platformFeeBps;
    private PublicKey feeAccount;
    private int maxAccounts;
    private PublicKey payer;
    private boolean wrapAndUnwrapSol = true;
    private PublicKey destinationTokenAccount;
    private PublicKey nativeDestinationAccount;
    private int blockhashSlotsToExpiry;
    private long tipAmount;
    private String computeUnitPricePercentile;
    private boolean forJitoBundle;

    BuilderImpl() {
    }

    BuilderImpl(final JupiterSwapBuildRequest prototype) {
      this.inputMint = prototype.inputMint();
      this.outputMint = prototype.outputMint();
      this.amount = prototype.amount();
      this.taker = prototype.taker();
      this.slippageBps = prototype.slippageBps();
      this.fastMode = prototype.fastMode();
      this.dexes = SwapV2Query.labels("dexes", prototype.dexes());
      this.excludeDexes = SwapV2Query.labels("excludeDexes", prototype.excludeDexes());
      this.platformFeeBps = prototype.platformFeeBps();
      this.feeAccount = prototype.feeAccount();
      this.maxAccounts = prototype.maxAccounts();
      this.payer = prototype.payer();
      this.wrapAndUnwrapSol = prototype.wrapAndUnwrapSol();
      this.destinationTokenAccount = prototype.destinationTokenAccount();
      this.nativeDestinationAccount = prototype.nativeDestinationAccount();
      this.blockhashSlotsToExpiry = prototype.blockhashSlotsToExpiry();
      this.tipAmount = prototype.tipAmount();
      this.computeUnitPricePercentile = prototype.computeUnitPricePercentile();
      this.forJitoBundle = prototype.forJitoBundle();
    }

    @Override
    public JupiterSwapBuildRequest createRequest() {
      requireConsistent(this);
      return new JupiterSwapBuildRequestRecord(
          inputMint,
          outputMint,
          amount,
          taker,
          slippageBps,
          fastMode,
          dexes,
          excludeDexes,
          platformFeeBps,
          feeAccount,
          maxAccounts,
          payer,
          wrapAndUnwrapSol,
          destinationTokenAccount,
          nativeDestinationAccount,
          blockhashSlotsToExpiry,
          tipAmount,
          computeUnitPricePercentile,
          forJitoBundle
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
    public Builder slippageBps(final int slippageBps) {
      this.slippageBps = Integer.toString(SwapV2Query.requireNonNegative("slippageBps", slippageBps));
      return this;
    }

    @Override
    public Builder rtseSlippage() {
      this.slippageBps = "rtse";
      return this;
    }

    @Override
    public Builder defaultSlippage() {
      this.slippageBps = null;
      return this;
    }

    @Override
    public Builder fastMode(final boolean fastMode) {
      this.fastMode = fastMode;
      return this;
    }

    @Override
    public Builder dexes(final Collection<String> dexes) {
      this.dexes = SwapV2Query.labels("dexes", dexes);
      return this;
    }

    @Override
    public Builder excludeDexes(final Collection<String> excludeDexes) {
      this.excludeDexes = SwapV2Query.labels("excludeDexes", excludeDexes);
      return this;
    }

    @Override
    public Builder platformFeeBps(final int platformFeeBps) {
      this.platformFeeBps = SwapV2Query.requireNonNegative("platformFeeBps", platformFeeBps);
      return this;
    }

    @Override
    public Builder feeAccount(final PublicKey feeAccount) {
      this.feeAccount = feeAccount;
      return this;
    }

    @Override
    public Builder maxAccounts(final int maxAccounts) {
      this.maxAccounts = SwapV2Query.requireNonNegative("maxAccounts", maxAccounts);
      return this;
    }

    @Override
    public Builder payer(final PublicKey payer) {
      this.payer = payer;
      return this;
    }

    @Override
    public Builder wrapAndUnwrapSol(final boolean wrapAndUnwrapSol) {
      this.wrapAndUnwrapSol = wrapAndUnwrapSol;
      return this;
    }

    @Override
    public Builder destinationTokenAccount(final PublicKey destinationTokenAccount) {
      this.destinationTokenAccount = destinationTokenAccount;
      return this;
    }

    @Override
    public Builder nativeDestinationAccount(final PublicKey nativeDestinationAccount) {
      this.nativeDestinationAccount = nativeDestinationAccount;
      return this;
    }

    @Override
    public Builder blockhashSlotsToExpiry(final int blockhashSlotsToExpiry) {
      this.blockhashSlotsToExpiry = SwapV2Query.requireNonNegative("blockhashSlotsToExpiry", blockhashSlotsToExpiry);
      return this;
    }

    @Override
    public Builder tipAmount(final long tipAmount) {
      this.tipAmount = SwapV2Query.requireNonNegative("tipAmount", tipAmount);
      return this;
    }

    @Override
    public Builder computeUnitPricePercentile(final ComputeUnitPriceLevel level) {
      this.computeUnitPricePercentile = level == null ? null : level.name();
      return this;
    }

    @Override
    public Builder computeUnitPricePercentileBps(final int bps) {
      this.computeUnitPricePercentile = Integer.toString(SwapV2Query.requireNonNegative("computeUnitPricePercentile", bps));
      return this;
    }

    @Override
    public Builder forJitoBundle(final boolean forJitoBundle) {
      this.forJitoBundle = forJitoBundle;
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
    public String slippageBps() {
      return slippageBps;
    }

    @Override
    public boolean fastMode() {
      return fastMode;
    }

    @Override
    public List<String> dexes() {
      return dexes;
    }

    @Override
    public List<String> excludeDexes() {
      return excludeDexes;
    }

    @Override
    public int platformFeeBps() {
      return platformFeeBps;
    }

    @Override
    public PublicKey feeAccount() {
      return feeAccount;
    }

    @Override
    public int maxAccounts() {
      return maxAccounts;
    }

    @Override
    public PublicKey payer() {
      return payer;
    }

    @Override
    public boolean wrapAndUnwrapSol() {
      return wrapAndUnwrapSol;
    }

    @Override
    public PublicKey destinationTokenAccount() {
      return destinationTokenAccount;
    }

    @Override
    public PublicKey nativeDestinationAccount() {
      return nativeDestinationAccount;
    }

    @Override
    public int blockhashSlotsToExpiry() {
      return blockhashSlotsToExpiry;
    }

    @Override
    public long tipAmount() {
      return tipAmount;
    }

    @Override
    public String computeUnitPricePercentile() {
      return computeUnitPricePercentile;
    }

    @Override
    public boolean forJitoBundle() {
      return forJitoBundle;
    }
  }
}

package software.sava.idl.clients.jupiter.swap.rest.request;

import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.JsonIterator;

import java.util.Collection;
import java.util.List;
import java.util.OptionalInt;
import java.util.OptionalLong;

/// The query of `GET /swap/v2/order`; accessors carry the API's parameter names, and a [Builder] is itself a request.
///
/// There is no `dexes` (`/order` honours only `excludeDexes`) and no `closeAuthority` (V2 ignores it).
///
/// The request models the parameters of Jupiter's public API reference (swap.yaml); Jupiter's enterprise-only
/// `integratorFeeBps` and `positiveSlippageBps` (private integrator pages swap/fee-settlement and
/// swap/positive-slippage) are not modelled. Without whitelisting, `/order` ignored `integratorFeeBps` and
/// answered 400 "Not authorized for positive slippage" to `positiveSlippageBps` (observed 2026-10-04).
public interface JupiterSwapOrderRequest {

  /// An empty builder.
  static Builder buildRequest() {
    return new JupiterSwapOrderRequestRecord.BuilderImpl();
  }

  /// A builder holding `prototype`'s parameters; an empty builder when it is null.
  static Builder buildRequest(final JupiterSwapOrderRequest prototype) {
    return prototype == null ? buildRequest() : new JupiterSwapOrderRequestRecord.BuilderImpl(prototype);
  }

  /// Reads an object of API-named keys over `prototype`'s values through the builder's setters; required parameters may be absent, contradictions throw.
  ///
  /// A JSON null unsets a parameter, except `amount` and `referralFee`, which throw on it: 0 unsets those.
  static JupiterSwapOrderRequest parseRequest(final JupiterSwapOrderRequest prototype, final JsonIterator ji) {
    return ji.parseObject(JupiterSwapOrderRequestRecord.Parser.FIELDS, new JupiterSwapOrderRequestRecord.Parser(buildRequest(prototype)));
  }

  /// [#parseRequest(JupiterSwapOrderRequest, JsonIterator)] without a prototype.
  static JupiterSwapOrderRequest parseRequest(final JsonIterator ji) {
    return parseRequest(null, ji);
  }

  /// The mint sold; required.
  PublicKey inputMint();

  /// The mint bought; required.
  PublicKey outputMint();

  /// The amount in base units: the input amount, or the output amount when [#swapMode()] is ExactOut; a u64 held in a
  /// long and sent unsigned; 0 means unset, and a sent request must set it.
  long amount();

  /// The wallet that signs the transaction; null asks for a quote whose `transaction` is null.
  PublicKey taker();

  /// The wallet (not a token account) that receives the output; must differ from the taker; null when unset.
  PublicKey receiver();

  /// `swapMode`; null sends nothing. The API reference documents only ExactIn, and the value is passed through:
  /// Jupiter answered with ExactOut quotes when probed on 2026-10-04, so for ExactOut [#amount()] is the output amount.
  SwapMode swapMode();

  /// Slippage in basis points; empty lets Jupiter choose, and 0 is sent.
  OptionalInt slippageBps();

  /// The referral account; needs [#referralFee()].
  PublicKey referralAccount();

  /// The referral fee in basis points, sent as `referralFee`; 0 means none, otherwise it needs [#referralAccount()].
  int referralFee();

  /// A wallet other than the taker that pays gas; setting it restricts routing to Metis.
  PublicKey payer();

  /// The priority fee in lamports; empty lets Jupiter choose it. A set fee, 0 included, needs [#broadcastFeeType()]:
  /// [Builder#createRequest()] and [#serialize()] reject one without it, because Jupiter would ignore it and charge its
  /// own estimate. The rule rests on behaviour observed on 2026-10-04; swap.yaml does not state it.
  OptionalLong priorityFeeLamports();

  /// The Jito tip in lamports; empty lets Jupiter choose. A set tip, 0 included, needs [#broadcastFeeType()]:
  /// [Builder#createRequest()] and [#serialize()] reject one without it, because Jupiter would ignore it and add no
  /// tip. The rule rests on behaviour observed on 2026-10-04; swap.yaml does not state it.
  OptionalLong jitoTipLamports();

  /// How a set priority fee or tip is applied: [BroadcastFeeType#maxCap] makes it a maximum and
  /// [BroadcastFeeType#exactFee] charges it exactly; null sends nothing. A set fee or tip needs it:
  /// [Builder#createRequest()] and [#serialize()] reject one without it, which Jupiter would ignore (observed
  /// 2026-10-04; swap.yaml does not state the rule). Set alone, it is sent all the same, and Jupiter ignores it.
  BroadcastFeeType broadcastFeeType();

  /// Router names to exclude (`metis`, `jupiterz`, `dflow`, `okx` at the time of writing); empty when unset, never null.
  ///
  /// An unknown name is not a validation error. Observed on 2026-10-04: an unknown name excludes nothing and the call
  /// succeeds, so a misspelled exclusion can still return that router's order; check
  /// [software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapOrder#router()].
  List<String> excludeRouters();

  /// Case-sensitive Metis DEX labels to exclude; empty when unset, never null.
  ///
  /// An unknown or miscased label is not a validation error, so check labels against
  /// [software.sava.idl.clients.jupiter.swap.rest.JupiterSwapV2Client#programIdToLabel()]. Observed on 2026-10-04: an
  /// unknown label is ignored and the call succeeds, so a misspelled exclusion excludes nothing.
  List<String> excludeDexes();

  /// The query without its leading `?`, parameters in the API reference's order, optional ones only when set.
  ///
  /// @throws IllegalStateException if a required parameter is missing or two parameters contradict each other,
  ///                               including a priority fee or Jito tip set without [#broadcastFeeType()]
  default String serialize() {
    JupiterSwapOrderRequestRecord.requireComplete(this);
    final var query = new StringBuilder();
    query.append("inputMint=").append(inputMint().toBase58());
    query.append("&outputMint=").append(outputMint().toBase58());
    query.append("&amount=").append(Long.toUnsignedString(amount()));
    final var taker = taker();
    if (taker != null) {
      query.append("&taker=").append(taker.toBase58());
    }
    final var receiver = receiver();
    if (receiver != null) {
      query.append("&receiver=").append(receiver.toBase58());
    }
    final var swapMode = swapMode();
    if (swapMode != null) {
      query.append("&swapMode=").append(swapMode.name());
    }
    final var slippageBps = slippageBps();
    if (slippageBps.isPresent()) {
      query.append("&slippageBps=").append(slippageBps.getAsInt());
    }
    final var referralAccount = referralAccount();
    if (referralAccount != null) {
      query.append("&referralAccount=").append(referralAccount.toBase58());
    }
    if (referralFee() > 0) {
      query.append("&referralFee=").append(referralFee());
    }
    final var payer = payer();
    if (payer != null) {
      query.append("&payer=").append(payer.toBase58());
    }
    final var priorityFeeLamports = priorityFeeLamports();
    if (priorityFeeLamports.isPresent()) {
      query.append("&priorityFeeLamports=").append(priorityFeeLamports.getAsLong());
    }
    final var jitoTipLamports = jitoTipLamports();
    if (jitoTipLamports.isPresent()) {
      query.append("&jitoTipLamports=").append(jitoTipLamports.getAsLong());
    }
    final var broadcastFeeType = broadcastFeeType();
    if (broadcastFeeType != null) {
      query.append("&broadcastFeeType=").append(broadcastFeeType.name());
    }
    SwapV2Query.appendLabels(query, "excludeRouters", excludeRouters());
    SwapV2Query.appendLabels(query, "excludeDexes", excludeDexes());
    return query.toString();
  }

  /// A mutable request; every setter returns this builder.
  interface Builder extends JupiterSwapOrderRequest {

    /// An immutable copy of this builder; missing required parameters are left to [#serialize()].
    ///
    /// @throws IllegalStateException if two parameters contradict each other, including a priority fee or Jito tip
    ///                               set without [#broadcastFeeType()]
    JupiterSwapOrderRequest createRequest();

    /// Sets [#inputMint()].
    Builder inputMint(final PublicKey inputMint);

    /// Sets [#outputMint()].
    Builder outputMint(final PublicKey outputMint);

    /// Sets [#amount()] from any long, read as an unsigned u64; 0 clears it.
    Builder amount(final long amount);

    /// Sets [#taker()].
    Builder taker(final PublicKey taker);

    /// Sets [#receiver()].
    Builder receiver(final PublicKey receiver);

    /// Sets [#swapMode()]; null clears it.
    Builder swapMode(final SwapMode swapMode);

    /// Sets [#slippageBps()], 0 included.
    ///
    /// @throws IllegalArgumentException if negative
    Builder slippageBps(final int slippageBps);

    /// Sets [#slippageBps()], 0 included; empty or null clears it, letting Jupiter choose.
    ///
    /// @throws IllegalArgumentException if the value is negative
    Builder slippageBps(final OptionalInt slippageBps);

    /// Sets [#referralAccount()].
    Builder referralAccount(final PublicKey referralAccount);

    /// Sets [#referralFee()]; 0 clears it, and 50-255 is the server's range.
    ///
    /// @throws IllegalArgumentException if negative
    Builder referralFee(final int referralFee);

    /// Sets [#payer()].
    Builder payer(final PublicKey payer);

    /// Sets [#priorityFeeLamports()], which Jupiter applies only with [#broadcastFeeType(BroadcastFeeType)], so
    /// [#createRequest()] and [#serialize()] reject a fee, 0 included, set without one; above 0 is the
    /// server's range, as Jupiter's Ultra manual-mode reference states (both observed 2026-10-04).
    ///
    /// @throws IllegalArgumentException if negative
    Builder priorityFeeLamports(final long priorityFeeLamports);

    /// Sets [#priorityFeeLamports()]; empty or null clears it, letting Jupiter choose, a present value needs
    /// [#broadcastFeeType(BroadcastFeeType)] as [#priorityFeeLamports(long)] describes, and above 0 is the
    /// server's range (Ultra manual-mode reference; observed 2026-10-04).
    ///
    /// @throws IllegalArgumentException if the value is negative
    Builder priorityFeeLamports(final OptionalLong priorityFeeLamports);

    /// Sets [#jitoTipLamports()], which Jupiter applies only with [#broadcastFeeType(BroadcastFeeType)], so
    /// [#createRequest()] and [#serialize()] reject a tip, 0 included, set without one; 1000 and above is the
    /// server's range, as Jupiter's Ultra manual-mode reference states (both observed 2026-10-04).
    ///
    /// @throws IllegalArgumentException if negative
    Builder jitoTipLamports(final long jitoTipLamports);

    /// Sets [#jitoTipLamports()]; empty or null clears it, letting Jupiter choose, a present value needs
    /// [#broadcastFeeType(BroadcastFeeType)] as [#jitoTipLamports(long)] describes, and 1000 and above is the
    /// server's range (Ultra manual-mode reference; observed 2026-10-04).
    ///
    /// @throws IllegalArgumentException if the value is negative
    Builder jitoTipLamports(final OptionalLong jitoTipLamports);

    /// Sets [#broadcastFeeType()]; null clears it, which [#createRequest()] and [#serialize()] reject while
    /// [#priorityFeeLamports()] or [#jitoTipLamports()] is set.
    Builder broadcastFeeType(final BroadcastFeeType broadcastFeeType);

    /// Copies the router names in order; null clears them.
    ///
    /// @throws IllegalArgumentException for a null or blank name, one with leading or trailing whitespace or
    ///                                  no-break space, or one containing a comma
    Builder excludeRouters(final Collection<String> excludeRouters);

    /// Copies the labels in order; null clears them.
    ///
    /// @throws IllegalArgumentException for a null or blank label, one with leading or trailing whitespace or
    ///                                  no-break space, or one containing a comma
    Builder excludeDexes(final Collection<String> excludeDexes);
  }
}

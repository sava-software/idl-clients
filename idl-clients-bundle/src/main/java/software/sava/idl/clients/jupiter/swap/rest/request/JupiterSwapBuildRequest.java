package software.sava.idl.clients.jupiter.swap.rest.request;

import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.JsonIterator;

import java.util.Collection;
import java.util.List;

/// The query of `GET /swap/v2/build`; accessors carry the API's parameter names, except [#fastMode()] for
/// `mode=fast`; a [Builder] is itself a request.
///
/// The request models the parameters of Jupiter's public API reference (swap.yaml); Jupiter's enterprise-only
/// `integratorFeeBps` and `positiveSlippageBps` (private integrator pages swap/fee-settlement and
/// swap/positive-slippage) are not modelled. Without whitelisting, `/build` ignored both (observed 2026-10-04).
public interface JupiterSwapBuildRequest {

  /// An empty builder: every optional parameter unset and `wrapAndUnwrapSol` true.
  static Builder buildRequest() {
    return new JupiterSwapBuildRequestRecord.BuilderImpl();
  }

  /// A builder holding `prototype`'s parameters; an empty builder when it is null.
  static Builder buildRequest(final JupiterSwapBuildRequest prototype) {
    return prototype == null ? buildRequest() : new JupiterSwapBuildRequestRecord.BuilderImpl(prototype);
  }

  /// Reads an object of API-named keys over `prototype`'s values through the builder's setters; required parameters may be absent, contradictions throw.
  ///
  /// Keys are the API's names, so fast mode is `"mode": "fast"`, not `fastMode`; unknown keys are skipped.
  ///
  /// `/build` is ExactIn only and takes no `swapMode`, but a key of that name, as a v1 quote or `/order` template
  /// carries, must be null or ExactIn, ignoring case. Any other string throws IllegalArgumentException, because an
  /// ExactOut amount is the output amount and `/build` would sell it.
  ///
  /// A JSON null unsets a parameter, except the ones 0 or a default unsets, which throw on it: the numbers `amount`,
  /// `platformFeeBps`, `maxAccounts`, `blockhashSlotsToExpiry` and `tipAmount`, and the booleans `wrapAndUnwrapSol`
  /// and `forJitoBundle`.
  static JupiterSwapBuildRequest parseRequest(final JupiterSwapBuildRequest prototype, final JsonIterator ji) {
    return ji.parseObject(JupiterSwapBuildRequestRecord.Parser.FIELDS, new JupiterSwapBuildRequestRecord.Parser(buildRequest(prototype)));
  }

  /// [#parseRequest(JupiterSwapBuildRequest, JsonIterator)] without a prototype.
  static JupiterSwapBuildRequest parseRequest(final JsonIterator ji) {
    return parseRequest(null, ji);
  }

  /// The mint sold; required.
  PublicKey inputMint();

  /// The mint bought; required.
  PublicKey outputMint();

  /// The input amount in base units, a u64 held in a long and sent unsigned; 0 means unset, and a sent request must set it.
  long amount();

  /// The wallet that signs the swap instruction; required.
  PublicKey taker();

  /// The `slippageBps` wire value: decimal basis points (`"0"` included), `"rtse"`, or null to send nothing (Jupiter's default is 50).
  String slippageBps();

  /// Whether `mode=fast` is sent: faster quoting at some cost to quote optimality (and a 90th-percentile default CU price).
  boolean fastMode();

  /// Case-sensitive DEX labels routing is restricted to, from [software.sava.idl.clients.jupiter.swap.rest.JupiterSwapV2Client#programIdToLabel()]; empty when unset, never null.
  ///
  /// An unknown or miscased label is not a validation error, so check labels against
  /// [software.sava.idl.clients.jupiter.swap.rest.JupiterSwapV2Client#programIdToLabel()]. Observed on 2026-10-04: a
  /// list in which no label is known answers 400 "No routes found", the same answer as a pair with no liquidity, and
  /// an unknown label beside a known one is ignored.
  List<String> dexes();

  /// Case-sensitive DEX labels routing avoids; empty when unset, never null.
  ///
  /// An unknown or miscased label is not a validation error, so check labels against
  /// [software.sava.idl.clients.jupiter.swap.rest.JupiterSwapV2Client#programIdToLabel()]. Observed on 2026-10-04: such
  /// a label is ignored and the call succeeds, so a misspelled exclusion excludes nothing.
  List<String> excludeDexes();

  /// The integrator fee in basis points; 0 means none, and a positive fee needs [#feeAccount()].
  int platformFeeBps();

  /// The token account that collects the platform fee; null when unset.
  PublicKey feeAccount();

  /// The most accounts the route may use; 0 means unset (Jupiter's default, 64). It bounds the route, not the transaction.
  int maxAccounts();

  /// The account that pays fees and rent instead of the taker; null when unset.
  ///
  /// The payer does not get its wSOL rent back. When SOL is wrapped (one side is SOL and [#wrapAndUnwrapSol()] is
  /// true), the payer funds the temporary wSOL account, and
  /// [software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapBuild#cleanupInstruction()] closes it to the
  /// taker with no transfer back (observed 2026-10-04). Jupiter documents that `/order` returns this rent to the
  /// payer; on `/build`, add your own transfer from the taker to the payer if it should.
  PublicKey payer();

  /// Whether Jupiter wraps and unwraps SOL; only `false` is ever sent, since true is the API's default.
  boolean wrapAndUnwrapSol();

  /// The SPL token account that receives the output; null when unset.
  PublicKey destinationTokenAccount();

  /// The native SOL account that receives the output; null when unset.
  PublicKey nativeDestinationAccount();

  /// Slots until the returned blockhash expires; 0 means unset (Jupiter's default, 150).
  int blockhashSlotsToExpiry();

  /// A SOL tip in lamports, which adds a `tipInstruction` for Jupiter's `/submit`; 0 means none, and Jupiter answers
  /// 400 below 1,000,000 (0.001 SOL, `/submit`'s minimum; observed 2026-10-04, not in swap.yaml).
  long tipAmount();

  /// The `computeUnitPricePercentile` wire value: a [ComputeUnitPriceLevel] name, decimal basis points, or null to send nothing.
  String computeUnitPricePercentile();

  /// Whether `forJitoBundle=true` is sent, excluding DEXes incompatible with Jito bundles.
  boolean forJitoBundle();

  /// The query without its leading `?`, parameters in the API reference's order, optional ones only when set.
  ///
  /// @throws IllegalStateException if a required parameter is missing or two parameters contradict each other
  default String serialize() {
    JupiterSwapBuildRequestRecord.requireComplete(this);
    final var query = new StringBuilder();
    query.append("inputMint=").append(inputMint().toBase58());
    query.append("&outputMint=").append(outputMint().toBase58());
    query.append("&amount=").append(Long.toUnsignedString(amount()));
    query.append("&taker=").append(taker().toBase58());
    final var slippageBps = slippageBps();
    if (slippageBps != null) {
      query.append("&slippageBps=").append(slippageBps);
    }
    if (fastMode()) {
      query.append("&mode=fast");
    }
    SwapV2Query.appendLabels(query, "dexes", dexes());
    SwapV2Query.appendLabels(query, "excludeDexes", excludeDexes());
    if (platformFeeBps() > 0) {
      query.append("&platformFeeBps=").append(platformFeeBps());
    }
    final var feeAccount = feeAccount();
    if (feeAccount != null) {
      query.append("&feeAccount=").append(feeAccount.toBase58());
    }
    if (maxAccounts() > 0) {
      query.append("&maxAccounts=").append(maxAccounts());
    }
    final var payer = payer();
    if (payer != null) {
      query.append("&payer=").append(payer.toBase58());
    }
    if (!wrapAndUnwrapSol()) {
      query.append("&wrapAndUnwrapSol=false");
    }
    final var destinationTokenAccount = destinationTokenAccount();
    if (destinationTokenAccount != null) {
      query.append("&destinationTokenAccount=").append(destinationTokenAccount.toBase58());
    }
    final var nativeDestinationAccount = nativeDestinationAccount();
    if (nativeDestinationAccount != null) {
      query.append("&nativeDestinationAccount=").append(nativeDestinationAccount.toBase58());
    }
    if (blockhashSlotsToExpiry() > 0) {
      query.append("&blockhashSlotsToExpiry=").append(blockhashSlotsToExpiry());
    }
    if (tipAmount() > 0) {
      query.append("&tipAmount=").append(tipAmount());
    }
    final var computeUnitPricePercentile = computeUnitPricePercentile();
    if (computeUnitPricePercentile != null) {
      query.append("&computeUnitPricePercentile=").append(computeUnitPricePercentile);
    }
    if (forJitoBundle()) {
      query.append("&forJitoBundle=true");
    }
    return query.toString();
  }

  /// A mutable request; every setter returns this builder.
  interface Builder extends JupiterSwapBuildRequest {

    /// An immutable copy of this builder; missing required parameters are left to [#serialize()].
    ///
    /// @throws IllegalStateException if two parameters contradict each other
    JupiterSwapBuildRequest createRequest();

    /// Sets [#inputMint()].
    Builder inputMint(final PublicKey inputMint);

    /// Sets [#outputMint()].
    Builder outputMint(final PublicKey outputMint);

    /// Sets [#amount()] from any long, read as an unsigned u64; 0 clears it.
    Builder amount(final long amount);

    /// Sets [#taker()].
    Builder taker(final PublicKey taker);

    /// A fixed tolerance in basis points, 0 included; replaces [#rtseSlippage()].
    ///
    /// @throws IllegalArgumentException if negative
    Builder slippageBps(final int slippageBps);

    /// Sends `slippageBps=rtse`, Jupiter's Real-Time Slippage Estimator; replaces a fixed tolerance.
    Builder rtseSlippage();

    /// Sends no `slippageBps`, leaving Jupiter's default of 50; replaces a fixed tolerance or [#rtseSlippage()].
    Builder defaultSlippage();

    /// Sets [#fastMode()].
    Builder fastMode(final boolean fastMode);

    /// Copies the labels in order; null clears them.
    ///
    /// @throws IllegalArgumentException for a null or blank label, one with leading or trailing whitespace or
    ///                                  no-break space, or one containing a comma
    Builder dexes(final Collection<String> dexes);

    /// Copies the labels in order; null clears them.
    ///
    /// @throws IllegalArgumentException for a null or blank label, one with leading or trailing whitespace or
    ///                                  no-break space, or one containing a comma
    Builder excludeDexes(final Collection<String> excludeDexes);

    /// Sets [#platformFeeBps()]; 0 clears it.
    ///
    /// @throws IllegalArgumentException if negative
    Builder platformFeeBps(final int platformFeeBps);

    /// Sets [#feeAccount()].
    Builder feeAccount(final PublicKey feeAccount);

    /// Sets [#maxAccounts()]; 0 clears it, and no upper bound is enforced.
    ///
    /// @throws IllegalArgumentException if negative
    Builder maxAccounts(final int maxAccounts);

    /// Sets [#payer()].
    Builder payer(final PublicKey payer);

    /// Sets [#wrapAndUnwrapSol()].
    Builder wrapAndUnwrapSol(final boolean wrapAndUnwrapSol);

    /// Sets [#destinationTokenAccount()].
    Builder destinationTokenAccount(final PublicKey destinationTokenAccount);

    /// Sets [#nativeDestinationAccount()].
    Builder nativeDestinationAccount(final PublicKey nativeDestinationAccount);

    /// Sets [#blockhashSlotsToExpiry()]; 0 clears it.
    ///
    /// @throws IllegalArgumentException if negative
    Builder blockhashSlotsToExpiry(final int blockhashSlotsToExpiry);

    /// Sets [#tipAmount()]; 0 clears it, and 1,000,000 is the server's minimum (observed 2026-10-04).
    ///
    /// @throws IllegalArgumentException if negative
    Builder tipAmount(final long tipAmount);

    /// A named percentile; replaces a basis-point value; null clears it.
    Builder computeUnitPricePercentile(final ComputeUnitPriceLevel level);

    /// A percentile in basis points, 0 included; replaces a named level.
    ///
    /// @throws IllegalArgumentException if negative
    Builder computeUnitPricePercentileBps(final int bps);

    /// Sets [#forJitoBundle()].
    Builder forJitoBundle(final boolean forJitoBundle);
  }
}

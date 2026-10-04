package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.encoding.ByteUtil;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.swap.RouteV2Data;
import software.sava.idl.clients.spl.compute_budget.gen.ComputeBudgetProgram.SetComputeUnitPriceIxData;
import systems.comodal.jsoniter.FieldIndexPredicate;
import systems.comodal.jsoniter.FieldMatcher;
import systems.comodal.jsoniter.JsonIterator;
import systems.comodal.jsoniter.ValueType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNullElse;
import static software.sava.idl.clients.spl.compute_budget.gen.ComputeBudgetProgram.SET_COMPUTE_UNIT_PRICE_DISCRIMINATOR;

/// A `GET /swap/v2/build` response: an ExactIn Metis quote and the instructions to assemble it, which you sign and send yourself.
///
/// For a v0 transaction, do not sign [#instructions()] as it stands: Jupiter sends the compute unit
/// price, an uncapped estimate, but not the compute unit limit. Put your own SetComputeUnitLimit
/// and a capped SetComputeUnitPrice in front of [#instructionsWithoutComputeBudget()] instead, and
/// compile with the lookup tables in [#addressesByLookupTableAddress()]. A SIMD-0385 v1
/// transaction has no lookup tables and carries its compute unit limit and priority fee as
/// ConfigValues, ignoring ComputeBudget instructions (ravina rejects them), so what a v1 caller
/// takes from this response is:
///
/// ```java
/// final var build = client.buildSwap(JupiterSwapBuildRequest.buildRequest()
///     .inputMint(in).outputMint(out).amount(amount).taker(feePayer).slippageBps(30).createRequest()).join();
/// final var data = build.readSwapInstructionData();          // data.inAmount() == amount
/// final var ixs = build.instructionsWithoutComputeBudget(mine);
/// if (build.distinctAccountCount(feePayer, mine) > Transaction.MAX_ACCOUNTS) {
///   // re-request with a lower maxAccounts, sized from the route as distinctAccountCount describes
/// }
/// final long price = Math.min(build.computeUnitPriceMicroLamports().orElse(estimate), cap);
/// ```
///
/// For either version, size the compute unit limit by simulation, as Jupiter's guide does: simulate
/// with 1,400,000, then set 1.2x the units consumed, capped at 1,400,000 (`swap/build/index.mdx`,
/// "Simulate compute unit limit"). A v0 priority fee is charged on the limit requested, not on the
/// units the swap consumes, so a limit left at 1,400,000 pays for 1,400,000 compute units. A v1
/// priority fee is a fixed lamport amount that a later limit change does not move.
/// [#computeUnitPriceMicroLamports()] shows how each version sets the capped `price` on that
/// limit. A ravina caller hands `ixs` to `InstructionService#processInstructions` instead, which
/// simulates at 1,400,000, sizes the limit itself and prices from its own estimate rather than from
/// `price`. Pass it a `cuBudgetMultiplier` of 1.2 to keep Jupiter's buffer: its overloads without
/// one use 1.0, exactly the units consumed, which Jupiter warns is too tight.
///
/// @param inputMint                     null when absent
/// @param outputMint                    null when absent
/// @param inAmount                      the input amount, a u64 read unsigned
/// @param outAmount                     the quoted output, a u64 read unsigned
/// @param otherAmountThreshold          the minimum output after slippage, a u64 read unsigned
/// @param swapMode                      `ExactIn`, as sent; null when absent
/// @param slippageBps                   the slippage in basis points, the `u16` the `route_v2` instruction carries (see [#readSwapInstructionData()]); 0 when absent
/// @param priceImpactPct                price impact as a decimal ratio ("0.001" = 0.1%); null when absent
/// @param routePlan                     the route steps; never null
/// @param computeBudgetInstructions     documented to hold only a SetComputeUnitPrice; never null
/// @param setupInstructions             pre-swap setup such as ATA creation; never null
/// @param swapInstruction               the route instruction; never null
/// @param cleanupInstruction            null when absent
/// @param otherInstructions             never null
/// @param tipInstruction                present only when `tipAmount` was requested; null otherwise
/// @param addressesByLookupTableAddress lookup table to its addresses, in response order, unmodifiable and copied on construction (address lists too); empty when Jupiter sends null; a v1 transaction uses none
/// @param blockhashWithMetadata         null when absent
public record JupiterSwapBuild(PublicKey inputMint,
                               PublicKey outputMint,
                               long inAmount,
                               long outAmount,
                               long otherAmountThreshold,
                               String swapMode,
                               int slippageBps,
                               BigDecimal priceImpactPct,
                               List<JupiterSwapRouteStep> routePlan,
                               List<Instruction> computeBudgetInstructions,
                               List<Instruction> setupInstructions,
                               Instruction swapInstruction,
                               Instruction cleanupInstruction,
                               List<Instruction> otherInstructions,
                               Instruction tipInstruction,
                               Map<PublicKey, List<PublicKey>> addressesByLookupTableAddress,
                               BlockhashWithMetadata blockhashWithMetadata) {

  private static final PublicKey COMPUTE_BUDGET_PROGRAM = SolanaAccounts.MAIN_NET.computeBudgetProgram();

  /// Requires a swap instruction, turns absent lists and an absent lookup-table map into empty unmodifiable ones, and
  /// copies every list and the map, so changing what was passed in never changes the record.
  ///
  /// @throws NullPointerException if `swapInstruction` is null, a list or an address list holds a null, or the map has a null table address or a null address list
  public JupiterSwapBuild {
    Objects.requireNonNull(swapInstruction, "/build response has no swapInstruction");
    routePlan = List.copyOf(requireNonNullElse(routePlan, List.of()));
    computeBudgetInstructions = List.copyOf(requireNonNullElse(computeBudgetInstructions, List.of()));
    setupInstructions = List.copyOf(requireNonNullElse(setupInstructions, List.of()));
    otherInstructions = List.copyOf(requireNonNullElse(otherInstructions, List.of()));
    addressesByLookupTableAddress = copyLookupTables(addressesByLookupTableAddress);
  }

  /// `Map.of()` for null; otherwise an unmodifiable `LinkedHashMap` in the given map's iteration order, each address
  /// list through `List.copyOf`, so a null table address, a null address list, or a null address inside one throws
  /// NPE, as `Map.copyOf` would. `Map.copyOf` is not used because it drops the order. There is no `isEmpty()`
  /// shortcut: an empty map copies to an empty map, and a shortcut would be a mutant no assertion can tell apart.
  private static Map<PublicKey, List<PublicKey>> copyLookupTables(final Map<PublicKey, List<PublicKey>> tables) {
    if (tables == null) {
      return Map.of();
    }
    final var copy = new LinkedHashMap<PublicKey, List<PublicKey>>();
    for (final var entry : tables.entrySet()) {
      copy.put(Objects.requireNonNull(entry.getKey(), "lookup table address"), List.copyOf(entry.getValue()));
    }
    return Collections.unmodifiableMap(copy);
  }

  /// Parses a `/build` body; unknown fields are skipped at every level.
  ///
  /// The amounts are u64 strings read unsigned, `""` and JSON null reading as 0. `slippageBps` and
  /// the blockhash numbers must be integral (`50.0` is accepted); route percentages, basis points
  /// and USD values are kept as sent.
  ///
  /// @throws NullPointerException     if the body has no `swapInstruction`, an instruction lacks its `programId` or
  ///                                  `data` or an account its `pubkey`, or a lookup-table address list holds a
  ///                                  JSON null
  /// @throws IllegalStateException    if an instruction has no `accounts` key (a JSON-null list reads as empty), or
  ///                                  a `blockhash` array has other than 32 elements or an element that is JSON
  ///                                  null, `""` or an int outside 0..255
  /// @throws ArithmeticException      if `slippageBps` or a `blockhash` element is fractional or beyond an int, or
  ///                                  another blockhash number is fractional or beyond a long
  /// @throws NumberFormatException    if an amount has a minus sign, a decimal point or an exponent, exceeds 2^64-1 or
  ///                                  is not a number, or another number is one `BigDecimal` cannot parse
  /// @throws IllegalArgumentException if a key is not a 32-byte base58 key (an empty mint reads as null), or an
  ///                                  instruction's `data` is not base64
  /// @throws systems.comodal.jsoniter.JsonException if a value has the wrong JSON type, such as a string
  ///                                                `blockhash` or `routePlan`
  public static JupiterSwapBuild parse(final JsonIterator ji) {
    return ji.parseObject(Parser.FIELDS, new Parser());
  }

  /// Every instruction exactly as Jupiter sent it, in its assembly order: compute budget, setup, swap, cleanup when present, other, tip when present.
  ///
  /// This is not a complete v0 instruction list: it carries Jupiter's SetComputeUnitPrice uncapped
  /// and, as documented, no SetComputeUnitLimit (see the class documentation). Each call returns a
  /// new unmodifiable list.
  public List<Instruction> instructions() {
    final var instructions = new ArrayList<Instruction>();
    instructions.addAll(computeBudgetInstructions);
    instructions.addAll(setupInstructions);
    instructions.add(swapInstruction);
    if (cleanupInstruction != null) {
      instructions.add(cleanupInstruction);
    }
    instructions.addAll(otherInstructions);
    if (tipInstruction != null) {
      instructions.add(tipInstruction);
    }
    return Collections.unmodifiableList(instructions);
  }

  /// [#instructions()] without any instruction that invokes the ComputeBudget program, wherever Jupiter put it: what a SIMD-0385 v1 transaction carries, and the base of a v0 transaction that sets its own compute budget.
  ///
  /// SIMD-0385 v1 transactions carry the compute unit limit and priority fee as ConfigValues and
  /// ignore ComputeBudget instructions, so a v1 transaction bids 0 unless you set a priority fee
  /// yourself; set it after the compute unit limit, as [#computeUnitPriceMicroLamports()] describes.
  /// If Jupiter ever returns RequestHeapFrame or SetLoadedAccountsDataSizeLimit in any slot, it is
  /// dropped too, and the matching v1 ConfigValue is yours to set: look for it among the
  /// ComputeBudget instructions of [#instructions()], not only in [#computeBudgetInstructions()],
  /// when that matters.
  ///
  /// Instructions are filtered by program id, so an instruction of another program that Jupiter
  /// put in [#computeBudgetInstructions()] is kept, first, and the ComputeBudget program appearing
  /// only as an account of another instruction is kept too. Relative order is kept, and each call
  /// returns a new unmodifiable list.
  public List<Instruction> instructionsWithoutComputeBudget() {
    return instructionsWithoutComputeBudget(List.of());
  }

  /// [#instructionsWithoutComputeBudget()] with `afterSwap` placed right after the swap and before cleanup, as Jupiter's own example does; `afterSwap` is not filtered.
  ///
  /// This is where Jupiter's example puts a custom instruction (`swap/build/index.mdx:310-321`), and
  /// the position you need to use the swap's wSOL output before cleanup closes the account. It is
  /// not the only interior position: Jupiter's ordering guide puts your pre-swap instructions after
  /// the setup instructions (`swap/build/common-instructions.mdx:9-18`), which prepending does not
  /// reach, since it lands before them. Insert into a copy of the list for that or any other
  /// interior position.
  ///
  /// Your own ComputeBudget instructions are kept. A v1 transaction ignores their values, though
  /// they still use compute units and an account slot; ravina rejects them with
  /// `IllegalArgumentException` and `TxBuilder` accepts them, so set the limit and fee as
  /// [#computeUnitPriceMicroLamports()] describes. An empty `afterSwap` gives the same list as
  /// [#instructionsWithoutComputeBudget()].
  ///
  /// @throws NullPointerException if `afterSwap` is null or holds a null
  public List<Instruction> instructionsWithoutComputeBudget(final Collection<Instruction> afterSwap) {
    final var instructions = new ArrayList<Instruction>();
    addUnlessComputeBudget(instructions, computeBudgetInstructions);
    addUnlessComputeBudget(instructions, setupInstructions);
    addUnlessComputeBudget(instructions, swapInstruction);
    instructions.addAll(List.copyOf(afterSwap));
    if (cleanupInstruction != null) {
      addUnlessComputeBudget(instructions, cleanupInstruction);
    }
    addUnlessComputeBudget(instructions, otherInstructions);
    if (tipInstruction != null) {
      addUnlessComputeBudget(instructions, tipInstruction);
    }
    return Collections.unmodifiableList(instructions);
  }

  private static boolean invokesComputeBudget(final Instruction ix) {
    return COMPUTE_BUDGET_PROGRAM.equals(ix.programId().publicKey());
  }

  private static void addUnlessComputeBudget(final List<Instruction> to, final Instruction ix) {
    if (!invokesComputeBudget(ix)) {
      to.add(ix);
    }
  }

  private static void addUnlessComputeBudget(final List<Instruction> to, final List<Instruction> instructions) {
    for (final var ix : instructions) {
      addUnlessComputeBudget(to, ix);
    }
  }

  /// The SetComputeUnitPrice price in micro-lamports per compute unit, never negative; empty when there is none. **Cap it before signing.**
  ///
  /// Jupiter estimates this price from recent priority fees, and the estimate can spike far above
  /// what lands a transaction (Jupiter: "Cap the compute unit price"; 82 billion micro-lamports has
  /// been observed). A low `computeUnitPricePercentile` is not a cap. Take
  /// `long price = Math.min(build.computeUnitPriceMicroLamports().orElse(myEstimate), myCap);` and
  /// apply it to a compute unit limit sized by simulation, as the class documentation describes.
  /// For a v1 transaction, call `TxBuilder#computeUnitLimit(int)` before
  /// `TxBuilder#priorityFeeLamportsFromComputeUnitPrice(price)`: the price is converted once,
  /// against the limit set at that moment (1,400,000 by default), so a price set first is charged
  /// for 1,400,000 compute units whatever limit follows, `Transaction#setComputeUnitLimit(int)`
  /// after simulation included. To tighten a transaction built with a non-zero priority fee,
  /// `Transaction#setPriorityFeeLamportsFromComputeUnitPrice(price, limit)` sets both in place.
  /// Jupiter does return a price of 0 (observed on 2026-10-04 for `ComputeUnitPriceLevel.medium`).
  /// It converts to a fee of 0, for which `TxBuilder` writes no priority-fee ConfigValue, so that
  /// overload then throws `IllegalStateException` and leaves the transaction unchanged. Tighten such
  /// a transaction with `Transaction#setComputeUnitLimit(int)` alone, since a price of 0 bids 0 at
  /// any limit, or rebuild it with `TxBuilder` to bid more. To keep the overload usable at any price,
  /// reserve the slot as ravina does: build with `TxBuilder#priorityFeeLamports(long)` set to 1, then
  /// call `Transaction#setPriorityFeeLamports(0)`.
  /// For a v0 transaction, put
  /// `ComputeBudgetProgram.setComputeUnitLimit(SolanaAccounts.MAIN_NET.invokedComputeBudgetProgram(), limit)`
  /// and `ComputeBudgetProgram.setComputeUnitPrice(SolanaAccounts.MAIN_NET.invokedComputeBudgetProgram(), price)`
  /// in front of [#instructionsWithoutComputeBudget()] instead of signing Jupiter's instruction.
  /// Callers with their own fee estimate can ignore this value. Catch `IllegalStateException` to
  /// fall back to your own estimate when Jupiter's instruction is malformed.
  ///
  /// The price is read from the instruction that invokes the ComputeBudget program with
  /// discriminator 3, wherever Jupiter put it. ComputeBudget instructions of another kind, such as
  /// SetComputeUnitLimit, are not prices, and neither is discriminator 3 under another program.
  /// Because the price is never negative, `Math.min(price, cap)` cannot select a negative bid,
  /// which `TxBuilder` would read as the maximum fee.
  ///
  /// @throws IllegalStateException if a SetComputeUnitPrice is not 9 bytes, appears more than once, or carries a price at or above 2^63
  public OptionalLong computeUnitPriceMicroLamports() {
    boolean found = false;
    long price = 0;
    for (final var ix : instructions()) {
      if (invokesComputeBudget(ix) && SET_COMPUTE_UNIT_PRICE_DISCRIMINATOR.test(ix)) {
        if (ix.len() != SetComputeUnitPriceIxData.BYTES) {
          throw new IllegalStateException("SetComputeUnitPrice data must be 9 bytes, was " + ix.len());
        }
        if (found) {
          throw new IllegalStateException(
              "more than one SetComputeUnitPrice; the runtime rejects duplicate compute budget instructions");
        }
        price = ByteUtil.getInt64LE(ix.data(), ix.offset() + SetComputeUnitPriceIxData.MICRO_LAMPORTS_OFFSET);
        if (price < 0) {
          throw new IllegalStateException(
              "SetComputeUnitPrice above Long.MAX_VALUE micro-lamports: " + Long.toUnsignedString(price));
        }
        found = true;
      }
    }
    return found ? OptionalLong.of(price) : OptionalLong.empty();
  }

  /// The distinct keys a v1 transaction of [#instructionsWithoutComputeBudget()] paid by `feePayer` declares: fee payer, every program id, every account.
  ///
  /// When the count exceeds `Transaction.MAX_ACCOUNTS`, request again with
  /// `JupiterSwapBuildRequest.buildRequest(previous).maxAccounts(lower).createRequest()`.
  /// `maxAccounts` bounds only the route's inner-swap accounts, about
  /// `swapInstruction().accounts().size()` less the route instruction's own 10 (12 for
  /// `shared_accounts_route_v2`, one more when [#readSwapInstructionData()]'s `platformFeeBps()` is
  /// not 0, for the fee account Jupiter passes ahead of the first step) and one program id per
  /// [#routePlan()] step, and a bound at or above that count cannot exclude the route you have. So set
  /// `lower` to the smaller of that count and `previous.maxAccounts()` (64, Jupiter's default, when it
  /// is 0 because it was never set), less the excess, and stop at a floor: Jupiter warns that values
  /// well below 64 degrade routing. Each re-request spends the main rate-limit bucket (0.5 requests per
  /// second without a key). A v1 transaction is also limited to 4,096 bytes, 64 instructions and 12
  /// signatures (ravina and a strict `TxBuilder` enforce all three), which this count does not check.
  ///
  /// There is no overload without the fee payer: a count without it is one low exactly when the
  /// fee payer is not already an account of an instruction.
  ///
  /// @throws NullPointerException if `feePayer` is null
  public int distinctAccountCount(final PublicKey feePayer) {
    return distinctAccountCount(feePayer, List.of());
  }

  /// [#distinctAccountCount(PublicKey)] with your own instructions folded in, counted as given: the number to compare with `Transaction.MAX_ACCOUNTS`.
  ///
  /// Your instructions are counted as given, ComputeBudget ones included; a key they share with
  /// Jupiter's instructions counts once.
  ///
  /// @throws NullPointerException if `feePayer` or `additionalInstructions` is null, or
  ///                              `additionalInstructions` holds a null
  public int distinctAccountCount(final PublicKey feePayer, final Collection<Instruction> additionalInstructions) {
    Objects.requireNonNull(feePayer, "feePayer");
    final var keys = new HashSet<PublicKey>();
    keys.add(feePayer);
    addKeys(keys, instructionsWithoutComputeBudget());
    addKeys(keys, additionalInstructions);
    return keys.size();
  }

  private static void addKeys(final Set<PublicKey> keys, final Collection<Instruction> instructions) {
    for (final var ix : instructions) {
      keys.add(ix.programId().publicKey());
      for (final var account : ix.accounts()) {
        keys.add(account.publicKey());
      }
    }
  }

  /// Decodes the swap instruction's `route_v2`/`shared_accounts_route_v2` data, to check it against the quote before signing.
  ///
  /// Compare `inAmount()` with the request's `amount()`, and `inAmount()`, `quotedOutAmount()` and
  /// `slippageBps()` with this response's `inAmount()`, `outAmount()` and `slippageBps()`, before
  /// signing. The amounts are u64 values held in a long and `slippageBps()` is the `u16` read
  /// unsigned into an int, the same representations `JupiterSwapBuild` uses, so `==` works.
  /// Comparing with this response only checks Jupiter against itself, so also hold `slippageBps()`
  /// to what you asked for. The request's `slippageBps()` is the wire string: compare with
  /// `Integer.parseInt(request.slippageBps())` when it is a number and with 50, Jupiter's default,
  /// when it is null; `"rtse"` lets Jupiter choose, so bound it by your own maximum.
  ///
  /// @throws UnsupportedOperationException for any other route instruction, data shorter than a discriminator
  ///                                       included (from [RouteV2Data#readData(byte\[\], int)])
  /// @throws IndexOutOfBoundsException     if the data ends inside the `route_v2`/`shared_accounts_route_v2`
  ///                                       layout or inside a route step, or the route plan's length prefix
  ///                                       exceeds the bytes after it
  /// @throws NullPointerException          if a route step names a variant this release's generated Jupiter
  ///                                       types lack, as when Jupiter routes through an AMM added to its
  ///                                       program after this release; the amounts and slippage, which precede
  ///                                       the route plan, can still be read at the `*_OFFSET` constants of
  ///                                       `JupiterProgram.RouteV2IxData` and `SharedAccountsRouteV2IxData`
  /// @throws IllegalArgumentException      if a route step carries an option tag other than 0 or 1
  public RouteV2Data readSwapInstructionData() {
    return RouteV2Data.readData(swapInstruction.data(), swapInstruction.offset());
  }

  private static final class Parser implements FieldIndexPredicate, Supplier<JupiterSwapBuild> {

    private static final FieldMatcher FIELDS = FieldMatcher.of(
        "inputMint",
        "outputMint",
        "inAmount",
        "outAmount",
        "otherAmountThreshold",
        "swapMode",
        "slippageBps",
        "priceImpactPct",
        "routePlan",
        "computeBudgetInstructions",
        "setupInstructions",
        "swapInstruction",
        "cleanupInstruction",
        "otherInstructions",
        "tipInstruction",
        "addressesByLookupTableAddress",
        "blockhashWithMetadata"
    );

    private PublicKey inputMint;
    private PublicKey outputMint;
    private long inAmount;
    private long outAmount;
    private long otherAmountThreshold;
    private String swapMode;
    private int slippageBps;
    private BigDecimal priceImpactPct;
    private List<JupiterSwapRouteStep> routePlan;
    private List<Instruction> computeBudgetInstructions;
    private List<Instruction> setupInstructions;
    private Instruction swapInstruction;
    private Instruction cleanupInstruction;
    private List<Instruction> otherInstructions;
    private Instruction tipInstruction;
    private Map<PublicKey, List<PublicKey>> addressesByLookupTableAddress;
    private BlockhashWithMetadata blockhashWithMetadata;

    private Parser() {
    }

    @Override
    public JupiterSwapBuild get() {
      return new JupiterSwapBuild(
          inputMint, outputMint, inAmount, outAmount, otherAmountThreshold, swapMode, slippageBps, priceImpactPct,
          routePlan, computeBudgetInstructions, setupInstructions, swapInstruction, cleanupInstruction,
          otherInstructions, tipInstruction, addressesByLookupTableAddress, blockhashWithMetadata
      );
    }

    @Override
    public boolean test(final int fieldIndex, final JsonIterator ji) {
      switch (fieldIndex) {
        case 0 -> inputMint = SwapV2Json.readOptionalKey(ji);
        case 1 -> outputMint = SwapV2Json.readOptionalKey(ji);
        case 2 -> inAmount = SwapV2Json.readU64(ji);
        case 3 -> outAmount = SwapV2Json.readU64(ji);
        case 4 -> otherAmountThreshold = SwapV2Json.readU64(ji);
        case 5 -> swapMode = ji.readString();
        case 6 -> slippageBps = SwapV2Json.readIntegralInt(ji);
        case 7 -> priceImpactPct = ji.readBigDecimal();
        case 8 -> routePlan = ji.readList(JupiterSwapRouteStep::parse);
        case 9 -> computeBudgetInstructions = SwapV2Json.readInstructions(ji);
        case 10 -> setupInstructions = SwapV2Json.readInstructions(ji);
        case 11 -> swapInstruction = SwapV2Json.readOptionalInstruction(ji);
        case 12 -> cleanupInstruction = SwapV2Json.readOptionalInstruction(ji);
        case 13 -> otherInstructions = SwapV2Json.readInstructions(ji);
        case 14 -> tipInstruction = SwapV2Json.readOptionalInstruction(ji);
        case 15 -> addressesByLookupTableAddress = SwapV2Json.readLookupTables(ji);
        case 16 -> blockhashWithMetadata = ji.readOrNull(ValueType.OBJECT, BlockhashWithMetadata::parse);
        default -> ji.skip();
      }
      return true;
    }
  }
}

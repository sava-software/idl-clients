package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import software.sava.core.tx.Instruction;
import software.sava.core.tx.TxBuilder;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterQuoteRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapBuildRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapExecuteRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterSwapOrderRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterUltraOrderRequest;
import software.sava.idl.clients.jupiter.voter.rest.response.ClaimAsrProof;
import software.sava.idl.clients.spl.compute_budget.gen.ComputeBudgetProgram.SetComputeUnitPriceIxData;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

/// Jazzer entry point for the Jupiter REST parsers — the only surface in this
/// module that parses bytes arriving over the network rather than on-chain
/// account data. The first byte selects a parser; the rest is fed to it as the
/// response body.
///
/// Contract: any body either parses or is rejected with a `RuntimeException`
/// — a `StackOverflowError`, `OutOfMemoryError`, or any other
/// non-`RuntimeException` throwable is a finding (the underlying json-iterator
/// is fuzzed in its own repo; what this harness exercises is this module's
/// field predicates, rewind-and-retry lookups, fixed-width proof reads, and
/// base64-bearing fields).
///
/// The selector is the first byte, unsigned, modulo 14:
/// `(data[0] & 0xFF) % 14`. It was `% 10` before the Swap API V2 parsers
/// joined; the seeds `00`–`09` lead with `0x00`–`0x09`, so they keep their
/// parsers:
///
/// - 0: a `/swap/v1/quote` response, `JupiterQuote.parse`
/// - 1: a `/swap/v1/swap-instructions` response, `JupiterSwapInstructions.parseInstructions`
/// - 2: `JupiterSwapInstructions.parseSwapInstruction`
/// - 3: `JupiterSwapInstructions.parseLookupTables`
/// - 4: `JupiterSwapIx.parse`
/// - 5: `JupiterSwapTx.parse`
/// - 6: an Ultra order, `JupiterUltraOrder.parse`
/// - 7: `JupiterPrice.parsePrices` and `JupiterTokenV2.parseTokens`
/// - 8: `ClaimAsrProof.parseProof`
/// - 9: `JupiterQuoteRequest.parseRequest` and `JupiterUltraOrderRequest.parseRequest`
/// - 10: a `/swap/v2/build` response, `JupiterSwapBuild.parse`
/// - 11: a `/swap/v2/order` response, `JupiterSwapOrder.parse`
/// - 12: `JupiterSwapBuildRequest.parseRequest` and `JupiterSwapOrderRequest.parseRequest`
/// - 13: the body as an `/execute` requestId for `JupiterSwapExecuteRequest.toJson`, then
///   as an `/execute` result for `JupiterExecuteOrder.parse`
///
/// Crash-only fuzzing cannot see a wrong answer, so after a successful parse
/// the harness re-checks what callers depend on:
///
/// - 0: the same bytes parse again to the same amounts.
/// - 1: `numInstructions()` must size an array that `mergeAllAccounts` fills
///   exactly — every slot written, none left null — regardless of which
///   combination of lists/cleanup the body carried.
/// - 10: the same bytes parse again to the same amounts, slippage and
///   instruction count. `instructions()` has one entry per instruction the body
///   carried, with the swap at index `computeBudget + setup`.
///   `instructionsWithoutComputeBudget()` equals a plain loop that drops every
///   instruction whose program is ComputeBudget, element by element by
///   identity, and the `afterSwap` overload puts a fixed memo instruction right
///   after a swap that is not itself ComputeBudget.
///   `computeUnitPriceMicroLamports()` agrees with an independent count of the
///   SetComputeUnitPrice instructions and the generated decoder: empty for none,
///   an `IllegalStateException` for two or more, for one that is not 9 bytes or
///   for a price at or above 2^63, and otherwise exactly the decoded price.
///   `distinctAccountCount` equals the `numAccounts()` of the v1 transaction
///   `TxBuilder` builds from the filtered instructions, whenever that
///   transaction can be encoded. A present blockhash is 32 bytes.
/// - 11: the same bytes parse again to the same amounts, requestId, errorCode
///   and transaction length (or absence); exactly one of `quoteOnly()`,
///   `hasTransaction()` and `transactionBuildFailed()` holds; and
///   `expireAtInstant()` may only reject.
/// - 12: for each request parser, the first `serialize()` rejects, with an
///   `IllegalStateException`, exactly when the parsed request lacks `inputMint`,
///   `outputMint` or a non-zero `amount`, or, for `/build`, `taker`. A query
///   that serializes serializes the same twice, names each parameter once, and
///   comes out the same after a copy through `buildRequest(parsed)`.
/// - 13: the `/execute` request constructor rejects that requestId, with an
///   `IllegalArgumentException`, exactly when it is blank. The body `toJson()`
///   writes for any other requestId reads back, through json-iterator, as
///   exactly the values it was built from.
///
/// Each parse is individually tolerated, but every invariant check runs outside
/// any catch, so a violation is never mistaken for a rejection.
///
/// Deliberately has no Jazzer imports so it compiles with the regular test
/// sources; the raw `byte[]` signature is all the driver needs.
///
/// Run with `./gradlew :idl-clients-bundle:fuzzJupiterResponse [-PmaxFuzzTime=<seconds>]`.
public final class JupiterResponseFuzz {

  /// The ComputeBudget program, spelled out rather than taken from `SolanaAccounts`, so the
  /// filter reference shares no constant with the code it checks.
  private static final PublicKey COMPUTE_BUDGET_PROGRAM =
      PublicKey.fromBase58Encoded("ComputeBudget111111111111111111111111111111");
  private static final int SET_COMPUTE_UNIT_PRICE_DISCRIMINATOR = 3;
  /// The discriminator byte plus a u64 price.
  private static final int SET_COMPUTE_UNIT_PRICE_LENGTH = 9;
  private static final int BLOCKHASH_LENGTH = 32;
  /// The caller's own instruction for the `afterSwap` overload; a memo, which is never filtered.
  private static final Instruction MEMO_IX = Instruction.createInstruction(
      PublicKey.fromBase58Encoded("MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr"), List.of(), "hi".getBytes(UTF_8)
  );
  /// The fee payer for the count differential: 32 bytes of `0x7f`, which is `JupiterSwapV2TestFixtures.OTHER`
  /// (`9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn`). A body naming it as an account checks that both counts take
  /// the fee payer once; one naming it as a program cannot be encoded and skips the comparison.
  private static final PublicKey FUZZ_PAYER = fuzzPayer();

  /// Feeds `data[1..]` to the parser `data[0]` selects and checks the invariants listed on the class.
  ///
  /// @param data a selector byte followed by the body; shorter inputs are ignored
  public static void fuzzerTestOneInput(final byte[] data) {
    if (data.length < 2) {
      return;
    }
    final int selector = data[0] & 0xFF;
    final byte[] json = Arrays.copyOfRange(data, 1, data.length);
    // Each parse is individually tolerated — garbage in -> RuntimeException
    // out is the documented contract — but the invariant checks run OUTSIDE
    // any catch, so a violation is never mistaken for a rejection.
    switch (selector % 14) {
      case 0 -> {
        final JupiterQuote quote;
        try {
          quote = JupiterQuote.parse(json, JsonIterator.parse(json));
        } catch (final RuntimeException rejected) {
          return;
        }
        if (quote != null) {
          // the same bytes parsed a second time must read the same amounts —
          // and must not throw, having parsed once already
          final var reParsed = JupiterQuote.parse(json, JsonIterator.parse(json));
          if (quote.inAmount() != reParsed.inAmount()
              || quote.outAmount() != reParsed.outAmount()
              || quote.slippageBps() != reParsed.slippageBps()) {
            throw new IllegalStateException("quote parse is not deterministic");
          }
        }
      }
      case 1 -> {
        final JupiterSwapInstructions instructions;
        try {
          instructions = JupiterSwapInstructions.parseInstructions(JsonIterator.parse(json));
        } catch (final RuntimeException rejected) {
          return;
        }
        if (instructions != null) {
          expectExactFill(instructions);
        }
      }
      case 2 -> tolerate(() -> JupiterSwapInstructions.parseSwapInstruction(JsonIterator.parse(json)));
      case 3 -> tolerate(() -> JupiterSwapInstructions.parseLookupTables(JsonIterator.parse(json)));
      case 4 -> tolerate(() -> JupiterSwapIx.parse(JsonIterator.parse(json)));
      case 5 -> tolerate(() -> JupiterSwapTx.parse(JsonIterator.parse(json)));
      case 6 -> tolerate(() -> JupiterUltraOrder.parse(JsonIterator.parse(json)));
      case 7 -> {
        tolerate(() -> JupiterPrice.parsePrices(JsonIterator.parse(json)));
        tolerate(() -> JupiterTokenV2.parseTokens(JsonIterator.parse(json)));
      }
      case 8 -> tolerate(() -> ClaimAsrProof.parseProof(JsonIterator.parse(json)));
      case 9 -> {
        tolerate(() -> JupiterQuoteRequest.parseRequest(JsonIterator.parse(json)));
        tolerate(() -> JupiterUltraOrderRequest.parseRequest(JsonIterator.parse(json)));
      }
      case 10 -> {
        final var build = parseOrNull(() -> JupiterSwapBuild.parse(JsonIterator.parse(json)));
        if (build != null) {
          expectDeterministicBuild(json, build);
          expectJupiterOrder(build);
          final var reference = withoutComputeBudget(build.instructions());
          expectComputeBudgetFiltered(build, reference);
          expectAfterSwapPlacement(build, reference);
          expectComputeUnitPrice(build);
          expectDistinctAccountCount(build);
          expectBlockhashLength(build);
        }
      }
      case 11 -> {
        final var order = parseOrNull(() -> JupiterSwapOrder.parse(JsonIterator.parse(json)));
        if (order != null) {
          expectDeterministicOrder(json, order);
          expectOneOrderState(order);
          tolerate(order::expireAtInstant);
        }
      }
      case 12 -> {
        final var parsedBuildRequest = parseOrNull(() -> JupiterSwapBuildRequest.parseRequest(JsonIterator.parse(json)));
        if (parsedBuildRequest != null) {
          expectStableQuery("/build", lacksRequired(parsedBuildRequest), parsedBuildRequest::serialize,
              () -> JupiterSwapBuildRequest.buildRequest(parsedBuildRequest).createRequest().serialize()
          );
        }
        final var parsedOrderRequest = parseOrNull(() -> JupiterSwapOrderRequest.parseRequest(JsonIterator.parse(json)));
        if (parsedOrderRequest != null) {
          expectStableQuery("/order", lacksRequired(parsedOrderRequest), parsedOrderRequest::serialize,
              () -> JupiterSwapOrderRequest.buildRequest(parsedOrderRequest).createRequest().serialize()
          );
        }
      }
      case 13 -> {
        expectExecuteBodyReadsBack(new String(json, UTF_8));
        tolerate(() -> JupiterExecuteOrder.parse(json, JsonIterator.parse(json)));
      }
    }
  }

  private static void tolerate(final Runnable parse) {
    try {
      parse.run();
    } catch (final RuntimeException rejected) {
      // rejection is the contract; only non-RuntimeException throwables escape
    }
  }

  /// The parse's result, or null when it rejects the body with a `RuntimeException`, which is the contract;
  /// only non-`RuntimeException` throwables escape.
  private static <T> T parseOrNull(final Supplier<T> parse) {
    try {
      return parse.get();
    } catch (final RuntimeException rejected) {
      return null;
    }
  }

  /// `numInstructions()` sizes the array; `mergeAllAccounts` must fill it
  /// exactly — the index arithmetic is deliberately asymmetric around the
  /// nullable cleanup instruction, and a hole or an overflow here corrupts the
  /// serialized transaction.
  private static void expectExactFill(final JupiterSwapInstructions instructions) {
    final int count;
    final var accounts = new java.util.HashMap<software.sava.core.accounts.PublicKey, software.sava.core.accounts.meta.AccountMeta>();
    try {
      count = instructions.numInstructions();
      // fee-payer recovery legitimately rejects bodies with no setup payer and
      // no swap signer; done here so the merge below owns no benign throws
      accounts.putAll(instructions.createAccountsMap());
    } catch (final RuntimeException absentComponents) {
      // a body missing whole sections has no merge to check
      return;
    }
    final var array = new Instruction[count];
    final int serializedLength;
    try {
      serializedLength = instructions.mergeAllAccounts(array, accounts);
    } catch (final NullPointerException noSwapInstruction) {
      // the one component numInstructions does not dereference; a body without
      // it is malformed, not mis-sized. Anything else — an index off the end
      // of the array numInstructions sized — propagates as a finding.
      return;
    }
    if (serializedLength < 0) {
      throw new IllegalStateException("negative serialized instruction length: " + serializedLength);
    }
    for (int i = 0; i < array.length; i++) {
      if (array[i] == null) {
        throw new IllegalStateException(
            "numInstructions sized " + count + " but slot " + i + " was never written");
      }
    }
  }

  /// The same bytes parsed a second time must read the same amounts, slippage and instruction count, and must not
  /// throw, having parsed once already. Whole records are never compared: their arrays compare by reference.
  private static void expectDeterministicBuild(final byte[] json, final JupiterSwapBuild build) {
    final var reParsed = JupiterSwapBuild.parse(JsonIterator.parse(json));
    if (build.inAmount() != reParsed.inAmount()
        || build.outAmount() != reParsed.outAmount()
        || build.otherAmountThreshold() != reParsed.otherAmountThreshold()
        || build.slippageBps() != reParsed.slippageBps()
        || build.instructions().size() != reParsed.instructions().size()) {
      throw new IllegalStateException("/build parse is not deterministic");
    }
  }

  /// `instructions()` is compute budget, setup, swap, the cleanup when present, other, then the tip when present.
  private static void expectJupiterOrder(final JupiterSwapBuild build) {
    final var instructions = build.instructions();
    final int swapIndex = build.computeBudgetInstructions().size() + build.setupInstructions().size();
    final int expectedSize = swapIndex + 1
        + (build.cleanupInstruction() == null ? 0 : 1)
        + build.otherInstructions().size()
        + (build.tipInstruction() == null ? 0 : 1);
    if (instructions.size() != expectedSize) {
      throw new IllegalStateException(
          "instructions() has " + instructions.size() + " entries, expected " + expectedSize);
    }
    if (instructions.get(swapIndex) != build.swapInstruction()) {
      throw new IllegalStateException("instructions() does not hold the swap instruction at index " + swapIndex);
    }
  }

  private static boolean invokesComputeBudget(final Instruction ix) {
    return COMPUTE_BUDGET_PROGRAM.equals(ix.programId().publicKey());
  }

  /// The reference for the ComputeBudget filter: `instructions` in order, minus every instruction whose program is
  /// ComputeBudget.
  private static List<Instruction> withoutComputeBudget(final List<Instruction> instructions) {
    final var reference = new ArrayList<Instruction>();
    for (final var ix : instructions) {
      if (!invokesComputeBudget(ix)) {
        reference.add(ix);
      }
    }
    return reference;
  }

  private static void expectSameInstructions(final String what,
                                             final List<Instruction> expected,
                                             final List<Instruction> actual) {
    if (actual.size() != expected.size()) {
      throw new IllegalStateException(what + " has " + actual.size() + " instructions, expected " + expected.size());
    }
    for (int i = 0; i < expected.size(); i++) {
      if (actual.get(i) != expected.get(i)) {
        throw new IllegalStateException(what + " holds the wrong instruction at index " + i);
      }
    }
  }

  private static void expectComputeBudgetFiltered(final JupiterSwapBuild build, final List<Instruction> reference) {
    final var filtered = build.instructionsWithoutComputeBudget();
    expectSameInstructions("instructionsWithoutComputeBudget()", reference, filtered);
    for (final var ix : filtered) {
      if (invokesComputeBudget(ix)) {
        throw new IllegalStateException("instructionsWithoutComputeBudget() kept a ComputeBudget instruction");
      }
    }
  }

  /// `afterSwap` lands right after the swap, ahead of the cleanup; checked when the swap itself is kept.
  private static void expectAfterSwapPlacement(final JupiterSwapBuild build, final List<Instruction> reference) {
    final var swap = build.swapInstruction();
    if (invokesComputeBudget(swap)) {
      return;
    }
    int swapIndex = -1;
    for (int i = 0; i < reference.size(); i++) {
      if (reference.get(i) == swap) {
        swapIndex = i;
        break;
      }
    }
    if (swapIndex < 0) {
      throw new IllegalStateException("the filter reference lost the swap instruction");
    }
    final var expected = new ArrayList<>(reference);
    expected.add(swapIndex + 1, MEMO_IX);
    expectSameInstructions(
        "instructionsWithoutComputeBudget(afterSwap)", expected, build.instructionsWithoutComputeBudget(List.of(MEMO_IX))
    );
  }

  /// The price helper against an independent count of SetComputeUnitPrice instructions (program ComputeBudget, at
  /// least one byte, discriminator 3) and the generated decoder; only `IllegalStateException` is an expected
  /// rejection, so any other throwable propagates as a finding.
  private static void expectComputeUnitPrice(final JupiterSwapBuild build) {
    int prices = 0;
    Instruction price = null;
    for (final var ix : build.instructions()) {
      if (invokesComputeBudget(ix) && ix.len() >= 1 && ix.data()[ix.offset()] == SET_COMPUTE_UNIT_PRICE_DISCRIMINATOR) {
        ++prices;
        price = ix;
      }
    }
    OptionalLong result = null;
    IllegalStateException rejected = null;
    try {
      result = build.computeUnitPriceMicroLamports();
    } catch (final IllegalStateException malformed) {
      rejected = malformed;
    }
    if (prices == 0) {
      if (rejected != null) {
        throw new IllegalStateException("no SetComputeUnitPrice, yet the price was rejected", rejected);
      }
      if (result.isPresent()) {
        throw new IllegalStateException("no SetComputeUnitPrice, yet the price is " + result.getAsLong());
      }
    } else if (prices > 1 || price.len() != SET_COMPUTE_UNIT_PRICE_LENGTH) {
      if (rejected == null) {
        throw new IllegalStateException(prices + " SetComputeUnitPrice instructions, the last " + price.len()
            + " bytes long, yet the price " + result + " was accepted");
      }
    } else {
      final long expected = SetComputeUnitPriceIxData.read(price.data(), price.offset()).microLamports();
      if (expected < 0) {
        if (rejected == null) {
          throw new IllegalStateException(
              "a price of " + Long.toUnsignedString(expected) + " micro-lamports was accepted as " + result);
        }
      } else if (rejected != null) {
        throw new IllegalStateException("the price " + expected + " was rejected", rejected);
      } else if (!result.equals(OptionalLong.of(expected))) {
        throw new IllegalStateException("the price is " + result + ", the instruction carries " + expected);
      }
    }
  }

  /// `distinctAccountCount` is the account count of the lookup-table-free v1 transaction `TxBuilder` builds from the
  /// filtered instructions; a body whose instructions cannot be encoded as one has nothing to compare.
  private static void expectDistinctAccountCount(final JupiterSwapBuild build) {
    final var builder = TxBuilder.createBuilder();
    builder.strict(false);
    builder.feePayer(FUZZ_PAYER).addInstructions(build.instructionsWithoutComputeBudget());
    final int numAccounts;
    try {
      numAccounts = builder.createTransaction().numAccounts();
    } catch (final RuntimeException unencodable) {
      // no instruction left once a ComputeBudget swap is dropped, a program that
      // is the fee payer, or more than a v1 field can encode
      return;
    }
    final int count = build.distinctAccountCount(FUZZ_PAYER);
    if (numAccounts != count) {
      throw new IllegalStateException(
          "distinctAccountCount is " + count + ", the v1 transaction declares " + numAccounts + " accounts");
    }
  }

  private static void expectBlockhashLength(final JupiterSwapBuild build) {
    final var blockhashWithMetadata = build.blockhashWithMetadata();
    if (blockhashWithMetadata != null) {
      final byte[] blockhash = blockhashWithMetadata.blockhash();
      if (blockhash != null && blockhash.length != BLOCKHASH_LENGTH) {
        throw new IllegalStateException("blockhash has " + blockhash.length + " bytes");
      }
    }
  }

  /// The same bytes parsed a second time must read the same values, and must not throw, having parsed once already.
  private static void expectDeterministicOrder(final byte[] json, final JupiterSwapOrder order) {
    final var reParsed = JupiterSwapOrder.parse(JsonIterator.parse(json));
    final byte[] transaction = order.transaction();
    final byte[] reParsedTransaction = reParsed.transaction();
    if (order.inAmount() != reParsed.inAmount()
        || order.outAmount() != reParsed.outAmount()
        || !Objects.equals(order.requestId(), reParsed.requestId())
        || order.errorCode() != reParsed.errorCode()
        || (transaction == null) != (reParsedTransaction == null)
        || (transaction != null && transaction.length != reParsedTransaction.length)) {
      throw new IllegalStateException("/order parse is not deterministic");
    }
  }

  private static void expectOneOrderState(final JupiterSwapOrder order) {
    final int states = (order.quoteOnly() ? 1 : 0)
        + (order.hasTransaction() ? 1 : 0)
        + (order.transactionBuildFailed() ? 1 : 0);
    if (states != 1) {
      throw new IllegalStateException(states + " of quoteOnly(), hasTransaction() and transactionBuildFailed() hold");
    }
  }

  /// Whether a `/build` request lacks a parameter the API requires: `inputMint`, `outputMint`, a non-zero `amount`
  /// (0 is unset) or `taker`. Spelled out from the accessors' documentation rather than taken from the check
  /// `serialize()` runs, so the oracle shares no code with what it checks.
  private static boolean lacksRequired(final JupiterSwapBuildRequest request) {
    return request.inputMint() == null
        || request.outputMint() == null
        || request.amount() == 0
        || request.taker() == null;
  }

  /// Whether an `/order` request lacks a parameter the API requires: `inputMint`, `outputMint` or a non-zero `amount`
  /// (0 is unset). `taker` is optional there, since a request without one asks for a quote alone.
  private static boolean lacksRequired(final JupiterSwapOrderRequest request) {
    return request.inputMint() == null
        || request.outputMint() == null
        || request.amount() == 0;
  }

  /// A parsed request's query, when it serializes at all: the same twice, each parameter named once, and unchanged
  /// by a copy through the request's `buildRequest(prototype)`. A template may lack a required parameter, so the
  /// first `serialize()` may reject, but only with `IllegalStateException` and only when `lacksRequired` holds: a
  /// complete request rejected, or an incomplete one serialized, is a finding.
  private static void expectStableQuery(final String endpoint,
                                        final boolean lacksRequired,
                                        final Supplier<String> serialize,
                                        final Supplier<String> serializeCopy) {
    String query = null;
    IllegalStateException rejected = null;
    try {
      query = serialize.get();
    } catch (final IllegalStateException incomplete) {
      rejected = incomplete;
    }
    if (lacksRequired) {
      if (rejected == null) {
        throw new IllegalStateException(endpoint + " serialized a request lacking a required parameter: " + query);
      }
      return;
    }
    if (rejected != null) {
      throw new IllegalStateException(endpoint + " rejected a request with every required parameter", rejected);
    }
    if (!query.equals(serialize.get())) {
      throw new IllegalStateException(endpoint + " query is not deterministic: " + query);
    }
    final var names = new HashSet<String>();
    for (final var parameter : query.split("&", -1)) {
      final int equals = parameter.indexOf('=');
      final var name = equals < 0 ? parameter : parameter.substring(0, equals);
      if (!names.add(name)) {
        throw new IllegalStateException(endpoint + " query names " + name + " twice: " + query);
      }
    }
    final var copied = serializeCopy.get();
    if (!query.equals(copied)) {
      throw new IllegalStateException(endpoint + " query changed when copied: " + query + " became " + copied);
    }
  }

  /// The body `toJson()` writes for `requestId` must read back as `AQID`, `requestId` and `"7"`. The constructor's
  /// `IllegalArgumentException` for a blank `requestId`, in `String.isBlank()`'s sense, is the only rejection
  /// tolerated; one for any other `requestId`, or a blank one accepted, is a finding.
  private static void expectExecuteBodyReadsBack(final String requestId) {
    JupiterSwapExecuteRequest request = null;
    IllegalArgumentException rejected = null;
    try {
      request = new JupiterSwapExecuteRequest("AQID", requestId, 7);
    } catch (final IllegalArgumentException invalid) {
      rejected = invalid;
    }
    if (requestId.isBlank()) {
      if (rejected == null) {
        throw new IllegalStateException("a blank requestId was accepted");
      }
      return;
    }
    if (rejected != null) {
      throw new IllegalStateException("a requestId that is not blank was rejected", rejected);
    }
    final var json = request.toJson();
    final var body = new ExecuteBody();
    JsonIterator.parse(json.getBytes(UTF_8)).testObject(body);
    if (!"AQID".equals(body.signedTransaction)
        || !requestId.equals(body.requestId)
        || !"7".equals(body.lastValidBlockHeight)) {
      throw new IllegalStateException("the /execute body " + json + " does not read back as the request it was written from");
    }
  }

  private static PublicKey fuzzPayer() {
    final byte[] key = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(key, (byte) 0x7f);
    return PublicKey.createPubKey(key);
  }

  /// The three fields of an `/execute` body, read back as strings.
  private static final class ExecuteBody implements FieldBufferPredicate {

    private String signedTransaction;
    private String requestId;
    private String lastValidBlockHeight;

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("signedTransaction", buf, offset, len)) {
        signedTransaction = ji.readString();
      } else if (fieldEquals("requestId", buf, offset, len)) {
        requestId = ji.readString();
      } else if (fieldEquals("lastValidBlockHeight", buf, offset, len)) {
        lastValidBlockHeight = ji.readString();
      } else {
        ji.skip();
      }
      return true;
    }
  }

  private JupiterResponseFuzz() {
  }
}

package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterQuoteRequest;
import software.sava.idl.clients.jupiter.swap.rest.request.JupiterUltraOrderRequest;
import software.sava.idl.clients.jupiter.voter.rest.response.ClaimAsrProof;
import systems.comodal.jsoniter.JsonIterator;

import java.util.Arrays;

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
/// Crash-only fuzzing cannot see a wrong answer, so on a successful
/// `parseInstructions` the harness re-runs the merge arithmetic the swap flow
/// depends on: `numInstructions()` must size an array that
/// `mergeAllAccounts` fills exactly — every slot written, none left null —
/// regardless of which combination of lists/cleanup the body carried.
///
/// Deliberately has no Jazzer imports so it compiles with the regular test
/// sources; the raw `byte[]` signature is all the driver needs.
///
/// Run with `./gradlew :idl-clients-bundle:fuzzJupiterResponse [-PmaxFuzzTime=<seconds>]`.
public final class JupiterResponseFuzz {

  public static void fuzzerTestOneInput(final byte[] data) {
    if (data.length < 2) {
      return;
    }
    final int selector = data[0] & 0xFF;
    final byte[] json = Arrays.copyOfRange(data, 1, data.length);
    // Each parse is individually tolerated — garbage in -> RuntimeException
    // out is the documented contract — but the invariant checks run OUTSIDE
    // any catch, so a violation is never mistaken for a rejection.
    switch (selector % 10) {
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
    }
  }

  private static void tolerate(final Runnable parse) {
    try {
      parse.run();
    } catch (final RuntimeException rejected) {
      // rejection is the contract; only non-RuntimeException throwables escape
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

  private JupiterResponseFuzz() {
  }
}

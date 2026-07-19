package software.sava.idl.clients.jupiter.swap.rest.response;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import systems.comodal.jsoniter.JsonIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the account/instruction merge that turns a Jupiter swap response into
/// a transaction.
///
/// `mergeAllAccounts` writes into a pre-sized array whose length comes from
/// `numInstructions()`, so the two have to agree exactly — one slot too few
/// throws, one too many leaves a `null` that fails later, during serialization,
/// with nothing pointing back here.
///
/// The index arithmetic is deliberately asymmetric and easy to break: the two
/// leading loops post-increment, the swap instruction is written *without*
/// advancing, and everything after it pre-increments. That way a null cleanup
/// instruction leaves no hole — the first "other" instruction lands in the slot
/// the cleanup would have used. The tests below drive every combination of
/// present/absent cleanup and empty/non-empty lists.
final class JupiterSwapInstructionsTests {

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  /// Each instruction gets its own program and its own account, so a
  /// mis-slotted instruction is visible by identity rather than by shape.
  private static Instruction ix(final int id) {
    return Instruction.createInstruction(
        AccountMeta.createInvoked(key(0x80 + id)),
        List.of(AccountMeta.createWrite(key(id))),
        new byte[]{(byte) id});
  }

  /// The wallet signs every swap, which is what the fee-payer fallback relies on.
  private static Instruction swapIx(final int id) {
    return Instruction.createInstruction(
        AccountMeta.createInvoked(key(0x80 + id)),
        List.of(AccountMeta.createWritableSigner(WALLET), AccountMeta.createWrite(key(id))),
        new byte[]{(byte) id});
  }

  private static final PublicKey WALLET = key(0x70);

  private static List<Instruction> ixs(final int... ids) {
    final var list = new ArrayList<Instruction>(ids.length);
    for (final int id : ids) {
      list.add(ix(id));
    }
    return list;
  }

  private static JupiterSwapInstructions of(final List<Instruction> computeBudget,
                                            final List<Instruction> setup,
                                            final Instruction swap,
                                            final Instruction cleanup,
                                            final List<Instruction> other) {
    return new JupiterSwapInstructions(computeBudget, setup, swap, cleanup, other, List.of());
  }

  /// The array `serializeTransaction` allocates must be exactly filled — no
  /// overflow, and no null left behind.
  private static void assertFillsExactly(final JupiterSwapInstructions instructions,
                                         final List<Instruction> expectedOrder) {
    assertEquals(expectedOrder.size(), instructions.numInstructions(),
        "numInstructions must match what mergeAllAccounts writes");

    final var array = new Instruction[instructions.numInstructions()];
    final var accounts = instructions.createAccountsMap();
    final int length = instructions.mergeAllAccounts(array, accounts);

    for (int i = 0; i < array.length; i++) {
      assertNotNull(array[i], "slot " + i + " was never written");
    }
    assertEquals(expectedOrder, Arrays.asList(array), "instructions must land in response order");
    assertTrue(length > 0, "the serialized length accumulates every instruction");

    // every instruction's accounts reached the map
    for (final var instruction : expectedOrder) {
      for (final var meta : instruction.accounts()) {
        assertTrue(accounts.containsKey(meta.publicKey()),
            "account " + meta.publicKey() + " missing from the merged map");
      }
    }
  }

  @Test
  void fillsEverySlotWithACleanupInstruction() {
    final var cb = ixs(1, 2);
    final var setup = ixs(3, 4);
    final var swap = swapIx(5);
    final var cleanup = ix(6);
    final var other = ixs(7, 8);

    final var expected = new ArrayList<Instruction>();
    expected.addAll(cb);
    expected.addAll(setup);
    expected.add(swap);
    expected.add(cleanup);
    expected.addAll(other);

    assertFillsExactly(of(cb, setup, swap, cleanup, other), expected);
  }

  /// The interesting case: with no cleanup instruction the swap's slot is not
  /// advanced past, so the first "other" instruction has to take the next slot
  /// rather than overwrite the swap.
  @Test
  void anAbsentCleanupLeavesNoHole() {
    final var cb = ixs(1);
    final var setup = ixs(2);
    final var swap = swapIx(3);
    final var other = ixs(4, 5);

    final var expected = new ArrayList<Instruction>();
    expected.addAll(cb);
    expected.addAll(setup);
    expected.add(swap);
    expected.addAll(other);

    final var instructions = of(cb, setup, swap, null, other);
    assertEquals(5, instructions.numInstructions(), "the cleanup slot is not counted");
    assertFillsExactly(instructions, expected);

    // the swap survives — it is not overwritten by the first other instruction
    final var array = new Instruction[instructions.numInstructions()];
    instructions.mergeAllAccounts(array, instructions.createAccountsMap());
    assertEquals(swap, array[2]);
    assertEquals(other.getFirst(), array[3]);
  }

  /// The minimum: a swap on its own.
  @Test
  void aLoneSwapInstructionIsOneSlot() {
    final var swap = swapIx(9);
    final var instructions = of(List.of(), List.of(), swap, null, List.of());

    assertEquals(1, instructions.numInstructions());
    assertFillsExactly(instructions, List.of(swap));
  }

  /// Adding a cleanup adds exactly one slot; adding an "other" adds exactly one
  /// more. This pins the `+ 1` and the conditional term in `numInstructions`
  /// against off-by-one edits.
  @Test
  void eachOptionalPieceAddsExactlyOneSlot() {
    final var swap = swapIx(9);
    final int bare = of(List.of(), List.of(), swap, null, List.of()).numInstructions();
    final int withCleanup = of(List.of(), List.of(), swap, ix(10), List.of()).numInstructions();
    final int withOther = of(List.of(), List.of(), swap, null, ixs(11)).numInstructions();
    final int withBoth = of(List.of(), List.of(), swap, ix(10), ixs(11)).numInstructions();

    assertEquals(1, bare);
    assertEquals(bare + 1, withCleanup);
    assertEquals(bare + 1, withOther);
    assertEquals(bare + 2, withBoth);

    // and the leading lists contribute one slot each
    assertEquals(bare + 2, of(ixs(1, 2), List.of(), swap, null, List.of()).numInstructions());
    assertEquals(bare + 3, of(ixs(1, 2), ixs(3), swap, null, List.of()).numInstructions());
  }

  /// The returned length is the sum of the per-instruction contributions, so
  /// dropping any one of the five accumulations shows up here.
  @Test
  void theSerializedLengthCountsEveryInstruction() {
    final var swap = swapIx(3);

    final var lone = of(List.of(), List.of(), swap, null, List.of());
    final int loneLength = lone.mergeAllAccounts(
        new Instruction[lone.numInstructions()], lone.createAccountsMap());

    // each additional instruction strictly increases the accumulated length
    final var withCb = of(ixs(1), List.of(), swap, null, List.of());
    final var withSetup = of(List.of(), ixs(2), swap, null, List.of());
    final var withCleanup = of(List.of(), List.of(), swap, ix(4), List.of());
    final var withOther = of(List.of(), List.of(), swap, null, ixs(5));

    for (final var bigger : List.of(withCb, withSetup, withCleanup, withOther)) {
      final int length = bigger.mergeAllAccounts(
          new Instruction[bigger.numInstructions()], bigger.createAccountsMap());
      assertTrue(length > loneLength,
          "an extra instruction must add to the serialized length");
    }
  }

  /// `serializeTransaction` is the only caller that sizes the array, so it is
  /// the path that would actually throw on an off-by-one.
  @Test
  void serializeTransactionAcceptsBothShapes() {
    final var withCleanup = of(ixs(1), ixs(2), swapIx(3), ix(4), ixs(5));
    final var withoutCleanup = of(ixs(1), ixs(2), swapIx(3), null, ixs(5));

    assertNotNull(withCleanup.serializeTransaction((software.sava.core.accounts.lookup.AddressLookupTable) null));
    assertNotNull(withoutCleanup.serializeTransaction((software.sava.core.accounts.lookup.AddressLookupTable) null));
  }

  /// Regression: the fee payer used to be read straight off
  /// `setupInstructions.getFirst()`, so a response with no setup instructions —
  /// which Jupiter returns whenever the ATAs already exist and no SOL wrapping is
  /// needed — threw `NoSuchElementException` out of `serializeTransaction`.
  @Test
  void theFeePayerSurvivesAnEmptySetupInstructionList() {
    final var swap = swapIx(3);

    for (final var setup : List.of(List.<Instruction>of(), ixs(2))) {
      final var instructions = of(ixs(1), setup, swap, null, List.of());
      final var accounts = assertDoesNotThrow(instructions::createAccountsMap,
          "an empty setup list must not break the fee-payer lookup");
      assertFalse(accounts.isEmpty());
      assertDoesNotThrow(() -> instructions.serializeTransaction(
          (software.sava.core.accounts.lookup.AddressLookupTable) null));
    }

    // with no setup instruction the wallet is recovered from the swap's signer
    final var noSetup = of(List.of(), List.of(), swap, null, List.of());
    assertTrue(noSetup.createAccountsMap().containsKey(WALLET));

    // a null field (Jupiter omitting it entirely) behaves the same as an empty list
    final var nullSetup = of(List.of(), null, swap, null, List.of());
    assertDoesNotThrow(nullSetup::createAccountsMap);

    // and a swap with no signer at all is reported, not silently mis-attributed
    final var unsigned = of(List.of(), List.of(), ix(3), null, List.of());
    assertThrows(IllegalStateException.class, unsigned::createAccountsMap);
  }

  // ---------------------------------------------------------------------------
  // response parsing
  // ---------------------------------------------------------------------------

  private static final String WALLET_B58 = "So11111111111111111111111111111111111111112";
  private static final String PROGRAM_B58 = "ComputeBudget111111111111111111111111111111";
  private static final String TABLE_B58 = "SysvarC1ock11111111111111111111111111111111";

  private static String instructionJson(final String programId) {
    return """
        {"programId":"%s",\
        "accounts":[{"pubkey":"%s","isSigner":true,"isWritable":true},\
        {"pubkey":"%s","isSigner":false,"isWritable":false}],\
        "data":"AQID"}""".formatted(programId, WALLET_B58, TABLE_B58);
  }

  private static JsonIterator ji(final String json) {
    return JsonIterator.parse(json.getBytes(UTF_8));
  }

  /// Each account flag combination maps to a different `AccountMeta` factory, so
  /// the parser's four-way branch is driven end to end.
  @Test
  void accountFlagsSelectTheRightMetaKind() {
    final String template = """
        {"pubkey":"%s","isSigner":%s,"isWritable":%s}""";

    final var writableSigner = JupiterSwapInstructions.parseInstruction(ji(
        """
        {"programId":"%s","accounts":[%s],"data":"AQ=="}"""
            .formatted(PROGRAM_B58, template.formatted(WALLET_B58, "true", "true"))));
    assertTrue(writableSigner.accounts().getFirst().signer());
    assertTrue(writableSigner.accounts().getFirst().write());

    final var readOnlySigner = JupiterSwapInstructions.parseInstruction(ji(
        """
        {"programId":"%s","accounts":[%s],"data":"AQ=="}"""
            .formatted(PROGRAM_B58, template.formatted(WALLET_B58, "true", "false"))));
    assertTrue(readOnlySigner.accounts().getFirst().signer());
    assertFalse(readOnlySigner.accounts().getFirst().write());

    final var writable = JupiterSwapInstructions.parseInstruction(ji(
        """
        {"programId":"%s","accounts":[%s],"data":"AQ=="}"""
            .formatted(PROGRAM_B58, template.formatted(WALLET_B58, "false", "true"))));
    assertFalse(writable.accounts().getFirst().signer());
    assertTrue(writable.accounts().getFirst().write());

    final var readOnly = JupiterSwapInstructions.parseInstruction(ji(
        """
        {"programId":"%s","accounts":[%s],"data":"AQ=="}"""
            .formatted(PROGRAM_B58, template.formatted(WALLET_B58, "false", "false"))));
    assertFalse(readOnly.accounts().getFirst().signer());
    assertFalse(readOnly.accounts().getFirst().write());

    // unknown fields are skipped rather than derailing the parse
    final var extra = JupiterSwapInstructions.parseInstruction(ji(
        """
        {"unknown":123,"programId":"%s","accounts":[],"data":"AQ==","alsoUnknown":{"a":[1]}}"""
            .formatted(PROGRAM_B58)));
    assertEquals(PROGRAM_B58, extra.programId().publicKey().toBase58());
    assertArrayEquals(new byte[]{1}, extra.data());
  }

  /// An empty array collapses to the shared empty list rather than allocating.
  @Test
  void anEmptyInstructionListIsTheSharedConstant() {
    assertTrue(JupiterSwapInstructions.parseInstructionsList(ji("[]")).isEmpty());
    assertSame(
        JupiterSwapInstructions.parseInstructionsList(ji("[]")),
        JupiterSwapInstructions.parseInstructionsList(ji("[]")));

    final var one = JupiterSwapInstructions.parseInstructionsList(
        ji("[" + instructionJson(PROGRAM_B58) + "]"));
    assertEquals(1, one.size());
  }

  /// `parseSwapInstruction` and `parseLookupTables` scan forward for their field
  /// and, on a miss, rewind to the start and scan again — so a field positioned
  /// *before* the cursor is still found. Both orderings are exercised, plus the
  /// genuinely-absent case that must survive the second miss.
  @Test
  void fieldLookupRewindsWhenTheFieldPrecedesTheCursor() {
    final String swapField = "\"swapInstruction\":" + instructionJson(PROGRAM_B58);
    final String tablesField = "\"addressLookupTableAddresses\":[\"" + TABLE_B58 + "\"]";

    // swap field first, then the tables field: finding the tables requires no rewind
    final var forward = ji("{" + swapField + "," + tablesField + "}");
    assertNotNull(JupiterSwapInstructions.parseSwapInstruction(forward));

    // tables first: locating the swap instruction requires the rewind
    final var backward = ji("{" + tablesField + "," + swapField + "}");
    assertNotNull(JupiterSwapInstructions.parseSwapInstruction(backward));

    // genuinely absent -> null, after both the forward scan and the rewind fail
    assertNull(JupiterSwapInstructions.parseSwapInstruction(ji("{" + tablesField + "}")));

    // the same shape for the lookup tables
    assertEquals(1, JupiterSwapInstructions.parseLookupTables(
        ji("{" + swapField + "," + tablesField + "}")).size());
    assertEquals(1, JupiterSwapInstructions.parseLookupTables(
        ji("{" + tablesField + "," + swapField + "}")).size());
    assertTrue(JupiterSwapInstructions.parseLookupTables(ji("{" + swapField + "}")).isEmpty());
  }

  /// The whole-response parse, including a null cleanup instruction, which
  /// Jupiter sends as a literal `null` rather than omitting the field.
  @Test
  void parsesAFullResponseWithANullCleanup() {
    final var json = """
        {"computeBudgetInstructions":[%s],\
        "setupInstructions":[],\
        "swapInstruction":%s,\
        "cleanupInstruction":null,\
        "otherInstructions":[],\
        "addressLookupTableAddresses":["%s"]}"""
        .formatted(instructionJson(PROGRAM_B58), instructionJson(PROGRAM_B58), TABLE_B58);

    final var parsed = JupiterSwapInstructions.parseInstructions(ji(json));

    assertEquals(1, parsed.computeBudgetInstructions().size());
    assertTrue(parsed.setupInstructions().isEmpty());
    assertNotNull(parsed.swapInstruction());
    assertNull(parsed.cleanupInstruction(), "an explicit JSON null is not an instruction");
    assertTrue(parsed.otherInstructions().isEmpty());
    assertEquals(1, parsed.addressLookupTableAddresses().size());

    // 1 compute budget + 1 swap, and no cleanup slot
    assertEquals(2, parsed.numInstructions());

    // and it serializes despite the empty setup list — the regression above
    assertDoesNotThrow(() -> parsed.serializeTransaction(
        (software.sava.core.accounts.lookup.AddressLookupTable) null));
  }
}

package software.sava.idl.clients.jupiter.swap.rest.response;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.tx.Instruction;
import software.sava.core.tx.TxBuilder;
import software.sava.idl.clients.spl.compute_budget.gen.ComputeBudgetProgram.SetComputeUnitPriceIxData;
import systems.comodal.jsoniter.JsonIterator;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.OptionalLong;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapV2TestFixtures.*;

/// The v1 transaction helpers on [JupiterSwapBuild]: Jupiter's instruction order, the ComputeBudget
/// filter, the strictly decoded compute-unit price, the distinct-account count and the route data
/// decoder. Records are built from the fixtures and directly with `Instruction.createInstruction`,
/// and instructions are compared by identity, since sava-core instructions compare by value.
final class JupiterSwapBuildHelpersTests {

  private static JupiterSwapBuild parse(final String json) {
    return JupiterSwapBuild.parse(JsonIterator.parse(json.getBytes(UTF_8)));
  }

  private static byte[] base64(final String data) {
    return Base64.getDecoder().decode(data);
  }

  private static Instruction computeBudget(final String data) {
    return Instruction.createInstruction(CB_KEY, List.of(), base64(data));
  }

  private static Instruction memo() {
    return Instruction.createInstruction(MEMO_KEY, List.of(createReadOnlySigner(TAKER_KEY)), base64("aGk="));
  }

  private static Instruction swap() {
    return Instruction.createInstruction(
        JUP_KEY, List.of(createReadOnlySigner(TAKER_KEY), createWrite(AMM_A_KEY)), base64("u2T6zDHErxQ="));
  }

  private static Instruction cleanup() {
    return Instruction.createInstruction(TOKEN_PROGRAM_KEY, List.of(createWrite(WSOL_ATA_KEY)), base64("CQ=="));
  }

  private static Instruction tip() {
    return Instruction.createInstruction(SYSTEM_KEY, List.of(createWrite(TIP_KEY)), base64("AgAAAEBCDwAAAAAA"));
  }

  private static JupiterSwapBuild of(final List<Instruction> computeBudgetInstructions,
                                     final List<Instruction> setupInstructions,
                                     final Instruction swapInstruction,
                                     final Instruction cleanupInstruction,
                                     final List<Instruction> otherInstructions,
                                     final Instruction tipInstruction) {
    return new JupiterSwapBuild(
        WSOL_KEY, USDC_KEY, 1_000_000L, 4_612_089L, 4_589_028L, "ExactIn", 50, null, List.of(),
        computeBudgetInstructions, setupInstructions, swapInstruction, cleanupInstruction, otherInstructions,
        tipInstruction, null, null
    );
  }

  private static JupiterSwapBuild withPrices(final List<Instruction> computeBudgetInstructions,
                                             final List<Instruction> otherInstructions) {
    return of(computeBudgetInstructions, List.of(), swap(), null, otherInstructions, null);
  }

  private static void assertSameInstructions(final List<Instruction> expected, final List<Instruction> actual) {
    assertEquals(expected.size(), actual.size(), () -> "instruction count of " + actual);
    for (int i = 0; i < expected.size(); i++) {
      assertSame(expected.get(i), actual.get(i), "instruction " + i);
    }
  }

  private static int v1AccountCount(final PublicKey feePayer, final List<Instruction> instructions) {
    final var builder = TxBuilder.createBuilder();
    builder.strict(false);
    builder.feePayer(feePayer);
    builder.addInstructions(instructions);
    return builder.createTransaction().numAccounts();
  }

  @Test
  void instructionsFollowJupitersDocumentedOrder() {
    final var build = parse(BUILD);
    final var instructions = build.instructions();
    assertSameInstructions(List.of(
        build.computeBudgetInstructions().getFirst(),
        build.setupInstructions().getFirst(),
        build.setupInstructions().getLast(),
        build.swapInstruction(),
        build.cleanupInstruction(),
        build.tipInstruction()
    ), instructions);
    assertEquals(List.of(CB_KEY, ATA_PROGRAM_KEY, TOKEN_PROGRAM_KEY, JUP_KEY, TOKEN_PROGRAM_KEY, SYSTEM_KEY),
        instructions.stream().map(ix -> ix.programId().publicKey()).toList());

    // other instructions sit between cleanup and tip
    final var cb = computeBudget("A4fWEgAAAAAA");
    final var setup = memo();
    final var swap = swap();
    final var cleanup = cleanup();
    final var other = memo();
    final var tip = tip();
    assertSameInstructions(
        List.of(cb, setup, swap, cleanup, other, tip),
        of(List.of(cb), List.of(setup), swap, cleanup, List.of(other), tip).instructions()
    );
  }

  @Test
  void anAbsentCleanupOrTipLeavesNoGap() {
    final var setup = memo();
    final var swap = swap();
    final var cleanup = cleanup();
    final var other = memo();
    final var tip = tip();
    assertSameInstructions(List.of(setup, swap, cleanup, other, tip),
        of(List.of(), List.of(setup), swap, cleanup, List.of(other), tip).instructions());
    assertSameInstructions(List.of(setup, swap, other, tip),
        of(List.of(), List.of(setup), swap, null, List.of(other), tip).instructions());
    assertSameInstructions(List.of(setup, swap, cleanup, other),
        of(List.of(), List.of(setup), swap, cleanup, List.of(other), null).instructions());
    assertSameInstructions(List.of(setup, swap, other),
        of(List.of(), List.of(setup), swap, null, List.of(other), null).instructions());

    // the filtered forms leave no gap either
    assertSameInstructions(List.of(setup, swap, other),
        of(List.of(), List.of(setup), swap, null, List.of(other), null).instructionsWithoutComputeBudget());
    final var mine = tip();
    assertSameInstructions(List.of(setup, swap, mine, other),
        of(List.of(), List.of(setup), swap, null, List.of(other), null).instructionsWithoutComputeBudget(List.of(mine)));
  }

  @Test
  void instructionsWithoutComputeBudgetDropsComputeBudgetWhereverItSits() {
    final var build = parse(BUILD);
    final var filtered = build.instructionsWithoutComputeBudget();
    assertSameInstructions(List.of(
        build.setupInstructions().getFirst(),
        build.setupInstructions().getLast(),
        build.swapInstruction(),
        build.cleanupInstruction(),
        build.tipInstruction()
    ), filtered);

    final var stray = parse(BUILD_STRAY_COMPUTE_BUDGET);
    assertEquals(List.of(MEMO_KEY, CB_KEY, CB_KEY, JUP_KEY),
        stray.instructions().stream().map(ix -> ix.programId().publicKey()).toList());
    assertSameInstructions(List.of(stray.computeBudgetInstructions().getFirst(), stray.swapInstruction()),
        stray.instructionsWithoutComputeBudget());

    // ComputeBudget instructions in the cleanup, other and tip slots, and as the swap itself (never
    // observed), are dropped too
    final var cleanupSlot = computeBudget("AkANAwA=");
    final var otherSlot = computeBudget("A0BCDwAAAAAA");
    final var tipSlot = computeBudget("AwAAAAAAAAAA");
    final var setup = memo();
    final var swap = swap();
    assertSameInstructions(List.of(setup, swap),
        of(List.of(computeBudget("A4fWEgAAAAAA")), List.of(setup), swap, cleanupSlot, List.of(otherSlot), tipSlot)
            .instructionsWithoutComputeBudget());
    final var computeBudgetSwap = computeBudget("AkANAwA=");
    assertSameInstructions(List.of(setup),
        of(List.of(), List.of(setup), computeBudgetSwap, null, List.of(), null).instructionsWithoutComputeBudget());

    // ravina's requireNoComputeBudgetInstructions, reproduced: no element invokes ComputeBudget
    for (final var json : List.of(BUILD, BUILD_MINIMAL, BUILD_STRAY_COMPUTE_BUDGET)) {
      for (final var ix : parse(json).instructionsWithoutComputeBudget()) {
        assertNotEquals(CB_KEY, ix.programId().publicKey());
      }
    }

    // the ComputeBudget program as a mere account of another instruction is kept
    final var mentionsComputeBudget = Instruction.createInstruction(
        MEMO_KEY, List.of(createRead(CB_KEY)), base64("aGk="));
    assertSameInstructions(List.of(mentionsComputeBudget, swap),
        of(List.of(), List.of(mentionsComputeBudget), swap, null, List.of(), null).instructionsWithoutComputeBudget());

    // duplicates are kept
    final var duplicate = memo();
    assertSameInstructions(List.of(duplicate, duplicate, swap),
        of(List.of(), List.of(duplicate, duplicate), swap, null, List.of(), null).instructionsWithoutComputeBudget());
  }

  @Test
  void aForeignInstructionInComputeBudgetInstructionsIsKeptFirst() {
    final var stray = parse(BUILD_STRAY_COMPUTE_BUDGET);
    final var foreign = stray.computeBudgetInstructions().getFirst();
    assertEquals(MEMO_KEY, foreign.programId().publicKey());
    final var filtered = stray.instructionsWithoutComputeBudget();
    assertSame(foreign, filtered.getFirst());
    assertSameInstructions(List.of(foreign, stray.swapInstruction()), filtered);

    final var mine = memo();
    assertSameInstructions(List.of(foreign, stray.swapInstruction(), mine),
        stray.instructionsWithoutComputeBudget(List.of(mine)));
  }

  @Test
  void aResponseWithOnlyASwapIsJustTheSwap() {
    final var swap = swap();
    final var build = of(List.of(), List.of(), swap, null, List.of(), null);
    assertSameInstructions(List.of(swap), build.instructions());
    assertSameInstructions(List.of(swap), build.instructionsWithoutComputeBudget());
    final var mine = memo();
    assertSameInstructions(List.of(swap, mine), build.instructionsWithoutComputeBudget(List.of(mine)));

    final var minimal = parse(BUILD_MINIMAL);
    assertSameInstructions(List.of(minimal.swapInstruction()), minimal.instructions());
    assertSameInstructions(List.of(minimal.swapInstruction()), minimal.instructionsWithoutComputeBudget());
  }

  @Test
  void callerInstructionsGoRightAfterTheSwapAndAreNotFiltered() {
    final var build = parse(BUILD);
    final var mine = memo();
    assertSameInstructions(List.of(
        build.setupInstructions().getFirst(),
        build.setupInstructions().getLast(),
        build.swapInstruction(),
        mine,
        build.cleanupInstruction(),
        build.tipInstruction()
    ), build.instructionsWithoutComputeBudget(List.of(mine)));

    final var myComputeBudget = computeBudget("AkANAwA=");
    assertSameInstructions(List.of(
        build.setupInstructions().getFirst(),
        build.setupInstructions().getLast(),
        build.swapInstruction(),
        myComputeBudget,
        mine,
        build.cleanupInstruction(),
        build.tipInstruction()
    ), build.instructionsWithoutComputeBudget(List.of(myComputeBudget, mine)));

    assertSameInstructions(build.instructionsWithoutComputeBudget(), build.instructionsWithoutComputeBudget(List.of()));
    assertThrows(NullPointerException.class, () -> build.instructionsWithoutComputeBudget(null));
    // a null element is rejected here, not copied into the returned list
    final var holdsANull = Arrays.asList(mine, null);
    assertThrows(NullPointerException.class, () -> build.instructionsWithoutComputeBudget(holdsANull));
  }

  @Test
  void theReturnedListsAreUnmodifiable() {
    final var build = parse(BUILD);
    final var extra = memo();
    assertThrows(UnsupportedOperationException.class, () -> build.instructions().add(extra));
    assertThrows(UnsupportedOperationException.class, () -> build.instructionsWithoutComputeBudget().add(extra));
    assertThrows(UnsupportedOperationException.class, () -> build.instructionsWithoutComputeBudget(List.of(extra)).add(extra));
    assertThrows(UnsupportedOperationException.class, () -> build.computeBudgetInstructions().add(extra));
    assertThrows(UnsupportedOperationException.class, () -> build.setupInstructions().add(extra));
    assertThrows(UnsupportedOperationException.class, () -> build.otherInstructions().add(extra));
    assertThrows(UnsupportedOperationException.class, () -> build.routePlan().removeFirst());

    // each call builds a new list
    assertNotSame(build.instructions(), build.instructions());
  }

  @Test
  void computeUnitPriceIsTheLittleEndianU64AfterDiscriminatorThree() {
    assertEquals(OptionalLong.of(1_234_567L), parse(BUILD).computeUnitPriceMicroLamports());
    assertEquals(OptionalLong.of(72_623_859_790_382_856L),
        withPrices(List.of(computeBudget("AwgHBgUEAwIB")), List.of()).computeUnitPriceMicroLamports());
    assertEquals(OptionalLong.of(Long.MAX_VALUE),
        withPrices(List.of(computeBudget("A/////////9/")), List.of()).computeUnitPriceMicroLamports());
    assertEquals(OptionalLong.of(1_000_000L), parse(BUILD_STRAY_COMPUTE_BUDGET).computeUnitPriceMicroLamports());
  }

  @Test
  void computeUnitPriceIsEmptyWithoutASetComputeUnitPrice() {
    assertEquals(OptionalLong.empty(), parse(BUILD_MINIMAL).computeUnitPriceMicroLamports());
    assertEquals(OptionalLong.empty(), withPrices(List.of(), List.of()).computeUnitPriceMicroLamports());
    assertEquals(OptionalLong.empty(),
        withPrices(List.of(computeBudget("AkANAwA=")), List.of()).computeUnitPriceMicroLamports());
    final var tokenTransfer = Instruction.createInstruction(
        TOKEN_PROGRAM_KEY, List.of(createWrite(WSOL_ATA_KEY)), base64("AwAAAAAAAAAA"));
    assertEquals(OptionalLong.empty(), withPrices(List.of(tokenTransfer), List.of()).computeUnitPriceMicroLamports());
    final var emptyData = Instruction.createInstruction(CB_KEY, List.of(), new byte[0]);
    assertEquals(OptionalLong.empty(), withPrices(List.of(emptyData), List.of()).computeUnitPriceMicroLamports());
    for (final int discriminator : new int[]{0, 1, 2, 4}) {
      final var otherKind = Instruction.createInstruction(
          CB_KEY, List.of(), new byte[]{(byte) discriminator, 5, 0, 0, 0, 0, 0, 0, 0});
      assertEquals(OptionalLong.empty(), withPrices(List.of(otherKind), List.of()).computeUnitPriceMicroLamports(),
          "discriminator " + discriminator);
    }
  }

  @Test
  void zeroIsAPriceNotAbsence() {
    assertEquals(OptionalLong.of(0L),
        withPrices(List.of(computeBudget("AwAAAAAAAAAA")), List.of()).computeUnitPriceMicroLamports());
  }

  @Test
  void aSetComputeUnitPriceOutsideItsListIsFound() {
    assertEquals(OptionalLong.of(1_000_000L),
        withPrices(List.of(), List.of(computeBudget("A0BCDwAAAAAA"))).computeUnitPriceMicroLamports());
    assertEquals(OptionalLong.of(1_234_567L),
        of(List.of(), List.of(), swap(), null, List.of(), computeBudget("A4fWEgAAAAAA")).computeUnitPriceMicroLamports());
    assertEquals(OptionalLong.of(1_234_567L),
        of(List.of(), List.of(computeBudget("A4fWEgAAAAAA")), swap(), null, List.of(), null)
            .computeUnitPriceMicroLamports());
  }

  @Test
  void aMalformedSetComputeUnitPriceIsRejected() {
    final var eightBytes = assertThrows(IllegalStateException.class,
        () -> withPrices(List.of(computeBudget("AwECAwQFBgc=")), List.of()).computeUnitPriceMicroLamports());
    assertEquals("SetComputeUnitPrice data must be 9 bytes, was 8", eightBytes.getMessage());
    final var tenBytes = assertThrows(IllegalStateException.class,
        () -> withPrices(List.of(computeBudget("AwUAAAAAAAAAAA==")), List.of()).computeUnitPriceMicroLamports());
    assertEquals("SetComputeUnitPrice data must be 9 bytes, was 10", tenBytes.getMessage());
  }

  @Test
  void aSecondSetComputeUnitPriceIsRejectedWhereverItSits() {
    final var twoInList = assertThrows(IllegalStateException.class, () -> withPrices(
        List.of(computeBudget("A4fWEgAAAAAA"), computeBudget("A0BCDwAAAAAA")), List.of()
    ).computeUnitPriceMicroLamports());
    assertEquals("more than one SetComputeUnitPrice; the runtime rejects duplicate compute budget instructions",
        twoInList.getMessage());
    assertThrows(IllegalStateException.class, () -> withPrices(
        List.of(computeBudget("A4fWEgAAAAAA")), List.of(computeBudget("AwAAAAAAAAAA"))
    ).computeUnitPriceMicroLamports());
  }

  @Test
  void aPriceAtOrAbove2Pow63IsRejected() {
    final var twoPow63 = assertThrows(IllegalStateException.class,
        () -> withPrices(List.of(computeBudget("AwAAAAAAAACA")), List.of()).computeUnitPriceMicroLamports());
    assertTrue(twoPow63.getMessage().contains("9223372036854775808"), twoPow63.getMessage());
    final var u64Max = assertThrows(IllegalStateException.class,
        () -> withPrices(List.of(computeBudget("A///////////")), List.of()).computeUnitPriceMicroLamports());
    assertTrue(u64Max.getMessage().contains("18446744073709551615"), u64Max.getMessage());
  }

  @Test
  void theInstructionSliceIsReadNotItsBackingArray() {
    // a SetComputeUnitPrice(1234567) at offset 5 of a padded array whose bytes around it would
    // read as a different price, or as another instruction, if the slice were ignored
    final byte[] padded = {
        3, 9, 9, 9, 9,
        3, (byte) 135, (byte) 214, 18, 0, 0, 0, 0, 0,
        7, 7, 7
    };
    final var slice = Instruction.createInstruction(CB_KEY, List.of(), padded, 5, 9);
    assertEquals(OptionalLong.of(1_234_567L), withPrices(List.of(slice), List.of()).computeUnitPriceMicroLamports());
  }

  @Test
  void theGeneratedDecoderAgrees() {
    final var build = parse(BUILD);
    final var ix = build.computeBudgetInstructions().getFirst();
    assertEquals(1_234_567L, SetComputeUnitPriceIxData.read(ix.data(), ix.offset()).microLamports());
    assertEquals(OptionalLong.of(SetComputeUnitPriceIxData.read(ix.data(), ix.offset()).microLamports()),
        build.computeUnitPriceMicroLamports());
  }

  @Test
  void distinctAccountCountMatchesRavinasKeySet() {
    final var build = parse(BUILD);
    assertEquals(14, build.distinctAccountCount(TAKER_KEY));
    assertEquals(15, build.distinctAccountCount(OTHER_KEY));

    final var minimal = parse(BUILD_MINIMAL);
    assertEquals(2, minimal.distinctAccountCount(TAKER_KEY));
    assertEquals(3, minimal.distinctAccountCount(OTHER_KEY));
    assertEquals(3, parse(BUILD_STRAY_COMPUTE_BUDGET).distinctAccountCount(TAKER_KEY));
  }

  @Test
  void distinctAccountCountFoldsInTheCallersInstructions() {
    final var build = parse(BUILD);
    assertEquals(15, build.distinctAccountCount(TAKER_KEY, List.of(memo())));
    final var knownKeys = Instruction.createInstruction(
        TOKEN_PROGRAM_KEY, List.of(createWrite(WSOL_ATA_KEY), createReadOnlySigner(TAKER_KEY)), base64("CQ=="));
    assertEquals(14, build.distinctAccountCount(TAKER_KEY, List.of(knownKeys)));
    assertEquals(14, build.distinctAccountCount(TAKER_KEY, List.of()));
    // the caller's own ComputeBudget instruction is counted as given
    assertEquals(15, build.distinctAccountCount(TAKER_KEY, List.of(computeBudget("AkANAwA="))));
    assertEquals(16, build.distinctAccountCount(OTHER_KEY, List.of(memo())));
  }

  @Test
  void distinctAccountCountNeverCountsTheComputeBudgetProgram() {
    final var build = parse(BUILD);
    assertEquals(14, build.distinctAccountCount(TAKER_KEY));
    assertEquals(15, v1AccountCount(TAKER_KEY, build.instructions()));
  }

  @Test
  void distinctAccountCountMatchesTheV1TransactionSavaBuilds() {
    final var build = parse(BUILD);
    final var filtered = build.instructionsWithoutComputeBudget();
    assertEquals(14, v1AccountCount(TAKER_KEY, filtered));
    assertEquals(15, v1AccountCount(OTHER_KEY, filtered));
    assertEquals(v1AccountCount(TAKER_KEY, filtered), build.distinctAccountCount(TAKER_KEY));
    assertEquals(v1AccountCount(OTHER_KEY, filtered), build.distinctAccountCount(OTHER_KEY));

    final var mine = memo();
    final var withMine = build.instructionsWithoutComputeBudget(List.of(mine));
    assertEquals(15, v1AccountCount(TAKER_KEY, withMine));
    assertEquals(v1AccountCount(TAKER_KEY, withMine), build.distinctAccountCount(TAKER_KEY, List.of(mine)));
  }

  @Test
  void distinctAccountCountRequiresAFeePayer() {
    final var build = parse(BUILD);
    final var e = assertThrows(NullPointerException.class, () -> build.distinctAccountCount(null));
    assertEquals("feePayer", e.getMessage());
    assertThrows(NullPointerException.class, () -> build.distinctAccountCount(null, List.of()));
    assertThrows(NullPointerException.class, () -> build.distinctAccountCount(TAKER_KEY, null));
    final var holdsANull = Arrays.asList(memo(), null);
    assertThrows(NullPointerException.class, () -> build.distinctAccountCount(TAKER_KEY, holdsANull));
  }

  @Test
  void readSwapInstructionDataDecodesTheRouteV2Payload() {
    final var build = parse(BUILD);
    final var data = build.readSwapInstructionData();
    assertEquals(100_000_000L, data.inAmount());
    assertEquals(461_208_958L, data.quotedOutAmount());
    assertEquals(50, data.slippageBps());
    assertEquals(0, data.platformFeeBps());
    assertEquals(0, data.positiveSlippageBps());
    assertEquals(0, data.routePlan().length);
    assertEquals(build.inAmount(), data.inAmount());
    assertEquals(build.outAmount(), data.quotedOutAmount());
    assertEquals(build.slippageBps(), data.slippageBps());

    final var foreign = Instruction.createInstruction(JUP_KEY, List.of(), new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 0, 0});
    final var foreignBuild = of(List.of(), List.of(), foreign, null, List.of(), null);
    assertThrows(UnsupportedOperationException.class, foreignBuild::readSwapInstructionData);
  }

  @Test
  void readSwapInstructionDataReadsTheSliceNotItsBackingArray() {
    // BUILD's route_v2 data at offset 5 of a padded array: read from the start of the array, the
    // padding is no route discriminator
    final var swap = parse(BUILD).swapInstruction();
    final byte[] data = swap.copyData();
    final byte[] padded = new byte[5 + data.length + 3];
    Arrays.fill(padded, (byte) 7);
    System.arraycopy(data, 0, padded, 5, data.length);
    final var slice = Instruction.createInstruction(JUP_KEY, swap.accounts(), padded, 5, data.length);
    final var route = of(List.of(), List.of(), slice, null, List.of(), null).readSwapInstructionData();
    assertEquals(100_000_000L, route.inAmount());
    assertEquals(461_208_958L, route.quotedOutAmount());
    assertEquals(50, route.slippageBps());
  }
}

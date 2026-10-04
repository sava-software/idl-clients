package software.sava.idl.clients.jupiter.swap.rest.response;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.Base58;
import software.sava.core.tx.Instruction;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapV2TestFixtures.*;

/// Parsing `GET /swap/v2/build` responses into [JupiterSwapBuild], and the record's own
/// normalization. Every expected value is written out by hand from the fixture text.
final class JupiterSwapBuildTests {

  /// A swap instruction for bodies that exercise other fields.
  private static final String SWAP_INSTRUCTION = """
      "swapInstruction": {"programId": "JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4",
        "accounts": [{"pubkey": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ", "isSigner": true, "isWritable": false}],
        "data": "u2T6zDHErxQ="}""";

  private static JupiterSwapBuild parse(final String json) {
    return JupiterSwapBuild.parse(JsonIterator.parse(json.getBytes(UTF_8)));
  }

  /// `{<swap instruction>, <fields>}`.
  private static JupiterSwapBuild parseWithSwap(final String fields) {
    return parse("{" + SWAP_INSTRUCTION + ", " + fields + "}");
  }

  private static Instruction swapInstruction() {
    return Instruction.createInstruction(JUP_KEY, List.of(createReadOnlySigner(TAKER_KEY)), new byte[]{1, 2, 3});
  }

  @Test
  void parsesEveryFieldOfTheFullFixture() {
    final var build = parse(BUILD);

    assertEquals(WSOL_KEY, build.inputMint());
    assertEquals(USDC_KEY, build.outputMint());
    assertEquals(100_000_000L, build.inAmount());
    assertEquals(461_208_958L, build.outAmount());
    assertEquals(460_024_271L, build.otherAmountThreshold());
    assertEquals("ExactIn", build.swapMode());
    assertEquals(50, build.slippageBps());
    assertEquals(new BigDecimal("0.0001"), build.priceImpactPct());

    assertEquals(2, build.routePlan().size());
    final var meteora = build.routePlan().getFirst();
    assertEquals("3EKkiwNLWqoUbzFkPrmKbtUB4EweE6f4STzevYUmezeL", meteora.ammKey());
    assertEquals("Meteora DLMM", meteora.label());
    assertEquals(WSOL_KEY, meteora.inputMint());
    assertEquals(USDC_KEY, meteora.outputMint());
    assertEquals(66_670_000L, meteora.inAmount());
    assertEquals(307_498_012L, meteora.outAmount());
    assertEquals(new BigDecimal("66.67"), meteora.percent());
    assertEquals(new BigDecimal("6667"), meteora.bps());
    assertEquals(new BigDecimal("133.36"), meteora.usdValue());
    final var raydium = build.routePlan().getLast();
    assertEquals("675kPX9MHTjS2zt1qfr1NYHuzeLXfQM9H24wFSUt1Mp8", raydium.ammKey());
    assertEquals("Raydium", raydium.label());
    assertEquals(WSOL_KEY, raydium.inputMint());
    assertEquals(USDC_KEY, raydium.outputMint());
    assertEquals(33_330_000L, raydium.inAmount());
    assertEquals(153_710_946L, raydium.outAmount());
    assertEquals(new BigDecimal("33.33"), raydium.percent());
    assertEquals(new BigDecimal("3333"), raydium.bps());
    assertNull(raydium.usdValue());

    assertEquals(1, build.computeBudgetInstructions().size());
    final var price = build.computeBudgetInstructions().getFirst();
    assertEquals(CB_KEY, price.programId().publicKey());
    assertEquals(List.of(), price.accounts());
    assertArrayEquals(new byte[]{3, (byte) 135, (byte) 214, 18, 0, 0, 0, 0, 0}, price.copyData());

    assertEquals(2, build.setupInstructions().size());
    final var createAta = build.setupInstructions().getFirst();
    assertEquals(ATA_PROGRAM_KEY, createAta.programId().publicKey());
    assertEquals(List.of(
        createWritableSigner(TAKER_KEY),
        createWrite(WSOL_ATA_KEY),
        createRead(TAKER_KEY),
        createRead(WSOL_KEY),
        createRead(SYSTEM_KEY),
        createRead(TOKEN_PROGRAM_KEY)
    ), createAta.accounts());
    assertArrayEquals(new byte[]{1}, createAta.copyData());
    final var syncNative = build.setupInstructions().getLast();
    assertEquals(TOKEN_PROGRAM_KEY, syncNative.programId().publicKey());
    assertEquals(List.of(createWrite(WSOL_ATA_KEY)), syncNative.accounts());
    assertArrayEquals(new byte[]{17}, syncNative.copyData());

    final var swap = build.swapInstruction();
    assertEquals(JUP_KEY, swap.programId().publicKey());
    assertEquals(List.of(
        createRead(TOKEN_PROGRAM_KEY),
        createReadOnlySigner(TAKER_KEY),
        createWrite(WSOL_ATA_KEY),
        createWrite(USDC_ATA_KEY),
        createRead(JUP_KEY),
        createRead(USDC_KEY),
        createRead(EVENT_AUTHORITY_KEY),
        createRead(JUP_KEY),
        createWrite(AMM_A_KEY),
        createWrite(VAULT_A_KEY),
        createWrite(VAULT_B_KEY)
    ), swap.accounts());
    assertArrayEquals(new byte[]{
        (byte) 187, 100, (byte) 250, (byte) 204, 49, (byte) 196, (byte) 175, 20,
        0, (byte) 225, (byte) 245, 5, 0, 0, 0, 0,
        126, 125, 125, 27, 0, 0, 0, 0,
        50, 0, 0, 0, 0, 0, 0, 0, 0, 0
    }, swap.copyData());

    final var cleanup = build.cleanupInstruction();
    assertEquals(TOKEN_PROGRAM_KEY, cleanup.programId().publicKey());
    assertEquals(List.of(
        createWrite(WSOL_ATA_KEY),
        createWrite(TAKER_KEY),
        createReadOnlySigner(TAKER_KEY)
    ), cleanup.accounts());
    assertArrayEquals(new byte[]{9}, cleanup.copyData());

    assertEquals(List.of(), build.otherInstructions());

    final var tip = build.tipInstruction();
    assertEquals(SYSTEM_KEY, tip.programId().publicKey());
    assertEquals(List.of(createWritableSigner(TAKER_KEY), createWrite(TIP_KEY)), tip.accounts());
    assertArrayEquals(new byte[]{2, 0, 0, 0, 64, 66, 15, 0, 0, 0, 0, 0}, tip.copyData());

    assertEquals(Map.of(ALT_KEY, List.of(VAULT_A_KEY, VAULT_B_KEY, EVENT_AUTHORITY_KEY)),
        build.addressesByLookupTableAddress());
    assertEquals(List.of(VAULT_A_KEY, VAULT_B_KEY, EVENT_AUTHORITY_KEY), build.addressesByLookupTableAddress().get(ALT_KEY));

    final var blockhash = build.blockhashWithMetadata();
    assertArrayEquals(new byte[]{
        1, 8, 15, 22, 29, 36, 43, 50, 57, 64, 71, 78, 85, 92, 99, 106,
        113, 120, 127, (byte) 134, (byte) 141, (byte) 148, (byte) 155, (byte) 162,
        (byte) 169, (byte) 176, (byte) 183, (byte) 190, (byte) 197, (byte) 204, (byte) 211, (byte) 218
    }, blockhash.blockhash());
    assertEquals("52Y2w7NXvFk4zUsVGXaq3ai8xBVNV8FX7aME2Vpz2tH", Base58.encode(blockhash.blockhash()));
    assertEquals(279_000_150L, blockhash.lastValidBlockHeight());
    assertEquals(Instant.ofEpochSecond(1_759_480_000L, 123_456_789L), blockhash.fetchedAt());
  }

  @Test
  void parsesTheDocsTypeScriptShape() {
    final var build = parse(BUILD_MINIMAL);

    assertEquals(WSOL_KEY, build.inputMint());
    assertEquals(USDC_KEY, build.outputMint());
    assertEquals(1_000_000L, build.inAmount());
    assertEquals(4_612_089L, build.outAmount());
    assertEquals(4_589_028L, build.otherAmountThreshold());
    assertEquals("ExactIn", build.swapMode());
    assertEquals(50, build.slippageBps());
    assertNull(build.priceImpactPct());

    assertEquals(1, build.routePlan().size());
    final var step = build.routePlan().getFirst();
    assertEquals("3EKkiwNLWqoUbzFkPrmKbtUB4EweE6f4STzevYUmezeL", step.ammKey());
    assertEquals("Meteora DLMM", step.label());
    assertEquals(WSOL_KEY, step.inputMint());
    assertEquals(USDC_KEY, step.outputMint());
    assertEquals(1_000_000L, step.inAmount());
    assertEquals(4_612_089L, step.outAmount());
    assertEquals(new BigDecimal("100"), step.percent());
    assertEquals(new BigDecimal("10000"), step.bps());
    assertNull(step.usdValue());

    assertEquals(List.of(), build.computeBudgetInstructions());
    assertEquals(List.of(), build.setupInstructions());
    assertEquals(JUP_KEY, build.swapInstruction().programId().publicKey());
    assertEquals(List.of(createReadOnlySigner(TAKER_KEY)), build.swapInstruction().accounts());
    assertArrayEquals(new byte[]{(byte) 187, 100, (byte) 250, (byte) 204, 49, (byte) 196, (byte) 175, 20},
        build.swapInstruction().copyData());
    assertNull(build.cleanupInstruction());
    assertEquals(List.of(), build.otherInstructions());
    assertNull(build.tipInstruction());
    assertEquals(Map.of(), build.addressesByLookupTableAddress());

    final var blockhash = build.blockhashWithMetadata();
    assertEquals("52Y2w7NXvFk4zUsVGXaq3ai8xBVNV8FX7aME2Vpz2tH", Base58.encode(blockhash.blockhash()));
    assertEquals(279_000_150L, blockhash.lastValidBlockHeight());
    assertNull(blockhash.fetchedAt());
  }

  @Test
  void explicitNullsReadAsAbsent() {
    final var build = parseWithSwap("""
        "inputMint": null, "outputMint": null, "inAmount": null, "outAmount": null, "otherAmountThreshold": null,
        "swapMode": null, "slippageBps": null, "priceImpactPct": null, "routePlan": null,
        "computeBudgetInstructions": null, "setupInstructions": null, "cleanupInstruction": null,
        "otherInstructions": null, "tipInstruction": null, "addressesByLookupTableAddress": null,
        "blockhashWithMetadata": null""");
    assertNull(build.inputMint());
    assertNull(build.outputMint());
    assertEquals(0L, build.inAmount());
    assertEquals(0L, build.outAmount());
    assertEquals(0L, build.otherAmountThreshold());
    assertNull(build.swapMode());
    assertEquals(0, build.slippageBps());
    assertNull(build.priceImpactPct());
    assertEquals(List.of(), build.routePlan());
    assertEquals(List.of(), build.computeBudgetInstructions());
    assertEquals(List.of(), build.setupInstructions());
    assertNull(build.cleanupInstruction());
    assertEquals(List.of(), build.otherInstructions());
    assertNull(build.tipInstruction());
    assertEquals(Map.of(), build.addressesByLookupTableAddress());
    assertNull(build.blockhashWithMetadata());
    assertEquals(JUP_KEY, build.swapInstruction().programId().publicKey());

    final var nullMetadata = parseWithSwap("""
        "blockhashWithMetadata": {"blockhash": null, "lastValidBlockHeight": null, "fetchedAt": null}""");
    final var blockhash = nullMetadata.blockhashWithMetadata();
    assertNull(blockhash.blockhash());
    assertEquals(0L, blockhash.lastValidBlockHeight());
    assertNull(blockhash.fetchedAt());
  }

  /// `""` for a top-level or route-step mint reads as null, as an absent one does, and the fields after it
  /// still read. Only `""` pins the tolerant key reader: a strict base58 reader also reads JSON null as null.
  @Test
  void emptyMintsReadAsAbsent() {
    final var build = parseWithSwap("""
        "inputMint": "", "outputMint": "", "inAmount": "7",
        "routePlan": [
          {"swapInfo": {"inputMint": "", "outputMint": null, "label": "A", "inAmount": "1"}, "bps": 5000},
          {"swapInfo": {"inputMint": null, "outputMint": "", "label": "B", "outAmount": "2"}, "bps": 5000}
        ]""");
    assertNull(build.inputMint());
    assertNull(build.outputMint());
    assertEquals(7L, build.inAmount());
    assertEquals(2, build.routePlan().size());

    final var first = build.routePlan().getFirst();
    assertNull(first.inputMint());
    assertNull(first.outputMint());
    assertEquals("A", first.label());
    assertEquals(1L, first.inAmount());
    assertEquals(new BigDecimal("5000"), first.bps());

    final var second = build.routePlan().getLast();
    assertNull(second.inputMint());
    assertNull(second.outputMint());
    assertEquals("B", second.label());
    assertEquals(2L, second.outAmount());
    assertEquals(new BigDecimal("5000"), second.bps());
  }

  @Test
  void routePlanNumbersMayBeFractionalQuotedOrNull() {
    final var build = parseWithSwap("""
        "routePlan": [
          {"swapInfo": {"ammKey": "3EKkiwNLWqoUbzFkPrmKbtUB4EweE6f4STzevYUmezeL", "label": "Meteora DLMM",
            "inputMint": "So11111111111111111111111111111111111111112", "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
            "inAmount": "18446744073709551615", "outAmount": "4612089"}, "percent": 33.33, "bps": 3333.3, "usdValue": 4.89e2},
          {"swapInfo": {"ammKey": "675kPX9MHTjS2zt1qfr1NYHuzeLXfQM9H24wFSUt1Mp8", "label": "Raydium",
            "inputMint": "So11111111111111111111111111111111111111112", "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
            "inAmount": "", "outAmount": null}, "percent": "100", "bps": "10000", "usdValue": null},
          {"swapInfo": null, "percent": null, "bps": null}
        ]""");
    assertEquals(3, build.routePlan().size());

    final var fractional = build.routePlan().getFirst();
    assertEquals(new BigDecimal("33.33"), fractional.percent());
    assertEquals(new BigDecimal("3333.3"), fractional.bps());
    assertEquals(new BigDecimal("489"), fractional.usdValue());
    assertEquals(-1L, fractional.inAmount());
    assertEquals(4_612_089L, fractional.outAmount());

    final var quoted = build.routePlan().get(1);
    assertEquals(new BigDecimal("100"), quoted.percent());
    assertEquals(new BigDecimal("10000"), quoted.bps());
    assertNull(quoted.usdValue());
    assertEquals("Raydium", quoted.label());
    assertEquals(0L, quoted.inAmount());
    assertEquals(0L, quoted.outAmount());

    final var nulls = build.routePlan().getLast();
    assertNull(nulls.percent());
    assertNull(nulls.bps());
    assertNull(nulls.usdValue());
    assertNull(nulls.ammKey());
    assertNull(nulls.label());
    assertNull(nulls.inputMint());
    assertNull(nulls.outputMint());
    assertEquals(0L, nulls.inAmount());
    assertEquals(0L, nulls.outAmount());
  }

  @Test
  void amountsAreUnsigned64BitAndStrict() {
    final var build = parseWithSwap("""
        "inAmount": "18446744073709551615", "outAmount": "", "otherAmountThreshold": null""");
    assertEquals(-1L, build.inAmount());
    assertEquals(0L, build.outAmount());
    assertEquals(0L, build.otherAmountThreshold());

    final var bare = parseWithSwap("""
        "inAmount": 9223372036854775808, "outAmount": "0", "otherAmountThreshold": 7""");
    assertEquals(Long.MIN_VALUE, bare.inAmount());
    assertEquals(0L, bare.outAmount());
    assertEquals(7L, bare.otherAmountThreshold());

    for (final var amount : List.of("\"1.5\"", "\"-1\"", "1e3")) {
      assertThrows(NumberFormatException.class, () -> parseWithSwap("\"inAmount\": " + amount), amount);
      assertThrows(NumberFormatException.class, () -> parseWithSwap("\"outAmount\": " + amount), amount);
      assertThrows(NumberFormatException.class, () -> parseWithSwap("\"otherAmountThreshold\": " + amount), amount);
    }
  }

  @Test
  void integralFieldsAcceptIntegralSpellings() {
    for (final var slippage : List.of("50.0", "\"50\"", "5e1")) {
      assertEquals(50, parseWithSwap("\"slippageBps\": " + slippage).slippageBps(), slippage);
    }
    final var build = parseWithSwap("""
        "blockhashWithMetadata": {"lastValidBlockHeight": 2.7900015e8,
          "fetchedAt": {"secs_since_epoch": 1.75948e9, "nanos_since_epoch": "5"}}""");
    assertEquals(279_000_150L, build.blockhashWithMetadata().lastValidBlockHeight());
    assertEquals(Instant.ofEpochSecond(1_759_480_000L, 5L), build.blockhashWithMetadata().fetchedAt());
    assertNull(build.blockhashWithMetadata().blockhash());
  }

  @Test
  void aFractionalSlippageIsRejected() {
    assertThrows(ArithmeticException.class, () -> parseWithSwap("\"slippageBps\": 50.5"));
  }

  @Test
  void fetchedAtWithoutSecondsIsAbsentAndMissingNanosAreZero() {
    final var noSeconds = parseWithSwap("""
        "blockhashWithMetadata": {"fetchedAt": {"nanos_since_epoch": 5}}""");
    assertNull(noSeconds.blockhashWithMetadata().fetchedAt());

    final var noNanos = parseWithSwap("""
        "blockhashWithMetadata": {"fetchedAt": {"secs_since_epoch": 1759480000}}""");
    assertEquals(Instant.ofEpochSecond(1_759_480_000L), noNanos.blockhashWithMetadata().fetchedAt());

    // not an object
    final var notAnObject = parseWithSwap("""
        "blockhashWithMetadata": {"fetchedAt": "2026-10-03T00:00:00Z", "lastValidBlockHeight": 9}""");
    assertNull(notAnObject.blockhashWithMetadata().fetchedAt());
    assertEquals(9L, notAnObject.blockhashWithMetadata().lastValidBlockHeight());
  }

  /// A JSON-null or `""` `secs_since_epoch` reads as absent: `fetchedAt` is null, not the epoch,
  /// and the fields on either side of it still read.
  @Test
  void aNullOrEmptySecsSinceEpochReadsAsAbsent() {
    for (final var secs : List.of("null", "\"\"")) {
      final var blockhash = parseWithSwap("""
          "blockhashWithMetadata": {
            "blockhash": [1,8,15,22,29,36,43,50,57,64,71,78,85,92,99,106,
              113,120,127,134,141,148,155,162,169,176,183,190,197,204,211,218],
            "fetchedAt": {"secs_since_epoch": %s, "nanos_since_epoch": 5},
            "lastValidBlockHeight": 9}""".formatted(secs)).blockhashWithMetadata();
      assertNull(blockhash.fetchedAt(), secs);
      assertEquals("52Y2w7NXvFk4zUsVGXaq3ai8xBVNV8FX7aME2Vpz2tH", Base58.encode(blockhash.blockhash()), secs);
      assertEquals(9L, blockhash.lastValidBlockHeight(), secs);
    }

    // a JSON-null or "" nanos_since_epoch reads as 0, as an absent one does
    for (final var nanos : List.of("null", "\"\"")) {
      final var fetchedAt = parseWithSwap("""
          "blockhashWithMetadata": {"fetchedAt": {"secs_since_epoch": 1759480000, "nanos_since_epoch": %s}}"""
          .formatted(nanos)).blockhashWithMetadata().fetchedAt();
      assertEquals(Instant.ofEpochSecond(1_759_480_000L), fetchedAt, nanos);
    }

    // zero seconds is a value, not absence
    final var zero = parseWithSwap("""
        "blockhashWithMetadata": {"fetchedAt": {"secs_since_epoch": 0}}""");
    assertEquals(Instant.EPOCH, zero.blockhashWithMetadata().fetchedAt());
  }

  /// `fetchedAt` is informational, so a time `Instant` cannot hold reads as absent instead of
  /// failing the parse, as long as its numbers fit a long: the blockhash and every instruction
  /// still read.
  @Test
  void aFetchedAtOutsideInstantsRangeReadsAsAbsent() {
    final var body = BUILD.replace("\"secs_since_epoch\": 1759480000", "\"secs_since_epoch\": 99999999999999999");
    assertNotEquals(BUILD, body);
    final var build = parse(body);
    final var blockhash = build.blockhashWithMetadata();
    assertNull(blockhash.fetchedAt());
    assertEquals("52Y2w7NXvFk4zUsVGXaq3ai8xBVNV8FX7aME2Vpz2tH", Base58.encode(blockhash.blockhash()));
    assertEquals(279_000_150L, blockhash.lastValidBlockHeight());
    assertEquals(List.of(CB_KEY, ATA_PROGRAM_KEY, TOKEN_PROGRAM_KEY, JUP_KEY, TOKEN_PROGRAM_KEY, SYSTEM_KEY),
        build.instructions().stream().map(ix -> ix.programId().publicKey()).toList());
    assertEquals(100_000_000L, build.readSwapInstructionData().inAmount());

    // past either end of the range, out to the last long on each side, and a nanosecond carry that
    // overflows a long; the field after fetchedAt still reads
    for (final var fetchedAt : List.of(
        "{\"secs_since_epoch\": 31556889864403200}",
        "{\"secs_since_epoch\": -31557014167219201}",
        "{\"secs_since_epoch\": 9223372036854775807}",
        "{\"secs_since_epoch\": -9223372036854775808}",
        "{\"secs_since_epoch\": 9223372036854775807, \"nanos_since_epoch\": 1000000000}",
        "{\"secs_since_epoch\": -9223372036854775808, \"nanos_since_epoch\": -1}")) {
      final var outside = parseWithSwap(
          "\"blockhashWithMetadata\": {\"fetchedAt\": " + fetchedAt + ", \"lastValidBlockHeight\": 9}"
      ).blockhashWithMetadata();
      assertNull(outside.fetchedAt(), fetchedAt);
      assertEquals(9L, outside.lastValidBlockHeight(), fetchedAt);
    }

    // the last second at either end of the range is kept
    final var latest = parseWithSwap("""
        "blockhashWithMetadata": {"fetchedAt": {"secs_since_epoch": 31556889864403199}}""");
    assertEquals(Instant.ofEpochSecond(31_556_889_864_403_199L), latest.blockhashWithMetadata().fetchedAt());
    final var earliest = parseWithSwap("""
        "blockhashWithMetadata": {"fetchedAt": {"secs_since_epoch": -31557014167219200}}""");
    assertEquals(Instant.ofEpochSecond(-31_557_014_167_219_200L), earliest.blockhashWithMetadata().fetchedAt());
  }

  /// A `fetchedAt` number a long cannot hold, a fraction or a value past either end of a long in
  /// either field, is no serde `SystemTime` value and fails the parse.
  @Test
  void aFetchedAtNumberALongCannotHoldFailsTheParse() {
    for (final var fetchedAt : List.of(
        "{\"secs_since_epoch\": 9223372036854775808}",
        "{\"secs_since_epoch\": \"18446744073709551615\"}",
        "{\"secs_since_epoch\": -9223372036854775809}",
        "{\"secs_since_epoch\": 1759480000, \"nanos_since_epoch\": 9223372036854775808}",
        "{\"secs_since_epoch\": 1.5}")) {
      assertThrows(ArithmeticException.class,
          () -> parseWithSwap("\"blockhashWithMetadata\": {\"fetchedAt\": " + fetchedAt + "}"), fetchedAt);
    }

    // the last long in nanos_since_epoch still reads, carried into the seconds
    final var lastNanos = parseWithSwap("""
        "blockhashWithMetadata": {"fetchedAt": {"secs_since_epoch": 1759480000,
          "nanos_since_epoch": 9223372036854775807}}""");
    assertEquals(Instant.ofEpochSecond(10_982_852_036L, 854_775_807L), lastNanos.blockhashWithMetadata().fetchedAt());
  }

  @Test
  void aBuildWithoutASwapInstructionIsRejected() {
    for (final var json : List.of(
        "{\"inAmount\": \"1\"}",
        "{\"inAmount\": \"1\", \"swapInstruction\": null}",
        "{\"inAmount\": \"1\", \"swapInstruction\": \"x\"}")) {
      final var e = assertThrows(NullPointerException.class, () -> parse(json), json);
      assertEquals("/build response has no swapInstruction", e.getMessage());
    }
  }

  /// The three tables are sent in an order a `HashMap` of these keys does not iterate in.
  @Test
  void lookupTablesKeepResponseOrderAndAreUnmodifiable() {
    final var build = parseWithSwap("""
        "addressesByLookupTableAddress": {
          "675kPX9MHTjS2zt1qfr1NYHuzeLXfQM9H24wFSUt1Mp8": ["3JF3sEqM796hk5WFqA6EtmEwJQ9quALszsfJyvXNQKy3"],
          "3NAM1YJMhSPvtAkmGTRABe1hYZN3aE2hZHKy3JZy9fHk": ["GGztQqQ6pCPaJQnNpXBgELr5cs3WwDakRbh1iEMzjgSJ", "3JF3sEqM796hk5WFqA6EtmEwJQ9quALszsfJyvXNQKy3"],
          "3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT": []}""");
    final var tables = build.addressesByLookupTableAddress();
    assertEquals(List.of(AMM_B_KEY, VAULT_B_KEY, ALT_KEY), List.copyOf(tables.keySet()));
    assertEquals(List.of(VAULT_A_KEY), tables.get(AMM_B_KEY));
    assertEquals(List.of(TIP_KEY, VAULT_A_KEY), tables.get(VAULT_B_KEY));
    assertEquals(List.of(), tables.get(ALT_KEY));

    assertThrows(UnsupportedOperationException.class, () -> tables.put(MEMO_KEY, List.of()));
    assertThrows(UnsupportedOperationException.class, () -> tables.remove(AMM_B_KEY));
    assertThrows(UnsupportedOperationException.class, () -> tables.get(VAULT_B_KEY).add(MEMO_KEY));
    assertThrows(UnsupportedOperationException.class, () -> tables.get(ALT_KEY).add(MEMO_KEY));
  }

  @Test
  void constructionNormalizesAbsentSections() {
    final var swap = swapInstruction();
    final var build = new JupiterSwapBuild(
        null, null, 0, 0, 0, null, 0, null,
        null, null, null, swap, null, null, null, null, null
    );
    assertEquals(List.of(), build.routePlan());
    assertEquals(List.of(), build.computeBudgetInstructions());
    assertEquals(List.of(), build.setupInstructions());
    assertEquals(List.of(), build.otherInstructions());
    assertEquals(Map.of(), build.addressesByLookupTableAddress());
    assertSame(swap, build.swapInstruction());
    assertNull(build.cleanupInstruction());
    assertNull(build.tipInstruction());
    assertNull(build.blockhashWithMetadata());
    assertThrows(UnsupportedOperationException.class, () -> build.setupInstructions().add(swap));
    assertThrows(UnsupportedOperationException.class, () -> build.addressesByLookupTableAddress().put(ALT_KEY, List.of()));

    // lists passed in are copied: changing them afterwards never changes the record
    final var memo = Instruction.createInstruction(MEMO_KEY, List.of(), new byte[]{'h', 'i'});
    final var routePlan = new ArrayList<JupiterSwapRouteStep>();
    final var computeBudget = new ArrayList<Instruction>();
    final var setup = new ArrayList<Instruction>();
    final var other = new ArrayList<Instruction>();
    setup.add(memo);
    final var copied = new JupiterSwapBuild(
        null, null, 0, 0, 0, null, 0, null,
        routePlan, computeBudget, setup, swap, null, other, null, null, null
    );
    routePlan.add(new JupiterSwapRouteStep(null, null, null, null, 0, 0, null, null, null));
    computeBudget.add(memo);
    setup.add(memo);
    other.add(memo);
    assertEquals(List.of(), copied.routePlan());
    assertEquals(List.of(), copied.computeBudgetInstructions());
    assertEquals(1, copied.setupInstructions().size());
    assertSame(memo, copied.setupInstructions().getFirst());
    assertEquals(List.of(), copied.otherInstructions());
    assertThrows(UnsupportedOperationException.class, () -> copied.routePlan().add(null));
    assertThrows(UnsupportedOperationException.class, () -> copied.computeBudgetInstructions().add(memo));
    assertThrows(UnsupportedOperationException.class, () -> copied.otherInstructions().add(memo));
  }

  @Test
  void constructionCopiesTheLookupTables() {
    final var altAddresses = new ArrayList<PublicKey>();
    altAddresses.add(VAULT_A_KEY);
    final var tables = new LinkedHashMap<PublicKey, List<PublicKey>>();
    tables.put(ALT_KEY, altAddresses);
    tables.put(AMM_A_KEY, List.of(VAULT_B_KEY));
    tables.put(AMM_B_KEY, List.of(TIP_KEY));

    final var build = new JupiterSwapBuild(
        null, null, 0, 0, 0, null, 0, null,
        null, null, null, swapInstruction(), null, null, null, tables, null
    );
    final var copy = build.addressesByLookupTableAddress();
    assertEquals(List.of(ALT_KEY, AMM_A_KEY, AMM_B_KEY), List.copyOf(copy.keySet()));
    assertEquals(List.of(VAULT_A_KEY), copy.get(ALT_KEY));
    assertEquals(List.of(VAULT_B_KEY), copy.get(AMM_A_KEY));
    assertEquals(List.of(TIP_KEY), copy.get(AMM_B_KEY));

    tables.put(MEMO_KEY, List.of());
    altAddresses.add(EVENT_AUTHORITY_KEY);
    assertEquals(List.of(ALT_KEY, AMM_A_KEY, AMM_B_KEY), List.copyOf(copy.keySet()));
    assertEquals(List.of(VAULT_A_KEY), copy.get(ALT_KEY));
    assertSame(copy, build.addressesByLookupTableAddress());

    assertThrows(UnsupportedOperationException.class, () -> copy.put(MEMO_KEY, List.of()));
    assertThrows(UnsupportedOperationException.class, () -> copy.get(ALT_KEY).add(EVENT_AUTHORITY_KEY));

    // the reverse insertion order is kept too; a HashMap of these keys iterates ALT, AMM_A, AMM_B
    final var reversed = new LinkedHashMap<PublicKey, List<PublicKey>>();
    reversed.put(AMM_B_KEY, List.of(TIP_KEY));
    reversed.put(AMM_A_KEY, List.of(VAULT_B_KEY));
    reversed.put(ALT_KEY, List.of(VAULT_A_KEY));
    final var reversedBuild = new JupiterSwapBuild(
        null, null, 0, 0, 0, null, 0, null,
        null, null, null, swapInstruction(), null, null, null, reversed, null
    );
    assertEquals(List.of(AMM_B_KEY, AMM_A_KEY, ALT_KEY), List.copyOf(reversedBuild.addressesByLookupTableAddress().keySet()));

    final var nullList = new LinkedHashMap<PublicKey, List<PublicKey>>();
    nullList.put(ALT_KEY, null);
    assertThrows(NullPointerException.class, () -> new JupiterSwapBuild(
        null, null, 0, 0, 0, null, 0, null,
        null, null, null, swapInstruction(), null, null, null, nullList, null
    ));

    // a null table address is rejected as Map.copyOf would, though a LinkedHashMap accepts it
    final var nullKey = new LinkedHashMap<PublicKey, List<PublicKey>>();
    nullKey.put(null, List.of());
    final var e = assertThrows(NullPointerException.class, () -> new JupiterSwapBuild(
        null, null, 0, 0, 0, null, 0, null,
        null, null, null, swapInstruction(), null, null, null, nullKey, null
    ));
    assertEquals("lookup table address", e.getMessage());
  }

  @Test
  void routeStepAmmKeyIsTheStringAsSent() {
    assertEquals("3EKkiwNLWqoUbzFkPrmKbtUB4EweE6f4STzevYUmezeL", parse(BUILD).routePlan().getFirst().ammKey());

    final var empty = parseWithSwap("""
        "routePlan": [{"swapInfo": {"ammKey": "", "label": "Meteora DLMM"}, "percent": 100, "bps": 10000}]""");
    assertEquals("", empty.routePlan().getFirst().ammKey());
    assertEquals("Meteora DLMM", empty.routePlan().getFirst().label());

    final var absent = parseWithSwap("""
        "routePlan": [{"swapInfo": {"label": "Meteora DLMM"}, "percent": 100, "bps": 10000}]""");
    assertNull(absent.routePlan().getFirst().ammKey());
    assertEquals("Meteora DLMM", absent.routePlan().getFirst().label());
  }
}

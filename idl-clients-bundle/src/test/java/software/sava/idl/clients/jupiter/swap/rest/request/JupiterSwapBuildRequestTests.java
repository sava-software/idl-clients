package software.sava.idl.clients.jupiter.swap.rest.request;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.JsonException;
import systems.comodal.jsoniter.JsonIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

/// Covers the `GET /swap/v2/build` request: its query, its setter checks, its required-parameter and
/// contradiction checks, and its JSON template parser.
///
/// The query *is* the request. A dropped parameter silently reverts to a Jupiter default, a misspelled name is
/// ignored rather than rejected, and a transposed key routes funds to the wrong account while staying well formed.
/// So every expected query is written out by hand in the API reference's parameter order, the account parameters
/// carry seven distinct keys, and parameters are compared by name and value after splitting on `&`, never by
/// substring.
final class JupiterSwapBuildRequestTests {

  private static final PublicKey WSOL = PublicKey.fromBase58Encoded("So11111111111111111111111111111111111111112");
  private static final PublicKey USDC = PublicKey.fromBase58Encoded("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");
  private static final PublicKey TAKER = PublicKey.fromBase58Encoded("GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ");
  private static final PublicKey PAYER = PublicKey.fromBase58Encoded("gasTzr94Pmp4Gf8vknQnqxeYxdgwFjbgdJa4msYRpnB");
  private static final PublicKey FEE_ACCOUNT = PublicKey.fromBase58Encoded("CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48");
  private static final PublicKey DEST = PublicKey.fromBase58Encoded("HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR");
  private static final PublicKey OTHER = PublicKey.fromBase58Encoded("9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn");

  /// B1: the four required parameters.
  private static final String MINIMAL_QUERY = "inputMint=So11111111111111111111111111111111111111112&outputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&amount=100000000&taker=GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ";
  /// B2: every parameter, on the `dexes` / `destinationTokenAccount` branch.
  private static final String FULL_QUERY = "inputMint=So11111111111111111111111111111111111111112&outputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&amount=100000000&taker=GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ&slippageBps=0&mode=fast&dexes=Meteora+DLMM%2COrca+V2&platformFeeBps=20&feeAccount=CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48&maxAccounts=40&payer=gasTzr94Pmp4Gf8vknQnqxeYxdgwFjbgdJa4msYRpnB&wrapAndUnwrapSol=false&destinationTokenAccount=HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR&blockhashSlotsToExpiry=300&tipAmount=1000000&computeUnitPricePercentile=veryHigh&forJitoBundle=true";
  /// B2 with `maxAccounts=20` in place of `maxAccounts=40`.
  private static final String FULL_QUERY_WITH_MAX_ACCOUNTS_20 = "inputMint=So11111111111111111111111111111111111111112&outputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&amount=100000000&taker=GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ&slippageBps=0&mode=fast&dexes=Meteora+DLMM%2COrca+V2&platformFeeBps=20&feeAccount=CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48&maxAccounts=20&payer=gasTzr94Pmp4Gf8vknQnqxeYxdgwFjbgdJa4msYRpnB&wrapAndUnwrapSol=false&destinationTokenAccount=HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR&blockhashSlotsToExpiry=300&tipAmount=1000000&computeUnitPricePercentile=veryHigh&forJitoBundle=true";
  /// B3: the alternative slots, `rtse`, `excludeDexes`, `nativeDestinationAccount` and a basis-point percentile.
  private static final String ALTERNATIVE_QUERY = "inputMint=So11111111111111111111111111111111111111112&outputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&amount=18446744073709551615&taker=GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ&slippageBps=rtse&excludeDexes=SolFi&nativeDestinationAccount=9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn&computeUnitPricePercentile=7500";

  private static JupiterSwapBuildRequest.Builder minimal() {
    return JupiterSwapBuildRequest.buildRequest()
        .inputMint(WSOL)
        .outputMint(USDC)
        .amount(100_000_000)
        .taker(TAKER);
  }

  /// Every B2 parameter.
  private static JupiterSwapBuildRequest.Builder full() {
    return minimal()
        .slippageBps(0)
        .fastMode(true)
        .dexes(List.of("Meteora DLMM", "Orca V2"))
        .platformFeeBps(20)
        .feeAccount(FEE_ACCOUNT)
        .maxAccounts(40)
        .payer(PAYER)
        .wrapAndUnwrapSol(false)
        .destinationTokenAccount(DEST)
        .blockhashSlotsToExpiry(300)
        .tipAmount(1_000_000)
        .computeUnitPricePercentile(ComputeUnitPriceLevel.veryHigh)
        .forJitoBundle(true);
  }

  /// Every B3 parameter.
  private static JupiterSwapBuildRequest.Builder alternative() {
    return JupiterSwapBuildRequest.buildRequest()
        .inputMint(WSOL)
        .outputMint(USDC)
        .amount(-1L)
        .taker(TAKER)
        .rtseSlippage()
        .excludeDexes(List.of("SolFi"))
        .nativeDestinationAccount(OTHER)
        .computeUnitPricePercentileBps(7500);
  }

  /// The query's `name=value` pairs in order; a pair without a name, an empty pair, or a leading or trailing `&`
  /// fails the test.
  private static List<Map.Entry<String, String>> pairs(final String query) {
    final var pairs = new ArrayList<Map.Entry<String, String>>();
    for (final var pair : query.split("&", -1)) {
      final int separator = pair.indexOf('=');
      assertTrue(separator > 0, () -> "not a name=value pair: '" + pair + "' in " + query);
      pairs.add(Map.entry(pair.substring(0, separator), pair.substring(separator + 1)));
    }
    return pairs;
  }

  private static List<String> names(final String query) {
    return pairs(query).stream().map(Map.Entry::getKey).toList();
  }

  /// Every value sent under `name`, in order; empty when the name is absent.
  private static List<String> values(final String query, final String name) {
    return pairs(query).stream().filter(pair -> pair.getKey().equals(name)).map(Map.Entry::getValue).toList();
  }

  private static JupiterSwapBuildRequest parse(final String json) {
    return JupiterSwapBuildRequest.parseRequest(JsonIterator.parse(json.getBytes(UTF_8)));
  }

  private static JupiterSwapBuildRequest parse(final JupiterSwapBuildRequest prototype, final String json) {
    return JupiterSwapBuildRequest.parseRequest(prototype, JsonIterator.parse(json.getBytes(UTF_8)));
  }

  private static void assertMissing(final String expectedMessage, final JupiterSwapBuildRequest.Builder builder) {
    assertEquals(expectedMessage, assertThrows(IllegalStateException.class, builder::serialize).getMessage());
    final var request = builder.createRequest();
    assertEquals(expectedMessage, assertThrows(IllegalStateException.class, request::serialize).getMessage());
  }

  private static void assertContradiction(final String expectedMessage, final JupiterSwapBuildRequest.Builder builder) {
    assertEquals(expectedMessage, assertThrows(IllegalStateException.class, builder::createRequest).getMessage());
    assertEquals(expectedMessage, assertThrows(IllegalStateException.class, builder::serialize).getMessage());
  }

  /// Asserts each accessor against the B2 values.
  private static void assertFullFields(final JupiterSwapBuildRequest request) {
    assertEquals(WSOL, request.inputMint());
    assertEquals(USDC, request.outputMint());
    assertEquals(100_000_000L, request.amount());
    assertEquals(TAKER, request.taker());
    assertEquals("0", request.slippageBps());
    assertTrue(request.fastMode());
    assertEquals(List.of("Meteora DLMM", "Orca V2"), request.dexes());
    assertEquals(List.of(), request.excludeDexes());
    assertEquals(20, request.platformFeeBps());
    assertEquals(FEE_ACCOUNT, request.feeAccount());
    assertEquals(40, request.maxAccounts());
    assertEquals(PAYER, request.payer());
    assertFalse(request.wrapAndUnwrapSol());
    assertEquals(DEST, request.destinationTokenAccount());
    assertNull(request.nativeDestinationAccount());
    assertEquals(300, request.blockhashSlotsToExpiry());
    assertEquals(1_000_000L, request.tipAmount());
    assertEquals("veryHigh", request.computeUnitPricePercentile());
    assertTrue(request.forJitoBundle());
  }

  /// Asserts each accessor against the B3 values; everything B3 leaves unset reads as its default.
  private static void assertAlternativeFields(final JupiterSwapBuildRequest request) {
    assertEquals(WSOL, request.inputMint());
    assertEquals(USDC, request.outputMint());
    assertEquals(-1L, request.amount());
    assertEquals(TAKER, request.taker());
    assertEquals("rtse", request.slippageBps());
    assertFalse(request.fastMode());
    assertEquals(List.of(), request.dexes());
    assertEquals(List.of("SolFi"), request.excludeDexes());
    assertEquals(0, request.platformFeeBps());
    assertNull(request.feeAccount());
    assertEquals(0, request.maxAccounts());
    assertNull(request.payer());
    assertTrue(request.wrapAndUnwrapSol());
    assertNull(request.destinationTokenAccount());
    assertEquals(OTHER, request.nativeDestinationAccount());
    assertEquals(0, request.blockhashSlotsToExpiry());
    assertEquals(0L, request.tipAmount());
    assertEquals("7500", request.computeUnitPricePercentile());
    assertFalse(request.forJitoBundle());
  }

  /// Asserts the state of a builder nothing has been set on.
  private static void assertEmpty(final JupiterSwapBuildRequest request) {
    assertNull(request.inputMint());
    assertNull(request.outputMint());
    assertEquals(0L, request.amount());
    assertNull(request.taker());
    assertNull(request.slippageBps());
    assertFalse(request.fastMode());
    assertEquals(List.of(), request.dexes());
    assertEquals(List.of(), request.excludeDexes());
    assertEquals(0, request.platformFeeBps());
    assertNull(request.feeAccount());
    assertEquals(0, request.maxAccounts());
    assertNull(request.payer());
    assertTrue(request.wrapAndUnwrapSol());
    assertNull(request.destinationTokenAccount());
    assertNull(request.nativeDestinationAccount());
    assertEquals(0, request.blockhashSlotsToExpiry());
    assertEquals(0L, request.tipAmount());
    assertNull(request.computeUnitPricePercentile());
    assertFalse(request.forJitoBundle());
  }

  @Test
  void requiredParametersLeadInSpecOrder() {
    final var builder = minimal();
    assertEquals(MINIMAL_QUERY, builder.serialize());
    assertEquals(MINIMAL_QUERY, builder.createRequest().serialize());
  }

  @Test
  void everyParameterIsEmittedUnderItsApiNameInSpecOrder() {
    final var builder = full();
    assertEquals(FULL_QUERY, builder.serialize());
    assertEquals(FULL_QUERY, builder.createRequest().serialize());
  }

  @Test
  void theAlternativeParametersSerializeInTheirSlots() {
    final var builder = alternative();
    assertEquals(ALTERNATIVE_QUERY, builder.serialize());
    assertEquals(ALTERNATIVE_QUERY, builder.createRequest().serialize());
  }

  /// An unset optional leaves Jupiter's default in force, and setting one to its default is the same as leaving
  /// it unset.
  @Test
  void unsetOptionalsAreOmitted() {
    assertEquals(List.of("inputMint", "outputMint", "amount", "taker"), names(minimal().createRequest().serialize()));

    final var explicitDefaults = minimal()
        .defaultSlippage()
        .fastMode(false)
        .dexes(List.of())
        .excludeDexes(null)
        .platformFeeBps(0)
        .feeAccount(null)
        .maxAccounts(0)
        .payer(null)
        .wrapAndUnwrapSol(true)
        .destinationTokenAccount(null)
        .nativeDestinationAccount(null)
        .blockhashSlotsToExpiry(0)
        .tipAmount(0)
        .computeUnitPricePercentile(null)
        .forJitoBundle(false);
    assertEquals(List.of("inputMint", "outputMint", "amount", "taker"), names(explicitDefaults.createRequest().serialize()));
  }

  @Test
  void slippageZeroIsSentRatherThanOmitted() {
    final var zero = minimal().slippageBps(0);
    assertEquals("0", zero.slippageBps());
    assertEquals(MINIMAL_QUERY + "&slippageBps=0", zero.createRequest().serialize());

    assertEquals(List.of("1"), values(minimal().slippageBps(1).serialize(), "slippageBps"));

    // unset sends nothing, leaving Jupiter's default of 50
    assertNull(minimal().slippageBps());
    assertEquals(List.of(), values(minimal().serialize(), "slippageBps"));
  }

  @Test
  void rtseAndBasisPointSlippageReplaceEachOther() {
    final var rtseThenFixed = minimal().rtseSlippage().slippageBps(25);
    assertEquals("25", rtseThenFixed.slippageBps());
    assertEquals(List.of("25"), values(rtseThenFixed.createRequest().serialize(), "slippageBps"));

    final var fixedThenRtse = minimal().slippageBps(25).rtseSlippage();
    assertEquals("rtse", fixedThenRtse.slippageBps());
    assertEquals(List.of("rtse"), values(fixedThenRtse.createRequest().serialize(), "slippageBps"));

    // and defaultSlippage() replaces either with nothing sent, leaving Jupiter's default of 50
    final var fixedThenDefault = minimal().slippageBps(25).defaultSlippage();
    assertNull(fixedThenDefault.slippageBps());
    assertEquals(MINIMAL_QUERY, fixedThenDefault.createRequest().serialize());

    final var rtseThenDefault = minimal().rtseSlippage().defaultSlippage();
    assertNull(rtseThenDefault.slippageBps());
    assertEquals(MINIMAL_QUERY, rtseThenDefault.createRequest().serialize());
  }

  @Test
  void computeUnitPricePercentileSendsALevelNameOrBasisPoints() {
    final var levels = new ComputeUnitPriceLevel[]{
        ComputeUnitPriceLevel.medium, ComputeUnitPriceLevel.high, ComputeUnitPriceLevel.veryHigh
    };
    final var wireNames = new String[]{"medium", "high", "veryHigh"};
    for (int i = 0; i < levels.length; ++i) {
      final var builder = minimal().computeUnitPricePercentile(levels[i]);
      assertEquals(wireNames[i], builder.computeUnitPricePercentile());
      assertEquals(MINIMAL_QUERY + "&computeUnitPricePercentile=" + wireNames[i], builder.createRequest().serialize());
    }

    final var zeroBps = minimal().computeUnitPricePercentileBps(0);
    assertEquals("0", zeroBps.computeUnitPricePercentile());
    assertEquals(MINIMAL_QUERY + "&computeUnitPricePercentile=0", zeroBps.serialize());
    assertEquals(List.of("7500"), values(minimal().computeUnitPricePercentileBps(7500).serialize(), "computeUnitPricePercentile"));

    // the last setter wins, in both directions
    final var levelThenBps = minimal().computeUnitPricePercentile(ComputeUnitPriceLevel.high).computeUnitPricePercentileBps(7500);
    assertEquals(List.of("7500"), values(levelThenBps.serialize(), "computeUnitPricePercentile"));
    final var bpsThenLevel = minimal().computeUnitPricePercentileBps(7500).computeUnitPricePercentile(ComputeUnitPriceLevel.high);
    assertEquals(List.of("high"), values(bpsThenLevel.serialize(), "computeUnitPricePercentile"));

    // a null level clears either form
    final var clearedLevel = minimal().computeUnitPricePercentile(ComputeUnitPriceLevel.veryHigh).computeUnitPricePercentile(null);
    assertNull(clearedLevel.computeUnitPricePercentile());
    assertEquals(MINIMAL_QUERY, clearedLevel.serialize());
    final var clearedBps = minimal().computeUnitPricePercentileBps(7500).computeUnitPricePercentile(null);
    assertNull(clearedBps.computeUnitPricePercentile());
    assertEquals(MINIMAL_QUERY, clearedBps.serialize());
  }

  @Test
  void amountIsSentUnsigned() {
    assertEquals(List.of("18446744073709551615"), values(minimal().amount(-1L).serialize(), "amount"));
    assertEquals(List.of("9223372036854775808"), values(minimal().amount(Long.MIN_VALUE).serialize(), "amount"));
    assertEquals(List.of("9223372036854775807"), values(minimal().amount(Long.MAX_VALUE).serialize(), "amount"));
    assertEquals(List.of("1"), values(minimal().amount(1).serialize(), "amount"));

    // the long keeps the bit pattern
    assertEquals(-1L, minimal().amount(-1L).createRequest().amount());
  }

  @Test
  void zeroOmitsTheParametersWhoseZeroIsNeverSent() {
    // platformFeeBps, with the feeAccount a positive fee needs
    assertEquals(
        MINIMAL_QUERY + "&feeAccount=CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48",
        minimal().feeAccount(FEE_ACCOUNT).platformFeeBps(0).createRequest().serialize()
    );
    assertEquals(
        MINIMAL_QUERY + "&platformFeeBps=1&feeAccount=CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48",
        minimal().feeAccount(FEE_ACCOUNT).platformFeeBps(1).createRequest().serialize()
    );

    assertEquals(MINIMAL_QUERY, minimal().maxAccounts(0).createRequest().serialize());
    assertEquals(MINIMAL_QUERY + "&maxAccounts=1", minimal().maxAccounts(1).createRequest().serialize());

    assertEquals(MINIMAL_QUERY, minimal().blockhashSlotsToExpiry(0).createRequest().serialize());
    assertEquals(MINIMAL_QUERY + "&blockhashSlotsToExpiry=1", minimal().blockhashSlotsToExpiry(1).createRequest().serialize());

    assertEquals(MINIMAL_QUERY, minimal().tipAmount(0).createRequest().serialize());
    assertEquals(MINIMAL_QUERY + "&tipAmount=1", minimal().tipAmount(1).createRequest().serialize());

    // 0 also clears a value set earlier
    assertEquals(MINIMAL_QUERY, minimal().maxAccounts(40).maxAccounts(0).serialize());
    assertEquals(MINIMAL_QUERY, minimal().tipAmount(1_000_000).tipAmount(0).serialize());
  }

  @Test
  void negativeValuesAreRejectedBySetters() {
    final var builder = minimal();
    assertEquals(
        "slippageBps must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> builder.slippageBps(-1)).getMessage()
    );
    assertEquals(
        "platformFeeBps must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> builder.platformFeeBps(-1)).getMessage()
    );
    assertEquals(
        "maxAccounts must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> builder.maxAccounts(-1)).getMessage()
    );
    assertEquals(
        "blockhashSlotsToExpiry must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> builder.blockhashSlotsToExpiry(-1)).getMessage()
    );
    assertEquals(
        "tipAmount must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> builder.tipAmount(-1L)).getMessage()
    );
    assertEquals(
        "computeUnitPricePercentile must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> builder.computeUnitPricePercentileBps(-1)).getMessage()
    );
    // a rejected value leaves the builder as it was
    assertEquals(MINIMAL_QUERY, builder.serialize());

    // 0 is accepted by all six
    final var zeros = minimal()
        .slippageBps(0)
        .platformFeeBps(0)
        .maxAccounts(0)
        .blockhashSlotsToExpiry(0)
        .tipAmount(0)
        .computeUnitPricePercentileBps(0);
    assertEquals("0", zeros.slippageBps());
    assertEquals(0, zeros.platformFeeBps());
    assertEquals(0, zeros.maxAccounts());
    assertEquals(0, zeros.blockhashSlotsToExpiry());
    assertEquals(0L, zeros.tipAmount());
    assertEquals("0", zeros.computeUnitPricePercentile());
    assertEquals(MINIMAL_QUERY + "&slippageBps=0&computeUnitPricePercentile=0", zeros.createRequest().serialize());
  }

  @Test
  void labelsAreJoinedAndUtf8Encoded() {
    final var dexes = minimal().dexes(List.of("Meteora DLMM", "Orca V2"));
    assertEquals(List.of("Meteora DLMM", "Orca V2"), dexes.dexes());
    assertEquals(List.of("Meteora+DLMM%2COrca+V2"), values(dexes.createRequest().serialize(), "dexes"));
    // the caller's order is kept, never sorted, from any collection
    assertEquals(
        List.of("Orca+V2%2CMeteora+DLMM"),
        values(minimal().dexes(new LinkedHashSet<>(List.of("Orca V2", "Meteora DLMM"))).serialize(), "dexes")
    );
    // non-ASCII is UTF-8 percent-encoded, and a label is sent with its case
    assertEquals(List.of("H%C3%A9llo"), values(minimal().dexes(List.of("Héllo")).serialize(), "dexes"));

    final var excludeDexes = minimal().excludeDexes(List.of("Meteora DLMM", "Orca V2"));
    assertEquals(List.of("Meteora DLMM", "Orca V2"), excludeDexes.excludeDexes());
    assertEquals(List.of("Meteora+DLMM%2COrca+V2"), values(excludeDexes.createRequest().serialize(), "excludeDexes"));
    assertEquals(
        List.of("Orca+V2%2CMeteora+DLMM"),
        values(minimal().excludeDexes(List.of("Orca V2", "Meteora DLMM")).serialize(), "excludeDexes")
    );
    assertEquals(List.of("H%C3%A9llo"), values(minimal().excludeDexes(List.of("Héllo")).serialize(), "excludeDexes"));
  }

  @Test
  void aHostileLabelCannotAddAParameter() {
    final var dexes = minimal().slippageBps(50).dexes(List.of("Orca&slippageBps=9999")).serialize();
    assertEquals(List.of("50"), values(dexes, "slippageBps"));
    assertEquals(List.of("Orca%26slippageBps%3D9999"), values(dexes, "dexes"));

    final var excludeDexes = minimal().slippageBps(50).excludeDexes(List.of("Orca&slippageBps=9999")).serialize();
    assertEquals(List.of("50"), values(excludeDexes, "slippageBps"));
    assertEquals(List.of("Orca%26slippageBps%3D9999"), values(excludeDexes, "excludeDexes"));
  }

  @Test
  void labelsThatCannotBeSentAreRejected() {
    final var builder = minimal().dexes(List.of("Orca V2")).excludeDexes(List.of("SolFi"));
    final List<List<String>> unsendable = List.of(
        Arrays.asList("Orca", null),
        List.of(""),
        List.of(" "),
        List.of("Orca,V2"),
        List.of(",Orca"),
        // a padded label can be sent, but Jupiter does not trim labels, so it names no DEX
        List.of(" Orca"),
        List.of("Orca "),
        List.of("Orca\t"),
        // padding with non-ASCII whitespace too: an ideographic space and a line separator
        List.of("Orca V2" + Character.toString(0x3000)),
        List.of(Character.toString(0x2028) + "Orca"),
        // and with the no-break spaces String#strip() keeps: U+00A0 leading, trailing or alone, U+2007 and U+202F
        List.of(Character.toString(0x00A0) + "Orca V2"),
        List.of("Orca V2" + Character.toString(0x00A0)),
        List.of(Character.toString(0x00A0)),
        List.of(Character.toString(0x2007) + "Orca"),
        List.of("Orca" + Character.toString(0x202F)),
        Arrays.asList("Orca V2", " SolFi")
    );
    for (final var labels : unsendable) {
      assertThrows(IllegalArgumentException.class, () -> builder.dexes(labels), labels::toString);
      assertThrows(IllegalArgumentException.class, () -> builder.excludeDexes(labels), labels::toString);
    }
    // a rejected list leaves the previous one in place
    assertEquals(List.of("Orca V2"), builder.dexes());
    assertEquals(List.of("SolFi"), builder.excludeDexes());

    // the message names the parameter and the value
    assertEquals(
        "dexes must not contain null",
        assertThrows(IllegalArgumentException.class, () -> builder.dexes(Arrays.asList("Orca", null))).getMessage()
    );
    assertEquals(
        "excludeDexes must not contain a blank value: \" \"",
        assertThrows(IllegalArgumentException.class, () -> builder.excludeDexes(List.of(" "))).getMessage()
    );
    assertEquals(
        "dexes must not contain a blank value: \"\"",
        assertThrows(IllegalArgumentException.class, () -> builder.dexes(List.of(""))).getMessage()
    );
    assertEquals(
        "excludeDexes must not contain a value with a comma: \"Orca,V2\"",
        assertThrows(IllegalArgumentException.class, () -> builder.excludeDexes(List.of("Orca,V2"))).getMessage()
    );
    assertEquals(
        "dexes must not contain a value with a comma: \",Orca\"",
        assertThrows(IllegalArgumentException.class, () -> builder.dexes(List.of(",Orca"))).getMessage()
    );
    assertEquals(
        "dexes must not have leading or trailing whitespace: \" Orca\"",
        assertThrows(IllegalArgumentException.class, () -> builder.dexes(List.of(" Orca"))).getMessage()
    );
    assertEquals(
        "excludeDexes must not have leading or trailing whitespace: \"Orca \"",
        assertThrows(IllegalArgumentException.class, () -> builder.excludeDexes(List.of("Orca "))).getMessage()
    );
  }

  @Test
  void nullLabelCollectionsClearAndCollectionsAreCopied() {
    final var dexes = minimal().dexes(List.of("Orca V2"));
    assertSame(dexes, dexes.dexes(null));
    assertEquals(List.of(), dexes.dexes());
    assertEquals(MINIMAL_QUERY, dexes.serialize());

    final var excludeDexes = minimal().excludeDexes(List.of("SolFi"));
    assertSame(excludeDexes, excludeDexes.excludeDexes(null));
    assertEquals(List.of(), excludeDexes.excludeDexes());
    assertEquals(MINIMAL_QUERY, excludeDexes.serialize());

    // a later change to the caller's collection does not reach the request
    final var labels = new ArrayList<>(List.of("Meteora DLMM"));
    final var copiedDexes = minimal().dexes(labels);
    final var copiedExcludeDexes = minimal().excludeDexes(labels);
    labels.add("Orca V2");
    assertEquals(List.of("Meteora DLMM"), copiedDexes.dexes());
    assertEquals(List.of("Meteora+DLMM"), values(copiedDexes.serialize(), "dexes"));
    assertEquals(List.of("Meteora DLMM"), copiedExcludeDexes.excludeDexes());
    assertEquals(List.of("Meteora+DLMM"), values(copiedExcludeDexes.serialize(), "excludeDexes"));

    // nor can the lists handed out change it
    assertThrows(UnsupportedOperationException.class, () -> copiedDexes.dexes().add("Orca V2"));
    assertThrows(UnsupportedOperationException.class, () -> copiedDexes.createRequest().dexes().add("Orca V2"));
    assertThrows(UnsupportedOperationException.class, () -> copiedExcludeDexes.excludeDexes().add("Orca V2"));
    assertThrows(UnsupportedOperationException.class, () -> copiedExcludeDexes.createRequest().excludeDexes().add("Orca V2"));

    // an empty builder holds empty lists, never null
    assertEquals(List.of(), JupiterSwapBuildRequest.buildRequest().dexes());
    assertEquals(List.of(), JupiterSwapBuildRequest.buildRequest().excludeDexes());
  }

  @Test
  void wrapAndUnwrapSolIsSentOnlyWhenDisabled() {
    assertTrue(JupiterSwapBuildRequest.buildRequest().wrapAndUnwrapSol());
    assertTrue(minimal().createRequest().wrapAndUnwrapSol());
    assertEquals(MINIMAL_QUERY, minimal().wrapAndUnwrapSol(true).createRequest().serialize());

    final var disabled = minimal().wrapAndUnwrapSol(false);
    assertFalse(disabled.wrapAndUnwrapSol());
    assertFalse(disabled.createRequest().wrapAndUnwrapSol());
    assertEquals(MINIMAL_QUERY + "&wrapAndUnwrapSol=false", disabled.createRequest().serialize());

    // re-enabling takes it off the query again
    assertEquals(MINIMAL_QUERY, minimal().wrapAndUnwrapSol(false).wrapAndUnwrapSol(true).serialize());
  }

  @Test
  void fastModeAndForJitoBundleAreSentOnlyWhenTrue() {
    assertFalse(JupiterSwapBuildRequest.buildRequest().fastMode());
    assertFalse(JupiterSwapBuildRequest.buildRequest().forJitoBundle());

    assertEquals(MINIMAL_QUERY + "&mode=fast", minimal().fastMode(true).createRequest().serialize());
    assertEquals(MINIMAL_QUERY + "&forJitoBundle=true", minimal().forJitoBundle(true).createRequest().serialize());
    assertEquals(MINIMAL_QUERY + "&mode=fast&forJitoBundle=true", minimal().fastMode(true).forJitoBundle(true).serialize());

    assertEquals(MINIMAL_QUERY, minimal().fastMode(false).forJitoBundle(false).serialize());
    assertEquals(MINIMAL_QUERY, minimal().fastMode(true).fastMode(false).forJitoBundle(true).forJitoBundle(false).serialize());
  }

  @Test
  void missingRequiredParametersAreRejectedBySerialize() {
    assertMissing(
        "/swap/v2/build requires inputMint",
        JupiterSwapBuildRequest.buildRequest().outputMint(USDC).amount(100_000_000).taker(TAKER)
    );
    assertMissing(
        "/swap/v2/build requires outputMint",
        JupiterSwapBuildRequest.buildRequest().inputMint(WSOL).amount(100_000_000).taker(TAKER)
    );
    assertMissing(
        "/swap/v2/build requires amount",
        JupiterSwapBuildRequest.buildRequest().inputMint(WSOL).outputMint(USDC).taker(TAKER)
    );
    assertMissing("/swap/v2/build requires amount", minimal().amount(0));
    assertMissing(
        "/swap/v2/build requires taker",
        JupiterSwapBuildRequest.buildRequest().inputMint(WSOL).outputMint(USDC).amount(100_000_000)
    );

    // the first missing parameter is named, in parameter order
    assertMissing("/swap/v2/build requires inputMint", JupiterSwapBuildRequest.buildRequest());
    assertMissing("/swap/v2/build requires outputMint", JupiterSwapBuildRequest.buildRequest().inputMint(WSOL));

    // and a missing parameter is reported before a contradiction
    final var incompleteAndContradictory = JupiterSwapBuildRequest.buildRequest()
        .inputMint(WSOL)
        .outputMint(USDC)
        .amount(100_000_000)
        .dexes(List.of("Orca V2"))
        .excludeDexes(List.of("SolFi"));
    assertEquals(
        "/swap/v2/build requires taker",
        assertThrows(IllegalStateException.class, incompleteAndContradictory::serialize).getMessage()
    );
  }

  @Test
  void contradictionsAreRejectedByCreateRequestAndSerialize() {
    assertContradiction(
        "dexes and excludeDexes are mutually exclusive",
        minimal().dexes(List.of("Orca V2")).excludeDexes(List.of("SolFi"))
    );
    assertContradiction(
        "destinationTokenAccount and nativeDestinationAccount are mutually exclusive",
        minimal().destinationTokenAccount(DEST).nativeDestinationAccount(OTHER)
    );
    assertContradiction("platformFeeBps requires feeAccount", minimal().platformFeeBps(1));

    // a zero platform fee needs no fee account
    assertEquals(MINIMAL_QUERY, minimal().platformFeeBps(0).createRequest().serialize());
    // a fee account without a fee is allowed, and sent
    assertEquals(
        MINIMAL_QUERY + "&feeAccount=CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48",
        minimal().feeAccount(FEE_ACCOUNT).createRequest().serialize()
    );
    // an empty list is no conflict
    assertEquals(
        MINIMAL_QUERY + "&excludeDexes=SolFi",
        minimal().dexes(List.of()).excludeDexes(List.of("SolFi")).createRequest().serialize()
    );
    assertEquals(
        MINIMAL_QUERY + "&dexes=Orca+V2",
        minimal().dexes(List.of("Orca V2")).excludeDexes(List.of()).createRequest().serialize()
    );
    // either destination alone is fine
    assertEquals(
        MINIMAL_QUERY + "&destinationTokenAccount=HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR",
        minimal().destinationTokenAccount(DEST).createRequest().serialize()
    );
    assertEquals(
        MINIMAL_QUERY + "&nativeDestinationAccount=9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn",
        minimal().nativeDestinationAccount(OTHER).createRequest().serialize()
    );
  }

  /// The ranges Jupiter enforces are left to Jupiter: draft SIMD-0596 would already allow 96 accounts.
  @Test
  void serverOwnedRangesAreSentAsGiven() {
    final var query = minimal()
        .maxAccounts(96)
        .blockhashSlotsToExpiry(301)
        .slippageBps(10001)
        .platformFeeBps(10001)
        .feeAccount(FEE_ACCOUNT)
        .computeUnitPricePercentileBps(10001)
        .createRequest()
        .serialize();
    assertEquals(
        MINIMAL_QUERY + "&slippageBps=10001&platformFeeBps=10001&feeAccount=CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48&maxAccounts=96&blockhashSlotsToExpiry=301&computeUnitPricePercentile=10001",
        query
    );
  }

  @Test
  void serverOwnedRulesAreSentAsGiven() {
    final var payerIsTaker = minimal().payer(TAKER).createRequest().serialize();
    assertEquals(List.of("GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ"), values(payerIsTaker, "payer"));
    assertEquals(List.of("GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ"), values(payerIsTaker, "taker"));

    final var sameMint = JupiterSwapBuildRequest.buildRequest()
        .inputMint(WSOL)
        .outputMint(WSOL)
        .amount(100_000_000)
        .taker(TAKER)
        .createRequest()
        .serialize();
    assertEquals(List.of("So11111111111111111111111111111111111111112"), values(sameMint, "inputMint"));
    assertEquals(List.of("So11111111111111111111111111111111111111112"), values(sameMint, "outputMint"));
  }

  @Test
  void everySetterReturnsItsBuilder() {
    final var builder = JupiterSwapBuildRequest.buildRequest();
    assertSame(builder, builder.inputMint(WSOL));
    assertSame(builder, builder.outputMint(USDC));
    assertSame(builder, builder.amount(100_000_000));
    assertSame(builder, builder.taker(TAKER));
    assertSame(builder, builder.slippageBps(50));
    assertSame(builder, builder.rtseSlippage());
    assertSame(builder, builder.defaultSlippage());
    assertSame(builder, builder.fastMode(true));
    assertSame(builder, builder.dexes(List.of("Orca V2")));
    assertSame(builder, builder.excludeDexes(List.of("SolFi")));
    assertSame(builder, builder.platformFeeBps(20));
    assertSame(builder, builder.feeAccount(FEE_ACCOUNT));
    assertSame(builder, builder.maxAccounts(40));
    assertSame(builder, builder.payer(PAYER));
    assertSame(builder, builder.wrapAndUnwrapSol(false));
    assertSame(builder, builder.destinationTokenAccount(DEST));
    assertSame(builder, builder.nativeDestinationAccount(OTHER));
    assertSame(builder, builder.blockhashSlotsToExpiry(300));
    assertSame(builder, builder.tipAmount(1_000_000));
    assertSame(builder, builder.computeUnitPricePercentile(ComputeUnitPriceLevel.high));
    assertSame(builder, builder.computeUnitPricePercentileBps(7500));
    assertSame(builder, builder.forJitoBundle(true));
  }

  @Test
  void prototypeBuilderCarriesEveryFieldForward() {
    // B2 and B3 together set all 19 parameters, the keys distinct and every value off its default
    final var full = full().createRequest();
    assertEquals(FULL_QUERY, full.serialize());
    assertEquals(FULL_QUERY, JupiterSwapBuildRequest.buildRequest(full).createRequest().serialize());
    assertFullFields(JupiterSwapBuildRequest.buildRequest(full));

    final var alternative = alternative().createRequest();
    assertEquals(ALTERNATIVE_QUERY, JupiterSwapBuildRequest.buildRequest(alternative).createRequest().serialize());
    assertAlternativeFields(JupiterSwapBuildRequest.buildRequest(alternative));

    // B2 sets fastMode and forJitoBundle both true and B3 neither, so only a prototype holding one without the
    // other shows each is copied from its own field
    final var fastOnly = JupiterSwapBuildRequest.buildRequest(minimal().fastMode(true).createRequest());
    assertTrue(fastOnly.fastMode());
    assertFalse(fastOnly.forJitoBundle());
    assertEquals(MINIMAL_QUERY + "&mode=fast", fastOnly.createRequest().serialize());

    final var jitoOnly = JupiterSwapBuildRequest.buildRequest(minimal().forJitoBundle(true).createRequest());
    assertFalse(jitoOnly.fastMode());
    assertTrue(jitoOnly.forJitoBundle());
    assertEquals(MINIMAL_QUERY + "&forJitoBundle=true", jitoOnly.createRequest().serialize());

    // overriding one parameter changes only that pair
    assertEquals(
        FULL_QUERY_WITH_MAX_ACCOUNTS_20,
        JupiterSwapBuildRequest.buildRequest(full).maxAccounts(20).createRequest().serialize()
    );
    assertEquals(40, full.maxAccounts());

    // a builder made from a builder is a copy
    final var original = minimal().maxAccounts(40);
    final var copy = JupiterSwapBuildRequest.buildRequest(original).maxAccounts(20);
    assertNotSame(original, copy);
    assertEquals(40, original.maxAccounts());
    assertEquals(20, copy.maxAccounts());

    // no prototype gives an empty builder
    final var empty = JupiterSwapBuildRequest.buildRequest(null);
    assertEmpty(empty);
    assertEquals(
        "/swap/v2/build requires inputMint",
        assertThrows(IllegalStateException.class, empty::serialize).getMessage()
    );
    assertEmpty(JupiterSwapBuildRequest.buildRequest());
  }

  @Test
  void builderAndRecordAccessorsReportEverySetField() {
    final var full = full();
    assertFullFields(full);
    final var request = full.createRequest();
    assertFullFields(request);
    // the request is a copy: changing the builder afterwards does not reach it
    full.maxAccounts(20).dexes(List.of("SolFi")).payer(OTHER).wrapAndUnwrapSol(true);
    assertFullFields(request);

    final var alternative = alternative();
    assertAlternativeFields(alternative);
    assertAlternativeFields(alternative.createRequest());
  }

  /// Every field lands from its API name, and unknown fields (leading, mid-object and trailing; scalar and
  /// structured, one holding a known name) are skipped without shifting the fields after them.
  @Test
  void parsedRequestReadsEveryFieldPastUnknownNeighbors() {
    final var named = parse("""
        {
          "unknownLeading": {"nested": [1, {"deep": true}]},
          "inputMint": "So11111111111111111111111111111111111111112",
          "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
          "amount": "18446744073709551615",
          "unknownMid": ["s", 2.5, false, null],
          "taker": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ",
          "slippageBps": "rtse",
          "mode": "fast",
          "dexes": ["Meteora DLMM", "Orca V2"],
          "platformFeeBps": 20,
          "feeAccount": "CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48",
          "maxAccounts": 40,
          "unknownObject": {"maxAccounts": 64},
          "payer": "gasTzr94Pmp4Gf8vknQnqxeYxdgwFjbgdJa4msYRpnB",
          "wrapAndUnwrapSol": false,
          "destinationTokenAccount": "HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR",
          "blockhashSlotsToExpiry": 300,
          "tipAmount": 1000000,
          "computeUnitPricePercentile": "veryHigh",
          "forJitoBundle": true,
          "unknownTrailing": "ignored"
        }""");
    assertEquals(WSOL, named.inputMint());
    assertEquals(USDC, named.outputMint());
    assertEquals(-1L, named.amount());
    assertEquals(TAKER, named.taker());
    assertEquals("rtse", named.slippageBps());
    assertTrue(named.fastMode());
    assertEquals(List.of("Meteora DLMM", "Orca V2"), named.dexes());
    assertEquals(List.of(), named.excludeDexes());
    assertEquals(20, named.platformFeeBps());
    assertEquals(FEE_ACCOUNT, named.feeAccount());
    assertEquals(40, named.maxAccounts());
    assertEquals(PAYER, named.payer());
    assertFalse(named.wrapAndUnwrapSol());
    assertEquals(DEST, named.destinationTokenAccount());
    assertNull(named.nativeDestinationAccount());
    assertEquals(300, named.blockhashSlotsToExpiry());
    assertEquals(1_000_000L, named.tipAmount());
    assertEquals("veryHigh", named.computeUnitPricePercentile());
    assertTrue(named.forJitoBundle());
    assertEquals(
        "inputMint=So11111111111111111111111111111111111111112&outputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&amount=18446744073709551615&taker=GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ&slippageBps=rtse&mode=fast&dexes=Meteora+DLMM%2COrca+V2&platformFeeBps=20&feeAccount=CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48&maxAccounts=40&payer=gasTzr94Pmp4Gf8vknQnqxeYxdgwFjbgdJa4msYRpnB&wrapAndUnwrapSol=false&destinationTokenAccount=HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR&blockhashSlotsToExpiry=300&tipAmount=1000000&computeUnitPricePercentile=veryHigh&forJitoBundle=true",
        named.serialize()
    );

    final var numeric = parse("""
        {
          "computeUnitPricePercentile": 7500,
          "unknownObject": {"slippageBps": 9999},
          "slippageBps": 30,
          "taker": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ",
          "excludeDexes": ["SolFi"],
          "amount": 100000000,
          "nativeDestinationAccount": "9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn",
          "unknownArray": [[1], {"amount": 5}],
          "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
          "inputMint": "So11111111111111111111111111111111111111112"
        }""");
    assertEquals(WSOL, numeric.inputMint());
    assertEquals(USDC, numeric.outputMint());
    assertEquals(100_000_000L, numeric.amount());
    assertEquals(TAKER, numeric.taker());
    assertEquals("30", numeric.slippageBps());
    assertFalse(numeric.fastMode());
    assertEquals(List.of(), numeric.dexes());
    assertEquals(List.of("SolFi"), numeric.excludeDexes());
    assertNull(numeric.destinationTokenAccount());
    assertEquals(OTHER, numeric.nativeDestinationAccount());
    assertTrue(numeric.wrapAndUnwrapSol());
    assertEquals("7500", numeric.computeUnitPricePercentile());
    assertEquals(
        "inputMint=So11111111111111111111111111111111111111112&outputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&amount=100000000&taker=GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ&slippageBps=30&excludeDexes=SolFi&nativeDestinationAccount=9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn&computeUnitPricePercentile=7500",
        numeric.serialize()
    );
  }

  @Test
  void parsedNamedValuesIgnoreCaseAndUnknownNamesAreRejected() {
    assertEquals("rtse", parse("""
        {"slippageBps": "RTSE"}""").slippageBps());
    assertEquals("veryHigh", parse("""
        {"computeUnitPricePercentile": "VeryHigh"}""").computeUnitPricePercentile());
    assertTrue(parse("""
        {"mode": "FAST"}""").fastMode());

    assertThrows(IllegalArgumentException.class, () -> parse("""
        {"slippageBps": "rtes"}"""));
    assertThrows(IllegalArgumentException.class, () -> parse("""
        {"computeUnitPricePercentile": "highest"}"""));
    assertEquals(
        "mode must be fast, ignoring case: slow",
        assertThrows(IllegalArgumentException.class, () -> parse("""
            {"mode": "slow"}""")).getMessage()
    );
    // an empty string is an unknown name, not a JSON null
    assertEquals(
        "mode must be fast, ignoring case: ",
        assertThrows(IllegalArgumentException.class, () -> parse("""
            {"mode": ""}""")).getMessage()
    );
    assertThrows(NumberFormatException.class, () -> parse("""
        {"computeUnitPricePercentile": ""}"""));
    assertThrows(NumberFormatException.class, () -> parse("""
        {"slippageBps": "abc"}"""));

    // a value a setter refuses is refused from a template too
    assertEquals(
        "maxAccounts must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> parse("""
            {"maxAccounts": -1}""")).getMessage()
    );
    // an amount is a u64: no sign, no null, no garbage
    assertThrows(NumberFormatException.class, () -> parse("""
        {"amount": -1}"""));
    assertThrows(NumberFormatException.class, () -> parse("""
        {"amount": null}"""));
    assertThrows(NumberFormatException.class, () -> parse("""
        {"amount": "lots"}"""));
  }

  /// A v1 quote or `/order` template may carry `swapMode`, which `/build` does not take. ExactIn or a JSON null
  /// passes and sends nothing; any other string is refused, ExactOut above all, whose amount `/build` would
  /// otherwise sell.
  @Test
  void aTemplateSwapModeMustBeExactIn() {
    assertEquals(MINIMAL_QUERY, parse(minimal(), """
        {"swapMode": "ExactIn"}""").serialize());
    assertEquals(MINIMAL_QUERY, parse(minimal(), """
        {"swapMode": "exactIN"}""").serialize());
    assertEquals(MINIMAL_QUERY, parse(minimal(), """
        {"swapMode": null}""").serialize());
    // the key is read in place: the fields around it still land
    final var around = parse("""
        {"maxAccounts": 40, "swapMode": "ExactIn", "slippageBps": 30}""");
    assertEquals(40, around.maxAccounts());
    assertEquals("30", around.slippageBps());

    assertEquals(
        "swapMode must be ExactIn, ignoring case, since /swap/v2/build is ExactIn only: ExactOut",
        assertThrows(IllegalArgumentException.class, () -> parse(minimal(), """
            {"swapMode": "ExactOut"}""")).getMessage()
    );
    assertEquals(
        "swapMode must be ExactIn, ignoring case, since /swap/v2/build is ExactIn only: exactout",
        assertThrows(IllegalArgumentException.class, () -> parse("""
            {"swapMode": "exactout"}""")).getMessage()
    );
    // an unknown or empty mode is not taken for ExactIn either
    assertEquals(
        "swapMode must be ExactIn, ignoring case, since /swap/v2/build is ExactIn only: ",
        assertThrows(IllegalArgumentException.class, () -> parse("""
            {"swapMode": ""}""")).getMessage()
    );
    assertThrows(IllegalArgumentException.class, () -> parse("""
        {"swapMode": "ExactInOut"}"""));
  }

  @Test
  void aPartialTemplateParsesAndOnlySerializeRejectsIt() {
    final var template = parse("""
        {"slippageBps":30,"maxAccounts":40}""");
    assertEquals("30", template.slippageBps());
    assertEquals(40, template.maxAccounts());
    assertEquals(
        "/swap/v2/build requires inputMint",
        assertThrows(IllegalStateException.class, template::serialize).getMessage()
    );

    final var completed = JupiterSwapBuildRequest.buildRequest(template)
        .inputMint(WSOL)
        .outputMint(USDC)
        .amount(100_000_000)
        .taker(TAKER);
    assertEquals(MINIMAL_QUERY + "&slippageBps=30&maxAccounts=40", completed.serialize());

    // a JSON null slippage read over it sends nothing, leaving Jupiter's default, and keeps the rest
    final var defaulted = JupiterSwapBuildRequest.parseRequest(template, JsonIterator.parse("""
        {"slippageBps": null}""".getBytes(UTF_8)));
    assertNull(defaulted.slippageBps());
    assertEquals(40, defaulted.maxAccounts());
    assertEquals("30", template.slippageBps());

    // a template read over a prototype keeps what it does not name, and a JSON null clears a key
    final var prototype = minimal().payer(PAYER).feeAccount(FEE_ACCOUNT);
    final var overridden = JupiterSwapBuildRequest.parseRequest(prototype, JsonIterator.parse("""
        {"maxAccounts": 40, "payer": null}""".getBytes(UTF_8)));
    assertEquals(
        MINIMAL_QUERY + "&feeAccount=CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48&maxAccounts=40",
        overridden.serialize()
    );
    // and the prototype itself is untouched
    assertEquals(PAYER, prototype.payer());
    assertEquals(0, prototype.maxAccounts());
  }

  /// A JSON null hands `mode` and `computeUnitPricePercentile` back to Jupiter through their clearing setters, as it
  /// does `slippageBps`, and the prototype keeps its values.
  @Test
  void aJsonNullTurnsFastModeOffAndClearsThePercentile() {
    final var prototype = minimal().fastMode(true).computeUnitPricePercentileBps(7500);
    assertEquals(MINIMAL_QUERY + "&mode=fast&computeUnitPricePercentile=7500", prototype.serialize());

    final var notFast = parse(prototype, """
        {"mode": null}""");
    assertFalse(notFast.fastMode());
    assertEquals(MINIMAL_QUERY + "&computeUnitPricePercentile=7500", notFast.serialize());

    final var noBps = parse(prototype, """
        {"computeUnitPricePercentile": null}""");
    assertNull(noBps.computeUnitPricePercentile());
    assertEquals(MINIMAL_QUERY + "&mode=fast", noBps.serialize());

    // a named level is cleared the same way
    final var levelPrototype = minimal().computeUnitPricePercentile(ComputeUnitPriceLevel.veryHigh);
    final var noLevel = parse(levelPrototype, """
        {"computeUnitPricePercentile": null}""");
    assertNull(noLevel.computeUnitPricePercentile());
    assertEquals(MINIMAL_QUERY, noLevel.serialize());

    // and the prototypes are untouched
    assertTrue(prototype.fastMode());
    assertEquals("7500", prototype.computeUnitPricePercentile());
    assertEquals("veryHigh", levelPrototype.computeUnitPricePercentile());
  }

  /// The rule `parseRequest` documents: a JSON null unsets a parameter, except the ones 0 or a default unsets, which
  /// throw on it. So one template hands every parameter of B2, and of B3, back to Jupiter.
  @Test
  void aJsonNullUnsetsAParameterUnlessZeroOrADefaultDoes() {
    final var unsetEverything = """
        {
          "inputMint": null,
          "outputMint": null,
          "amount": 0,
          "taker": null,
          "slippageBps": null,
          "mode": null,
          "dexes": null,
          "excludeDexes": null,
          "platformFeeBps": 0,
          "feeAccount": null,
          "maxAccounts": 0,
          "payer": null,
          "wrapAndUnwrapSol": true,
          "destinationTokenAccount": null,
          "nativeDestinationAccount": null,
          "blockhashSlotsToExpiry": 0,
          "tipAmount": 0,
          "computeUnitPricePercentile": null,
          "forJitoBundle": false
        }""";
    final var full = full();
    assertEmpty(parse(full, unsetEverything));
    assertFullFields(full);
    final var alternative = alternative();
    assertEmpty(parse(alternative, unsetEverything));
    assertAlternativeFields(alternative);

    // the numbers and booleans that 0 or a default unsets throw on a JSON null
    final var refusingNull = List.of(
        "platformFeeBps", "maxAccounts", "blockhashSlotsToExpiry", "tipAmount", "wrapAndUnwrapSol", "forJitoBundle"
    );
    for (final var name : refusingNull) {
      assertThrows(JsonException.class, () -> parse("{\"" + name + "\": null}"), name);
    }
    assertThrows(NumberFormatException.class, () -> parse("""
        {"amount": null}"""));
  }

  @Test
  void parsedContradictionsAreRejected() {
    assertEquals(
        "dexes and excludeDexes are mutually exclusive",
        assertThrows(IllegalStateException.class, () -> parse("""
            {"dexes":["A"],"excludeDexes":["B"]}""")).getMessage()
    );
    assertEquals(
        "destinationTokenAccount and nativeDestinationAccount are mutually exclusive",
        assertThrows(IllegalStateException.class, () -> parse("""
            {
              "destinationTokenAccount": "HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR",
              "nativeDestinationAccount": "9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn"
            }""")).getMessage()
    );
    assertEquals(
        "platformFeeBps requires feeAccount",
        assertThrows(IllegalStateException.class, () -> parse("""
            {"platformFeeBps": 20}""")).getMessage()
    );
  }
}

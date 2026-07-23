package software.sava.idl.clients.jupiter.swap.rest.request;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

/// Covers the quote-request builder and its query-string serialization.
///
/// This string *is* the request: a dropped parameter silently reverts the
/// quote to a Jupiter-side default (a different slippage, a different route
/// restriction), and a misspelled key is ignored by the API rather than
/// rejected. So every parameter is asserted by name, and every optional one is
/// asserted to be absent when unset — an always-emitted parameter is just as
/// wrong as a missing one.
final class JupiterQuoteRequestTests {

  private static final PublicKey INPUT_MINT =
      PublicKey.fromBase58Encoded("So11111111111111111111111111111111111111112");
  private static final PublicKey OUTPUT_MINT =
      PublicKey.fromBase58Encoded("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");

  private static JupiterQuoteRequest.Builder minimal() {
    return JupiterQuoteRequest.buildRequest()
        .inputTokenMint(INPUT_MINT)
        .outputTokenMint(OUTPUT_MINT);
  }

  private static List<String> params(final String query) {
    return Arrays.asList(query.split("&"));
  }

  @Test
  void mintsAreAlwaysEmittedUnderTheApiParameterNames() {
    final var query = minimal().create().serialize();

    // the API names differ from the accessor names: inputMint, not inputTokenMint
    assertEquals("inputMint=" + INPUT_MINT.toBase58() + "&outputMint=" + OUTPUT_MINT.toBase58(), query);
    assertTrue(params(query).getFirst().startsWith("inputMint="), "the query must not lead with '&'");
  }

  /// Every optional parameter is omitted at its default, so an unset field
  /// leaves the API default in force rather than pinning a zero.
  @Test
  void unsetOptionalsAreOmitted() {
    final var query = minimal().create().serialize();

    for (final var name : new String[]{
        "amount", "slippageBps", "swapMode", "dexes", "excludeDexes",
        "restrictIntermediateTokens", "onlyDirectRoutes", "asLegacyTransaction",
        "platformFeeBps", "maxAccounts", "instructionVersion"
    }) {
      assertFalse(query.contains(name + "="), name + " must be omitted when unset: " + query);
    }
  }

  @Test
  void eachParameterIsEmittedUnderItsOwnName() {
    final var query = JupiterQuoteRequest.buildRequest()
        .inputTokenMint(INPUT_MINT)
        .outputTokenMint(OUTPUT_MINT)
        .amount(1_500L)
        .slippageBps(50)
        .swapMode(SwapMode.ExactIn)
        .restrictIntermediateTokens(true)
        .onlyDirectRoutes(true)
        .asLegacyTransaction(true)
        .platformFeeBps(10)
        .maxAccounts(32)
        .instructionVersion("v2")
        .create()
        .serialize();

    final var parts = params(query);
    assertTrue(parts.contains("amount=1500"), query);
    assertTrue(parts.contains("slippageBps=50"), query);
    assertTrue(parts.contains("swapMode=ExactIn"), query);
    assertTrue(parts.contains("restrictIntermediateTokens=true"), query);
    assertTrue(parts.contains("onlyDirectRoutes=true"), query);
    assertTrue(parts.contains("asLegacyTransaction=true"), query);
    assertTrue(parts.contains("platformFeeBps=10"), query);
    assertTrue(parts.contains("maxAccounts=32"), query);
    assertTrue(parts.contains("instructionVersion=v2"), query);

    // the numeric parameters carry their own value, not each other's
    assertFalse(parts.contains("slippageBps=10"), "platformFeeBps must not leak into slippageBps");
    assertFalse(parts.contains("platformFeeBps=50"), "slippageBps must not leak into platformFeeBps");
  }

  /// The boolean flags are emitted only when true — there is no `=false` form,
  /// so a flag that always emits would force the opposite of the default.
  @Test
  void booleanFlagsEmitOnlyWhenSet() {
    final var onlyRestrict = minimal().restrictIntermediateTokens(true).create().serialize();
    assertTrue(onlyRestrict.contains("restrictIntermediateTokens=true"));
    assertFalse(onlyRestrict.contains("onlyDirectRoutes"));
    assertFalse(onlyRestrict.contains("asLegacyTransaction"));

    final var onlyDirect = minimal().onlyDirectRoutes(true).create().serialize();
    assertTrue(onlyDirect.contains("onlyDirectRoutes=true"));
    assertFalse(onlyDirect.contains("restrictIntermediateTokens"));

    final var onlyLegacy = minimal().asLegacyTransaction(true).create().serialize();
    assertTrue(onlyLegacy.contains("asLegacyTransaction=true"));
    assertFalse(onlyLegacy.contains("onlyDirectRoutes"));

    // explicitly false is the same as unset
    assertEquals(minimal().create().serialize(),
        minimal().restrictIntermediateTokens(false).onlyDirectRoutes(false).asLegacyTransaction(false).create().serialize());
  }

  /// Numeric parameters are emitted only above zero, so an explicit zero does
  /// not pin a meaningless bound.
  @Test
  void numericParametersAreOmittedAtZero() {
    final var query = minimal()
        .amount(0L)
        .slippageBps(0)
        .platformFeeBps(0)
        .maxAccounts(0)
        .create()
        .serialize();

    assertFalse(query.contains("amount="), query);
    assertFalse(query.contains("slippageBps="), query);
    assertFalse(query.contains("platformFeeBps="), query);
    assertFalse(query.contains("maxAccounts="), query);

    // one above zero is emitted — the boundary is exclusive
    assertTrue(minimal().slippageBps(1).create().serialize().contains("slippageBps=1"));
    assertTrue(minimal().maxAccounts(1).create().serialize().contains("maxAccounts=1"));
    assertTrue(minimal().platformFeeBps(1).create().serialize().contains("platformFeeBps=1"));
    assertTrue(minimal().amount(1L).create().serialize().contains("amount=1"));
  }

  /// The amount is a BigInteger so it can carry a full u64 without wrapping.
  @Test
  void amountCarriesLargeValues() {
    final var u64Max = new BigInteger("18446744073709551615");
    assertTrue(minimal().amount(u64Max).create().serialize().contains("amount=18446744073709551615"));

    // the long convenience overload agrees with the BigInteger form
    assertEquals(
        minimal().amount(BigInteger.valueOf(1_500L)).create().serialize(),
        minimal().amount(1_500L).create().serialize());

    // a negative amount is treated as unset rather than serialized
    assertFalse(minimal().amount(BigInteger.valueOf(-1L)).create().serialize().contains("amount="));
  }

  /// `dexes` and `excludeDexes` are mutually exclusive: an include list wins
  /// and the exclude list is dropped, since sending both is a contradiction.
  @Test
  void dexListsAreMutuallyExclusiveAndUrlEncoded() {
    final var includeOnly = minimal().dexes(List.of("Orca", "Raydium")).create().serialize();
    assertTrue(includeOnly.contains("dexes=Orca%2CRaydium"), includeOnly);
    assertFalse(includeOnly.contains("excludeDexes="), includeOnly);

    final var excludeOnly = minimal().excludeDexes(List.of("Orca", "Raydium")).create().serialize();
    assertTrue(excludeOnly.contains("excludeDexes=Orca%2CRaydium"), excludeOnly);

    // both set: the include list takes precedence
    final var both = minimal()
        .dexes(List.of("Orca"))
        .excludeDexes(List.of("Raydium"))
        .create()
        .serialize();
    assertTrue(both.contains("dexes=Orca"), both);
    assertFalse(both.contains("excludeDexes"), both);

    // an empty list is the same as unset, and falls through to the exclude list
    final var emptyInclude = minimal()
        .dexes(List.of())
        .excludeDexes(List.of("Raydium"))
        .create()
        .serialize();
    assertTrue(emptyInclude.contains("excludeDexes=Raydium"), emptyInclude);

    // the comma separator is encoded, not sent raw
    assertFalse(includeOnly.contains("dexes=Orca,Raydium"), "the separator must be percent-encoded");
  }

  @Test
  void swapModeCarriesItsOwnName() {
    assertTrue(minimal().swapMode(SwapMode.ExactIn).create().serialize().contains("swapMode=ExactIn"));
    assertTrue(minimal().swapMode(SwapMode.ExactOut).create().serialize().contains("swapMode=ExactOut"));
    assertNotEquals(
        minimal().swapMode(SwapMode.ExactIn).create().serialize(),
        minimal().swapMode(SwapMode.ExactOut).create().serialize());
  }

  @Test
  void instructionVersionIsOmittedWhenBlank() {
    assertFalse(minimal().instructionVersion(null).create().serialize().contains("instructionVersion"));
    assertFalse(minimal().instructionVersion("").create().serialize().contains("instructionVersion"));
    assertFalse(minimal().instructionVersion("   ").create().serialize().contains("instructionVersion"));
    assertTrue(minimal().instructionVersion("v2").create().serialize().contains("instructionVersion=v2"));
  }

  // ---------------------------------------------------------------------------
  // builder plumbing
  // ---------------------------------------------------------------------------

  /// Every setter must be readable back — a builder that drops a field
  /// produces a request that serializes without it.
  @Test
  void buildersRoundTripEveryField() {
    final var request = JupiterQuoteRequest.buildRequest()
        .inputTokenMint(INPUT_MINT)
        .outputTokenMint(OUTPUT_MINT)
        .amount(BigInteger.valueOf(1_500L))
        .slippageBps(50)
        .swapMode(SwapMode.ExactOut)
        .dexes(List.of("Orca"))
        .excludeDexes(List.of("Raydium"))
        .restrictIntermediateTokens(true)
        .onlyDirectRoutes(true)
        .asLegacyTransaction(true)
        .platformFeeBps(10)
        .maxAccounts(32)
        .instructionVersion("v2")
        .create();

    assertEquals(INPUT_MINT, request.inputTokenMint());
    assertEquals(OUTPUT_MINT, request.outputTokenMint());
    assertEquals(BigInteger.valueOf(1_500L), request.amount());
    assertEquals(50, request.slippageBps());
    assertEquals(SwapMode.ExactOut, request.swapMode());
    assertEquals(List.of("Orca"), List.copyOf(request.dexes()));
    assertEquals(List.of("Raydium"), List.copyOf(request.excludeDexes()));
    assertTrue(request.restrictIntermediateTokens());
    assertTrue(request.onlyDirectRoutes());
    assertTrue(request.asLegacyTransaction());
    assertEquals(10, request.platformFeeBps());
    assertEquals(32, request.maxAccounts());
    assertEquals("v2", request.instructionVersion());
  }

  /// Building from a prototype carries every field forward, and an override
  /// replaces only the field it names.
  @Test
  void prototypeBuilderCarriesFieldsForward() {
    final var prototype = JupiterQuoteRequest.buildRequest()
        .inputTokenMint(INPUT_MINT)
        .outputTokenMint(OUTPUT_MINT)
        .slippageBps(50)
        .maxAccounts(32)
        .create();

    final var copy = JupiterQuoteRequest.buildRequest(prototype).create();
    assertEquals(prototype.serialize(), copy.serialize());

    final var overridden = JupiterQuoteRequest.buildRequest(prototype).slippageBps(100).create();
    assertEquals(100, overridden.slippageBps());
    assertEquals(32, overridden.maxAccounts(), "untouched fields carry forward");
    assertEquals(INPUT_MINT, overridden.inputTokenMint());

    // a null prototype starts from an empty builder rather than throwing
    assertNotNull(JupiterQuoteRequest.buildRequest(null));
  }

  /// The JSON parser feeds the same builder, so a parsed request serializes to
  /// the same query as one built directly.
  @Test
  void parsedRequestMatchesTheBuiltRequest() {
    final var json = """
        {
          "inputMint": "%s",
          "outputMint": "%s",
          "amount": 1500,
          "slippageBps": 50,
          "onlyDirectRoutes": true
        }""".formatted(INPUT_MINT.toBase58(), OUTPUT_MINT.toBase58());

    final var parsed = JupiterQuoteRequest.parseRequest(JsonIterator.parse(json.getBytes(UTF_8)));

    assertEquals(INPUT_MINT, parsed.inputTokenMint());
    assertEquals(OUTPUT_MINT, parsed.outputTokenMint());
    assertEquals(BigInteger.valueOf(1_500L), parsed.amount());
    assertEquals(50, parsed.slippageBps());
    assertTrue(parsed.onlyDirectRoutes());

    assertEquals(
        JupiterQuoteRequest.buildRequest()
            .inputTokenMint(INPUT_MINT)
            .outputTokenMint(OUTPUT_MINT)
            .amount(1_500L)
            .slippageBps(50)
            .onlyDirectRoutes(true)
            .create()
            .serialize(),
        parsed.serialize());
  }

  /// Parsing over a prototype overlays only the fields the JSON carries.
  @Test
  void parsedRequestOverlaysAPrototype() {
    final var prototype = JupiterQuoteRequest.buildRequest()
        .inputTokenMint(INPUT_MINT)
        .outputTokenMint(OUTPUT_MINT)
        .maxAccounts(32)
        .create();

    final var parsed = JupiterQuoteRequest.parseRequest(
        prototype, JsonIterator.parse("{\"slippageBps\":75}".getBytes(UTF_8)));

    assertEquals(75, parsed.slippageBps(), "the JSON field wins");
    assertEquals(32, parsed.maxAccounts(), "the prototype field survives");
    assertEquals(INPUT_MINT, parsed.inputTokenMint());
  }

  /// Every parseable field lands from its API name, and unknown fields —
  /// leading, mid-object, and trailing; scalar and structured — are skipped
  /// without shifting the fields after them. The builder's setters and
  /// `ji.skip()` all return their receiver, so a dropped call is an
  /// expressible mutation; each field asserts a non-default value so a
  /// dropped setter cannot hide behind the builder's default.
  @Test
  void parsedRequestReadsEveryFieldPastUnknownNeighbors() {
    final var json = """
        {
          "unknownLeading": {"nested": [1, {"deep": true}]},
          "amount": 123456789,
          "swapMode": "exactout",
          "inputMint": "%s",
          "unknownMid": ["s", 2.5, false],
          "outputMint": "%s",
          "slippageBps": 75,
          "dexes": ["Orca", "Meteora DLMM"],
          "excludeDexes": ["Phoenix"],
          "restrictIntermediateTokens": true,
          "onlyDirectRoutes": true,
          "asLegacyTransaction": true,
          "platformFeeBps": 85,
          "maxAccounts": 33,
          "instructionVersion": "V2",
          "unknownTrailing": "ignored"
        }""".formatted(INPUT_MINT.toBase58(), OUTPUT_MINT.toBase58());

    final var parsed = JupiterQuoteRequest.parseRequest(JsonIterator.parse(json.getBytes(UTF_8)));

    assertEquals(BigInteger.valueOf(123_456_789L), parsed.amount());
    assertEquals(SwapMode.ExactOut, parsed.swapMode(), "swapMode matches case-insensitively");
    assertEquals(INPUT_MINT, parsed.inputTokenMint());
    assertEquals(OUTPUT_MINT, parsed.outputTokenMint());
    assertEquals(75, parsed.slippageBps());
    assertEquals(List.of("Orca", "Meteora DLMM"), List.copyOf(parsed.dexes()));
    assertEquals(List.of("Phoenix"), List.copyOf(parsed.excludeDexes()));
    assertTrue(parsed.restrictIntermediateTokens());
    assertTrue(parsed.onlyDirectRoutes());
    assertTrue(parsed.asLegacyTransaction());
    assertEquals(85, parsed.platformFeeBps());
    assertEquals(33, parsed.maxAccounts());
    assertEquals("V2", parsed.instructionVersion());
  }
}

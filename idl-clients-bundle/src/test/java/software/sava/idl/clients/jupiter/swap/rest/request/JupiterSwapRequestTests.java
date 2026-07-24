package software.sava.idl.clients.jupiter.swap.rest.request;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the swap-request JSON body builder.
///
/// Unlike the quote and Ultra requests this one serializes JSON, and three of
/// its booleans **default to true** (`wrapAndUnwrapSol`, `useSharedAccounts`,
/// `skipUserAccountsRpcCalls`). That inverts the usual rule: a flag is emitted
/// when it differs from the Jupiter-side default, so an omission here does not
/// mean "unset" — it means "leave the API default in force", and getting the
/// polarity backwards silently changes whether SOL is wrapped or shared
/// accounts are used.
final class JupiterSwapRequestTests {

  private static final PublicKey USER = key(0x11);
  private static final PublicKey PAYER = key(0x12);
  private static final PublicKey FEE_ACCOUNT = key(0x13);
  private static final PublicKey TRACKING = key(0x14);
  private static final PublicKey DESTINATION = key(0x15);
  private static final PublicKey NATIVE_DESTINATION = key(0x16);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static JupiterSwapRequest.Builder minimal() {
    return JupiterSwapRequest.buildRequest().userPublicKey(USER);
  }

  private static String json(final JupiterSwapRequest request) {
    return request.preSerialize().toString();
  }

  @Test
  void bodyOpensWithTheUserAndEndsReadyForTheQuote() {
    final var body = json(minimal().createRequest());

    assertTrue(body.startsWith("{\"userPublicKey\":\"" + USER + "\""), body);
    // preSerialize deliberately stops mid-object: the caller appends the quote
    assertTrue(body.endsWith(",\"quoteResponse\":"), body);
    assertFalse(body.endsWith("}"), "the object is left open for the quote");
  }

  /// The three true-by-default flags are emitted only when flipped to false,
  /// and `useSharedAccounts` is always emitted because the API default differs.
  @Test
  void trueByDefaultFlagsEmitOnlyWhenDisabled() {
    final var defaults = json(minimal().createRequest());
    // wrapAndUnwrapSol is emitted only to say "false" — true is the API default
    assertFalse(defaults.contains("wrapAndUnwrapSol"), defaults);
    // useSharedAccounts is always sent, carrying its current value
    assertTrue(defaults.contains("\"useSharedAccounts\":true"), defaults);
    // skipUserAccountsRpcCalls is emitted only when true, and defaults to true
    assertTrue(defaults.contains("\"skipUserAccountsRpcCalls\":true"), defaults);

    final var disabled = json(minimal()
        .wrapAndUnwrapSol(false)
        .useSharedAccounts(false)
        .skipUserAccountsRpcCalls(false)
        .createRequest());
    assertTrue(disabled.contains("\"wrapAndUnwrapSol\":false"), disabled);
    assertTrue(disabled.contains("\"useSharedAccounts\":false"), disabled);
    // turning the skip off omits it — there is no false form
    assertFalse(disabled.contains("skipUserAccountsRpcCalls"), disabled);

    // re-enabling returns to the default rendering
    assertEquals(defaults, json(minimal()
        .wrapAndUnwrapSol(true)
        .useSharedAccounts(true)
        .skipUserAccountsRpcCalls(true)
        .createRequest()));
  }

  /// The false-by-default flags behave the usual way: emitted only when set.
  @Test
  void falseByDefaultFlagsEmitOnlyWhenSet() {
    final var defaults = json(minimal().createRequest());
    assertFalse(defaults.contains("asLegacyTransaction"), defaults);
    assertFalse(defaults.contains("dynamicComputeUnitLimit"), defaults);

    assertTrue(json(minimal().asLegacyTransaction(true).createRequest())
        .contains("\"asLegacyTransaction\":true"));
    assertTrue(json(minimal().dynamicComputeUnitLimit(true).createRequest())
        .contains("\"dynamicComputeUnitLimit\": true"));

    // and setting one does not emit the other
    final var onlyLegacy = json(minimal().asLegacyTransaction(true).createRequest());
    assertFalse(onlyLegacy.contains("dynamicComputeUnitLimit"), onlyLegacy);
  }

  @Test
  void optionalAccountsAreOmittedWhenUnset() {
    final var body = json(minimal().createRequest());
    for (final var field : new String[]{
        "payer", "feeAccount", "trackingAccount", "destinationTokenAccount",
        "nativeDestinationAccount", "prioritizationFeeLamports",
        "computeUnitPriceMicroLamports", "blockhashSlotsToExpiry"
    }) {
      assertFalse(body.contains(field), field + " must be omitted when unset: " + body);
    }
  }

  @Test
  void eachAccountIsEmittedUnderItsOwnField() {
    final var body = json(minimal()
        .payer(PAYER)
        .feeAccount(FEE_ACCOUNT)
        .trackingAccount(TRACKING)
        .destinationTokenAccount(DESTINATION)
        .createRequest());

    assertTrue(body.contains("\"payer\":\"" + PAYER + "\""), body);
    assertTrue(body.contains("\"feeAccount\":\"" + FEE_ACCOUNT + "\""), body);
    assertTrue(body.contains("\"trackingAccount\":\"" + TRACKING + "\""), body);
    assertTrue(body.contains("\"destinationTokenAccount\":\"" + DESTINATION + "\""), body);

    // four distinct keys in four distinct fields — no transposition
    assertEquals(4, java.util.Set.of(PAYER, FEE_ACCOUNT, TRACKING, DESTINATION).size());
    assertFalse(body.contains("\"payer\":\"" + FEE_ACCOUNT + "\""), "fee account must not land in payer");
  }

  /// `destinationTokenAccount` and `nativeDestinationAccount` are mutually
  /// exclusive — the token account wins, since sending both is contradictory.
  @Test
  void destinationAccountsAreMutuallyExclusive() {
    final var tokenOnly = json(minimal().destinationTokenAccount(DESTINATION).createRequest());
    assertTrue(tokenOnly.contains("\"destinationTokenAccount\":\"" + DESTINATION + "\""), tokenOnly);
    assertFalse(tokenOnly.contains("nativeDestinationAccount"), tokenOnly);

    final var nativeOnly = json(minimal().nativeDestinationAccount(NATIVE_DESTINATION).createRequest());
    assertTrue(nativeOnly.contains("\"nativeDestinationAccount\":\"" + NATIVE_DESTINATION + "\""), nativeOnly);
    assertFalse(nativeOnly.contains("destinationTokenAccount"), nativeOnly);

    // both set: the token account takes precedence and the native one is dropped
    final var both = json(minimal()
        .destinationTokenAccount(DESTINATION)
        .nativeDestinationAccount(NATIVE_DESTINATION)
        .createRequest());
    assertTrue(both.contains("\"destinationTokenAccount\":\"" + DESTINATION + "\""), both);
    assertFalse(both.contains("nativeDestinationAccount"), both);
  }

  /// A dynamic compute-unit limit and an explicit unit price are mutually
  /// exclusive — the dynamic flag wins, since asking for both is contradictory.
  @Test
  void dynamicLimitSuppressesTheExplicitUnitPrice() {
    assertFalse(json(minimal().createRequest()).contains("computeUnitPriceMicroLamports"));
    // only a positive price is sent; zero and the unset sentinel are both silent
    assertFalse(json(minimal().computeUnitPriceMicroLamports(0).createRequest())
        .contains("computeUnitPriceMicroLamports"));
    assertTrue(json(minimal().computeUnitPriceMicroLamports(5_000).createRequest())
        .contains("\"computeUnitPriceMicroLamports\":5000"));

    // with the dynamic flag set, the price is dropped even when positive
    final var both = json(minimal()
        .dynamicComputeUnitLimit(true)
        .computeUnitPriceMicroLamports(5_000)
        .createRequest());
    assertTrue(both.contains("\"dynamicComputeUnitLimit\": true"), both);
    assertFalse(both.contains("computeUnitPriceMicroLamports"), both);
  }

  @Test
  void blockhashSlotsToExpiryIsOmittedAtZero() {
    assertFalse(json(minimal().blockhashSlotsToExpiry(0).createRequest()).contains("blockhashSlotsToExpiry"));
    assertTrue(json(minimal().blockhashSlotsToExpiry(1).createRequest())
        .contains("\"blockhashSlotsToExpiry\":1"));
    assertTrue(json(minimal().blockhashSlotsToExpiry(150).createRequest())
        .contains("\"blockhashSlotsToExpiry\":150"));
  }

  /// The priority fee and Jito tip share one `prioritizationFeeLamports`
  /// object, comma-separated when both are present — a missing separator would
  /// produce invalid JSON.
  @Test
  void priorityFeeAndJitoTipShareOneObject() {
    final var fee = new PriorityFeeLamports("high", 1_000_000L, true);

    final var feeOnly = json(minimal().prioritizationFeeLamports(fee).createRequest());
    assertTrue(feeOnly.contains("\"prioritizationFeeLamports\":{"), feeOnly);
    assertTrue(feeOnly.contains("\"priorityLevel\": \"high\""), feeOnly);
    assertTrue(feeOnly.contains("\"maxLamports\": 1000000"), feeOnly);
    assertTrue(feeOnly.contains("\"global\": true"), feeOnly);
    assertFalse(feeOnly.contains("jitoTipLamports"), feeOnly);

    final var tip = new JitoTip(50_000L, null);
    final var tipOnly = json(minimal().jitoTip(tip).createRequest());
    assertTrue(tipOnly.contains("\"prioritizationFeeLamports\":{"), tipOnly);
    assertTrue(tipOnly.contains("jitoTipLamports"), tipOnly);
    assertFalse(tipOnly.contains("priorityLevelWithMaxLamports"), tipOnly);

    // both: one object carrying both entries, separated by a comma
    final var both = json(minimal().prioritizationFeeLamports(fee).jitoTip(tip).createRequest());
    assertTrue(both.contains("priorityLevelWithMaxLamports"), both);
    assertTrue(both.contains("jitoTipLamports"), both);
    final int feeIdx = both.indexOf("priorityLevelWithMaxLamports");
    final int tipIdx = both.indexOf("jitoTipLamports");
    assertTrue(feeIdx < tipIdx, "the priority level leads");

    // The exact object, built from the same toJson() the body embeds: the fee
    // and tip JSON both contain commas and braces of their own, so containment
    // checks on "," or "}" cannot tell the separator and the closing brace of
    // the shared object from their lookalikes inside the entries.
    final String expectedObject = "\"prioritizationFeeLamports\":{" + fee.toJson() + "," + tip.toJson() + '}';
    assertTrue(both.contains(expectedObject), both);

    // fee alone: no separator — a stray trailing comma is invalid JSON
    assertTrue(feeOnly.contains("\"prioritizationFeeLamports\":{" + fee.toJson() + '}'), feeOnly);
    assertTrue(tipOnly.contains("\"prioritizationFeeLamports\":{" + tip.toJson() + '}'), tipOnly);
  }

  /// Every setter round-trips through the record, so a builder that drops a
  /// field would produce a body missing it.
  @Test
  void builderRoundTripsEveryField() {
    final var fee = new PriorityFeeLamports("veryHigh", 2L, false);
    final var tip = new JitoTip(3L, null);

    final var request = JupiterSwapRequest.buildRequest()
        .userPublicKey(USER)
        .payer(PAYER)
        .wrapAndUnwrapSol(false)
        .useSharedAccounts(false)
        .feeAccount(FEE_ACCOUNT)
        .trackingAccount(TRACKING)
        .prioritizationFeeLamports(fee)
        .jitoTip(tip)
        .asLegacyTransaction(true)
        .destinationTokenAccount(DESTINATION)
        .nativeDestinationAccount(NATIVE_DESTINATION)
        .dynamicComputeUnitLimit(true)
        .computeUnitPriceMicroLamports(7)
        .skipUserAccountsRpcCalls(false)
        .blockhashSlotsToExpiry(42)
        .createRequest();

    assertEquals(USER, request.userPublicKey());
    assertEquals(PAYER, request.payer());
    assertFalse(request.wrapAndUnwrapSol());
    assertFalse(request.useSharedAccounts());
    assertEquals(FEE_ACCOUNT, request.feeAccount());
    assertEquals(TRACKING, request.trackingAccount());
    assertEquals(fee, request.prioritizationFeeLamports());
    assertEquals(tip, request.jitoTip());
    assertTrue(request.asLegacyTransaction());
    assertEquals(DESTINATION, request.destinationTokenAccount());
    assertEquals(NATIVE_DESTINATION, request.nativeDestinationAccount());
    assertTrue(request.dynamicComputeUnitLimit());
    assertEquals(7, request.computeUnitPriceMicroLamports());
    assertFalse(request.skipUserAccountsRpcCalls());
    assertEquals(42, request.blockhashSlotsToExpiry());

    // the builder's own accessors report what was set, before createRequest()
    final var builder = JupiterSwapRequest.buildRequest().userPublicKey(USER).feeAccount(FEE_ACCOUNT);
    assertEquals(USER, builder.userPublicKey());
    assertEquals(FEE_ACCOUNT, builder.feeAccount());
    assertTrue(builder.wrapAndUnwrapSol(), "defaults visible through the builder too");
    assertTrue(builder.useSharedAccounts());
    assertTrue(builder.skipUserAccountsRpcCalls());

    // every remaining accessor, on a builder where each field differs from its
    // default — including the true-by-default booleans read back as false
    final var full = JupiterSwapRequest.buildRequest()
        .payer(PAYER)
        .trackingAccount(TRACKING)
        .prioritizationFeeLamports(fee)
        .jitoTip(tip)
        .asLegacyTransaction(true)
        .destinationTokenAccount(DESTINATION)
        .nativeDestinationAccount(NATIVE_DESTINATION)
        .dynamicComputeUnitLimit(true)
        .computeUnitPriceMicroLamports(7)
        .blockhashSlotsToExpiry(42)
        .wrapAndUnwrapSol(false)
        .useSharedAccounts(false)
        .skipUserAccountsRpcCalls(false);
    assertEquals(PAYER, full.payer());
    assertEquals(TRACKING, full.trackingAccount());
    assertEquals(fee, full.prioritizationFeeLamports());
    assertEquals(tip, full.jitoTip());
    assertTrue(full.asLegacyTransaction());
    assertEquals(DESTINATION, full.destinationTokenAccount());
    assertEquals(NATIVE_DESTINATION, full.nativeDestinationAccount());
    assertTrue(full.dynamicComputeUnitLimit());
    assertEquals(7, full.computeUnitPriceMicroLamports());
    assertEquals(42, full.blockhashSlotsToExpiry());
    assertFalse(full.wrapAndUnwrapSol());
    assertFalse(full.useSharedAccounts());
    assertFalse(full.skipUserAccountsRpcCalls());
    assertFalse(JupiterSwapRequest.buildRequest().asLegacyTransaction());
    assertFalse(JupiterSwapRequest.buildRequest().dynamicComputeUnitLimit());
  }

  /// The tip renders under two different field names — payer-less lamports, or
  /// lamports with an explicit payer — and never a null payer.
  @Test
  void jitoTipRendersWithAndWithoutPayer() {
    assertEquals("""
        "jitoTipLamports": {"lamports": 50000}
        """, new JitoTip(50_000L, null).toJson());
    assertEquals("""
        "jitoTipLamportsWithPayer": {"lamports": 50000, "payer": "%s"}
        """.formatted(PAYER), new JitoTip(50_000L, PAYER).toJson());
  }
}

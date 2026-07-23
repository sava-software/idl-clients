package software.sava.idl.clients.jupiter.swap.rest.request;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

/// Covers the Ultra order-request builder and its query-string serialization.
///
/// Five of the parameters are bare public keys — `taker`, `receiver`, `payer`,
/// `closeAuthority`, `referralAccount` — and they control who signs, who
/// receives the output, and who pays. Interchanging two of them produces a
/// perfectly well-formed request that routes funds to the wrong account, so
/// each is asserted under its own parameter name with a distinct key.
final class JupiterUltraOrderRequestTests {

  private static final PublicKey INPUT_MINT =
      PublicKey.fromBase58Encoded("So11111111111111111111111111111111111111112");
  private static final PublicKey OUTPUT_MINT =
      PublicKey.fromBase58Encoded("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");

  private static final PublicKey TAKER = key(0x11);
  private static final PublicKey RECEIVER = key(0x12);
  private static final PublicKey PAYER = key(0x13);
  private static final PublicKey CLOSE_AUTHORITY = key(0x14);
  private static final PublicKey REFERRAL = key(0x15);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static JupiterUltraOrderRequest.Builder minimal() {
    return JupiterUltraOrderRequest.build()
        .inputMint(INPUT_MINT)
        .outputMint(OUTPUT_MINT);
  }

  private static List<String> params(final String query) {
    return Arrays.asList(query.split("&"));
  }

  @Test
  void mintsAreAlwaysEmitted() {
    assertEquals(
        "inputMint=" + INPUT_MINT.toBase58() + "&outputMint=" + OUTPUT_MINT.toBase58(),
        minimal().createRequest().serialize());
  }

  @Test
  void unsetOptionalsAreOmitted() {
    final var query = minimal().createRequest().serialize();
    for (final var name : new String[]{
        "amount", "taker", "receiver", "payer", "closeAuthority",
        "referralAccount", "referralFeeBps", "excludeRouters", "excludeDexes"
    }) {
      assertFalse(query.contains(name + "="), name + " must be omitted when unset: " + query);
    }
  }

  /// The five account parameters each carry their own key. Given distinct
  /// keys, any transposition between them is visible.
  @Test
  void eachAccountParameterCarriesItsOwnKey() {
    final var query = minimal()
        .taker(TAKER)
        .receiver(RECEIVER)
        .payer(PAYER)
        .closeAuthority(CLOSE_AUTHORITY)
        .referralAccount(REFERRAL)
        .createRequest()
        .serialize();

    final var parts = params(query);
    assertTrue(parts.contains("taker=" + TAKER.toBase58()), query);
    assertTrue(parts.contains("receiver=" + RECEIVER.toBase58()), query);
    assertTrue(parts.contains("payer=" + PAYER.toBase58()), query);
    assertTrue(parts.contains("closeAuthority=" + CLOSE_AUTHORITY.toBase58()), query);
    assertTrue(parts.contains("referralAccount=" + REFERRAL.toBase58()), query);

    // the five keys are distinct, so the assertions above cannot be satisfied
    // by a builder that stores them all in one field
    assertEquals(5, Set.of(TAKER, RECEIVER, PAYER, CLOSE_AUTHORITY, REFERRAL).size());
  }

  /// Setting one account must not emit any of the others.
  @Test
  void accountParametersAreIndependent() {
    final var takerOnly = minimal().taker(TAKER).createRequest().serialize();
    assertTrue(takerOnly.contains("taker=" + TAKER.toBase58()));
    assertFalse(takerOnly.contains("receiver="));
    assertFalse(takerOnly.contains("payer="));
    assertFalse(takerOnly.contains("closeAuthority="));

    final var receiverOnly = minimal().receiver(RECEIVER).createRequest().serialize();
    assertTrue(receiverOnly.contains("receiver=" + RECEIVER.toBase58()));
    assertFalse(receiverOnly.contains("taker="));

    final var payerOnly = minimal().payer(PAYER).createRequest().serialize();
    assertTrue(payerOnly.contains("payer=" + PAYER.toBase58()));
    assertFalse(payerOnly.contains("closeAuthority="));

    final var closeOnly = minimal().closeAuthority(CLOSE_AUTHORITY).createRequest().serialize();
    assertTrue(closeOnly.contains("closeAuthority=" + CLOSE_AUTHORITY.toBase58()));
    assertFalse(closeOnly.contains("payer="));
  }

  @Test
  void amountAndReferralFeeAreOmittedAtZero() {
    assertFalse(minimal().amount(BigInteger.ZERO).createRequest().serialize().contains("amount="));
    assertFalse(minimal().referralFeeBps(0).createRequest().serialize().contains("referralFeeBps="));

    assertTrue(minimal().amount(BigInteger.ONE).createRequest().serialize().contains("amount=1"));
    assertTrue(minimal().referralFeeBps(1).createRequest().serialize().contains("referralFeeBps=1"));

    // the amount is a BigInteger so a full u64 survives
    assertTrue(minimal().amount(new BigInteger("18446744073709551615")).createRequest().serialize()
        .contains("amount=18446744073709551615"));
  }

  /// The exclusion sets are comma joined, and the singular setters accumulate
  /// rather than replace — dropping one silently widens the routing.
  @Test
  void exclusionSetsAreJoinedAndAccumulate() {
    final var routers = minimal().excludeRouters(Set.of("metis")).createRequest().serialize();
    assertTrue(routers.contains("excludeRouters=metis"), routers);
    assertFalse(routers.contains("excludeDexes="), routers);

    final var dexes = minimal().excludeDexes(Set.of("Orca")).createRequest().serialize();
    assertTrue(dexes.contains("excludeDexes=Orca"), dexes);
    assertFalse(dexes.contains("excludeRouters="), dexes);

    // both sets can be sent together — unlike the quote request's dexes pair
    final var both = minimal()
        .excludeRouters(Set.of("metis"))
        .excludeDexes(Set.of("Orca"))
        .createRequest()
        .serialize();
    assertTrue(both.contains("excludeRouters=metis"), both);
    assertTrue(both.contains("excludeDexes=Orca"), both);

    // the singular setters add to the set
    final var accumulated = minimal()
        .excludeDex("Orca")
        .excludeDex("Raydium")
        .createRequest();
    assertEquals(Set.of("Orca", "Raydium"), Set.copyOf(accumulated.excludeDexes()));
    final var accumulatedQuery = accumulated.serialize();
    assertTrue(accumulatedQuery.contains("Orca"), accumulatedQuery);
    assertTrue(accumulatedQuery.contains("Raydium"), accumulatedQuery);

    final var routerAccumulated = minimal().excludeRouter("metis").excludeRouter("jupiterz").createRequest();
    assertEquals(Set.of("metis", "jupiterz"), Set.copyOf(routerAccumulated.excludeRouters()));

    // an empty set is the same as unset
    assertFalse(minimal().excludeDexes(Set.of()).createRequest().serialize().contains("excludeDexes="));
  }

  @Test
  void buildersRoundTripEveryField() {
    final var request = minimal()
        .amount(BigInteger.valueOf(1_500L))
        .taker(TAKER)
        .receiver(RECEIVER)
        .payer(PAYER)
        .closeAuthority(CLOSE_AUTHORITY)
        .referralAccount(REFERRAL)
        .referralFeeBps(25)
        .excludeRouters(Set.of("metis"))
        .excludeDexes(Set.of("Orca"))
        .createRequest();

    assertEquals(INPUT_MINT, request.inputMint());
    assertEquals(OUTPUT_MINT, request.outputMint());
    assertEquals(BigInteger.valueOf(1_500L), request.amount());
    assertEquals(TAKER, request.taker());
    assertEquals(RECEIVER, request.receiver());
    assertEquals(PAYER, request.payer());
    assertEquals(CLOSE_AUTHORITY, request.closeAuthority());
    assertEquals(REFERRAL, request.referralAccount());
    assertEquals(25, request.referralFeeBps());
    assertEquals(Set.of("metis"), Set.copyOf(request.excludeRouters()));
    assertEquals(Set.of("Orca"), Set.copyOf(request.excludeDexes()));
  }

  @Test
  void prototypeBuilderCarriesFieldsForward() {
    final var prototype = minimal().taker(TAKER).referralFeeBps(25).createRequest();

    assertEquals(prototype.serialize(), JupiterUltraOrderRequest.build(prototype).createRequest().serialize());

    final var overridden = JupiterUltraOrderRequest.build(prototype).referralFeeBps(50).createRequest();
    assertEquals(50, overridden.referralFeeBps());
    assertEquals(TAKER, overridden.taker(), "untouched fields carry forward");

    assertNotNull(JupiterUltraOrderRequest.build(null));
  }

  @Test
  void parsedRequestMatchesTheBuiltRequest() {
    final var json = """
        {
          "inputMint": "%s",
          "outputMint": "%s",
          "amount": 1500,
          "taker": "%s"
        }""".formatted(INPUT_MINT.toBase58(), OUTPUT_MINT.toBase58(), TAKER.toBase58());

    final var parsed = JupiterUltraOrderRequest.parseRequest(JsonIterator.parse(json.getBytes(UTF_8)));

    assertEquals(INPUT_MINT, parsed.inputMint());
    assertEquals(OUTPUT_MINT, parsed.outputMint());
    assertEquals(BigInteger.valueOf(1_500L), parsed.amount());
    assertEquals(TAKER, parsed.taker());

    assertEquals(
        minimal().amount(BigInteger.valueOf(1_500L)).taker(TAKER).createRequest().serialize(),
        parsed.serialize());
  }

  /// Every parseable field lands from its API name, and unknown fields —
  /// leading, mid-object, and trailing; scalar and structured — are skipped
  /// without shifting the fields after them. Five fields are bare public keys
  /// carrying distinct fill bytes, so a setter dropped (they return their
  /// receiver) or a mis-slotted key is visible by identity.
  @Test
  void parsedRequestReadsEveryFieldPastUnknownNeighbors() {
    final var json = """
        {
          "unknownLeading": {"nested": [1, {"deep": true}]},
          "amount": 98765432109876543210,
          "inputMint": "%s",
          "outputMint": "%s",
          "unknownMid": ["s", 2.5, false],
          "taker": "%s",
          "receiver": "%s",
          "payer": "%s",
          "closeAuthority": "%s",
          "referralAccount": "%s",
          "referralFeeBps": 42,
          "unknownTrailing": "ignored"
        }""".formatted(INPUT_MINT.toBase58(), OUTPUT_MINT.toBase58(), TAKER.toBase58(),
        RECEIVER.toBase58(), PAYER.toBase58(), CLOSE_AUTHORITY.toBase58(), REFERRAL.toBase58());

    final var parsed = JupiterUltraOrderRequest.parseRequest(JsonIterator.parse(json.getBytes(UTF_8)));

    assertEquals(new BigInteger("98765432109876543210"), parsed.amount(),
        "amount is a BigInteger read, wider than long");
    assertEquals(INPUT_MINT, parsed.inputMint());
    assertEquals(OUTPUT_MINT, parsed.outputMint());
    assertEquals(TAKER, parsed.taker());
    assertEquals(RECEIVER, parsed.receiver());
    assertEquals(PAYER, parsed.payer());
    assertEquals(CLOSE_AUTHORITY, parsed.closeAuthority());
    assertEquals(REFERRAL, parsed.referralAccount());
    assertEquals(42, parsed.referralFeeBps());
  }
}

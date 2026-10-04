package software.sava.idl.clients.jupiter.swap.rest.request;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.JsonException;
import systems.comodal.jsoniter.JsonIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

/// Covers the `GET /swap/v2/order` request: its query, its setter checks, its required-parameter and
/// contradiction checks, and its JSON template parser.
///
/// Six parameters are bare public keys: the two mints, and the four accounts that decide who signs, who receives the
/// output, who pays and who earns the referral fee. So each carries a distinct key, and every expected query is
/// written out by hand in the API reference's parameter order. Parameters are compared by name and value after
/// splitting on `&`, never by substring.
final class JupiterSwapOrderRequestTests {

  private static final PublicKey WSOL = PublicKey.fromBase58Encoded("So11111111111111111111111111111111111111112");
  private static final PublicKey USDC = PublicKey.fromBase58Encoded("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");
  private static final PublicKey TAKER = PublicKey.fromBase58Encoded("GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ");
  private static final PublicKey PAYER = PublicKey.fromBase58Encoded("gasTzr94Pmp4Gf8vknQnqxeYxdgwFjbgdJa4msYRpnB");
  private static final PublicKey OTHER = PublicKey.fromBase58Encoded("9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn");
  private static final PublicKey REFERRAL = PublicKey.fromBase58Encoded("3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT");

  /// O2: a quote-only request, the three required parameters.
  private static final String MINIMAL_QUERY = "inputMint=So11111111111111111111111111111111111111112&outputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&amount=1000000";
  /// O1: every parameter.
  private static final String FULL_QUERY = "inputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&outputMint=So11111111111111111111111111111111111111112&amount=100000000&taker=GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ&receiver=9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn&swapMode=ExactIn&slippageBps=0&referralAccount=3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT&referralFee=50&payer=gasTzr94Pmp4Gf8vknQnqxeYxdgwFjbgdJa4msYRpnB&priorityFeeLamports=0&jitoTipLamports=10000&broadcastFeeType=exactFee&excludeRouters=jupiterz%2Cdflow&excludeDexes=Orca+V2";
  /// O1 with `slippageBps=25` in place of `slippageBps=0`.
  private static final String FULL_QUERY_WITH_SLIPPAGE_25 = "inputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&outputMint=So11111111111111111111111111111111111111112&amount=100000000&taker=GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ&receiver=9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn&swapMode=ExactIn&slippageBps=25&referralAccount=3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT&referralFee=50&payer=gasTzr94Pmp4Gf8vknQnqxeYxdgwFjbgdJa4msYRpnB&priorityFeeLamports=0&jitoTipLamports=10000&broadcastFeeType=exactFee&excludeRouters=jupiterz%2Cdflow&excludeDexes=Orca+V2";

  private static JupiterSwapOrderRequest.Builder minimal() {
    return JupiterSwapOrderRequest.buildRequest()
        .inputMint(WSOL)
        .outputMint(USDC)
        .amount(1_000_000);
  }

  /// Every O1 parameter.
  private static JupiterSwapOrderRequest.Builder full() {
    return JupiterSwapOrderRequest.buildRequest()
        .inputMint(USDC)
        .outputMint(WSOL)
        .amount(100_000_000)
        .taker(TAKER)
        .receiver(OTHER)
        .swapMode(SwapMode.ExactIn)
        .slippageBps(0)
        .referralAccount(REFERRAL)
        .referralFee(50)
        .payer(PAYER)
        .priorityFeeLamports(0)
        .jitoTipLamports(10_000)
        .broadcastFeeType(BroadcastFeeType.exactFee)
        .excludeRouters(List.of("jupiterz", "dflow"))
        .excludeDexes(List.of("Orca V2"));
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

  private static JupiterSwapOrderRequest parse(final String json) {
    return JupiterSwapOrderRequest.parseRequest(JsonIterator.parse(json.getBytes(UTF_8)));
  }

  private static JupiterSwapOrderRequest parse(final JupiterSwapOrderRequest prototype, final String json) {
    return JupiterSwapOrderRequest.parseRequest(prototype, JsonIterator.parse(json.getBytes(UTF_8)));
  }

  private static void assertMissing(final String expectedMessage, final JupiterSwapOrderRequest.Builder builder) {
    assertEquals(expectedMessage, assertThrows(IllegalStateException.class, builder::serialize).getMessage());
    final var request = builder.createRequest();
    assertEquals(expectedMessage, assertThrows(IllegalStateException.class, request::serialize).getMessage());
  }

  private static void assertContradiction(final String expectedMessage, final JupiterSwapOrderRequest.Builder builder) {
    assertEquals(expectedMessage, assertThrows(IllegalStateException.class, builder::createRequest).getMessage());
    assertEquals(expectedMessage, assertThrows(IllegalStateException.class, builder::serialize).getMessage());
  }

  /// Asserts that the builder and the request it creates both serialize to `expectedQuery`.
  private static void assertSent(final String expectedQuery, final JupiterSwapOrderRequest.Builder builder) {
    assertEquals(expectedQuery, builder.serialize());
    assertEquals(expectedQuery, builder.createRequest().serialize());
  }

  /// Asserts each accessor against the O1 values.
  private static void assertFullFields(final JupiterSwapOrderRequest request) {
    assertEquals(USDC, request.inputMint());
    assertEquals(WSOL, request.outputMint());
    assertEquals(100_000_000L, request.amount());
    assertEquals(TAKER, request.taker());
    assertEquals(OTHER, request.receiver());
    assertEquals(SwapMode.ExactIn, request.swapMode());
    assertEquals(OptionalInt.of(0), request.slippageBps());
    assertEquals(REFERRAL, request.referralAccount());
    assertEquals(50, request.referralFee());
    assertEquals(PAYER, request.payer());
    assertEquals(OptionalLong.of(0), request.priorityFeeLamports());
    assertEquals(OptionalLong.of(10_000), request.jitoTipLamports());
    assertEquals(BroadcastFeeType.exactFee, request.broadcastFeeType());
    assertEquals(List.of("jupiterz", "dflow"), request.excludeRouters());
    assertEquals(List.of("Orca V2"), request.excludeDexes());
  }

  /// Asserts the state of a builder nothing has been set on.
  private static void assertEmpty(final JupiterSwapOrderRequest request) {
    assertNull(request.inputMint());
    assertNull(request.outputMint());
    assertEquals(0L, request.amount());
    assertNull(request.taker());
    assertNull(request.receiver());
    assertNull(request.swapMode());
    assertEquals(OptionalInt.empty(), request.slippageBps());
    assertNull(request.referralAccount());
    assertEquals(0, request.referralFee());
    assertNull(request.payer());
    assertEquals(OptionalLong.empty(), request.priorityFeeLamports());
    assertEquals(OptionalLong.empty(), request.jitoTipLamports());
    assertNull(request.broadcastFeeType());
    assertEquals(List.of(), request.excludeRouters());
    assertEquals(List.of(), request.excludeDexes());
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
    final var query = builder.createRequest().serialize();
    assertEquals(FULL_QUERY, query);

    final var names = names(query);
    assertFalse(names.contains("dexes"), query);
    assertFalse(names.contains("closeAuthority"), query);
    assertFalse(names.contains("referralFeeBps"), query);
  }

  @Test
  void referralFeeIsSentUnderTheSpecName() {
    final var query = minimal().referralAccount(REFERRAL).referralFee(50).createRequest().serialize();
    assertEquals(MINIMAL_QUERY + "&referralAccount=3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT&referralFee=50", query);
    assertEquals(List.of("50"), values(query, "referralFee"));
    assertEquals(List.of(), values(query, "referralFeeBps"));
  }

  @Test
  void referralAccountAndFeeComeTogether() {
    final var message = "referralAccount and referralFee must be set together";
    assertContradiction(message, minimal().referralAccount(REFERRAL));
    assertContradiction(message, minimal().referralAccount(REFERRAL).referralFee(0));
    assertContradiction(message, minimal().referralFee(50));
    assertContradiction(message, minimal().referralFee(1));

    // both, down to the smallest fee
    assertEquals(
        MINIMAL_QUERY + "&referralAccount=3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT&referralFee=1",
        minimal().referralAccount(REFERRAL).referralFee(1).createRequest().serialize()
    );
    // neither
    assertEquals(MINIMAL_QUERY, minimal().referralFee(0).createRequest().serialize());
  }

  @Test
  void receiverMustDifferFromTaker() {
    final var message = "receiver must differ from taker";
    assertContradiction(message, minimal().taker(TAKER).receiver(TAKER));
    // equal keys, not only the same instance
    assertContradiction(
        message,
        minimal().taker(TAKER).receiver(PublicKey.fromBase58Encoded("GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ"))
    );

    assertEquals(
        MINIMAL_QUERY + "&taker=GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ&receiver=9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn",
        minimal().taker(TAKER).receiver(OTHER).createRequest().serialize()
    );
    // a receiver without a taker is allowed
    assertEquals(
        MINIMAL_QUERY + "&receiver=9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn",
        minimal().receiver(OTHER).createRequest().serialize()
    );
  }

  /// Jupiter applies a priority fee or a Jito tip only together with `broadcastFeeType`: sent without one, the fee
  /// was ignored for Jupiter's own estimate and no tip was added (observed 2026-10-04; swap.yaml does not state the
  /// rule). So a fee or tip without it, 0 included, is a contradiction, checked after the other two.
  @Test
  void priorityFeeAndTipNeedBroadcastFeeType() {
    final var message = "priorityFeeLamports and jitoTipLamports require broadcastFeeType";
    // a fee alone and a tip alone, 0 included, from a number or from an optional holding one
    assertContradiction(message, minimal().priorityFeeLamports(5_000));
    assertContradiction(message, minimal().priorityFeeLamports(0));
    assertContradiction(message, minimal().priorityFeeLamports(OptionalLong.of(5_000)));
    assertContradiction(message, minimal().jitoTipLamports(10_000));
    assertContradiction(message, minimal().jitoTipLamports(0));
    assertContradiction(message, minimal().jitoTipLamports(OptionalLong.of(10_000)));
    // both together
    assertContradiction(message, minimal().priorityFeeLamports(5_000).jitoTipLamports(10_000));
    // and a fee whose broadcastFeeType was cleared again
    assertContradiction(
        message,
        minimal().priorityFeeLamports(5_000).broadcastFeeType(BroadcastFeeType.maxCap).broadcastFeeType(null)
    );

    // either broadcastFeeType lets a fee or a tip be sent
    assertSent(
        MINIMAL_QUERY + "&priorityFeeLamports=5000&broadcastFeeType=maxCap",
        minimal().priorityFeeLamports(5_000).broadcastFeeType(BroadcastFeeType.maxCap)
    );
    assertSent(
        MINIMAL_QUERY + "&priorityFeeLamports=5000&broadcastFeeType=exactFee",
        minimal().priorityFeeLamports(5_000).broadcastFeeType(BroadcastFeeType.exactFee)
    );
    assertSent(
        MINIMAL_QUERY + "&jitoTipLamports=10000&broadcastFeeType=maxCap",
        minimal().jitoTipLamports(10_000).broadcastFeeType(BroadcastFeeType.maxCap)
    );
    assertSent(
        MINIMAL_QUERY + "&jitoTipLamports=10000&broadcastFeeType=exactFee",
        minimal().jitoTipLamports(10_000).broadcastFeeType(BroadcastFeeType.exactFee)
    );
    // set before them, with both at 0
    assertSent(
        MINIMAL_QUERY + "&priorityFeeLamports=0&jitoTipLamports=0&broadcastFeeType=exactFee",
        minimal().broadcastFeeType(BroadcastFeeType.exactFee).priorityFeeLamports(0).jitoTipLamports(0)
    );

    // a broadcastFeeType alone is still sent as given
    assertSent(MINIMAL_QUERY + "&broadcastFeeType=maxCap", minimal().broadcastFeeType(BroadcastFeeType.maxCap));
    assertSent(MINIMAL_QUERY + "&broadcastFeeType=exactFee", minimal().broadcastFeeType(BroadcastFeeType.exactFee));

    // a fee or tip cleared again needs none, whether by an empty optional or by null
    assertSent(MINIMAL_QUERY, minimal().priorityFeeLamports(5_000).priorityFeeLamports(OptionalLong.empty()));
    assertSent(MINIMAL_QUERY, minimal().jitoTipLamports(10_000).jitoTipLamports(OptionalLong.empty()));
    assertSent(MINIMAL_QUERY, minimal().priorityFeeLamports(5_000).priorityFeeLamports(null));
    assertSent(MINIMAL_QUERY, minimal().jitoTipLamports(10_000).jitoTipLamports(null));

    // the two older contradictions are reported first
    assertContradiction(
        "receiver must differ from taker",
        minimal().taker(TAKER).receiver(TAKER).priorityFeeLamports(5_000)
    );
    assertContradiction(
        "referralAccount and referralFee must be set together",
        minimal().referralFee(50).jitoTipLamports(10_000)
    );
    // and serialize() reports a missing parameter before it, where createRequest() checks no required parameter
    final var incomplete = JupiterSwapOrderRequest.buildRequest()
        .inputMint(WSOL)
        .outputMint(USDC)
        .priorityFeeLamports(5_000);
    assertEquals(
        "/swap/v2/order requires amount",
        assertThrows(IllegalStateException.class, incomplete::serialize).getMessage()
    );
    assertEquals(message, assertThrows(IllegalStateException.class, incomplete::createRequest).getMessage());
  }

  @Test
  void zeroSlippagePriorityFeeAndTipAreSent() {
    final var zeros = minimal()
        .slippageBps(0)
        .priorityFeeLamports(0)
        .jitoTipLamports(0)
        .broadcastFeeType(BroadcastFeeType.maxCap);
    assertEquals(OptionalInt.of(0), zeros.slippageBps());
    assertEquals(OptionalLong.of(0), zeros.priorityFeeLamports());
    assertEquals(OptionalLong.of(0), zeros.jitoTipLamports());

    final var request = zeros.createRequest();
    assertEquals(OptionalInt.of(0), request.slippageBps());
    assertEquals(OptionalLong.of(0), request.priorityFeeLamports());
    assertEquals(OptionalLong.of(0), request.jitoTipLamports());
    assertEquals(
        MINIMAL_QUERY + "&slippageBps=0&priorityFeeLamports=0&jitoTipLamports=0&broadcastFeeType=maxCap",
        request.serialize()
    );
  }

  @Test
  void unsetOptionalNumbersAreEmptyAndOmitted() {
    final var unset = minimal();
    assertEquals(OptionalInt.empty(), unset.slippageBps());
    assertEquals(OptionalLong.empty(), unset.priorityFeeLamports());
    assertEquals(OptionalLong.empty(), unset.jitoTipLamports());
    assertEquals(0, unset.referralFee());

    final var request = unset.createRequest();
    assertEquals(OptionalInt.empty(), request.slippageBps());
    assertEquals(OptionalLong.empty(), request.priorityFeeLamports());
    assertEquals(OptionalLong.empty(), request.jitoTipLamports());
    assertEquals(0, request.referralFee());
    assertEquals(List.of("inputMint", "outputMint", "amount"), names(request.serialize()));

    // each one set leaves the other two off the query, from a number or from an optional holding one; a fee or a tip
    // goes with the broadcastFeeType it needs
    assertEquals(MINIMAL_QUERY + "&slippageBps=50", minimal().slippageBps(50).serialize());
    assertEquals(MINIMAL_QUERY + "&slippageBps=50", minimal().slippageBps(OptionalInt.of(50)).serialize());
    assertEquals(
        MINIMAL_QUERY + "&priorityFeeLamports=5000&broadcastFeeType=maxCap",
        minimal().priorityFeeLamports(5_000).broadcastFeeType(BroadcastFeeType.maxCap).serialize()
    );
    assertEquals(
        MINIMAL_QUERY + "&priorityFeeLamports=5000&broadcastFeeType=maxCap",
        minimal().priorityFeeLamports(OptionalLong.of(5_000)).broadcastFeeType(BroadcastFeeType.maxCap).serialize()
    );
    assertEquals(
        MINIMAL_QUERY + "&jitoTipLamports=10000&broadcastFeeType=maxCap",
        minimal().jitoTipLamports(10_000).broadcastFeeType(BroadcastFeeType.maxCap).serialize()
    );
    assertEquals(
        MINIMAL_QUERY + "&jitoTipLamports=10000&broadcastFeeType=maxCap",
        minimal().jitoTipLamports(OptionalLong.of(10_000)).broadcastFeeType(BroadcastFeeType.maxCap).serialize()
    );

    // an empty optional clears a value set earlier, handing the choice back to Jupiter
    final var clearedByEmpty = minimal()
        .slippageBps(50)
        .priorityFeeLamports(5_000)
        .jitoTipLamports(10_000)
        .slippageBps(OptionalInt.empty())
        .priorityFeeLamports(OptionalLong.empty())
        .jitoTipLamports(OptionalLong.empty());
    assertEquals(OptionalInt.empty(), clearedByEmpty.slippageBps());
    assertEquals(OptionalLong.empty(), clearedByEmpty.priorityFeeLamports());
    assertEquals(OptionalLong.empty(), clearedByEmpty.jitoTipLamports());
    assertEquals(List.of("inputMint", "outputMint", "amount"), names(clearedByEmpty.createRequest().serialize()));

    // and so does a null optional
    final var clearedByNull = minimal()
        .slippageBps(50)
        .priorityFeeLamports(5_000)
        .jitoTipLamports(10_000)
        .slippageBps(null)
        .priorityFeeLamports(null)
        .jitoTipLamports(null);
    assertEquals(OptionalInt.empty(), clearedByNull.slippageBps());
    assertEquals(OptionalLong.empty(), clearedByNull.priorityFeeLamports());
    assertEquals(OptionalLong.empty(), clearedByNull.jitoTipLamports());
    assertEquals(List.of("inputMint", "outputMint", "amount"), names(clearedByNull.createRequest().serialize()));

    // and every optional set to its default is the same as unset
    final var explicitDefaults = minimal()
        .taker(null)
        .receiver(null)
        .swapMode(null)
        .slippageBps(OptionalInt.empty())
        .referralAccount(null)
        .referralFee(0)
        .payer(null)
        .priorityFeeLamports(OptionalLong.empty())
        .jitoTipLamports(OptionalLong.empty())
        .broadcastFeeType(null)
        .excludeRouters(null)
        .excludeDexes(List.of());
    assertEquals(List.of("inputMint", "outputMint", "amount"), names(explicitDefaults.createRequest().serialize()));
  }

  @Test
  void negativeValuesAreRejectedBySetters() {
    final var builder = minimal();
    assertEquals(
        "slippageBps must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> builder.slippageBps(-1)).getMessage()
    );
    assertEquals(
        "referralFee must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> builder.referralFee(-1)).getMessage()
    );
    assertEquals(
        "priorityFeeLamports must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> builder.priorityFeeLamports(-1L)).getMessage()
    );
    assertEquals(
        "jitoTipLamports must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> builder.jitoTipLamports(-1L)).getMessage()
    );
    // so is a negative value held by an optional
    assertEquals(
        "slippageBps must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> builder.slippageBps(OptionalInt.of(-1))).getMessage()
    );
    assertEquals(
        "priorityFeeLamports must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> builder.priorityFeeLamports(OptionalLong.of(-1L))).getMessage()
    );
    assertEquals(
        "jitoTipLamports must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> builder.jitoTipLamports(OptionalLong.of(-1L))).getMessage()
    );
    // a rejected value leaves the builder as it was
    assertEquals(MINIMAL_QUERY, builder.serialize());
    assertEquals(OptionalInt.empty(), builder.slippageBps());
    assertEquals(0, builder.referralFee());

    // including a value set earlier, which a rejected optional does not clear
    final var set = minimal()
        .slippageBps(50)
        .priorityFeeLamports(5_000)
        .jitoTipLamports(10_000)
        .broadcastFeeType(BroadcastFeeType.maxCap);
    assertThrows(IllegalArgumentException.class, () -> set.slippageBps(OptionalInt.of(-1)));
    assertThrows(IllegalArgumentException.class, () -> set.priorityFeeLamports(OptionalLong.of(-1L)));
    assertThrows(IllegalArgumentException.class, () -> set.jitoTipLamports(OptionalLong.of(-1L)));
    assertEquals(
        MINIMAL_QUERY + "&slippageBps=50&priorityFeeLamports=5000&jitoTipLamports=10000&broadcastFeeType=maxCap",
        set.serialize()
    );

    // 0 is accepted by all four
    final var zeros = minimal()
        .slippageBps(0)
        .referralFee(0)
        .priorityFeeLamports(0)
        .jitoTipLamports(0)
        .broadcastFeeType(BroadcastFeeType.maxCap);
    assertEquals(OptionalInt.of(0), zeros.slippageBps());
    assertEquals(0, zeros.referralFee());
    assertEquals(OptionalLong.of(0), zeros.priorityFeeLamports());
    assertEquals(OptionalLong.of(0), zeros.jitoTipLamports());
    assertEquals(
        MINIMAL_QUERY + "&slippageBps=0&priorityFeeLamports=0&jitoTipLamports=0&broadcastFeeType=maxCap",
        zeros.createRequest().serialize()
    );

    // and held by an optional
    final var optionalZeros = minimal()
        .slippageBps(OptionalInt.of(0))
        .priorityFeeLamports(OptionalLong.of(0L))
        .jitoTipLamports(OptionalLong.of(0L))
        .broadcastFeeType(BroadcastFeeType.maxCap);
    assertEquals(OptionalInt.of(0), optionalZeros.slippageBps());
    assertEquals(OptionalLong.of(0), optionalZeros.priorityFeeLamports());
    assertEquals(OptionalLong.of(0), optionalZeros.jitoTipLamports());
    assertEquals(
        MINIMAL_QUERY + "&slippageBps=0&priorityFeeLamports=0&jitoTipLamports=0&broadcastFeeType=maxCap",
        optionalZeros.createRequest().serialize()
    );
  }

  @Test
  void swapModeAndBroadcastFeeTypeUseTheirWireNames() {
    assertEquals(MINIMAL_QUERY + "&swapMode=ExactIn", minimal().swapMode(SwapMode.ExactIn).createRequest().serialize());
    // ExactOut is passed through for Jupiter to answer
    assertEquals(MINIMAL_QUERY + "&swapMode=ExactOut", minimal().swapMode(SwapMode.ExactOut).createRequest().serialize());
    assertEquals(
        MINIMAL_QUERY + "&broadcastFeeType=maxCap",
        minimal().broadcastFeeType(BroadcastFeeType.maxCap).createRequest().serialize()
    );
    assertEquals(
        MINIMAL_QUERY + "&broadcastFeeType=exactFee",
        minimal().broadcastFeeType(BroadcastFeeType.exactFee).createRequest().serialize()
    );

    // null clears either
    final var cleared = minimal()
        .swapMode(SwapMode.ExactOut)
        .broadcastFeeType(BroadcastFeeType.maxCap)
        .swapMode(null)
        .broadcastFeeType(null);
    assertNull(cleared.swapMode());
    assertNull(cleared.broadcastFeeType());
    assertEquals(MINIMAL_QUERY, cleared.serialize());
  }

  @Test
  void exclusionListsAreJoinedAndUtf8Encoded() {
    final var routers = minimal().excludeRouters(List.of("jupiterz", "dflow"));
    assertEquals(List.of("jupiterz", "dflow"), routers.excludeRouters());
    assertEquals(List.of("jupiterz%2Cdflow"), values(routers.createRequest().serialize(), "excludeRouters"));
    // the caller's order is kept, never sorted
    assertEquals(
        List.of("dflow%2Cjupiterz"),
        values(minimal().excludeRouters(List.of("dflow", "jupiterz")).serialize(), "excludeRouters")
    );

    final var dexes = minimal().excludeDexes(List.of("Meteora DLMM", "Héllo"));
    assertEquals(List.of("Meteora DLMM", "Héllo"), dexes.excludeDexes());
    assertEquals(List.of("Meteora+DLMM%2CH%C3%A9llo"), values(dexes.createRequest().serialize(), "excludeDexes"));

    // both lists can be sent together
    assertEquals(
        MINIMAL_QUERY + "&excludeRouters=metis&excludeDexes=SolFi",
        minimal().excludeRouters(List.of("metis")).excludeDexes(List.of("SolFi")).createRequest().serialize()
    );

    // a hostile name cannot add a parameter
    final var hostile = minimal().slippageBps(50).excludeRouters(List.of("okx&slippageBps=9999")).serialize();
    assertEquals(List.of("50"), values(hostile, "slippageBps"));
    assertEquals(List.of("okx%26slippageBps%3D9999"), values(hostile, "excludeRouters"));

    // names that cannot be sent are rejected, naming the parameter, and leave the previous list in place; so is a
    // padded name: it can be sent, but Jupiter does not trim DEX labels, so it names no DEX
    final var builder = minimal().excludeRouters(List.of("metis")).excludeDexes(List.of("SolFi"));
    final List<List<String>> unsendable = List.of(
        Arrays.asList("okx", null),
        List.of(""),
        List.of(" "),
        List.of("jupiterz,dflow"),
        List.of(",okx"),
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
    for (final var names : unsendable) {
      assertThrows(IllegalArgumentException.class, () -> builder.excludeRouters(names), names::toString);
      assertThrows(IllegalArgumentException.class, () -> builder.excludeDexes(names), names::toString);
    }
    assertEquals(List.of("metis"), builder.excludeRouters());
    assertEquals(List.of("SolFi"), builder.excludeDexes());
    assertEquals(
        "excludeRouters must not contain null",
        assertThrows(IllegalArgumentException.class, () -> builder.excludeRouters(Arrays.asList("okx", null))).getMessage()
    );
    assertEquals(
        "excludeRouters must not contain a blank value: \"\"",
        assertThrows(IllegalArgumentException.class, () -> builder.excludeRouters(List.of(""))).getMessage()
    );
    assertEquals(
        "excludeRouters must not contain a value with a comma: \"jupiterz,dflow\"",
        assertThrows(IllegalArgumentException.class, () -> builder.excludeRouters(List.of("jupiterz,dflow"))).getMessage()
    );
    assertEquals(
        "excludeDexes must not contain a blank value: \" \"",
        assertThrows(IllegalArgumentException.class, () -> builder.excludeDexes(List.of(" "))).getMessage()
    );
    assertEquals(
        "excludeDexes must not have leading or trailing whitespace: \" Orca\"",
        assertThrows(IllegalArgumentException.class, () -> builder.excludeDexes(List.of(" Orca"))).getMessage()
    );
    assertEquals(
        "excludeRouters must not have leading or trailing whitespace: \"okx \"",
        assertThrows(IllegalArgumentException.class, () -> builder.excludeRouters(List.of("okx "))).getMessage()
    );

    // null clears, and the caller's collection is copied
    assertSame(builder, builder.excludeRouters(null));
    assertEquals(List.of(), builder.excludeRouters());
    final var labels = new ArrayList<>(List.of("SolFi"));
    final var copied = minimal().excludeRouters(labels).excludeDexes(labels);
    labels.add("Orca V2");
    assertEquals(List.of("SolFi"), copied.excludeRouters());
    assertEquals(List.of("SolFi"), copied.excludeDexes());
    assertThrows(UnsupportedOperationException.class, () -> copied.excludeRouters().add("okx"));
    assertThrows(UnsupportedOperationException.class, () -> copied.createRequest().excludeDexes().add("okx"));
  }

  /// The ranges Jupiter enforces are left to Jupiter, including a referral fee above and below 50-255.
  @Test
  void serverOwnedRangesAreSentAsGiven() {
    assertEquals(
        MINIMAL_QUERY + "&referralAccount=3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT&referralFee=256",
        minimal().referralAccount(REFERRAL).referralFee(256).createRequest().serialize()
    );
    assertEquals(
        List.of("1"),
        values(minimal().referralAccount(REFERRAL).referralFee(1).createRequest().serialize(), "referralFee")
    );
    assertEquals(MINIMAL_QUERY + "&slippageBps=10001", minimal().slippageBps(10001).createRequest().serialize());
  }

  @Test
  void serverOwnedRulesAreSentAsGiven() {
    final var payerIsTaker = minimal().taker(TAKER).payer(TAKER).createRequest().serialize();
    assertEquals(List.of("GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ"), values(payerIsTaker, "taker"));
    assertEquals(List.of("GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ"), values(payerIsTaker, "payer"));

    final var sameMint = JupiterSwapOrderRequest.buildRequest()
        .inputMint(WSOL)
        .outputMint(WSOL)
        .amount(1_000_000)
        .createRequest()
        .serialize();
    assertEquals(List.of("So11111111111111111111111111111111111111112"), values(sameMint, "inputMint"));
    assertEquals(List.of("So11111111111111111111111111111111111111112"), values(sameMint, "outputMint"));

    // Jupiter ignores a broadcast fee type with neither a priority fee nor a tip; it is sent all the same
    final var feeTypeAlone = minimal().broadcastFeeType(BroadcastFeeType.maxCap).createRequest().serialize();
    assertEquals(List.of("maxCap"), values(feeTypeAlone, "broadcastFeeType"));
    final var names = names(feeTypeAlone);
    assertFalse(names.contains("priorityFeeLamports"), feeTypeAlone);
    assertFalse(names.contains("jitoTipLamports"), feeTypeAlone);
  }

  @Test
  void missingRequiredParametersAreRejectedBySerialize() {
    assertMissing(
        "/swap/v2/order requires inputMint",
        JupiterSwapOrderRequest.buildRequest().outputMint(USDC).amount(1_000_000)
    );
    assertMissing(
        "/swap/v2/order requires outputMint",
        JupiterSwapOrderRequest.buildRequest().inputMint(WSOL).amount(1_000_000)
    );
    assertMissing("/swap/v2/order requires amount", JupiterSwapOrderRequest.buildRequest().inputMint(WSOL).outputMint(USDC));
    assertMissing("/swap/v2/order requires amount", minimal().amount(0));

    // the first missing parameter is named, in parameter order
    assertMissing("/swap/v2/order requires inputMint", JupiterSwapOrderRequest.buildRequest());
    assertMissing("/swap/v2/order requires outputMint", JupiterSwapOrderRequest.buildRequest().inputMint(WSOL));

    // and a missing parameter is reported before a contradiction
    final var incompleteAndContradictory = JupiterSwapOrderRequest.buildRequest()
        .inputMint(WSOL)
        .outputMint(USDC)
        .referralFee(50);
    assertEquals(
        "/swap/v2/order requires amount",
        assertThrows(IllegalStateException.class, incompleteAndContradictory::serialize).getMessage()
    );
  }

  @Test
  void everySetterReturnsItsBuilder() {
    final var builder = JupiterSwapOrderRequest.buildRequest();
    assertSame(builder, builder.inputMint(USDC));
    assertSame(builder, builder.outputMint(WSOL));
    assertSame(builder, builder.amount(100_000_000));
    assertSame(builder, builder.taker(TAKER));
    assertSame(builder, builder.receiver(OTHER));
    assertSame(builder, builder.swapMode(SwapMode.ExactIn));
    assertSame(builder, builder.slippageBps(0));
    assertSame(builder, builder.slippageBps(OptionalInt.of(25)));
    assertSame(builder, builder.referralAccount(REFERRAL));
    assertSame(builder, builder.referralFee(50));
    assertSame(builder, builder.payer(PAYER));
    assertSame(builder, builder.priorityFeeLamports(0));
    assertSame(builder, builder.priorityFeeLamports(OptionalLong.of(5_000)));
    assertSame(builder, builder.jitoTipLamports(10_000));
    assertSame(builder, builder.jitoTipLamports(OptionalLong.of(20_000)));
    assertSame(builder, builder.broadcastFeeType(BroadcastFeeType.exactFee));
    assertSame(builder, builder.excludeRouters(List.of("jupiterz")));
    assertSame(builder, builder.excludeDexes(List.of("Orca V2")));
  }

  @Test
  void prototypeBuilderCarriesEveryFieldForward() {
    // O1 sets all 15 parameters, the keys distinct and every value off its default
    final var full = full().createRequest();
    assertEquals(FULL_QUERY, full.serialize());
    assertEquals(FULL_QUERY, JupiterSwapOrderRequest.buildRequest(full).createRequest().serialize());
    assertFullFields(JupiterSwapOrderRequest.buildRequest(full));

    // overriding one parameter changes only that pair
    assertEquals(
        FULL_QUERY_WITH_SLIPPAGE_25,
        JupiterSwapOrderRequest.buildRequest(full).slippageBps(25).createRequest().serialize()
    );
    assertEquals(OptionalInt.of(0), full.slippageBps());

    // a builder made from a builder is a copy
    final var original = minimal().slippageBps(50);
    final var copy = JupiterSwapOrderRequest.buildRequest(original).slippageBps(25);
    assertNotSame(original, copy);
    assertEquals(OptionalInt.of(50), original.slippageBps());
    assertEquals(OptionalInt.of(25), copy.slippageBps());

    // no prototype gives an empty builder
    final var empty = JupiterSwapOrderRequest.buildRequest(null);
    assertEmpty(empty);
    assertEquals(
        "/swap/v2/order requires inputMint",
        assertThrows(IllegalStateException.class, empty::serialize).getMessage()
    );
    assertEmpty(JupiterSwapOrderRequest.buildRequest());
  }

  @Test
  void builderAndRecordAccessorsReportEverySetField() {
    final var builder = full();
    assertFullFields(builder);
    final var request = builder.createRequest();
    assertFullFields(request);
    // the request is a copy: changing the builder afterwards does not reach it
    builder.slippageBps(25).excludeRouters(List.of("okx")).receiver(PAYER).jitoTipLamports(1);
    assertFullFields(request);
  }

  /// Every field lands from its API name, and unknown fields (leading, mid-object and trailing; scalar and
  /// structured, one holding a known name) are skipped without shifting the fields after them.
  @Test
  void parsedRequestReadsEveryFieldPastUnknownNeighbors() {
    final var parsed = parse("""
        {
          "unknownLeading": {"nested": [1, {"deep": true}]},
          "inputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
          "outputMint": "So11111111111111111111111111111111111111112",
          "amount": "18446744073709551615",
          "taker": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ",
          "unknownMid": ["s", 2.5, false, null],
          "receiver": "9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn",
          "swapMode": "ExactOut",
          "slippageBps": 0,
          "referralAccount": "3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT",
          "referralFee": 50,
          "unknownObject": {"referralFee": 255},
          "payer": "gasTzr94Pmp4Gf8vknQnqxeYxdgwFjbgdJa4msYRpnB",
          "priorityFeeLamports": 0,
          "jitoTipLamports": 10000,
          "broadcastFeeType": "exactFee",
          "excludeRouters": ["jupiterz", "dflow"],
          "excludeDexes": ["Orca V2"],
          "unknownTrailing": "ignored"
        }""");
    assertEquals(USDC, parsed.inputMint());
    assertEquals(WSOL, parsed.outputMint());
    assertEquals(-1L, parsed.amount());
    assertEquals(TAKER, parsed.taker());
    assertEquals(OTHER, parsed.receiver());
    assertEquals(SwapMode.ExactOut, parsed.swapMode());
    assertEquals(OptionalInt.of(0), parsed.slippageBps());
    assertEquals(REFERRAL, parsed.referralAccount());
    assertEquals(50, parsed.referralFee());
    assertEquals(PAYER, parsed.payer());
    assertEquals(OptionalLong.of(0), parsed.priorityFeeLamports());
    assertEquals(OptionalLong.of(10_000), parsed.jitoTipLamports());
    assertEquals(BroadcastFeeType.exactFee, parsed.broadcastFeeType());
    assertEquals(List.of("jupiterz", "dflow"), parsed.excludeRouters());
    assertEquals(List.of("Orca V2"), parsed.excludeDexes());
    assertEquals(
        "inputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&outputMint=So11111111111111111111111111111111111111112&amount=18446744073709551615&taker=GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ&receiver=9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn&swapMode=ExactOut&slippageBps=0&referralAccount=3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT&referralFee=50&payer=gasTzr94Pmp4Gf8vknQnqxeYxdgwFjbgdJa4msYRpnB&priorityFeeLamports=0&jitoTipLamports=10000&broadcastFeeType=exactFee&excludeRouters=jupiterz%2Cdflow&excludeDexes=Orca+V2",
        parsed.serialize()
    );

    // the quote-only shape, with a bare-number amount
    final var quoteOnly = parse("""
        {"amount": 1000000, "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", "inputMint": "So11111111111111111111111111111111111111112"}""");
    assertEquals(MINIMAL_QUERY, quoteOnly.serialize());

    // a template read over a prototype keeps what it does not name, and a JSON null clears a key
    final var prototype = full();
    final var overridden = JupiterSwapOrderRequest.parseRequest(prototype, JsonIterator.parse("""
        {"slippageBps": 25, "receiver": null}""".getBytes(UTF_8)));
    assertEquals(
        "inputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&outputMint=So11111111111111111111111111111111111111112&amount=100000000&taker=GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ&swapMode=ExactIn&slippageBps=25&referralAccount=3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT&referralFee=50&payer=gasTzr94Pmp4Gf8vknQnqxeYxdgwFjbgdJa4msYRpnB&priorityFeeLamports=0&jitoTipLamports=10000&broadcastFeeType=exactFee&excludeRouters=jupiterz%2Cdflow&excludeDexes=Orca+V2",
        overridden.serialize()
    );
    // and the prototype itself is untouched
    assertEquals(OTHER, prototype.receiver());
    assertEquals(OptionalInt.of(0), prototype.slippageBps());

    // a JSON null clears an optional number too, handing the choice back to Jupiter
    final var cleared = JupiterSwapOrderRequest.parseRequest(prototype, JsonIterator.parse("""
        {"slippageBps": null, "priorityFeeLamports": null, "jitoTipLamports": null}""".getBytes(UTF_8)));
    assertEquals(OptionalInt.empty(), cleared.slippageBps());
    assertEquals(OptionalLong.empty(), cleared.priorityFeeLamports());
    assertEquals(OptionalLong.empty(), cleared.jitoTipLamports());
    assertEquals(
        "inputMint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&outputMint=So11111111111111111111111111111111111111112&amount=100000000&taker=GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ&receiver=9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn&swapMode=ExactIn&referralAccount=3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT&referralFee=50&payer=gasTzr94Pmp4Gf8vknQnqxeYxdgwFjbgdJa4msYRpnB&broadcastFeeType=exactFee&excludeRouters=jupiterz%2Cdflow&excludeDexes=Orca+V2",
        cleared.serialize()
    );
    assertEquals(OptionalInt.of(0), prototype.slippageBps());
    assertEquals(OptionalLong.of(0), prototype.priorityFeeLamports());
    assertEquals(OptionalLong.of(10_000), prototype.jitoTipLamports());

    // contradictions are rejected at parse, required parameters are not
    assertEquals(
        "referralAccount and referralFee must be set together",
        assertThrows(IllegalStateException.class, () -> parse("""
            {"referralFee": 50}""")).getMessage()
    );
    final var template = parse("""
        {"slippageBps": 30}""");
    assertEquals(OptionalInt.of(30), template.slippageBps());
    assertEquals(
        "/swap/v2/order requires inputMint",
        assertThrows(IllegalStateException.class, template::serialize).getMessage()
    );
  }

  @Test
  void parsedEnumValuesIgnoreCaseAndUnknownNamesAreRejected() {
    assertEquals(SwapMode.ExactIn, parse("""
        {"swapMode": "exactin"}""").swapMode());
    assertEquals(SwapMode.ExactOut, parse("""
        {"swapMode": "EXACTOUT"}""").swapMode());
    assertEquals(BroadcastFeeType.maxCap, parse("""
        {"broadcastFeeType": "MaxCap"}""").broadcastFeeType());
    assertEquals(BroadcastFeeType.exactFee, parse("""
        {"broadcastFeeType": "EXACTFEE"}""").broadcastFeeType());

    assertEquals(
        "swapMode must be one of [ExactIn, ExactOut], ignoring case",
        assertThrows(IllegalArgumentException.class, () -> parse("""
            {"swapMode": "ExactBoth"}""")).getMessage()
    );
    assertEquals(
        "broadcastFeeType must be one of [maxCap, exactFee], ignoring case",
        assertThrows(IllegalArgumentException.class, () -> parse("""
            {"broadcastFeeType": "max"}""")).getMessage()
    );
    // an empty string is an unknown name, not a JSON null
    assertEquals(
        "swapMode must be one of [ExactIn, ExactOut], ignoring case",
        assertThrows(IllegalArgumentException.class, () -> parse("""
            {"swapMode": ""}""")).getMessage()
    );
    assertEquals(
        "broadcastFeeType must be one of [maxCap, exactFee], ignoring case",
        assertThrows(IllegalArgumentException.class, () -> parse("""
            {"broadcastFeeType": ""}""")).getMessage()
    );

    // a value a setter refuses is refused from a template too
    assertEquals(
        "referralFee must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> parse("""
            {"referralFee": -1}""")).getMessage()
    );
    // slippageBps is a number on /order, so /build's rtse is refused
    assertThrows(JsonException.class, () -> parse("""
        {"slippageBps": "rtse"}"""));
    // an amount is a u64: no sign, no null, no garbage
    assertThrows(NumberFormatException.class, () -> parse("""
        {"amount": -1}"""));
    assertThrows(NumberFormatException.class, () -> parse("""
        {"amount": null}"""));
    assertThrows(NumberFormatException.class, () -> parse("""
        {"amount": "lots"}"""));
  }

  /// A JSON null hands `swapMode` and `broadcastFeeType` back to Jupiter through their clearing setters, as it does
  /// the optional numbers, and the prototype keeps its values.
  @Test
  void aJsonNullClearsSwapModeAndBroadcastFeeType() {
    final var prototype = minimal()
        .swapMode(SwapMode.ExactOut)
        .priorityFeeLamports(5_000)
        .broadcastFeeType(BroadcastFeeType.maxCap);
    assertEquals(
        MINIMAL_QUERY + "&swapMode=ExactOut&priorityFeeLamports=5000&broadcastFeeType=maxCap",
        prototype.serialize()
    );

    final var noSwapMode = parse(prototype, """
        {"swapMode": null}""");
    assertNull(noSwapMode.swapMode());
    assertEquals(MINIMAL_QUERY + "&priorityFeeLamports=5000&broadcastFeeType=maxCap", noSwapMode.serialize());

    // clearing broadcastFeeType alone leaves the prototype's fee without one, so the template is rejected
    assertEquals(
        "priorityFeeLamports and jitoTipLamports require broadcastFeeType",
        assertThrows(IllegalStateException.class, () -> parse(prototype, """
            {"broadcastFeeType": null}""")).getMessage()
    );
    // and cleared together with the fee, it is gone while the rest of the prototype stays
    final var noFeeType = parse(prototype, """
        {"broadcastFeeType": null, "priorityFeeLamports": null}""");
    assertNull(noFeeType.broadcastFeeType());
    assertEquals(MINIMAL_QUERY + "&swapMode=ExactOut", noFeeType.serialize());

    // and the prototype is untouched
    assertEquals(SwapMode.ExactOut, prototype.swapMode());
    assertEquals(OptionalLong.of(5_000), prototype.priorityFeeLamports());
    assertEquals(BroadcastFeeType.maxCap, prototype.broadcastFeeType());
  }

  /// A template is checked once every key has been read, so a fee or tip it carries needs a `broadcastFeeType`
  /// from the template, in either key order, or from the prototype.
  @Test
  void aParsedPriorityFeeOrTipNeedsBroadcastFeeType() {
    final var message = "priorityFeeLamports and jitoTipLamports require broadcastFeeType";
    assertEquals(message, assertThrows(IllegalStateException.class, () -> parse("""
        {"priorityFeeLamports": 5000}""")).getMessage());
    assertEquals(message, assertThrows(IllegalStateException.class, () -> parse("""
        {"jitoTipLamports": 10000}""")).getMessage());
    assertEquals(message, assertThrows(IllegalStateException.class, () -> parse("""
        {"priorityFeeLamports": 0, "jitoTipLamports": 0}""")).getMessage());
    // a JSON null broadcastFeeType is none
    assertEquals(message, assertThrows(IllegalStateException.class, () -> parse("""
        {"jitoTipLamports": 10000, "broadcastFeeType": null}""")).getMessage());
    // and so is a prototype's unset one
    assertEquals(message, assertThrows(IllegalStateException.class, () -> parse(minimal(), """
        {"priorityFeeLamports": 5000}""")).getMessage());

    // a broadcastFeeType after the fee or before the tip is read all the same
    final var fee = parse("""
        {"priorityFeeLamports": 5000, "broadcastFeeType": "maxCap"}""");
    assertEquals(OptionalLong.of(5_000), fee.priorityFeeLamports());
    assertEquals(BroadcastFeeType.maxCap, fee.broadcastFeeType());
    final var tip = parse("""
        {"broadcastFeeType": "exactFee", "jitoTipLamports": 10000}""");
    assertEquals(OptionalLong.of(10_000), tip.jitoTipLamports());
    assertEquals(BroadcastFeeType.exactFee, tip.broadcastFeeType());
    // as is the prototype's
    final var overPrototype = parse(minimal().broadcastFeeType(BroadcastFeeType.maxCap), """
        {"jitoTipLamports": 10000}""");
    assertEquals(MINIMAL_QUERY + "&jitoTipLamports=10000&broadcastFeeType=maxCap", overPrototype.serialize());

    // a JSON null fee or tip needs none
    final var nulls = parse("""
        {"priorityFeeLamports": null, "jitoTipLamports": null}""");
    assertEquals(OptionalLong.empty(), nulls.priorityFeeLamports());
    assertEquals(OptionalLong.empty(), nulls.jitoTipLamports());
    assertNull(nulls.broadcastFeeType());
  }

  /// The rule `parseRequest` documents: a JSON null unsets a parameter, except `amount` and `referralFee`, which
  /// throw on it. So one template hands every parameter of O1 back to Jupiter.
  @Test
  void aJsonNullUnsetsAParameterUnlessZeroDoes() {
    final var full = full();
    assertEmpty(parse(full, """
        {
          "inputMint": null,
          "outputMint": null,
          "amount": 0,
          "taker": null,
          "receiver": null,
          "swapMode": null,
          "slippageBps": null,
          "referralAccount": null,
          "referralFee": 0,
          "payer": null,
          "priorityFeeLamports": null,
          "jitoTipLamports": null,
          "broadcastFeeType": null,
          "excludeRouters": null,
          "excludeDexes": null
        }"""));
    assertFullFields(full);

    // the two that 0 unsets throw on a JSON null
    assertThrows(NumberFormatException.class, () -> parse("""
        {"amount": null}"""));
    assertThrows(JsonException.class, () -> parse("""
        {"referralFee": null}"""));
  }
}

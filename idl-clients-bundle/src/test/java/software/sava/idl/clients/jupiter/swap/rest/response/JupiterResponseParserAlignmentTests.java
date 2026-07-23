package software.sava.idl.clients.jupiter.swap.rest.response;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

/// Every REST response parser routes unrecognized fields through `ji.skip()`,
/// which returns its iterator — so a dropped skip is an expressible mutation
/// that leaves the iterator parked inside the unknown value, misreading every
/// field after it. The API adds fields without notice, so each parser is fed a
/// fixture with unknown neighbors — leading, in between, trailing; scalar and
/// structured — and must land the same values it reads from a clean fixture.
final class JupiterResponseParserAlignmentTests {

  private static final String WSOL = "So11111111111111111111111111111111111111112";
  private static final String USDC = "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v";
  private static final PublicKey WSOL_KEY = PublicKey.fromBase58Encoded(WSOL);
  private static final PublicKey USDC_KEY = PublicKey.fromBase58Encoded(USDC);

  @Test
  void quoteRouteAndPlatformFeeParsePastUnknownFields() {
    final String json = """
        {"unknownLeading":{"a":[1,{"b":2}]},
         "inputMint":"%s","inAmount":"1000",
         "unknownMid":[true,"x",1.25],
         "outputMint":"%s","outAmount":"25","otherAmountThreshold":"24",
         "swapMode":"ExactIn","slippageBps":50,
         "platformFee":{"unknownFee":"?","amount":7,"feeBps":9,"unknownFeeTail":[]},
         "priceImpactPct":"0.5",
         "routePlan":[{"unknownRoute":{},"swapInfo":{"unknownInfo":8,"ammKey":"%s","label":"Orca",
           "inputMint":"%s","outputMint":"%s","inAmount":"1000","outAmount":"25","feeAmount":"3",
           "feeMint":"%s","unknownInfoTail":true},
           "percent":100,"bps":10000,"usdValue":1.5,"unknownRouteTail":0},
          {"swapInfo":{"ammKey":"%s"},"percent":null,"bps":null}],
         "contextSlot":42,"timeTaken":0.1,
         "unknownTrailing":"z"}""".formatted(WSOL, USDC, WSOL, WSOL, USDC, USDC, USDC);

    final byte[] bytes = json.getBytes(UTF_8);
    final var quote = JupiterQuote.parse(bytes, JsonIterator.parse(bytes));

    assertEquals(WSOL_KEY, quote.inputMint());
    assertEquals(1_000L, quote.inAmount());
    assertEquals(USDC_KEY, quote.outputMint());
    assertEquals(25L, quote.outAmount());
    assertEquals(24L, quote.otherAmountThreshold());
    assertEquals("ExactIn", quote.swapMode());
    assertEquals(50, quote.slippageBps());
    assertEquals(new PlatformFee(7L, 9), quote.platformFee());
    assertEquals(new BigDecimal("0.5"), quote.priceImpactPct());
    assertEquals(42L, quote.contextSlot());
    assertEquals(0.1d, quote.timeTaken());

    assertEquals(2, quote.routePlan().size());
    final var route = quote.routePlan().getFirst();
    assertEquals(WSOL_KEY, route.ammKey());
    assertEquals("Orca", route.label());
    assertEquals(WSOL_KEY, route.inputMint());
    assertEquals(USDC_KEY, route.outputMint());
    assertEquals(1_000L, route.inAmount());
    assertEquals(25L, route.outAmount());
    assertEquals(3L, route.feeAmount());
    assertEquals(USDC_KEY, route.feeMint());
    assertEquals(100, route.percent());
    assertEquals(10_000, route.bps());
    assertEquals(new BigDecimal("1.5"), route.usdValue());

    // JSON null percent/bps deliberately parse to the -1 sentinel, not zero —
    // the notNull() checks are only observable through a null-carrying route
    final var nullRoute = quote.routePlan().getLast();
    assertEquals(USDC_KEY, nullRoute.ammKey());
    assertEquals(-1, nullRoute.percent());
    assertEquals(-1, nullRoute.bps());
  }

  @Test
  void executeOrderAndSwapEventsParsePastUnknownFields() {
    final String json = """
        {"unknownLeading":{"deep":[{}]},
         "status":"Success","signature":"sig","slot":123456789,
         "unknownMid":[1,2],
         "code":7,"totalInputAmount":1000,"totalOutputAmount":25,
         "inputAmountResult":"1000","outputAmountResult":"25",
         "swapEvents":[{"unknownEvent":true,"inputMint":"%s","inputAmount":"1000",
           "outputMint":"%s","outputAmount":"25","unknownEventTail":0}],
         "unknownTrailing":"x"}""".formatted(WSOL, USDC);

    final byte[] bytes = json.getBytes(UTF_8);
    final var order = JupiterExecuteOrder.parse(bytes, JsonIterator.parse(bytes));

    assertEquals("Success", order.status());
    assertEquals("sig", order.signature());
    assertEquals(BigInteger.valueOf(123_456_789L), order.slot());
    assertEquals(7L, order.code());
    assertEquals(1_000L, order.totalInputAmount());
    assertEquals(25L, order.totalOutputAmount());
    assertEquals("1000", order.inputAmountResult());
    assertEquals("25", order.outputAmountResult());
    assertEquals(1, order.swapEvents().size());
    final var event = order.swapEvents().getFirst();
    assertEquals(WSOL_KEY, event.inputMint());
    assertEquals(1_000L, event.inputAmount());
    assertEquals(USDC_KEY, event.outputMint());
    assertEquals(25L, event.outputAmount());
  }

  @Test
  void priceParsesPastUnknownFields() {
    final String json = """
        {"unknownLeading":[{"x":1}],
         "usdPrice":147.5,
         "unknownMid":true,
         "decimals":9,"liquidity":5.5,"priceChange24h":-1.25,
         "blockId":9876543210,
         "unknownTrailing":{}}""";

    final var price = JupiterPrice.parsePrice(WSOL_KEY, JsonIterator.parse(json.getBytes(UTF_8)));

    assertEquals(WSOL_KEY, price.mint());
    assertEquals(147.5d, price.usdPrice());
    assertEquals(9, price.decimals());
    assertEquals(5.5d, price.liquidity());
    assertEquals(-1.25d, price.priceChange24h());
    assertEquals(BigInteger.valueOf(9_876_543_210L), price.blockId());
  }

  @Test
  void ultraOrderParsesPastUnknownFields() {
    final String json = """
        {"unknownLeading":{"n":[]},
         "mode":"manual","requestId":"abc","inAmount":"500","outAmount":"20",
         "unknownMid":[false],
         "inputMint":"%s","outputMint":"%s",
         "gasless":true,"transaction":"AQID","totalTime":250,
         "unknownTrailing":1}""".formatted(WSOL, USDC);

    final var order = JupiterUltraOrder.parse(JsonIterator.parse(json.getBytes(UTF_8)));

    assertEquals("manual", order.mode());
    assertEquals("abc", order.requestId());
    assertEquals(500L, order.inAmount());
    assertEquals(20L, order.outAmount());
    assertEquals(WSOL_KEY, order.inputMint());
    assertEquals(USDC_KEY, order.outputMint());
    assertTrue(order.gasless());
    assertArrayEquals(new byte[]{1, 2, 3}, order.transaction());
    assertEquals(250L, order.totalTime());
  }

  @Test
  void swapTxParsesPastUnknownFields() {
    final String json = """
        {"unknownLeading":{"b":1},
         "swapTransaction":"AQID",
         "unknownMid":[2],
         "lastValidBlockHeight":123,"prioritizationFeeLamports":45,
         "unknownTrailing":"x"}""";

    final var tx = JupiterSwapTx.parse(JsonIterator.parse(json.getBytes(UTF_8)));

    assertArrayEquals(new byte[]{1, 2, 3}, tx.swapTransaction());
    assertEquals(123L, tx.lastValidBlockHeight());
    assertEquals(45, tx.prioritizationFeeLamports());
  }

  /// The token parser has three distinct skip paths: a plain unknown field, a
  /// `stats*` field whose unit suffix is unrecognized (skipped as a whole
  /// object), and unknown fields *inside* a recognized stats object, the
  /// nested pool, and the audit. The `stats5m`/`stats24h` names also pin the
  /// suffix arithmetic that extracts the duration from the field name.
  @Test
  void tokenV2StatsPoolAndAuditParsePastUnknownFields() {
    final String json = """
        {"unknownLeading":{"deep":true},
         "id":"%s","name":"Wrapped SOL","symbol":"SOL","decimals":9,
         "stats30s":{"priceChange":9.9},
         "stats5m":{"unknownStat":3,"priceChange":1.5,"numBuys":7,"unknownStatTail":[1]},
         "stats24h":{"priceChange":-2.5},
         "warmth":true,
         "usdPrice":147.5,"holderCount":100,
         "firstPool":{"unknownPool":1,"id":"pool1","createdAt":"2024-01-02T03:04:05Z","unknownPoolTail":[]},
         "audit":{"unknownAudit":{},"isSus":true,"mintAuthorityDisabled":true,
           "freezeAuthorityDisabled":true,"topHoldersPercentage":12.5,
           "devBalancePercentage":3.5,"devMigrations":2,"unknownAuditTail":0},
         "isVerified":true,"tags":["verified","strict"],
         "updatedAt":"2024-05-06T07:08:09Z",
         "unknownTrailing":"end"}""".formatted(WSOL);

    final var token = JupiterTokenV2.parseToken(JsonIterator.parse(json.getBytes(UTF_8)));

    assertEquals(WSOL_KEY, token.address());
    assertEquals("Wrapped SOL", token.name());
    assertEquals("SOL", token.symbol());
    assertEquals(9, token.decimals());
    assertEquals(147.5d, token.usdPrice());
    assertEquals(100L, token.holderCount());
    assertTrue(token.verified());
    assertTrue(token.tags().contains("strict"));
    assertEquals(Instant.parse("2024-05-06T07:08:09Z"), token.updatedAt());

    // stats30s carries an unrecognized unit and is skipped whole, and "warmth"
    // — a non-stats field that happens to end in the hours unit letter — must
    // route through the plain-unknown skip, not the stats-prefix branch, whose
    // duration parse would choke on it. Two entries.
    assertEquals(2, token.tokenStats().size());
    final var fiveMinutes = token.tokenStats().stream()
        .filter(s -> s.duration().equals(Duration.ofMinutes(5)))
        .findFirst().orElseThrow();
    assertEquals(1.5d, fiveMinutes.priceChange());
    assertEquals(7, fiveMinutes.numBuys());
    final var day = token.tokenStats().stream()
        .filter(s -> s.duration().equals(Duration.ofHours(24)))
        .findFirst().orElseThrow();
    assertEquals(-2.5d, day.priceChange());

    assertEquals(new TokenPool("pool1", Instant.parse("2024-01-02T03:04:05Z")), token.firstPool());
    // every audit field carries a non-default value, so a skipped field cannot
    // hide behind its default
    assertEquals(new TokenAudit(true, true, true, 12.5d, 3.5d, 2), token.audit());
  }
}

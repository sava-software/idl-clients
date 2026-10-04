package software.sava.idl.clients.jupiter.swap.rest.response;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapV2TestFixtures.*;

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

  /// The `/build` parser skips at seven levels: the body (where the live API already sends
  /// `transactionVersion`), a route step, its `swapInfo`, an instruction, an account meta,
  /// `blockhashWithMetadata` and its `fetchedAt`.
  @Test
  void swapBuildParsesPastUnknownFields() {
    final String json = """
        {"unknownLeading":{"a":[1,{"b":2}]},
         "inputMint":"%s","outputMint":"%s",
         "transactionVersion":0,
         "inAmount":"1000","outAmount":"25","otherAmountThreshold":"24",
         "unknownMid":[true,"x",1.25],
         "swapMode":"ExactIn","slippageBps":50,"priceImpactPct":"0.5",
         "routePlan":[{"unknownRoute":{"r":[1]},"swapInfo":{"unknownInfo":8,"ammKey":"%s","label":"Orca",
           "inputMint":"%s","outputMint":"%s","inAmount":"1000","outAmount":"25","feeAmount":"3","feeMint":"%s",
           "unknownInfoTail":{"t":true}},
           "percent":100,"bps":10000,"usdValue":1.5,"unknownRouteTail":0}],
         "computeBudgetInstructions":[{"unknownIx":[1,2],"programId":"%s","accounts":[],"data":"A4fWEgAAAAAA",
           "unknownIxTail":"t"}],
         "setupInstructions":[],
         "swapInstruction":{"programId":"%s","unknownIxMid":{"m":1},
           "accounts":[{"unknownMeta":{"x":[]},"pubkey":"%s","isSigner":true,"unknownMetaMid":7,"isWritable":false,
             "unknownMetaTail":[null]}],
           "data":"AQID"},
         "cleanupInstruction":null,
         "otherInstructions":[{"programId":"%s","accounts":[],"data":"aGk=","unknownOtherTail":{"o":[]}}],
         "tipInstruction":null,
         "addressesByLookupTableAddress":{"%s":["%s"]},
         "blockhashWithMetadata":{"unknownHash":{"h":1},
           "blockhash":[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31],
           "unknownHashMid":"m","lastValidBlockHeight":279000150,
           "fetchedAt":{"unknownTime":[1],"secs_since_epoch":1759480000,"unknownTimeMid":{},
             "nanos_since_epoch":123456789,"unknownTimeTail":true},
           "unknownHashTail":[]},
         "unknownTrailing":"z"}""".formatted(
        WSOL, USDC, AMM_A, WSOL, USDC, USDC, CB, JUP, TAKER, MEMO, ALT, VAULT_A);

    final var build = JupiterSwapBuild.parse(JsonIterator.parse(json.getBytes(UTF_8)));

    assertEquals(WSOL_KEY, build.inputMint());
    assertEquals(USDC_KEY, build.outputMint());
    assertEquals(1_000L, build.inAmount());
    assertEquals(25L, build.outAmount());
    assertEquals(24L, build.otherAmountThreshold());
    assertEquals("ExactIn", build.swapMode());
    assertEquals(50, build.slippageBps());
    assertEquals(new BigDecimal("0.5"), build.priceImpactPct());

    assertEquals(1, build.routePlan().size());
    final var step = build.routePlan().getFirst();
    assertEquals("3EKkiwNLWqoUbzFkPrmKbtUB4EweE6f4STzevYUmezeL", step.ammKey());
    assertEquals("Orca", step.label());
    assertEquals(WSOL_KEY, step.inputMint());
    assertEquals(USDC_KEY, step.outputMint());
    assertEquals(1_000L, step.inAmount());
    assertEquals(25L, step.outAmount());
    assertEquals(new BigDecimal("100"), step.percent());
    assertEquals(new BigDecimal("10000"), step.bps());
    assertEquals(new BigDecimal("1.5"), step.usdValue());

    assertEquals(1, build.computeBudgetInstructions().size());
    final var price = build.computeBudgetInstructions().getFirst();
    assertEquals(CB_KEY, price.programId().publicKey());
    assertEquals(List.of(), price.accounts());
    assertArrayEquals(new byte[]{3, (byte) 135, (byte) 214, 18, 0, 0, 0, 0, 0}, price.copyData());
    assertEquals(List.of(), build.setupInstructions());

    final var swap = build.swapInstruction();
    assertEquals(JUP_KEY, swap.programId().publicKey());
    assertEquals(List.of(AccountMeta.createReadOnlySigner(TAKER_KEY)), swap.accounts());
    assertArrayEquals(new byte[]{1, 2, 3}, swap.copyData());
    assertNull(build.cleanupInstruction());
    assertEquals(1, build.otherInstructions().size());
    final var other = build.otherInstructions().getFirst();
    assertEquals(MEMO_KEY, other.programId().publicKey());
    assertEquals(List.of(), other.accounts());
    assertArrayEquals(new byte[]{'h', 'i'}, other.copyData());
    assertNull(build.tipInstruction());

    assertEquals(Map.of(ALT_KEY, List.of(VAULT_A_KEY)), build.addressesByLookupTableAddress());

    final var blockhash = build.blockhashWithMetadata();
    assertArrayEquals(new byte[]{
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
        16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31
    }, blockhash.blockhash());
    assertEquals(279_000_150L, blockhash.lastValidBlockHeight());
    assertEquals(Instant.ofEpochSecond(1_759_480_000L, 123_456_789L), blockhash.fetchedAt());
  }

  /// The `/order` parser skips unknown fields, fields the API has added (`jitOptimized`,
  /// `guaranteedPrice`) and the deprecated `swapType` and `priceImpactPct`, at the top level, in a
  /// route step, in its `swapInfo` and in `platformFee`. `error` is read, as the fallback for an
  /// absent `errorMessage`. The three fee payers and the taker are four different keys, so a
  /// transposed pair of these same-typed fields cannot hide behind an equal value.
  @Test
  void swapOrderParsesPastUnknownFields() {
    final String json = """
        {"unknownLeading":{"n":[{"x":1}]},
         "mode":"ultra","jitOptimized":true,
         "inputMint":"%s","outputMint":"%s","inAmount":"100","outAmount":"461",
         "guaranteedPrice":{"price":"4.61","tolerance":[1,2]},
         "inUsdValue":1.5,"outUsdValue":1.25,"priceImpact":-0.5,"swapUsdValue":1.75,"otherAmountThreshold":"460",
         "swapMode":"ExactIn","slippageBps":26,"priceImpactPct":"-0.005",
         "routePlan":[{"unknownRoute":[1],"swapInfo":{"unknownInfo":{"i":1},"ammKey":"%s","label":"SolFi",
           "inputMint":"%s","outputMint":"%s","inAmount":"100","outAmount":"461","feeAmount":"0","feeMint":"%s",
           "unknownInfoTail":null},
           "percent":100,"bps":10000,"usdValue":1.5,"unknownRouteTail":"t"}],
         "unknownMid":[false,{"y":2}],
         "referralAccount":"%s","feeMint":"%s","feeBps":2,
         "platformFee":{"unknownFee":"?","amount":"92","unknownFeeMid":[1],"feeBps":2,"feeMint":"%s",
           "unknownFeeTail":{}},
         "signatureFeeLamports":5000,"signatureFeePayer":"%s",
         "prioritizationFeeLamports":696237,"prioritizationFeePayer":"%s",
         "rentFeeLamports":0,"rentFeePayer":"%s",
         "swapType":"aggregator","router":"metis","transaction":"AQID","lastValidBlockHeight":"279000150",
         "gasless":true,"requestId":"req","totalTime":359,"taker":"%s","quoteId":"q","maker":"%s",
         "expireAt":"1758598698","errorCode":2,"error":"e",
         "unknownTrailing":{"z":[]}}""".formatted(
        USDC, WSOL, AMM_A, USDC, WSOL, SYSTEM, REFERRAL, WSOL, WSOL, PAYER, OTHER, DEST, TAKER, FEE_ACCOUNT);

    final var order = JupiterSwapOrder.parse(JsonIterator.parse(json.getBytes(UTF_8)));

    assertEquals("ultra", order.mode());
    assertEquals(USDC_KEY, order.inputMint());
    assertEquals(WSOL_KEY, order.outputMint());
    assertEquals(100L, order.inAmount());
    assertEquals(461L, order.outAmount());
    assertEquals(new BigDecimal("1.5"), order.inUsdValue());
    assertEquals(new BigDecimal("1.25"), order.outUsdValue());
    assertEquals(new BigDecimal("-0.5"), order.priceImpact());
    assertEquals(new BigDecimal("1.75"), order.swapUsdValue());
    assertEquals(460L, order.otherAmountThreshold());
    assertEquals("ExactIn", order.swapMode());
    assertEquals(new BigDecimal("26"), order.slippageBps());

    assertEquals(1, order.routePlan().size());
    final var step = order.routePlan().getFirst();
    assertEquals("3EKkiwNLWqoUbzFkPrmKbtUB4EweE6f4STzevYUmezeL", step.ammKey());
    assertEquals("SolFi", step.label());
    assertEquals(USDC_KEY, step.inputMint());
    assertEquals(WSOL_KEY, step.outputMint());
    assertEquals(100L, step.inAmount());
    assertEquals(461L, step.outAmount());
    assertEquals(new BigDecimal("100"), step.percent());
    assertEquals(new BigDecimal("10000"), step.bps());
    assertEquals(new BigDecimal("1.5"), step.usdValue());

    assertEquals(REFERRAL_KEY, order.referralAccount());
    assertEquals(WSOL_KEY, order.feeMint());
    assertEquals(new BigDecimal("2"), order.feeBps());
    assertEquals(new JupiterSwapPlatformFee(92L, new BigDecimal("2"), WSOL_KEY), order.platformFee());
    assertEquals(new BigDecimal("5000"), order.signatureFeeLamports());
    assertEquals(PAYER_KEY, order.signatureFeePayer());
    assertEquals(new BigDecimal("696237"), order.prioritizationFeeLamports());
    assertEquals(OTHER_KEY, order.prioritizationFeePayer());
    assertEquals(new BigDecimal("0"), order.rentFeeLamports());
    assertEquals(DEST_KEY, order.rentFeePayer());
    assertEquals("metis", order.router());
    assertArrayEquals(new byte[]{1, 2, 3}, order.transaction());
    assertEquals(279_000_150L, order.lastValidBlockHeight());
    assertTrue(order.gasless());
    assertEquals("req", order.requestId());
    assertEquals(new BigDecimal("359"), order.totalTime());
    assertEquals(TAKER_KEY, order.taker());
    assertEquals("q", order.quoteId());
    assertEquals(FEE_ACCOUNT_KEY, order.maker());
    assertEquals("1758598698", order.expireAt());
    assertEquals(2, order.errorCode());
    assertEquals("e", order.errorMessage());
  }
}

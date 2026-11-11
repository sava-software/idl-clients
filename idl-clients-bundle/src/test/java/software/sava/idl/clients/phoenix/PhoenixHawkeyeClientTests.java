package software.sava.idl.clients.phoenix;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.phoenix.hawkeye.gen.HawkeyeProgram;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewAssetReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewBboReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewFundingReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewLiquidationPriceReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewMarginReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewOrderQuoteReturn;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.BaseLots;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.OrderFlags;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.OrderPacket;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.OrderPacketKind;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.QuoteLots;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.SelfTradeBehavior;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.Side;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.Ticks;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.OptionalLong;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;
import static software.sava.idl.clients.phoenix.HawkeyeViewTests.*;

/// [PhoenixClient]'s Hawkeye methods, held to what mainnet simulated.
///
/// The client supplies the accounts every view shares (Hawkeye, the Eternal program, its global
/// configuration and the header of each account group), creates the scratch account an order
/// quote needs, and reads a view's return data only once it is known to be Hawkeye's, one record
/// long and of the expected kind.
///
/// The instructions are the ones unsigned `simulateTransaction` runs accepted on mainnet on
/// 2026-11-11, against the image deployed at slot 466314629: those of `HawkeyeViewTests`, and four
/// order quotes more, each with the slot it ran at. An order quote takes the order packet Eternal's
/// placement instructions take, so those vectors are built from Eternal's types, and one test holds
/// them equal to what Hawkeye's own generated types write.
final class PhoenixHawkeyeClientTests {

  private static final HexFormat HEX = HexFormat.of();

  /// The fee payer Phoenix's SDKs simulate with, which paid for the scratch accounts below.
  private static final PublicKey FEE_PAYER = PublicKey.fromBase58Encoded("9WzDXwBbmkg8ZTbNMqUxvQRAyrZzDsGYdLVL9zYtAWWM");
  /// The scratch account of the quote at slot 468101473.
  private static final PublicKey QUOTE_SCRATCH = PublicKey.fromBase58Encoded("B7htUKD1goyrhetAx5BfM3pQjvgahWW4bVCWiqsHVwJE");
  /// The scratch accounts of the quotes at slots 468101458, 468101456 and 468101463.
  private static final PublicKey POST_ONLY_SCRATCH = PublicKey.fromBase58Encoded("4uFbNmsgBFhvg9f1Ae81A1CzNULH9dMBic6rsAG3EvJP");
  private static final PublicKey POST_ONLY_SLIDING_SCRATCH = PublicKey.fromBase58Encoded("8p3xUZfmGryN9z9dt28Xmu5nPLUs6K6KK2FVHPGnb4RF");
  private static final PublicKey LIMIT_SCRATCH = PublicKey.fromBase58Encoded("HCADXmUB39ZRxgnYQVYAyvB6cBvhRTvR7J9KWy9hEJv6");

  private static final byte[] CLIENT_ORDER_ID = HEX.parseHex("0102030405060708090a0b0c0d0e0f10");

  private static final String VIEW_ORDER_QUOTE = "c8af9751feabf82c";

  /// A market sell of 100 base lots, every option absent: the quote of `HawkeyeViewTests`, simulated
  /// at slot 468098902.
  private static final OrderPacket MARKET_SELL = new OrderPacket(new OrderPacketKind.ImmediateOrCancel(
      Side.Ask, null, new BaseLots(100), null, new BaseLots(0), new QuoteLots(0), SelfTradeBehavior.Abort,
      OptionalLong.empty(), new byte[OrderPacketKind.ImmediateOrCancel.CLIENT_ORDER_ID_LEN], OptionalLong.empty(),
      new OrderFlags(0), false
  ));
  private static final String MARKET_SELL_BORSH = "020100" + "6400000000000000" + "00"
      + "0000000000000000" + "0000000000000000" + "00" + "00" + "00000000000000000000000000000000" + "00" + "00" + "00";

  /// An immediate-or-cancel bid with every option present: a price, a quote-lot cap, a match limit
  /// and a last valid slot. Simulated at slot 468101473 against a reference price of 8374 ticks.
  private static final OrderPacket BID_WITH_EVERY_OPTION = new OrderPacket(new OrderPacketKind.ImmediateOrCancel(
      Side.Bid, new Ticks(8_384), new BaseLots(100), new QuoteLots(837_400_000), new BaseLots(0), new QuoteLots(0),
      SelfTradeBehavior.Abort, OptionalLong.of(5), new byte[OrderPacketKind.ImmediateOrCancel.CLIENT_ORDER_ID_LEN],
      OptionalLong.of(999_999_999_999L), new OrderFlags(0), false
  ));
  private static final String BID_WITH_EVERY_OPTION_BORSH = "0200" + "01c020000000000000" + "6400000000000000"
      + "01c0b5e93100000000" + "0000000000000000" + "0000000000000000" + "00" + "010500000000000000"
      + "00000000000000000000000000000000" + "01ff0fa5d4e8000000" + "00" + "00";

  /// A post-only bid of 50 lots at 8354 ticks with a last valid slot. Simulated at slot 468101458.
  private static final OrderPacket POST_ONLY = new OrderPacket(new OrderPacketKind.PostOnly(
      Side.Bid, new Ticks(8_354), new BaseLots(50), CLIENT_ORDER_ID, false, OptionalLong.of(1), new OrderFlags(0), false
  ));
  private static final String POST_ONLY_BORSH = "0000" + "a220000000000000" + "3200000000000000"
      + "0102030405060708090a0b0c0d0e0f10" + "00" + "010100000000000000" + "00" + "00";

  /// A post-only bid that slides, with no last valid slot. Simulated at slot 468101456.
  private static final OrderPacket POST_ONLY_SLIDING = new OrderPacket(new OrderPacketKind.PostOnly(
      Side.Bid, new Ticks(8_379), new BaseLots(50), CLIENT_ORDER_ID, true, OptionalLong.empty(), new OrderFlags(0), false
  ));
  private static final String POST_ONLY_SLIDING_BORSH = "0000" + "bb20000000000000" + "3200000000000000"
      + "0102030405060708090a0b0c0d0e0f10" + "01" + "00" + "00" + "00";

  /// A limit bid of 100,000 lots at 8376 ticks with a match limit of one. Simulated at slot
  /// 468101463 against a reference price of 8374 ticks.
  private static final OrderPacket LIMIT = new OrderPacket(new OrderPacketKind.Limit(
      Side.Bid, new Ticks(8_376), new BaseLots(100_000), SelfTradeBehavior.Abort, OptionalLong.of(1), CLIENT_ORDER_ID,
      OptionalLong.empty(), new OrderFlags(0), false
  ));
  private static final String LIMIT_BORSH = "0100" + "b820000000000000" + "a086010000000000" + "00"
      + "010100000000000000" + "0102030405060708090a0b0c0d0e0f10" + "00" + "00" + "00";

  private static final List<PublicKey> MARKET_VIEW_ACCOUNTS = List.of(
      ETERNAL, GLOBAL_CONFIGURATION, GLOBAL_TRADER_INDEX, ACTIVE_TRADER_BUFFER, PERP_ASSET_MAP,
      SOL_ORDERBOOK, SOL_SPLINE_COLLECTION
  );

  private static PhoenixClient client() {
    return PhoenixClient.createClient(PhoenixAccounts.MAIN_NET);
  }

  /// A key that is no account of the vectors.
  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static String magicU64(final byte[] data) {
    return Long.toUnsignedString(ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getLong());
  }

  private static String data(final Instruction ix) {
    return HEX.formatHex(ix.data(), ix.offset(), ix.offset() + ix.len());
  }

  private static List<PublicKey> keys(final Instruction ix) {
    return ix.accounts().stream().map(AccountMeta::publicKey).toList();
  }

  /// Hawkeye is invoked, and every account is read and none signs.
  private static void assertView(final List<PublicKey> expected, final Instruction ix) {
    assertEquals(HAWKEYE, ix.programId().publicKey());
    assertTrue(ix.programId().invoked());
    assertEquals(expected, keys(ix));
    for (final var account : ix.accounts()) {
      assertFalse(account.write(), account.publicKey().toBase58());
      assertFalse(account.signer(), account.publicKey().toBase58());
    }
  }

  /// Hawkeye is invoked over the market's accounts, the trader and the scratch account, the last
  /// the one account written.
  private static void assertQuote(final PublicKey scratch, final String expectedData, final Instruction ix) {
    assertEquals(HAWKEYE, ix.programId().publicKey());
    assertTrue(ix.programId().invoked());
    assertEquals(
        List.of(ETERNAL, GLOBAL_CONFIGURATION, GLOBAL_TRADER_INDEX, ACTIVE_TRADER_BUFFER, PERP_ASSET_MAP,
            SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, TRADER, scratch),
        keys(ix)
    );
    for (final var account : ix.accounts()) {
      assertEquals(account.publicKey().equals(scratch), account.write(), account.publicKey().toBase58());
      assertFalse(account.signer(), account.publicKey().toBase58());
    }
    assertEquals(expectedData, data(ix));
  }

  @Test
  void mainnetsAccountsNameHawkeye() {
    final var hawkeye = PhoenixAccounts.MAIN_NET.invokedHawkeyeProgram();
    assertEquals(HAWKEYE, hawkeye.publicKey());
    assertTrue(hawkeye.invoked());
    assertFalse(hawkeye.write());
    assertFalse(hawkeye.signer());
  }

  /// The factories that predate Hawkeye name the one deployment there is; the ones that take a
  /// Hawkeye program place it, and move nothing else.
  @Test
  void theFactoriesPlaceTheHawkeyeProgramTheyAreGiven() {
    final var mainnet = PhoenixAccounts.MAIN_NET;
    final var ember = mainnet.invokedEmberProgram().publicKey();
    final var eternal = mainnet.invokedEternalProgram().publicKey();
    final var other = key(0x48);

    final var fromKeys = PhoenixAccounts.createAccounts(ember, mainnet.emberUSDCMint(), eternal, mainnet.usdcMint());
    assertEquals(mainnet, fromKeys);
    final var fromStrings = PhoenixAccounts.createAccounts(
        ember.toBase58(), mainnet.emberUSDCMint().toBase58(), eternal.toBase58(), mainnet.usdcMint().toBase58()
    );
    assertEquals(mainnet, fromStrings);

    final var withKey = PhoenixAccounts.createAccounts(ember, mainnet.emberUSDCMint(), eternal, other, mainnet.usdcMint());
    assertEquals(other, withKey.invokedHawkeyeProgram().publicKey());
    assertTrue(withKey.invokedHawkeyeProgram().invoked());
    assertEquals(mainnet.invokedEmberProgram(), withKey.invokedEmberProgram());
    assertEquals(mainnet.emberStateProgram(), withKey.emberStateProgram());
    assertEquals(mainnet.emberVaultProgram(), withKey.emberVaultProgram());
    assertEquals(mainnet.emberUSDCMint(), withKey.emberUSDCMint());
    assertEquals(mainnet.invokedEternalProgram(), withKey.invokedEternalProgram());
    assertEquals(mainnet.eternalGlobalConfig(), withKey.eternalGlobalConfig());
    assertEquals(mainnet.eternalLogAuthority(), withKey.eternalLogAuthority());
    assertEquals(mainnet.globalTraderIndex(), withKey.globalTraderIndex());
    assertEquals(mainnet.activeTraderBuffer(), withKey.activeTraderBuffer());
    assertEquals(mainnet.usdcMint(), withKey.usdcMint());

    final var withString = PhoenixAccounts.createAccounts(
        ember.toBase58(), mainnet.emberUSDCMint().toBase58(), eternal.toBase58(), other.toBase58(), mainnet.usdcMint().toBase58()
    );
    assertEquals(withKey, withString);
  }

  /// A client of other accounts invokes their Hawkeye and passes their Eternal accounts, in every
  /// view: none of the four shared accounts, nor the program, is mainnet's.
  @Test
  void aViewInvokesTheHawkeyeProgramOfTheClientsAccounts() {
    final var otherHawkeye = key(0x48);
    final var otherEternal = key(0x45);
    final var mainnet = PhoenixAccounts.MAIN_NET;
    final var accounts = PhoenixAccounts.createAccounts(
        mainnet.invokedEmberProgram().publicKey(), mainnet.emberUSDCMint(), otherEternal, otherHawkeye, mainnet.usdcMint()
    );
    final var client = PhoenixClient.createClient(accounts);

    final var shared = List.of(
        otherEternal, accounts.eternalGlobalConfig(), accounts.globalTraderIndex(), accounts.activeTraderBuffer()
    );
    for (int i = 0; i < 4; ++i) {
      assertNotEquals(TRADER_VIEW_ACCOUNTS.get(i), shared.get(i), "slot " + i);
    }
    final var traderView = List.of(
        shared.get(0), shared.get(1), shared.get(2), shared.get(3), PERP_ASSET_MAP, TRADER
    );
    final var views = List.of(
        client.viewMargin(TRADER, PERP_ASSET_MAP),
        client.viewMarginForAsset(TRADER, PERP_ASSET_MAP, 2),
        client.viewLiquidationPrice(TRADER, PERP_ASSET_MAP, 2),
        client.viewFunding(TRADER, PERP_ASSET_MAP, 2)
    );
    for (final var ix : views) {
      assertEquals(otherHawkeye, ix.programId().publicKey(), data(ix));
      assertTrue(ix.programId().invoked(), data(ix));
      assertEquals(traderView, keys(ix), data(ix));
    }

    final var bbo = client.viewBbo(PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION);
    assertEquals(otherHawkeye, bbo.programId().publicKey());
    assertEquals(
        List.of(shared.get(0), shared.get(1), shared.get(2), shared.get(3), PERP_ASSET_MAP,
            SOL_ORDERBOOK, SOL_SPLINE_COLLECTION),
        keys(bbo)
    );

    final var quote = client.viewOrderQuote(
        TRADER, PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, SCRATCH, 0, MARKET_SELL
    );
    assertEquals(otherHawkeye, quote.programId().publicKey());
    assertTrue(quote.programId().invoked());
    assertEquals(
        List.of(shared.get(0), shared.get(1), shared.get(2), shared.get(3), PERP_ASSET_MAP,
            SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, TRADER, SCRATCH),
        keys(quote)
    );

    // the scratch account is owned by that Hawkeye, and only its return data is read
    final var scratch = client.createOrderQuoteScratchAccount(FEE_PAYER, QUOTE_SCRATCH, 8);
    assertEquals(HEX.formatHex(otherHawkeye.toByteArray()), data(scratch).substring(40));
    assertEquals(
        "viewBbo expects return data from " + otherHawkeye.toBase58() + ", not " + HAWKEYE.toBase58() + ".",
        assertThrows(IllegalStateException.class, () -> client.readViewBbo(HAWKEYE, HEX.parseHex(BBO_RETURN))).getMessage()
    );
    assertEquals(8_385L, client.readViewBbo(otherHawkeye, HEX.parseHex(BBO_RETURN)).bestAskTicks());
  }

  @Test
  void theMarginViewIsTheOneMainnetAccepted() {
    final var ix = client().viewMargin(TRADER, PERP_ASSET_MAP);
    assertEquals("b20a7cadecd27506", data(ix));
    assertView(TRADER_VIEW_ACCOUNTS, ix);
  }

  /// The asset id is all a caller gives; the client writes the four bytes of padding after it.
  @Test
  void theAssetViewsAreTheOnesMainnetAccepted() {
    final var client = client();

    final var asset = client.viewMarginForAsset(TRADER, PERP_ASSET_MAP, 0);
    assertEquals("2012affea1ed4d9b0000000000000000", data(asset));
    assertView(TRADER_VIEW_ACCOUNTS, asset);

    final var liquidation = client.viewLiquidationPrice(TRADER, PERP_ASSET_MAP, 0);
    assertEquals("f9c9bc602c6844140000000000000000", data(liquidation));
    assertView(TRADER_VIEW_ACCOUNTS, liquidation);

    final var funding = client.viewFunding(TRADER, PERP_ASSET_MAP, 0);
    assertEquals("c2138e49fa58a7bf0000000000000000", data(funding));
    assertView(TRADER_VIEW_ACCOUNTS, funding);

    // ETH, asset 2: the id reaches each view's data
    assertEquals("2012affea1ed4d9b0200000000000000", data(client.viewMarginForAsset(TRADER, PERP_ASSET_MAP, 2)));
    assertEquals("f9c9bc602c6844140200000000000000", data(client.viewLiquidationPrice(TRADER, PERP_ASSET_MAP, 2)));
    assertEquals("c2138e49fa58a7bf0200000000000000", data(client.viewFunding(TRADER, PERP_ASSET_MAP, 2)));
  }

  @Test
  void theBboViewIsTheOneMainnetAccepted() {
    final var ix = client().viewBbo(PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION);
    assertEquals("375f232d53af1252", data(ix));
    assertView(MARKET_VIEW_ACCOUNTS, ix);
  }

  /// The quote of `HawkeyeViewTests`, from the packet a market sell would be placed with.
  @Test
  void anOrderQuoteIsTheOneMainnetAccepted() {
    final var ix = client().viewOrderQuote(
        TRADER, PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, SCRATCH, 0, MARKET_SELL
    );
    assertQuote(SCRATCH, VIEW_ORDER_QUOTE + "0000000000000000" + MARKET_SELL_BORSH, ix);
  }

  /// The two instructions that follow the compute unit limit in the transaction simulated at slot
  /// 468101473: the scratch account, four mebibytes with no lamports, owned by Hawkeye and signing
  /// its own creation beside the payer, then the quote, with the reference price ahead of the packet.
  @Test
  void theScratchAccountAndItsQuoteAreTheTransactionMainnetAccepted() {
    final var client = client();

    final var scratch = client.createOrderQuoteScratchAccount(FEE_PAYER, QUOTE_SCRATCH, 4_194_304);
    assertEquals(SolanaAccounts.MAIN_NET.systemProgram(), scratch.programId().publicKey());
    assertTrue(scratch.programId().invoked());
    assertEquals(
        "00000000" + "0000000000000000" + "0000400000000000"
            + "0654cc5994bedf9c513e939ae5d52444af85002e011e9cf04c15c50b481cb214",
        data(scratch)
    );
    assertEquals(HEX.formatHex(HAWKEYE.toByteArray()), data(scratch).substring(40));
    assertEquals(List.of(FEE_PAYER, QUOTE_SCRATCH), keys(scratch));
    for (final var account : scratch.accounts()) {
      assertTrue(account.write(), account.publicKey().toBase58());
      assertTrue(account.signer(), account.publicKey().toBase58());
    }

    final var quote = client.viewOrderQuote(
        TRADER, PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, QUOTE_SCRATCH, 8_374, BID_WITH_EVERY_OPTION
    );
    assertQuote(QUOTE_SCRATCH, VIEW_ORDER_QUOTE + "b620000000000000" + BID_WITH_EVERY_OPTION_BORSH, quote);
  }

  /// The space is the caller's to choose, and reaches the instruction as given.
  @Test
  void theScratchAccountIsAsLargeAsAsked() {
    final var scratch = client().createOrderQuoteScratchAccount(FEE_PAYER, QUOTE_SCRATCH, 3_059_776);
    assertEquals("00000000" + "0000000000000000" + "40b02e0000000000", data(scratch).substring(0, 40));
  }

  /// Post-only and limit packets: the quote instructions mainnet accepted at slots 468101458,
  /// 468101456 and 468101463, each over its own scratch account.
  @Test
  void everyKindOfPacketIsQuotedAsMainnetAcceptedIt() {
    final var client = client();
    assertQuote(POST_ONLY_SCRATCH, VIEW_ORDER_QUOTE + "0000000000000000" + POST_ONLY_BORSH, client.viewOrderQuote(
        TRADER, PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, POST_ONLY_SCRATCH, 0, POST_ONLY
    ));
    assertQuote(POST_ONLY_SLIDING_SCRATCH, VIEW_ORDER_QUOTE + "0000000000000000" + POST_ONLY_SLIDING_BORSH, client.viewOrderQuote(
        TRADER, PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, POST_ONLY_SLIDING_SCRATCH, 0, POST_ONLY_SLIDING
    ));
    assertQuote(LIMIT_SCRATCH, VIEW_ORDER_QUOTE + "b620000000000000" + LIMIT_BORSH, client.viewOrderQuote(
        TRADER, PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, LIMIT_SCRATCH, 8_374, LIMIT
    ));
  }

  /// A reference price using all eight bytes sits between the discriminator and the packet.
  @Test
  void theReferencePriceIsAU64AheadOfThePacket() {
    final var ix = client().viewOrderQuote(
        TRADER, PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, SCRATCH, 0x8877665544332211L, POST_ONLY
    );
    assertEquals(VIEW_ORDER_QUOTE + "1122334455667788" + POST_ONLY_BORSH, data(ix));
  }

  /// What the quote is for: the packet is the bytes Eternal's placement instructions carry after
  /// their own discriminators, so the order quoted is the order placed.
  @Test
  void theQuoteCarriesThePacketThePlacementInstructionsCarry() {
    final var client = client();
    final var wallet = FEE_PAYER;
    for (final var packet : List.of(MARKET_SELL, BID_WITH_EVERY_OPTION, POST_ONLY, POST_ONLY_SLIDING, LIMIT)) {
      final var quote = data(client.viewOrderQuote(
          TRADER, PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, SCRATCH, 0, packet
      ));
      final var limit = data(client.placeLimitOrder(
          wallet, TRADER, PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, packet
      ));
      final var market = data(client.placeMarketOrder(
          wallet, TRADER, PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, packet
      ));
      assertEquals(limit.substring(16), quote.substring(32));
      assertEquals(market.substring(16), quote.substring(32));
    }
  }

  /// Hawkeye's IDL declares its own copy of the order packet. Read from the bytes Eternal's type
  /// writes, it is the same packet, and the generated Hawkeye builder writes the instruction the
  /// client does.
  @Test
  void hawkeyesOwnPacketTypeWritesTheSameInstruction() {
    final var client = client();
    final long referencePrice = 8_374;
    for (final var packet : List.of(MARKET_SELL, BID_WITH_EVERY_OPTION, POST_ONLY, POST_ONLY_SLIDING, LIMIT)) {
      final byte[] borsh = new byte[packet.l()];
      assertEquals(borsh.length, packet.write(borsh, 0));
      final var hawkeyePacket = software.sava.idl.clients.phoenix.hawkeye.gen.types.OrderPacketKind.read(borsh, 0);
      assertEquals(borsh.length, hawkeyePacket.l());

      final var generated = HawkeyeProgram.viewOrderQuote(
          PhoenixAccounts.MAIN_NET.invokedHawkeyeProgram(), ETERNAL, GLOBAL_CONFIGURATION, GLOBAL_TRADER_INDEX,
          ACTIVE_TRADER_BUFFER, PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, TRADER, SCRATCH,
          referencePrice, hawkeyePacket
      );
      final var ix = client.viewOrderQuote(
          TRADER, PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, SCRATCH, referencePrice, packet
      );
      assertEquals(data(generated), data(ix));
      assertEquals(generated.accounts(), ix.accounts());
      assertEquals(generated.programId(), ix.programId());
    }
  }

  private static byte[] written(final SerDe record) {
    final byte[] data = new byte[record.l()];
    assertEquals(data.length, record.write(data, 0));
    return data;
  }

  /// Each reader takes what mainnet returned, and what it reads is the whole of it.
  @Test
  void eachReaderReadsWhatMainnetReturned() {
    final var client = client();

    final var margin = client.readViewMargin(HAWKEYE, HEX.parseHex(MARGIN_RETURN));
    assertEquals(MARGIN_RETURN, HEX.formatHex(written(margin)));
    assertEquals(69, margin.positionCount());
    assertEquals(363_821_343_014L, margin.collateralQuoteLots());

    final var asset = client.readViewMarginForAsset(HAWKEYE, HEX.parseHex(ASSET_RETURN));
    assertEquals(ASSET_RETURN, HEX.formatHex(written(asset)));
    assertEquals(-6_426L, asset.baseLots());

    final var liquidation = client.readViewLiquidationPrice(HAWKEYE, HEX.parseHex(LIQUIDATION_PRICE_RETURN));
    assertEquals(LIQUIDATION_PRICE_RETURN, HEX.formatHex(written(liquidation)));
    assertEquals(238_427L, liquidation.liquidationPriceTicks());

    final var funding = client.readViewFunding(HAWKEYE, HEX.parseHex(FUNDING_RETURN));
    assertEquals(FUNDING_RETURN, HEX.formatHex(written(funding)));
    assertEquals(505_651_544L, funding.totalAccumulatedFundingQuoteLots());

    final var bbo = client.readViewBbo(HAWKEYE, HEX.parseHex(BBO_RETURN));
    assertEquals(BBO_RETURN, HEX.formatHex(written(bbo)));
    assertEquals(8_384L, bbo.bestBidTicks());

    final var quote = client.readViewOrderQuote(HAWKEYE, HEX.parseHex(ORDER_QUOTE_RETURN));
    assertEquals(ORDER_QUOTE_RETURN, HEX.formatHex(written(quote)));
    assertEquals(83_800_000L, quote.filledQuoteLots());
  }

  /// A reader of the client, the name it reports a refusal under, and what mainnet returned for it.
  private record Reader(String view,
                        String magicName,
                        int bytes,
                        String returned,
                        BiFunction<PublicKey, byte[], ? extends SerDe> read) {

    byte[] data() {
      return HEX.parseHex(returned);
    }

    String refusal(final PublicKey returnProgramKey, final byte[] returnData) {
      final var thrown = assertThrows(IllegalStateException.class, () -> read.apply(returnProgramKey, returnData), view);
      return thrown.getMessage();
    }
  }

  private static List<Reader> readers(final PhoenixClient client) {
    return List.of(
        new Reader("viewMargin", "margin", ViewMarginReturn.BYTES, MARGIN_RETURN, client::readViewMargin),
        new Reader("viewMarginForAsset", "asset", ViewAssetReturn.BYTES, ASSET_RETURN, client::readViewMarginForAsset),
        new Reader("viewLiquidationPrice", "liquidation_price", ViewLiquidationPriceReturn.BYTES, LIQUIDATION_PRICE_RETURN, client::readViewLiquidationPrice),
        new Reader("viewFunding", "funding", ViewFundingReturn.BYTES, FUNDING_RETURN, client::readViewFunding),
        new Reader("viewBbo", "bbo", ViewBboReturn.BYTES, BBO_RETURN, client::readViewBbo),
        new Reader("viewOrderQuote", "order_quote", ViewOrderQuoteReturn.BYTES, ORDER_QUOTE_RETURN, client::readViewOrderQuote)
    );
  }

  /// A simulation that failed before the view ran has no return data, and neither does one whose
  /// last instruction set none.
  @Test
  void noReturnDataIsRefused() {
    for (final var reader : readers(client())) {
      assertEquals(reader.view + " returned no data.", reader.refusal(null, null));
      assertEquals(reader.view + " returned no data.", reader.refusal(null, reader.data()));
      assertEquals(reader.view + " returned no data.", reader.refusal(HAWKEYE, null));
    }
  }

  /// A simulation names the program whose return data it reports, and a well-formed record that
  /// another program returned is not Hawkeye's answer.
  @Test
  void anotherProgramsReturnDataIsRefused() {
    for (final var reader : readers(client())) {
      assertEquals(
          reader.view + " expects return data from " + HAWKEYE.toBase58() + ", not " + ETERNAL.toBase58() + ".",
          reader.refusal(ETERNAL, reader.data())
      );
    }
  }

  /// One byte short and one byte long are both refused, where the generated `read` throws on the
  /// first and reads past the second.
  @Test
  void aRecordOfAnotherLengthIsRefused() {
    for (final var reader : readers(client())) {
      final byte[] data = reader.data();
      assertEquals(reader.bytes, data.length);
      assertEquals(
          reader.view + " returns " + reader.bytes + " bytes, not " + (reader.bytes - 1) + ".",
          reader.refusal(HAWKEYE, Arrays.copyOf(data, data.length - 1))
      );
      assertEquals(
          reader.view + " returns " + reader.bytes + " bytes, not " + (reader.bytes + 1) + ".",
          reader.refusal(HAWKEYE, Arrays.copyOf(data, data.length + 1))
      );
      assertEquals(reader.view + " returns " + reader.bytes + " bytes, not 0.", reader.refusal(HAWKEYE, new byte[0]));
    }
  }

  /// Each reader expects the magic its view's name hashes to, computed here and not copied: a
  /// record with any one of its eight bytes changed is refused.
  @Test
  void aRecordOpeningWithAnotherMagicIsRefused() {
    for (final var reader : readers(client())) {
      final byte[] expected = magic(reader.magicName);
      assertArrayEquals(expected, Arrays.copyOf(reader.data(), 8), reader.view);
      final var expectedMagic = magicU64(expected);
      for (int i = 0; i < 8; ++i) {
        final byte[] data = reader.data();
        data[i] ^= 1;
        assertEquals(
            reader.view + " return data opens with magic " + expectedMagic + ", not " + magicU64(data) + ".",
            reader.refusal(HAWKEYE, data)
        );
      }
    }
  }

  /// A record of the right length for one view and the magic of another is refused: the funding
  /// view's 48 bytes opening with the BBO view's magic.
  @Test
  void anotherViewsMagicIsRefused() {
    final byte[] data = HEX.parseHex(FUNDING_RETURN);
    System.arraycopy(magic("bbo"), 0, data, 0, 8);
    final var client = client();
    final var thrown = assertThrows(IllegalStateException.class, () -> client.readViewFunding(HAWKEYE, data));
    assertEquals(
        "viewFunding return data opens with magic 3650558849534319640, not 17278657705831514481.",
        thrown.getMessage()
    );
  }
}

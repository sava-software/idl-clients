package software.sava.idl.clients.phoenix;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.phoenix.hawkeye.gen.HawkeyeProgram;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.BaseLots;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.OrderFlags;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.OrderPacketKind;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.QuoteLots;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.SelfTradeBehavior;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.Side;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewAssetParams;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewAssetReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewBboReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewFundingReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewLiquidationPriceReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewMarginReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewOrderQuoteReturn;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.OptionalLong;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.*;

/// Phoenix Hawkeye's view instructions and their return data, held to mainnet simulations.
///
/// Hawkeye (`RiSeVw3ZjNfsaXPRb4mgaqYaEEt41pNNJoDvVh7pgQj`) reads Phoenix Eternal's accounts and
/// answers through return data, so it is simulated, not sent. A result is raw `repr(C)` bytes
/// opening with an eight-byte magic, `sha256("return:phoenix_hawkeye_<view>")[0..8]`, which the
/// caller checks along with the length and the returning program before reading: the generated
/// `read` checks none of them.
///
/// The vectors are unsigned `simulateTransaction` runs on mainnet on 2026-11-11, against the image
/// deployed at slot 466314629, for trader `HMvgP5p9…` (69 positions, short SOL) and the SOL market
/// (asset 0): the instructions below are the ones the program accepted, and the return data is
/// what it answered at slots 468097932 to 468098902. Each of the Eternal account groups
/// `globalTraderIndex` and `activeTraderBuffer` was one account then, its header, which is all the
/// typed builders can pass; with more, a caller flattens every member into the list overload.
///
/// The last vector is Phoenix's own: the order-quote parity vector its Rust and TypeScript SDKs
/// share (`Ellipsis-Labs/rise-public` at `673a7df`, `rust/ix/src/hawkeye.rs`,
/// `decodes_order_quote_parity_vector`).
final class HawkeyeViewTests {

  private static final HexFormat HEX = HexFormat.of();

  private static final PublicKey HAWKEYE = PublicKey.fromBase58Encoded("RiSeVw3ZjNfsaXPRb4mgaqYaEEt41pNNJoDvVh7pgQj");
  private static final AccountMeta INVOKED_HAWKEYE = AccountMeta.createInvoked(HAWKEYE);
  private static final PublicKey ETERNAL = PhoenixAccounts.MAIN_NET.invokedEternalProgram().publicKey();
  private static final PublicKey GLOBAL_CONFIGURATION = PublicKey.fromBase58Encoded("2zskx2iyCvb6Stg7RBZkt1f6MrF4dpYtMG3yMvKwqtUZ");
  private static final PublicKey GLOBAL_TRADER_INDEX = PublicKey.fromBase58Encoded("HCrPXLByGqRh2szQi3gj7oRdRVBNi1gccAyn4CQCT3HK");
  private static final PublicKey ACTIVE_TRADER_BUFFER = PublicKey.fromBase58Encoded("2U32rSzzrQS3eVmGHsnbw5kcqKF3wQXpHGd3hMq5YJok");
  private static final PublicKey PERP_ASSET_MAP = PublicKey.fromBase58Encoded("2nHGAaEw3D5dd4hVueaUNoygkQFmoeKqRQWnSPqSMFUC");
  private static final PublicKey TRADER = PublicKey.fromBase58Encoded("HMvgP5p9oPz8PiKGXiEdYzZDnK7SwqDmfBRib6UUuQUk");
  private static final PublicKey SOL_ORDERBOOK = PublicKey.fromBase58Encoded("71Si24E4uc3oCaPbPZTozC1ptSNNqygjjebxSmErSsC2");
  private static final PublicKey SOL_SPLINE_COLLECTION = PublicKey.fromBase58Encoded("EVhkquLbfm5rDRXtZu9FoyDSXX5mYq2EYU6yD8zfKEqM");
  private static final PublicKey SCRATCH = PublicKey.fromBase58Encoded("APvQa8nybkJiAuuJXjSjiMrHamjKZErXJmCmpDyUPL8U");

  private static final ViewAssetParams SOL = new ViewAssetParams(0, new byte[4]);

  private static String data(final Instruction ix) {
    return HEX.formatHex(Arrays.copyOfRange(ix.data(), ix.offset(), ix.offset() + ix.len()));
  }

  private static void assertReadOnly(final List<PublicKey> expected, final Instruction ix) {
    assertEquals(expected, ix.accounts().stream().map(AccountMeta::publicKey).toList());
    for (final var account : ix.accounts()) {
      assertFalse(account.write(), account.publicKey().toBase58());
      assertFalse(account.signer(), account.publicKey().toBase58());
    }
    assertEquals(HAWKEYE, ix.programId().publicKey());
  }

  private static final List<PublicKey> TRADER_VIEW_ACCOUNTS = List.of(
      ETERNAL, GLOBAL_CONFIGURATION, GLOBAL_TRADER_INDEX, ACTIVE_TRADER_BUFFER, PERP_ASSET_MAP, TRADER
  );

  /// The magic a view's return data opens with, from the name its IDL publishes.
  private static byte[] magic(final String view) {
    try {
      final var digest = MessageDigest.getInstance("SHA-256").digest(("return:phoenix_hawkeye_" + view).getBytes(US_ASCII));
      return Arrays.copyOf(digest, 8);
    } catch (final NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
  }

  /// What a caller does before reading: the whole of the return data is one record of the
  /// expected kind.
  private static byte[] returned(final String hex, final String view, final int bytes) {
    final byte[] data = HEX.parseHex(hex);
    assertEquals(bytes, data.length);
    assertArrayEquals(magic(view), Arrays.copyOf(data, 8));
    return data;
  }

  /// The Eternal accounts every view opens with are the ones [PhoenixAccounts] derives: the global
  /// configuration and the header of each account group.
  @Test
  void theSharedAccountsAreTheOnesPhoenixAccountsDerives() {
    final var accounts = PhoenixAccounts.MAIN_NET;
    assertEquals(GLOBAL_CONFIGURATION, accounts.eternalGlobalConfig());
    assertEquals(GLOBAL_TRADER_INDEX, accounts.globalTraderIndex());
    assertEquals(ACTIVE_TRADER_BUFFER, accounts.activeTraderBuffer());
  }

  @Test
  void theMarginViewIsItsDiscriminatorOverTheSixAccounts() {
    final var ix = HawkeyeProgram.viewMargin(
        INVOKED_HAWKEYE, ETERNAL, GLOBAL_CONFIGURATION, GLOBAL_TRADER_INDEX, ACTIVE_TRADER_BUFFER, PERP_ASSET_MAP, TRADER
    );
    assertEquals("b20a7cadecd27506", data(ix));
    assertReadOnly(TRADER_VIEW_ACCOUNTS, ix);
  }

  /// The asset id is a u32 followed by four bytes of padding, eight argument bytes in all.
  @Test
  void theAssetViewsCarryTheAssetIdAndItsPadding() {
    final var asset = HawkeyeProgram.viewMarginForAsset(
        INVOKED_HAWKEYE, ETERNAL, GLOBAL_CONFIGURATION, GLOBAL_TRADER_INDEX, ACTIVE_TRADER_BUFFER, PERP_ASSET_MAP, TRADER, SOL
    );
    assertEquals("2012affea1ed4d9b0000000000000000", data(asset));
    assertReadOnly(TRADER_VIEW_ACCOUNTS, asset);

    final var liquidation = HawkeyeProgram.viewLiquidationPrice(
        INVOKED_HAWKEYE, ETERNAL, GLOBAL_CONFIGURATION, GLOBAL_TRADER_INDEX, ACTIVE_TRADER_BUFFER, PERP_ASSET_MAP, TRADER, SOL
    );
    assertEquals("f9c9bc602c6844140000000000000000", data(liquidation));
    assertReadOnly(TRADER_VIEW_ACCOUNTS, liquidation);

    final var funding = HawkeyeProgram.viewFunding(
        INVOKED_HAWKEYE, ETERNAL, GLOBAL_CONFIGURATION, GLOBAL_TRADER_INDEX, ACTIVE_TRADER_BUFFER, PERP_ASSET_MAP, TRADER, SOL
    );
    assertEquals("c2138e49fa58a7bf0000000000000000", data(funding));
    assertReadOnly(TRADER_VIEW_ACCOUNTS, funding);

    final var eth = HawkeyeProgram.viewFunding(
        INVOKED_HAWKEYE, ETERNAL, GLOBAL_CONFIGURATION, GLOBAL_TRADER_INDEX, ACTIVE_TRADER_BUFFER, PERP_ASSET_MAP, TRADER,
        new ViewAssetParams(2, new byte[4])
    );
    assertEquals("c2138e49fa58a7bf0200000000000000", data(eth));
  }

  /// The market's orderbook and spline collection follow the perp asset map, and no trader.
  @Test
  void theBboViewTakesTheMarketInPlaceOfATrader() {
    final var ix = HawkeyeProgram.viewBbo(
        INVOKED_HAWKEYE, ETERNAL, GLOBAL_CONFIGURATION, GLOBAL_TRADER_INDEX, ACTIVE_TRADER_BUFFER, PERP_ASSET_MAP,
        SOL_ORDERBOOK, SOL_SPLINE_COLLECTION
    );
    assertEquals("375f232d53af1252", data(ix));
    assertReadOnly(
        List.of(ETERNAL, GLOBAL_CONFIGURATION, GLOBAL_TRADER_INDEX, ACTIVE_TRADER_BUFFER, PERP_ASSET_MAP, SOL_ORDERBOOK, SOL_SPLINE_COLLECTION),
        ix
    );
  }

  /// A market sell of 100 base lots: the reference price (zero, so no slippage is measured), then
  /// the Borsh order packet, 49 bytes for an immediate-or-cancel with every option absent. The
  /// scratch account is the one writable account and comes last.
  @Test
  void anOrderQuoteIsTheReferencePriceThenTheOrderPacket() {
    final var packet = new OrderPacketKind.ImmediateOrCancel(
        Side.Ask,
        null,
        new BaseLots(100),
        null,
        new BaseLots(0),
        new QuoteLots(0),
        SelfTradeBehavior.Abort,
        OptionalLong.empty(),
        new byte[16],
        OptionalLong.empty(),
        new OrderFlags(0),
        false
    );
    final var ix = HawkeyeProgram.viewOrderQuote(
        INVOKED_HAWKEYE, ETERNAL, GLOBAL_CONFIGURATION, GLOBAL_TRADER_INDEX, ACTIVE_TRADER_BUFFER, PERP_ASSET_MAP,
        SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, TRADER, SCRATCH, 0, packet
    );
    assertEquals(
        "c8af9751feabf82c" + "0000000000000000" + "020100" + "6400000000000000" + "00"
            + "0000000000000000" + "0000000000000000" + "00" + "00" + "00000000000000000000000000000000" + "00" + "00" + "00",
        data(ix)
    );
    final var accounts = ix.accounts();
    assertEquals(
        List.of(ETERNAL, GLOBAL_CONFIGURATION, GLOBAL_TRADER_INDEX, ACTIVE_TRADER_BUFFER, PERP_ASSET_MAP,
            SOL_ORDERBOOK, SOL_SPLINE_COLLECTION, TRADER, SCRATCH),
        accounts.stream().map(AccountMeta::publicKey).toList()
    );
    for (final var account : accounts) {
      assertEquals(account.publicKey().equals(SCRATCH), account.write(), account.publicKey().toBase58());
      assertFalse(account.signer(), account.publicKey().toBase58());
    }
  }

  @Test
  void aMarginReturnReadsAsMainnetAnsweredIt() {
    final byte[] data = returned(
        "3f25ff3d9d5b5f950100450000000000262171b5540000001bbade7256000000e138fbaa17000000202eb78205000000"
            + "3a81e3c73e0000009bc0f1631f000000c93a4e0e2f0000006cb3608e0c000000b459304706000000f5986dbd01000000"
            + "f5986dbd010000000000000000000000",
        "margin", ViewMarginReturn.BYTES
    );
    final var margin = ViewMarginReturn.read(data, 0);
    assertEquals(1, margin.version());
    assertEquals(69, margin.positionCount());
    assertEquals(0, margin.riskState());
    assertEquals(0, margin.isLiquidatable());
    assertEquals(363_821_343_014L, margin.collateralQuoteLots());
    assertEquals(371_294_386_715L, margin.effectiveCollateralQuoteLots());
    assertEquals(101_652_838_625L, margin.freeCollateralQuoteLots());
    assertEquals(23_667_879_456L, margin.withdrawableCollateralQuoteLots());
    assertEquals(269_641_548_090L, margin.initialMarginQuoteLots());
    assertEquals(134_820_774_043L, margin.maintenanceMarginQuoteLots());
    assertEquals(202_103_470_793L, margin.cancelMarginQuoteLots());
    assertEquals(53_928_309_612L, margin.backstopMarginQuoteLots());
    assertEquals(26_964_154_804L, margin.highRiskMarginQuoteLots());
    assertEquals(7_473_043_701L, margin.unrealizedPnlQuoteLots());
    assertEquals(7_473_043_701L, margin.discountedUnrealizedPnlQuoteLots());
    assertEquals(0, margin.unsettledFundingQuoteLots());
  }

  /// A short position: the base lots and the position's value are negative.
  @Test
  void anAssetReturnReadsAsMainnetAnsweredIt() {
    final byte[] data = returned(
        "b91d5e28f8ff58db00000000010001000000000000000000e6e6ffffffffffffec38594b01000000c120000000000000"
            + "47330d0000000000d893d6befeffffffc4cc2f0a00000000c4cc2f0a0000000000000000000000004004281b00000000"
            + "2002940d0000000030035e140000000040676e0500000000a033b70200000000",
        "asset", ViewAssetReturn.BYTES
    );
    final var asset = ViewAssetReturn.read(data, 0);
    assertEquals(0, asset.assetId());
    assertEquals(1, asset.version());
    assertEquals(1, asset.hasPositionOrOrders());
    assertEquals(-6_426L, asset.baseLots());
    assertEquals(5_559_105_772L, asset.virtualQuoteLots());
    assertEquals(8_385L, asset.markPriceTicks());
    assertEquals(865_095L, asset.entryPriceQuoteLotsPerBaseLot());
    assertEquals(-5_388_201_000L, asset.positionValueQuoteLots());
    assertEquals(170_904_772L, asset.unrealizedPnlQuoteLots());
    assertEquals(170_904_772L, asset.discountedUnrealizedPnlQuoteLots());
    assertEquals(0, asset.unsettledFundingQuoteLots());
    assertEquals(455_607_360L, asset.initialMarginQuoteLots());
    assertEquals(227_803_680L, asset.maintenanceMarginQuoteLots());
    assertEquals(341_705_520L, asset.cancelMarginQuoteLots());
    assertEquals(91_121_472L, asset.backstopMarginQuoteLots());
    assertEquals(45_560_736L, asset.highRiskMarginQuoteLots());
  }

  @Test
  void aLiquidationPriceReturnReadsAsMainnetAnsweredIt() {
    final byte[] data = returned(
        "4168ef60b1343dd200000000010001025ba3030000000000c12000000000000047330d0000000000571ce76c56000000"
            + "4362ec621f000000",
        "liquidation_price", ViewLiquidationPriceReturn.BYTES
    );
    final var liquidation = ViewLiquidationPriceReturn.read(data, 0);
    assertEquals(0, liquidation.assetId());
    assertEquals(1, liquidation.version());
    assertEquals(1, liquidation.status());
    assertEquals(2, liquidation.side());
    assertEquals(238_427L, liquidation.liquidationPriceTicks());
    assertEquals(8_385L, liquidation.markPriceTicks());
    assertEquals(865_095L, liquidation.entryPriceQuoteLotsPerBaseLot());
    assertEquals(371_194_272_855L, liquidation.effectiveCollateralQuoteLots());
    assertEquals(134_803_644_995L, liquidation.maintenanceMarginQuoteLots());
  }

  /// Flags 3: both totals are present, bit 0 the accumulated and bit 1 the unsettled.
  @Test
  void aFundingReturnReadsAsMainnetAnsweredIt() {
    final byte[] data = returned(
        "18dc26a2ee63a932000000000100030058a1231e000000000000000000000000dcd1010000000000b9a3030000000000",
        "funding", ViewFundingReturn.BYTES
    );
    final var funding = ViewFundingReturn.read(data, 0);
    assertEquals(0, funding.assetId());
    assertEquals(1, funding.version());
    assertEquals(3, funding.flags());
    assertEquals(505_651_544L, funding.totalAccumulatedFundingQuoteLots());
    assertEquals(0, funding.totalUnsettledFundingQuoteLots());
    assertEquals(119_260L, funding.currentFundingRateMicroBps());
    assertEquals(238_521L, funding.projected1hFundingRateMicroBps());
  }

  /// Flags 3: a bid and an ask are both present, bit 0 the bid and bit 1 the ask.
  @Test
  void aBboReturnReadsAsMainnetAnsweredIt() {
    final byte[] data = returned(
        "7141a71fa31fcaef0100030000000000c020000000000000c120000000000000c120000000000000c220000000000000"
            + "919be61b00000000919be61b00000000",
        "bbo", ViewBboReturn.BYTES
    );
    final var bbo = ViewBboReturn.read(data, 0);
    assertEquals(1, bbo.version());
    assertEquals(3, bbo.flags());
    assertEquals(8_384L, bbo.bestBidTicks());
    assertEquals(8_385L, bbo.bestAskTicks());
    assertEquals(8_385L, bbo.markPriceTicks());
    assertEquals(8_386L, bbo.indexPriceTicks());
    assertEquals(468_097_937L, bbo.markPriceLastUpdatedSlot());
    assertEquals(468_097_937L, bbo.indexPriceLastUpdatedSlot());
  }

  /// The answer to the order quote above: the 100 lots filled at 8380 ticks, with the fee beside
  /// the notional, not inside it. Flags 5: an average price and an effective size, no slippage,
  /// since no reference price was given.
  @Test
  void anOrderQuoteReturnReadsAsMainnetAnsweredIt() {
    final byte[] data = returned(
        "886fb65c1ccf29150100050100000000569fe61b0000000064000000000000006400000000000000c0affe0400000000"
            + "92720000000000000000000000000000000000000000000070c90c000000000000000000000000000000000000000000"
            + "640000000000000000000000000000000000000000000000",
        "order_quote", ViewOrderQuoteReturn.BYTES
    );
    final var quote = ViewOrderQuoteReturn.read(data, 0);
    assertEquals(1, quote.version());
    assertEquals(5, quote.flags());
    assertEquals(1, quote.side());
    assertEquals(0, quote.assetId());
    assertEquals(468_098_902L, quote.slot());
    assertEquals(100, quote.requestedBaseLots());
    assertEquals(100, quote.filledBaseLots());
    assertEquals(83_800_000L, quote.filledQuoteLots());
    assertEquals(29_330L, quote.feeQuoteLots());
    assertEquals(0, quote.postedBaseLots());
    assertEquals(0, quote.unfilledBaseLots());
    assertEquals(838_000L, quote.averagePriceQuoteLotsPerBaseLot());
    assertEquals(0, quote.referencePriceTicks());
    assertEquals(0, quote.slippageBps());
    assertEquals(100, quote.effectiveBaseLots());
    assertEquals(0, quote.postedPriceTicks());
    assertEquals(0, quote.outcome());
    assertEquals(0, quote.rejectionReason());
  }

  /// Phoenix's SDK vector: a partly filled bid with a posted remainder and a negative slippage.
  @Test
  void theSdksOrderQuoteParityVectorReadsAsTheSdkReadsIt() {
    final byte[] data = returned(
        "886fb65c1ccf29150100070007000000" + "7b00000000000000" + "1400000000000000" + "0a00000000000000"
            + "e803000000000000" + "0200000000000000" + "0300000000000000" + "0a00000000000000"
            + "6400000000000000" + "6500000000000000" + "9dffffffffffffff" + "1400000000000000"
            + "6300000000000000" + "0000000000000000",
        "order_quote", ViewOrderQuoteReturn.BYTES
    );
    final var quote = ViewOrderQuoteReturn.read(data, 0);
    assertEquals(1, quote.version());
    assertEquals(7, quote.flags());
    assertEquals(0, quote.side());
    assertEquals(7, quote.assetId());
    assertEquals(123, quote.slot());
    assertEquals(20, quote.requestedBaseLots());
    assertEquals(10, quote.filledBaseLots());
    assertEquals(1_000, quote.filledQuoteLots());
    assertEquals(2, quote.feeQuoteLots());
    assertEquals(3, quote.postedBaseLots());
    assertEquals(10, quote.unfilledBaseLots());
    assertEquals(100, quote.averagePriceQuoteLotsPerBaseLot());
    assertEquals(101, quote.referencePriceTicks());
    assertEquals(-99, quote.slippageBps());
    assertEquals(20, quote.effectiveBaseLots());
    assertEquals(99, quote.postedPriceTicks());
    assertEquals(0, quote.outcome());
    assertEquals(0, quote.rejectionReason());
  }
}

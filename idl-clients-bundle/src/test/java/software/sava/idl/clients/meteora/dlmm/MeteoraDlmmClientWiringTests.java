package software.sava.idl.clients.meteora.dlmm;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.meteora.MeteoraAccounts;
import software.sava.idl.clients.meteora.MeteoraPDAs;
import software.sava.idl.clients.meteora.dlmm.gen.LbClmmProgram;
import software.sava.idl.clients.meteora.dlmm.gen.types.*;

import java.util.Arrays;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;
import static software.sava.idl.clients.meteora.dlmm.DlmmUtils.NO_REMAINING_ACCOUNTS;

/// Pins every instruction builder on the DLMM client to the generated
/// `LbClmmProgram` call it must delegate to, with the client-supplied defaults
/// (fee payer, owner, event authority, program id) spelled out on the mirror
/// side — the generated builder's parameter names are the reference for each
/// slot. Every account is a distinct fill-byte key, so a transposed pair of
/// same-typed keys changes the compared list. The default overloads are then
/// checked against the explicit calls they delegate to: an `LbPair`- or
/// `PositionV2`-sourced field landing in the wrong slot shows up as a
/// different account list, and derived reserves/vault PDAs are recomputed
/// independently here.
final class MeteoraDlmmClientWiringTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final MeteoraAccounts METEORA_ACCOUNTS = MeteoraAccounts.MAIN_NET;

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey FEE_PAYER = key(0x12);

  private static final MeteoraDlmmClient CLIENT =
      MeteoraDlmmClient.createClient(SOLANA_ACCOUNTS, METEORA_ACCOUNTS, OWNER, AccountMeta.createFeePayer(FEE_PAYER));

  private static final PublicKey POSITION = key(0x20);
  private static final PublicKey LB_PAIR = key(0x21);
  private static final PublicKey BITMAP_EXT = key(0x22);
  private static final PublicKey USER_X = key(0x23);
  private static final PublicKey USER_Y = key(0x24);
  private static final PublicKey RESERVE_X = key(0x25);
  private static final PublicKey RESERVE_Y = key(0x26);
  private static final PublicKey MINT_X = key(0x27);
  private static final PublicKey MINT_Y = key(0x28);
  private static final PublicKey PROG_X = key(0x29);
  private static final PublicKey PROG_Y = key(0x2A);
  private static final PublicKey ORACLE = key(0x2B);
  private static final PublicKey HOST_FEE = key(0x2C);
  private static final PublicKey REWARD_VAULT = key(0x2D);
  private static final PublicKey REWARD_MINT = key(0x2E);
  private static final PublicKey USER_TOKEN = key(0x2F);
  private static final PublicKey LIMIT_ORDER = key(0x30);
  private static final PublicKey RENT_RECEIVER = key(0x31);
  private static final PublicKey FUNDER = key(0x32);
  private static final PublicKey BASE = key(0x33);
  private static final PublicKey FROM_ARRAY = key(0x34);
  private static final PublicKey TO_ARRAY = key(0x35);
  private static final PublicKey BIN_LOWER = key(0x36);
  private static final PublicKey BIN_UPPER = key(0x37);

  private static final AccountMeta INVOKED = METEORA_ACCOUNTS.invokedDlmmProgram();
  private static final PublicKey DLMM = METEORA_ACCOUNTS.dlmmProgram();
  private static final PublicKey EVENT_AUTHORITY = METEORA_ACCOUNTS.eventAuthority().publicKey();

  private static final StrategyParameters STRATEGY_PARAMS =
      new StrategyParameters(5, 6, StrategyType.SpotBalanced, new byte[StrategyParameters.PARAMETERES_LEN]);
  private static final LiquidityParameterByStrategy STRATEGY =
      new LiquidityParameterByStrategy(1L, 2L, 3, 4, STRATEGY_PARAMS);
  private static final LiquidityParameter LIQUIDITY =
      new LiquidityParameter(1L, 2L, new BinLiquidityDistribution[]{new BinLiquidityDistribution(7, 8, 9)});
  private static final AddLiquiditySingleSidePreciseParameter2 PRECISE =
      new AddLiquiditySingleSidePreciseParameter2(new CompressedBinDepositAmount[]{new CompressedBinDepositAmount(7, 8L)}, 1L, 2L);
  private static final LiquidityParameterByWeight WEIGHT =
      new LiquidityParameterByWeight(1L, 2L, 3, 4, new BinLiquidityDistributionByWeight[]{new BinLiquidityDistributionByWeight(7, 8)});
  private static final LiquidityParameterByStrategyOneSide STRATEGY_ONE_SIDE =
      new LiquidityParameterByStrategyOneSide(1L, 2, 3, STRATEGY_PARAMS);
  private static final LiquidityOneSideParameter ONE_SIDE =
      new LiquidityOneSideParameter(1L, 2, 3, new BinLiquidityDistributionByWeight[]{new BinLiquidityDistributionByWeight(7, 8)});
  private static final BinLiquidityReduction[] REDUCTIONS = {new BinLiquidityReduction(7, 8)};
  private static final RebalanceLiquidityParams REBALANCE = new RebalanceLiquidityParams(
      1, 2, true, false, 3L, 4L, 5L, 6L, 7,
      new byte[RebalanceLiquidityParams.PADDING_LEN], new RemoveLiquidityParams[0], new AddLiquidityParams[0]
  );
  private static final PlaceLimitOrderParams LIMIT_PARAMS = new PlaceLimitOrderParams(
      true, new byte[PlaceLimitOrderParams.PADDING_LEN], new RelativeBin(1, 2),
      new BinLimitOrderAmount[]{new BinLimitOrderAmount(7, 8L)}
  );

  private static void assertIx(final Instruction expected, final Instruction actual) {
    assertEquals(expected.programId().publicKey(), actual.programId().publicKey(), "invoked program");
    assertEquals(
        expected.accounts().stream().map(AccountMeta::publicKey).toList(),
        actual.accounts().stream().map(AccountMeta::publicKey).toList(),
        "account order"
    );
    for (int i = 0; i < expected.accounts().size(); i++) {
      final var e = expected.accounts().get(i);
      final var a = actual.accounts().get(i);
      assertEquals(e.write(), a.write(), "writable at slot " + i);
      assertEquals(e.signer(), a.signer(), "signer at slot " + i);
    }
    assertArrayEquals(expected.data(), actual.data(), "instruction data");
  }

  // ---------------------------------------------------------------------------
  // impl -> generated builder bindings
  // ---------------------------------------------------------------------------

  @Test
  void positionLifecycleBindsTheGeneratedBuilders() {
    assertIx(
        LbClmmProgram.initializePosition(INVOKED, SOLANA_ACCOUNTS, FEE_PAYER, POSITION, LB_PAIR,
            SOLANA_ACCOUNTS.rentSysVar(), OWNER, EVENT_AUTHORITY, DLMM, 5, 10
        ),
        CLIENT.initializePosition(POSITION, LB_PAIR, 5, 10)
    );
    assertIx(
        LbClmmProgram.initializePositionPda(INVOKED, SOLANA_ACCOUNTS, FEE_PAYER, BASE, POSITION, LB_PAIR,
            SOLANA_ACCOUNTS.rentSysVar(), OWNER, EVENT_AUTHORITY, DLMM, 5, 10
        ),
        CLIENT.initializePositionWithSeeds(POSITION, BASE, LB_PAIR, 5, 10)
    );
    assertIx(
        LbClmmProgram.initializePosition2(INVOKED, SOLANA_ACCOUNTS, FEE_PAYER, POSITION, LB_PAIR,
            OWNER, EVENT_AUTHORITY, DLMM, 5, 10
        ),
        CLIENT.initializePosition2(POSITION, LB_PAIR, 5, 10)
    );
    assertIx(
        LbClmmProgram.increasePositionLength2(INVOKED, SOLANA_ACCOUNTS, FUNDER, LB_PAIR, POSITION,
            OWNER, EVENT_AUTHORITY, DLMM, 42
        ),
        CLIENT.increasePositionLength2(FUNDER, LB_PAIR, POSITION, 42)
    );
    assertIx(
        LbClmmProgram.decreasePositionLength(INVOKED, SOLANA_ACCOUNTS, RENT_RECEIVER, POSITION,
            OWNER, EVENT_AUTHORITY, DLMM, 3, 1
        ),
        CLIENT.decreasePositionLength(POSITION, RENT_RECEIVER, 3, 1)
    );
    assertIx(
        LbClmmProgram.closePosition2(INVOKED, POSITION, OWNER, RENT_RECEIVER, EVENT_AUTHORITY, DLMM),
        CLIENT.closePosition(POSITION, RENT_RECEIVER)
    );
    assertIx(
        LbClmmProgram.closePositionIfEmpty(INVOKED, POSITION, OWNER, RENT_RECEIVER, EVENT_AUTHORITY, DLMM),
        CLIENT.closePositionIfEmpty(POSITION, RENT_RECEIVER)
    );
    assertIx(
        LbClmmProgram.goToABin(INVOKED, LB_PAIR, BITMAP_EXT, FROM_ARRAY, TO_ARRAY, EVENT_AUTHORITY, DLMM, 9),
        CLIENT.goToABin(LB_PAIR, BITMAP_EXT, FROM_ARRAY, TO_ARRAY, 9)
    );
    assertIx(
        LbClmmProgram.setPairStatusPermissionless(INVOKED, LB_PAIR, OWNER, 1),
        CLIENT.setPairStatusPermissionless(LB_PAIR, 1)
    );
  }

  @Test
  void liquidityOperationsBindTheGeneratedBuilders() {
    // a null remaining-accounts info must default to the empty payload
    assertIx(
        LbClmmProgram.addLiquidityByStrategy2(INVOKED, POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, OWNER, PROG_X, PROG_Y, EVENT_AUTHORITY, DLMM,
            STRATEGY, NO_REMAINING_ACCOUNTS
        ),
        CLIENT.addLiquidityByStrategy(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, PROG_X, PROG_Y, STRATEGY, null
        )
    );
    assertIx(
        LbClmmProgram.addLiquidity2(INVOKED, POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, OWNER, PROG_X, PROG_Y, EVENT_AUTHORITY, DLMM,
            LIQUIDITY, NO_REMAINING_ACCOUNTS
        ),
        CLIENT.addLiquidity(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, PROG_X, PROG_Y, LIQUIDITY, null
        )
    );
    assertIx(
        LbClmmProgram.addLiquidityOneSidePrecise2(INVOKED, POSITION, LB_PAIR, BITMAP_EXT, USER_TOKEN,
            RESERVE_X, MINT_X, OWNER, PROG_X, EVENT_AUTHORITY, DLMM, PRECISE, NO_REMAINING_ACCOUNTS
        ),
        CLIENT.addLiquidityOneSidePrecise(POSITION, LB_PAIR, BITMAP_EXT, USER_TOKEN,
            RESERVE_X, MINT_X, PROG_X, PRECISE, null
        )
    );
    assertIx(
        LbClmmProgram.addLiquidityByWeight2(INVOKED, POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, OWNER, PROG_X, PROG_Y, EVENT_AUTHORITY, DLMM,
            WEIGHT, NO_REMAINING_ACCOUNTS
        ),
        CLIENT.addLiquidityByWeight(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, PROG_X, PROG_Y, WEIGHT, null
        )
    );
    assertIx(
        LbClmmProgram.addLiquidityByStrategyOneSide(INVOKED, POSITION, LB_PAIR, BITMAP_EXT, USER_TOKEN,
            RESERVE_X, MINT_X, BIN_LOWER, BIN_UPPER, OWNER, PROG_X, EVENT_AUTHORITY, DLMM, STRATEGY_ONE_SIDE
        ),
        CLIENT.addLiquidityByStrategyOneSide(POSITION, LB_PAIR, BITMAP_EXT, USER_TOKEN,
            RESERVE_X, MINT_X, BIN_LOWER, BIN_UPPER, PROG_X, STRATEGY_ONE_SIDE
        )
    );
    assertIx(
        LbClmmProgram.addLiquidityOneSide(INVOKED, POSITION, LB_PAIR, BITMAP_EXT, USER_TOKEN,
            RESERVE_X, MINT_X, BIN_LOWER, BIN_UPPER, OWNER, PROG_X, EVENT_AUTHORITY, DLMM, ONE_SIDE
        ),
        CLIENT.addLiquidityOneSide(POSITION, LB_PAIR, BITMAP_EXT, USER_TOKEN,
            RESERVE_X, MINT_X, BIN_LOWER, BIN_UPPER, PROG_X, ONE_SIDE
        )
    );
    assertIx(
        LbClmmProgram.removeLiquidityByRange2(INVOKED, SOLANA_ACCOUNTS, POSITION, LB_PAIR, BITMAP_EXT,
            USER_X, USER_Y, RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, OWNER, PROG_X, PROG_Y,
            EVENT_AUTHORITY, DLMM, 5, 10, 2_500, NO_REMAINING_ACCOUNTS
        ),
        CLIENT.removeLiquidityByRange(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, PROG_X, PROG_Y, 5, 10, 2_500, null
        )
    );
    assertIx(
        LbClmmProgram.removeLiquidity2(INVOKED, SOLANA_ACCOUNTS, POSITION, LB_PAIR, BITMAP_EXT,
            USER_X, USER_Y, RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, OWNER, PROG_X, PROG_Y,
            EVENT_AUTHORITY, DLMM, REDUCTIONS, NO_REMAINING_ACCOUNTS
        ),
        CLIENT.removeLiquidity(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, PROG_X, PROG_Y, REDUCTIONS, null
        )
    );
    assertIx(
        LbClmmProgram.removeAllLiquidity(INVOKED, POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, BIN_LOWER, BIN_UPPER, OWNER, PROG_X, PROG_Y,
            EVENT_AUTHORITY, DLMM
        ),
        CLIENT.removeAllLiquidity(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, BIN_LOWER, BIN_UPPER, PROG_X, PROG_Y
        )
    );
    assertIx(
        LbClmmProgram.rebalanceLiquidity(INVOKED, SOLANA_ACCOUNTS, POSITION, LB_PAIR, BITMAP_EXT,
            USER_X, USER_Y, RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, OWNER, RENT_RECEIVER, PROG_X, PROG_Y,
            EVENT_AUTHORITY, DLMM, REBALANCE, NO_REMAINING_ACCOUNTS
        ),
        CLIENT.rebalanceLiquidity(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, RENT_RECEIVER, PROG_X, PROG_Y, REBALANCE, null
        )
    );
  }

  @Test
  void claimsBindTheGeneratedBuilders() {
    assertIx(
        LbClmmProgram.claimFee2(INVOKED, SOLANA_ACCOUNTS, LB_PAIR, POSITION, OWNER,
            RESERVE_X, RESERVE_Y, USER_X, USER_Y, MINT_X, MINT_Y, PROG_X, PROG_Y,
            EVENT_AUTHORITY, DLMM, 5, 10, NO_REMAINING_ACCOUNTS
        ),
        CLIENT.claimFee(LB_PAIR, POSITION, RESERVE_X, RESERVE_Y, USER_X, USER_Y,
            MINT_X, MINT_Y, PROG_X, PROG_Y, 5, 10, null
        )
    );
    assertIx(
        LbClmmProgram.claimReward2(INVOKED, SOLANA_ACCOUNTS, LB_PAIR, POSITION, OWNER,
            REWARD_VAULT, REWARD_MINT, USER_TOKEN, PROG_X, EVENT_AUTHORITY, DLMM,
            1, 5, 10, NO_REMAINING_ACCOUNTS
        ),
        CLIENT.claimReward(LB_PAIR, POSITION, REWARD_VAULT, REWARD_MINT, USER_TOKEN, PROG_X, 1, 5, 10, null)
    );
  }

  @Test
  void swapsAndLimitOrdersBindTheGeneratedBuilders() {
    assertIx(
        LbClmmProgram.swap2(INVOKED, SOLANA_ACCOUNTS, LB_PAIR, BITMAP_EXT, RESERVE_X, RESERVE_Y,
            USER_X, USER_Y, MINT_X, MINT_Y, ORACLE, HOST_FEE, OWNER, PROG_X, PROG_Y,
            EVENT_AUTHORITY, DLMM, 1_000L, 900L, NO_REMAINING_ACCOUNTS
        ),
        CLIENT.swap(LB_PAIR, BITMAP_EXT, RESERVE_X, RESERVE_Y, USER_X, USER_Y,
            MINT_X, MINT_Y, ORACLE, HOST_FEE, PROG_X, PROG_Y, 1_000L, 900L, null
        )
    );
    assertIx(
        LbClmmProgram.swapExactOut2(INVOKED, SOLANA_ACCOUNTS, LB_PAIR, BITMAP_EXT, RESERVE_X, RESERVE_Y,
            USER_X, USER_Y, MINT_X, MINT_Y, ORACLE, HOST_FEE, OWNER, PROG_X, PROG_Y,
            EVENT_AUTHORITY, DLMM, 1_100L, 1_000L, NO_REMAINING_ACCOUNTS
        ),
        CLIENT.swapExactOut(LB_PAIR, BITMAP_EXT, RESERVE_X, RESERVE_Y, USER_X, USER_Y,
            MINT_X, MINT_Y, ORACLE, HOST_FEE, PROG_X, PROG_Y, 1_100L, 1_000L, null
        )
    );
    assertIx(
        LbClmmProgram.swapWithPriceImpact2(INVOKED, SOLANA_ACCOUNTS, LB_PAIR, BITMAP_EXT, RESERVE_X, RESERVE_Y,
            USER_X, USER_Y, MINT_X, MINT_Y, ORACLE, HOST_FEE, OWNER, PROG_X, PROG_Y,
            EVENT_AUTHORITY, DLMM, 1_000L, OptionalInt.of(7), 50, NO_REMAINING_ACCOUNTS
        ),
        CLIENT.swapWithPriceImpact(LB_PAIR, BITMAP_EXT, RESERVE_X, RESERVE_Y, USER_X, USER_Y,
            MINT_X, MINT_Y, ORACLE, HOST_FEE, PROG_X, PROG_Y, 1_000L, OptionalInt.of(7), 50, null
        )
    );

    // hostFeeIn is a trailing optional: absent, the program id itself holds the
    // slot so every later account keeps its position
    final var withHostFee = CLIENT.swap(LB_PAIR, BITMAP_EXT, RESERVE_X, RESERVE_Y, USER_X, USER_Y,
        MINT_X, MINT_Y, ORACLE, HOST_FEE, PROG_X, PROG_Y, 1_000L, 900L, null
    );
    final var withoutHostFee = CLIENT.swap(LB_PAIR, BITMAP_EXT, RESERVE_X, RESERVE_Y, USER_X, USER_Y,
        MINT_X, MINT_Y, ORACLE, null, PROG_X, PROG_Y, 1_000L, 900L, null
    );
    final int hostFeeSlot = withHostFee.accounts().stream().map(AccountMeta::publicKey).toList().indexOf(HOST_FEE);
    assertTrue(hostFeeSlot >= 0, "the host fee account must be present when supplied");
    assertEquals(DLMM, withoutHostFee.accounts().get(hostFeeSlot).publicKey(),
        "an absent host fee is substituted by the program id, not dropped"
    );
    assertEquals(withHostFee.accounts().size(), withoutHostFee.accounts().size(),
        "the sentinel keeps every later account in position"
    );

    // placeLimitOrder wires the fee payer as the rent payer and the owner twice
    assertIx(
        LbClmmProgram.placeLimitOrder(INVOKED, SOLANA_ACCOUNTS, LB_PAIR, BITMAP_EXT, RESERVE_X, MINT_X,
            LIMIT_ORDER, FEE_PAYER, OWNER, USER_TOKEN, OWNER, PROG_X, EVENT_AUTHORITY, DLMM,
            LIMIT_PARAMS, NO_REMAINING_ACCOUNTS
        ),
        CLIENT.placeLimitOrder(LB_PAIR, BITMAP_EXT, RESERVE_X, MINT_X, LIMIT_ORDER, USER_TOKEN, PROG_X,
            LIMIT_PARAMS, null
        )
    );
    assertIx(
        LbClmmProgram.cancelLimitOrder(INVOKED, SOLANA_ACCOUNTS, LB_PAIR, BITMAP_EXT, RESERVE_X, RESERVE_Y,
            MINT_X, MINT_Y, LIMIT_ORDER, USER_X, USER_Y, OWNER, PROG_X, PROG_Y, EVENT_AUTHORITY, DLMM,
            new int[]{1, 2}, NO_REMAINING_ACCOUNTS
        ),
        CLIENT.cancelLimitOrder(LB_PAIR, BITMAP_EXT, RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, LIMIT_ORDER,
            USER_X, USER_Y, PROG_X, PROG_Y, new int[]{1, 2}, null
        )
    );
    assertIx(
        LbClmmProgram.closeLimitOrderIfEmpty(INVOKED, LIMIT_ORDER, OWNER, RENT_RECEIVER, EVENT_AUTHORITY, DLMM),
        CLIENT.closeLimitOrderIfEmpty(LIMIT_ORDER, RENT_RECEIVER)
    );
  }

  // ---------------------------------------------------------------------------
  // default overloads delegate with the documented defaults
  // ---------------------------------------------------------------------------

  /// Only the fields the overloads read are populated; a generated record
  /// accepts null for everything else.
  private static PositionV2 position(final PublicKey address, final PublicKey lbPair,
                                     final int lowerBinId, final int upperBinId) {
    return new PositionV2(address, null, lbPair, null, null, null, null, lowerBinId, upperBinId,
        0L, 0L, 0L, null, null, 0L, 0, null, 0, 0, null
    );
  }

  private static LbPair lbPair(final PublicKey address) {
    return new LbPair(address, null, null, null, null, null, 0, 0, 0, 0, 0, null, 0, 0,
        MINT_X, MINT_Y, RESERVE_X, RESERVE_Y, null, null, null, ORACLE, null, 0L, null,
        null, null, 0L, 0L, null, 0L, null, 0, 0, 0, null
    );
  }

  private static final LbPair FETCHED_PAIR = lbPair(LB_PAIR);
  private static final PositionV2 FETCHED_POSITION = position(POSITION, LB_PAIR, 5, 10);

  @Test
  void lbPairOverloadsSourceReservesMintsAndOracleFromTheAccount() {
    assertIx(
        CLIENT.addLiquidityByStrategy(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, PROG_X, PROG_Y, STRATEGY, null
        ),
        CLIENT.addLiquidityByStrategy(POSITION, FETCHED_PAIR, BITMAP_EXT, USER_X, USER_Y,
            PROG_X, PROG_Y, STRATEGY, null
        )
    );
    assertIx(
        CLIENT.addLiquidity(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, PROG_X, PROG_Y, LIQUIDITY, null
        ),
        CLIENT.addLiquidity(POSITION, FETCHED_PAIR, BITMAP_EXT, USER_X, USER_Y,
            PROG_X, PROG_Y, LIQUIDITY, null
        )
    );
    assertIx(
        CLIENT.addLiquidityByWeight(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, PROG_X, PROG_Y, WEIGHT, null
        ),
        CLIENT.addLiquidityByWeight(POSITION, FETCHED_PAIR, BITMAP_EXT, USER_X, USER_Y,
            PROG_X, PROG_Y, WEIGHT, null
        )
    );
    assertIx(
        CLIENT.removeLiquidityByRange(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, PROG_X, PROG_Y, 5, 10, 2_500, null
        ),
        CLIENT.removeLiquidityByRange(POSITION, FETCHED_PAIR, BITMAP_EXT, USER_X, USER_Y,
            PROG_X, PROG_Y, 5, 10, 2_500, null
        )
    );
    assertIx(
        CLIENT.removeLiquidity(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, PROG_X, PROG_Y, REDUCTIONS, null
        ),
        CLIENT.removeLiquidity(POSITION, FETCHED_PAIR, BITMAP_EXT, USER_X, USER_Y,
            PROG_X, PROG_Y, REDUCTIONS, null
        )
    );
    assertIx(
        CLIENT.rebalanceLiquidity(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, FEE_PAYER, PROG_X, PROG_Y, REBALANCE, null
        ),
        CLIENT.rebalanceLiquidity(POSITION, FETCHED_PAIR, BITMAP_EXT, USER_X, USER_Y,
            PROG_X, PROG_Y, REBALANCE, null
        )
    );

    // bid fills the Y side, ask the X side — swapping them funds the wrong reserve
    assertIx(
        CLIENT.addLiquidityOneSidePrecise(POSITION, LB_PAIR, BITMAP_EXT, USER_TOKEN,
            RESERVE_Y, MINT_Y, PROG_Y, PRECISE, null
        ),
        CLIENT.bidLiquidityPrecise(POSITION, FETCHED_PAIR, BITMAP_EXT, USER_TOKEN, PROG_Y, PRECISE, null)
    );
    assertIx(
        CLIENT.addLiquidityOneSidePrecise(POSITION, LB_PAIR, BITMAP_EXT, USER_TOKEN,
            RESERVE_X, MINT_X, PROG_X, PRECISE, null
        ),
        CLIENT.askLiquidityPrecise(POSITION, FETCHED_PAIR, BITMAP_EXT, USER_TOKEN, PROG_X, PRECISE, null)
    );

    assertIx(
        CLIENT.swap(LB_PAIR, BITMAP_EXT, RESERVE_X, RESERVE_Y, USER_X, USER_Y,
            MINT_X, MINT_Y, ORACLE, HOST_FEE, PROG_X, PROG_Y, 1_000L, 900L, null
        ),
        CLIENT.swap(FETCHED_PAIR, BITMAP_EXT, USER_X, USER_Y, HOST_FEE, PROG_X, PROG_Y, 1_000L, 900L, null)
    );
    assertIx(
        CLIENT.swapExactOut(LB_PAIR, BITMAP_EXT, RESERVE_X, RESERVE_Y, USER_X, USER_Y,
            MINT_X, MINT_Y, ORACLE, HOST_FEE, PROG_X, PROG_Y, 1_100L, 1_000L, null
        ),
        CLIENT.swapExactOut(FETCHED_PAIR, BITMAP_EXT, USER_X, USER_Y, HOST_FEE, PROG_X, PROG_Y, 1_100L, 1_000L, null)
    );
    assertIx(
        CLIENT.swapWithPriceImpact(LB_PAIR, BITMAP_EXT, RESERVE_X, RESERVE_Y, USER_X, USER_Y,
            MINT_X, MINT_Y, ORACLE, HOST_FEE, PROG_X, PROG_Y, 1_000L, OptionalInt.of(7), 50, null
        ),
        CLIENT.swapWithPriceImpact(FETCHED_PAIR, BITMAP_EXT, USER_X, USER_Y, HOST_FEE, PROG_X, PROG_Y,
            1_000L, OptionalInt.of(7), 50, null
        )
    );
    assertIx(
        CLIENT.cancelLimitOrder(LB_PAIR, BITMAP_EXT, RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, LIMIT_ORDER,
            USER_X, USER_Y, PROG_X, PROG_Y, new int[]{1, 2}, null
        ),
        CLIENT.cancelLimitOrder(FETCHED_PAIR, BITMAP_EXT, LIMIT_ORDER, USER_X, USER_Y, PROG_X, PROG_Y,
            new int[]{1, 2}, null
        )
    );
    assertIx(
        CLIENT.claimFee(LB_PAIR, POSITION, RESERVE_X, RESERVE_Y, USER_X, USER_Y,
            MINT_X, MINT_Y, PROG_X, PROG_Y, 5, 10, null
        ),
        CLIENT.claimFee(FETCHED_PAIR, FETCHED_POSITION, USER_X, USER_Y, PROG_X, PROG_Y, null)
    );
    assertIx(
        CLIENT.removeAllLiquidity(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, BIN_LOWER, BIN_UPPER, PROG_X, PROG_Y
        ),
        CLIENT.removeAllLiquidity(FETCHED_POSITION, FETCHED_PAIR, BITMAP_EXT, USER_X, USER_Y,
            BIN_LOWER, BIN_UPPER, PROG_X, PROG_Y
        )
    );
  }

  @Test
  void mintOverloadsDeriveTheReservePDAs() {
    final var reserveX = MeteoraPDAs.reservePDA(LB_PAIR, MINT_X, DLMM).publicKey();
    final var reserveY = MeteoraPDAs.reservePDA(LB_PAIR, MINT_Y, DLMM).publicKey();

    assertIx(
        CLIENT.addLiquidityByStrategy(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            reserveX, reserveY, MINT_X, MINT_Y, PROG_X, PROG_Y, STRATEGY, null
        ),
        CLIENT.addLiquidityByStrategy(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            MINT_X, MINT_Y, PROG_X, PROG_Y, STRATEGY, null
        )
    );
    assertIx(
        CLIENT.addLiquidity(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            reserveX, reserveY, MINT_X, MINT_Y, PROG_X, PROG_Y, LIQUIDITY, null
        ),
        CLIENT.addLiquidity(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            MINT_X, MINT_Y, PROG_X, PROG_Y, LIQUIDITY, null
        )
    );
    assertIx(
        CLIENT.removeLiquidityByRange(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            reserveX, reserveY, MINT_X, MINT_Y, PROG_X, PROG_Y, 5, 10, 2_500, null
        ),
        CLIENT.removeLiquidityByRange(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            MINT_X, MINT_Y, PROG_X, PROG_Y, 5, 10, 2_500, null
        )
    );
    assertIx(
        CLIENT.removeLiquidity(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            reserveX, reserveY, MINT_X, MINT_Y, PROG_X, PROG_Y, REDUCTIONS, null
        ),
        CLIENT.removeLiquidity(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            MINT_X, MINT_Y, PROG_X, PROG_Y, REDUCTIONS, null
        )
    );
    assertIx(
        CLIENT.claimFee(LB_PAIR, POSITION, reserveX, reserveY, USER_X, USER_Y,
            MINT_X, MINT_Y, PROG_X, PROG_Y, 5, 10, null
        ),
        CLIENT.claimFee(LB_PAIR, POSITION, USER_X, USER_Y, MINT_X, MINT_Y, PROG_X, PROG_Y, 5, 10, null)
    );

    // the PositionV2 overload additionally sources position, pair and bin range
    assertIx(
        CLIENT.claimFee(LB_PAIR, POSITION, reserveX, reserveY, USER_X, USER_Y,
            MINT_X, MINT_Y, PROG_X, PROG_Y, 5, 10, null
        ),
        CLIENT.claimFee(FETCHED_POSITION, USER_X, USER_Y, MINT_X, MINT_Y, PROG_X, PROG_Y, null)
    );
    assertIx(
        CLIENT.removeLiquidityByRange(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            reserveX, reserveY, MINT_X, MINT_Y, PROG_X, PROG_Y, 5, 10, 2_500, null
        ),
        CLIENT.removeLiquidityByRange(FETCHED_POSITION, BITMAP_EXT, USER_X, USER_Y,
            MINT_X, MINT_Y, PROG_X, PROG_Y, 2_500, null
        )
    );
    assertIx(
        CLIENT.removeLiquidityByRange(POSITION, LB_PAIR, BITMAP_EXT, USER_X, USER_Y,
            RESERVE_X, RESERVE_Y, MINT_X, MINT_Y, PROG_X, PROG_Y, 5, 10, 2_500, null
        ),
        CLIENT.removeLiquidityByRange(FETCHED_POSITION, FETCHED_PAIR, BITMAP_EXT, USER_X, USER_Y,
            PROG_X, PROG_Y, 2_500, null
        )
    );

    // claimReward's short form derives the reward vault for its index — and a
    // different index derives a different vault
    final var vault1 = MeteoraPDAs.rewardVaultPDA(LB_PAIR, 1, DLMM).publicKey();
    assertIx(
        CLIENT.claimReward(LB_PAIR, POSITION, vault1, REWARD_MINT, USER_TOKEN, PROG_X, 1, 5, 10, null),
        CLIENT.claimReward(LB_PAIR, POSITION, REWARD_MINT, USER_TOKEN, PROG_X, 1, 5, 10, null)
    );
    assertIx(
        CLIENT.claimReward(LB_PAIR, POSITION, vault1, REWARD_MINT, USER_TOKEN, PROG_X, 1, 5, 10, null),
        CLIENT.claimReward(FETCHED_POSITION, REWARD_MINT, USER_TOKEN, PROG_X, 1, null)
    );
    assertNotEquals(
        CLIENT.claimReward(LB_PAIR, POSITION, REWARD_MINT, USER_TOKEN, PROG_X, 0, 5, 10, null)
            .accounts().stream().map(AccountMeta::publicKey).toList(),
        CLIENT.claimReward(LB_PAIR, POSITION, REWARD_MINT, USER_TOKEN, PROG_X, 1, 5, 10, null)
            .accounts().stream().map(AccountMeta::publicKey).toList(),
        "the reward index selects the vault"
    );
  }

  @Test
  void feePayerFallbacksAndPositionShortForms() {
    assertIx(CLIENT.initializePositionWithSeeds(POSITION, FEE_PAYER, LB_PAIR, 5, 10),
        CLIENT.initializePositionWithSeeds(POSITION, LB_PAIR, 5, 10)
    );
    assertIx(CLIENT.closePosition(POSITION, FEE_PAYER), CLIENT.closePosition(POSITION));
    assertIx(CLIENT.closePosition(POSITION, RENT_RECEIVER),
        CLIENT.closePosition(FETCHED_POSITION, RENT_RECEIVER)
    );
    assertIx(CLIENT.closePosition(POSITION, FEE_PAYER), CLIENT.closePosition(FETCHED_POSITION));
    assertIx(CLIENT.closePositionIfEmpty(POSITION, FEE_PAYER), CLIENT.closePositionIfEmpty(POSITION));
    assertIx(CLIENT.closePositionIfEmpty(POSITION, RENT_RECEIVER),
        CLIENT.closePositionIfEmpty(FETCHED_POSITION, RENT_RECEIVER)
    );
    assertIx(CLIENT.closePositionIfEmpty(POSITION, FEE_PAYER), CLIENT.closePositionIfEmpty(FETCHED_POSITION));
    assertIx(CLIENT.increasePositionLength2(FEE_PAYER, LB_PAIR, POSITION, 42),
        CLIENT.increasePositionLength2(LB_PAIR, POSITION, 42)
    );
    assertIx(CLIENT.decreasePositionLength(POSITION, FEE_PAYER, 3, 1),
        CLIENT.decreasePositionLength(POSITION, 3, 1)
    );
    assertIx(CLIENT.closeLimitOrderIfEmpty(LIMIT_ORDER, FEE_PAYER),
        CLIENT.closeLimitOrderIfEmpty(LIMIT_ORDER)
    );

    // derivePositionAccount defaults its base to the fee payer
    assertEquals(
        MeteoraPDAs.positionPDA(LB_PAIR, BASE, 5, 10, DLMM).publicKey(),
        CLIENT.derivePositionAccount(LB_PAIR, BASE, 5, 10).publicKey()
    );
    assertEquals(
        MeteoraPDAs.positionPDA(LB_PAIR, FEE_PAYER, 5, 10, DLMM).publicKey(),
        CLIENT.derivePositionAccount(LB_PAIR, 5, 10).publicKey()
    );
  }

  @Test
  void binAccountDerivationsDelegateAcrossOverloads() {
    // the PositionV2 static overload reads pair and range from the account
    assertEquals(
        MeteoraDlmmClient.deriveBinAccounts(DLMM, LB_PAIR, 5, 10).stream().map(AccountMeta::publicKey).toList(),
        MeteoraDlmmClient.deriveBinAccounts(DLMM, FETCHED_POSITION).stream().map(AccountMeta::publicKey).toList()
    );
    // the instance overloads default the program id
    assertEquals(
        MeteoraDlmmClient.deriveBinAccounts(DLMM, LB_PAIR, 5, 10).stream().map(AccountMeta::publicKey).toList(),
        CLIENT.deriveBinAccounts(LB_PAIR, 5, 10).stream().map(AccountMeta::publicKey).toList()
    );
    assertEquals(
        CLIENT.deriveBinAccounts(LB_PAIR, 5, 10).stream().map(AccountMeta::publicKey).toList(),
        CLIENT.deriveBinAccounts(FETCHED_POSITION).stream().map(AccountMeta::publicKey).toList()
    );

    // appendBinAccounts appends exactly the derived range after the instruction's own
    final var ix = CLIENT.closePosition(POSITION, RENT_RECEIVER);
    final var appended = CLIENT.appendBinAccounts(LB_PAIR, 5, 10, ix);
    final var derived = CLIENT.deriveBinAccounts(LB_PAIR, 5, 10);
    assertEquals(ix.accounts().size() + derived.size(), appended.accounts().size());
    for (int i = 0; i < derived.size(); i++) {
      assertEquals(derived.get(i).publicKey(),
          appended.accounts().get(ix.accounts().size() + i).publicKey(), "appended slot " + i
      );
    }
  }
}

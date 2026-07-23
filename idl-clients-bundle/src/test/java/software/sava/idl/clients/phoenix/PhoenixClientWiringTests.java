package software.sava.idl.clients.phoenix;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.phoenix.ember.gen.EmberProgram;
import software.sava.idl.clients.phoenix.ember.gen.types.DepositParams;
import software.sava.idl.clients.phoenix.ember.gen.types.WithdrawParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.EternalProgram;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.*;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;

/// Pins every remaining PhoenixClient builder to the generated `EmberProgram`
/// / `EternalProgram` call it delegates to, with the client-supplied constants
/// (program id, log authority, global config, trader index, active trader
/// buffer, system program) spelled out on the mirror side. The generated
/// builders' parameter names are the reference per slot; several conditional
/// and escrow instructions take the caller's keys in a *different* order than
/// the client method declares them, which is exactly the transposition these
/// mirrors freeze.
final class PhoenixClientWiringTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final PhoenixAccounts ACCOUNTS = PhoenixAccounts.MAIN_NET;
  private static final PhoenixClient CLIENT = PhoenixClient.createClient(SOLANA_ACCOUNTS, ACCOUNTS);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey PAYER = key(0x12);
  private static final PublicKey TRADER_ACCOUNT = key(0x13);
  private static final PublicKey PERP_ASSET_MAP = key(0x14);
  private static final PublicKey ORDERBOOK = key(0x15);
  private static final PublicKey SPLINES = key(0x16);
  private static final PublicKey MINT = key(0x17);
  private static final PublicKey EMBER_MINT = key(0x18);
  private static final PublicKey TOKEN_ACCOUNT = key(0x19);
  private static final PublicKey EMBER_TOKEN_ACCOUNT = key(0x1A);
  private static final PublicKey OUTPUT_MINT = key(0x1B);
  private static final PublicKey OUTPUT_TOKEN_ACCOUNT = key(0x1C);
  private static final PublicKey CONDITIONAL_ORDERS = key(0x1D);
  private static final PublicKey STOP_LOSS = key(0x1E);
  private static final PublicKey NEW_AUTHORITY = key(0x1F);
  private static final PublicKey DST_TRADER = key(0x20);
  private static final PublicKey CHILD_TRADER = key(0x21);
  private static final PublicKey PERMISSION = key(0x22);
  private static final PublicKey RECEIVER_WALLET = key(0x23);
  private static final PublicKey RECEIVER_TRADER = key(0x24);
  private static final PublicKey RECEIVER_ESCROW = key(0x25);
  private static final PublicKey TRADER_ESCROW = key(0x26);
  private static final PublicKey TOKEN_PROGRAM = key(0x27);
  private static final PublicKey FUNDER = key(0x28);

  private static final AccountMeta INVOKED = ACCOUNTS.invokedEternalProgram();
  private static final PublicKey ETERNAL = ACCOUNTS.invokedEternalProgram().publicKey();
  private static final PublicKey LOG = ACCOUNTS.eternalLogAuthority();
  private static final PublicKey CONFIG = ACCOUNTS.eternalGlobalConfig();
  private static final PublicKey TRADER_INDEX = ACCOUNTS.globalTraderIndex();
  private static final PublicKey ACTIVE_BUFFER = ACCOUNTS.activeTraderBuffer();
  private static final PublicKey SYSTEM = SOLANA_ACCOUNTS.systemProgram();

  private static final TriggerOrderParams GREATER =
      new TriggerOrderParams(Direction.GreaterThan, Side.Bid, StopLossOrderKind.IOC, 100L, 101L);
  private static final TriggerOrderParams LESS =
      new TriggerOrderParams(Direction.LessThan, Side.Ask, StopLossOrderKind.Limit, 90L, 89L);
  private static final OrderPacket LIMIT_PACKET = new OrderPacket(new OrderPacketKind.PostOnly(
      Side.Bid, new Ticks(100L), new BaseLots(5L), new byte[OrderPacketKind.PostOnly.CLIENT_ORDER_ID_LEN],
      false, OptionalLong.of(9L), new OrderFlags(0), false));
  private static final MultipleOrderPacket MULTI_PACKET = new MultipleOrderPacket(
      new CondensedOrder[]{new CondensedOrder(100L, 5L, OptionalLong.of(9L))},
      new CondensedOrder[]{new CondensedOrder(101L, 6L, OptionalLong.empty())},
      null, true);
  private static final OrderIds ORDER_IDS = new OrderIds(
      new CancelId[]{new CancelId(new NodePointer(3L), new FIFOOrderId(new Ticks(100L), 7L))});
  private static final EscrowParticipantMetadata ESCROW_META = new EscrowParticipantMetadata(1, 2, 3, 4);
  private static final EscrowAction CASH = new EscrowAction.Cash(1_000L, new byte[EscrowAction.Cash.PADDING_0_LEN]);

  private static void assertIx(final Instruction expected, final Instruction actual) {
    assertEquals(expected.programId().publicKey(), actual.programId().publicKey(), "invoked program");
    assertEquals(
        expected.accounts().stream().map(AccountMeta::publicKey).toList(),
        actual.accounts().stream().map(AccountMeta::publicKey).toList(),
        "account order");
    for (int i = 0; i < expected.accounts().size(); i++) {
      assertEquals(expected.accounts().get(i).write(), actual.accounts().get(i).write(), "writable at slot " + i);
      assertEquals(expected.accounts().get(i).signer(), actual.accounts().get(i).signer(), "signer at slot " + i);
    }
    assertArrayEquals(expected.data(), actual.data(), "instruction data");
  }

  @Test
  void identityAndEmberVaultBindings() {
    assertEquals(SOLANA_ACCOUNTS, CLIENT.solanaAccounts());
    assertEquals(ACCOUNTS, CLIENT.phoenixAccounts());

    assertIx(
        EmberProgram.deposit(ACCOUNTS.invokedEmberProgram(), OWNER, ACCOUNTS.emberStateProgram(),
            MINT, EMBER_MINT, TOKEN_ACCOUNT, EMBER_TOKEN_ACCOUNT, ACCOUNTS.emberVaultProgram(),
            TOKEN_PROGRAM, new DepositParams(1_000L)),
        CLIENT.deposit(OWNER, MINT, EMBER_MINT, TOKEN_ACCOUNT, EMBER_TOKEN_ACCOUNT,
            TOKEN_PROGRAM, new DepositParams(1_000L)));
    assertIx(
        EmberProgram.withdraw(ACCOUNTS.invokedEmberProgram(), OWNER, ACCOUNTS.emberStateProgram(),
            MINT, OUTPUT_MINT, TOKEN_ACCOUNT, OUTPUT_TOKEN_ACCOUNT, ACCOUNTS.emberVaultProgram(),
            TOKEN_PROGRAM, new WithdrawParams(1_000L)),
        CLIENT.withdraw(OWNER, MINT, OUTPUT_MINT, TOKEN_ACCOUNT, OUTPUT_TOKEN_ACCOUNT,
            TOKEN_PROGRAM, new WithdrawParams(1_000L)));
  }

  @Test
  void traderLifecycleBindings() {
    assertIx(
        EternalProgram.registerTrader(INVOKED, ETERNAL, LOG, CONFIG, PAYER, OWNER, TRADER_ACCOUNT,
            SYSTEM, new RegisterTraderParams(8L, 1, 2)),
        CLIENT.registerTrader(PAYER, OWNER, TRADER_ACCOUNT, new RegisterTraderParams(8L, 1, 2)));
    assertIx(
        EternalProgram.delegateTrader(INVOKED, ETERNAL, LOG, CONFIG, OWNER, TRADER_ACCOUNT, NEW_AUTHORITY),
        CLIENT.delegateTrader(OWNER, TRADER_ACCOUNT, NEW_AUTHORITY));
    assertIx(
        EternalProgram.updateTraderState(INVOKED, ETERNAL, LOG, CONFIG, TRADER_ACCOUNT, PERP_ASSET_MAP,
            TRADER_INDEX, ACTIVE_BUFFER),
        CLIENT.updateTraderState(TRADER_ACCOUNT, PERP_ASSET_MAP));
    assertIx(
        EternalProgram.transferCollateral(INVOKED, ETERNAL, LOG, CONFIG, OWNER, TRADER_ACCOUNT,
            DST_TRADER, PERP_ASSET_MAP, TRADER_INDEX, ACTIVE_BUFFER, new TransferCollateralInstruction(1_000L)),
        CLIENT.transferCollateral(OWNER, TRADER_ACCOUNT, DST_TRADER, PERP_ASSET_MAP,
            new TransferCollateralInstruction(1_000L)));
    assertIx(
        EternalProgram.transferCollateralChildToParent(INVOKED, ETERNAL, LOG, CONFIG, OWNER,
            CHILD_TRADER, TRADER_ACCOUNT, PERP_ASSET_MAP, TRADER_INDEX, ACTIVE_BUFFER),
        CLIENT.transferCollateralChildToParent(OWNER, CHILD_TRADER, TRADER_ACCOUNT, PERP_ASSET_MAP));
  }

  @Test
  void orderFlowBindings() {
    assertIx(
        EternalProgram.placeMarketOrder(INVOKED, ETERNAL, LOG, CONFIG, OWNER, TRADER_ACCOUNT,
            PERP_ASSET_MAP, TRADER_INDEX, ACTIVE_BUFFER, ORDERBOOK, SPLINES, LIMIT_PACKET),
        CLIENT.placeMarketOrder(OWNER, TRADER_ACCOUNT, PERP_ASSET_MAP, ORDERBOOK, SPLINES, LIMIT_PACKET));
    assertIx(
        EternalProgram.placeLimitOrder(INVOKED, ETERNAL, LOG, CONFIG, OWNER, TRADER_ACCOUNT,
            PERP_ASSET_MAP, TRADER_INDEX, ACTIVE_BUFFER, ORDERBOOK, SPLINES, LIMIT_PACKET),
        CLIENT.placeLimitOrder(OWNER, TRADER_ACCOUNT, PERP_ASSET_MAP, ORDERBOOK, SPLINES, LIMIT_PACKET));
    assertIx(
        EternalProgram.placeMultiLimitOrder(INVOKED, ETERNAL, LOG, CONFIG, OWNER, TRADER_ACCOUNT,
            PERP_ASSET_MAP, TRADER_INDEX, ACTIVE_BUFFER, ORDERBOOK, SPLINES, MULTI_PACKET),
        CLIENT.placeMultiLimitOrder(OWNER, TRADER_ACCOUNT, PERP_ASSET_MAP, ORDERBOOK, SPLINES, MULTI_PACKET));
    assertIx(
        EternalProgram.cancelOrdersById(INVOKED, ETERNAL, LOG, CONFIG, OWNER, TRADER_ACCOUNT,
            PERP_ASSET_MAP, TRADER_INDEX, ACTIVE_BUFFER, ORDERBOOK, SPLINES, ORDER_IDS),
        CLIENT.cancelOrdersById(OWNER, TRADER_ACCOUNT, PERP_ASSET_MAP, ORDERBOOK, SPLINES, ORDER_IDS));
    assertIx(
        EternalProgram.cancelUpTo(INVOKED, ETERNAL, LOG, CONFIG, OWNER, TRADER_ACCOUNT,
            PERP_ASSET_MAP, TRADER_INDEX, ACTIVE_BUFFER, ORDERBOOK, SPLINES,
            new CancelUpToInstruction(Side.Bid, OptionalLong.of(3L), OptionalLong.empty())),
        CLIENT.cancelUpTo(OWNER, TRADER_ACCOUNT, PERP_ASSET_MAP, ORDERBOOK, SPLINES,
            new CancelUpToInstruction(Side.Bid, OptionalLong.of(3L), OptionalLong.empty())));
    assertIx(
        EternalProgram.cancelAll(INVOKED, ETERNAL, LOG, CONFIG, OWNER, TRADER_ACCOUNT,
            PERP_ASSET_MAP, TRADER_INDEX, ACTIVE_BUFFER, ORDERBOOK, SPLINES),
        CLIENT.cancelAll(OWNER, TRADER_ACCOUNT, PERP_ASSET_MAP, ORDERBOOK, SPLINES));
  }

  /// The conditional and stop-loss builders take the caller's keys in a
  /// different order than the client method declares them (trader account
  /// before wallet; payer leading), so these mirrors are the transposition
  /// pins for the riskiest bindings.
  @Test
  void conditionalOrderAndStopLossBindings() {
    assertIx(
        EternalProgram.createConditionalOrdersAccount(INVOKED, ETERNAL, LOG, CONFIG, PAYER, OWNER,
            TRADER_ACCOUNT, CONDITIONAL_ORDERS, SYSTEM, new CreateConditionalOrdersAccountInstruction(4)),
        CLIENT.createConditionalOrdersAccount(PAYER, OWNER, TRADER_ACCOUNT, CONDITIONAL_ORDERS,
            new CreateConditionalOrdersAccountInstruction(4)));
    final var positionParams = new PlacePositionConditionalOrderInstruction(
        7L, GREATER, LESS, OptionalLong.of(5L), OptionalInt.empty());
    assertIx(
        EternalProgram.placePositionConditionalOrder(INVOKED, ETERNAL, LOG, CONFIG, PAYER,
            TRADER_ACCOUNT, PERP_ASSET_MAP, TRADER_INDEX, ACTIVE_BUFFER, ORDERBOOK, SPLINES,
            OWNER, CONDITIONAL_ORDERS, SYSTEM, positionParams),
        CLIENT.placePositionConditionalOrder(PAYER, OWNER, TRADER_ACCOUNT, PERP_ASSET_MAP,
            ORDERBOOK, SPLINES, CONDITIONAL_ORDERS, positionParams));
    final var attachedParams = new PlaceAttachedConditionalOrderInstruction(
        new FIFOOrderId(new Ticks(100L), 7L), 7L, GREATER, LESS);
    assertIx(
        EternalProgram.placeAttachedConditionalOrder(INVOKED, ETERNAL, LOG, CONFIG,
            TRADER_ACCOUNT, OWNER, ORDERBOOK, CONDITIONAL_ORDERS, PAYER,
            TRADER_INDEX, ACTIVE_BUFFER, SYSTEM, attachedParams),
        CLIENT.placeAttachedConditionalOrder(PAYER, OWNER, TRADER_ACCOUNT, ORDERBOOK,
            CONDITIONAL_ORDERS, attachedParams));
    final var withConditionals = new PlaceLimitOrderWithConditionalsInstruction(
        LIMIT_PACKET, 42L, GREATER, LESS);
    assertIx(
        EternalProgram.placeLimitOrderWithConditionals(INVOKED, ETERNAL, LOG, CONFIG, OWNER,
            TRADER_ACCOUNT, PERP_ASSET_MAP, TRADER_INDEX, ACTIVE_BUFFER, ORDERBOOK, SPLINES,
            PAYER, CONDITIONAL_ORDERS, SYSTEM, withConditionals),
        CLIENT.placeLimitOrderWithConditionals(PAYER, OWNER, TRADER_ACCOUNT, PERP_ASSET_MAP,
            ORDERBOOK, SPLINES, CONDITIONAL_ORDERS, withConditionals));
    assertIx(
        EternalProgram.cancelConditionalOrder(INVOKED, ETERNAL, LOG, CONFIG,
            TRADER_ACCOUNT, OWNER, ORDERBOOK, CONDITIONAL_ORDERS,
            new CancelConditionalOrderInstruction(2, true, false)),
        CLIENT.cancelConditionalOrder(OWNER, TRADER_ACCOUNT, ORDERBOOK, CONDITIONAL_ORDERS,
            new CancelConditionalOrderInstruction(2, true, false)));
    assertIx(
        EternalProgram.cancelAllPlusConditional(INVOKED, ETERNAL, LOG, CONFIG, OWNER, TRADER_ACCOUNT,
            PERP_ASSET_MAP, TRADER_INDEX, ACTIVE_BUFFER, ORDERBOOK, SPLINES, CONDITIONAL_ORDERS),
        CLIENT.cancelAllPlusConditional(OWNER, TRADER_ACCOUNT, PERP_ASSET_MAP, ORDERBOOK, SPLINES,
            CONDITIONAL_ORDERS));
    final var stopLoss = new PlaceStopLossInstruction(100L, 99L, 5L, Side.Ask,
        Direction.LessThan, StopLossOrderKind.IOC);
    assertIx(
        EternalProgram.placeStopLoss(INVOKED, ETERNAL, LOG, CONFIG, PAYER, TRADER_ACCOUNT,
            PERP_ASSET_MAP, TRADER_INDEX, ACTIVE_BUFFER, ORDERBOOK, SPLINES, OWNER, STOP_LOSS,
            SYSTEM, stopLoss),
        CLIENT.placeStopLoss(PAYER, OWNER, TRADER_ACCOUNT, PERP_ASSET_MAP, ORDERBOOK, SPLINES,
            STOP_LOSS, stopLoss));
    assertIx(
        EternalProgram.cancelStopLoss(INVOKED, ETERNAL, LOG, CONFIG, FUNDER, TRADER_ACCOUNT,
            OWNER, STOP_LOSS, SYSTEM, new CancelStopLossInstruction(Direction.GreaterThan)),
        CLIENT.cancelStopLoss(FUNDER, OWNER, TRADER_ACCOUNT, STOP_LOSS,
            new CancelStopLossInstruction(Direction.GreaterThan)));
  }

  @Test
  void escrowBindings() {
    assertIx(
        EternalProgram.createEscrowAccount(INVOKED, SOLANA_ACCOUNTS, ETERNAL, LOG, CONFIG,
            PAYER, OWNER, TRADER_ESCROW, new CreateEscrowAccountParams(8L)),
        CLIENT.createEscrowAccount(PAYER, OWNER, TRADER_ESCROW, new CreateEscrowAccountParams(8L)));
    final var createRequest = new CreateEscrowRequestParams(ESCROW_META, OptionalLong.of(42L),
        new EscrowAction[]{CASH});
    assertIx(
        EternalProgram.createEscrowRequest(INVOKED, ETERNAL, LOG, CONFIG, OWNER, TRADER_ACCOUNT,
            PERMISSION, RECEIVER_WALLET, RECEIVER_TRADER, RECEIVER_ESCROW, PERP_ASSET_MAP,
            TRADER_INDEX, ACTIVE_BUFFER, createRequest),
        CLIENT.createEscrowRequest(OWNER, TRADER_ACCOUNT, PERMISSION, RECEIVER_WALLET,
            RECEIVER_TRADER, RECEIVER_ESCROW, PERP_ASSET_MAP, createRequest));
    assertIx(
        EternalProgram.acceptEscrowRequest(INVOKED, ETERNAL, LOG, CONFIG, OWNER, TRADER_ACCOUNT,
            RECEIVER_WALLET, RECEIVER_TRADER, RECEIVER_ESCROW, PERP_ASSET_MAP,
            TRADER_INDEX, ACTIVE_BUFFER),
        CLIENT.acceptEscrowRequest(OWNER, TRADER_ACCOUNT, RECEIVER_WALLET, RECEIVER_TRADER,
            RECEIVER_ESCROW, PERP_ASSET_MAP));
    assertIx(
        EternalProgram.cancelEscrowRequest(INVOKED, ETERNAL, LOG, CONFIG, OWNER, RECEIVER_WALLET,
            RECEIVER_ESCROW, new CancelEscrowRequestParams(7L)),
        CLIENT.cancelEscrowRequest(OWNER, RECEIVER_WALLET, RECEIVER_ESCROW,
            new CancelEscrowRequestParams(7L)));
    final var upsert = new UpsertEscrowRequestParams(ESCROW_META, CASH);
    assertIx(
        EternalProgram.upsertEscrowRequest(INVOKED, ETERNAL, LOG, CONFIG, OWNER, TRADER_ACCOUNT,
            PERMISSION, RECEIVER_WALLET, RECEIVER_TRADER, RECEIVER_ESCROW, PERP_ASSET_MAP,
            TRADER_INDEX, ACTIVE_BUFFER, upsert),
        CLIENT.upsertEscrowRequest(OWNER, TRADER_ACCOUNT, PERMISSION, RECEIVER_WALLET,
            RECEIVER_TRADER, RECEIVER_ESCROW, PERP_ASSET_MAP, upsert));
  }
}

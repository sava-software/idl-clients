package software.sava.idl.clients.phoenix.perpetuals.gen;

import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.CancelStopLossInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.CancelUpToInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.ClearExpiredOrdersParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.ClosePositionsParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.DepositFundsInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.ExecuteStopLossInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.ForceCancelRiskIncreasingParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.LiquidationParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.LiquidationTransferParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.LogHeader;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.MultipleOrderPacket;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.OrderIds;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.OrderPacket;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.PlaceStopLossInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.RegisterTraderParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.RevokePermissionParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.SetPermissionInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.TraderCapabilityUpdate;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.TransferCollateralChildToParentInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.TransferCollateralInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.UncrossCrankParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.UpdateOraclePricesParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.UpdateOraclePricesWithOrderingParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.UpdateSplineParametersParamsWithOrdering;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.UpdateSplinePriceParamsWithOrdering;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.WithdrawFundsInstruction;

import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class EternalProgram {

  public static final Discriminator PLACE_MARKET_ORDER_DISCRIMINATOR = toDiscriminator(90, 118, 192, 252, 192, 99, 39, 145);

  /// Places an immediate-or-cancel order that consumes resting liquidity on the specified market.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; enforces the market is registered and cross trading is enabled.
  /// @param traderWalletKey Trader authority placing the market order.
  /// @param traderAccountKey Trader state PDA ("trader" account) holding balances and open positions.
  /// @param perpAssetMapKey Perp asset map governing risk limits and funding parameters for the market.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static List<AccountMeta> placeMarketOrderKeys(final PublicKey phoenixProgramKey,
                                                       final PublicKey phoenixLogAuthorityKey,
                                                       final PublicKey globalConfigurationKey,
                                                       final PublicKey traderWalletKey,
                                                       final PublicKey traderAccountKey,
                                                       final PublicKey perpAssetMapKey,
                                                       final PublicKey globalTraderIndexKey,
                                                       final PublicKey activeTraderBufferKey,
                                                       final PublicKey orderbookKey,
                                                       final PublicKey splinesKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createReadOnlySigner(traderWalletKey),
      createWrite(traderAccountKey),
      createWrite(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(orderbookKey),
      createWrite(splinesKey)
    );
  }

  /// Places an immediate-or-cancel order that consumes resting liquidity on the specified market.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; enforces the market is registered and cross trading is enabled.
  /// @param traderWalletKey Trader authority placing the market order.
  /// @param traderAccountKey Trader state PDA ("trader" account) holding balances and open positions.
  /// @param perpAssetMapKey Perp asset map governing risk limits and funding parameters for the market.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static Instruction placeMarketOrder(final AccountMeta invokedEternalProgramMeta,
                                             final PublicKey phoenixProgramKey,
                                             final PublicKey phoenixLogAuthorityKey,
                                             final PublicKey globalConfigurationKey,
                                             final PublicKey traderWalletKey,
                                             final PublicKey traderAccountKey,
                                             final PublicKey perpAssetMapKey,
                                             final PublicKey globalTraderIndexKey,
                                             final PublicKey activeTraderBufferKey,
                                             final PublicKey orderbookKey,
                                             final PublicKey splinesKey,
                                             final OrderPacket orderPacket) {
    final var keys = placeMarketOrderKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderWalletKey,
      traderAccountKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      orderbookKey,
      splinesKey
    );
    return placeMarketOrder(invokedEternalProgramMeta, keys, orderPacket);
  }

  /// Places an immediate-or-cancel order that consumes resting liquidity on the specified market.
  ///
  public static Instruction placeMarketOrder(final AccountMeta invokedEternalProgramMeta,
                                             final List<AccountMeta> keys,
                                             final OrderPacket orderPacket) {
    final byte[] _data = new byte[8 + orderPacket.l()];
    int i = PLACE_MARKET_ORDER_DISCRIMINATOR.write(_data, 0);
    orderPacket.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record PlaceMarketOrderIxData(Discriminator discriminator, OrderPacket orderPacket) implements SerDe {  

    public static PlaceMarketOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ORDER_PACKET_OFFSET = 8;

    public static PlaceMarketOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var orderPacket = OrderPacket.read(_data, i);
      return new PlaceMarketOrderIxData(discriminator, orderPacket);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += orderPacket.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + orderPacket.l();
    }
  }

  public static final Discriminator PLACE_LIMIT_ORDER_DISCRIMINATOR = toDiscriminator(108, 176, 33, 186, 146, 229, 1, 197);

  /// Places a limit order that may rest on the orderbook or take liquidity depending on matching conditions.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; enforces the market is registered and cross trading is enabled.
  /// @param traderWalletKey Trader authority placing the limit order.
  /// @param traderAccountKey Trader state PDA ("trader" account) holding balances and open positions.
  /// @param perpAssetMapKey Perp asset map governing risk limits and funding parameters for the market.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static List<AccountMeta> placeLimitOrderKeys(final PublicKey phoenixProgramKey,
                                                      final PublicKey phoenixLogAuthorityKey,
                                                      final PublicKey globalConfigurationKey,
                                                      final PublicKey traderWalletKey,
                                                      final PublicKey traderAccountKey,
                                                      final PublicKey perpAssetMapKey,
                                                      final PublicKey globalTraderIndexKey,
                                                      final PublicKey activeTraderBufferKey,
                                                      final PublicKey orderbookKey,
                                                      final PublicKey splinesKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createReadOnlySigner(traderWalletKey),
      createWrite(traderAccountKey),
      createWrite(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(orderbookKey),
      createWrite(splinesKey)
    );
  }

  /// Places a limit order that may rest on the orderbook or take liquidity depending on matching conditions.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; enforces the market is registered and cross trading is enabled.
  /// @param traderWalletKey Trader authority placing the limit order.
  /// @param traderAccountKey Trader state PDA ("trader" account) holding balances and open positions.
  /// @param perpAssetMapKey Perp asset map governing risk limits and funding parameters for the market.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static Instruction placeLimitOrder(final AccountMeta invokedEternalProgramMeta,
                                            final PublicKey phoenixProgramKey,
                                            final PublicKey phoenixLogAuthorityKey,
                                            final PublicKey globalConfigurationKey,
                                            final PublicKey traderWalletKey,
                                            final PublicKey traderAccountKey,
                                            final PublicKey perpAssetMapKey,
                                            final PublicKey globalTraderIndexKey,
                                            final PublicKey activeTraderBufferKey,
                                            final PublicKey orderbookKey,
                                            final PublicKey splinesKey,
                                            final OrderPacket orderPacket) {
    final var keys = placeLimitOrderKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderWalletKey,
      traderAccountKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      orderbookKey,
      splinesKey
    );
    return placeLimitOrder(invokedEternalProgramMeta, keys, orderPacket);
  }

  /// Places a limit order that may rest on the orderbook or take liquidity depending on matching conditions.
  ///
  public static Instruction placeLimitOrder(final AccountMeta invokedEternalProgramMeta,
                                            final List<AccountMeta> keys,
                                            final OrderPacket orderPacket) {
    final byte[] _data = new byte[8 + orderPacket.l()];
    int i = PLACE_LIMIT_ORDER_DISCRIMINATOR.write(_data, 0);
    orderPacket.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record PlaceLimitOrderIxData(Discriminator discriminator, OrderPacket orderPacket) implements SerDe {  

    public static PlaceLimitOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ORDER_PACKET_OFFSET = 8;

    public static PlaceLimitOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var orderPacket = OrderPacket.read(_data, i);
      return new PlaceLimitOrderIxData(discriminator, orderPacket);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += orderPacket.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + orderPacket.l();
    }
  }

  public static final Discriminator PLACE_MULTI_LIMIT_ORDER_DISCRIMINATOR = toDiscriminator(236, 208, 221, 172, 141, 226, 129, 84);

  /// Places multiple post-only limit orders in a single transaction, allowing batch order placement for market making.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; enforces the market is registered and cross trading is enabled.
  /// @param traderWalletKey Trader authority placing the batch of limit orders.
  /// @param traderAccountKey Trader state PDA holding balances and open positions.
  /// @param perpAssetMapKey Perp asset map governing risk limits and funding parameters for the market.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static List<AccountMeta> placeMultiLimitOrderKeys(final PublicKey phoenixProgramKey,
                                                           final PublicKey phoenixLogAuthorityKey,
                                                           final PublicKey globalConfigurationKey,
                                                           final PublicKey traderWalletKey,
                                                           final PublicKey traderAccountKey,
                                                           final PublicKey perpAssetMapKey,
                                                           final PublicKey globalTraderIndexKey,
                                                           final PublicKey activeTraderBufferKey,
                                                           final PublicKey orderbookKey,
                                                           final PublicKey splinesKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createReadOnlySigner(traderWalletKey),
      createWrite(traderAccountKey),
      createWrite(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(orderbookKey),
      createWrite(splinesKey)
    );
  }

  /// Places multiple post-only limit orders in a single transaction, allowing batch order placement for market making.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; enforces the market is registered and cross trading is enabled.
  /// @param traderWalletKey Trader authority placing the batch of limit orders.
  /// @param traderAccountKey Trader state PDA holding balances and open positions.
  /// @param perpAssetMapKey Perp asset map governing risk limits and funding parameters for the market.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static Instruction placeMultiLimitOrder(final AccountMeta invokedEternalProgramMeta,
                                                 final PublicKey phoenixProgramKey,
                                                 final PublicKey phoenixLogAuthorityKey,
                                                 final PublicKey globalConfigurationKey,
                                                 final PublicKey traderWalletKey,
                                                 final PublicKey traderAccountKey,
                                                 final PublicKey perpAssetMapKey,
                                                 final PublicKey globalTraderIndexKey,
                                                 final PublicKey activeTraderBufferKey,
                                                 final PublicKey orderbookKey,
                                                 final PublicKey splinesKey,
                                                 final MultipleOrderPacket orderPacket) {
    final var keys = placeMultiLimitOrderKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderWalletKey,
      traderAccountKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      orderbookKey,
      splinesKey
    );
    return placeMultiLimitOrder(invokedEternalProgramMeta, keys, orderPacket);
  }

  /// Places multiple post-only limit orders in a single transaction, allowing batch order placement for market making.
  ///
  public static Instruction placeMultiLimitOrder(final AccountMeta invokedEternalProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final MultipleOrderPacket orderPacket) {
    final byte[] _data = new byte[8 + orderPacket.l()];
    int i = PLACE_MULTI_LIMIT_ORDER_DISCRIMINATOR.write(_data, 0);
    orderPacket.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record PlaceMultiLimitOrderIxData(Discriminator discriminator, MultipleOrderPacket orderPacket) implements SerDe {  

    public static PlaceMultiLimitOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ORDER_PACKET_OFFSET = 8;

    public static PlaceMultiLimitOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var orderPacket = MultipleOrderPacket.read(_data, i);
      return new PlaceMultiLimitOrderIxData(discriminator, orderPacket);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += orderPacket.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + orderPacket.l();
    }
  }

  public static final Discriminator CANCEL_ORDERS_BY_ID_DISCRIMINATOR = toDiscriminator(234, 204, 126, 94, 222, 22, 141, 24);

  /// Cancels a specific set of resting orders identified by their FIFO order ids.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; enforces the market is registered and cross trading is enabled.
  /// @param traderWalletKey Trader authority requesting the cancellation.
  /// @param traderAccountKey Trader state PDA ("trader" account) holding balances and open positions.
  /// @param perpAssetMapKey Perp asset map governing risk limits and funding parameters for the market.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account containing the resting orders to cancel.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static List<AccountMeta> cancelOrdersByIdKeys(final PublicKey phoenixProgramKey,
                                                       final PublicKey phoenixLogAuthorityKey,
                                                       final PublicKey globalConfigurationKey,
                                                       final PublicKey traderWalletKey,
                                                       final PublicKey traderAccountKey,
                                                       final PublicKey perpAssetMapKey,
                                                       final PublicKey globalTraderIndexKey,
                                                       final PublicKey activeTraderBufferKey,
                                                       final PublicKey orderbookKey,
                                                       final PublicKey splinesKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createReadOnlySigner(traderWalletKey),
      createWrite(traderAccountKey),
      createWrite(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(orderbookKey),
      createWrite(splinesKey)
    );
  }

  /// Cancels a specific set of resting orders identified by their FIFO order ids.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; enforces the market is registered and cross trading is enabled.
  /// @param traderWalletKey Trader authority requesting the cancellation.
  /// @param traderAccountKey Trader state PDA ("trader" account) holding balances and open positions.
  /// @param perpAssetMapKey Perp asset map governing risk limits and funding parameters for the market.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account containing the resting orders to cancel.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static Instruction cancelOrdersById(final AccountMeta invokedEternalProgramMeta,
                                             final PublicKey phoenixProgramKey,
                                             final PublicKey phoenixLogAuthorityKey,
                                             final PublicKey globalConfigurationKey,
                                             final PublicKey traderWalletKey,
                                             final PublicKey traderAccountKey,
                                             final PublicKey perpAssetMapKey,
                                             final PublicKey globalTraderIndexKey,
                                             final PublicKey activeTraderBufferKey,
                                             final PublicKey orderbookKey,
                                             final PublicKey splinesKey,
                                             final OrderIds orderIds) {
    final var keys = cancelOrdersByIdKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderWalletKey,
      traderAccountKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      orderbookKey,
      splinesKey
    );
    return cancelOrdersById(invokedEternalProgramMeta, keys, orderIds);
  }

  /// Cancels a specific set of resting orders identified by their FIFO order ids.
  ///
  public static Instruction cancelOrdersById(final AccountMeta invokedEternalProgramMeta,
                                             final List<AccountMeta> keys,
                                             final OrderIds orderIds) {
    final byte[] _data = new byte[8 + orderIds.l()];
    int i = CANCEL_ORDERS_BY_ID_DISCRIMINATOR.write(_data, 0);
    orderIds.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record CancelOrdersByIdIxData(Discriminator discriminator, OrderIds orderIds) implements SerDe {  

    public static CancelOrdersByIdIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ORDER_IDS_OFFSET = 8;

    public static CancelOrdersByIdIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var orderIds = OrderIds.read(_data, i);
      return new CancelOrdersByIdIxData(discriminator, orderIds);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += orderIds.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + orderIds.l();
    }
  }

  public static final Discriminator CANCEL_UP_TO_DISCRIMINATOR = toDiscriminator(26, 209, 244, 253, 59, 175, 227, 54);

  /// Cancels resting orders on the specified side up to an optional order or price threshold.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; enforces the market is registered and cross trading is enabled.
  /// @param traderWalletKey Trader authority requesting the cancellation.
  /// @param traderAccountKey Trader state PDA ("trader" account) holding balances and open positions.
  /// @param perpAssetMapKey Perp asset map governing risk limits and funding parameters for the market.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account containing the resting orders to cancel.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static List<AccountMeta> cancelUpToKeys(final PublicKey phoenixProgramKey,
                                                 final PublicKey phoenixLogAuthorityKey,
                                                 final PublicKey globalConfigurationKey,
                                                 final PublicKey traderWalletKey,
                                                 final PublicKey traderAccountKey,
                                                 final PublicKey perpAssetMapKey,
                                                 final PublicKey globalTraderIndexKey,
                                                 final PublicKey activeTraderBufferKey,
                                                 final PublicKey orderbookKey,
                                                 final PublicKey splinesKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createReadOnlySigner(traderWalletKey),
      createWrite(traderAccountKey),
      createWrite(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(orderbookKey),
      createWrite(splinesKey)
    );
  }

  /// Cancels resting orders on the specified side up to an optional order or price threshold.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; enforces the market is registered and cross trading is enabled.
  /// @param traderWalletKey Trader authority requesting the cancellation.
  /// @param traderAccountKey Trader state PDA ("trader" account) holding balances and open positions.
  /// @param perpAssetMapKey Perp asset map governing risk limits and funding parameters for the market.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account containing the resting orders to cancel.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static Instruction cancelUpTo(final AccountMeta invokedEternalProgramMeta,
                                       final PublicKey phoenixProgramKey,
                                       final PublicKey phoenixLogAuthorityKey,
                                       final PublicKey globalConfigurationKey,
                                       final PublicKey traderWalletKey,
                                       final PublicKey traderAccountKey,
                                       final PublicKey perpAssetMapKey,
                                       final PublicKey globalTraderIndexKey,
                                       final PublicKey activeTraderBufferKey,
                                       final PublicKey orderbookKey,
                                       final PublicKey splinesKey,
                                       final CancelUpToInstruction params) {
    final var keys = cancelUpToKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderWalletKey,
      traderAccountKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      orderbookKey,
      splinesKey
    );
    return cancelUpTo(invokedEternalProgramMeta, keys, params);
  }

  /// Cancels resting orders on the specified side up to an optional order or price threshold.
  ///
  public static Instruction cancelUpTo(final AccountMeta invokedEternalProgramMeta,
                                       final List<AccountMeta> keys,
                                       final CancelUpToInstruction params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = CANCEL_UP_TO_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record CancelUpToIxData(Discriminator discriminator, CancelUpToInstruction params) implements SerDe {  

    public static CancelUpToIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static CancelUpToIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = CancelUpToInstruction.read(_data, i);
      return new CancelUpToIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator CANCEL_ALL_DISCRIMINATOR = toDiscriminator(98, 191, 75, 220, 115, 40, 71, 237);

  /// Cancels every resting order owned by the connected trader on the specified market.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; enforces the market is registered and cross trading is enabled.
  /// @param traderWalletKey Trader authority requesting the cancellation.
  /// @param traderAccountKey Trader state PDA ("trader" account) holding balances and open positions.
  /// @param perpAssetMapKey Perp asset map governing risk limits and funding parameters for the market.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account containing the resting orders to cancel.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static List<AccountMeta> cancelAllKeys(final PublicKey phoenixProgramKey,
                                                final PublicKey phoenixLogAuthorityKey,
                                                final PublicKey globalConfigurationKey,
                                                final PublicKey traderWalletKey,
                                                final PublicKey traderAccountKey,
                                                final PublicKey perpAssetMapKey,
                                                final PublicKey globalTraderIndexKey,
                                                final PublicKey activeTraderBufferKey,
                                                final PublicKey orderbookKey,
                                                final PublicKey splinesKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createReadOnlySigner(traderWalletKey),
      createWrite(traderAccountKey),
      createWrite(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(orderbookKey),
      createWrite(splinesKey)
    );
  }

  /// Cancels every resting order owned by the connected trader on the specified market.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; enforces the market is registered and cross trading is enabled.
  /// @param traderWalletKey Trader authority requesting the cancellation.
  /// @param traderAccountKey Trader state PDA ("trader" account) holding balances and open positions.
  /// @param perpAssetMapKey Perp asset map governing risk limits and funding parameters for the market.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account containing the resting orders to cancel.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static Instruction cancelAll(final AccountMeta invokedEternalProgramMeta,
                                      final PublicKey phoenixProgramKey,
                                      final PublicKey phoenixLogAuthorityKey,
                                      final PublicKey globalConfigurationKey,
                                      final PublicKey traderWalletKey,
                                      final PublicKey traderAccountKey,
                                      final PublicKey perpAssetMapKey,
                                      final PublicKey globalTraderIndexKey,
                                      final PublicKey activeTraderBufferKey,
                                      final PublicKey orderbookKey,
                                      final PublicKey splinesKey) {
    final var keys = cancelAllKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderWalletKey,
      traderAccountKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      orderbookKey,
      splinesKey
    );
    return cancelAll(invokedEternalProgramMeta, keys);
  }

  /// Cancels every resting order owned by the connected trader on the specified market.
  ///
  public static Instruction cancelAll(final AccountMeta invokedEternalProgramMeta,
                                      final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedEternalProgramMeta, keys, CANCEL_ALL_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_ORACLE_PRICES_DISCRIMINATOR = toDiscriminator(31, 180, 61, 161, 248, 128, 43, 94);

  /// Updates oracle-provided spot and perp prices for one or more perpetual markets.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; provides oracle authority and quote decimals for conversions.
  /// @param authorityKey Wallet authorised to submit oracle price updates (root oracle or delegated user).
  /// @param permissionAccountKey Permission account recording oracle capabilities and remaining allowances.
  /// @param perpAssetMapKey Perpetual asset map storing mark price state and oracle metadata.
  public static List<AccountMeta> updateOraclePricesKeys(final PublicKey phoenixProgramKey,
                                                         final PublicKey phoenixLogAuthorityKey,
                                                         final PublicKey globalConfigurationKey,
                                                         final PublicKey authorityKey,
                                                         final PublicKey permissionAccountKey,
                                                         final PublicKey perpAssetMapKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createRead(globalConfigurationKey),
      createReadOnlySigner(authorityKey),
      createWrite(permissionAccountKey),
      createWrite(perpAssetMapKey)
    );
  }

  /// Updates oracle-provided spot and perp prices for one or more perpetual markets.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; provides oracle authority and quote decimals for conversions.
  /// @param authorityKey Wallet authorised to submit oracle price updates (root oracle or delegated user).
  /// @param permissionAccountKey Permission account recording oracle capabilities and remaining allowances.
  /// @param perpAssetMapKey Perpetual asset map storing mark price state and oracle metadata.
  public static Instruction updateOraclePrices(final AccountMeta invokedEternalProgramMeta,
                                               final PublicKey phoenixProgramKey,
                                               final PublicKey phoenixLogAuthorityKey,
                                               final PublicKey globalConfigurationKey,
                                               final PublicKey authorityKey,
                                               final PublicKey permissionAccountKey,
                                               final PublicKey perpAssetMapKey,
                                               final UpdateOraclePricesParams params) {
    final var keys = updateOraclePricesKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      authorityKey,
      permissionAccountKey,
      perpAssetMapKey
    );
    return updateOraclePrices(invokedEternalProgramMeta, keys, params);
  }

  /// Updates oracle-provided spot and perp prices for one or more perpetual markets.
  ///
  public static Instruction updateOraclePrices(final AccountMeta invokedEternalProgramMeta,
                                               final List<AccountMeta> keys,
                                               final UpdateOraclePricesParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = UPDATE_ORACLE_PRICES_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record UpdateOraclePricesIxData(Discriminator discriminator, UpdateOraclePricesParams params) implements SerDe {  

    public static UpdateOraclePricesIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static UpdateOraclePricesIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = UpdateOraclePricesParams.read(_data, i);
      return new UpdateOraclePricesIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator UPDATE_ORACLE_PRICES_WITH_ORDERING_DISCRIMINATOR = toDiscriminator(164, 0, 14, 250, 58, 198, 25, 24);

  /// Updates oracle prices with an explicit timestamp for deterministic ordering and optional timestamp reset.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; provides oracle authority and quote decimals for conversions.
  /// @param authorityKey Wallet authorised to submit oracle price updates (root oracle or delegated user).
  /// @param permissionAccountKey Permission account recording oracle capabilities and remaining allowances.
  /// @param perpAssetMapKey Perpetual asset map storing mark price state and oracle metadata.
  public static List<AccountMeta> updateOraclePricesWithOrderingKeys(final PublicKey phoenixProgramKey,
                                                                     final PublicKey phoenixLogAuthorityKey,
                                                                     final PublicKey globalConfigurationKey,
                                                                     final PublicKey authorityKey,
                                                                     final PublicKey permissionAccountKey,
                                                                     final PublicKey perpAssetMapKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createRead(globalConfigurationKey),
      createReadOnlySigner(authorityKey),
      createWrite(permissionAccountKey),
      createWrite(perpAssetMapKey)
    );
  }

  /// Updates oracle prices with an explicit timestamp for deterministic ordering and optional timestamp reset.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; provides oracle authority and quote decimals for conversions.
  /// @param authorityKey Wallet authorised to submit oracle price updates (root oracle or delegated user).
  /// @param permissionAccountKey Permission account recording oracle capabilities and remaining allowances.
  /// @param perpAssetMapKey Perpetual asset map storing mark price state and oracle metadata.
  public static Instruction updateOraclePricesWithOrdering(final AccountMeta invokedEternalProgramMeta,
                                                           final PublicKey phoenixProgramKey,
                                                           final PublicKey phoenixLogAuthorityKey,
                                                           final PublicKey globalConfigurationKey,
                                                           final PublicKey authorityKey,
                                                           final PublicKey permissionAccountKey,
                                                           final PublicKey perpAssetMapKey,
                                                           final UpdateOraclePricesWithOrderingParams params) {
    final var keys = updateOraclePricesWithOrderingKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      authorityKey,
      permissionAccountKey,
      perpAssetMapKey
    );
    return updateOraclePricesWithOrdering(invokedEternalProgramMeta, keys, params);
  }

  /// Updates oracle prices with an explicit timestamp for deterministic ordering and optional timestamp reset.
  ///
  public static Instruction updateOraclePricesWithOrdering(final AccountMeta invokedEternalProgramMeta,
                                                           final List<AccountMeta> keys,
                                                           final UpdateOraclePricesWithOrderingParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = UPDATE_ORACLE_PRICES_WITH_ORDERING_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record UpdateOraclePricesWithOrderingIxData(Discriminator discriminator, UpdateOraclePricesWithOrderingParams params) implements SerDe {  

    public static UpdateOraclePricesWithOrderingIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static UpdateOraclePricesWithOrderingIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = UpdateOraclePricesWithOrderingParams.read(_data, i);
      return new UpdateOraclePricesWithOrderingIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator UPDATE_SPLINE_PRICE_DISCRIMINATOR = toDiscriminator(45, 155, 200, 52, 37, 214, 187, 140);

  /// Updates the mid-price for a spline market-making program on the specified market.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param signerAccountKey Authority of the spline trader account authorised to update the spline price.
  /// @param traderAccountKey Spline trader account whose price is being updated.
  /// @param splineAccountKey Spline collection PDA containing the spline configuration and pricing data.
  public static List<AccountMeta> updateSplinePriceKeys(final PublicKey phoenixProgramKey,
                                                        final PublicKey phoenixLogAuthorityKey,
                                                        final PublicKey signerAccountKey,
                                                        final PublicKey traderAccountKey,
                                                        final PublicKey splineAccountKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createReadOnlySigner(signerAccountKey),
      createRead(traderAccountKey),
      createWrite(splineAccountKey)
    );
  }

  /// Updates the mid-price for a spline market-making program on the specified market.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param signerAccountKey Authority of the spline trader account authorised to update the spline price.
  /// @param traderAccountKey Spline trader account whose price is being updated.
  /// @param splineAccountKey Spline collection PDA containing the spline configuration and pricing data.
  public static Instruction updateSplinePrice(final AccountMeta invokedEternalProgramMeta,
                                              final PublicKey phoenixProgramKey,
                                              final PublicKey phoenixLogAuthorityKey,
                                              final PublicKey signerAccountKey,
                                              final PublicKey traderAccountKey,
                                              final PublicKey splineAccountKey,
                                              final UpdateSplinePriceParamsWithOrdering params) {
    final var keys = updateSplinePriceKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      signerAccountKey,
      traderAccountKey,
      splineAccountKey
    );
    return updateSplinePrice(invokedEternalProgramMeta, keys, params);
  }

  /// Updates the mid-price for a spline market-making program on the specified market.
  ///
  public static Instruction updateSplinePrice(final AccountMeta invokedEternalProgramMeta,
                                              final List<AccountMeta> keys,
                                              final UpdateSplinePriceParamsWithOrdering params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = UPDATE_SPLINE_PRICE_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record UpdateSplinePriceIxData(Discriminator discriminator, UpdateSplinePriceParamsWithOrdering params) implements SerDe {  

    public static UpdateSplinePriceIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static UpdateSplinePriceIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = UpdateSplinePriceParamsWithOrdering.read(_data, i);
      return new UpdateSplinePriceIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator DEPOSIT_FUNDS_DISCRIMINATOR = toDiscriminator(202, 39, 52, 211, 53, 20, 250, 88);

  /// Deposits collateral tokens from a trader's token account into the global vault, crediting the trader's account.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that tracks vault and system parameters.
  /// @param traderWalletKey Authority of the trader account making the deposit.
  /// @param traderTokenAccountKey Trader's token account from which funds are transferred.
  /// @param traderAccountKey Trader state PDA that receives the collateral credit.
  /// @param globalVaultKey Global vault token account that receives the deposited tokens.
  /// @param tokenProgramKey SPL Token program for executing the transfer.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  public static List<AccountMeta> depositFundsKeys(final PublicKey phoenixProgramKey,
                                                   final PublicKey phoenixLogAuthorityKey,
                                                   final PublicKey globalConfigurationKey,
                                                   final PublicKey traderWalletKey,
                                                   final PublicKey traderTokenAccountKey,
                                                   final PublicKey traderAccountKey,
                                                   final PublicKey globalVaultKey,
                                                   final PublicKey tokenProgramKey,
                                                   final PublicKey globalTraderIndexKey,
                                                   final PublicKey activeTraderBufferKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createReadOnlySigner(traderWalletKey),
      createWrite(traderTokenAccountKey),
      createWrite(traderAccountKey),
      createWrite(globalVaultKey),
      createRead(tokenProgramKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey)
    );
  }

  /// Deposits collateral tokens from a trader's token account into the global vault, crediting the trader's account.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that tracks vault and system parameters.
  /// @param traderWalletKey Authority of the trader account making the deposit.
  /// @param traderTokenAccountKey Trader's token account from which funds are transferred.
  /// @param traderAccountKey Trader state PDA that receives the collateral credit.
  /// @param globalVaultKey Global vault token account that receives the deposited tokens.
  /// @param tokenProgramKey SPL Token program for executing the transfer.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  public static Instruction depositFunds(final AccountMeta invokedEternalProgramMeta,
                                         final PublicKey phoenixProgramKey,
                                         final PublicKey phoenixLogAuthorityKey,
                                         final PublicKey globalConfigurationKey,
                                         final PublicKey traderWalletKey,
                                         final PublicKey traderTokenAccountKey,
                                         final PublicKey traderAccountKey,
                                         final PublicKey globalVaultKey,
                                         final PublicKey tokenProgramKey,
                                         final PublicKey globalTraderIndexKey,
                                         final PublicKey activeTraderBufferKey,
                                         final DepositFundsInstruction params) {
    final var keys = depositFundsKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderWalletKey,
      traderTokenAccountKey,
      traderAccountKey,
      globalVaultKey,
      tokenProgramKey,
      globalTraderIndexKey,
      activeTraderBufferKey
    );
    return depositFunds(invokedEternalProgramMeta, keys, params);
  }

  /// Deposits collateral tokens from a trader's token account into the global vault, crediting the trader's account.
  ///
  public static Instruction depositFunds(final AccountMeta invokedEternalProgramMeta,
                                         final List<AccountMeta> keys,
                                         final DepositFundsInstruction params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = DEPOSIT_FUNDS_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record DepositFundsIxData(Discriminator discriminator, DepositFundsInstruction params) implements SerDe {  

    public static DepositFundsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int PARAMS_OFFSET = 8;

    public static DepositFundsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = DepositFundsInstruction.read(_data, i);
      return new DepositFundsIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REGISTER_TRADER_DISCRIMINATOR = toDiscriminator(75, 243, 224, 167, 1, 5, 51, 32);

  /// Registers a new trader account with the specified configuration and position capacity.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that validates the trader registration.
  /// @param payerKey Account paying for the trader account creation and rent exemption.
  /// @param traderWalletKey Authority that will control the trader account; used to derive the trader PDA.
  /// @param traderAccountKey Trader state PDA to be created; derived from "trader", trader_wallet, trader_pda_index, trader_subaccount_index.
  /// @param systemProgramKey System program for creating the trader account.
  public static List<AccountMeta> registerTraderKeys(final PublicKey phoenixProgramKey,
                                                     final PublicKey phoenixLogAuthorityKey,
                                                     final PublicKey globalConfigurationKey,
                                                     final PublicKey payerKey,
                                                     final PublicKey traderWalletKey,
                                                     final PublicKey traderAccountKey,
                                                     final PublicKey systemProgramKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createRead(globalConfigurationKey),
      createWritableSigner(payerKey),
      createRead(traderWalletKey),
      createWrite(traderAccountKey),
      createRead(systemProgramKey)
    );
  }

  /// Registers a new trader account with the specified configuration and position capacity.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that validates the trader registration.
  /// @param payerKey Account paying for the trader account creation and rent exemption.
  /// @param traderWalletKey Authority that will control the trader account; used to derive the trader PDA.
  /// @param traderAccountKey Trader state PDA to be created; derived from "trader", trader_wallet, trader_pda_index, trader_subaccount_index.
  /// @param systemProgramKey System program for creating the trader account.
  public static Instruction registerTrader(final AccountMeta invokedEternalProgramMeta,
                                           final PublicKey phoenixProgramKey,
                                           final PublicKey phoenixLogAuthorityKey,
                                           final PublicKey globalConfigurationKey,
                                           final PublicKey payerKey,
                                           final PublicKey traderWalletKey,
                                           final PublicKey traderAccountKey,
                                           final PublicKey systemProgramKey,
                                           final RegisterTraderParams params) {
    final var keys = registerTraderKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      payerKey,
      traderWalletKey,
      traderAccountKey,
      systemProgramKey
    );
    return registerTrader(invokedEternalProgramMeta, keys, params);
  }

  /// Registers a new trader account with the specified configuration and position capacity.
  ///
  public static Instruction registerTrader(final AccountMeta invokedEternalProgramMeta,
                                           final List<AccountMeta> keys,
                                           final RegisterTraderParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = REGISTER_TRADER_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record RegisterTraderIxData(Discriminator discriminator, RegisterTraderParams params) implements SerDe {  

    public static RegisterTraderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 18;

    public static final int PARAMS_OFFSET = 8;

    public static RegisterTraderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = RegisterTraderParams.read(_data, i);
      return new RegisterTraderIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator LIQUIDATE_VIA_MARKET_ORDER_DISCRIMINATOR = toDiscriminator(251, 241, 184, 108, 70, 70, 127, 198);

  /// Liquidates a trader's position by executing an immediate-or-cancel market order to reduce their exposure.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; enforces the market is registered and liquidation is enabled.
  /// @param liquidatorWalletKey Liquidator authority executing the liquidation.
  /// @param liquidatedTraderKey Trader account being liquidated (must be in liquidatable state).
  /// @param traderAccountKey Trader state PDA ("trader" account) holding balances and open positions.
  /// @param perpAssetMapKey Perp asset map governing risk limits and funding parameters for the market.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static List<AccountMeta> liquidateViaMarketOrderKeys(final PublicKey phoenixProgramKey,
                                                              final PublicKey phoenixLogAuthorityKey,
                                                              final PublicKey globalConfigurationKey,
                                                              final PublicKey liquidatorWalletKey,
                                                              final PublicKey liquidatedTraderKey,
                                                              final PublicKey traderAccountKey,
                                                              final PublicKey perpAssetMapKey,
                                                              final PublicKey globalTraderIndexKey,
                                                              final PublicKey activeTraderBufferKey,
                                                              final PublicKey orderbookKey,
                                                              final PublicKey splinesKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createReadOnlySigner(liquidatorWalletKey),
      createWrite(liquidatedTraderKey),
      createWrite(traderAccountKey),
      createWrite(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(orderbookKey),
      createWrite(splinesKey)
    );
  }

  /// Liquidates a trader's position by executing an immediate-or-cancel market order to reduce their exposure.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA; enforces the market is registered and liquidation is enabled.
  /// @param liquidatorWalletKey Liquidator authority executing the liquidation.
  /// @param liquidatedTraderKey Trader account being liquidated (must be in liquidatable state).
  /// @param traderAccountKey Trader state PDA ("trader" account) holding balances and open positions.
  /// @param perpAssetMapKey Perp asset map governing risk limits and funding parameters for the market.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static Instruction liquidateViaMarketOrder(final AccountMeta invokedEternalProgramMeta,
                                                    final PublicKey phoenixProgramKey,
                                                    final PublicKey phoenixLogAuthorityKey,
                                                    final PublicKey globalConfigurationKey,
                                                    final PublicKey liquidatorWalletKey,
                                                    final PublicKey liquidatedTraderKey,
                                                    final PublicKey traderAccountKey,
                                                    final PublicKey perpAssetMapKey,
                                                    final PublicKey globalTraderIndexKey,
                                                    final PublicKey activeTraderBufferKey,
                                                    final PublicKey orderbookKey,
                                                    final PublicKey splinesKey,
                                                    final LiquidationParams params) {
    final var keys = liquidateViaMarketOrderKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      liquidatorWalletKey,
      liquidatedTraderKey,
      traderAccountKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      orderbookKey,
      splinesKey
    );
    return liquidateViaMarketOrder(invokedEternalProgramMeta, keys, params);
  }

  /// Liquidates a trader's position by executing an immediate-or-cancel market order to reduce their exposure.
  ///
  public static Instruction liquidateViaMarketOrder(final AccountMeta invokedEternalProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final LiquidationParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = LIQUIDATE_VIA_MARKET_ORDER_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record LiquidateViaMarketOrderIxData(Discriminator discriminator, LiquidationParams params) implements SerDe {  

    public static LiquidateViaMarketOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 57;

    public static final int PARAMS_OFFSET = 8;

    public static LiquidateViaMarketOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = LiquidationParams.read(_data, i);
      return new LiquidateViaMarketOrderIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator LIQUIDATION_TRANSFER_DISCRIMINATOR = toDiscriminator(57, 218, 215, 68, 174, 23, 204, 127);

  /// Transfers positions directly between a liquidator and liquidatee once the liquidatee is flagged as unhealthy.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that owns the global trader index, active trader buffer, and collateral controls.
  /// @param perpAssetMapKey Perp asset map governing risk limits, pricing, and funding parameters for every listed market.
  /// @param liquidatorKey Authority of the liquidator who receives transferred positions.
  /// @param liquidatorAccountKey Liquidator trader account whose balances are credited as transfers are applied.
  /// @param liquidateeAccountKey Liquidatee trader account whose unhealthy positions are reduced.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  public static List<AccountMeta> liquidationTransferKeys(final PublicKey phoenixProgramKey,
                                                          final PublicKey phoenixLogAuthorityKey,
                                                          final PublicKey globalConfigurationKey,
                                                          final PublicKey perpAssetMapKey,
                                                          final PublicKey liquidatorKey,
                                                          final PublicKey liquidatorAccountKey,
                                                          final PublicKey liquidateeAccountKey,
                                                          final PublicKey globalTraderIndexKey,
                                                          final PublicKey activeTraderBufferKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createWrite(perpAssetMapKey),
      createReadOnlySigner(liquidatorKey),
      createWrite(liquidatorAccountKey),
      createWrite(liquidateeAccountKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey)
    );
  }

  /// Transfers positions directly between a liquidator and liquidatee once the liquidatee is flagged as unhealthy.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that owns the global trader index, active trader buffer, and collateral controls.
  /// @param perpAssetMapKey Perp asset map governing risk limits, pricing, and funding parameters for every listed market.
  /// @param liquidatorKey Authority of the liquidator who receives transferred positions.
  /// @param liquidatorAccountKey Liquidator trader account whose balances are credited as transfers are applied.
  /// @param liquidateeAccountKey Liquidatee trader account whose unhealthy positions are reduced.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  public static Instruction liquidationTransfer(final AccountMeta invokedEternalProgramMeta,
                                                final PublicKey phoenixProgramKey,
                                                final PublicKey phoenixLogAuthorityKey,
                                                final PublicKey globalConfigurationKey,
                                                final PublicKey perpAssetMapKey,
                                                final PublicKey liquidatorKey,
                                                final PublicKey liquidatorAccountKey,
                                                final PublicKey liquidateeAccountKey,
                                                final PublicKey globalTraderIndexKey,
                                                final PublicKey activeTraderBufferKey,
                                                final LiquidationTransferParams params) {
    final var keys = liquidationTransferKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      perpAssetMapKey,
      liquidatorKey,
      liquidatorAccountKey,
      liquidateeAccountKey,
      globalTraderIndexKey,
      activeTraderBufferKey
    );
    return liquidationTransfer(invokedEternalProgramMeta, keys, params);
  }

  /// Transfers positions directly between a liquidator and liquidatee once the liquidatee is flagged as unhealthy.
  ///
  public static Instruction liquidationTransfer(final AccountMeta invokedEternalProgramMeta,
                                                final List<AccountMeta> keys,
                                                final LiquidationTransferParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = LIQUIDATION_TRANSFER_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record LiquidationTransferIxData(Discriminator discriminator, LiquidationTransferParams params) implements SerDe {  

    public static LiquidationTransferIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static LiquidationTransferIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = LiquidationTransferParams.read(_data, i);
      return new LiquidationTransferIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator CLOSE_MATCHED_POSITIONS_DISCRIMINATOR = toDiscriminator(112, 174, 32, 120, 93, 113, 10, 161);

  /// Closes offsetting positions between an at-loss trader and an in-profit trader during ADL.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that authorizes ADL operations and tracks trader index PDAs.
  /// @param perpAssetMapKey Perp asset map account containing risk parameters used to validate the ADL transfer.
  /// @param callerKey ADL authority invoking the matched close instruction.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param atLossAccountKey Trader account currently at a loss whose matched exposure will be closed.
  /// @param inProfitAccountKey Trader account with offsetting profitable exposure to receive the transfer.
  public static List<AccountMeta> closeMatchedPositionsKeys(final PublicKey phoenixProgramKey,
                                                            final PublicKey phoenixLogAuthorityKey,
                                                            final PublicKey globalConfigurationKey,
                                                            final PublicKey perpAssetMapKey,
                                                            final PublicKey callerKey,
                                                            final PublicKey globalTraderIndexKey,
                                                            final PublicKey activeTraderBufferKey,
                                                            final PublicKey atLossAccountKey,
                                                            final PublicKey inProfitAccountKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createRead(globalConfigurationKey),
      createWrite(perpAssetMapKey),
      createReadOnlySigner(callerKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(atLossAccountKey),
      createWrite(inProfitAccountKey)
    );
  }

  /// Closes offsetting positions between an at-loss trader and an in-profit trader during ADL.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that authorizes ADL operations and tracks trader index PDAs.
  /// @param perpAssetMapKey Perp asset map account containing risk parameters used to validate the ADL transfer.
  /// @param callerKey ADL authority invoking the matched close instruction.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param atLossAccountKey Trader account currently at a loss whose matched exposure will be closed.
  /// @param inProfitAccountKey Trader account with offsetting profitable exposure to receive the transfer.
  public static Instruction closeMatchedPositions(final AccountMeta invokedEternalProgramMeta,
                                                  final PublicKey phoenixProgramKey,
                                                  final PublicKey phoenixLogAuthorityKey,
                                                  final PublicKey globalConfigurationKey,
                                                  final PublicKey perpAssetMapKey,
                                                  final PublicKey callerKey,
                                                  final PublicKey globalTraderIndexKey,
                                                  final PublicKey activeTraderBufferKey,
                                                  final PublicKey atLossAccountKey,
                                                  final PublicKey inProfitAccountKey,
                                                  final ClosePositionsParams params) {
    final var keys = closeMatchedPositionsKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      perpAssetMapKey,
      callerKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      atLossAccountKey,
      inProfitAccountKey
    );
    return closeMatchedPositions(invokedEternalProgramMeta, keys, params);
  }

  /// Closes offsetting positions between an at-loss trader and an in-profit trader during ADL.
  ///
  public static Instruction closeMatchedPositions(final AccountMeta invokedEternalProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final ClosePositionsParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = CLOSE_MATCHED_POSITIONS_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record CloseMatchedPositionsIxData(Discriminator discriminator, ClosePositionsParams params) implements SerDe {  

    public static CloseMatchedPositionsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static CloseMatchedPositionsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = ClosePositionsParams.read(_data, i);
      return new CloseMatchedPositionsIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator LOG_DISCRIMINATOR = toDiscriminator(141, 230, 214, 242, 9, 209, 207, 170);

  /// Internal instruction used by the Phoenix log authority to emit events on-chain.
  /// This instruction is called via CPI and should not be invoked directly.
  ///
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA that must sign via CPI.
  public static List<AccountMeta> logKeys(final PublicKey phoenixLogAuthorityKey) {
    return List.of(
      createReadOnlySigner(phoenixLogAuthorityKey)
    );
  }

  /// Internal instruction used by the Phoenix log authority to emit events on-chain.
  /// This instruction is called via CPI and should not be invoked directly.
  ///
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA that must sign via CPI.
  /// @param header Log header containing batch index and event count.
  /// @param events Serialized event data (variable length).
  public static Instruction log(final AccountMeta invokedEternalProgramMeta,
                                final PublicKey phoenixLogAuthorityKey,
                                final LogHeader header,
                                final byte[] events) {
    final var keys = logKeys(
      phoenixLogAuthorityKey
    );
    return log(invokedEternalProgramMeta, keys, header, events);
  }

  /// Internal instruction used by the Phoenix log authority to emit events on-chain.
  /// This instruction is called via CPI and should not be invoked directly.
  ///
  /// @param header Log header containing batch index and event count.
  /// @param events Serialized event data (variable length).
  public static Instruction log(final AccountMeta invokedEternalProgramMeta,
                                final List<AccountMeta> keys,
                                final LogHeader header,
                                final byte[] events) {
    final byte[] _data = new byte[8 + header.l() + SerDeUtil.lenVector(4, events)];
    int i = LOG_DISCRIMINATOR.write(_data, 0);
    i += header.write(_data, i);
    SerDeUtil.writeVector(4, events, _data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  /// @param header Log header containing batch index and event count.
  /// @param events Serialized event data (variable length).
  public record LogIxData(Discriminator discriminator, LogHeader header, byte[] events) implements SerDe {  

    public static LogIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int HEADER_OFFSET = 8;
    public static final int EVENTS_OFFSET = 12;

    public static LogIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var header = LogHeader.read(_data, i);
      i += header.l();
      final var events = SerDeUtil.readbyteVector(4, _data, i);
      return new LogIxData(discriminator, header, events);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += header.write(_data, i);
      i += SerDeUtil.writeVector(4, events, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + header.l() + SerDeUtil.lenVector(4, events);
    }
  }

  public static final Discriminator WITHDRAW_FUNDS_DISCRIMINATOR = toDiscriminator(241, 36, 29, 111, 208, 31, 104, 217);

  /// Withdraws collateral tokens from the global vault to a trader's token account, debiting the trader's account balance.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that tracks vault and system parameters.
  /// @param traderWalletKey Authority of the trader account initiating the withdrawal.
  /// @param traderAccountKey Trader state PDA from which collateral is debited.
  /// @param perpAssetMapKey Perp asset map for margin and risk checks.
  /// @param globalVaultKey Global vault token account from which tokens are withdrawn.
  /// @param destinationTokenAccountKey Trader's destination token account to receive the withdrawn tokens.
  /// @param tokenProgramKey SPL Token program for executing the transfer.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param withdrawQueueKey Withdraw queue PDA tracking pending withdrawal requests.
  public static List<AccountMeta> withdrawFundsKeys(final PublicKey phoenixProgramKey,
                                                    final PublicKey phoenixLogAuthorityKey,
                                                    final PublicKey globalConfigurationKey,
                                                    final PublicKey traderWalletKey,
                                                    final PublicKey traderAccountKey,
                                                    final PublicKey perpAssetMapKey,
                                                    final PublicKey globalVaultKey,
                                                    final PublicKey destinationTokenAccountKey,
                                                    final PublicKey tokenProgramKey,
                                                    final PublicKey globalTraderIndexKey,
                                                    final PublicKey activeTraderBufferKey,
                                                    final PublicKey withdrawQueueKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createReadOnlySigner(traderWalletKey),
      createWrite(traderAccountKey),
      createWrite(perpAssetMapKey),
      createWrite(globalVaultKey),
      createWrite(destinationTokenAccountKey),
      createRead(tokenProgramKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(withdrawQueueKey)
    );
  }

  /// Withdraws collateral tokens from the global vault to a trader's token account, debiting the trader's account balance.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that tracks vault and system parameters.
  /// @param traderWalletKey Authority of the trader account initiating the withdrawal.
  /// @param traderAccountKey Trader state PDA from which collateral is debited.
  /// @param perpAssetMapKey Perp asset map for margin and risk checks.
  /// @param globalVaultKey Global vault token account from which tokens are withdrawn.
  /// @param destinationTokenAccountKey Trader's destination token account to receive the withdrawn tokens.
  /// @param tokenProgramKey SPL Token program for executing the transfer.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param withdrawQueueKey Withdraw queue PDA tracking pending withdrawal requests.
  public static Instruction withdrawFunds(final AccountMeta invokedEternalProgramMeta,
                                          final PublicKey phoenixProgramKey,
                                          final PublicKey phoenixLogAuthorityKey,
                                          final PublicKey globalConfigurationKey,
                                          final PublicKey traderWalletKey,
                                          final PublicKey traderAccountKey,
                                          final PublicKey perpAssetMapKey,
                                          final PublicKey globalVaultKey,
                                          final PublicKey destinationTokenAccountKey,
                                          final PublicKey tokenProgramKey,
                                          final PublicKey globalTraderIndexKey,
                                          final PublicKey activeTraderBufferKey,
                                          final PublicKey withdrawQueueKey,
                                          final WithdrawFundsInstruction params) {
    final var keys = withdrawFundsKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderWalletKey,
      traderAccountKey,
      perpAssetMapKey,
      globalVaultKey,
      destinationTokenAccountKey,
      tokenProgramKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      withdrawQueueKey
    );
    return withdrawFunds(invokedEternalProgramMeta, keys, params);
  }

  /// Withdraws collateral tokens from the global vault to a trader's token account, debiting the trader's account balance.
  ///
  public static Instruction withdrawFunds(final AccountMeta invokedEternalProgramMeta,
                                          final List<AccountMeta> keys,
                                          final WithdrawFundsInstruction params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = WITHDRAW_FUNDS_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record WithdrawFundsIxData(Discriminator discriminator, WithdrawFundsInstruction params) implements SerDe {  

    public static WithdrawFundsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int PARAMS_OFFSET = 8;

    public static WithdrawFundsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = WithdrawFundsInstruction.read(_data, i);
      return new WithdrawFundsIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPLINE_PARAMETERS_DISCRIMINATOR = toDiscriminator(241, 91, 139, 170, 18, 41, 203, 22);

  /// Updates spline curve parameters for automated market making on bid and ask sides.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param signerAccountKey Authority authorized to update spline parameters.
  /// @param traderAccountKey Trader account associated with the spline.
  /// @param splineAccountKey Spline collection account holding bid and ask curve parameters.
  public static List<AccountMeta> updateSplineParametersKeys(final PublicKey phoenixProgramKey,
                                                             final PublicKey phoenixLogAuthorityKey,
                                                             final PublicKey signerAccountKey,
                                                             final PublicKey traderAccountKey,
                                                             final PublicKey splineAccountKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createReadOnlySigner(signerAccountKey),
      createRead(traderAccountKey),
      createWrite(splineAccountKey)
    );
  }

  /// Updates spline curve parameters for automated market making on bid and ask sides.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param signerAccountKey Authority authorized to update spline parameters.
  /// @param traderAccountKey Trader account associated with the spline.
  /// @param splineAccountKey Spline collection account holding bid and ask curve parameters.
  public static Instruction updateSplineParameters(final AccountMeta invokedEternalProgramMeta,
                                                   final PublicKey phoenixProgramKey,
                                                   final PublicKey phoenixLogAuthorityKey,
                                                   final PublicKey signerAccountKey,
                                                   final PublicKey traderAccountKey,
                                                   final PublicKey splineAccountKey,
                                                   final UpdateSplineParametersParamsWithOrdering params) {
    final var keys = updateSplineParametersKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      signerAccountKey,
      traderAccountKey,
      splineAccountKey
    );
    return updateSplineParameters(invokedEternalProgramMeta, keys, params);
  }

  /// Updates spline curve parameters for automated market making on bid and ask sides.
  ///
  public static Instruction updateSplineParameters(final AccountMeta invokedEternalProgramMeta,
                                                   final List<AccountMeta> keys,
                                                   final UpdateSplineParametersParamsWithOrdering params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = UPDATE_SPLINE_PARAMETERS_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record UpdateSplineParametersIxData(Discriminator discriminator, UpdateSplineParametersParamsWithOrdering params) implements SerDe {  

    public static UpdateSplineParametersIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static UpdateSplineParametersIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = UpdateSplineParametersParamsWithOrdering.read(_data, i);
      return new UpdateSplineParametersIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator PLACE_STOP_LOSS_DISCRIMINATOR = toDiscriminator(236, 46, 164, 66, 235, 92, 162, 137);

  /// Places a stop-loss order that triggers when the market price reaches the specified trigger price.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that tracks vault and system parameters.
  /// @param payerKey Payer for creating the stop loss account.
  /// @param traderAccountKey Trader account PDA that holds position and order state.
  /// @param perpAssetMapKey Perp asset map tracking positions and risk metrics.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  /// @param traderWalletKey Trader wallet authority.
  /// @param stopLossAccountKey Stop loss account PDA to be created or updated.
  /// @param systemProgramKey System program for creating the stop loss account.
  public static List<AccountMeta> placeStopLossKeys(final PublicKey phoenixProgramKey,
                                                    final PublicKey phoenixLogAuthorityKey,
                                                    final PublicKey globalConfigurationKey,
                                                    final PublicKey payerKey,
                                                    final PublicKey traderAccountKey,
                                                    final PublicKey perpAssetMapKey,
                                                    final PublicKey globalTraderIndexKey,
                                                    final PublicKey activeTraderBufferKey,
                                                    final PublicKey orderbookKey,
                                                    final PublicKey splinesKey,
                                                    final PublicKey traderWalletKey,
                                                    final PublicKey stopLossAccountKey,
                                                    final PublicKey systemProgramKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createRead(globalConfigurationKey),
      createWritableSigner(payerKey),
      createWrite(traderAccountKey),
      createWrite(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(orderbookKey),
      createWrite(splinesKey),
      createReadOnlySigner(traderWalletKey),
      createWrite(stopLossAccountKey),
      createRead(systemProgramKey)
    );
  }

  /// Places a stop-loss order that triggers when the market price reaches the specified trigger price.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that tracks vault and system parameters.
  /// @param payerKey Payer for creating the stop loss account.
  /// @param traderAccountKey Trader account PDA that holds position and order state.
  /// @param perpAssetMapKey Perp asset map tracking positions and risk metrics.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  /// @param traderWalletKey Trader wallet authority.
  /// @param stopLossAccountKey Stop loss account PDA to be created or updated.
  /// @param systemProgramKey System program for creating the stop loss account.
  public static Instruction placeStopLoss(final AccountMeta invokedEternalProgramMeta,
                                          final PublicKey phoenixProgramKey,
                                          final PublicKey phoenixLogAuthorityKey,
                                          final PublicKey globalConfigurationKey,
                                          final PublicKey payerKey,
                                          final PublicKey traderAccountKey,
                                          final PublicKey perpAssetMapKey,
                                          final PublicKey globalTraderIndexKey,
                                          final PublicKey activeTraderBufferKey,
                                          final PublicKey orderbookKey,
                                          final PublicKey splinesKey,
                                          final PublicKey traderWalletKey,
                                          final PublicKey stopLossAccountKey,
                                          final PublicKey systemProgramKey,
                                          final PlaceStopLossInstruction params) {
    final var keys = placeStopLossKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      payerKey,
      traderAccountKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      orderbookKey,
      splinesKey,
      traderWalletKey,
      stopLossAccountKey,
      systemProgramKey
    );
    return placeStopLoss(invokedEternalProgramMeta, keys, params);
  }

  /// Places a stop-loss order that triggers when the market price reaches the specified trigger price.
  ///
  public static Instruction placeStopLoss(final AccountMeta invokedEternalProgramMeta,
                                          final List<AccountMeta> keys,
                                          final PlaceStopLossInstruction params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = PLACE_STOP_LOSS_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record PlaceStopLossIxData(Discriminator discriminator, PlaceStopLossInstruction params) implements SerDe {  

    public static PlaceStopLossIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 35;

    public static final int PARAMS_OFFSET = 8;

    public static PlaceStopLossIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = PlaceStopLossInstruction.read(_data, i);
      return new PlaceStopLossIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator EXECUTE_STOP_LOSS_DISCRIMINATOR = toDiscriminator(38, 122, 90, 90, 182, 241, 7, 4);

  /// Executes a stop-loss order when the trigger conditions are met.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param signerKey Signer authorized to execute stop loss orders.
  /// @param permissionAccountKey Permission account verifying execution authority.
  /// @param traderAccountKey Trader account PDA that holds position and order state.
  /// @param perpAssetMapKey Perp asset map tracking positions and risk metrics.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  /// @param globalConfigurationKey Global configuration PDA that tracks vault and system parameters.
  /// @param funderKey Account to receive rent from closed stop loss account.
  /// @param stopLossAccountKey Stop loss account to execute and close.
  public static List<AccountMeta> executeStopLossKeys(final PublicKey phoenixProgramKey,
                                                      final PublicKey phoenixLogAuthorityKey,
                                                      final PublicKey signerKey,
                                                      final PublicKey permissionAccountKey,
                                                      final PublicKey traderAccountKey,
                                                      final PublicKey perpAssetMapKey,
                                                      final PublicKey globalTraderIndexKey,
                                                      final PublicKey activeTraderBufferKey,
                                                      final PublicKey orderbookKey,
                                                      final PublicKey splinesKey,
                                                      final PublicKey globalConfigurationKey,
                                                      final PublicKey funderKey,
                                                      final PublicKey stopLossAccountKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createReadOnlySigner(signerKey),
      createWrite(permissionAccountKey),
      createWrite(traderAccountKey),
      createWrite(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(orderbookKey),
      createWrite(splinesKey),
      createWrite(globalConfigurationKey),
      createWrite(funderKey),
      createWrite(stopLossAccountKey)
    );
  }

  /// Executes a stop-loss order when the trigger conditions are met.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param signerKey Signer authorized to execute stop loss orders.
  /// @param permissionAccountKey Permission account verifying execution authority.
  /// @param traderAccountKey Trader account PDA that holds position and order state.
  /// @param perpAssetMapKey Perp asset map tracking positions and risk metrics.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  /// @param globalConfigurationKey Global configuration PDA that tracks vault and system parameters.
  /// @param funderKey Account to receive rent from closed stop loss account.
  /// @param stopLossAccountKey Stop loss account to execute and close.
  public static Instruction executeStopLoss(final AccountMeta invokedEternalProgramMeta,
                                            final PublicKey phoenixProgramKey,
                                            final PublicKey phoenixLogAuthorityKey,
                                            final PublicKey signerKey,
                                            final PublicKey permissionAccountKey,
                                            final PublicKey traderAccountKey,
                                            final PublicKey perpAssetMapKey,
                                            final PublicKey globalTraderIndexKey,
                                            final PublicKey activeTraderBufferKey,
                                            final PublicKey orderbookKey,
                                            final PublicKey splinesKey,
                                            final PublicKey globalConfigurationKey,
                                            final PublicKey funderKey,
                                            final PublicKey stopLossAccountKey,
                                            final ExecuteStopLossInstruction params) {
    final var keys = executeStopLossKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      signerKey,
      permissionAccountKey,
      traderAccountKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      orderbookKey,
      splinesKey,
      globalConfigurationKey,
      funderKey,
      stopLossAccountKey
    );
    return executeStopLoss(invokedEternalProgramMeta, keys, params);
  }

  /// Executes a stop-loss order when the trigger conditions are met.
  ///
  public static Instruction executeStopLoss(final AccountMeta invokedEternalProgramMeta,
                                            final List<AccountMeta> keys,
                                            final ExecuteStopLossInstruction params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = EXECUTE_STOP_LOSS_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record ExecuteStopLossIxData(Discriminator discriminator, ExecuteStopLossInstruction params) implements SerDe {  

    public static ExecuteStopLossIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static final int PARAMS_OFFSET = 8;

    public static ExecuteStopLossIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = ExecuteStopLossInstruction.read(_data, i);
      return new ExecuteStopLossIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CANCEL_STOP_LOSS_DISCRIMINATOR = toDiscriminator(120, 201, 10, 102, 12, 9, 111, 126);

  /// Cancels a pending stop-loss order and closes the stop loss account.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that tracks vault and system parameters.
  /// @param funderKey Account to receive rent from closed stop loss account.
  /// @param traderAccountKey Trader account associated with the stop loss order.
  /// @param traderWalletKey Trader wallet authority.
  /// @param stopLossAccountKey Stop loss account to cancel and close.
  /// @param systemProgramKey System program for closing the account.
  public static List<AccountMeta> cancelStopLossKeys(final PublicKey phoenixProgramKey,
                                                     final PublicKey phoenixLogAuthorityKey,
                                                     final PublicKey globalConfigurationKey,
                                                     final PublicKey funderKey,
                                                     final PublicKey traderAccountKey,
                                                     final PublicKey traderWalletKey,
                                                     final PublicKey stopLossAccountKey,
                                                     final PublicKey systemProgramKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createWrite(funderKey),
      createRead(traderAccountKey),
      createReadOnlySigner(traderWalletKey),
      createWrite(stopLossAccountKey),
      createRead(systemProgramKey)
    );
  }

  /// Cancels a pending stop-loss order and closes the stop loss account.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that tracks vault and system parameters.
  /// @param funderKey Account to receive rent from closed stop loss account.
  /// @param traderAccountKey Trader account associated with the stop loss order.
  /// @param traderWalletKey Trader wallet authority.
  /// @param stopLossAccountKey Stop loss account to cancel and close.
  /// @param systemProgramKey System program for closing the account.
  public static Instruction cancelStopLoss(final AccountMeta invokedEternalProgramMeta,
                                           final PublicKey phoenixProgramKey,
                                           final PublicKey phoenixLogAuthorityKey,
                                           final PublicKey globalConfigurationKey,
                                           final PublicKey funderKey,
                                           final PublicKey traderAccountKey,
                                           final PublicKey traderWalletKey,
                                           final PublicKey stopLossAccountKey,
                                           final PublicKey systemProgramKey,
                                           final CancelStopLossInstruction params) {
    final var keys = cancelStopLossKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      funderKey,
      traderAccountKey,
      traderWalletKey,
      stopLossAccountKey,
      systemProgramKey
    );
    return cancelStopLoss(invokedEternalProgramMeta, keys, params);
  }

  /// Cancels a pending stop-loss order and closes the stop loss account.
  ///
  public static Instruction cancelStopLoss(final AccountMeta invokedEternalProgramMeta,
                                           final List<AccountMeta> keys,
                                           final CancelStopLossInstruction params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = CANCEL_STOP_LOSS_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record CancelStopLossIxData(Discriminator discriminator, CancelStopLossInstruction params) implements SerDe {  

    public static CancelStopLossIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static final int PARAMS_OFFSET = 8;

    public static CancelStopLossIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = CancelStopLossInstruction.read(_data, i);
      return new CancelStopLossIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator FORCE_CANCEL_ALL_DISCRIMINATOR = toDiscriminator(201, 0, 188, 127, 41, 55, 80, 48);

  /// Force cancels all open orders for a trader account. Requires authority authorization.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that tracks vault and system parameters.
  /// @param authorityKey Authority authorized to force cancel orders.
  /// @param traderAccountKey Trader account PDA that holds position and order state.
  /// @param perpAssetMapKey Perp asset map tracking positions and risk metrics.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static List<AccountMeta> forceCancelAllKeys(final PublicKey phoenixProgramKey,
                                                     final PublicKey phoenixLogAuthorityKey,
                                                     final PublicKey globalConfigurationKey,
                                                     final PublicKey authorityKey,
                                                     final PublicKey traderAccountKey,
                                                     final PublicKey perpAssetMapKey,
                                                     final PublicKey globalTraderIndexKey,
                                                     final PublicKey activeTraderBufferKey,
                                                     final PublicKey orderbookKey,
                                                     final PublicKey splinesKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createReadOnlySigner(authorityKey),
      createWrite(traderAccountKey),
      createWrite(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(orderbookKey),
      createWrite(splinesKey)
    );
  }

  /// Force cancels all open orders for a trader account. Requires authority authorization.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that tracks vault and system parameters.
  /// @param authorityKey Authority authorized to force cancel orders.
  /// @param traderAccountKey Trader account PDA that holds position and order state.
  /// @param perpAssetMapKey Perp asset map tracking positions and risk metrics.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static Instruction forceCancelAll(final AccountMeta invokedEternalProgramMeta,
                                           final PublicKey phoenixProgramKey,
                                           final PublicKey phoenixLogAuthorityKey,
                                           final PublicKey globalConfigurationKey,
                                           final PublicKey authorityKey,
                                           final PublicKey traderAccountKey,
                                           final PublicKey perpAssetMapKey,
                                           final PublicKey globalTraderIndexKey,
                                           final PublicKey activeTraderBufferKey,
                                           final PublicKey orderbookKey,
                                           final PublicKey splinesKey) {
    final var keys = forceCancelAllKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      authorityKey,
      traderAccountKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      orderbookKey,
      splinesKey
    );
    return forceCancelAll(invokedEternalProgramMeta, keys);
  }

  /// Force cancels all open orders for a trader account. Requires authority authorization.
  ///
  public static Instruction forceCancelAll(final AccountMeta invokedEternalProgramMeta,
                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedEternalProgramMeta, keys, FORCE_CANCEL_ALL_DISCRIMINATOR);
  }

  public static final Discriminator FORCE_CANCEL_RISK_INCREASING_DISCRIMINATOR = toDiscriminator(41, 55, 217, 35, 92, 72, 42, 238);

  /// Force cancels risk-increasing orders for a trader to reduce margin exposure.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that tracks vault and system parameters.
  /// @param traderAccountKey Trader account PDA that holds position and order state.
  /// @param perpAssetMapKey Perp asset map tracking positions and risk metrics.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static List<AccountMeta> forceCancelRiskIncreasingKeys(final PublicKey phoenixProgramKey,
                                                                final PublicKey phoenixLogAuthorityKey,
                                                                final PublicKey globalConfigurationKey,
                                                                final PublicKey traderAccountKey,
                                                                final PublicKey perpAssetMapKey,
                                                                final PublicKey globalTraderIndexKey,
                                                                final PublicKey activeTraderBufferKey,
                                                                final PublicKey orderbookKey,
                                                                final PublicKey splinesKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createWrite(traderAccountKey),
      createWrite(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(orderbookKey),
      createWrite(splinesKey)
    );
  }

  /// Force cancels risk-increasing orders for a trader to reduce margin exposure.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA that tracks vault and system parameters.
  /// @param traderAccountKey Trader account PDA that holds position and order state.
  /// @param perpAssetMapKey Perp asset map tracking positions and risk metrics.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static Instruction forceCancelRiskIncreasing(final AccountMeta invokedEternalProgramMeta,
                                                      final PublicKey phoenixProgramKey,
                                                      final PublicKey phoenixLogAuthorityKey,
                                                      final PublicKey globalConfigurationKey,
                                                      final PublicKey traderAccountKey,
                                                      final PublicKey perpAssetMapKey,
                                                      final PublicKey globalTraderIndexKey,
                                                      final PublicKey activeTraderBufferKey,
                                                      final PublicKey orderbookKey,
                                                      final PublicKey splinesKey,
                                                      final ForceCancelRiskIncreasingParams params) {
    final var keys = forceCancelRiskIncreasingKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderAccountKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      orderbookKey,
      splinesKey
    );
    return forceCancelRiskIncreasing(invokedEternalProgramMeta, keys, params);
  }

  /// Force cancels risk-increasing orders for a trader to reduce margin exposure.
  ///
  public static Instruction forceCancelRiskIncreasing(final AccountMeta invokedEternalProgramMeta,
                                                      final List<AccountMeta> keys,
                                                      final ForceCancelRiskIncreasingParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = FORCE_CANCEL_RISK_INCREASING_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record ForceCancelRiskIncreasingIxData(Discriminator discriminator, ForceCancelRiskIncreasingParams params) implements SerDe {  

    public static ForceCancelRiskIncreasingIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 8;

    public static final int PARAMS_OFFSET = 8;

    public static ForceCancelRiskIncreasingIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = ForceCancelRiskIncreasingParams.read(_data, i);
      return new ForceCancelRiskIncreasingIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_TRADER_STATE_DISCRIMINATOR = toDiscriminator(249, 139, 82, 44, 126, 66, 133, 220);

  /// Settles funding for a trader and evicts them from the active trader buffer when they no longer have resting orders.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Read-only global configuration PDA used to verify the perp asset map and buffer keys.
  /// @param traderAccountKey Trader state PDA whose funding snapshot and active status are updated.
  /// @param perpAssetMapKey Perp asset map providing funding parameters for settlement.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  public static List<AccountMeta> updateTraderStateKeys(final PublicKey phoenixProgramKey,
                                                        final PublicKey phoenixLogAuthorityKey,
                                                        final PublicKey globalConfigurationKey,
                                                        final PublicKey traderAccountKey,
                                                        final PublicKey perpAssetMapKey,
                                                        final PublicKey globalTraderIndexKey,
                                                        final PublicKey activeTraderBufferKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createRead(globalConfigurationKey),
      createWrite(traderAccountKey),
      createRead(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey)
    );
  }

  /// Settles funding for a trader and evicts them from the active trader buffer when they no longer have resting orders.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Read-only global configuration PDA used to verify the perp asset map and buffer keys.
  /// @param traderAccountKey Trader state PDA whose funding snapshot and active status are updated.
  /// @param perpAssetMapKey Perp asset map providing funding parameters for settlement.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  public static Instruction updateTraderState(final AccountMeta invokedEternalProgramMeta,
                                              final PublicKey phoenixProgramKey,
                                              final PublicKey phoenixLogAuthorityKey,
                                              final PublicKey globalConfigurationKey,
                                              final PublicKey traderAccountKey,
                                              final PublicKey perpAssetMapKey,
                                              final PublicKey globalTraderIndexKey,
                                              final PublicKey activeTraderBufferKey) {
    final var keys = updateTraderStateKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderAccountKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey
    );
    return updateTraderState(invokedEternalProgramMeta, keys);
  }

  /// Settles funding for a trader and evicts them from the active trader buffer when they no longer have resting orders.
  ///
  public static Instruction updateTraderState(final AccountMeta invokedEternalProgramMeta,
                                              final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedEternalProgramMeta, keys, UPDATE_TRADER_STATE_DISCRIMINATOR);
  }

  public static final Discriminator CONSUME_WITHDRAW_QUEUE_DISCRIMINATOR = toDiscriminator(228, 40, 211, 139, 39, 212, 116, 113);

  /// Processes pending withdrawals from the withdraw queue.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param perpAssetMapKey Perp asset map account.
  /// @param globalVaultKey Global vault token account.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param withdrawQueueKey Withdraw queue account.
  /// @param tokenProgramKey SPL Token program.
  /// @param traderWalletKey Trader wallet receiving the withdrawal.
  /// @param destinationTokenAccountKey Destination token account for withdrawn funds.
  /// @param traderAccountKey Trader state PDA.
  public static List<AccountMeta> consumeWithdrawQueueKeys(final PublicKey phoenixProgramKey,
                                                           final PublicKey phoenixLogAuthorityKey,
                                                           final PublicKey globalConfigurationKey,
                                                           final PublicKey perpAssetMapKey,
                                                           final PublicKey globalVaultKey,
                                                           final PublicKey globalTraderIndexKey,
                                                           final PublicKey activeTraderBufferKey,
                                                           final PublicKey withdrawQueueKey,
                                                           final PublicKey tokenProgramKey,
                                                           final PublicKey traderWalletKey,
                                                           final PublicKey destinationTokenAccountKey,
                                                           final PublicKey traderAccountKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createWrite(perpAssetMapKey),
      createWrite(globalVaultKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(withdrawQueueKey),
      createRead(tokenProgramKey),
      createRead(traderWalletKey),
      createWrite(destinationTokenAccountKey),
      createWrite(traderAccountKey)
    );
  }

  /// Processes pending withdrawals from the withdraw queue.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param perpAssetMapKey Perp asset map account.
  /// @param globalVaultKey Global vault token account.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param withdrawQueueKey Withdraw queue account.
  /// @param tokenProgramKey SPL Token program.
  /// @param traderWalletKey Trader wallet receiving the withdrawal.
  /// @param destinationTokenAccountKey Destination token account for withdrawn funds.
  /// @param traderAccountKey Trader state PDA.
  public static Instruction consumeWithdrawQueue(final AccountMeta invokedEternalProgramMeta,
                                                 final PublicKey phoenixProgramKey,
                                                 final PublicKey phoenixLogAuthorityKey,
                                                 final PublicKey globalConfigurationKey,
                                                 final PublicKey perpAssetMapKey,
                                                 final PublicKey globalVaultKey,
                                                 final PublicKey globalTraderIndexKey,
                                                 final PublicKey activeTraderBufferKey,
                                                 final PublicKey withdrawQueueKey,
                                                 final PublicKey tokenProgramKey,
                                                 final PublicKey traderWalletKey,
                                                 final PublicKey destinationTokenAccountKey,
                                                 final PublicKey traderAccountKey) {
    final var keys = consumeWithdrawQueueKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      perpAssetMapKey,
      globalVaultKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      withdrawQueueKey,
      tokenProgramKey,
      traderWalletKey,
      destinationTokenAccountKey,
      traderAccountKey
    );
    return consumeWithdrawQueue(invokedEternalProgramMeta, keys);
  }

  /// Processes pending withdrawals from the withdraw queue.
  ///
  public static Instruction consumeWithdrawQueue(final AccountMeta invokedEternalProgramMeta,
                                                 final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedEternalProgramMeta, keys, CONSUME_WITHDRAW_QUEUE_DISCRIMINATOR);
  }

  public static final Discriminator CREATE_PERMISSION_DISCRIMINATOR = toDiscriminator(190, 182, 26, 164, 156, 221, 8, 0);

  /// Creates a new permission account PDA for delegating authority.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param payerKey Payer for account creation rent.
  /// @param permissionKey Permission account PDA to create.
  /// @param authorityKey Authority granting the permission.
  /// @param userKey User receiving the permission.
  public static List<AccountMeta> createPermissionKeys(final SolanaAccounts solanaAccounts,
                                                       final PublicKey phoenixProgramKey,
                                                       final PublicKey phoenixLogAuthorityKey,
                                                       final PublicKey payerKey,
                                                       final PublicKey permissionKey,
                                                       final PublicKey authorityKey,
                                                       final PublicKey userKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWritableSigner(payerKey),
      createWrite(permissionKey),
      createRead(authorityKey),
      createRead(userKey),
      createRead(solanaAccounts.systemProgram())
    );
  }

  /// Creates a new permission account PDA for delegating authority.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param payerKey Payer for account creation rent.
  /// @param permissionKey Permission account PDA to create.
  /// @param authorityKey Authority granting the permission.
  /// @param userKey User receiving the permission.
  public static Instruction createPermission(final AccountMeta invokedEternalProgramMeta,
                                             final SolanaAccounts solanaAccounts,
                                             final PublicKey phoenixProgramKey,
                                             final PublicKey phoenixLogAuthorityKey,
                                             final PublicKey payerKey,
                                             final PublicKey permissionKey,
                                             final PublicKey authorityKey,
                                             final PublicKey userKey) {
    final var keys = createPermissionKeys(
      solanaAccounts,
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      payerKey,
      permissionKey,
      authorityKey,
      userKey
    );
    return createPermission(invokedEternalProgramMeta, keys);
  }

  /// Creates a new permission account PDA for delegating authority.
  ///
  public static Instruction createPermission(final AccountMeta invokedEternalProgramMeta,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedEternalProgramMeta, keys, CREATE_PERMISSION_DISCRIMINATOR);
  }

  public static final Discriminator DELEGATE_TRADER_DISCRIMINATOR = toDiscriminator(3, 164, 192, 106, 119, 91, 122, 224);

  /// Delegates position authority of a trader account to another address.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param traderWalletKey Current trader authority.
  /// @param traderAccountKey Trader state PDA to delegate.
  /// @param newPositionAuthorityKey New position authority address.
  public static List<AccountMeta> delegateTraderKeys(final PublicKey phoenixProgramKey,
                                                     final PublicKey phoenixLogAuthorityKey,
                                                     final PublicKey globalConfigurationKey,
                                                     final PublicKey traderWalletKey,
                                                     final PublicKey traderAccountKey,
                                                     final PublicKey newPositionAuthorityKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createRead(globalConfigurationKey),
      createReadOnlySigner(traderWalletKey),
      createWrite(traderAccountKey),
      createRead(newPositionAuthorityKey)
    );
  }

  /// Delegates position authority of a trader account to another address.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param traderWalletKey Current trader authority.
  /// @param traderAccountKey Trader state PDA to delegate.
  /// @param newPositionAuthorityKey New position authority address.
  public static Instruction delegateTrader(final AccountMeta invokedEternalProgramMeta,
                                           final PublicKey phoenixProgramKey,
                                           final PublicKey phoenixLogAuthorityKey,
                                           final PublicKey globalConfigurationKey,
                                           final PublicKey traderWalletKey,
                                           final PublicKey traderAccountKey,
                                           final PublicKey newPositionAuthorityKey) {
    final var keys = delegateTraderKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderWalletKey,
      traderAccountKey,
      newPositionAuthorityKey
    );
    return delegateTrader(invokedEternalProgramMeta, keys);
  }

  /// Delegates position authority of a trader account to another address.
  ///
  public static Instruction delegateTrader(final AccountMeta invokedEternalProgramMeta,
                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedEternalProgramMeta, keys, DELEGATE_TRADER_DISCRIMINATOR);
  }

  public static final Discriminator REVOKE_PERMISSION_DISCRIMINATOR = toDiscriminator(116, 82, 33, 181, 121, 144, 249, 227);

  /// Revokes permission flags from a permission account.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param authorityKey Root authority revoking the permission.
  /// @param maybePermissionAccountKey Optional permission account for delegation.
  /// @param targetPermissionKey Permission account to revoke flags from.
  public static List<AccountMeta> revokePermissionKeys(final PublicKey phoenixProgramKey,
                                                       final PublicKey phoenixLogAuthorityKey,
                                                       final PublicKey globalConfigurationKey,
                                                       final PublicKey authorityKey,
                                                       final PublicKey maybePermissionAccountKey,
                                                       final PublicKey targetPermissionKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createRead(globalConfigurationKey),
      createReadOnlySigner(authorityKey),
      createWrite(maybePermissionAccountKey),
      createWrite(targetPermissionKey)
    );
  }

  /// Revokes permission flags from a permission account.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param authorityKey Root authority revoking the permission.
  /// @param maybePermissionAccountKey Optional permission account for delegation.
  /// @param targetPermissionKey Permission account to revoke flags from.
  public static Instruction revokePermission(final AccountMeta invokedEternalProgramMeta,
                                             final PublicKey phoenixProgramKey,
                                             final PublicKey phoenixLogAuthorityKey,
                                             final PublicKey globalConfigurationKey,
                                             final PublicKey authorityKey,
                                             final PublicKey maybePermissionAccountKey,
                                             final PublicKey targetPermissionKey,
                                             final RevokePermissionParams params) {
    final var keys = revokePermissionKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      authorityKey,
      maybePermissionAccountKey,
      targetPermissionKey
    );
    return revokePermission(invokedEternalProgramMeta, keys, params);
  }

  /// Revokes permission flags from a permission account.
  ///
  public static Instruction revokePermission(final AccountMeta invokedEternalProgramMeta,
                                             final List<AccountMeta> keys,
                                             final RevokePermissionParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = REVOKE_PERMISSION_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record RevokePermissionIxData(Discriminator discriminator, RevokePermissionParams params) implements SerDe {  

    public static RevokePermissionIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int PARAMS_OFFSET = 8;

    public static RevokePermissionIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = RevokePermissionParams.read(_data, i);
      return new RevokePermissionIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_PERMISSION_DISCRIMINATOR = toDiscriminator(70, 126, 41, 194, 245, 189, 128, 8);

  /// Sets permission flags on a permission account.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param permissionKey Permission account to modify.
  /// @param authorityKey Authority owning the permission.
  /// @param userKey User the permission applies to.
  public static List<AccountMeta> setPermissionKeys(final PublicKey phoenixProgramKey,
                                                    final PublicKey phoenixLogAuthorityKey,
                                                    final PublicKey permissionKey,
                                                    final PublicKey authorityKey,
                                                    final PublicKey userKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(permissionKey),
      createReadOnlySigner(authorityKey),
      createRead(userKey)
    );
  }

  /// Sets permission flags on a permission account.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param permissionKey Permission account to modify.
  /// @param authorityKey Authority owning the permission.
  /// @param userKey User the permission applies to.
  public static Instruction setPermission(final AccountMeta invokedEternalProgramMeta,
                                          final PublicKey phoenixProgramKey,
                                          final PublicKey phoenixLogAuthorityKey,
                                          final PublicKey permissionKey,
                                          final PublicKey authorityKey,
                                          final PublicKey userKey,
                                          final SetPermissionInstruction params) {
    final var keys = setPermissionKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      permissionKey,
      authorityKey,
      userKey
    );
    return setPermission(invokedEternalProgramMeta, keys, params);
  }

  /// Sets permission flags on a permission account.
  ///
  public static Instruction setPermission(final AccountMeta invokedEternalProgramMeta,
                                          final List<AccountMeta> keys,
                                          final SetPermissionInstruction params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = SET_PERMISSION_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record SetPermissionIxData(Discriminator discriminator, SetPermissionInstruction params) implements SerDe {  

    public static SetPermissionIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static SetPermissionIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = SetPermissionInstruction.read(_data, i);
      return new SetPermissionIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator SET_TRADER_CAPABILITIES_DELEGATED_DISCRIMINATOR = toDiscriminator(207, 170, 17, 21, 53, 35, 88, 151);

  /// Sets trader capability flags via a delegated permission.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param authorityKey Authority with trader onboarding delegation.
  /// @param maybePermissionAccountKey Permission account for delegation validation.
  /// @param traderAccountKey Trader state PDA.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  public static List<AccountMeta> setTraderCapabilitiesDelegatedKeys(final PublicKey phoenixProgramKey,
                                                                     final PublicKey phoenixLogAuthorityKey,
                                                                     final PublicKey globalConfigurationKey,
                                                                     final PublicKey authorityKey,
                                                                     final PublicKey maybePermissionAccountKey,
                                                                     final PublicKey traderAccountKey,
                                                                     final PublicKey globalTraderIndexKey,
                                                                     final PublicKey activeTraderBufferKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createRead(globalConfigurationKey),
      createReadOnlySigner(authorityKey),
      createWrite(maybePermissionAccountKey),
      createWrite(traderAccountKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey)
    );
  }

  /// Sets trader capability flags via a delegated permission.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param authorityKey Authority with trader onboarding delegation.
  /// @param maybePermissionAccountKey Permission account for delegation validation.
  /// @param traderAccountKey Trader state PDA.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  public static Instruction setTraderCapabilitiesDelegated(final AccountMeta invokedEternalProgramMeta,
                                                           final PublicKey phoenixProgramKey,
                                                           final PublicKey phoenixLogAuthorityKey,
                                                           final PublicKey globalConfigurationKey,
                                                           final PublicKey authorityKey,
                                                           final PublicKey maybePermissionAccountKey,
                                                           final PublicKey traderAccountKey,
                                                           final PublicKey globalTraderIndexKey,
                                                           final PublicKey activeTraderBufferKey,
                                                           final TraderCapabilityUpdate params) {
    final var keys = setTraderCapabilitiesDelegatedKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      authorityKey,
      maybePermissionAccountKey,
      traderAccountKey,
      globalTraderIndexKey,
      activeTraderBufferKey
    );
    return setTraderCapabilitiesDelegated(invokedEternalProgramMeta, keys, params);
  }

  /// Sets trader capability flags via a delegated permission.
  ///
  public static Instruction setTraderCapabilitiesDelegated(final AccountMeta invokedEternalProgramMeta,
                                                           final List<AccountMeta> keys,
                                                           final TraderCapabilityUpdate params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = SET_TRADER_CAPABILITIES_DELEGATED_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record SetTraderCapabilitiesDelegatedIxData(Discriminator discriminator, TraderCapabilityUpdate params) implements SerDe {  

    public static SetTraderCapabilitiesDelegatedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static SetTraderCapabilitiesDelegatedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = TraderCapabilityUpdate.read(_data, i);
      return new SetTraderCapabilitiesDelegatedIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator SET_TRADER_CAPABILITY_DISCRIMINATOR = toDiscriminator(191, 72, 45, 190, 214, 250, 182, 213);

  /// Sets trader capability flags via risk authority.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param authorityKey Risk authority.
  /// @param maybePermissionAccountKey Permission account for delegation validation.
  /// @param traderAccountKey Trader state PDA.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  public static List<AccountMeta> setTraderCapabilityKeys(final PublicKey phoenixProgramKey,
                                                          final PublicKey phoenixLogAuthorityKey,
                                                          final PublicKey globalConfigurationKey,
                                                          final PublicKey authorityKey,
                                                          final PublicKey maybePermissionAccountKey,
                                                          final PublicKey traderAccountKey,
                                                          final PublicKey globalTraderIndexKey,
                                                          final PublicKey activeTraderBufferKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createRead(globalConfigurationKey),
      createReadOnlySigner(authorityKey),
      createWrite(maybePermissionAccountKey),
      createWrite(traderAccountKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey)
    );
  }

  /// Sets trader capability flags via risk authority.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param authorityKey Risk authority.
  /// @param maybePermissionAccountKey Permission account for delegation validation.
  /// @param traderAccountKey Trader state PDA.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  public static Instruction setTraderCapability(final AccountMeta invokedEternalProgramMeta,
                                                final PublicKey phoenixProgramKey,
                                                final PublicKey phoenixLogAuthorityKey,
                                                final PublicKey globalConfigurationKey,
                                                final PublicKey authorityKey,
                                                final PublicKey maybePermissionAccountKey,
                                                final PublicKey traderAccountKey,
                                                final PublicKey globalTraderIndexKey,
                                                final PublicKey activeTraderBufferKey,
                                                final TraderCapabilityUpdate params) {
    final var keys = setTraderCapabilityKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      authorityKey,
      maybePermissionAccountKey,
      traderAccountKey,
      globalTraderIndexKey,
      activeTraderBufferKey
    );
    return setTraderCapability(invokedEternalProgramMeta, keys, params);
  }

  /// Sets trader capability flags via risk authority.
  ///
  public static Instruction setTraderCapability(final AccountMeta invokedEternalProgramMeta,
                                                final List<AccountMeta> keys,
                                                final TraderCapabilityUpdate params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = SET_TRADER_CAPABILITY_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record SetTraderCapabilityIxData(Discriminator discriminator, TraderCapabilityUpdate params) implements SerDe {  

    public static SetTraderCapabilityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static SetTraderCapabilityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = TraderCapabilityUpdate.read(_data, i);
      return new SetTraderCapabilityIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator SYNC_PARENT_TO_CHILD_DISCRIMINATOR = toDiscriminator(175, 137, 217, 11, 235, 39, 150, 19);

  /// Syncs capabilities from a parent trader account to a child (isolated margin) account.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param traderWalletKey Trader wallet owning both accounts.
  /// @param parentTraderAccountKey Parent (cross margin) trader account.
  /// @param childTraderAccountKey Child (isolated margin) trader account to sync.
  /// @param globalTraderIndexKey Global trader index account.
  public static List<AccountMeta> syncParentToChildKeys(final PublicKey phoenixProgramKey,
                                                        final PublicKey phoenixLogAuthorityKey,
                                                        final PublicKey globalConfigurationKey,
                                                        final PublicKey traderWalletKey,
                                                        final PublicKey parentTraderAccountKey,
                                                        final PublicKey childTraderAccountKey,
                                                        final PublicKey globalTraderIndexKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createRead(globalConfigurationKey),
      createRead(traderWalletKey),
      createRead(parentTraderAccountKey),
      createWrite(childTraderAccountKey),
      createWrite(globalTraderIndexKey)
    );
  }

  /// Syncs capabilities from a parent trader account to a child (isolated margin) account.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param traderWalletKey Trader wallet owning both accounts.
  /// @param parentTraderAccountKey Parent (cross margin) trader account.
  /// @param childTraderAccountKey Child (isolated margin) trader account to sync.
  /// @param globalTraderIndexKey Global trader index account.
  public static Instruction syncParentToChild(final AccountMeta invokedEternalProgramMeta,
                                              final PublicKey phoenixProgramKey,
                                              final PublicKey phoenixLogAuthorityKey,
                                              final PublicKey globalConfigurationKey,
                                              final PublicKey traderWalletKey,
                                              final PublicKey parentTraderAccountKey,
                                              final PublicKey childTraderAccountKey,
                                              final PublicKey globalTraderIndexKey) {
    final var keys = syncParentToChildKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderWalletKey,
      parentTraderAccountKey,
      childTraderAccountKey,
      globalTraderIndexKey
    );
    return syncParentToChild(invokedEternalProgramMeta, keys);
  }

  /// Syncs capabilities from a parent trader account to a child (isolated margin) account.
  ///
  public static Instruction syncParentToChild(final AccountMeta invokedEternalProgramMeta,
                                              final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedEternalProgramMeta, keys, SYNC_PARENT_TO_CHILD_DISCRIMINATOR);
  }

  public static final Discriminator SYNC_TRADER_CAPABILITIES_DISCRIMINATOR = toDiscriminator(154, 64, 87, 130, 130, 34, 138, 0);

  /// Syncs capability flags from a parent trader account to a frozen child account.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param traderWalletKey Trader wallet owning both accounts.
  /// @param parentTraderAccountKey Parent (cross margin) trader account.
  /// @param childTraderAccountKey Child trader account to sync capabilities to.
  public static List<AccountMeta> syncTraderCapabilitiesKeys(final PublicKey phoenixProgramKey,
                                                             final PublicKey phoenixLogAuthorityKey,
                                                             final PublicKey globalConfigurationKey,
                                                             final PublicKey traderWalletKey,
                                                             final PublicKey parentTraderAccountKey,
                                                             final PublicKey childTraderAccountKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createRead(globalConfigurationKey),
      createRead(traderWalletKey),
      createRead(parentTraderAccountKey),
      createWrite(childTraderAccountKey)
    );
  }

  /// Syncs capability flags from a parent trader account to a frozen child account.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param traderWalletKey Trader wallet owning both accounts.
  /// @param parentTraderAccountKey Parent (cross margin) trader account.
  /// @param childTraderAccountKey Child trader account to sync capabilities to.
  public static Instruction syncTraderCapabilities(final AccountMeta invokedEternalProgramMeta,
                                                   final PublicKey phoenixProgramKey,
                                                   final PublicKey phoenixLogAuthorityKey,
                                                   final PublicKey globalConfigurationKey,
                                                   final PublicKey traderWalletKey,
                                                   final PublicKey parentTraderAccountKey,
                                                   final PublicKey childTraderAccountKey) {
    final var keys = syncTraderCapabilitiesKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderWalletKey,
      parentTraderAccountKey,
      childTraderAccountKey
    );
    return syncTraderCapabilities(invokedEternalProgramMeta, keys);
  }

  /// Syncs capability flags from a parent trader account to a frozen child account.
  ///
  public static Instruction syncTraderCapabilities(final AccountMeta invokedEternalProgramMeta,
                                                   final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedEternalProgramMeta, keys, SYNC_TRADER_CAPABILITIES_DISCRIMINATOR);
  }

  public static final Discriminator TRANSFER_COLLATERAL_DISCRIMINATOR = toDiscriminator(157, 163, 63, 27, 242, 72, 251, 97);

  /// Transfers collateral between two trader accounts owned by the same wallet.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param traderWalletKey Trader wallet owning both accounts.
  /// @param srcTraderAccountKey Source trader account.
  /// @param dstTraderAccountKey Destination trader account.
  /// @param perpAssetMapKey Perp asset map for margin calculations.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  public static List<AccountMeta> transferCollateralKeys(final PublicKey phoenixProgramKey,
                                                         final PublicKey phoenixLogAuthorityKey,
                                                         final PublicKey globalConfigurationKey,
                                                         final PublicKey traderWalletKey,
                                                         final PublicKey srcTraderAccountKey,
                                                         final PublicKey dstTraderAccountKey,
                                                         final PublicKey perpAssetMapKey,
                                                         final PublicKey globalTraderIndexKey,
                                                         final PublicKey activeTraderBufferKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createRead(globalConfigurationKey),
      createReadOnlySigner(traderWalletKey),
      createWrite(srcTraderAccountKey),
      createWrite(dstTraderAccountKey),
      createRead(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey)
    );
  }

  /// Transfers collateral between two trader accounts owned by the same wallet.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param traderWalletKey Trader wallet owning both accounts.
  /// @param srcTraderAccountKey Source trader account.
  /// @param dstTraderAccountKey Destination trader account.
  /// @param perpAssetMapKey Perp asset map for margin calculations.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  public static Instruction transferCollateral(final AccountMeta invokedEternalProgramMeta,
                                               final PublicKey phoenixProgramKey,
                                               final PublicKey phoenixLogAuthorityKey,
                                               final PublicKey globalConfigurationKey,
                                               final PublicKey traderWalletKey,
                                               final PublicKey srcTraderAccountKey,
                                               final PublicKey dstTraderAccountKey,
                                               final PublicKey perpAssetMapKey,
                                               final PublicKey globalTraderIndexKey,
                                               final PublicKey activeTraderBufferKey,
                                               final TransferCollateralInstruction params) {
    final var keys = transferCollateralKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderWalletKey,
      srcTraderAccountKey,
      dstTraderAccountKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey
    );
    return transferCollateral(invokedEternalProgramMeta, keys, params);
  }

  /// Transfers collateral between two trader accounts owned by the same wallet.
  ///
  public static Instruction transferCollateral(final AccountMeta invokedEternalProgramMeta,
                                               final List<AccountMeta> keys,
                                               final TransferCollateralInstruction params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = TRANSFER_COLLATERAL_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record TransferCollateralIxData(Discriminator discriminator, TransferCollateralInstruction params) implements SerDe {  

    public static TransferCollateralIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int PARAMS_OFFSET = 8;

    public static TransferCollateralIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = TransferCollateralInstruction.read(_data, i);
      return new TransferCollateralIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator TRANSFER_COLLATERAL_CHILD_TO_PARENT_DISCRIMINATOR = toDiscriminator(51, 100, 177, 115, 139, 135, 247, 139);

  /// Transfers collateral from a child (isolated margin) account to its parent (cross margin) account.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param traderWalletKey Trader wallet owning both accounts.
  /// @param childTraderAccountKey Child (isolated margin) trader account.
  /// @param parentTraderAccountKey Parent (cross margin) trader account.
  /// @param perpAssetMapKey Perp asset map for margin calculations.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  public static List<AccountMeta> transferCollateralChildToParentKeys(final PublicKey phoenixProgramKey,
                                                                      final PublicKey phoenixLogAuthorityKey,
                                                                      final PublicKey globalConfigurationKey,
                                                                      final PublicKey traderWalletKey,
                                                                      final PublicKey childTraderAccountKey,
                                                                      final PublicKey parentTraderAccountKey,
                                                                      final PublicKey perpAssetMapKey,
                                                                      final PublicKey globalTraderIndexKey,
                                                                      final PublicKey activeTraderBufferKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createRead(globalConfigurationKey),
      createRead(traderWalletKey),
      createWrite(childTraderAccountKey),
      createWrite(parentTraderAccountKey),
      createRead(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey)
    );
  }

  /// Transfers collateral from a child (isolated margin) account to its parent (cross margin) account.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param traderWalletKey Trader wallet owning both accounts.
  /// @param childTraderAccountKey Child (isolated margin) trader account.
  /// @param parentTraderAccountKey Parent (cross margin) trader account.
  /// @param perpAssetMapKey Perp asset map for margin calculations.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  public static Instruction transferCollateralChildToParent(final AccountMeta invokedEternalProgramMeta,
                                                            final PublicKey phoenixProgramKey,
                                                            final PublicKey phoenixLogAuthorityKey,
                                                            final PublicKey globalConfigurationKey,
                                                            final PublicKey traderWalletKey,
                                                            final PublicKey childTraderAccountKey,
                                                            final PublicKey parentTraderAccountKey,
                                                            final PublicKey perpAssetMapKey,
                                                            final PublicKey globalTraderIndexKey,
                                                            final PublicKey activeTraderBufferKey,
                                                            final TransferCollateralChildToParentInstruction params) {
    final var keys = transferCollateralChildToParentKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      traderWalletKey,
      childTraderAccountKey,
      parentTraderAccountKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey
    );
    return transferCollateralChildToParent(invokedEternalProgramMeta, keys, params);
  }

  /// Transfers collateral from a child (isolated margin) account to its parent (cross margin) account.
  ///
  public static Instruction transferCollateralChildToParent(final AccountMeta invokedEternalProgramMeta,
                                                            final List<AccountMeta> keys,
                                                            final TransferCollateralChildToParentInstruction params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = TRANSFER_COLLATERAL_CHILD_TO_PARENT_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record TransferCollateralChildToParentIxData(Discriminator discriminator, TransferCollateralChildToParentInstruction params) implements SerDe {  

    public static TransferCollateralChildToParentIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int PARAMS_OFFSET = 8;

    public static TransferCollateralChildToParentIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = TransferCollateralChildToParentInstruction.read(_data, i);
      return new TransferCollateralChildToParentIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UNCROSS_CRANK_DISCRIMINATOR = toDiscriminator(196, 121, 94, 253, 139, 42, 252, 147);

  /// Cranks the orderbook to uncross any crossed orders.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param perpAssetMapKey Perp asset map account.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account.
  /// @param splinesKey Spline collection account.
  public static List<AccountMeta> uncrossCrankKeys(final PublicKey phoenixProgramKey,
                                                   final PublicKey phoenixLogAuthorityKey,
                                                   final PublicKey globalConfigurationKey,
                                                   final PublicKey perpAssetMapKey,
                                                   final PublicKey globalTraderIndexKey,
                                                   final PublicKey activeTraderBufferKey,
                                                   final PublicKey orderbookKey,
                                                   final PublicKey splinesKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createWrite(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(orderbookKey),
      createWrite(splinesKey)
    );
  }

  /// Cranks the orderbook to uncross any crossed orders.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param perpAssetMapKey Perp asset map account.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account.
  /// @param splinesKey Spline collection account.
  public static Instruction uncrossCrank(final AccountMeta invokedEternalProgramMeta,
                                         final PublicKey phoenixProgramKey,
                                         final PublicKey phoenixLogAuthorityKey,
                                         final PublicKey globalConfigurationKey,
                                         final PublicKey perpAssetMapKey,
                                         final PublicKey globalTraderIndexKey,
                                         final PublicKey activeTraderBufferKey,
                                         final PublicKey orderbookKey,
                                         final PublicKey splinesKey,
                                         final UncrossCrankParams params) {
    final var keys = uncrossCrankKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      orderbookKey,
      splinesKey
    );
    return uncrossCrank(invokedEternalProgramMeta, keys, params);
  }

  /// Cranks the orderbook to uncross any crossed orders.
  ///
  public static Instruction uncrossCrank(final AccountMeta invokedEternalProgramMeta,
                                         final List<AccountMeta> keys,
                                         final UncrossCrankParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = UNCROSS_CRANK_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record UncrossCrankIxData(Discriminator discriminator, UncrossCrankParams params) implements SerDe {  

    public static UncrossCrankIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int PARAMS_OFFSET = 8;

    public static UncrossCrankIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = UncrossCrankParams.read(_data, i);
      return new UncrossCrankIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CLEAR_EXPIRED_ORDERS_DISCRIMINATOR = toDiscriminator(128, 95, 102, 169, 146, 205, 248, 94);

  /// Clears expired orders from the orderbook as a maintenance crank operation.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param perpAssetMapKey Perp asset map tracking positions and risk metrics.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static List<AccountMeta> clearExpiredOrdersKeys(final PublicKey phoenixProgramKey,
                                                         final PublicKey phoenixLogAuthorityKey,
                                                         final PublicKey globalConfigurationKey,
                                                         final PublicKey perpAssetMapKey,
                                                         final PublicKey globalTraderIndexKey,
                                                         final PublicKey activeTraderBufferKey,
                                                         final PublicKey orderbookKey,
                                                         final PublicKey splinesKey) {
    return List.of(
      createRead(phoenixProgramKey),
      createRead(phoenixLogAuthorityKey),
      createWrite(globalConfigurationKey),
      createWrite(perpAssetMapKey),
      createWrite(globalTraderIndexKey),
      createWrite(activeTraderBufferKey),
      createWrite(orderbookKey),
      createWrite(splinesKey)
    );
  }

  /// Clears expired orders from the orderbook as a maintenance crank operation.
  ///
  /// @param phoenixProgramKey Phoenix Eternal program id; logged to assert the correct executable is invoked.
  /// @param phoenixLogAuthorityKey Phoenix log authority PDA used for emitting on-chain market events.
  /// @param globalConfigurationKey Global configuration PDA.
  /// @param perpAssetMapKey Perp asset map tracking positions and risk metrics.
  /// @param globalTraderIndexKey Global trader index account.
  /// @param activeTraderBufferKey Active trader buffer account.
  /// @param orderbookKey Market orderbook account that stores resting liquidity.
  /// @param splinesKey Spline collection PDA that tracks market-making programs for this market.
  public static Instruction clearExpiredOrders(final AccountMeta invokedEternalProgramMeta,
                                               final PublicKey phoenixProgramKey,
                                               final PublicKey phoenixLogAuthorityKey,
                                               final PublicKey globalConfigurationKey,
                                               final PublicKey perpAssetMapKey,
                                               final PublicKey globalTraderIndexKey,
                                               final PublicKey activeTraderBufferKey,
                                               final PublicKey orderbookKey,
                                               final PublicKey splinesKey,
                                               final ClearExpiredOrdersParams params) {
    final var keys = clearExpiredOrdersKeys(
      phoenixProgramKey,
      phoenixLogAuthorityKey,
      globalConfigurationKey,
      perpAssetMapKey,
      globalTraderIndexKey,
      activeTraderBufferKey,
      orderbookKey,
      splinesKey
    );
    return clearExpiredOrders(invokedEternalProgramMeta, keys, params);
  }

  /// Clears expired orders from the orderbook as a maintenance crank operation.
  ///
  public static Instruction clearExpiredOrders(final AccountMeta invokedEternalProgramMeta,
                                               final List<AccountMeta> keys,
                                               final ClearExpiredOrdersParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = CLEAR_EXPIRED_ORDERS_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedEternalProgramMeta, keys, _data);
  }

  public record ClearExpiredOrdersIxData(Discriminator discriminator, ClearExpiredOrdersParams params) implements SerDe {  

    public static ClearExpiredOrdersIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static ClearExpiredOrdersIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = ClearExpiredOrdersParams.read(_data, i);
      return new ClearExpiredOrdersIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  private EternalProgram() {
  }
}

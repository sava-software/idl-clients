package software.sava.idl.clients.phoenix;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.phoenix.ember.gen.types.DepositParams;
import software.sava.idl.clients.phoenix.ember.gen.types.WithdrawParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.CancelConditionalOrderInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.CancelEscrowRequestParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.CancelStopLossInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.CancelUpToInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.CreateConditionalOrdersAccountInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.CreateEscrowAccountParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.CreateEscrowRequestParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.DepositFundsInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.MultipleOrderPacket;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.OrderIds;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.OrderPacket;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.PlaceAttachedConditionalOrderInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.PlaceLimitOrderWithConditionalsInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.PlacePositionConditionalOrderInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.PlaceStopLossInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.RegisterTraderParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.TransferCollateralInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.UpsertEscrowRequestParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.WithdrawFundsInstruction;

public interface PhoenixClient {

  static PhoenixClient createClient(final SolanaAccounts solanaAccounts, final PhoenixAccounts accounts) {
    return new PhoenixClientImpl(solanaAccounts, accounts);
  }

  static PhoenixClient createClient(final PhoenixAccounts accounts) {
    return createClient(SolanaAccounts.MAIN_NET, accounts);
  }

  SolanaAccounts solanaAccounts();

  PhoenixAccounts phoenixAccounts();

  // Ember

  Instruction deposit(final PublicKey owner,
                      final PublicKey mint,
                      final PublicKey emberMint,
                      final PublicKey tokenAccount,
                      final PublicKey emberTokenAccount,
                      final PublicKey tokenProgram,
                      final DepositParams depositParams);

  Instruction withdraw(final PublicKey owner,
                       final PublicKey inputMint,
                       final PublicKey outputMint,
                       final PublicKey inputTokenAccount,
                       final PublicKey outputTokenAccount,
                       final PublicKey tokenProgram,
                       final WithdrawParams withdrawParams);

  // Eternal: trader lifecycle

  Instruction registerTrader(final PublicKey payerKey,
                             final PublicKey traderWalletKey,
                             final PublicKey traderAccountKey,
                             final RegisterTraderParams params);

  Instruction depositFunds(final PublicKey traderWalletKey,
                           final PublicKey traderTokenAccountKey,
                           final PublicKey traderAccountKey,
                           final PublicKey tokenProgram,
                           final DepositFundsInstruction depositFundsInstruction);

  Instruction withdrawFunds(final PublicKey traderWalletKey,
                            final PublicKey traderAccountKey,
                            final PublicKey perpAssetMapKey,
                            final PublicKey destinationTokenAccountKey,
                            final PublicKey tokenProgramKey,
                            final PublicKey withdrawQueueKey,
                            final WithdrawFundsInstruction params);

  Instruction delegateTrader(final PublicKey traderWalletKey,
                             final PublicKey traderAccountKey,
                             final PublicKey newPositionAuthorityKey);

  Instruction updateTraderState(final PublicKey traderAccountKey,
                                final PublicKey perpAssetMapKey);

  Instruction transferCollateral(final PublicKey traderWalletKey,
                                 final PublicKey srcTraderAccountKey,
                                 final PublicKey dstTraderAccountKey,
                                 final PublicKey perpAssetMapKey,
                                 final TransferCollateralInstruction params);

  Instruction transferCollateralChildToParent(final PublicKey traderWalletKey,
                                              final PublicKey childTraderAccountKey,
                                              final PublicKey parentTraderAccountKey,
                                              final PublicKey perpAssetMapKey);

  Instruction syncParentToChild(final PublicKey traderWalletKey,
                                final PublicKey parentTraderAccountKey,
                                final PublicKey childTraderAccountKey);

  // Eternal: order management

  Instruction placeMarketOrder(final PublicKey traderWalletKey,
                               final PublicKey traderAccountKey,
                               final PublicKey perpAssetMapKey,
                               final PublicKey orderbookKey,
                               final PublicKey splinesKey,
                               final OrderPacket orderPacket);

  Instruction placeLimitOrder(final PublicKey traderWalletKey,
                              final PublicKey traderAccountKey,
                              final PublicKey perpAssetMapKey,
                              final PublicKey orderbookKey,
                              final PublicKey splinesKey,
                              final OrderPacket orderPacket);

  Instruction placeMultiLimitOrder(final PublicKey traderWalletKey,
                                   final PublicKey traderAccountKey,
                                   final PublicKey perpAssetMapKey,
                                   final PublicKey orderbookKey,
                                   final PublicKey splinesKey,
                                   final MultipleOrderPacket orderPacket);

  Instruction cancelOrdersById(final PublicKey traderWalletKey,
                               final PublicKey traderAccountKey,
                               final PublicKey perpAssetMapKey,
                               final PublicKey orderbookKey,
                               final PublicKey splinesKey,
                               final OrderIds orderIds);

  Instruction cancelUpTo(final PublicKey traderWalletKey,
                         final PublicKey traderAccountKey,
                         final PublicKey perpAssetMapKey,
                         final PublicKey orderbookKey,
                         final PublicKey splinesKey,
                         final CancelUpToInstruction params);

  Instruction cancelAll(final PublicKey traderWalletKey,
                        final PublicKey traderAccountKey,
                        final PublicKey perpAssetMapKey,
                        final PublicKey orderbookKey,
                        final PublicKey splinesKey);

  // Eternal: conditional orders

  Instruction createConditionalOrdersAccount(final PublicKey payerKey,
                                             final PublicKey traderWalletKey,
                                             final PublicKey traderAccountKey,
                                             final PublicKey traderConditionalOrdersKey,
                                             final CreateConditionalOrdersAccountInstruction params);

  Instruction placePositionConditionalOrder(final PublicKey payerKey,
                                            final PublicKey traderWalletKey,
                                            final PublicKey traderAccountKey,
                                            final PublicKey perpAssetMapKey,
                                            final PublicKey orderbookKey,
                                            final PublicKey splinesKey,
                                            final PublicKey traderConditionalOrdersKey,
                                            final PlacePositionConditionalOrderInstruction params);

  Instruction placeAttachedConditionalOrder(final PublicKey payerKey,
                                            final PublicKey traderWalletKey,
                                            final PublicKey traderAccountKey,
                                            final PublicKey orderbookKey,
                                            final PublicKey traderConditionalOrdersKey,
                                            final PlaceAttachedConditionalOrderInstruction params);

  Instruction placeLimitOrderWithConditionals(final PublicKey payerKey,
                                              final PublicKey traderWalletKey,
                                              final PublicKey traderAccountKey,
                                              final PublicKey perpAssetMapKey,
                                              final PublicKey orderbookKey,
                                              final PublicKey splinesKey,
                                              final PublicKey traderConditionalOrdersKey,
                                              final PlaceLimitOrderWithConditionalsInstruction params);

  Instruction cancelConditionalOrder(final PublicKey traderWalletKey,
                                     final PublicKey traderAccountKey,
                                     final PublicKey orderbookKey,
                                     final PublicKey traderConditionalOrdersKey,
                                     final CancelConditionalOrderInstruction params);

  Instruction cancelAllPlusConditional(final PublicKey traderWalletKey,
                                       final PublicKey traderAccountKey,
                                       final PublicKey perpAssetMapKey,
                                       final PublicKey orderbookKey,
                                       final PublicKey splinesKey,
                                       final PublicKey traderConditionalOrdersKey);

  // Eternal: stop loss (user-facing)

  Instruction placeStopLoss(final PublicKey payerKey,
                            final PublicKey traderWalletKey,
                            final PublicKey traderAccountKey,
                            final PublicKey perpAssetMapKey,
                            final PublicKey orderbookKey,
                            final PublicKey splinesKey,
                            final PublicKey stopLossAccountKey,
                            final PlaceStopLossInstruction params);

  Instruction cancelStopLoss(final PublicKey funderKey,
                             final PublicKey traderWalletKey,
                             final PublicKey traderAccountKey,
                             final PublicKey stopLossAccountKey,
                             final CancelStopLossInstruction params);

  // Eternal: escrow (user-facing)

  Instruction createEscrowAccount(final PublicKey payerKey,
                                  final PublicKey traderWalletKey,
                                  final PublicKey traderEscrowKey,
                                  final CreateEscrowAccountParams params);

  Instruction createEscrowRequest(final PublicKey senderWalletKey,
                                  final PublicKey senderTraderAccountKey,
                                  final PublicKey permissionAccountKey,
                                  final PublicKey receiverWalletKey,
                                  final PublicKey receiverTraderAccountKey,
                                  final PublicKey receiverEscrowKey,
                                  final PublicKey perpAssetMapKey,
                                  final CreateEscrowRequestParams params);

  Instruction acceptEscrowRequest(final PublicKey senderWalletKey,
                                  final PublicKey senderTraderAccountKey,
                                  final PublicKey receiverWalletKey,
                                  final PublicKey receiverTraderAccountKey,
                                  final PublicKey receiverEscrowKey,
                                  final PublicKey perpAssetMapKey);

  Instruction cancelEscrowRequest(final PublicKey signerWalletKey,
                                  final PublicKey receiverWalletKey,
                                  final PublicKey receiverEscrowKey,
                                  final CancelEscrowRequestParams params);

  Instruction upsertEscrowRequest(final PublicKey senderWalletKey,
                                  final PublicKey senderTraderAccountKey,
                                  final PublicKey permissionAccountKey,
                                  final PublicKey receiverWalletKey,
                                  final PublicKey receiverTraderAccountKey,
                                  final PublicKey receiverEscrowKey,
                                  final PublicKey perpAssetMapKey,
                                  final UpsertEscrowRequestParams params);
}

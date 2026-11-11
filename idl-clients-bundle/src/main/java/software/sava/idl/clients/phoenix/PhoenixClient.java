package software.sava.idl.clients.phoenix;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.phoenix.ember.gen.types.DepositParams;
import software.sava.idl.clients.phoenix.ember.gen.types.WithdrawParams;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewAssetReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewBboReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewFundingReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewLiquidationPriceReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewMarginReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewOrderQuoteReturn;
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

  /// Requests the owner's signature so Ember can approve and burn the wrapped tokens,
  /// including when a separate account pays the transaction fee. The generated builder's
  /// nonsigner owner does not request that approval and can succeed without withdrawing.
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

  /// @param globalVaultKey the protocol's vault for the deposited mint —
  ///                       derive with [PhoenixAccounts#globalVaultPDA]. It is
  ///                       *not* the global configuration account.
  Instruction depositFunds(final PublicKey traderWalletKey,
                           final PublicKey traderTokenAccountKey,
                           final PublicKey traderAccountKey,
                           final PublicKey globalVaultKey,
                           final PublicKey tokenProgram,
                           final DepositFundsInstruction depositFundsInstruction);

  /// @param globalVaultKey the protocol's vault for the withdrawn mint —
  ///                       derive with [PhoenixAccounts#globalVaultPDA]. It is
  ///                       *not* the global configuration account.
  Instruction withdrawFunds(final PublicKey traderWalletKey,
                            final PublicKey traderAccountKey,
                            final PublicKey perpAssetMapKey,
                            final PublicKey globalVaultKey,
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

  /// Sweeps a child trader's collateral to its parent with the trader wallet unsigned, as the IDL
  /// and the TypeScript SDK pass it, which keeps the sweep open to anyone. A child whose trader
  /// preference bit 0 disables sweeps needs the wallet's signature: without it the call fails
  /// `MissingRequiredSignature`. A wallet that signs the transaction for any other reason, as fee
  /// payer or for another instruction, signs here too, since a signature belongs to the whole
  /// message. The Rust SDK always marks it a signer (`rise/rust/ix/src/transfer_collateral.rs`).
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

  // Hawkeye: views of Eternal's state, answered through return data

  /// The trader's margin, risk, collateral, PnL and funding across every position, answered as
  /// the [ViewMarginReturn] that [#readViewMargin(PublicKey, byte\[\])] reads.
  ///
  /// Hawkeye's instructions change nothing and answer only through return data, so the
  /// transaction is simulated, not sent, and the answer is the simulation's return data.
  /// Nothing signs it, and its fee payer is any account that could pay the fee. The view goes
  /// last in its transaction: every instruction that runs clears the return data of the one
  /// before it, whether or not it sets its own. Give the transaction a compute unit limit ahead
  /// of the view: Phoenix's SDKs set 1,400,000, and this view took 476,000 units for a trader
  /// with 69 positions, against a default of 200,000.
  ///
  /// Eternal's `globalTraderIndex` and `activeTraderBuffer` are each a group of accounts, a
  /// header followed by its arenas. Every view here passes the header alone, as this client
  /// does for Eternal's own instructions. That is the whole group while the header's
  /// `num_arenas` is 1, as both were on mainnet on 2026-11-11; once a group has grown, build
  /// the account list yourself and use the generated `HawkeyeProgram` builder that takes one.
  /// For an order quote that builder takes Hawkeye's own copy of the packet type, which
  /// `OrderPacketKind.read` of `hawkeye.gen.types` reads from the bytes Eternal's packet writes.
  Instruction viewMargin(final PublicKey traderAccountKey,
                         final PublicKey perpAssetMapKey);

  /// The trader's position, PnL, funding and margin on one asset, answered as the
  /// [ViewAssetReturn] that [#readViewMarginForAsset(PublicKey, byte\[\])] reads. Simulated like
  /// [#viewMargin(PublicKey, PublicKey)].
  ///
  /// @param assetId the asset's id in the perp asset map, a u32
  Instruction viewMarginForAsset(final PublicKey traderAccountKey,
                                 final PublicKey perpAssetMapKey,
                                 final long assetId);

  /// The price at which the trader's position on one asset is liquidated, answered as the
  /// [ViewLiquidationPriceReturn] that [#readViewLiquidationPrice(PublicKey, byte\[\])] reads.
  /// Simulated like [#viewMargin(PublicKey, PublicKey)].
  ///
  /// @param assetId the asset's id in the perp asset map, a u32
  Instruction viewLiquidationPrice(final PublicKey traderAccountKey,
                                   final PublicKey perpAssetMapKey,
                                   final long assetId);

  /// The trader's accumulated and unsettled funding on one asset and the asset's funding rates,
  /// answered as the [ViewFundingReturn] that [#readViewFunding(PublicKey, byte\[\])] reads.
  /// Simulated like [#viewMargin(PublicKey, PublicKey)].
  ///
  /// @param assetId the asset's id in the perp asset map, a u32
  Instruction viewFunding(final PublicKey traderAccountKey,
                          final PublicKey perpAssetMapKey,
                          final long assetId);

  /// A market's best bid and ask, counting the splines' liquidity as well as the book's, with
  /// its mark and index prices, answered as the [ViewBboReturn] that
  /// [#readViewBbo(PublicKey, byte\[\])] reads. Simulated like
  /// [#viewMargin(PublicKey, PublicKey)].
  Instruction viewBbo(final PublicKey perpAssetMapKey,
                      final PublicKey orderbookKey,
                      final PublicKey splinesKey);

  /// Creates the scratch account [#viewOrderQuote] copies Eternal's state into: no lamports,
  /// owned by Hawkeye, which closes it when the quote succeeds. It goes ahead of the quote in
  /// the same transaction; without it the quote fails `IllegalOwner`.
  ///
  /// The scratch account signs its own creation. A simulation that does not verify signatures
  /// takes any key that is not yet an account.
  ///
  /// @param space bytes to allocate. The quote copies the seven Eternal accounts it is given,
  ///              each rounded up to eight bytes, and needs room beyond them for the entries
  ///              matching adds; too little fails `AccountDataTooSmall`. For the SOL market on
  ///              mainnet on 2026-11-11 those accounts came to 2,928,704 bytes, which was too
  ///              little, and 3,059,776 was enough. The accounts grow, so leave headroom:
  ///              4,194,304 worked, and an account holds at most 10,485,760.
  Instruction createOrderQuoteScratchAccount(final PublicKey payerKey,
                                             final PublicKey scratchKey,
                                             final long space);

  /// What matching `orderPacket` against the market would fill, post and charge, answered as the
  /// [ViewOrderQuoteReturn] that [#readViewOrderQuote(PublicKey, byte\[\])] reads. Simulated like
  /// [#viewMargin(PublicKey, PublicKey)], after
  /// [#createOrderQuoteScratchAccount(PublicKey, PublicKey, long)] in the same transaction.
  ///
  /// The packet is the one the order would be placed with: Hawkeye reads the Borsh bytes
  /// [#placeLimitOrder] and [#placeMarketOrder] carry. The quote runs the matching engine and
  /// nothing else. It does not check the trader's margin or the authority to place, and a
  /// packet setting `cancelExisting` is answered as unsupported. An order the engine would
  /// reject is still a successful simulation: read the return's `outcome` (0 accepted,
  /// 1 rejected, 2 unsupported) and `rejectionReason` before its fills.
  ///
  /// @param scratchKey          the account [#createOrderQuoteScratchAccount] creates, the one
  ///                            account written
  /// @param referencePriceTicks the price slippage is measured against, fees excluded; zero
  ///                            leaves slippage out of the answer
  Instruction viewOrderQuote(final PublicKey traderAccountKey,
                             final PublicKey perpAssetMapKey,
                             final PublicKey orderbookKey,
                             final PublicKey splinesKey,
                             final PublicKey scratchKey,
                             final long referencePriceTicks,
                             final OrderPacket orderPacket);

  // Hawkeye: reading what a view returned

  /// Reads [#viewMargin]'s answer from a simulation's return data, after checking what the
  /// generated `ViewMarginReturn.read` does not: that Hawkeye is the program that returned it,
  /// that it is exactly one record long, and that it opens with the record's magic,
  /// `sha256("return:phoenix_hawkeye_margin")[0..8]`.
  ///
  /// Check the simulation's error before reading. A simulation that failed before the view ran
  /// has no return data and is refused here, but one that failed after it, as when an account an
  /// earlier instruction created is left below the rent minimum, still carries the view's answer.
  ///
  /// A simulation reports the return data of its last instruction alone, so an instruction
  /// after the view leaves nothing to read. Unsigned 64-bit fields are held in a `long`.
  ///
  /// @param returnProgramKey the program the simulation names as having returned the data
  /// @param returnData       the bytes it returned
  /// @throws IllegalStateException if there is no return data, or if any of the three checks
  ///                               fails
  ViewMarginReturn readViewMargin(final PublicKey returnProgramKey, final byte[] returnData);

  /// Reads [#viewMarginForAsset]'s answer, checked as [#readViewMargin(PublicKey, byte\[\])]
  /// checks its own. The magic is `sha256("return:phoenix_hawkeye_asset")[0..8]`.
  ViewAssetReturn readViewMarginForAsset(final PublicKey returnProgramKey, final byte[] returnData);

  /// Reads [#viewLiquidationPrice]'s answer, checked as [#readViewMargin(PublicKey, byte\[\])]
  /// checks its own. The magic is `sha256("return:phoenix_hawkeye_liquidation_price")[0..8]`.
  ViewLiquidationPriceReturn readViewLiquidationPrice(final PublicKey returnProgramKey, final byte[] returnData);

  /// Reads [#viewFunding]'s answer, checked as [#readViewMargin(PublicKey, byte\[\])] checks its
  /// own. The magic is `sha256("return:phoenix_hawkeye_funding")[0..8]`. Each total is present
  /// only when its bit of `flags` is set: bit 0 the accumulated, bit 1 the unsettled.
  ViewFundingReturn readViewFunding(final PublicKey returnProgramKey, final byte[] returnData);

  /// Reads [#viewBbo]'s answer, checked as [#readViewMargin(PublicKey, byte\[\])] checks its own.
  /// The magic is `sha256("return:phoenix_hawkeye_bbo")[0..8]`. A side is present only when its
  /// bit of `flags` is set, bit 0 the bid and bit 1 the ask; an absent side reads zero.
  ViewBboReturn readViewBbo(final PublicKey returnProgramKey, final byte[] returnData);

  /// Reads [#viewOrderQuote]'s answer, checked as [#readViewMargin(PublicKey, byte\[\])] checks
  /// its own. The magic is `sha256("return:phoenix_hawkeye_order_quote")[0..8]`. Bits 0, 1 and 2
  /// of `flags` say whether the average price, the slippage and the effective size are present,
  /// and the posted price means something only when lots were posted.
  ViewOrderQuoteReturn readViewOrderQuote(final PublicKey returnProgramKey, final byte[] returnData);
}

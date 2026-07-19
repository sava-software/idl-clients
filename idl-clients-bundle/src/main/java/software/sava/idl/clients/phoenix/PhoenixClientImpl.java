package software.sava.idl.clients.phoenix;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.phoenix.ember.gen.EmberProgram;
import software.sava.idl.clients.phoenix.ember.gen.types.DepositParams;
import software.sava.idl.clients.phoenix.ember.gen.types.WithdrawParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.EternalProgram;
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

final class PhoenixClientImpl implements PhoenixClient {

  private final SolanaAccounts solanaAccounts;
  private final PhoenixAccounts accounts;

  PhoenixClientImpl(final SolanaAccounts solanaAccounts, final PhoenixAccounts accounts) {
    this.solanaAccounts = solanaAccounts;
    this.accounts = accounts;
  }

  @Override
  public SolanaAccounts solanaAccounts() {
    return solanaAccounts;
  }

  @Override
  public PhoenixAccounts phoenixAccounts() {
    return accounts;
  }

  private PublicKey eternalProgramId() {
    return accounts.invokedEternalProgram().publicKey();
  }

  @Override
  public Instruction deposit(final PublicKey owner,
                             final PublicKey mint,
                             final PublicKey emberMint,
                             final PublicKey tokenAccount,
                             final PublicKey emberTokenAccount,
                             final PublicKey tokenProgram,
                             final DepositParams depositParams) {
    return EmberProgram.deposit(
        accounts.invokedEmberProgram(),
        owner,
        accounts.emberStateProgram(),
        mint,
        emberMint,
        tokenAccount,
        emberTokenAccount,
        accounts.emberVaultProgram(),
        tokenProgram,
        depositParams
    );
  }

  @Override
  public Instruction withdraw(final PublicKey owner,
                              final PublicKey inputMint,
                              final PublicKey outputMint,
                              final PublicKey inputTokenAccount,
                              final PublicKey outputTokenAccount,
                              final PublicKey tokenProgram,
                              final WithdrawParams withdrawParams) {
    return EmberProgram.withdraw(
        accounts.invokedEmberProgram(),
        owner,
        accounts.emberStateProgram(),
        inputMint,
        outputMint,
        inputTokenAccount,
        outputTokenAccount,
        accounts.emberVaultProgram(),
        tokenProgram,
        withdrawParams
    );
  }

  @Override
  public Instruction registerTrader(final PublicKey payerKey,
                                    final PublicKey traderWalletKey,
                                    final PublicKey traderAccountKey,
                                    final RegisterTraderParams params) {
    return EternalProgram.registerTrader(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        payerKey,
        traderWalletKey,
        traderAccountKey,
        solanaAccounts.systemProgram(),
        params
    );
  }

  @Override
  public Instruction depositFunds(final PublicKey traderWalletKey,
                                  final PublicKey traderTokenAccountKey,
                                  final PublicKey traderAccountKey,
                                  final PublicKey globalVaultKey,
                                  final PublicKey tokenProgram,
                                  final DepositFundsInstruction depositFundsInstruction) {
    return EternalProgram.depositFunds(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        traderTokenAccountKey,
        traderAccountKey,
        globalVaultKey,
        tokenProgram,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        depositFundsInstruction
    );
  }

  @Override
  public Instruction withdrawFunds(final PublicKey traderWalletKey,
                                   final PublicKey traderAccountKey,
                                   final PublicKey perpAssetMapKey,
                                   final PublicKey globalVaultKey,
                                   final PublicKey destinationTokenAccountKey,
                                   final PublicKey tokenProgramKey,
                                   final PublicKey withdrawQueueKey,
                                   final WithdrawFundsInstruction params) {
    return EternalProgram.withdrawFunds(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        traderAccountKey,
        perpAssetMapKey,
        globalVaultKey,
        destinationTokenAccountKey,
        tokenProgramKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        withdrawQueueKey,
        params
    );
  }

  @Override
  public Instruction delegateTrader(final PublicKey traderWalletKey,
                                    final PublicKey traderAccountKey,
                                    final PublicKey newPositionAuthorityKey) {
    return EternalProgram.delegateTrader(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        traderAccountKey,
        newPositionAuthorityKey
    );
  }

  @Override
  public Instruction updateTraderState(final PublicKey traderAccountKey,
                                       final PublicKey perpAssetMapKey) {
    return EternalProgram.updateTraderState(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderAccountKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer()
    );
  }

  @Override
  public Instruction transferCollateral(final PublicKey traderWalletKey,
                                        final PublicKey srcTraderAccountKey,
                                        final PublicKey dstTraderAccountKey,
                                        final PublicKey perpAssetMapKey,
                                        final TransferCollateralInstruction params) {
    return EternalProgram.transferCollateral(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        srcTraderAccountKey,
        dstTraderAccountKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        params
    );
  }

  @Override
  public Instruction transferCollateralChildToParent(final PublicKey traderWalletKey,
                                                     final PublicKey childTraderAccountKey,
                                                     final PublicKey parentTraderAccountKey,
                                                     final PublicKey perpAssetMapKey) {
    return EternalProgram.transferCollateralChildToParent(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        childTraderAccountKey,
        parentTraderAccountKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer()
    );
  }

  @Override
  public Instruction syncParentToChild(final PublicKey traderWalletKey,
                                       final PublicKey parentTraderAccountKey,
                                       final PublicKey childTraderAccountKey) {
    return EternalProgram.syncParentToChild(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        parentTraderAccountKey,
        childTraderAccountKey,
        accounts.globalTraderIndex()
    );
  }

  @Override
  public Instruction placeMarketOrder(final PublicKey traderWalletKey,
                                      final PublicKey traderAccountKey,
                                      final PublicKey perpAssetMapKey,
                                      final PublicKey orderbookKey,
                                      final PublicKey splinesKey,
                                      final OrderPacket orderPacket) {
    return EternalProgram.placeMarketOrder(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        traderAccountKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        orderbookKey,
        splinesKey,
        orderPacket
    );
  }

  @Override
  public Instruction placeLimitOrder(final PublicKey traderWalletKey,
                                     final PublicKey traderAccountKey,
                                     final PublicKey perpAssetMapKey,
                                     final PublicKey orderbookKey,
                                     final PublicKey splinesKey,
                                     final OrderPacket orderPacket) {
    return EternalProgram.placeLimitOrder(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        traderAccountKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        orderbookKey,
        splinesKey,
        orderPacket
    );
  }

  @Override
  public Instruction placeMultiLimitOrder(final PublicKey traderWalletKey,
                                          final PublicKey traderAccountKey,
                                          final PublicKey perpAssetMapKey,
                                          final PublicKey orderbookKey,
                                          final PublicKey splinesKey,
                                          final MultipleOrderPacket orderPacket) {
    return EternalProgram.placeMultiLimitOrder(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        traderAccountKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        orderbookKey,
        splinesKey,
        orderPacket
    );
  }

  @Override
  public Instruction cancelOrdersById(final PublicKey traderWalletKey,
                                      final PublicKey traderAccountKey,
                                      final PublicKey perpAssetMapKey,
                                      final PublicKey orderbookKey,
                                      final PublicKey splinesKey,
                                      final OrderIds orderIds) {
    return EternalProgram.cancelOrdersById(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        traderAccountKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        orderbookKey,
        splinesKey,
        orderIds
    );
  }

  @Override
  public Instruction cancelUpTo(final PublicKey traderWalletKey,
                                final PublicKey traderAccountKey,
                                final PublicKey perpAssetMapKey,
                                final PublicKey orderbookKey,
                                final PublicKey splinesKey,
                                final CancelUpToInstruction params) {
    return EternalProgram.cancelUpTo(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        traderAccountKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        orderbookKey,
        splinesKey,
        params
    );
  }

  @Override
  public Instruction cancelAll(final PublicKey traderWalletKey,
                               final PublicKey traderAccountKey,
                               final PublicKey perpAssetMapKey,
                               final PublicKey orderbookKey,
                               final PublicKey splinesKey) {
    return EternalProgram.cancelAll(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        traderAccountKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        orderbookKey,
        splinesKey
    );
  }

  @Override
  public Instruction createConditionalOrdersAccount(final PublicKey payerKey,
                                                    final PublicKey traderWalletKey,
                                                    final PublicKey traderAccountKey,
                                                    final PublicKey traderConditionalOrdersKey,
                                                    final CreateConditionalOrdersAccountInstruction params) {
    return EternalProgram.createConditionalOrdersAccount(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        payerKey,
        traderWalletKey,
        traderAccountKey,
        traderConditionalOrdersKey,
        solanaAccounts.systemProgram(),
        params
    );
  }

  @Override
  public Instruction placePositionConditionalOrder(final PublicKey payerKey,
                                                   final PublicKey traderWalletKey,
                                                   final PublicKey traderAccountKey,
                                                   final PublicKey perpAssetMapKey,
                                                   final PublicKey orderbookKey,
                                                   final PublicKey splinesKey,
                                                   final PublicKey traderConditionalOrdersKey,
                                                   final PlacePositionConditionalOrderInstruction params) {
    return EternalProgram.placePositionConditionalOrder(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        payerKey,
        traderAccountKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        orderbookKey,
        splinesKey,
        traderWalletKey,
        traderConditionalOrdersKey,
        solanaAccounts.systemProgram(),
        params
    );
  }

  @Override
  public Instruction placeAttachedConditionalOrder(final PublicKey payerKey,
                                                   final PublicKey traderWalletKey,
                                                   final PublicKey traderAccountKey,
                                                   final PublicKey orderbookKey,
                                                   final PublicKey traderConditionalOrdersKey,
                                                   final PlaceAttachedConditionalOrderInstruction params) {
    return EternalProgram.placeAttachedConditionalOrder(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderAccountKey,
        traderWalletKey,
        orderbookKey,
        traderConditionalOrdersKey,
        payerKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        solanaAccounts.systemProgram(),
        params
    );
  }

  @Override
  public Instruction placeLimitOrderWithConditionals(final PublicKey payerKey,
                                                     final PublicKey traderWalletKey,
                                                     final PublicKey traderAccountKey,
                                                     final PublicKey perpAssetMapKey,
                                                     final PublicKey orderbookKey,
                                                     final PublicKey splinesKey,
                                                     final PublicKey traderConditionalOrdersKey,
                                                     final PlaceLimitOrderWithConditionalsInstruction params) {
    return EternalProgram.placeLimitOrderWithConditionals(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        traderAccountKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        orderbookKey,
        splinesKey,
        payerKey,
        traderConditionalOrdersKey,
        solanaAccounts.systemProgram(),
        params
    );
  }

  @Override
  public Instruction cancelConditionalOrder(final PublicKey traderWalletKey,
                                            final PublicKey traderAccountKey,
                                            final PublicKey orderbookKey,
                                            final PublicKey traderConditionalOrdersKey,
                                            final CancelConditionalOrderInstruction params) {
    return EternalProgram.cancelConditionalOrder(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderAccountKey,
        traderWalletKey,
        orderbookKey,
        traderConditionalOrdersKey,
        params
    );
  }

  @Override
  public Instruction cancelAllPlusConditional(final PublicKey traderWalletKey,
                                              final PublicKey traderAccountKey,
                                              final PublicKey perpAssetMapKey,
                                              final PublicKey orderbookKey,
                                              final PublicKey splinesKey,
                                              final PublicKey traderConditionalOrdersKey) {
    return EternalProgram.cancelAllPlusConditional(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        traderAccountKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        orderbookKey,
        splinesKey,
        traderConditionalOrdersKey
    );
  }

  @Override
  public Instruction placeStopLoss(final PublicKey payerKey,
                                   final PublicKey traderWalletKey,
                                   final PublicKey traderAccountKey,
                                   final PublicKey perpAssetMapKey,
                                   final PublicKey orderbookKey,
                                   final PublicKey splinesKey,
                                   final PublicKey stopLossAccountKey,
                                   final PlaceStopLossInstruction params) {
    return EternalProgram.placeStopLoss(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        payerKey,
        traderAccountKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        orderbookKey,
        splinesKey,
        traderWalletKey,
        stopLossAccountKey,
        solanaAccounts.systemProgram(),
        params
    );
  }

  @Override
  public Instruction cancelStopLoss(final PublicKey funderKey,
                                    final PublicKey traderWalletKey,
                                    final PublicKey traderAccountKey,
                                    final PublicKey stopLossAccountKey,
                                    final CancelStopLossInstruction params) {
    return EternalProgram.cancelStopLoss(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        funderKey,
        traderAccountKey,
        traderWalletKey,
        stopLossAccountKey,
        solanaAccounts.systemProgram(),
        params
    );
  }

  @Override
  public Instruction createEscrowAccount(final PublicKey payerKey,
                                         final PublicKey traderWalletKey,
                                         final PublicKey traderEscrowKey,
                                         final CreateEscrowAccountParams params) {
    return EternalProgram.createEscrowAccount(
        accounts.invokedEternalProgram(),
        solanaAccounts,
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        payerKey,
        traderWalletKey,
        traderEscrowKey,
        params
    );
  }

  @Override
  public Instruction createEscrowRequest(final PublicKey senderWalletKey,
                                         final PublicKey senderTraderAccountKey,
                                         final PublicKey permissionAccountKey,
                                         final PublicKey receiverWalletKey,
                                         final PublicKey receiverTraderAccountKey,
                                         final PublicKey receiverEscrowKey,
                                         final PublicKey perpAssetMapKey,
                                         final CreateEscrowRequestParams params) {
    return EternalProgram.createEscrowRequest(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        senderWalletKey,
        senderTraderAccountKey,
        permissionAccountKey,
        receiverWalletKey,
        receiverTraderAccountKey,
        receiverEscrowKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        params
    );
  }

  @Override
  public Instruction acceptEscrowRequest(final PublicKey senderWalletKey,
                                         final PublicKey senderTraderAccountKey,
                                         final PublicKey receiverWalletKey,
                                         final PublicKey receiverTraderAccountKey,
                                         final PublicKey receiverEscrowKey,
                                         final PublicKey perpAssetMapKey) {
    return EternalProgram.acceptEscrowRequest(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        senderWalletKey,
        senderTraderAccountKey,
        receiverWalletKey,
        receiverTraderAccountKey,
        receiverEscrowKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer()
    );
  }

  @Override
  public Instruction cancelEscrowRequest(final PublicKey signerWalletKey,
                                         final PublicKey receiverWalletKey,
                                         final PublicKey receiverEscrowKey,
                                         final CancelEscrowRequestParams params) {
    return EternalProgram.cancelEscrowRequest(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        signerWalletKey,
        receiverWalletKey,
        receiverEscrowKey,
        params
    );
  }

  @Override
  public Instruction upsertEscrowRequest(final PublicKey senderWalletKey,
                                         final PublicKey senderTraderAccountKey,
                                         final PublicKey permissionAccountKey,
                                         final PublicKey receiverWalletKey,
                                         final PublicKey receiverTraderAccountKey,
                                         final PublicKey receiverEscrowKey,
                                         final PublicKey perpAssetMapKey,
                                         final UpsertEscrowRequestParams params) {
    return EternalProgram.upsertEscrowRequest(
        accounts.invokedEternalProgram(),
        eternalProgramId(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        senderWalletKey,
        senderTraderAccountKey,
        permissionAccountKey,
        receiverWalletKey,
        receiverTraderAccountKey,
        receiverEscrowKey,
        perpAssetMapKey,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        params
    );
  }
}

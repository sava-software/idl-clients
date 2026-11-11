package software.sava.idl.clients.phoenix;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.phoenix.ember.gen.EmberProgram;
import software.sava.idl.clients.phoenix.ember.gen.types.DepositParams;
import software.sava.idl.clients.phoenix.ember.gen.types.WithdrawParams;
import software.sava.idl.clients.phoenix.hawkeye.gen.HawkeyeProgram;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewAssetParams;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewAssetReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewBboReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewFundingReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewLiquidationPriceReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewMarginReturn;
import software.sava.idl.clients.phoenix.hawkeye.gen.types.ViewOrderQuoteReturn;
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
import software.sava.idl.clients.spl.system.gen.SystemProgram;

import java.util.List;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.idl.clients.phoenix.hawkeye.gen.HawkeyeProgram.ViewOrderQuoteIxData.ORDER_PACKET_OFFSET;
import static software.sava.idl.clients.phoenix.hawkeye.gen.HawkeyeProgram.ViewOrderQuoteIxData.REFERENCE_PRICE_TICKS_OFFSET;

final class PhoenixClientImpl implements PhoenixClient {

  /// What each view's return data opens with, `sha256("return:phoenix_hawkeye_<view>")[0..8]`
  /// read as a little-endian u64: the `magicU64` Hawkeye's IDL publishes for each return type.
  private static final long VIEW_MARGIN_RETURN_MAGIC = 0x955F5B9D3DFF253FL;
  private static final long VIEW_ASSET_RETURN_MAGIC = 0xDB58FFF8285E1DB9L;
  private static final long VIEW_LIQUIDATION_PRICE_RETURN_MAGIC = 0xD23D34B160EF6841L;
  private static final long VIEW_BBO_RETURN_MAGIC = 0xEFCA1FA31FA74171L;
  private static final long VIEW_FUNDING_RETURN_MAGIC = 0x32A963EEA226DC18L;
  private static final long VIEW_ORDER_QUOTE_RETURN_MAGIC = 0x1529CF1C5CB66F88L;

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
    // The IDL no longer requests the owner's signature. Preserve automatic approval,
    // as both Phoenix SDKs do, even when the owner is not the transaction's fee payer.
    return EmberProgram.withdraw(
        accounts.invokedEmberProgram(),
        List.of(
            AccountMeta.createReadOnlySigner(owner),
            AccountMeta.createRead(accounts.emberStateProgram()),
            AccountMeta.createRead(inputMint),
            AccountMeta.createWrite(outputMint),
            AccountMeta.createWrite(inputTokenAccount),
            AccountMeta.createWrite(outputTokenAccount),
            AccountMeta.createWrite(accounts.emberVaultProgram()),
            AccountMeta.createRead(tokenProgram)
        ),
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

  /// The IDL declares `traderWallet` as a non-signer, but the program's own SDK
  /// pushes it as `AccountMeta::readonly_signer`
  /// (`rise/rust/ix/src/sync_parent_to_child.rs::build_accounts`) — the wallet
  /// has to authorize moving collateral between its own trader accounts. The
  /// generated builder follows the IDL, so the meta is rebuilt here.
  ///
  /// The TypeScript SDK's other Eternal builders agree with the IDL on every
  /// signer. The Rust SDK parts from it once more, signing the wallet of
  /// `transfer_collateral_child_to_parent`, which this client leaves unsigned on
  /// purpose: see [PhoenixClient#transferCollateralChildToParent].
  @Override
  public Instruction syncParentToChild(final PublicKey traderWalletKey,
                                       final PublicKey parentTraderAccountKey,
                                       final PublicKey childTraderAccountKey) {
    return EternalProgram.syncParentToChild(
        accounts.invokedEternalProgram(),
        List.of(
            AccountMeta.createRead(eternalProgramId()),
            AccountMeta.createRead(accounts.eternalLogAuthority()),
            AccountMeta.createRead(accounts.eternalGlobalConfig()),
            AccountMeta.createReadOnlySigner(traderWalletKey),
            AccountMeta.createRead(parentTraderAccountKey),
            AccountMeta.createWrite(childTraderAccountKey),
            AccountMeta.createWrite(accounts.globalTraderIndex())
        )
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

  private PublicKey hawkeyeProgramId() {
    return accounts.invokedHawkeyeProgram().publicKey();
  }

  private static ViewAssetParams asset(final long assetId) {
    return new ViewAssetParams(assetId, new byte[ViewAssetParams.PADDING_LEN]);
  }

  @Override
  public Instruction viewMargin(final PublicKey traderAccountKey,
                                final PublicKey perpAssetMapKey) {
    return HawkeyeProgram.viewMargin(
        accounts.invokedHawkeyeProgram(),
        eternalProgramId(),
        accounts.eternalGlobalConfig(),
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        perpAssetMapKey,
        traderAccountKey
    );
  }

  @Override
  public Instruction viewMarginForAsset(final PublicKey traderAccountKey,
                                        final PublicKey perpAssetMapKey,
                                        final long assetId) {
    return HawkeyeProgram.viewMarginForAsset(
        accounts.invokedHawkeyeProgram(),
        eternalProgramId(),
        accounts.eternalGlobalConfig(),
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        perpAssetMapKey,
        traderAccountKey,
        asset(assetId)
    );
  }

  @Override
  public Instruction viewLiquidationPrice(final PublicKey traderAccountKey,
                                          final PublicKey perpAssetMapKey,
                                          final long assetId) {
    return HawkeyeProgram.viewLiquidationPrice(
        accounts.invokedHawkeyeProgram(),
        eternalProgramId(),
        accounts.eternalGlobalConfig(),
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        perpAssetMapKey,
        traderAccountKey,
        asset(assetId)
    );
  }

  @Override
  public Instruction viewFunding(final PublicKey traderAccountKey,
                                 final PublicKey perpAssetMapKey,
                                 final long assetId) {
    return HawkeyeProgram.viewFunding(
        accounts.invokedHawkeyeProgram(),
        eternalProgramId(),
        accounts.eternalGlobalConfig(),
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        perpAssetMapKey,
        traderAccountKey,
        asset(assetId)
    );
  }

  @Override
  public Instruction viewBbo(final PublicKey perpAssetMapKey,
                             final PublicKey orderbookKey,
                             final PublicKey splinesKey) {
    return HawkeyeProgram.viewBbo(
        accounts.invokedHawkeyeProgram(),
        eternalProgramId(),
        accounts.eternalGlobalConfig(),
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        perpAssetMapKey,
        orderbookKey,
        splinesKey
    );
  }

  @Override
  public Instruction createOrderQuoteScratchAccount(final PublicKey payerKey,
                                                    final PublicKey scratchKey,
                                                    final long space) {
    return SystemProgram.createAccount(
        solanaAccounts.invokedSystemProgram(),
        payerKey,
        scratchKey,
        0,
        space,
        hawkeyeProgramId()
    );
  }

  /// Hawkeye's IDL declares its own copy of Eternal's order packet, which the generated builder
  /// takes. The program reads the Borsh bytes Eternal's placement instructions carry, and
  /// Phoenix's SDK serializes one `OrderPacket` for both
  /// (`rise/rust/ix/src/hawkeye.rs::create_hawkeye_view_order_quote_ix`), so the packet a
  /// caller would place is written as it is: the discriminator, the reference price, the packet.
  @Override
  public Instruction viewOrderQuote(final PublicKey traderAccountKey,
                                    final PublicKey perpAssetMapKey,
                                    final PublicKey orderbookKey,
                                    final PublicKey splinesKey,
                                    final PublicKey scratchKey,
                                    final long referencePriceTicks,
                                    final OrderPacket orderPacket) {
    final var keys = HawkeyeProgram.viewOrderQuoteKeys(
        eternalProgramId(),
        accounts.eternalGlobalConfig(),
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        perpAssetMapKey,
        orderbookKey,
        splinesKey,
        traderAccountKey,
        scratchKey
    );
    final byte[] data = new byte[ORDER_PACKET_OFFSET + orderPacket.l()];
    HawkeyeProgram.VIEW_ORDER_QUOTE_DISCRIMINATOR.write(data, 0);
    putInt64LE(data, REFERENCE_PRICE_TICKS_OFFSET, referencePriceTicks);
    orderPacket.write(data, ORDER_PACKET_OFFSET);
    return Instruction.createInstruction(accounts.invokedHawkeyeProgram(), keys, data);
  }

  /// The return data of `view`, once it is known to be Hawkeye's and to be one whole record of
  /// the expected kind.
  private byte[] returned(final String view,
                          final PublicKey returnProgramKey,
                          final byte[] returnData,
                          final int bytes,
                          final long magic) {
    if (returnProgramKey == null || returnData == null) {
      throw new IllegalStateException(view + " returned no data.");
    }
    final var hawkeyeProgram = hawkeyeProgramId();
    if (!hawkeyeProgram.equals(returnProgramKey)) {
      throw new IllegalStateException(String.format(
          "%s expects return data from %s, not %s.", view, hawkeyeProgram, returnProgramKey
      ));
    }
    if (returnData.length != bytes) {
      throw new IllegalStateException(String.format(
          "%s returns %d bytes, not %d.", view, bytes, returnData.length
      ));
    }
    final long returnedMagic = getInt64LE(returnData, 0);
    if (returnedMagic != magic) {
      throw new IllegalStateException(String.format(
          "%s return data opens with magic %s, not %s.",
          view, Long.toUnsignedString(magic), Long.toUnsignedString(returnedMagic)
      ));
    }
    return returnData;
  }

  @Override
  public ViewMarginReturn readViewMargin(final PublicKey returnProgramKey, final byte[] returnData) {
    return ViewMarginReturn.read(
        returned("viewMargin", returnProgramKey, returnData, ViewMarginReturn.BYTES, VIEW_MARGIN_RETURN_MAGIC), 0
    );
  }

  @Override
  public ViewAssetReturn readViewMarginForAsset(final PublicKey returnProgramKey, final byte[] returnData) {
    return ViewAssetReturn.read(
        returned("viewMarginForAsset", returnProgramKey, returnData, ViewAssetReturn.BYTES, VIEW_ASSET_RETURN_MAGIC), 0
    );
  }

  @Override
  public ViewLiquidationPriceReturn readViewLiquidationPrice(final PublicKey returnProgramKey, final byte[] returnData) {
    return ViewLiquidationPriceReturn.read(
        returned("viewLiquidationPrice", returnProgramKey, returnData, ViewLiquidationPriceReturn.BYTES, VIEW_LIQUIDATION_PRICE_RETURN_MAGIC), 0
    );
  }

  @Override
  public ViewFundingReturn readViewFunding(final PublicKey returnProgramKey, final byte[] returnData) {
    return ViewFundingReturn.read(
        returned("viewFunding", returnProgramKey, returnData, ViewFundingReturn.BYTES, VIEW_FUNDING_RETURN_MAGIC), 0
    );
  }

  @Override
  public ViewBboReturn readViewBbo(final PublicKey returnProgramKey, final byte[] returnData) {
    return ViewBboReturn.read(
        returned("viewBbo", returnProgramKey, returnData, ViewBboReturn.BYTES, VIEW_BBO_RETURN_MAGIC), 0
    );
  }

  @Override
  public ViewOrderQuoteReturn readViewOrderQuote(final PublicKey returnProgramKey, final byte[] returnData) {
    return ViewOrderQuoteReturn.read(
        returned("viewOrderQuote", returnProgramKey, returnData, ViewOrderQuoteReturn.BYTES, VIEW_ORDER_QUOTE_RETURN_MAGIC), 0
    );
  }
}

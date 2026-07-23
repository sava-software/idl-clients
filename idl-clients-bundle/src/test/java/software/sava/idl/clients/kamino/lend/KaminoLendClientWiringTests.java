package software.sava.idl.clients.kamino.lend;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.kamino.farms.gen.FarmsProgram;
import software.sava.idl.clients.kamino.lend.gen.KaminoLendingProgram;
import software.sava.idl.clients.kamino.lend.gen.types.InitObligationArgs;
import software.sava.idl.clients.kamino.lend.gen.types.ObligationOrder;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.spl.SPLAccountClient;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// Pins the remaining KaminoLendClient builders to the generated
/// `KaminoLendingProgram`/`FarmsProgram` calls they delegate to, with the
/// client-supplied values (owner, fee payer, the owner's user-metadata PDA,
/// the wired program ids and sysvars) spelled out on the mirror side. Reserve
/// PDAs ride through `KaminoReservePDAs`, so a builder reaching for the wrong
/// component (the withdrawReferrerFees bug's shape) changes the compared list.
final class KaminoLendClientWiringTests {

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final KaminoAccounts KAMINO_ACCOUNTS = KaminoAccounts.MAIN_NET;
  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey FEE_PAYER = key(0x12);
  private static final SPLAccountClient ACCOUNT_CLIENT =
      SPLAccountClient.createClient(SOLANA_ACCOUNTS, OWNER, AccountMeta.createFeePayer(FEE_PAYER));
  private static final KaminoLendClient CLIENT =
      KaminoLendClient.createClient(ACCOUNT_CLIENT, KAMINO_ACCOUNTS);

  private static final PublicKey MARKET = key(0x13);
  private static final PublicKey RESERVE = key(0x14);
  private static final PublicKey OBLIGATION = key(0x15);
  private static final PublicKey MINT = key(0x16);
  private static final PublicKey REFERRER = key(0x17);
  private static final PublicKey REFERRER_TOKEN_STATE = key(0x18);
  private static final PublicKey REFERRER_STATE = key(0x19);
  private static final PublicKey SHORT_URL = key(0x1A);
  private static final PublicKey REFERRER_METADATA = key(0x1B);
  private static final PublicKey LOOKUP_TABLE = key(0x1C);
  private static final PublicKey SOURCE_TOKEN_ACCOUNT = key(0x1D);
  private static final PublicKey DESTINATION_TOKEN_ACCOUNT = key(0x1E);
  private static final PublicKey OBLIGATION_FARM = key(0x1F);
  private static final PublicKey RESERVE_FARM_STATE = key(0x20);
  private static final PublicKey RESERVE_COLLATERAL_ACCOUNT = key(0x21);
  private static final PublicKey NEW_OWNER = key(0x22);
  private static final PublicKey USER_STATE = key(0x23);
  private static final PublicKey FARM_STATE = key(0x24);
  private static final PublicKey USER_ATA = key(0x25);
  private static final PublicKey FARM_VAULT = key(0x26);
  private static final PublicKey FARM_VAULTS_AUTHORITY = key(0x27);
  private static final PublicKey TOKEN_PROGRAM = key(0x28);

  private static final AccountMeta INVOKED = KAMINO_ACCOUNTS.invokedKLendProgram();
  private static final PublicKey K_LEND = KAMINO_ACCOUNTS.kLendProgram();
  private static final PublicKey FARMS = KAMINO_ACCOUNTS.farmProgram();
  private static final PublicKey RENT = SOLANA_ACCOUNTS.rentSysVar();
  private static final PublicKey SYSTEM = SOLANA_ACCOUNTS.systemProgram();
  private static final PublicKey IX_SYSVAR = SOLANA_ACCOUNTS.instructionsSysVar();
  private static final PublicKey OWNER_METADATA =
      KaminoAccounts.userMetadataPda(OWNER, K_LEND).publicKey();

  private static final KaminoReservePDAs RESERVE_PDAS = KAMINO_ACCOUNTS.createReservePDAs(
      KaminoMarketPDAs.createPDAs(K_LEND, MARKET), MINT, SOLANA_ACCOUNTS.tokenProgram());

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
  void referrerAndUserSetupBindings() {
    assertEquals(OWNER, CLIENT.user());

    assertIx(
        KaminoLendingProgram.initReferrerTokenState(INVOKED, FEE_PAYER, MARKET, RESERVE,
            REFERRER, REFERRER_TOKEN_STATE, RENT, SYSTEM),
        CLIENT.initReferrerTokenState(MARKET, RESERVE, REFERRER, REFERRER_TOKEN_STATE));
    assertIx(
        KaminoLendingProgram.initReferrerStateAndShortUrl(INVOKED, REFERRER, REFERRER_STATE,
            SHORT_URL, REFERRER_METADATA, RENT, SYSTEM, "ref"),
        CLIENT.initReferrerStateAndShortUrl(REFERRER, REFERRER_STATE, SHORT_URL, REFERRER_METADATA, "ref"));
    assertIx(
        KaminoLendingProgram.deleteReferrerStateAndShortUrl(INVOKED, REFERRER, REFERRER_STATE,
            SHORT_URL, RENT, SYSTEM),
        CLIENT.deleteReferrerStateAndShortUrl(REFERRER, REFERRER_STATE, SHORT_URL));
    assertIx(
        KaminoLendingProgram.initUserMetadata(INVOKED, OWNER, FEE_PAYER, OWNER_METADATA,
            REFERRER_METADATA, RENT, SYSTEM, LOOKUP_TABLE),
        CLIENT.initUserMetadata(REFERRER_METADATA, LOOKUP_TABLE));
  }

  @Test
  void obligationLifecycleBindings() {
    assertIx(
        KaminoLendingProgram.initObligation(INVOKED, OWNER, FEE_PAYER, OBLIGATION, MARKET,
            SYSTEM, SYSTEM, OWNER_METADATA, RENT, SYSTEM, new InitObligationArgs(0, 1)),
        CLIENT.initObligation(MARKET, OBLIGATION, new InitObligationArgs(0, 1)));

    // the Reserve overload reads the address and collateral farm off the account
    assertIx(
        KaminoLendingProgram.initObligationFarmsForReserve(INVOKED, FEE_PAYER, OWNER, OBLIGATION,
            RESERVE_PDAS.marketAuthority(), RESERVE, RESERVE_FARM_STATE, OBLIGATION_FARM,
            RESERVE_PDAS.market(), FARMS, RENT, SYSTEM, 1),
        CLIENT.initObligationFarmsForReserve(reserve(), RESERVE_PDAS, OBLIGATION, OBLIGATION_FARM, 1));

    assertIx(
        KaminoLendingProgram.refreshReservesBatch(INVOKED, true),
        CLIENT.refreshReservesBatch(true));
    assertIx(
        KaminoLendingProgram.refreshObligation(INVOKED, MARKET, OBLIGATION),
        CLIENT.refreshObligation(MARKET, OBLIGATION));
    assertIx(
        KaminoLendingProgram.refreshObligationFarmsForReserve(INVOKED, OWNER, OBLIGATION,
            KAMINO_ACCOUNTS.lendingMarketAuthPda(MARKET).publicKey(), RESERVE, RESERVE_FARM_STATE,
            OBLIGATION_FARM, MARKET, FARMS, RENT, SYSTEM, 1),
        CLIENT.refreshObligationFarmsForReserve(OBLIGATION, RESERVE, RESERVE_FARM_STATE,
            OBLIGATION_FARM, MARKET, 1));

    assertIx(
        KaminoLendingProgram.requestElevationGroup(INVOKED, OWNER, OBLIGATION, MARKET, 2),
        CLIENT.requestElevationGroup(OBLIGATION, MARKET, 2));
    final var padding2 = new BigInteger[ObligationOrder.PADDING_2_LEN];
    Arrays.fill(padding2, BigInteger.ZERO);
    final var order = new ObligationOrder(BigInteger.ONE, BigInteger.TWO, 1, 2, 3, 4,
        new byte[ObligationOrder.PADDING_1_LEN], padding2);
    assertIx(
        KaminoLendingProgram.setObligationOrder(INVOKED, OWNER, OBLIGATION, MARKET, 1, order),
        CLIENT.setObligationOrder(OBLIGATION, MARKET, 1, order));

    assertIx(
        KaminoLendingProgram.initiateObligationOwnershipTransfer(INVOKED, OWNER, OBLIGATION,
            IX_SYSVAR, NEW_OWNER),
        CLIENT.initiateObligationOwnershipTransfer(OBLIGATION, NEW_OWNER));
    assertIx(
        KaminoLendingProgram.acceptObligationOwnership(INVOKED, OWNER, OBLIGATION, IX_SYSVAR),
        CLIENT.acceptObligationOwnership(OBLIGATION));
    assertIx(
        KaminoLendingProgram.abortObligationOwnershipTransfer(INVOKED, OWNER, OBLIGATION, IX_SYSVAR),
        CLIENT.abortObligationOwnershipTransfer(OBLIGATION));
  }

  @Test
  void depositWithdrawAndRedeemBindings() {
    assertIx(
        KaminoLendingProgram.depositReserveLiquidityAndObligationCollateral(INVOKED, OWNER,
            OBLIGATION, RESERVE_PDAS.market(), RESERVE_PDAS.marketAuthority(), RESERVE,
            RESERVE_PDAS.mint(), RESERVE_PDAS.liquiditySupplyVault(), RESERVE_PDAS.collateralMint(),
            RESERVE_PDAS.collateralSupplyVault(), SOURCE_TOKEN_ACCOUNT, K_LEND,
            RESERVE_PDAS.tokenProgram(), RESERVE_PDAS.tokenProgram(), IX_SYSVAR, 1_000L),
        CLIENT.depositReserveLiquidityAndObligationCollateral(OBLIGATION, RESERVE, RESERVE_PDAS,
            SOURCE_TOKEN_ACCOUNT, 1_000L));
    assertIx(
        KaminoLendingProgram.depositReserveLiquidityAndObligationCollateralV2(INVOKED, OWNER,
            OBLIGATION, RESERVE_PDAS.market(), RESERVE_PDAS.marketAuthority(), RESERVE,
            RESERVE_PDAS.mint(), RESERVE_PDAS.liquiditySupplyVault(), RESERVE_PDAS.collateralMint(),
            RESERVE_PDAS.collateralSupplyVault(), SOURCE_TOKEN_ACCOUNT, K_LEND,
            RESERVE_PDAS.tokenProgram(), RESERVE_PDAS.tokenProgram(), IX_SYSVAR,
            OBLIGATION_FARM, RESERVE_FARM_STATE, FARMS, 1_000L),
        CLIENT.depositReserveLiquidityAndObligationCollateralV2(OBLIGATION, RESERVE, RESERVE_PDAS,
            SOURCE_TOKEN_ACCOUNT, OBLIGATION_FARM, RESERVE_FARM_STATE, 1_000L));
    assertIx(
        KaminoLendingProgram.withdrawObligationCollateralAndRedeemReserveCollateralV2(INVOKED, OWNER,
            OBLIGATION, RESERVE_PDAS.market(), RESERVE_PDAS.marketAuthority(), RESERVE,
            RESERVE_PDAS.mint(), RESERVE_COLLATERAL_ACCOUNT, RESERVE_PDAS.collateralMint(),
            RESERVE_PDAS.liquiditySupplyVault(), DESTINATION_TOKEN_ACCOUNT, K_LEND,
            RESERVE_PDAS.tokenProgram(), RESERVE_PDAS.tokenProgram(), IX_SYSVAR,
            OBLIGATION_FARM, RESERVE_FARM_STATE, FARMS, 500L),
        CLIENT.withdrawObligationCollateralAndRedeemReserveCollateralV2(OBLIGATION, RESERVE,
            RESERVE_COLLATERAL_ACCOUNT, RESERVE_PDAS, DESTINATION_TOKEN_ACCOUNT,
            OBLIGATION_FARM, RESERVE_FARM_STATE, 500L));
    assertIx(
        KaminoLendingProgram.depositReserveLiquidity(INVOKED, OWNER, RESERVE,
            RESERVE_PDAS.market(), RESERVE_PDAS.marketAuthority(), RESERVE_PDAS.mint(),
            RESERVE_PDAS.liquiditySupplyVault(), RESERVE_PDAS.collateralMint(),
            SOURCE_TOKEN_ACCOUNT, DESTINATION_TOKEN_ACCOUNT,
            RESERVE_PDAS.tokenProgram(), RESERVE_PDAS.tokenProgram(), IX_SYSVAR, 1_000L),
        CLIENT.depositReserveLiquidity(RESERVE, RESERVE_PDAS, SOURCE_TOKEN_ACCOUNT,
            DESTINATION_TOKEN_ACCOUNT, 1_000L));
    // redeem swaps the (market, reserve) leading pair relative to deposit
    assertIx(
        KaminoLendingProgram.redeemReserveCollateral(INVOKED, OWNER, RESERVE_PDAS.market(),
            RESERVE, RESERVE_PDAS.marketAuthority(), RESERVE_PDAS.mint(),
            RESERVE_PDAS.collateralMint(), RESERVE_PDAS.liquiditySupplyVault(),
            SOURCE_TOKEN_ACCOUNT, DESTINATION_TOKEN_ACCOUNT,
            RESERVE_PDAS.tokenProgram(), RESERVE_PDAS.tokenProgram(), IX_SYSVAR, 500L),
        CLIENT.redeemReserveCollateral(RESERVE, RESERVE_PDAS, SOURCE_TOKEN_ACCOUNT,
            DESTINATION_TOKEN_ACCOUNT, 500L));

    assertIx(
        KaminoLendingProgram.depositObligationCollateral(INVOKED, OWNER, OBLIGATION,
            RESERVE_PDAS.market(), RESERVE, RESERVE_COLLATERAL_ACCOUNT, SOURCE_TOKEN_ACCOUNT,
            RESERVE_PDAS.tokenProgram(), IX_SYSVAR, 500L),
        CLIENT.depositObligationCollateral(OBLIGATION, RESERVE, RESERVE_PDAS,
            RESERVE_COLLATERAL_ACCOUNT, SOURCE_TOKEN_ACCOUNT, 500L));
    assertIx(
        KaminoLendingProgram.depositObligationCollateralV2(INVOKED, OWNER, OBLIGATION,
            RESERVE_PDAS.market(), RESERVE, RESERVE_COLLATERAL_ACCOUNT, SOURCE_TOKEN_ACCOUNT,
            RESERVE_PDAS.tokenProgram(), IX_SYSVAR, RESERVE_PDAS.marketAuthority(),
            OBLIGATION_FARM, RESERVE_FARM_STATE, FARMS, 500L),
        CLIENT.depositObligationCollateralV2(OBLIGATION, RESERVE, RESERVE_PDAS,
            RESERVE_COLLATERAL_ACCOUNT, SOURCE_TOKEN_ACCOUNT, OBLIGATION_FARM,
            RESERVE_FARM_STATE, 500L));
    assertIx(
        KaminoLendingProgram.withdrawObligationCollateral(INVOKED, OWNER, OBLIGATION,
            RESERVE_PDAS.market(), RESERVE_PDAS.marketAuthority(), RESERVE,
            RESERVE_COLLATERAL_ACCOUNT, DESTINATION_TOKEN_ACCOUNT,
            RESERVE_PDAS.tokenProgram(), IX_SYSVAR, 500L),
        CLIENT.withdrawObligationCollateral(OBLIGATION, RESERVE, RESERVE_PDAS,
            RESERVE_COLLATERAL_ACCOUNT, DESTINATION_TOKEN_ACCOUNT, 500L));
    assertIx(
        KaminoLendingProgram.withdrawObligationCollateralV2(INVOKED, OWNER, OBLIGATION,
            RESERVE_PDAS.market(), RESERVE_PDAS.marketAuthority(), RESERVE,
            RESERVE_COLLATERAL_ACCOUNT, DESTINATION_TOKEN_ACCOUNT,
            RESERVE_PDAS.tokenProgram(), IX_SYSVAR,
            OBLIGATION_FARM, RESERVE_FARM_STATE, FARMS, 500L),
        CLIENT.withdrawObligationCollateralV2(OBLIGATION, RESERVE, RESERVE_PDAS,
            RESERVE_COLLATERAL_ACCOUNT, DESTINATION_TOKEN_ACCOUNT, OBLIGATION_FARM,
            RESERVE_FARM_STATE, 500L));
    assertIx(
        KaminoLendingProgram.withdrawObligationCollateralAndRedeemReserveCollateral(INVOKED, OWNER,
            OBLIGATION, RESERVE_PDAS.market(), RESERVE_PDAS.marketAuthority(), RESERVE,
            RESERVE_PDAS.mint(), RESERVE_COLLATERAL_ACCOUNT, RESERVE_PDAS.collateralMint(),
            RESERVE_PDAS.liquiditySupplyVault(), DESTINATION_TOKEN_ACCOUNT, K_LEND,
            RESERVE_PDAS.tokenProgram(), RESERVE_PDAS.tokenProgram(), IX_SYSVAR, 500L),
        CLIENT.withdrawObligationCollateralAndRedeemReserveCollateral(OBLIGATION, RESERVE,
            RESERVE_COLLATERAL_ACCOUNT, RESERVE_PDAS, DESTINATION_TOKEN_ACCOUNT, 500L));

    assertIx(
        KaminoLendingProgram.repayObligationLiquidityV2(INVOKED, OWNER, OBLIGATION, MARKET,
            RESERVE, MINT, DESTINATION_TOKEN_ACCOUNT, SOURCE_TOKEN_ACCOUNT, TOKEN_PROGRAM,
            IX_SYSVAR, OBLIGATION_FARM, RESERVE_FARM_STATE,
            KAMINO_ACCOUNTS.lendingMarketAuthPda(MARKET).publicKey(), FARMS, 500L),
        CLIENT.repayObligationLiquidityV2(OBLIGATION, MARKET, RESERVE, MINT,
            DESTINATION_TOKEN_ACCOUNT, SOURCE_TOKEN_ACCOUNT, TOKEN_PROGRAM,
            OBLIGATION_FARM, RESERVE_FARM_STATE, 500L));
  }

  @Test
  void farmWithdrawBinding() {
    assertIx(
        FarmsProgram.withdrawUnstakedDeposits(KAMINO_ACCOUNTS.invokedFarmsProgram(), OWNER,
            USER_STATE, FARM_STATE, USER_ATA, FARM_VAULT, FARM_VAULTS_AUTHORITY, TOKEN_PROGRAM),
        CLIENT.withdrawUnstakedFarmDeposits(USER_STATE, FARM_STATE, USER_ATA, FARM_VAULT,
            FARM_VAULTS_AUTHORITY, TOKEN_PROGRAM));
  }

  /// Only the fields the Reserve overload reads are populated.
  private static Reserve reserve() {
    return new Reserve(RESERVE, null, 0L, null, MARKET, RESERVE_FARM_STATE, null, null, null,
        null, null, null, null, 0L, null, null, null);
  }
}

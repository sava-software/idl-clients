package software.sava.idl.clients.marinade.stake_pool;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.marinade.stake_pool.gen.MarinadeFinanceProgram;
import software.sava.idl.clients.marinade.stake_pool.gen.types.FeeCents;
import software.sava.idl.clients.marinade.stake_pool.gen.types.List;
import software.sava.idl.clients.marinade.stake_pool.gen.types.StakeSystem;
import software.sava.idl.clients.marinade.stake_pool.gen.types.State;
import software.sava.idl.clients.marinade.stake_pool.gen.types.ValidatorSystem;
import software.sava.idl.clients.spl.SPLAccountClient;
import software.sava.idl.clients.spl.associated_token.gen.AssociatedTokenPDAs;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// Pins every instruction builder on the Marinade client to the generated
/// `MarinadeFinanceProgram` call it must delegate to, with the client-supplied
/// defaults (owner as authority, fee payer as rent payer, the MAIN_NET account
/// constants) spelled out on the mirror side — the generated builder's
/// parameter names are the reference for each slot. Every caller-supplied
/// account is a distinct fill-byte key, so a transposed pair of same-typed
/// keys changes the compared list. The `default` overloads are then checked
/// against the explicit calls they delegate to: the property that matters is
/// *which* identity fills each omitted slot (owner vs fee payer), and which
/// `State` field feeds each list account.
final class MarinadeProgramClientWiringTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final MarinadeAccounts MARINADE_ACCOUNTS = MarinadeAccounts.MAIN_NET;

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey FEE_PAYER = key(0x12);

  private static final PublicKey MSOL_TOKEN_ACCOUNT = key(0x21);
  private static final PublicKey VALIDATOR_LIST = key(0x22);
  private static final PublicKey STAKE_LIST = key(0x23);
  private static final PublicKey STAKE_ACCOUNT = key(0x24);
  private static final PublicKey DUPLICATION_FLAG = key(0x25);
  private static final PublicKey RENT_PAYER = key(0x26);
  private static final PublicKey SOL_RECEIVER = key(0x27);
  private static final PublicKey NEW_TICKET = key(0x28);
  private static final PublicKey SOL_SOURCE = key(0x29);
  private static final PublicKey LP_TOKEN_ACCOUNT = key(0x2A);
  private static final PublicKey MSOL_RECEIVER = key(0x2B);
  private static final PublicKey STAKE_WITHDRAW_AUTH = key(0x2C);
  private static final PublicKey STAKE_DEPOSIT_AUTH = key(0x2D);
  private static final PublicKey SPLIT_STAKE = key(0x2E);
  private static final PublicKey SPLIT_STAKE_RENT_PAYER = key(0x2F);
  private static final PublicKey BENEFICIARY = key(0x30);
  private static final PublicKey VALIDATOR = key(0x31);

  private static final AccountMeta INVOKED = MARINADE_ACCOUNTS.invokedMarinadeProgram();

  private static final MarinadeProgramClient CLIENT = MarinadeProgramClient.createClient(
      SPLAccountClient.createClient(SOLANA_ACCOUNTS, OWNER, AccountMeta.createFeePayer(FEE_PAYER))
  );

  /// A synthetic `State` carrying only the two list account keys the
  /// State-sourced overloads must read — distinct keys, so a swap between the
  /// validator and stake list slots changes the compared account list.
  private static State stateWithLists(final PublicKey validatorListKey, final PublicKey stakeListKey) {
    final byte[] data = new byte[State.DELINQUENT_UPGRADER_OFFSET + 1 + (2 * FeeCents.BYTES)];
    State.DISCRIMINATOR.write(data, 0);
    data[State.DELINQUENT_UPGRADER_OFFSET] = 2; // Done — a unit variant
    validatorListKey.write(data, State.VALIDATOR_SYSTEM_OFFSET + ValidatorSystem.VALIDATOR_LIST_OFFSET + List.ACCOUNT_OFFSET);
    stakeListKey.write(data, State.STAKE_SYSTEM_OFFSET + StakeSystem.STAKE_LIST_OFFSET + List.ACCOUNT_OFFSET);
    return State.read(data, 0);
  }

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
  void marinadeDepositBindsTheGeneratedBuilder() {
    assertIx(
        MarinadeFinanceProgram.deposit(
            INVOKED,
            MARINADE_ACCOUNTS.stateAccount(),
            MARINADE_ACCOUNTS.mSolTokenMint(),
            MARINADE_ACCOUNTS.liquidityPoolSolLegAccount(),
            MARINADE_ACCOUNTS.liquidityPoolMSolLegAccount(),
            MARINADE_ACCOUNTS.liquidityPoolMSolLegAuthority(),
            MARINADE_ACCOUNTS.treasuryReserveSolPDA(),
            OWNER,
            MSOL_TOKEN_ACCOUNT,
            MARINADE_ACCOUNTS.mSolTokenMintAuthorityPDA(),
            SOLANA_ACCOUNTS.systemProgram(),
            SOLANA_ACCOUNTS.tokenProgram(),
            42L
        ),
        CLIENT.marinadeDeposit(MSOL_TOKEN_ACCOUNT, 42L)
    );
  }

  @Test
  void depositStakeAccountBindsTheGeneratedBuilder() {
    assertIx(
        MarinadeFinanceProgram.depositStakeAccount(
            INVOKED,
            MARINADE_ACCOUNTS.stateAccount(),
            VALIDATOR_LIST,
            STAKE_LIST,
            STAKE_ACCOUNT,
            OWNER,
            DUPLICATION_FLAG,
            RENT_PAYER,
            MARINADE_ACCOUNTS.mSolTokenMint(),
            MSOL_TOKEN_ACCOUNT,
            MARINADE_ACCOUNTS.mSolTokenMintAuthorityPDA(),
            SOLANA_ACCOUNTS.clockSysVar(),
            SOLANA_ACCOUNTS.rentSysVar(),
            SOLANA_ACCOUNTS.systemProgram(),
            SOLANA_ACCOUNTS.tokenProgram(),
            SOLANA_ACCOUNTS.stakeProgram(),
            7
        ),
        CLIENT.depositStakeAccount(
            VALIDATOR_LIST, STAKE_LIST, STAKE_ACCOUNT, DUPLICATION_FLAG, RENT_PAYER, MSOL_TOKEN_ACCOUNT, 7
        )
    );
  }

  /// The rent-payer-less overload substitutes the client's fee payer — not the
  /// owner — into the rent payer slot.
  @Test
  void depositStakeAccountDefaultsRentPayerToFeePayer() {
    assertIx(
        CLIENT.depositStakeAccount(
            VALIDATOR_LIST, STAKE_LIST, STAKE_ACCOUNT, DUPLICATION_FLAG, FEE_PAYER, MSOL_TOKEN_ACCOUNT, 7
        ),
        CLIENT.depositStakeAccount(
            VALIDATOR_LIST, STAKE_LIST, STAKE_ACCOUNT, DUPLICATION_FLAG, MSOL_TOKEN_ACCOUNT, 7
        )
    );
  }

  /// The `State`-sourced overload reads the validator list from the validator
  /// system and the stake list from the stake system — planted as distinct
  /// keys so a swap is visible — and derives the duplication flag from the
  /// validator key.
  @Test
  void depositStakeAccountReadsListsFromState() {
    final var state = stateWithLists(VALIDATOR_LIST, STAKE_LIST);
    assertIx(
        CLIENT.depositStakeAccount(
            VALIDATOR_LIST,
            STAKE_LIST,
            STAKE_ACCOUNT,
            CLIENT.findDuplicationKey(VALIDATOR).publicKey(),
            FEE_PAYER,
            MSOL_TOKEN_ACCOUNT,
            7
        ),
        CLIENT.depositStakeAccount(state, STAKE_ACCOUNT, MSOL_TOKEN_ACCOUNT, VALIDATOR, 7)
    );
  }

  @Test
  void withdrawStakeAccountBindsTheGeneratedBuilder() {
    assertIx(
        MarinadeFinanceProgram.withdrawStakeAccount(
            INVOKED,
            MARINADE_ACCOUNTS.stateAccount(),
            MARINADE_ACCOUNTS.mSolTokenMint(),
            MSOL_TOKEN_ACCOUNT,
            OWNER,
            MARINADE_ACCOUNTS.treasuryMSolAccount(),
            VALIDATOR_LIST,
            STAKE_LIST,
            STAKE_WITHDRAW_AUTH,
            STAKE_DEPOSIT_AUTH,
            STAKE_ACCOUNT,
            SPLIT_STAKE,
            SPLIT_STAKE_RENT_PAYER,
            SOLANA_ACCOUNTS.clockSysVar(),
            SOLANA_ACCOUNTS.systemProgram(),
            SOLANA_ACCOUNTS.tokenProgram(),
            SOLANA_ACCOUNTS.stakeProgram(),
            3,
            7,
            42L,
            BENEFICIARY
        ),
        CLIENT.withdrawStakeAccount(
            MSOL_TOKEN_ACCOUNT,
            VALIDATOR_LIST,
            STAKE_LIST,
            STAKE_WITHDRAW_AUTH,
            STAKE_DEPOSIT_AUTH,
            STAKE_ACCOUNT,
            SPLIT_STAKE,
            SPLIT_STAKE_RENT_PAYER,
            BENEFICIARY,
            3,
            7,
            42L
        )
    );
  }

  /// The short overloads fill the split-stake rent payer with the *fee payer*
  /// and the beneficiary with the *owner* — two different identities here, so
  /// a swap between those two slots is visible.
  @Test
  void withdrawStakeAccountOverloadsDelegate() {
    final var explicit = CLIENT.withdrawStakeAccount(
        MSOL_TOKEN_ACCOUNT,
        VALIDATOR_LIST,
        STAKE_LIST,
        STAKE_WITHDRAW_AUTH,
        STAKE_DEPOSIT_AUTH,
        STAKE_ACCOUNT,
        SPLIT_STAKE,
        FEE_PAYER,
        OWNER,
        3,
        7,
        42L
    );
    assertIx(
        explicit,
        CLIENT.withdrawStakeAccount(
            MSOL_TOKEN_ACCOUNT,
            VALIDATOR_LIST,
            STAKE_LIST,
            STAKE_WITHDRAW_AUTH,
            STAKE_DEPOSIT_AUTH,
            STAKE_ACCOUNT,
            SPLIT_STAKE,
            3,
            7,
            42L
        )
    );

    // the State-sourced overloads additionally bind the stored withdraw and
    // deposit authorities — distinct PDAs, asserted by slot via the mirror
    final var state = stateWithLists(VALIDATOR_LIST, STAKE_LIST);
    final var fromState = CLIENT.withdrawStakeAccount(
        MSOL_TOKEN_ACCOUNT,
        VALIDATOR_LIST,
        STAKE_LIST,
        MARINADE_ACCOUNTS.stakeWithdrawAuthority(),
        MARINADE_ACCOUNTS.stakeDepositAuthority(),
        STAKE_ACCOUNT,
        SPLIT_STAKE,
        FEE_PAYER,
        OWNER,
        3,
        7,
        42L
    );
    assertIx(fromState, CLIENT.withdrawStakeAccount(state, MSOL_TOKEN_ACCOUNT, STAKE_ACCOUNT, SPLIT_STAKE, 3, 7, 42L));
    assertIx(
        CLIENT.withdrawStakeAccount(
            MSOL_TOKEN_ACCOUNT,
            VALIDATOR_LIST,
            STAKE_LIST,
            MARINADE_ACCOUNTS.stakeWithdrawAuthority(),
            MARINADE_ACCOUNTS.stakeDepositAuthority(),
            STAKE_ACCOUNT,
            SPLIT_STAKE,
            SPLIT_STAKE_RENT_PAYER,
            BENEFICIARY,
            3,
            7,
            42L
        ),
        CLIENT.withdrawStakeAccount(
            state, MSOL_TOKEN_ACCOUNT, STAKE_ACCOUNT, SPLIT_STAKE, SPLIT_STAKE_RENT_PAYER, BENEFICIARY, 3, 7, 42L
        )
    );
  }

  @Test
  void liquidUnstakeBindsTheGeneratedBuilder() {
    assertIx(
        MarinadeFinanceProgram.liquidUnstake(
            INVOKED,
            MARINADE_ACCOUNTS.stateAccount(),
            MARINADE_ACCOUNTS.mSolTokenMint(),
            MARINADE_ACCOUNTS.liquidityPoolSolLegAccount(),
            MARINADE_ACCOUNTS.liquidityPoolMSolLegAccount(),
            MARINADE_ACCOUNTS.treasuryMSolAccount(),
            MSOL_TOKEN_ACCOUNT,
            OWNER,
            SOL_RECEIVER,
            SOLANA_ACCOUNTS.systemProgram(),
            SOLANA_ACCOUNTS.tokenProgram(),
            42L
        ),
        CLIENT.liquidUnstake(MSOL_TOKEN_ACCOUNT, SOL_RECEIVER, 42L)
    );
    // the short overload sends the unstaked SOL to the owner
    assertIx(
        CLIENT.liquidUnstake(MSOL_TOKEN_ACCOUNT, OWNER, 42L),
        CLIENT.liquidUnstake(MSOL_TOKEN_ACCOUNT, 42L)
    );
  }

  @Test
  void orderUnstakeBindsTheGeneratedBuilder() {
    assertIx(
        MarinadeFinanceProgram.orderUnstake(
            INVOKED,
            MARINADE_ACCOUNTS.stateAccount(),
            MARINADE_ACCOUNTS.mSolTokenMint(),
            MSOL_TOKEN_ACCOUNT,
            OWNER,
            NEW_TICKET,
            SOLANA_ACCOUNTS.clockSysVar(),
            SOLANA_ACCOUNTS.rentSysVar(),
            SOLANA_ACCOUNTS.tokenProgram(),
            42L
        ),
        CLIENT.orderUnstake(MSOL_TOKEN_ACCOUNT, NEW_TICKET, 42L)
    );
  }

  @Test
  void addLiquidityBindsTheGeneratedBuilder() {
    assertIx(
        MarinadeFinanceProgram.addLiquidity(
            INVOKED,
            MARINADE_ACCOUNTS.stateAccount(),
            MARINADE_ACCOUNTS.liquidityPoolMSolSolMint(),
            MARINADE_ACCOUNTS.liquidityPoolAuthPDA(),
            MARINADE_ACCOUNTS.liquidityPoolMSolLegAccount(),
            MARINADE_ACCOUNTS.liquidityPoolSolLegAccount(),
            SOL_SOURCE,
            LP_TOKEN_ACCOUNT,
            SOLANA_ACCOUNTS.systemProgram(),
            SOLANA_ACCOUNTS.tokenProgram(),
            42L
        ),
        CLIENT.addLiquidity(SOL_SOURCE, LP_TOKEN_ACCOUNT, 42L)
    );
    // the short overload funds the deposit from the owner
    assertIx(
        CLIENT.addLiquidity(OWNER, LP_TOKEN_ACCOUNT, 42L),
        CLIENT.addLiquidity(LP_TOKEN_ACCOUNT, 42L)
    );
    // the ATA overload mints to the owner's LP-mint ATA
    assertIx(
        CLIENT.addLiquidity(
            OWNER,
            CLIENT.deriveATA(OWNER, MARINADE_ACCOUNTS.liquidityPoolMSolSolMint()),
            42L
        ),
        CLIENT.addLiquidityATA(42L)
    );
  }

  @Test
  void removeLiquidityBindsTheGeneratedBuilder() {
    assertIx(
        MarinadeFinanceProgram.removeLiquidity(
            INVOKED,
            MARINADE_ACCOUNTS.stateAccount(),
            MARINADE_ACCOUNTS.liquidityPoolMSolSolMint(),
            LP_TOKEN_ACCOUNT,
            OWNER,
            SOL_RECEIVER,
            MSOL_RECEIVER,
            MARINADE_ACCOUNTS.liquidityPoolSolLegAccount(),
            MARINADE_ACCOUNTS.liquidityPoolMSolLegAccount(),
            MARINADE_ACCOUNTS.liquidityPoolMSolLegAuthority(),
            SOLANA_ACCOUNTS.systemProgram(),
            SOLANA_ACCOUNTS.tokenProgram(),
            42L
        ),
        CLIENT.removeLiquidity(LP_TOKEN_ACCOUNT, SOL_RECEIVER, MSOL_RECEIVER, 42L)
    );
    // the short overload sends the SOL leg to the owner; the mSOL receiver
    // stays caller-supplied and distinct, so the two receiver slots cannot swap
    assertIx(
        CLIENT.removeLiquidity(LP_TOKEN_ACCOUNT, OWNER, MSOL_RECEIVER, 42L),
        CLIENT.removeLiquidity(LP_TOKEN_ACCOUNT, MSOL_RECEIVER, 42L)
    );
    // the ATA overload burns from the owner's LP ATA and receives mSOL at the
    // owner's mSOL ATA — two different mints, so the two derivations differ
    final var lpAta = CLIENT.deriveATA(OWNER, MARINADE_ACCOUNTS.liquidityPoolMSolSolMint());
    final var msolAta = CLIENT.deriveATA(OWNER, MARINADE_ACCOUNTS.mSolTokenMint());
    assertNotEquals(lpAta, msolAta);
    assertIx(
        CLIENT.removeLiquidity(lpAta, OWNER, msolAta, 42L),
        CLIENT.removeLiquidityATA(42L)
    );
  }

  // ---------------------------------------------------------------------------
  // derivations
  // ---------------------------------------------------------------------------

  @Test
  void deriveAtaBindsTheTokenProgramAndBothInputs() {
    final var mint = MARINADE_ACCOUNTS.mSolTokenMint();
    final var ata = CLIENT.deriveATA(OWNER, mint);
    assertEquals(
        AssociatedTokenPDAs.associatedTokenPDA(
            SOLANA_ACCOUNTS.associatedTokenAccountProgram(),
            OWNER,
            SOLANA_ACCOUNTS.tokenProgram(),
            mint
        ).publicKey(),
        ata
    );
    // both inputs participate
    assertNotEquals(ata, CLIENT.deriveATA(FEE_PAYER, mint));
    assertNotEquals(ata, CLIENT.deriveATA(OWNER, MARINADE_ACCOUNTS.liquidityPoolMSolSolMint()));
  }

  @Test
  void findDuplicationKeyDelegatesToTheAccounts() {
    assertEquals(
        MARINADE_ACCOUNTS.findDuplicationKey(VALIDATOR).publicKey(),
        CLIENT.findDuplicationKey(VALIDATOR).publicKey()
    );
  }

  /// The deprecated seed account derives off the *fee payer* under the
  /// Marinade program.
  @Test
  @SuppressWarnings("deprecation")
  void offCurveSeedAccountDerivesOffTheFeePayer() {
    final var derived = CLIENT.createOffCurveAccountWithSeed("ticket-1");
    final var expected =
        PublicKey.createOffCurveAccountWithAsciiSeed(FEE_PAYER, "ticket-1", MARINADE_ACCOUNTS.marinadeProgram());
    assertEquals(expected.publicKey(), derived.publicKey());
    assertEquals(FEE_PAYER, derived.baseKey());
    assertEquals(MARINADE_ACCOUNTS.marinadeProgram(), derived.program());
    assertNotEquals(derived.publicKey(), CLIENT.createOffCurveAccountWithSeed("ticket-2").publicKey());
  }
}

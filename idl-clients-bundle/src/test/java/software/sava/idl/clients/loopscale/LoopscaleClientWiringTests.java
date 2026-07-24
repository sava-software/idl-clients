package software.sava.idl.clients.loopscale;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.loopscale.gen.LoopscaleProgram;
import software.sava.idl.clients.loopscale.gen.types.BorrowPrincipalParams;
import software.sava.idl.clients.loopscale.gen.types.CreateLoanParams;
import software.sava.idl.clients.loopscale.gen.types.DepositCollateralParams;
import software.sava.idl.clients.loopscale.gen.types.ExpectedLoanValues;
import software.sava.idl.clients.loopscale.gen.types.UpdateWeightMatrixParams;
import software.sava.idl.clients.spl.SPLAccountClient;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// Pins every instruction builder on the Loopscale client to the generated
/// `LoopscaleProgram` call it must delegate to, with the client-supplied
/// defaults (fee payer, owner as borrower, the protocol-admin trio, event
/// authority, program id) spelled out on the mirror side — the generated
/// builder's parameter names are the reference for each slot. Every
/// caller-supplied account is a distinct fill-byte key, so a transposed pair
/// of same-typed keys changes the compared list. The short overloads are then
/// checked against the explicit calls they delegate to: the property that
/// matters is which identity fills each omitted slot (owner vs fee payer) and
/// which ATA — borrower's vs loan's vs strategy's — lands where.
final class LoopscaleClientWiringTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final LoopscaleAccounts LOOPSCALE_ACCOUNTS = LoopscaleAccounts.MAIN_NET;

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey FEE_PAYER = key(0x12);

  private static final PublicKey BORROWER = key(0x21);
  private static final PublicKey LOAN = key(0x22);
  private static final PublicKey BORROWER_COLLATERAL_TA = key(0x23);
  private static final PublicKey LOAN_COLLATERAL_TA = key(0x24);
  private static final PublicKey DEPOSIT_MINT = key(0x25);
  private static final PublicKey ASSET_IDENTIFIER = key(0x26);
  private static final PublicKey TOKEN_PROGRAM = key(0x27);
  private static final PublicKey STRATEGY = key(0x28);
  private static final PublicKey MARKET_INFORMATION = key(0x29);
  private static final PublicKey PRINCIPAL_MINT = key(0x2A);
  private static final PublicKey BORROWER_TA = key(0x2B);
  private static final PublicKey STRATEGY_TA = key(0x2C);

  private static final SPLAccountClient ACCOUNT_CLIENT =
      SPLAccountClient.createClient(SOLANA_ACCOUNTS, OWNER, AccountMeta.createFeePayer(FEE_PAYER));

  private static final ExpectedLoanValues EXPECTED = new ExpectedLoanValues(7L, new long[]{1, 2, 3, 4, 5});
  private static final DepositCollateralParams DEPOSIT_PARAMS =
      new DepositCollateralParams(42L, 1, key(0x31), new byte[]{1});
  private static final UpdateWeightMatrixParams WEIGHT_PARAMS =
      new UpdateWeightMatrixParams(2, new long[]{9, 8, 7, 6, 5}, EXPECTED, new byte[]{1});
  private static final BorrowPrincipalParams BORROW_PARAMS =
      new BorrowPrincipalParams(42L, new byte[]{1}, 3, EXPECTED, false);

  private static LoopscaleClient client() {
    return LoopscaleClient.create(ACCOUNT_CLIENT, LOOPSCALE_ACCOUNTS);
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

  /// The client is built inside the test body — factory and accessor coverage
  /// attributed to a field initializer is unstable under PIT.
  @Test
  void clientBindsItsIdentity() {
    final var client = client();
    assertEquals(SOLANA_ACCOUNTS, client.solanaAccounts());
    assertSame(LOOPSCALE_ACCOUNTS, client.loopscaleAccounts());
    assertSame(ACCOUNT_CLIENT.splClient(), client.splClient());
    assertEquals(OWNER, client.owner());
    assertEquals(FEE_PAYER, client.feePayer());
    assertNotEquals(client.owner(), client.feePayer());
  }

  /// The loan and strategy token accounts are ATAs *owned by the PDA*, not by
  /// the wallet: the first argument is the ATA owner.
  @Test
  void loanAndStrategyTokenAccountsDeriveOffTheirPda() {
    final var client = client();
    final var splClient = ACCOUNT_CLIENT.splClient();

    assertEquals(
        splClient.findATA(LOAN, TOKEN_PROGRAM, DEPOSIT_MINT).publicKey(),
        client.loanTokenAccount(LOAN, DEPOSIT_MINT, TOKEN_PROGRAM)
    );
    assertEquals(
        splClient.findATA(STRATEGY, TOKEN_PROGRAM, PRINCIPAL_MINT).publicKey(),
        client.strategyTokenAccount(STRATEGY, PRINCIPAL_MINT, TOKEN_PROGRAM)
    );
    // the two-arg overloads bind the classic token program
    assertEquals(
        client.loanTokenAccount(LOAN, DEPOSIT_MINT, SOLANA_ACCOUNTS.tokenProgram()),
        client.loanTokenAccount(LOAN, DEPOSIT_MINT)
    );
    assertEquals(
        client.strategyTokenAccount(STRATEGY, PRINCIPAL_MINT, SOLANA_ACCOUNTS.tokenProgram()),
        client.strategyTokenAccount(STRATEGY, PRINCIPAL_MINT)
    );
    // both inputs participate, and loan vs strategy is the owner seed
    assertNotEquals(
        client.loanTokenAccount(LOAN, DEPOSIT_MINT),
        client.strategyTokenAccount(STRATEGY, DEPOSIT_MINT)
    );
    assertNotEquals(
        client.loanTokenAccount(LOAN, DEPOSIT_MINT),
        client.loanTokenAccount(LOAN, PRINCIPAL_MINT)
    );
  }

  @Test
  void createLoanBindsTheGeneratedBuilder() {
    final var client = client();
    final var loan = LoopscalePDAs.loan(BORROWER, 7L, LOOPSCALE_ACCOUNTS.loopscaleProgram()).publicKey();
    assertIx(
        LoopscaleProgram.createLoan(
            LOOPSCALE_ACCOUNTS.invokedLoopscaleProgram(),
            LOOPSCALE_ACCOUNTS.protocolAdmin(),
            FEE_PAYER,
            BORROWER,
            loan,
            SOLANA_ACCOUNTS.systemProgram(),
            LOOPSCALE_ACCOUNTS.protocolAdminState(),
            LOOPSCALE_ACCOUNTS.eventAuthority(),
            LOOPSCALE_ACCOUNTS.loopscaleProgram(),
            new CreateLoanParams(7L)
        ),
        client.createLoan(BORROWER, 7L)
    );
    // the short overload borrows as the owner
    assertIx(client.createLoan(OWNER, 7L), client.createLoan(7L));
  }

  @Test
  void depositCollateralBindsTheGeneratedBuilder() {
    final var client = client();
    assertIx(
        LoopscaleProgram.depositCollateral(
            LOOPSCALE_ACCOUNTS.invokedLoopscaleProgram(),
            LOOPSCALE_ACCOUNTS.protocolAdmin(),
            FEE_PAYER,
            BORROWER,
            LOAN,
            BORROWER_COLLATERAL_TA,
            LOAN_COLLATERAL_TA,
            DEPOSIT_MINT,
            ASSET_IDENTIFIER,
            SOLANA_ACCOUNTS.systemProgram(),
            TOKEN_PROGRAM,
            SOLANA_ACCOUNTS.associatedTokenAccountProgram(),
            LOOPSCALE_ACCOUNTS.protocolAdminState(),
            LOOPSCALE_ACCOUNTS.eventAuthority(),
            LOOPSCALE_ACCOUNTS.loopscaleProgram(),
            DEPOSIT_PARAMS
        ),
        client.depositCollateral(
            BORROWER, LOAN, BORROWER_COLLATERAL_TA, LOAN_COLLATERAL_TA,
            DEPOSIT_MINT, ASSET_IDENTIFIER, TOKEN_PROGRAM, DEPOSIT_PARAMS
        )
    );
    // the short overload: owner borrows, the borrower ATA is the *owner's*,
    // the loan ATA is the *loan PDA's*, and the mint doubles as the asset id
    final var tokenProgram = SOLANA_ACCOUNTS.tokenProgram();
    assertIx(
        client.depositCollateral(
            OWNER,
            LOAN,
            ACCOUNT_CLIENT.splClient().findATA(OWNER, tokenProgram, DEPOSIT_MINT).publicKey(),
            client.loanTokenAccount(LOAN, DEPOSIT_MINT, tokenProgram),
            DEPOSIT_MINT,
            DEPOSIT_MINT,
            tokenProgram,
            DEPOSIT_PARAMS
        ),
        client.depositCollateral(LOAN, DEPOSIT_MINT, DEPOSIT_PARAMS)
    );
  }

  @Test
  void updateWeightMatrixBindsTheGeneratedBuilder() {
    final var client = client();
    assertIx(
        LoopscaleProgram.updateWeightMatrix(
            LOOPSCALE_ACCOUNTS.invokedLoopscaleProgram(),
            LOOPSCALE_ACCOUNTS.protocolAdmin(),
            BORROWER,
            LOAN,
            LOOPSCALE_ACCOUNTS.protocolAdminState(),
            WEIGHT_PARAMS
        ),
        client.updateWeightMatrix(BORROWER, LOAN, WEIGHT_PARAMS)
    );
    assertIx(
        client.updateWeightMatrix(OWNER, LOAN, WEIGHT_PARAMS),
        client.updateWeightMatrix(LOAN, WEIGHT_PARAMS)
    );
  }

  @Test
  void borrowPrincipalBindsTheGeneratedBuilder() {
    final var client = client();
    assertIx(
        LoopscaleProgram.borrowPrincipal(
            LOOPSCALE_ACCOUNTS.invokedLoopscaleProgram(),
            LOOPSCALE_ACCOUNTS.protocolAdmin(),
            FEE_PAYER,
            BORROWER,
            LOAN,
            STRATEGY,
            MARKET_INFORMATION,
            PRINCIPAL_MINT,
            BORROWER_TA,
            STRATEGY_TA,
            SOLANA_ACCOUNTS.associatedTokenAccountProgram(),
            TOKEN_PROGRAM,
            SOLANA_ACCOUNTS.systemProgram(),
            LOOPSCALE_ACCOUNTS.protocolAdminState(),
            LOOPSCALE_ACCOUNTS.eventAuthority(),
            LOOPSCALE_ACCOUNTS.loopscaleProgram(),
            BORROW_PARAMS
        ),
        client.borrowPrincipal(
            BORROWER, LOAN, STRATEGY, MARKET_INFORMATION, PRINCIPAL_MINT,
            BORROWER_TA, STRATEGY_TA, TOKEN_PROGRAM, BORROW_PARAMS
        )
    );
    // the short overload: the borrower's ATA receives, the strategy's ATA funds
    final var tokenProgram = SOLANA_ACCOUNTS.tokenProgram();
    assertIx(
        client.borrowPrincipal(
            OWNER,
            LOAN,
            STRATEGY,
            MARKET_INFORMATION,
            PRINCIPAL_MINT,
            ACCOUNT_CLIENT.splClient().findATA(OWNER, tokenProgram, PRINCIPAL_MINT).publicKey(),
            client.strategyTokenAccount(STRATEGY, PRINCIPAL_MINT, tokenProgram),
            tokenProgram,
            BORROW_PARAMS
        ),
        client.borrowPrincipal(LOAN, STRATEGY, MARKET_INFORMATION, PRINCIPAL_MINT, BORROW_PARAMS)
    );
  }

  /// The accounts factories are exercised from inside the test body — the
  /// `MAIN_NET` constant's own construction runs in `<clinit>`, whose coverage
  /// attribution is unstable under PIT.
  @Test
  void accountsFactoriesDeriveTheAdminStateAndEventAuthority() {
    final var program = LOOPSCALE_ACCOUNTS.loopscaleProgram();
    final var admin = LOOPSCALE_ACCOUNTS.protocolAdmin();

    final var fromKeys = LoopscaleAccounts.createAccounts(program, admin);
    assertEquals(program, fromKeys.invokedLoopscaleProgram().publicKey());
    assertEquals(program, fromKeys.loopscaleProgram());
    assertEquals(admin, fromKeys.protocolAdmin());
    assertEquals(LoopscalePDAs.eventAuthority(program).publicKey(), fromKeys.eventAuthority());
    // the admin state is the on-chain-confirmed singleton, not a re-derivation
    assertEquals(
        PublicKey.fromBase58Encoded("HcgXEnEsgvGowVnSjMmrzSewdx9yGvfXixiuMJPhyW2z"),
        fromKeys.protocolAdminState()
    );

    final var fromStrings = LoopscaleAccounts.createAccounts(
        "1oopBoJG58DgkUVKkEzKgyG9dvRmpgeEm1AVjoHkF78",
        "CyNKPfqsSLAejjZtEeNG3pR4SkPhSPHXdGhuNTyudrNs"
    );
    assertEquals(program, fromStrings.loopscaleProgram());
    assertEquals(admin, fromStrings.protocolAdmin());
    assertEquals(fromKeys.eventAuthority(), fromStrings.eventAuthority());
    assertEquals(fromKeys.protocolAdminState(), fromStrings.protocolAdminState());

    // loanPda binds the instance's own program
    assertEquals(
        LoopscalePDAs.loan(BORROWER, 7L, program).publicKey(),
        fromKeys.loanPda(BORROWER, 7L).publicKey()
    );
  }
}

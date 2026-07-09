package software.sava.idl.clients.loopscale;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.loopscale.gen.LoopscaleProgram;
import software.sava.idl.clients.loopscale.gen.types.BorrowPrincipalParams;
import software.sava.idl.clients.loopscale.gen.types.CreateLoanParams;
import software.sava.idl.clients.loopscale.gen.types.DepositCollateralParams;
import software.sava.idl.clients.loopscale.gen.types.UpdateWeightMatrixParams;
import software.sava.idl.clients.spl.SPLAccountClient;
import software.sava.idl.clients.spl.SPLClient;

final class LoopscaleClientImpl implements LoopscaleClient {

  private final SolanaAccounts solanaAccounts;
  private final LoopscaleAccounts accounts;
  private final SPLClient splClient;
  private final PublicKey owner;
  private final PublicKey feePayer;

  LoopscaleClientImpl(final SPLAccountClient splAccountClient, final LoopscaleAccounts accounts) {
    this.solanaAccounts = splAccountClient.solanaAccounts();
    this.accounts = accounts;
    this.splClient = splAccountClient.splClient();
    this.owner = splAccountClient.owner();
    this.feePayer = splAccountClient.feePayer().publicKey();
  }

  @Override
  public SolanaAccounts solanaAccounts() {
    return solanaAccounts;
  }

  @Override
  public LoopscaleAccounts loopscaleAccounts() {
    return accounts;
  }

  @Override
  public SPLClient splClient() {
    return splClient;
  }

  @Override
  public PublicKey owner() {
    return owner;
  }

  @Override
  public PublicKey feePayer() {
    return feePayer;
  }

  @Override
  public PublicKey loanTokenAccount(final PublicKey loan, final PublicKey mint, final PublicKey tokenProgram) {
    return splClient.findATA(loan, tokenProgram, mint).publicKey();
  }

  @Override
  public PublicKey loanTokenAccount(final PublicKey loan, final PublicKey mint) {
    return loanTokenAccount(loan, mint, solanaAccounts.tokenProgram());
  }

  @Override
  public PublicKey strategyTokenAccount(final PublicKey strategy, final PublicKey mint, final PublicKey tokenProgram) {
    return splClient.findATA(strategy, tokenProgram, mint).publicKey();
  }

  @Override
  public PublicKey strategyTokenAccount(final PublicKey strategy, final PublicKey mint) {
    return strategyTokenAccount(strategy, mint, solanaAccounts.tokenProgram());
  }

  @Override
  public Instruction createLoan(final PublicKey borrower, final long nonce) {
    final var loan = LoopscalePDAs.loan(borrower, nonce, accounts.loopscaleProgram()).publicKey();
    return LoopscaleProgram.createLoan(
        accounts.invokedLoopscaleProgram(),
        accounts.protocolAdmin(),
        feePayer,
        borrower,
        loan,
        solanaAccounts.systemProgram(),
        accounts.protocolAdminState(),
        accounts.eventAuthority(),
        accounts.loopscaleProgram(),
        new CreateLoanParams(nonce)
    );
  }

  @Override
  public Instruction createLoan(final long nonce) {
    return createLoan(owner, nonce);
  }

  @Override
  public Instruction depositCollateral(final PublicKey borrower,
                                       final PublicKey loan,
                                       final PublicKey borrowerCollateralTa,
                                       final PublicKey loanCollateralTa,
                                       final PublicKey depositMint,
                                       final PublicKey assetIdentifier,
                                       final PublicKey tokenProgram,
                                       final DepositCollateralParams params) {
    return LoopscaleProgram.depositCollateral(
        accounts.invokedLoopscaleProgram(),
        accounts.protocolAdmin(),
        feePayer,
        borrower,
        loan,
        borrowerCollateralTa,
        loanCollateralTa,
        depositMint,
        assetIdentifier,
        solanaAccounts.systemProgram(),
        tokenProgram,
        solanaAccounts.associatedTokenAccountProgram(),
        accounts.protocolAdminState(),
        accounts.eventAuthority(),
        accounts.loopscaleProgram(),
        params
    );
  }

  @Override
  public Instruction depositCollateral(final PublicKey loan,
                                       final PublicKey depositMint,
                                       final DepositCollateralParams params) {
    final var tokenProgram = solanaAccounts.tokenProgram();
    final var borrowerCollateralTa = splClient.findATA(owner, tokenProgram, depositMint).publicKey();
    final var loanCollateralTa = loanTokenAccount(loan, depositMint, tokenProgram);
    return depositCollateral(
        owner,
        loan,
        borrowerCollateralTa,
        loanCollateralTa,
        depositMint,
        depositMint,
        tokenProgram,
        params
    );
  }

  @Override
  public Instruction updateWeightMatrix(final PublicKey borrower,
                                        final PublicKey loan,
                                        final UpdateWeightMatrixParams params) {
    return LoopscaleProgram.updateWeightMatrix(
        accounts.invokedLoopscaleProgram(),
        accounts.protocolAdmin(),
        borrower,
        loan,
        accounts.protocolAdminState(),
        params
    );
  }

  @Override
  public Instruction updateWeightMatrix(final PublicKey loan, final UpdateWeightMatrixParams params) {
    return updateWeightMatrix(owner, loan, params);
  }

  @Override
  public Instruction borrowPrincipal(final PublicKey borrower,
                                     final PublicKey loan,
                                     final PublicKey strategy,
                                     final PublicKey marketInformation,
                                     final PublicKey principalMint,
                                     final PublicKey borrowerTa,
                                     final PublicKey strategyTa,
                                     final PublicKey tokenProgram,
                                     final BorrowPrincipalParams params) {
    return LoopscaleProgram.borrowPrincipal(
        accounts.invokedLoopscaleProgram(),
        accounts.protocolAdmin(),
        feePayer,
        borrower,
        loan,
        strategy,
        marketInformation,
        principalMint,
        borrowerTa,
        strategyTa,
        solanaAccounts.associatedTokenAccountProgram(),
        tokenProgram,
        solanaAccounts.systemProgram(),
        accounts.protocolAdminState(),
        accounts.eventAuthority(),
        accounts.loopscaleProgram(),
        params
    );
  }

  @Override
  public Instruction borrowPrincipal(final PublicKey loan,
                                     final PublicKey strategy,
                                     final PublicKey marketInformation,
                                     final PublicKey principalMint,
                                     final BorrowPrincipalParams params) {
    final var tokenProgram = solanaAccounts.tokenProgram();
    final var borrowerTa = splClient.findATA(owner, tokenProgram, principalMint).publicKey();
    final var strategyTa = strategyTokenAccount(strategy, principalMint, tokenProgram);
    return borrowPrincipal(
        owner,
        loan,
        strategy,
        marketInformation,
        principalMint,
        borrowerTa,
        strategyTa,
        tokenProgram,
        params
    );
  }
}

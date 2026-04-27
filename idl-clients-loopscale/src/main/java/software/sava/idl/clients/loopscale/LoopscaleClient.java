package software.sava.idl.clients.loopscale;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.loopscale.gen.types.BorrowPrincipalParams;
import software.sava.idl.clients.loopscale.gen.types.DepositCollateralParams;
import software.sava.idl.clients.loopscale.gen.types.UpdateWeightMatrixParams;
import software.sava.idl.clients.spl.SPLAccountClient;
import software.sava.idl.clients.spl.SPLClient;

public interface LoopscaleClient {

  static LoopscaleClient create(final SPLAccountClient splAccountClient, final LoopscaleAccounts accounts) {
    return new LoopscaleClientImpl(splAccountClient, accounts);
  }

  SolanaAccounts solanaAccounts();

  LoopscaleAccounts loopscaleAccounts();

  SPLClient splClient();

  PublicKey owner();

  PublicKey feePayer();

  PublicKey loanTokenAccount(final PublicKey loan, final PublicKey mint, final PublicKey tokenProgram);

  PublicKey loanTokenAccount(final PublicKey loan, final PublicKey mint);

  PublicKey strategyTokenAccount(final PublicKey strategy, final PublicKey mint, final PublicKey tokenProgram);

  PublicKey strategyTokenAccount(final PublicKey strategy, final PublicKey mint);

  Instruction createLoan(final PublicKey borrower, final long nonce);

  Instruction createLoan(final long nonce);

  Instruction depositCollateral(final PublicKey borrower,
                                final PublicKey loan,
                                final PublicKey borrowerCollateralTa,
                                final PublicKey loanCollateralTa,
                                final PublicKey depositMint,
                                final PublicKey assetIdentifier,
                                final PublicKey tokenProgram,
                                final DepositCollateralParams params);

  Instruction depositCollateral(final PublicKey loan,
                                final PublicKey depositMint,
                                final DepositCollateralParams params);

  Instruction updateWeightMatrix(final PublicKey borrower,
                                 final PublicKey loan,
                                 final UpdateWeightMatrixParams params);

  Instruction updateWeightMatrix(final PublicKey loan, final UpdateWeightMatrixParams params);

  Instruction borrowPrincipal(final PublicKey borrower,
                              final PublicKey loan,
                              final PublicKey strategy,
                              final PublicKey marketInformation,
                              final PublicKey principalMint,
                              final PublicKey borrowerTa,
                              final PublicKey strategyTa,
                              final PublicKey tokenProgram,
                              final BorrowPrincipalParams params);

  Instruction borrowPrincipal(final PublicKey loan,
                              final PublicKey strategy,
                              final PublicKey marketInformation,
                              final PublicKey principalMint,
                              final BorrowPrincipalParams params);
}

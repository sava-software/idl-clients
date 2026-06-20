package software.sava.idl.clients.marginfi.v2;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.marginfi.v2.gen.MarginfiProgram;
import software.sava.idl.clients.spl.SPLAccountClient;
import software.sava.idl.clients.spl.SPLClient;

final class MarginfiClientImpl implements MarginfiClient {

  private final SolanaAccounts solanaAccounts;
  private final MarginfiAccounts accounts;
  private final SPLClient splClient;
  private final PublicKey authority;
  private final PublicKey feePayer;

  MarginfiClientImpl(final SPLAccountClient splAccountClient, final MarginfiAccounts accounts) {
    this.solanaAccounts = splAccountClient.solanaAccounts();
    this.accounts = accounts;
    this.splClient = splAccountClient.splClient();
    this.authority = splAccountClient.owner();
    this.feePayer = splAccountClient.feePayer().publicKey();
  }

  @Override
  public SolanaAccounts solanaAccounts() {
    return solanaAccounts;
  }

  @Override
  public MarginfiAccounts marginfiAccounts() {
    return accounts;
  }

  @Override
  public SPLClient splClient() {
    return splClient;
  }

  @Override
  public PublicKey authority() {
    return authority;
  }

  @Override
  public PublicKey feePayer() {
    return feePayer;
  }

  @Override
  public Instruction initializeAccount(final PublicKey marginfiAccount,
                                       final PublicKey authority,
                                       final PublicKey feePayer) {
    return MarginfiProgram.marginfiAccountInitialize(
        accounts.invokedMarginfiProgram(),
        solanaAccounts,
        accounts.marginfiGroup(),
        marginfiAccount,
        authority,
        feePayer
    );
  }

  @Override
  public Instruction closeAccount(final PublicKey marginfiAccount,
                                  final PublicKey authority,
                                  final PublicKey feePayer) {
    return MarginfiProgram.marginfiAccountClose(
        accounts.invokedMarginfiProgram(),
        marginfiAccount,
        authority,
        feePayer
    );
  }

  @Override
  public Instruction updateEmissionsDestinationAccount(final PublicKey marginfiAccount,
                                                       final PublicKey authority,
                                                       final PublicKey destinationAccount) {
    return MarginfiProgram.marginfiAccountUpdateEmissionsDestinationAccount(
        accounts.invokedMarginfiProgram(),
        marginfiAccount,
        authority,
        destinationAccount
    );
  }

  @Override
  public Instruction transferToNewAccount(final PublicKey oldMarginfiAccount,
                                          final PublicKey newMarginfiAccount,
                                          final PublicKey authority,
                                          final PublicKey feePayer,
                                          final PublicKey newAuthority,
                                          final PublicKey globalFeeWallet) {
    return MarginfiProgram.transferToNewAccount(
        accounts.invokedMarginfiProgram(),
        solanaAccounts,
        accounts.marginfiGroup(),
        oldMarginfiAccount,
        newMarginfiAccount,
        authority,
        feePayer,
        newAuthority,
        globalFeeWallet
    );
  }

  @Override
  public Instruction deposit(final PublicKey marginfiAccount,
                             final PublicKey authority,
                             final PublicKey bank,
                             final PublicKey signerTokenAccount,
                             final PublicKey liquidityVault,
                             final PublicKey tokenProgram,
                             final long amount,
                             final Boolean depositUpToLimit) {
    return MarginfiProgram.lendingAccountDeposit(
        accounts.invokedMarginfiProgram(),
        accounts.marginfiGroup(),
        marginfiAccount,
        authority,
        bank,
        signerTokenAccount,
        liquidityVault,
        tokenProgram,
        amount,
        depositUpToLimit
    );
  }

  @Override
  public Instruction repay(final PublicKey marginfiAccount,
                           final PublicKey authority,
                           final PublicKey bank,
                           final PublicKey signerTokenAccount,
                           final PublicKey liquidityVault,
                           final PublicKey tokenProgram,
                           final long amount,
                           final Boolean repayAll) {
    return MarginfiProgram.lendingAccountRepay(
        accounts.invokedMarginfiProgram(),
        accounts.marginfiGroup(),
        marginfiAccount,
        authority,
        bank,
        signerTokenAccount,
        liquidityVault,
        tokenProgram,
        amount,
        repayAll
    );
  }

  @Override
  public Instruction withdraw(final PublicKey marginfiAccount,
                              final PublicKey authority,
                              final PublicKey bank,
                              final PublicKey destinationTokenAccount,
                              final PublicKey bankLiquidityVaultAuthority,
                              final PublicKey liquidityVault,
                              final PublicKey tokenProgram,
                              final long amount,
                              final Boolean withdrawAll) {
    return MarginfiProgram.lendingAccountWithdraw(
        accounts.invokedMarginfiProgram(),
        accounts.marginfiGroup(),
        marginfiAccount,
        authority,
        bank,
        destinationTokenAccount,
        bankLiquidityVaultAuthority,
        liquidityVault,
        tokenProgram,
        amount,
        withdrawAll
    );
  }

  @Override
  public Instruction borrow(final PublicKey marginfiAccount,
                            final PublicKey authority,
                            final PublicKey bank,
                            final PublicKey destinationTokenAccount,
                            final PublicKey bankLiquidityVaultAuthority,
                            final PublicKey liquidityVault,
                            final PublicKey tokenProgram,
                            final long amount) {
    return MarginfiProgram.lendingAccountBorrow(
        accounts.invokedMarginfiProgram(),
        accounts.marginfiGroup(),
        marginfiAccount,
        authority,
        bank,
        destinationTokenAccount,
        bankLiquidityVaultAuthority,
        liquidityVault,
        tokenProgram,
        amount
    );
  }

  @Override
  public Instruction closeBalance(final PublicKey marginfiAccount,
                                  final PublicKey authority,
                                  final PublicKey bank) {
    return MarginfiProgram.lendingAccountCloseBalance(
        accounts.invokedMarginfiProgram(),
        accounts.marginfiGroup(),
        marginfiAccount,
        authority,
        bank
    );
  }

  @Override
  public Instruction startFlashloan(final PublicKey marginfiAccount,
                                    final PublicKey authority,
                                    final long endIndex) {
    return MarginfiProgram.lendingAccountStartFlashloan(
        accounts.invokedMarginfiProgram(),
        solanaAccounts,
        marginfiAccount,
        authority,
        endIndex
    );
  }

  @Override
  public Instruction endFlashloan(final PublicKey marginfiAccount, final PublicKey authority) {
    return MarginfiProgram.lendingAccountEndFlashloan(
        accounts.invokedMarginfiProgram(),
        marginfiAccount,
        authority
    );
  }
}

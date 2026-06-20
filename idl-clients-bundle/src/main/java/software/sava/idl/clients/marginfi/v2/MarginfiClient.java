package software.sava.idl.clients.marginfi.v2;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.SPLAccountClient;
import software.sava.idl.clients.spl.SPLClient;

/// A hand-written convenience client wrapping the trader / end-user facing instructions of the
/// marginfi v2 ("Borrow Lending Prime Broker") program.
///
/// Only non-admin, non-keeper instructions are exposed. Admin, permissionless crank, liquidation
/// and program-integration (Drift / Kamino / JupLend / Solend) instructions are intentionally not
/// wrapped here.
///
/// Health-affecting instructions ([#borrow], [#withdraw]) require the marginfi risk engine to read
/// every active balance's bank and oracle. Those accounts are passed as `remaining_accounts` and
/// are **not** emitted by the generated builders: the caller must append them to the returned
/// [Instruction] via [Instruction#extraAccounts] in the order `<bank1, oracle1, bank2, oracle2, ...>`.
public interface MarginfiClient {

  static MarginfiClient createClient(final SPLAccountClient splAccountClient, final MarginfiAccounts accounts) {
    return new MarginfiClientImpl(splAccountClient, accounts);
  }

  SolanaAccounts solanaAccounts();

  MarginfiAccounts marginfiAccounts();

  SPLClient splClient();

  /// The account authority / owner used by the convenience `default` overloads.
  PublicKey authority();

  PublicKey feePayer();

  // ------------------------------------------------------------------
  // marginfi account lifecycle
  // ------------------------------------------------------------------

  Instruction initializeAccount(final PublicKey marginfiAccount,
                                final PublicKey authority,
                                final PublicKey feePayer);

  default Instruction initializeAccount(final PublicKey marginfiAccount) {
    return initializeAccount(marginfiAccount, authority(), feePayer());
  }

  Instruction closeAccount(final PublicKey marginfiAccount,
                           final PublicKey authority,
                           final PublicKey feePayer);

  default Instruction closeAccount(final PublicKey marginfiAccount) {
    return closeAccount(marginfiAccount, authority(), feePayer());
  }

  Instruction updateEmissionsDestinationAccount(final PublicKey marginfiAccount,
                                                final PublicKey authority,
                                                final PublicKey destinationAccount);

  default Instruction updateEmissionsDestinationAccount(final PublicKey marginfiAccount,
                                                        final PublicKey destinationAccount) {
    return updateEmissionsDestinationAccount(marginfiAccount, authority(), destinationAccount);
  }

  Instruction transferToNewAccount(final PublicKey oldMarginfiAccount,
                                   final PublicKey newMarginfiAccount,
                                   final PublicKey authority,
                                   final PublicKey feePayer,
                                   final PublicKey newAuthority,
                                   final PublicKey globalFeeWallet);

  default Instruction transferToNewAccount(final PublicKey oldMarginfiAccount,
                                           final PublicKey newMarginfiAccount,
                                           final PublicKey newAuthority,
                                           final PublicKey globalFeeWallet) {
    return transferToNewAccount(oldMarginfiAccount, newMarginfiAccount, authority(), feePayer(), newAuthority, globalFeeWallet);
  }

  // ------------------------------------------------------------------
  // lending operations
  // ------------------------------------------------------------------

  Instruction deposit(final PublicKey marginfiAccount,
                      final PublicKey authority,
                      final PublicKey bank,
                      final PublicKey signerTokenAccount,
                      final PublicKey liquidityVault,
                      final PublicKey tokenProgram,
                      final long amount,
                      final Boolean depositUpToLimit);

  default Instruction deposit(final PublicKey marginfiAccount,
                              final PublicKey bank,
                              final PublicKey mint,
                              final long amount) {
    final var tokenProgram = solanaAccounts().tokenProgram();
    final var signerTokenAccount = splClient().findATA(authority(), tokenProgram, mint).publicKey();
    final var liquidityVault = marginfiAccounts().bankLiquidityVaultPDA(bank).publicKey();
    return deposit(marginfiAccount, authority(), bank, signerTokenAccount, liquidityVault, tokenProgram, amount, null);
  }

  Instruction repay(final PublicKey marginfiAccount,
                    final PublicKey authority,
                    final PublicKey bank,
                    final PublicKey signerTokenAccount,
                    final PublicKey liquidityVault,
                    final PublicKey tokenProgram,
                    final long amount,
                    final Boolean repayAll);

  default Instruction repay(final PublicKey marginfiAccount,
                            final PublicKey bank,
                            final PublicKey mint,
                            final long amount,
                            final Boolean repayAll) {
    final var tokenProgram = solanaAccounts().tokenProgram();
    final var signerTokenAccount = splClient().findATA(authority(), tokenProgram, mint).publicKey();
    final var liquidityVault = marginfiAccounts().bankLiquidityVaultPDA(bank).publicKey();
    return repay(marginfiAccount, authority(), bank, signerTokenAccount, liquidityVault, tokenProgram, amount, repayAll);
  }

  Instruction withdraw(final PublicKey marginfiAccount,
                       final PublicKey authority,
                       final PublicKey bank,
                       final PublicKey destinationTokenAccount,
                       final PublicKey bankLiquidityVaultAuthority,
                       final PublicKey liquidityVault,
                       final PublicKey tokenProgram,
                       final long amount,
                       final Boolean withdrawAll);

  default Instruction withdraw(final PublicKey marginfiAccount,
                               final PublicKey bank,
                               final PublicKey mint,
                               final long amount,
                               final Boolean withdrawAll) {
    final var tokenProgram = solanaAccounts().tokenProgram();
    final var destinationTokenAccount = splClient().findATA(authority(), tokenProgram, mint).publicKey();
    final var liquidityVault = marginfiAccounts().bankLiquidityVaultPDA(bank).publicKey();
    final var bankLiquidityVaultAuthority = marginfiAccounts().bankLiquidityVaultAuthorityPDA(bank).publicKey();
    return withdraw(marginfiAccount, authority(), bank, destinationTokenAccount, bankLiquidityVaultAuthority, liquidityVault, tokenProgram, amount, withdrawAll);
  }

  Instruction borrow(final PublicKey marginfiAccount,
                     final PublicKey authority,
                     final PublicKey bank,
                     final PublicKey destinationTokenAccount,
                     final PublicKey bankLiquidityVaultAuthority,
                     final PublicKey liquidityVault,
                     final PublicKey tokenProgram,
                     final long amount);

  default Instruction borrow(final PublicKey marginfiAccount,
                             final PublicKey bank,
                             final PublicKey mint,
                             final long amount) {
    final var tokenProgram = solanaAccounts().tokenProgram();
    final var destinationTokenAccount = splClient().findATA(authority(), tokenProgram, mint).publicKey();
    final var liquidityVault = marginfiAccounts().bankLiquidityVaultPDA(bank).publicKey();
    final var bankLiquidityVaultAuthority = marginfiAccounts().bankLiquidityVaultAuthorityPDA(bank).publicKey();
    return borrow(marginfiAccount, authority(), bank, destinationTokenAccount, bankLiquidityVaultAuthority, liquidityVault, tokenProgram, amount);
  }

  Instruction closeBalance(final PublicKey marginfiAccount,
                           final PublicKey authority,
                           final PublicKey bank);

  default Instruction closeBalance(final PublicKey marginfiAccount, final PublicKey bank) {
    return closeBalance(marginfiAccount, authority(), bank);
  }

  // ------------------------------------------------------------------
  // flash loans
  // ------------------------------------------------------------------

  Instruction startFlashloan(final PublicKey marginfiAccount,
                             final PublicKey authority,
                             final long endIndex);

  default Instruction startFlashloan(final PublicKey marginfiAccount, final long endIndex) {
    return startFlashloan(marginfiAccount, authority(), endIndex);
  }

  Instruction endFlashloan(final PublicKey marginfiAccount, final PublicKey authority);

  default Instruction endFlashloan(final PublicKey marginfiAccount) {
    return endFlashloan(marginfiAccount, authority());
  }
}

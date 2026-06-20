package software.sava.idl.clients.marginfi.v2;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.marginfi.v2.gen.types.OrderTrigger;
import software.sava.idl.clients.spl.SPLAccountClient;
import software.sava.idl.clients.spl.SPLClient;

import java.util.OptionalInt;

/// A hand-written convenience client wrapping the trader / end-user facing instructions of the
/// marginfi v2 ("Borrow Lending Prime Broker") program.
///
/// Only non-admin, non-keeper instructions are exposed. Admin, permissionless crank, liquidation
/// and program-integration (Drift / Kamino / JupLend / Solend) instructions are intentionally not
/// wrapped here.
///
/// Health-affecting instructions ([#borrow], [#withdraw], [#pulseHealth]) require the marginfi risk
/// engine to read every active balance's bank and oracle. Those accounts are passed as
/// `remaining_accounts` and are **not** emitted by the generated builders: the caller must append
/// them to the returned [Instruction] via [Instruction#extraAccounts] in the order
/// `<bank1, oracle1, bank2, oracle2, ...>`.
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

  /// Initialize a marginfi account whose address is a program-derived address (PDA) seeded by
  /// `(marginfi_group, authority, account_index, third_party_id)`. The caller is responsible for
  /// passing the matching derived `marginfiAccount` key.
  Instruction initializeAccountPda(final PublicKey marginfiAccount,
                                   final PublicKey authority,
                                   final PublicKey feePayer,
                                   final int accountIndex,
                                   final OptionalInt thirdPartyId);

  default Instruction initializeAccountPda(final PublicKey marginfiAccount,
                                           final int accountIndex,
                                           final OptionalInt thirdPartyId) {
    return initializeAccountPda(marginfiAccount, authority(), feePayer(), accountIndex, thirdPartyId);
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

  /// Same as [#transferToNewAccount] except the resulting account is a PDA seeded by
  /// `(marginfi_group, new_authority, account_index, third_party_id)`. The caller is responsible
  /// for passing the matching derived `newMarginfiAccount` key.
  Instruction transferToNewAccountPda(final PublicKey oldMarginfiAccount,
                                      final PublicKey newMarginfiAccount,
                                      final PublicKey authority,
                                      final PublicKey feePayer,
                                      final PublicKey newAuthority,
                                      final PublicKey globalFeeWallet,
                                      final int accountIndex,
                                      final OptionalInt thirdPartyId);

  default Instruction transferToNewAccountPda(final PublicKey oldMarginfiAccount,
                                              final PublicKey newMarginfiAccount,
                                              final PublicKey newAuthority,
                                              final PublicKey globalFeeWallet,
                                              final int accountIndex,
                                              final OptionalInt thirdPartyId) {
    return transferToNewAccountPda(oldMarginfiAccount, newMarginfiAccount, authority(), feePayer(), newAuthority, globalFeeWallet, accountIndex, thirdPartyId);
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

  /// (permissionless) Zero out `emissions_outstanding` on a balance after emissions are disabled
  /// on the bank.
  Instruction clearEmissions(final PublicKey marginfiAccount, final PublicKey bank);

  /// (permissionless) Refresh the internal risk-engine health cache of a marginfi account.
  ///
  /// The caller must append every active balance's bank and oracle as `remaining_accounts` on the
  /// returned [Instruction] via [Instruction#extraAccounts] in the order
  /// `<bank1, oracle1, bank2, oracle2, ...>`.
  Instruction pulseHealth(final PublicKey marginfiAccount);

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

  // ------------------------------------------------------------------
  // conditional orders (stop-loss / take-profit)
  // ------------------------------------------------------------------

  /// (user) Create a new conditional order.
  /// * `bankKeys` - the lending and borrowing position banks in the user's balances for which the
  ///   order is being placed.
  /// * `trigger` - the order type (stop loss, take profit, or both) and the dollar threshold at
  ///   which to trigger.
  Instruction placeOrder(final PublicKey marginfiAccount,
                         final PublicKey feePayer,
                         final PublicKey authority,
                         final PublicKey order,
                         final PublicKey globalFeeWallet,
                         final PublicKey[] bankKeys,
                         final OrderTrigger trigger);

  default Instruction placeOrder(final PublicKey marginfiAccount,
                                 final PublicKey order,
                                 final PublicKey globalFeeWallet,
                                 final PublicKey[] bankKeys,
                                 final OrderTrigger trigger) {
    return placeOrder(marginfiAccount, feePayer(), authority(), order, globalFeeWallet, bankKeys, trigger);
  }

  /// (user) Close an existing conditional order, returning its rent to `feeRecipient`.
  Instruction closeOrder(final PublicKey marginfiAccount,
                         final PublicKey authority,
                         final PublicKey order,
                         final PublicKey feeRecipient);

  default Instruction closeOrder(final PublicKey marginfiAccount,
                                 final PublicKey order,
                                 final PublicKey feeRecipient) {
    return closeOrder(marginfiAccount, authority(), order, feeRecipient);
  }
}

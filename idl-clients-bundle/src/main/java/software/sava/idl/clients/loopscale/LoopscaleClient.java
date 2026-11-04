package software.sava.idl.clients.loopscale;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.loopscale.gen.types.BorrowPrincipalParams;
import software.sava.idl.clients.loopscale.gen.types.DepositCollateralParams;
import software.sava.idl.clients.loopscale.gen.types.UpdateWeightMatrixParams;
import software.sava.idl.clients.spl.SPLAccountClient;
import software.sava.idl.clients.spl.SPLClient;

/// A hand-written client for the borrower side of the Loopscale lending program.
///
/// Every instruction built here names Loopscale's protocol admin ([LoopscaleAccounts#protocolAdmin],
/// `CyNKPfqsSLAejjZtEeNG3pR4SkPhSPHXdGhuNTyudrNs` on mainnet) as a read-only signer, and the
/// program checks it. Without that signature a call fails `3010 AccountNotSigner`, and with the
/// borrower in its place it fails `6136 InvalidAuthority` (both measured on `borrow_principal`). A
/// transaction built from these instructions needs Loopscale's signature as well as the
/// borrower's. Whether Loopscale signs for any caller is not established.
///
/// ## Remaining accounts
///
/// Whether an instruction is complete as built depends on the loan, which the IDL cannot express.
/// Outside a `lock_loan`/`unlock_loan` pair, `deposit_collateral` on a loan with an active ledger
/// and `borrow_principal` run the loan's health check. The program reads the check from accounts
/// appended with [Instruction#extraAccounts] and from the params' `asset_index_guidance`, and
/// fails `6006 NotEnoughRemainingAccounts` without the accounts. Inside a pair the deposit and the
/// borrow need no health check. `unlock_loan` carries it instead, the same way, and needs nothing
/// when the pair leaves no active ledger. This client does not wrap the pair. Build it with the
/// generated `LoopscaleProgram.lockLoan`, whose `unlockIdx` is the unlock's top-level instruction
/// index, and `LoopscaleProgram.unlockLoan`. `update_weight_matrix` has a tail of its own,
/// described on [#updateWeightMatrix].
///
/// The health check, as measured on loans with one active ledger (no loan on chain had more):
///
/// - accounts: the ledger's `marketInformation` (read-only is enough), the principal's oracle
///   group, then one oracle group per collateral slot the loan holds, in slot order;
/// - guidance: each of those assets' index in that market's `assetData`, in the same order. An
///   asset priced through a quote asset is followed by the quote asset's index.
///
/// An asset's oracle group depends on its `oracleType` and `quoteMint` in that market, with
/// `oracle` its `oracleAccount`:
///
/// - 19 (the beam price account), no quote asset: `[oracle, zero key]`, the zero key being
///   `11111111111111111111111111111111`. For the few mints whose entry in that account references
///   the beam program `beamVVkNmKeXcuZ6zLpC9eM5YgVyAn4Z9xdPrz3gCW2`, it is
///   `[oracle, beam program, mint, oracle]`;
/// - 6 or 9, no quote asset: `[oracle]`;
/// - 1, priced through its quote asset: `[oracle]`, then the quote asset's group.
///
/// A wrong account in any position fails `6010 InvalidRemainingAccount`. Extra trailing accounts
/// and guidance bytes are ignored. Other oracle types and quoted types 6 and 9 were not measured,
/// and some of the layouts involved were inferred from single accounts, so this client has no
/// derivation helper. `LoopscaleRemainingAccountsTests` pins one live call. The calls measured used
/// 220,000 to 495,000 compute units, so set a compute-unit limit.
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

  /// Deposit collateral into `loan`, from `borrowerCollateralTa` into `loanCollateralTa`.
  ///
  /// Complete as built inside a lock pair, or on a loan without an active ledger. Pass an empty
  /// `assetIndexGuidance` then. Outside a lock pair on a loan with an active ledger, append the
  /// health check (see the type documentation) with [Instruction#extraAccounts] and pass its
  /// guidance as `assetIndexGuidance`. Without the accounts the program fails `6006`. With too few
  /// guidance bytes it fails `6067 InvalidAssetIndexGuidance`, and with a wrong index
  /// `6066 InvalidAssetIndex`. On such a loan, depositing an asset the loan does not hold yet
  /// reached `6120 PriceUnderflow` even with the health check. How to add a second collateral asset
  /// there is not established. The remaining accounts that [DepositCollateralParams] documents,
  /// carried over from the IDL, are not what the deployed program reads.
  Instruction depositCollateral(final PublicKey borrower,
                                final PublicKey loan,
                                final PublicKey borrowerCollateralTa,
                                final PublicKey loanCollateralTa,
                                final PublicKey depositMint,
                                final PublicKey assetIdentifier,
                                final PublicKey tokenProgram,
                                final DepositCollateralParams params);

  /// Deposit `depositMint`, which is also the asset identifier, from the owner's associated token
  /// account into the loan's, under the classic token program. Remaining accounts and
  /// `assetIndexGuidance` are as for the full overload.
  Instruction depositCollateral(final PublicKey loan,
                                final PublicKey depositMint,
                                final DepositCollateralParams params);

  /// Set the weights `loan` gives one collateral slot.
  ///
  /// Complete as built on a loan without an active ledger. Pass an empty `assetIndexGuidance`
  /// then. Inside a lock pair on a loan with an active ledger, including one a borrow in the same
  /// pair opened, the program reads a tail of its own rather than the health check: append it with
  /// [Instruction#extraAccounts]. Its guidance is `[C]`, the collateral's index in the ledger's
  /// market. With `MI` the ledger's `marketInformation` and `S` its `strategy`, passed writable as
  /// the live calls do, the tail is:
  ///
  /// - for assigning the collateral to the ledger (weight 0 to 1,000,000): `[MI, S, S, MI, MI]`;
  /// - for releasing it (1,000,000 to 0): `[MI, S, MI]`.
  ///
  /// Without the tail the program fails `6006`, and with a wrong account in any position it fails
  /// `6010`. Setting a weight to the value it already holds failed `6010` with every tail tried.
  /// Outside a lock pair on a loan with an active ledger, the tail is not established.
  Instruction updateWeightMatrix(final PublicKey borrower,
                                 final PublicKey loan,
                                 final UpdateWeightMatrixParams params);

  /// Set the weights with the owner as borrower. Remaining accounts and `assetIndexGuidance` are as
  /// for the full overload.
  Instruction updateWeightMatrix(final PublicKey loan, final UpdateWeightMatrixParams params);

  /// Borrow principal for `loan` from `strategy` in the market `marketInformation`.
  ///
  /// Complete as built inside a lock pair. Pass an empty `assetIndexGuidance` then. The exception
  /// is a `strategy` that keeps its principal in Marginfi (its `externalYieldSource` is 1). It needs
  /// its external-yield accounts appended with [Instruction#extraAccounts] and guidance `[C]`, the
  /// collateral's index in `marketInformation`. Outside a lock pair, append the health check (see
  /// the type documentation) the same way, after the external-yield accounts when there are any.
  /// Pass guidance `[C, P, C]`: the collateral's index, then the health check's guidance. The live
  /// borrow that opened a loan's first ledger (`2b4VPFuX…`) carried the check too. Without the
  /// accounts the program fails `6006`. With too few guidance bytes it fails `6067`, and with a
  /// wrong index `6066`. Leaving out the external-yield accounts fails
  /// `3007 AccountOwnedByWrongProgram` outside a pair and `6006` inside one.
  ///
  /// The external-yield accounts, passed writable, are the Marginfi bank (the strategy's
  /// `externalYieldVault`), the strategy's Marginfi account (`externalYieldAccount`), the bank's
  /// group, its liquidity vault and that vault's authority. Two read-only accounts follow: the
  /// Marginfi program and the bank's first oracle key. They were measured for one bank, whose
  /// Marginfi risk-engine group is the bank and one oracle. Banks with other groups were not
  /// measured.
  Instruction borrowPrincipal(final PublicKey borrower,
                              final PublicKey loan,
                              final PublicKey strategy,
                              final PublicKey marketInformation,
                              final PublicKey principalMint,
                              final PublicKey borrowerTa,
                              final PublicKey strategyTa,
                              final PublicKey tokenProgram,
                              final BorrowPrincipalParams params);

  /// Borrow into the owner's associated token account for `principalMint` from the strategy's,
  /// under the classic token program. Remaining accounts and `assetIndexGuidance` are as for the
  /// full overload.
  Instruction borrowPrincipal(final PublicKey loan,
                              final PublicKey strategy,
                              final PublicKey marketInformation,
                              final PublicKey principalMint,
                              final BorrowPrincipalParams params);
}

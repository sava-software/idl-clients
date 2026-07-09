package software.sava.idl.clients.spl;

import software.sava.core.accounts.AccountWithSeed;
import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.stake.LockUp;
import software.sava.idl.clients.spl.stake.StakeAccount;
import software.sava.idl.clients.spl.stake.gen.types.StakeAuthorize;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.OptionalLong;
import java.util.SequencedCollection;

import static software.sava.idl.clients.spl.compute_budget.ComputeBudgetUtil.COMPUTE_UNITS_CONSUMED;
import static software.sava.idl.clients.spl.compute_budget.gen.ComputeBudgetProgram.setComputeUnitLimit;
import static software.sava.idl.clients.spl.compute_budget.gen.ComputeBudgetProgram.setComputeUnitPrice;

public interface SPLClient {

  static SPLClient createClient(final SolanaAccounts solanaAccounts) {
    return new SPLClientImpl(solanaAccounts);
  }

  static SPLClient createClient() {
    return createClient(SolanaAccounts.MAIN_NET);
  }

  SolanaAccounts solanaAccounts();

  SPLAccountClient createAccountClient(final PublicKey owner, final AccountMeta feePayer);

  default SPLAccountClient createAccountClient(final PublicKey owner, final PublicKey feePayer) {
    return createAccountClient(owner, AccountMeta.createFeePayer(feePayer));
  }

  default SPLAccountClient createAccountClient(final PublicKey owner) {
    return createAccountClient(owner, owner);
  }

  Instruction syncNative(final PublicKey tokenAccount);

  ProgramDerivedAddress findATA(final PublicKey owner, final PublicKey tokenProgram, final PublicKey mint);

  default ProgramDerivedAddress findATA(final PublicKey owner, final PublicKey mint) {
    return findATA(owner, solanaAccounts().tokenProgram(), mint);
  }

  default ProgramDerivedAddress find2022ATA(final PublicKey owner, final PublicKey mint) {
    return findATA(owner, solanaAccounts().token2022Program(), mint);
  }

  Instruction createAccount(final PublicKey payerKey,
                            final PublicKey newAccountPublicKey,
                            final long lamports,
                            final long space,
                            final PublicKey programOwner);

  Instruction createAccountWithSeed(final PublicKey payerKey,
                                    final AccountWithSeed accountWithSeed,
                                    final long lamports,
                                    final long space,
                                    final PublicKey programOwner);

  ProgramDerivedAddress findLookupTableAddress(final PublicKey authority, final long recentSlot);

  Instruction createLookupTable(final ProgramDerivedAddress uninitializedTableAccount,
                                final PublicKey authority,
                                final PublicKey feePayer,
                                final long recentSlot);

  default Instruction createLookupTable(final ProgramDerivedAddress uninitializedTableAccount,
                                        final PublicKey authority,
                                        final long recentSlot) {
    return createLookupTable(uninitializedTableAccount, authority, authority, recentSlot);
  }

  Instruction extendLookupTable(final PublicKey tableAccount,
                                final PublicKey authority,
                                final PublicKey feePayer,
                                final SequencedCollection<PublicKey> newAddresses);

  default Instruction extendLookupTable(final PublicKey tableAccount,
                                        final PublicKey authority,
                                        final SequencedCollection<PublicKey> newAddresses) {
    return extendLookupTable(tableAccount, authority, authority, newAddresses);
  }

  Instruction deactivateLookupTable(final PublicKey tableAccount, final PublicKey authority);

  Instruction closeLookupTable(final PublicKey tableAccount,
                               final PublicKey authority,
                               final PublicKey lamportRecipient);

  default Instruction closeLookupTable(final PublicKey tableAccount, final PublicKey authority) {
    return closeLookupTable(tableAccount, authority, authority);
  }

  Instruction freezeLookupTable(final PublicKey tableAccount, final PublicKey authority);

  default Instruction allocateStakeAccount(final PublicKey newAccountPublicKey, final PublicKey payerKey) {
    return createAccount(payerKey, newAccountPublicKey, 0L, StakeAccount.BYTES, solanaAccounts().stakeProgram());
  }

  Instruction initializeStakeAccount(final PublicKey unInitializedStakeAccount,
                                     final PublicKey staker,
                                     final PublicKey withdrawer,
                                     final LockUp lockUp);

  default Instruction initializeStakeAccount(final PublicKey unInitializedStakeAccount,
                                             final PublicKey staker,
                                             final PublicKey withdrawer) {
    return initializeStakeAccount(unInitializedStakeAccount, staker, withdrawer, LockUp.NO_LOCKUP);
  }

  Instruction initializeStakeAccountChecked(final PublicKey unInitializedStakeAccount,
                                            final PublicKey staker,
                                            final PublicKey withdrawer);

  Instruction authorizeStakeAccount(final PublicKey stakeAccount,
                                    final PublicKey stakeOrWithdrawAuthority,
                                    final PublicKey lockupAuthority,
                                    final PublicKey newAuthority,
                                    final StakeAuthorize stakeAuthorize);

  default Instruction authorizeStakeAccount(final PublicKey stakeAccount,
                                            final PublicKey stakeOrWithdrawAuthority,
                                            final PublicKey newAuthority,
                                            final StakeAuthorize stakeAuthorize) {
    return authorizeStakeAccount(stakeAccount, stakeOrWithdrawAuthority, null, newAuthority, stakeAuthorize);
  }

  default Instruction authorizeStakeAccount(final StakeAccount stakeAccount,
                                            final PublicKey newAuthority,
                                            final StakeAuthorize stakeAuthorize) {
    return authorizeStakeAccount(
        stakeAccount.address(),
        stakeAuthorize == StakeAuthorize.staker
            ? stakeAccount.stakeAuthority()
            : stakeAccount.withdrawAuthority(),
        newAuthority,
        stakeAuthorize
    );
  }

  Instruction authorizeStakeAccountChecked(final PublicKey stakeAccount,
                                           final PublicKey stakeOrWithdrawAuthority,
                                           final PublicKey newStakeOrWithdrawAuthority,
                                           final PublicKey lockupAuthority,
                                           final StakeAuthorize stakeAuthorize);

  default Instruction authorizeStakeAccountChecked(final PublicKey stakeAccount,
                                                   final PublicKey stakeOrWithdrawAuthority,
                                                   final PublicKey newStakeOrWithdrawAuthority,
                                                   final StakeAuthorize stakeAuthorize) {
    return authorizeStakeAccountChecked(
        stakeAccount, stakeOrWithdrawAuthority, newStakeOrWithdrawAuthority, null, stakeAuthorize
    );
  }

  default Instruction authorizeStakeAccountChecked(final StakeAccount stakeAccount,
                                                   final PublicKey newAuthority,
                                                   final StakeAuthorize stakeAuthorize) {
    return authorizeStakeAccountChecked(
        stakeAccount.address(),
        stakeAuthorize == StakeAuthorize.staker
            ? stakeAccount.stakeAuthority()
            : stakeAccount.withdrawAuthority(),
        newAuthority,
        stakeAuthorize
    );
  }

  Instruction authorizeStakeAccountWithSeed(final PublicKey stakeAccount,
                                            final AccountWithSeed baseKeyOrWithdrawAuthority,
                                            final PublicKey lockupAuthority,
                                            final PublicKey newAuthorizedPublicKey,
                                            final StakeAuthorize stakeAuthorize,
                                            final PublicKey authorityOwner);

  default Instruction authorizeStakeAccountWithSeed(final PublicKey stakeAccount,
                                                    final AccountWithSeed baseKeyOrWithdrawAuthority,
                                                    final PublicKey newAuthorizedPublicKey,
                                                    final StakeAuthorize stakeAuthorize,
                                                    final PublicKey authorityOwner) {
    return authorizeStakeAccountWithSeed(
        stakeAccount, baseKeyOrWithdrawAuthority, null, newAuthorizedPublicKey, stakeAuthorize, authorityOwner
    );
  }

  Instruction authorizeStakeAccountCheckedWithSeed(final PublicKey stakeAccount,
                                                   final AccountWithSeed baseKeyOrWithdrawAuthority,
                                                   final PublicKey stakeOrWithdrawAuthority,
                                                   final PublicKey lockupAuthority,
                                                   final StakeAuthorize stakeAuthorize,
                                                   final PublicKey authorityOwner);

  Instruction delegateStakeAccount(final PublicKey initializedStakeAccount,
                                   final PublicKey validatorVoteAccount,
                                   final PublicKey stakeAuthority);

  default Instruction delegateStakeAccount(final StakeAccount initializedStakeAccount,
                                           final PublicKey validatorVoteAccount) {
    return delegateStakeAccount(
        initializedStakeAccount.address(), validatorVoteAccount, initializedStakeAccount.stakeAuthority()
    );
  }

  Instruction reDelegateStakeAccount(final StakeAccount delegatedStakeAccount,
                                     final PublicKey uninitializedStakeAccount,
                                     final PublicKey validatorVoteAccount);

  Instruction splitStakeAccount(final StakeAccount splitStakeAccount,
                                final PublicKey unInitializedStakeAccount,
                                final long lamports);

  Instruction mergeStakeAccounts(final StakeAccount destinationStakeAccount,
                                 final PublicKey srcStakeAccount);

  default Instruction mergeStakeAccounts(final StakeAccount destinationStakeAccount,
                                         final StakeAccount srcStakeAccount) {
    return mergeStakeAccounts(destinationStakeAccount, srcStakeAccount.address());
  }

  default List<Instruction> mergeStakeAccountKeysInto(final StakeAccount destinationStakeAccount,
                                                      final Collection<PublicKey> stakeAccounts) {
    return stakeAccounts.stream()
        .map(stakeAccount -> mergeStakeAccounts(destinationStakeAccount, stakeAccount))
        .toList();
  }

  default List<Instruction> mergeStakeAccountsInto(final StakeAccount destinationStakeAccount,
                                                   final Collection<StakeAccount> stakeAccounts) {
    return stakeAccounts.stream()
        .map(StakeAccount::address)
        .map(stakeAccount -> mergeStakeAccounts(destinationStakeAccount, stakeAccount))
        .toList();
  }

  default List<Instruction> mergeStakeAccounts(final List<StakeAccount> stakeAccounts) {
    if (stakeAccounts.size() < 2) {
      return List.of();
    }
    final var mergeInto = stakeAccounts.getFirst();
    return stakeAccounts.stream().skip(1)
        .map(StakeAccount::address)
        .map(stakeAccount -> mergeStakeAccounts(mergeInto, stakeAccount))
        .toList();
  }

  default List<Instruction> mergeStakeAccounts(final Collection<StakeAccount> stakeAccounts) {
    if (stakeAccounts.size() < 2) {
      return List.of();
    }
    final var array = stakeAccounts.toArray(StakeAccount[]::new);
    final var mergeInto = array[0];
    return Arrays.stream(array, 1, array.length)
        .map(StakeAccount::address)
        .map(stakeAccount -> mergeStakeAccounts(mergeInto, stakeAccount))
        .toList();
  }

  Instruction withdrawStakeAccount(final StakeAccount stakeAccount,
                                   final PublicKey recipient,
                                   final long lamports);

  Instruction deactivateStakeAccount(final PublicKey delegatedStakeAccount, final PublicKey stakeAuthority);

  default Instruction deactivateStakeAccount(final StakeAccount delegatedStakeAccount) {
    return deactivateStakeAccount(delegatedStakeAccount.address(), delegatedStakeAccount.stakeAuthority());
  }

  default List<Instruction> deactivateStakeAccounts(final Collection<StakeAccount> delegatedStakeAccounts) {
    return delegatedStakeAccounts.stream().map(this::deactivateStakeAccount).toList();
  }

  Instruction deactivateDelinquentStake(final PublicKey delegatedStakeAccount,
                                        final PublicKey delinquentVoteAccount,
                                        final PublicKey referenceVoteAccount);

  Instruction setStakeAccountLockup(final PublicKey initializedStakeAccount,
                                    final PublicKey lockupOrWithdrawAuthority,
                                    final Instant timestamp,
                                    final OptionalLong epoch,
                                    final PublicKey custodian);

  Instruction setStakeAccountLockupChecked(final PublicKey initializedStakeAccount,
                                           final PublicKey lockupOrWithdrawAuthority,
                                           final PublicKey newLockupAuthority,
                                           final Instant timestamp,
                                           final OptionalLong epoch);

  default Instruction setStakeAccountLockupChecked(final PublicKey initializedStakeAccount,
                                                   final PublicKey lockupOrWithdrawAuthority,
                                                   final Instant timestamp,
                                                   final OptionalLong epoch) {
    return setStakeAccountLockupChecked(initializedStakeAccount, lockupOrWithdrawAuthority, null, timestamp, epoch);
  }

  Instruction moveStake(final PublicKey sourceStakeAccount,
                        final PublicKey destinationStakeAccount,
                        final PublicKey stakeAuthority,
                        final long lamports);

  default Instruction moveStake(final StakeAccount sourceStakeAccount,
                                final PublicKey destinationStakeAccount,
                                final long lamports) {
    return moveStake(sourceStakeAccount.address(), destinationStakeAccount, sourceStakeAccount.stakeAuthority(), lamports);
  }

  Instruction moveLamports(final PublicKey sourceStakeAccount,
                           final PublicKey destinationStakeAccount,
                           final PublicKey stakeAuthority,
                           final long lamports);

  default Instruction moveLamports(final StakeAccount sourceStakeAccount,
                                   final PublicKey destinationStakeAccount,
                                   final long lamports) {
    return moveLamports(sourceStakeAccount.address(), destinationStakeAccount, sourceStakeAccount.stakeAuthority(), lamports);
  }

  /// Creates a SetComputeUnitLimit instruction, padded to cover the consumption of the compute
  /// budget instructions themselves, for legacy and v0 transactions.
  ///
  /// Per SIMD-0385, v1 transactions ignore ComputeBudgetProgram instructions for configuration;
  /// they are processed as successful no-ops which still consume compute units. Configure a v1
  /// transaction via {@link software.sava.core.tx.TxBuilder#computeUnitLimit(int)} or update it
  /// in place via {@link software.sava.core.tx.Transaction#setComputeUnitLimit(int)} instead.
  default Instruction computeBudgetLimit(final int computeUnitLimit) {
    return setComputeUnitLimit(solanaAccounts().invokedComputeBudgetProgram(), computeUnitLimit + COMPUTE_UNITS_CONSUMED);
  }

  /// Creates a SetComputeUnitPrice instruction, priced in micro-lamports per compute unit, for
  /// legacy and v0 transactions.
  ///
  /// Per SIMD-0385, v1 transactions ignore ComputeBudgetProgram instructions for configuration;
  /// they are processed as successful no-ops which still consume compute units. Configure a v1
  /// transaction's priority fee, in total lamports, via
  /// {@link software.sava.core.tx.TxBuilder#priorityFeeLamports(long)} or update it in place via
  /// {@link software.sava.core.tx.Transaction#setPriorityFeeLamports(long)} instead.
  default Instruction computeBudgetPrice(final long microLamportComputeUnitPrice) {
    return setComputeUnitPrice(solanaAccounts().invokedComputeBudgetProgram(), microLamportComputeUnitPrice);
  }
}

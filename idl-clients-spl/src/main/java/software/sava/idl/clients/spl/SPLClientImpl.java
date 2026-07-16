package software.sava.idl.clients.spl;

import software.sava.core.accounts.AccountWithSeed;
import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.spl.associated_token.gen.AssociatedTokenPDAs;
import software.sava.idl.clients.spl.lut.gen.AddressLookupTablePDAs;
import software.sava.idl.clients.spl.lut.gen.AddressLookupTableProgram;
import software.sava.idl.clients.spl.stake.LockUp;
import software.sava.idl.clients.spl.stake.StakeAccount;
import software.sava.idl.clients.spl.stake.gen.SolanaStakeInterfaceProgram;
import software.sava.idl.clients.spl.stake.gen.types.AuthorizeCheckedWithSeedArgs;
import software.sava.idl.clients.spl.stake.gen.types.AuthorizeWithSeedArgs;
import software.sava.idl.clients.spl.stake.gen.types.Authorized;
import software.sava.idl.clients.spl.stake.gen.types.Epoch;
import software.sava.idl.clients.spl.stake.gen.types.Lockup;
import software.sava.idl.clients.spl.stake.gen.types.LockupArgs;
import software.sava.idl.clients.spl.stake.gen.types.LockupCheckedArgs;
import software.sava.idl.clients.spl.stake.gen.types.StakeAuthorize;
import software.sava.idl.clients.spl.stake.gen.types.UnixTimestamp;
import software.sava.idl.clients.spl.system.gen.SystemProgram;
import software.sava.idl.clients.spl.token.gen.TokenProgram;

import java.time.Instant;
import java.util.OptionalLong;
import java.util.SequencedCollection;

import static java.nio.charset.StandardCharsets.US_ASCII;

record SPLClientImpl(SolanaAccounts solanaAccounts) implements SPLClient {

  @Override
  public SPLAccountClient createAccountClient(final PublicKey owner, final AccountMeta feePayer) {
    return new SPLAccountClientImpl(this, owner, feePayer);
  }

  @Override
  public Instruction syncNative(final PublicKey tokenAccount) {
    return TokenProgram.syncNative(solanaAccounts.invokedTokenProgram(), null, tokenAccount);
  }

  @Override
  public ProgramDerivedAddress findATA(final PublicKey owner, final PublicKey tokenProgram, final PublicKey mint) {
    return AssociatedTokenPDAs.associatedTokenPDA(
        solanaAccounts.associatedTokenAccountProgram(),
        owner,
        tokenProgram,
        mint
    );
  }

  @Override
  public Instruction createAccount(final PublicKey payerKey,
                                   final PublicKey newAccountPublicKey,
                                   final long lamports,
                                   final long space,
                                   final PublicKey programOwner) {
    return SystemProgram.createAccount(
        solanaAccounts.invokedSystemProgram(),
        payerKey,
        newAccountPublicKey,
        lamports,
        space,
        programOwner
    );
  }

  @Override
  public Instruction createAccountWithSeed(final PublicKey payerKey,
                                           final AccountWithSeed accountWithSeed,
                                           final long lamports,
                                           final long space,
                                           final PublicKey programOwner) {
    return SystemProgram.createAccountWithSeed(
        solanaAccounts.invokedSystemProgram(),
        payerKey,
        accountWithSeed.publicKey(),
        accountWithSeed.baseKey(),
        accountWithSeed.baseKey(),
        new String(accountWithSeed.asciiSeed(), US_ASCII),
        lamports,
        space,
        programOwner
    );
  }

  @Override
  public ProgramDerivedAddress findLookupTableAddress(final PublicKey authority, final long recentSlot) {
    return AddressLookupTablePDAs.addressLookupTablePDA(
        solanaAccounts.addressLookupTableProgram(),
        authority,
        recentSlot
    );
  }

  @Override
  public Instruction createLookupTable(final ProgramDerivedAddress uninitializedTableAccount,
                                       final PublicKey authority,
                                       final PublicKey feePayer,
                                       final long recentSlot) {
    return AddressLookupTableProgram.createLookupTable(
        solanaAccounts.invokedAddressLookupTableProgram(),
        solanaAccounts,
        uninitializedTableAccount.publicKey(),
        authority,
        feePayer,
        recentSlot,
        uninitializedTableAccount.nonce()
    );
  }

  @Override
  public Instruction extendLookupTable(final PublicKey tableAccount,
                                       final PublicKey authority,
                                       final PublicKey feePayer,
                                       final SequencedCollection<PublicKey> newAddresses) {
    return AddressLookupTableProgram.extendLookupTable(
        solanaAccounts.invokedAddressLookupTableProgram(),
        solanaAccounts,
        tableAccount,
        authority,
        feePayer,
        newAddresses.toArray(PublicKey[]::new)
    );
  }

  @Override
  public Instruction deactivateLookupTable(final PublicKey tableAccount, final PublicKey authority) {
    return AddressLookupTableProgram.deactivateLookupTable(
        solanaAccounts.invokedAddressLookupTableProgram(),
        tableAccount,
        authority
    );
  }

  @Override
  public Instruction closeLookupTable(final PublicKey tableAccount,
                                      final PublicKey authority,
                                      final PublicKey lamportRecipient) {
    return AddressLookupTableProgram.closeLookupTable(
        solanaAccounts.invokedAddressLookupTableProgram(),
        tableAccount,
        authority,
        lamportRecipient
    );
  }

  @Override
  public Instruction freezeLookupTable(final PublicKey tableAccount, final PublicKey authority) {
    return AddressLookupTableProgram.freezeLookupTable(
        solanaAccounts.invokedAddressLookupTableProgram(),
        tableAccount,
        authority
    );
  }

  private static Lockup toLockup(final LockUp lockUp) {
    if (lockUp == null) {
      return new Lockup(new UnixTimestamp(0L), new Epoch(0L), PublicKey.NONE);
    }
    final var custodian = lockUp.custodian() == null ? PublicKey.NONE : lockUp.custodian();
    return new Lockup(new UnixTimestamp(lockUp.unixTimestamp()), new Epoch(lockUp.epoch()), custodian);
  }

  private static LockupArgs toLockupArgs(final Instant timestamp,
                                         final OptionalLong epoch,
                                         final PublicKey custodian) {
    final var ts = timestamp == null ? null : new UnixTimestamp(timestamp.getEpochSecond());
    final var ep = epoch.isPresent() ? new Epoch(epoch.getAsLong()) : null;
    return new LockupArgs(ts, ep, custodian);
  }

  private static LockupCheckedArgs toLockupCheckedArgs(final Instant timestamp, final OptionalLong epoch) {
    final var ts = timestamp == null ? null : new UnixTimestamp(timestamp.getEpochSecond());
    final var ep = epoch.isPresent() ? new Epoch(epoch.getAsLong()) : null;
    return new LockupCheckedArgs(ts, ep);
  }

  @Override
  public Instruction initializeStakeAccount(final PublicKey unInitializedStakeAccount,
                                            final PublicKey staker,
                                            final PublicKey withdrawer,
                                            final LockUp lockUp) {
    return SolanaStakeInterfaceProgram.initialize(
        solanaAccounts.invokedStakeProgram(),
        solanaAccounts,
        unInitializedStakeAccount,
        new Authorized(staker, withdrawer),
        toLockup(lockUp)
    );
  }

  @Override
  public Instruction initializeStakeAccountChecked(final PublicKey unInitializedStakeAccount,
                                                   final PublicKey staker,
                                                   final PublicKey withdrawer) {
    return SolanaStakeInterfaceProgram.initializeChecked(
        solanaAccounts.invokedStakeProgram(),
        solanaAccounts,
        unInitializedStakeAccount,
        staker,
        withdrawer
    );
  }

  @Override
  public Instruction authorizeStakeAccount(final PublicKey stakeAccount,
                                           final PublicKey stakeOrWithdrawAuthority,
                                           final PublicKey lockupAuthority,
                                           final PublicKey newAuthority,
                                           final StakeAuthorize stakeAuthorize) {
    return SolanaStakeInterfaceProgram.authorize(
        solanaAccounts.invokedStakeProgram(),
        solanaAccounts,
        stakeAccount,
        stakeOrWithdrawAuthority,
        lockupAuthority,
        newAuthority,
        stakeAuthorize
    );
  }

  @Override
  public Instruction authorizeStakeAccountChecked(final PublicKey stakeAccount,
                                                  final PublicKey stakeOrWithdrawAuthority,
                                                  final PublicKey newStakeOrWithdrawAuthority,
                                                  final PublicKey lockupAuthority,
                                                  final StakeAuthorize stakeAuthorize) {
    return SolanaStakeInterfaceProgram.authorizeChecked(
        solanaAccounts.invokedStakeProgram(),
        solanaAccounts,
        stakeAccount,
        stakeOrWithdrawAuthority,
        newStakeOrWithdrawAuthority,
        lockupAuthority,
        stakeAuthorize
    );
  }

  @Override
  public Instruction authorizeStakeAccountWithSeed(final PublicKey stakeAccount,
                                                   final AccountWithSeed baseKeyOrWithdrawAuthority,
                                                   final PublicKey lockupAuthority,
                                                   final PublicKey newAuthorizedPublicKey,
                                                   final StakeAuthorize stakeAuthorize,
                                                   final PublicKey authorityOwner) {
    final var args = AuthorizeWithSeedArgs.createRecord(
        newAuthorizedPublicKey,
        stakeAuthorize,
        new String(baseKeyOrWithdrawAuthority.asciiSeed(), US_ASCII),
        authorityOwner
    );
    return SolanaStakeInterfaceProgram.authorizeWithSeed(
        solanaAccounts.invokedStakeProgram(),
        solanaAccounts,
        stakeAccount,
        baseKeyOrWithdrawAuthority.baseKey(),
        lockupAuthority,
        args
    );
  }

  @Override
  public Instruction authorizeStakeAccountCheckedWithSeed(final PublicKey stakeAccount,
                                                          final AccountWithSeed baseKeyOrWithdrawAuthority,
                                                          final PublicKey stakeOrWithdrawAuthority,
                                                          final PublicKey lockupAuthority,
                                                          final StakeAuthorize stakeAuthorize,
                                                          final PublicKey authorityOwner) {
    final var args = AuthorizeCheckedWithSeedArgs.createRecord(
        stakeAuthorize,
        new String(baseKeyOrWithdrawAuthority.asciiSeed(), US_ASCII),
        authorityOwner
    );
    return SolanaStakeInterfaceProgram.authorizeCheckedWithSeed(
        solanaAccounts.invokedStakeProgram(),
        solanaAccounts,
        stakeAccount,
        baseKeyOrWithdrawAuthority.baseKey(),
        stakeOrWithdrawAuthority,
        lockupAuthority,
        args
    );
  }

  @Override
  public Instruction delegateStakeAccount(final PublicKey initializedStakeAccount,
                                          final PublicKey validatorVoteAccount,
                                          final PublicKey stakeAuthority) {
    return SolanaStakeInterfaceProgram.delegateStake(
        solanaAccounts.invokedStakeProgram(),
        solanaAccounts,
        initializedStakeAccount,
        validatorVoteAccount,
        solanaAccounts.stakeConfig(),
        stakeAuthority
    );
  }

  @Override
  public Instruction reDelegateStakeAccount(final StakeAccount delegatedStakeAccount,
                                            final PublicKey uninitializedStakeAccount,
                                            final PublicKey validatorVoteAccount) {
    return SolanaStakeInterfaceProgram.redelegate(solanaAccounts.invokedStakeProgram());
  }

  @Override
  public Instruction splitStakeAccount(final StakeAccount splitStakeAccount,
                                       final PublicKey unInitializedStakeAccount,
                                       final long lamports) {
    return SolanaStakeInterfaceProgram.split(
        solanaAccounts.invokedStakeProgram(),
        splitStakeAccount.address(),
        unInitializedStakeAccount,
        splitStakeAccount.stakeAuthority(),
        lamports
    );
  }

  @Override
  public Instruction mergeStakeAccounts(final StakeAccount destinationStakeAccount, final PublicKey srcStakeAccount) {
    return SolanaStakeInterfaceProgram.merge(
        solanaAccounts.invokedStakeProgram(),
        solanaAccounts,
        destinationStakeAccount.address(),
        srcStakeAccount,
        destinationStakeAccount.stakeAuthority()
    );
  }

  @Override
  public Instruction withdrawStakeAccount(final StakeAccount stakeAccount,
                                          final PublicKey recipient,
                                          final long lamports) {
    final var lockup = stakeAccount.lockup();
    final PublicKey lockupAuthority = (lockup == null || lockup.equals(LockUp.NO_LOCKUP)) ? null : lockup.custodian();
    return SolanaStakeInterfaceProgram.withdraw(
        solanaAccounts.invokedStakeProgram(),
        solanaAccounts,
        stakeAccount.address(),
        recipient,
        stakeAccount.withdrawAuthority(),
        lockupAuthority,
        lamports
    );
  }

  @Override
  public Instruction deactivateStakeAccount(final PublicKey delegatedStakeAccount, final PublicKey stakeAuthority) {
    return SolanaStakeInterfaceProgram.deactivate(
        solanaAccounts.invokedStakeProgram(),
        solanaAccounts,
        delegatedStakeAccount,
        stakeAuthority
    );
  }

  @Override
  public Instruction deactivateDelinquentStake(final PublicKey delegatedStakeAccount,
                                               final PublicKey delinquentVoteAccount,
                                               final PublicKey referenceVoteAccount) {
    return SolanaStakeInterfaceProgram.deactivateDelinquent(
        solanaAccounts.invokedStakeProgram(),
        delegatedStakeAccount,
        delinquentVoteAccount,
        referenceVoteAccount
    );
  }

  @Override
  public Instruction setStakeAccountLockup(final PublicKey initializedStakeAccount,
                                           final PublicKey lockupOrWithdrawAuthority,
                                           final Instant timestamp,
                                           final OptionalLong epoch,
                                           final PublicKey custodian) {
    return SolanaStakeInterfaceProgram.setLockup(
        solanaAccounts.invokedStakeProgram(),
        initializedStakeAccount,
        lockupOrWithdrawAuthority,
        toLockupArgs(timestamp, epoch, custodian)
    );
  }

  @Override
  public Instruction setStakeAccountLockupChecked(final PublicKey initializedStakeAccount,
                                                  final PublicKey lockupOrWithdrawAuthority,
                                                  final PublicKey newLockupAuthority,
                                                  final Instant timestamp,
                                                  final OptionalLong epoch) {
    return SolanaStakeInterfaceProgram.setLockupChecked(
        solanaAccounts.invokedStakeProgram(),
        initializedStakeAccount,
        lockupOrWithdrawAuthority,
        newLockupAuthority,
        toLockupCheckedArgs(timestamp, epoch)
    );
  }

  @Override
  public Instruction moveStake(final PublicKey sourceStakeAccount,
                               final PublicKey destinationStakeAccount,
                               final PublicKey stakeAuthority,
                               final long lamports) {
    return SolanaStakeInterfaceProgram.moveStake(
        solanaAccounts.invokedStakeProgram(),
        sourceStakeAccount,
        destinationStakeAccount,
        stakeAuthority,
        lamports
    );
  }

  @Override
  public Instruction moveLamports(final PublicKey sourceStakeAccount,
                                  final PublicKey destinationStakeAccount,
                                  final PublicKey stakeAuthority,
                                  final long lamports) {
    return SolanaStakeInterfaceProgram.moveLamports(
        solanaAccounts.invokedStakeProgram(),
        sourceStakeAccount,
        destinationStakeAccount,
        stakeAuthority,
        lamports
    );
  }
}

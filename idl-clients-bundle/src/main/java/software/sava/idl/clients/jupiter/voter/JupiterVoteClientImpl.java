package software.sava.idl.clients.jupiter.voter;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.JupiterAccounts;
import software.sava.idl.clients.jupiter.governance.gen.GovernProgram;
import software.sava.idl.clients.jupiter.governance.gen.types.GovernanceParameters;
import software.sava.idl.clients.jupiter.governance.gen.types.ProposalInstruction;
import software.sava.idl.clients.jupiter.merkle_distributor.gen.MerkleDistributorProgram;
import software.sava.idl.clients.jupiter.voter.gen.LockedVoterProgram;
import software.sava.idl.clients.jupiter.voter.gen.types.Escrow;
import software.sava.idl.clients.jupiter.voter.gen.types.LockerParams;
import software.sava.idl.clients.spl.SPLAccountClient;

final class JupiterVoteClientImpl implements JupiterVoteClient {

  private final SPLAccountClient splAccountClient;
  private final SolanaAccounts solanaAccounts;
  private final JupiterAccounts jupiterAccounts;
  private final PublicKey escrowOwnerKey;
  private final PublicKey ownerJupATA;
  private final PublicKey escrowKey;
  private final PublicKey escrowJupATA;
  private final PublicKey feePayer;

  JupiterVoteClientImpl(final SPLAccountClient splAccountClient, final JupiterAccounts jupiterAccounts) {
    this.splAccountClient = splAccountClient;
    this.solanaAccounts = splAccountClient.solanaAccounts();
    this.jupiterAccounts = jupiterAccounts;
    this.escrowOwnerKey = splAccountClient.owner();
    final var jupTokenMint = jupiterAccounts.jupTokenMint();
    this.ownerJupATA = splAccountClient.findATA(jupTokenMint).publicKey();
    this.escrowKey = jupiterAccounts.deriveEscrow(escrowOwnerKey).publicKey();
    this.escrowJupATA = splAccountClient.splClient().findATA(escrowKey, jupTokenMint).publicKey();
    this.feePayer = splAccountClient.feePayer().publicKey();
  }

  @Override
  public SPLAccountClient splAccountClient() {
    return splAccountClient;
  }

  @Override
  public SolanaAccounts solanaAccounts() {
    return solanaAccounts;
  }

  @Override
  public JupiterAccounts jupiterAccounts() {
    return jupiterAccounts;
  }

  @Override
  public PublicKey feePayer() {
    return feePayer;
  }

  @Override
  public PublicKey escrowOwnerKey() {
    return escrowOwnerKey;
  }

  @Override
  public PublicKey escrowOwnerKeyATA() {
    return ownerJupATA;
  }

  @Override
  public PublicKey escrowKey() {
    return escrowKey;
  }

  @Override
  public PublicKey escrowATA() {
    return escrowJupATA;
  }

  @Override
  public PublicKey deriveVoteKey(final PublicKey proposal) {
    return jupiterAccounts.deriveVote(proposal, escrowOwnerKey).publicKey();
  }

  @Override
  public Instruction newEscrow(final PublicKey escrowOwnerKey,
                               final PublicKey escrowKey,
                               final PublicKey payer) {
    return LockedVoterProgram.newEscrow(
        jupiterAccounts.invokedGovProgram(),
        jupiterAccounts.lockerKey(),
        escrowKey,
        escrowOwnerKey,
        payer,
        solanaAccounts.systemProgram()
    );
  }

  @Override
  public Instruction newVote(final PublicKey proposal,
                             final PublicKey voteKey,
                             final PublicKey payer,
                             final PublicKey voter) {
    return GovernProgram.newVote(
        jupiterAccounts.invokedGovProgram(),
        proposal,
        voteKey,
        payer,
        solanaAccounts.systemProgram(),
        voter
    );
  }

  @Override
  public Instruction castVote(final PublicKey escrowKey,
                              final PublicKey voteDelegate,
                              final PublicKey proposal,
                              final PublicKey voteKey,
                              final int side) {
    return LockedVoterProgram.castVote(
        jupiterAccounts.invokedVoteProgram(),
        jupiterAccounts.lockerKey(),
        escrowKey,
        voteDelegate,
        proposal,
        voteKey,
        jupiterAccounts.governorKey(),
        jupiterAccounts.govProgram(),
        side
    );
  }

  @Override
  public Instruction setVoteDelegate(final PublicKey escrowOwnerKey,
                                     final PublicKey escrowKey,
                                     final PublicKey newDelegate) {
    return LockedVoterProgram.setVoteDelegate(
        jupiterAccounts.invokedVoteProgram(),
        escrowKey,
        escrowOwnerKey,
        newDelegate
    );
  }

  @Override
  public Instruction increaseLockedAmount(final PublicKey escrowKey,
                                          final PublicKey escrowTokensKey,
                                          final PublicKey payerKey,
                                          final PublicKey sourceTokensKey,
                                          final long amount) {
    return LockedVoterProgram.increaseLockedAmount(
        jupiterAccounts.invokedVoteProgram(),
        jupiterAccounts.lockerKey(),
        escrowKey,
        escrowTokensKey,
        payerKey,
        sourceTokensKey,
        solanaAccounts.tokenProgram(),
        amount
    );
  }

  @Override
  public Instruction extendLockDuration(final PublicKey lockerKey,
                                        final PublicKey escrowKey,
                                        final PublicKey escrowOwnerKey,
                                        final long duration) {
    return LockedVoterProgram.extendLockDuration(
        jupiterAccounts.invokedVoteProgram(),
        lockerKey,
        escrowKey,
        escrowOwnerKey,
        duration
    );
  }

  @Override
  public Instruction toggleMaxLock(final PublicKey lockerKey,
                                   final PublicKey escrowKey,
                                   final PublicKey escrowOwnerKey,
                                   final boolean maxLock) {
    return LockedVoterProgram.toggleMaxLock(
        jupiterAccounts.invokedVoteProgram(),
        lockerKey,
        escrowKey,
        escrowOwnerKey,
        maxLock
    );
  }

  @Override
  public Instruction withdraw(final PublicKey lockerKey,
                              final PublicKey escrowKey,
                              final PublicKey escrowOwnerKey,
                              final PublicKey escrowTokensKey,
                              final PublicKey payerKey,
                              final PublicKey destinationTokensKey) {
    return LockedVoterProgram.withdraw(
        jupiterAccounts.invokedVoteProgram(),
        lockerKey,
        escrowKey,
        escrowOwnerKey,
        escrowTokensKey,
        destinationTokensKey,
        payerKey,
        solanaAccounts.tokenProgram()
    );
  }

  @Override
  public Instruction openPartialUnstaking(final PublicKey lockerKey,
                                          final PublicKey escrowKey,
                                          final PublicKey escrowOwnerKey,
                                          final PublicKey partialUnstakeKey,
                                          final long amount,
                                          final String memo) {
    return LockedVoterProgram.openPartialUnstaking(
        jupiterAccounts.invokedVoteProgram(),
        lockerKey,
        escrowKey,
        partialUnstakeKey,
        escrowOwnerKey,
        solanaAccounts.systemProgram(),
        amount,
        memo
    );
  }

  @Override
  public Instruction mergePartialUnstaking(final PublicKey lockerKey,
                                           final PublicKey escrowKey,
                                           final PublicKey escrowOwnerKey,
                                           final PublicKey partialUnstakeKey) {
    return LockedVoterProgram.mergePartialUnstaking(
        jupiterAccounts.invokedVoteProgram(),
        lockerKey,
        escrowKey,
        partialUnstakeKey,
        escrowOwnerKey
    );
  }

  @Override
  public Instruction withdrawPartialUnstaking(final PublicKey lockerKey,
                                              final PublicKey escrowKey,
                                              final PublicKey escrowOwnerKey,
                                              final PublicKey escrowTokensKey,
                                              final PublicKey partialUnstakeKey,
                                              final PublicKey payerKey,
                                              final PublicKey destinationTokensKey) {
    return LockedVoterProgram.withdrawPartialUnstaking(
        jupiterAccounts.invokedVoteProgram(),
        lockerKey,
        escrowKey,
        partialUnstakeKey,
        escrowOwnerKey,
        escrowTokensKey,
        destinationTokensKey,
        payerKey,
        solanaAccounts.tokenProgram()
    );
  }

  @Override
  public Instruction newLocker(final PublicKey baseKey,
                               final PublicKey lockerKey,
                               final PublicKey tokenMintKey,
                               final PublicKey governorKey,
                               final PublicKey payerKey,
                               final LockerParams params) {
    return LockedVoterProgram.newLocker(
        jupiterAccounts.invokedVoteProgram(),
        baseKey,
        lockerKey,
        tokenMintKey,
        governorKey,
        payerKey,
        solanaAccounts.systemProgram(),
        params
    );
  }

  @Override
  public Instruction activateProposal(final PublicKey proposalKey, final PublicKey smartWalletKey) {
    return LockedVoterProgram.activateProposal(
        jupiterAccounts.invokedVoteProgram(),
        jupiterAccounts.lockerKey(),
        jupiterAccounts.governorKey(),
        proposalKey,
        jupiterAccounts.govProgram(),
        smartWalletKey
    );

  }

  @Override
  public Instruction setLockerParams(final PublicKey smartWalletKey, final LockerParams params) {
    return LockedVoterProgram.setLockerParams(
        jupiterAccounts.invokedVoteProgram(),
        jupiterAccounts.lockerKey(),
        jupiterAccounts.governorKey(),
        smartWalletKey,
        params
    );
  }

  @Override
  public Instruction createGovernor(final PublicKey baseKey,
                                    final PublicKey governorKey,
                                    final PublicKey smartWalletKey,
                                    final PublicKey payerKey,
                                    final PublicKey locker,
                                    final GovernanceParameters params) {
    return GovernProgram.createGovernor(
        jupiterAccounts.invokedGovProgram(),
        baseKey,
        governorKey,
        smartWalletKey,
        payerKey,
        solanaAccounts.systemProgram(),
        locker,
        params
    );
  }

  @Override
  public Instruction createProposal(final PublicKey governorKey,
                                    final PublicKey proposalKey,
                                    final PublicKey smartWalletKey,
                                    final PublicKey proposerKey,
                                    final PublicKey payerKey,
                                    final PublicKey eventAuthorityKey,
                                    final int proposalType,
                                    final int maxOption,
                                    final ProposalInstruction[] instructions) {
    return GovernProgram.createProposal(
        jupiterAccounts.invokedGovProgram(),
        governorKey,
        proposalKey,
        smartWalletKey,
        proposerKey,
        payerKey,
        solanaAccounts.systemProgram(),
        eventAuthorityKey,
        jupiterAccounts.govProgram(),
        proposalType,
        maxOption,
        instructions
    );
  }

  @Override
  public Instruction newClaimAndStake(final PublicKey claimStatusKey,
                                      final PublicKey fromKey,
                                      final PublicKey distributor,
                                      final PublicKey operator,
                                      final PublicKey escrowTokensKey,
                                      final PublicKey tokenProgram,
                                      final long amountUnlocked,
                                      final long amountLocked,
                                      final byte[][] proof) {
    return MerkleDistributorProgram.newClaimAndStake(
        jupiterAccounts.invokedMerkleDistributorProgram(),
        distributor,
        claimStatusKey,
        fromKey,
        escrowOwnerKey,
        operator,
        tokenProgram,
        solanaAccounts.systemProgram(),
        jupiterAccounts.voteProgram(),
        jupiterAccounts.lockerKey(),
        escrowKey,
        escrowTokensKey,
        amountUnlocked,
        amountLocked,
        proof
    );
  }

  @Override
  public Instruction newEscrow(final PublicKey payer) {
    return newEscrow(escrowOwnerKey, escrowKey, payer);
  }

  @Override
  public Instruction newVote(final PublicKey proposal,
                             final PublicKey voteKey,
                             final PublicKey payer) {
    return newVote(proposal, voteKey, payer, escrowOwnerKey);
  }

  @Override
  public Instruction newVote(final PublicKey proposal, final PublicKey payer) {
    final var voteKey = deriveVoteKey(proposal);
    return newVote(
        proposal,
        voteKey,
        payer,
        escrowKey
    );
  }

  @Override
  public Instruction castVote(final PublicKey proposal,
                              final PublicKey voteKey,
                              final int side) {
    return castVote(
        escrowKey,
        escrowOwnerKey,
        proposal,
        voteKey,
        side
    );
  }

  @Override
  public Instruction setVoteDelegate(final PublicKey newDelegate) {
    return setVoteDelegate(escrowOwnerKey, escrowKey, newDelegate);
  }

  @Override
  public Instruction increaseLockedAmount(final PublicKey escrowTokensKey,
                                          final PublicKey payerKey,
                                          final PublicKey sourceTokensKey,
                                          final long amount) {
    return increaseLockedAmount(
        escrowKey,
        escrowTokensKey,
        payerKey,
        sourceTokensKey,
        amount
    );
  }

  @Override
  public Instruction increaseLockedAmount(final PublicKey payerKey, final long amount) {
    return increaseLockedAmount(
        escrowJupATA,
        payerKey,
        ownerJupATA,
        amount
    );
  }

  @Override
  public Instruction extendLockDuration(final Escrow escrow, final long duration) {
    return extendLockDuration(
        escrow.locker(),
        escrow._address(),
        escrow.owner(),
        duration
    );
  }

  @Override
  public Instruction extendLockDuration(final long duration) {
    return extendLockDuration(
        jupiterAccounts.lockerKey(),
        escrowKey,
        escrowOwnerKey,
        duration
    );
  }

  @Override
  public Instruction toggleMaxLock(final Escrow escrow, final boolean maxLock) {
    return toggleMaxLock(
        escrow.locker(),
        escrow._address(),
        escrow.owner(),
        maxLock
    );
  }

  @Override
  public Instruction toggleMaxLock(final PublicKey escrowOwner,
                                   final PublicKey escrowKey,
                                   final boolean maxLock) {
    return toggleMaxLock(
        jupiterAccounts.lockerKey(),
        escrowKey,
        escrowOwner,
        maxLock
    );
  }

  @Override
  public Instruction toggleMaxLock(final boolean maxLock) {
    return toggleMaxLock(
        jupiterAccounts.lockerKey(),
        escrowKey,
        escrowOwnerKey,
        maxLock
    );
  }

  @Override
  public Instruction withdraw(final Escrow escrow,
                              final PublicKey payerKey,
                              final PublicKey destinationTokensKey) {
    return withdraw(
        escrow.locker(),
        escrow._address(),
        escrow.owner(),
        escrow.tokens(),
        payerKey,
        destinationTokensKey
    );
  }

  @Override
  public Instruction withdraw(final PublicKey payerKey) {
    return withdraw(
        jupiterAccounts.lockerKey(),
        escrowKey,
        escrowOwnerKey,
        escrowJupATA,
        payerKey,
        ownerJupATA
    );
  }

  @Override
  public Instruction withdraw() {
    return withdraw(escrowOwnerKey);
  }

  @Override
  public Instruction withdraw(final Escrow escrow) {
    return withdraw(escrow, escrow.owner(), ownerJupATA);
  }

  @Override
  public Instruction openPartialUnstaking(final Escrow escrow,
                                          final PublicKey partialUnstakeKey,
                                          final long amount,
                                          final String memo) {
    return openPartialUnstaking(
        escrow.locker(),
        escrow._address(),
        escrow.owner(),
        partialUnstakeKey,
        amount,
        memo
    );
  }

  @Override
  public Instruction openPartialUnstaking(final PublicKey partialUnstakeKey,
                                          final long amount,
                                          final String memo) {
    return openPartialUnstaking(
        jupiterAccounts.lockerKey(),
        escrowKey,
        escrowOwnerKey,
        partialUnstakeKey,
        amount,
        memo
    );
  }

  @Override
  public Instruction mergePartialUnstaking(final Escrow escrow, final PublicKey partialUnstakeKey) {
    return mergePartialUnstaking(
        escrow.locker(),
        escrow._address(),
        escrow.owner(),
        partialUnstakeKey
    );
  }

  @Override
  public Instruction mergePartialUnstaking(final PublicKey partialUnstakeKey) {
    return mergePartialUnstaking(
        jupiterAccounts.lockerKey(),
        escrowKey,
        escrowOwnerKey,
        partialUnstakeKey
    );
  }

  @Override
  public Instruction withdrawPartialUnstaking(final Escrow escrow,
                                              final PublicKey partialUnstakeKey,
                                              final PublicKey payerKey,
                                              final PublicKey destinationTokensKey) {
    return withdrawPartialUnstaking(
        escrow.locker(),
        escrow._address(),
        escrow.owner(),
        escrow.tokens(),
        partialUnstakeKey,
        payerKey,
        destinationTokensKey
    );
  }

  @Override
  public Instruction withdrawPartialUnstaking(final PublicKey partialUnstakeKey, final PublicKey payerKey) {
    return withdrawPartialUnstaking(
        jupiterAccounts.lockerKey(),
        escrowKey,
        escrowOwnerKey,
        escrowJupATA,
        partialUnstakeKey,
        payerKey,
        ownerJupATA
    );
  }
}

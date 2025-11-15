package software.sava.idl.clients.jupiter.governance.gen;

import java.lang.String;

import java.util.Arrays;
import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.governance.gen.types.GovernanceParameters;
import software.sava.idl.clients.jupiter.governance.gen.types.ProposalInstruction;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class GovernProgram {

  public static final Discriminator CREATE_GOVERNOR_DISCRIMINATOR = toDiscriminator(103, 30, 78, 252, 28, 128, 40, 3);

  /// Creates a Governor.
  ///
  /// @param baseKey Base of the Governor key.
  /// @param governorKey Governor.
  /// @param smartWalletKey The Smart Wallet.
  /// @param payerKey Payer.
  /// @param systemProgramKey System program.
  public static List<AccountMeta> createGovernorKeys(final PublicKey baseKey,
                                                     final PublicKey governorKey,
                                                     final PublicKey smartWalletKey,
                                                     final PublicKey payerKey,
                                                     final PublicKey systemProgramKey) {
    return List.of(
      createReadOnlySigner(baseKey),
      createWrite(governorKey),
      createRead(smartWalletKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey)
    );
  }

  /// Creates a Governor.
  ///
  /// @param baseKey Base of the Governor key.
  /// @param governorKey Governor.
  /// @param smartWalletKey The Smart Wallet.
  /// @param payerKey Payer.
  /// @param systemProgramKey System program.
  public static Instruction createGovernor(final AccountMeta invokedGovernProgramMeta,
                                           final PublicKey baseKey,
                                           final PublicKey governorKey,
                                           final PublicKey smartWalletKey,
                                           final PublicKey payerKey,
                                           final PublicKey systemProgramKey,
                                           final PublicKey locker,
                                           final GovernanceParameters params) {
    final var keys = createGovernorKeys(
      baseKey,
      governorKey,
      smartWalletKey,
      payerKey,
      systemProgramKey
    );
    return createGovernor(invokedGovernProgramMeta, keys, locker, params);
  }

  /// Creates a Governor.
  ///
  public static Instruction createGovernor(final AccountMeta invokedGovernProgramMeta,
                                           final List<AccountMeta> keys,
                                           final PublicKey locker,
                                           final GovernanceParameters params) {
    final byte[] _data = new byte[40 + Borsh.len(params)];
    int i = CREATE_GOVERNOR_DISCRIMINATOR.write(_data, 0);
    locker.write(_data, i);
    i += 32;
    Borsh.write(params, _data, i);

    return Instruction.createInstruction(invokedGovernProgramMeta, keys, _data);
  }

  public record CreateGovernorIxData(Discriminator discriminator, PublicKey locker, GovernanceParameters params) implements Borsh {  

    public static CreateGovernorIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 72;

    public static CreateGovernorIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var locker = readPubKey(_data, i);
      i += 32;
      final var params = GovernanceParameters.read(_data, i);
      return new CreateGovernorIxData(discriminator, locker, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      locker.write(_data, i);
      i += 32;
      i += Borsh.write(params, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CREATE_PROPOSAL_DISCRIMINATOR = toDiscriminator(132, 116, 68, 174, 216, 160, 198, 22);

  /// Creates a Proposal.
  /// This may be called by anyone, since the Proposal does not do anything until
  /// it is activated in activate_proposal.
  ///
  /// @param governorKey The Governor.
  /// @param proposalKey The Proposal.
  /// @param smartWalletKey smart wallet of governor
  /// @param proposerKey Proposer of the proposal.
  ///                    One of the owners. Checked in the handler via SmartWallet::owner_index.
  /// @param payerKey Payer of the proposal.
  /// @param systemProgramKey System program.
  public static List<AccountMeta> createProposalKeys(final PublicKey governorKey,
                                                     final PublicKey proposalKey,
                                                     final PublicKey smartWalletKey,
                                                     final PublicKey proposerKey,
                                                     final PublicKey payerKey,
                                                     final PublicKey systemProgramKey,
                                                     final PublicKey eventAuthorityKey,
                                                     final PublicKey programKey) {
    return List.of(
      createWrite(governorKey),
      createWrite(proposalKey),
      createRead(smartWalletKey),
      createReadOnlySigner(proposerKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// Creates a Proposal.
  /// This may be called by anyone, since the Proposal does not do anything until
  /// it is activated in activate_proposal.
  ///
  /// @param governorKey The Governor.
  /// @param proposalKey The Proposal.
  /// @param smartWalletKey smart wallet of governor
  /// @param proposerKey Proposer of the proposal.
  ///                    One of the owners. Checked in the handler via SmartWallet::owner_index.
  /// @param payerKey Payer of the proposal.
  /// @param systemProgramKey System program.
  public static Instruction createProposal(final AccountMeta invokedGovernProgramMeta,
                                           final PublicKey governorKey,
                                           final PublicKey proposalKey,
                                           final PublicKey smartWalletKey,
                                           final PublicKey proposerKey,
                                           final PublicKey payerKey,
                                           final PublicKey systemProgramKey,
                                           final PublicKey eventAuthorityKey,
                                           final PublicKey programKey,
                                           final int proposalType,
                                           final int maxOption,
                                           final ProposalInstruction[] instructions) {
    final var keys = createProposalKeys(
      governorKey,
      proposalKey,
      smartWalletKey,
      proposerKey,
      payerKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return createProposal(
      invokedGovernProgramMeta,
      keys,
      proposalType,
      maxOption,
      instructions
    );
  }

  /// Creates a Proposal.
  /// This may be called by anyone, since the Proposal does not do anything until
  /// it is activated in activate_proposal.
  ///
  public static Instruction createProposal(final AccountMeta invokedGovernProgramMeta,
                                           final List<AccountMeta> keys,
                                           final int proposalType,
                                           final int maxOption,
                                           final ProposalInstruction[] instructions) {
    final byte[] _data = new byte[10 + Borsh.lenVector(instructions)];
    int i = CREATE_PROPOSAL_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) proposalType;
    ++i;
    _data[i] = (byte) maxOption;
    ++i;
    Borsh.writeVector(instructions, _data, i);

    return Instruction.createInstruction(invokedGovernProgramMeta, keys, _data);
  }

  public record CreateProposalIxData(Discriminator discriminator,
                                     int proposalType,
                                     int maxOption,
                                     ProposalInstruction[] instructions) implements Borsh {  

    public static CreateProposalIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static CreateProposalIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var proposalType = _data[i] & 0xFF;
      ++i;
      final var maxOption = _data[i] & 0xFF;
      ++i;
      final var instructions = Borsh.readVector(ProposalInstruction.class, ProposalInstruction::read, _data, i);
      return new CreateProposalIxData(discriminator, proposalType, maxOption, instructions);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) proposalType;
      ++i;
      _data[i] = (byte) maxOption;
      ++i;
      i += Borsh.writeVector(instructions, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 1 + 1 + Borsh.lenVector(instructions);
    }
  }

  public static final Discriminator ACTIVATE_PROPOSAL_DISCRIMINATOR = toDiscriminator(90, 186, 203, 234, 70, 185, 191, 21);

  /// Activates a proposal.
  /// Only the Governor::voter may call this; that program
  /// may ensure that only certain types of users can activate proposals.
  ///
  /// @param governorKey The Governor.
  /// @param proposalKey The Proposal to activate.
  /// @param lockerKey The locker of the Governor that may activate the proposal.
  public static List<AccountMeta> activateProposalKeys(final PublicKey governorKey,
                                                       final PublicKey proposalKey,
                                                       final PublicKey lockerKey) {
    return List.of(
      createRead(governorKey),
      createWrite(proposalKey),
      createReadOnlySigner(lockerKey)
    );
  }

  /// Activates a proposal.
  /// Only the Governor::voter may call this; that program
  /// may ensure that only certain types of users can activate proposals.
  ///
  /// @param governorKey The Governor.
  /// @param proposalKey The Proposal to activate.
  /// @param lockerKey The locker of the Governor that may activate the proposal.
  public static Instruction activateProposal(final AccountMeta invokedGovernProgramMeta,
                                             final PublicKey governorKey,
                                             final PublicKey proposalKey,
                                             final PublicKey lockerKey) {
    final var keys = activateProposalKeys(
      governorKey,
      proposalKey,
      lockerKey
    );
    return activateProposal(invokedGovernProgramMeta, keys);
  }

  /// Activates a proposal.
  /// Only the Governor::voter may call this; that program
  /// may ensure that only certain types of users can activate proposals.
  ///
  public static Instruction activateProposal(final AccountMeta invokedGovernProgramMeta,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedGovernProgramMeta, keys, ACTIVATE_PROPOSAL_DISCRIMINATOR);
  }

  public static final Discriminator CANCEL_PROPOSAL_DISCRIMINATOR = toDiscriminator(106, 74, 128, 146, 19, 65, 39, 23);

  /// Cancels a proposal.
  /// This is only callable by the creator of the proposal.
  ///
  /// @param governorKey The Governor.
  /// @param proposalKey The Proposal to activate.
  /// @param proposerKey The Proposal::proposer.
  public static List<AccountMeta> cancelProposalKeys(final PublicKey governorKey,
                                                     final PublicKey proposalKey,
                                                     final PublicKey proposerKey,
                                                     final PublicKey eventAuthorityKey,
                                                     final PublicKey programKey) {
    return List.of(
      createRead(governorKey),
      createWrite(proposalKey),
      createReadOnlySigner(proposerKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// Cancels a proposal.
  /// This is only callable by the creator of the proposal.
  ///
  /// @param governorKey The Governor.
  /// @param proposalKey The Proposal to activate.
  /// @param proposerKey The Proposal::proposer.
  public static Instruction cancelProposal(final AccountMeta invokedGovernProgramMeta,
                                           final PublicKey governorKey,
                                           final PublicKey proposalKey,
                                           final PublicKey proposerKey,
                                           final PublicKey eventAuthorityKey,
                                           final PublicKey programKey) {
    final var keys = cancelProposalKeys(
      governorKey,
      proposalKey,
      proposerKey,
      eventAuthorityKey,
      programKey
    );
    return cancelProposal(invokedGovernProgramMeta, keys);
  }

  /// Cancels a proposal.
  /// This is only callable by the creator of the proposal.
  ///
  public static Instruction cancelProposal(final AccountMeta invokedGovernProgramMeta,
                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedGovernProgramMeta, keys, CANCEL_PROPOSAL_DISCRIMINATOR);
  }

  public static final Discriminator QUEUE_PROPOSAL_DISCRIMINATOR = toDiscriminator(168, 219, 139, 211, 205, 152, 125, 110);

  /// Queues a proposal for execution by the SmartWallet.
  ///
  /// @param governorKey The Governor.
  /// @param proposalKey The Proposal to queue.
  /// @param transactionKey The transaction key of the proposal.
  ///                       This account is passed to and validated by the Smart Wallet program to be initialized.
  /// @param smartWalletKey The Smart Wallet.
  /// @param payerKey Payer of the queued transaction.
  /// @param smartWalletProgramKey The Smart Wallet program.
  /// @param systemProgramKey The System program.
  public static List<AccountMeta> queueProposalKeys(final PublicKey governorKey,
                                                    final PublicKey proposalKey,
                                                    final PublicKey transactionKey,
                                                    final PublicKey smartWalletKey,
                                                    final PublicKey payerKey,
                                                    final PublicKey smartWalletProgramKey,
                                                    final PublicKey systemProgramKey,
                                                    final PublicKey eventAuthorityKey,
                                                    final PublicKey programKey) {
    return List.of(
      createRead(governorKey),
      createWrite(proposalKey),
      createWrite(transactionKey),
      createWrite(smartWalletKey),
      createWritableSigner(payerKey),
      createRead(smartWalletProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// Queues a proposal for execution by the SmartWallet.
  ///
  /// @param governorKey The Governor.
  /// @param proposalKey The Proposal to queue.
  /// @param transactionKey The transaction key of the proposal.
  ///                       This account is passed to and validated by the Smart Wallet program to be initialized.
  /// @param smartWalletKey The Smart Wallet.
  /// @param payerKey Payer of the queued transaction.
  /// @param smartWalletProgramKey The Smart Wallet program.
  /// @param systemProgramKey The System program.
  public static Instruction queueProposal(final AccountMeta invokedGovernProgramMeta,
                                          final PublicKey governorKey,
                                          final PublicKey proposalKey,
                                          final PublicKey transactionKey,
                                          final PublicKey smartWalletKey,
                                          final PublicKey payerKey,
                                          final PublicKey smartWalletProgramKey,
                                          final PublicKey systemProgramKey,
                                          final PublicKey eventAuthorityKey,
                                          final PublicKey programKey) {
    final var keys = queueProposalKeys(
      governorKey,
      proposalKey,
      transactionKey,
      smartWalletKey,
      payerKey,
      smartWalletProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return queueProposal(invokedGovernProgramMeta, keys);
  }

  /// Queues a proposal for execution by the SmartWallet.
  ///
  public static Instruction queueProposal(final AccountMeta invokedGovernProgramMeta,
                                          final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedGovernProgramMeta, keys, QUEUE_PROPOSAL_DISCRIMINATOR);
  }

  public static final Discriminator NEW_VOTE_DISCRIMINATOR = toDiscriminator(163, 108, 157, 189, 140, 80, 13, 143);

  /// Creates a new Vote. Anyone can call this.
  ///
  /// @param proposalKey Proposal being voted on.
  /// @param voteKey The vote.
  /// @param payerKey Payer of the Vote.
  /// @param systemProgramKey System program.
  public static List<AccountMeta> newVoteKeys(final PublicKey proposalKey,
                                              final PublicKey voteKey,
                                              final PublicKey payerKey,
                                              final PublicKey systemProgramKey) {
    return List.of(
      createRead(proposalKey),
      createWrite(voteKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey)
    );
  }

  /// Creates a new Vote. Anyone can call this.
  ///
  /// @param proposalKey Proposal being voted on.
  /// @param voteKey The vote.
  /// @param payerKey Payer of the Vote.
  /// @param systemProgramKey System program.
  public static Instruction newVote(final AccountMeta invokedGovernProgramMeta,
                                    final PublicKey proposalKey,
                                    final PublicKey voteKey,
                                    final PublicKey payerKey,
                                    final PublicKey systemProgramKey,
                                    final PublicKey voter) {
    final var keys = newVoteKeys(
      proposalKey,
      voteKey,
      payerKey,
      systemProgramKey
    );
    return newVote(invokedGovernProgramMeta, keys, voter);
  }

  /// Creates a new Vote. Anyone can call this.
  ///
  public static Instruction newVote(final AccountMeta invokedGovernProgramMeta,
                                    final List<AccountMeta> keys,
                                    final PublicKey voter) {
    final byte[] _data = new byte[40];
    int i = NEW_VOTE_DISCRIMINATOR.write(_data, 0);
    voter.write(_data, i);

    return Instruction.createInstruction(invokedGovernProgramMeta, keys, _data);
  }

  public record NewVoteIxData(Discriminator discriminator, PublicKey voter) implements Borsh {  

    public static NewVoteIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static NewVoteIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var voter = readPubKey(_data, i);
      return new NewVoteIxData(discriminator, voter);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      voter.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_VOTE_DISCRIMINATOR = toDiscriminator(171, 33, 83, 172, 148, 215, 239, 97);

  /// Sets a Vote weight and side.
  /// This may only be called by the Governor::voter.
  ///
  /// @param governorKey The Governor.
  /// @param proposalKey The Proposal.
  /// @param voteKey The Vote.
  /// @param lockerKey The Governor::locker.
  public static List<AccountMeta> setVoteKeys(final PublicKey governorKey,
                                              final PublicKey proposalKey,
                                              final PublicKey voteKey,
                                              final PublicKey lockerKey) {
    return List.of(
      createRead(governorKey),
      createWrite(proposalKey),
      createWrite(voteKey),
      createReadOnlySigner(lockerKey)
    );
  }

  /// Sets a Vote weight and side.
  /// This may only be called by the Governor::voter.
  ///
  /// @param governorKey The Governor.
  /// @param proposalKey The Proposal.
  /// @param voteKey The Vote.
  /// @param lockerKey The Governor::locker.
  public static Instruction setVote(final AccountMeta invokedGovernProgramMeta,
                                    final PublicKey governorKey,
                                    final PublicKey proposalKey,
                                    final PublicKey voteKey,
                                    final PublicKey lockerKey,
                                    final int side,
                                    final long weight) {
    final var keys = setVoteKeys(
      governorKey,
      proposalKey,
      voteKey,
      lockerKey
    );
    return setVote(invokedGovernProgramMeta, keys, side, weight);
  }

  /// Sets a Vote weight and side.
  /// This may only be called by the Governor::voter.
  ///
  public static Instruction setVote(final AccountMeta invokedGovernProgramMeta,
                                    final List<AccountMeta> keys,
                                    final int side,
                                    final long weight) {
    final byte[] _data = new byte[17];
    int i = SET_VOTE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) side;
    ++i;
    putInt64LE(_data, i, weight);

    return Instruction.createInstruction(invokedGovernProgramMeta, keys, _data);
  }

  public record SetVoteIxData(Discriminator discriminator, int side, long weight) implements Borsh {  

    public static SetVoteIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 17;

    public static SetVoteIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var side = _data[i] & 0xFF;
      ++i;
      final var weight = getInt64LE(_data, i);
      return new SetVoteIxData(discriminator, side, weight);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) side;
      ++i;
      putInt64LE(_data, i, weight);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_GOVERNANCE_PARAMS_DISCRIMINATOR = toDiscriminator(175, 187, 3, 73, 8, 251, 67, 178);

  /// Sets the GovernanceParameters.
  /// This may only be called by the Governor::smart_wallet.
  ///
  /// @param governorKey The Governor
  /// @param smartWalletKey The Smart Wallet.
  public static List<AccountMeta> setGovernanceParamsKeys(final PublicKey governorKey,
                                                          final PublicKey smartWalletKey) {
    return List.of(
      createWrite(governorKey),
      createReadOnlySigner(smartWalletKey)
    );
  }

  /// Sets the GovernanceParameters.
  /// This may only be called by the Governor::smart_wallet.
  ///
  /// @param governorKey The Governor
  /// @param smartWalletKey The Smart Wallet.
  public static Instruction setGovernanceParams(final AccountMeta invokedGovernProgramMeta,
                                                final PublicKey governorKey,
                                                final PublicKey smartWalletKey,
                                                final GovernanceParameters params) {
    final var keys = setGovernanceParamsKeys(
      governorKey,
      smartWalletKey
    );
    return setGovernanceParams(invokedGovernProgramMeta, keys, params);
  }

  /// Sets the GovernanceParameters.
  /// This may only be called by the Governor::smart_wallet.
  ///
  public static Instruction setGovernanceParams(final AccountMeta invokedGovernProgramMeta,
                                                final List<AccountMeta> keys,
                                                final GovernanceParameters params) {
    final byte[] _data = new byte[8 + Borsh.len(params)];
    int i = SET_GOVERNANCE_PARAMS_DISCRIMINATOR.write(_data, 0);
    Borsh.write(params, _data, i);

    return Instruction.createInstruction(invokedGovernProgramMeta, keys, _data);
  }

  public record SetGovernanceParamsIxData(Discriminator discriminator, GovernanceParameters params) implements Borsh {  

    public static SetGovernanceParamsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static SetGovernanceParamsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = GovernanceParameters.read(_data, i);
      return new SetGovernanceParamsIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(params, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_VOTING_REWARD_DISCRIMINATOR = toDiscriminator(227, 241, 48, 137, 30, 26, 104, 70);

  /// Sets Voting Reward.
  /// This may only be called by the Governor::smart_wallet.
  ///
  /// @param governorKey The Governor
  /// @param rewardMintKey reward mint
  /// @param smartWalletKey The Smart Wallet.
  public static List<AccountMeta> setVotingRewardKeys(final PublicKey governorKey,
                                                      final PublicKey rewardMintKey,
                                                      final PublicKey smartWalletKey) {
    return List.of(
      createWrite(governorKey),
      createRead(rewardMintKey),
      createReadOnlySigner(smartWalletKey)
    );
  }

  /// Sets Voting Reward.
  /// This may only be called by the Governor::smart_wallet.
  ///
  /// @param governorKey The Governor
  /// @param rewardMintKey reward mint
  /// @param smartWalletKey The Smart Wallet.
  public static Instruction setVotingReward(final AccountMeta invokedGovernProgramMeta,
                                            final PublicKey governorKey,
                                            final PublicKey rewardMintKey,
                                            final PublicKey smartWalletKey,
                                            final long rewardPerProposal) {
    final var keys = setVotingRewardKeys(
      governorKey,
      rewardMintKey,
      smartWalletKey
    );
    return setVotingReward(invokedGovernProgramMeta, keys, rewardPerProposal);
  }

  /// Sets Voting Reward.
  /// This may only be called by the Governor::smart_wallet.
  ///
  public static Instruction setVotingReward(final AccountMeta invokedGovernProgramMeta,
                                            final List<AccountMeta> keys,
                                            final long rewardPerProposal) {
    final byte[] _data = new byte[16];
    int i = SET_VOTING_REWARD_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, rewardPerProposal);

    return Instruction.createInstruction(invokedGovernProgramMeta, keys, _data);
  }

  public record SetVotingRewardIxData(Discriminator discriminator, long rewardPerProposal) implements Borsh {  

    public static SetVotingRewardIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static SetVotingRewardIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var rewardPerProposal = getInt64LE(_data, i);
      return new SetVotingRewardIxData(discriminator, rewardPerProposal);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, rewardPerProposal);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CLAIM_REWARD_DISCRIMINATOR = toDiscriminator(149, 95, 181, 242, 94, 90, 158, 162);

  /// Claim rewards, for voter
  ///
  /// @param governorKey The Governor
  /// @param rewardVaultKey reward mint
  /// @param proposalKey proposal
  /// @param voteKey The Vote.
  /// @param voterKey Owner of the vault
  ///                 TODO: check whether vote delegrate can claim on behalf of owner?
  /// @param voterTokenAccountKey Voter token account
  /// @param tokenProgramKey Token program.
  public static List<AccountMeta> claimRewardKeys(final PublicKey governorKey,
                                                  final PublicKey rewardVaultKey,
                                                  final PublicKey proposalKey,
                                                  final PublicKey voteKey,
                                                  final PublicKey voterKey,
                                                  final PublicKey voterTokenAccountKey,
                                                  final PublicKey tokenProgramKey,
                                                  final PublicKey eventAuthorityKey,
                                                  final PublicKey programKey) {
    return List.of(
      createWrite(governorKey),
      createWrite(rewardVaultKey),
      createWrite(proposalKey),
      createWrite(voteKey),
      createReadOnlySigner(voterKey),
      createWrite(voterTokenAccountKey),
      createRead(tokenProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// Claim rewards, for voter
  ///
  /// @param governorKey The Governor
  /// @param rewardVaultKey reward mint
  /// @param proposalKey proposal
  /// @param voteKey The Vote.
  /// @param voterKey Owner of the vault
  ///                 TODO: check whether vote delegrate can claim on behalf of owner?
  /// @param voterTokenAccountKey Voter token account
  /// @param tokenProgramKey Token program.
  public static Instruction claimReward(final AccountMeta invokedGovernProgramMeta,
                                        final PublicKey governorKey,
                                        final PublicKey rewardVaultKey,
                                        final PublicKey proposalKey,
                                        final PublicKey voteKey,
                                        final PublicKey voterKey,
                                        final PublicKey voterTokenAccountKey,
                                        final PublicKey tokenProgramKey,
                                        final PublicKey eventAuthorityKey,
                                        final PublicKey programKey) {
    final var keys = claimRewardKeys(
      governorKey,
      rewardVaultKey,
      proposalKey,
      voteKey,
      voterKey,
      voterTokenAccountKey,
      tokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return claimReward(invokedGovernProgramMeta, keys);
  }

  /// Claim rewards, for voter
  ///
  public static Instruction claimReward(final AccountMeta invokedGovernProgramMeta,
                                        final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedGovernProgramMeta, keys, CLAIM_REWARD_DISCRIMINATOR);
  }

  public static final Discriminator SET_LOCKER_DISCRIMINATOR = toDiscriminator(17, 6, 101, 72, 250, 23, 152, 96);

  /// Sets the locker of the Governor.
  ///
  /// @param governorKey The Governor
  /// @param smartWalletKey The Smart Wallet.
  public static List<AccountMeta> setLockerKeys(final PublicKey governorKey,
                                                final PublicKey smartWalletKey) {
    return List.of(
      createWrite(governorKey),
      createReadOnlySigner(smartWalletKey)
    );
  }

  /// Sets the locker of the Governor.
  ///
  /// @param governorKey The Governor
  /// @param smartWalletKey The Smart Wallet.
  public static Instruction setLocker(final AccountMeta invokedGovernProgramMeta,
                                      final PublicKey governorKey,
                                      final PublicKey smartWalletKey,
                                      final PublicKey newLocker) {
    final var keys = setLockerKeys(
      governorKey,
      smartWalletKey
    );
    return setLocker(invokedGovernProgramMeta, keys, newLocker);
  }

  /// Sets the locker of the Governor.
  ///
  public static Instruction setLocker(final AccountMeta invokedGovernProgramMeta,
                                      final List<AccountMeta> keys,
                                      final PublicKey newLocker) {
    final byte[] _data = new byte[40];
    int i = SET_LOCKER_DISCRIMINATOR.write(_data, 0);
    newLocker.write(_data, i);

    return Instruction.createInstruction(invokedGovernProgramMeta, keys, _data);
  }

  public record SetLockerIxData(Discriminator discriminator, PublicKey newLocker) implements Borsh {  

    public static SetLockerIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static SetLockerIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var newLocker = readPubKey(_data, i);
      return new SetLockerIxData(discriminator, newLocker);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      newLocker.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CREATE_PROPOSAL_META_DISCRIMINATOR = toDiscriminator(238, 138, 212, 160, 46, 53, 51, 88);

  /// Creates a ProposalMeta.
  ///
  /// @param proposalKey The Proposal.
  /// @param proposerKey Proposer of the proposal.
  /// @param proposalMetaKey The ProposalMeta.
  /// @param payerKey Payer of the ProposalMeta.
  /// @param systemProgramKey System program.
  public static List<AccountMeta> createProposalMetaKeys(final PublicKey proposalKey,
                                                         final PublicKey proposerKey,
                                                         final PublicKey proposalMetaKey,
                                                         final PublicKey payerKey,
                                                         final PublicKey systemProgramKey,
                                                         final PublicKey eventAuthorityKey,
                                                         final PublicKey programKey) {
    return List.of(
      createRead(proposalKey),
      createReadOnlySigner(proposerKey),
      createWrite(proposalMetaKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// Creates a ProposalMeta.
  ///
  /// @param proposalKey The Proposal.
  /// @param proposerKey Proposer of the proposal.
  /// @param proposalMetaKey The ProposalMeta.
  /// @param payerKey Payer of the ProposalMeta.
  /// @param systemProgramKey System program.
  public static Instruction createProposalMeta(final AccountMeta invokedGovernProgramMeta,
                                               final PublicKey proposalKey,
                                               final PublicKey proposerKey,
                                               final PublicKey proposalMetaKey,
                                               final PublicKey payerKey,
                                               final PublicKey systemProgramKey,
                                               final PublicKey eventAuthorityKey,
                                               final PublicKey programKey,
                                               final int bump,
                                               final String title,
                                               final String descriptionLink) {
    final var keys = createProposalMetaKeys(
      proposalKey,
      proposerKey,
      proposalMetaKey,
      payerKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return createProposalMeta(
      invokedGovernProgramMeta,
      keys,
      bump,
      title,
      descriptionLink
    );
  }

  /// Creates a ProposalMeta.
  ///
  public static Instruction createProposalMeta(final AccountMeta invokedGovernProgramMeta,
                                               final List<AccountMeta> keys,
                                               final int bump,
                                               final String title,
                                               final String descriptionLink) {
    final byte[] _title = title.getBytes(UTF_8);
    final byte[] _descriptionLink = descriptionLink.getBytes(UTF_8);
    final byte[] _data = new byte[17 + _title.length + _descriptionLink.length];
    int i = CREATE_PROPOSAL_META_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) bump;
    ++i;
    i += Borsh.writeVector(_title, _data, i);
    Borsh.writeVector(_descriptionLink, _data, i);

    return Instruction.createInstruction(invokedGovernProgramMeta, keys, _data);
  }

  public record CreateProposalMetaIxData(Discriminator discriminator,
                                         int bump,
                                         String title, byte[] _title,
                                         String descriptionLink, byte[] _descriptionLink) implements Borsh {  

    public static CreateProposalMetaIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static CreateProposalMetaIxData createRecord(final Discriminator discriminator,
                                                        final int bump,
                                                        final String title,
                                                        final String descriptionLink) {
      return new CreateProposalMetaIxData(discriminator, bump, title, title.getBytes(UTF_8), descriptionLink, descriptionLink.getBytes(UTF_8));
    }

    public static CreateProposalMetaIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var bump = _data[i] & 0xFF;
      ++i;
      final int _titleLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _title = Arrays.copyOfRange(_data, i, i + _titleLength);
      final var title = new String(_title, UTF_8);
      i += _title.length;
      final int _descriptionLinkLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _descriptionLink = Arrays.copyOfRange(_data, i, i + _descriptionLinkLength);
      final var descriptionLink = new String(_descriptionLink, UTF_8);
      return new CreateProposalMetaIxData(discriminator, bump, title, _title, descriptionLink, _descriptionLink);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) bump;
      ++i;
      i += Borsh.writeVector(_title, _data, i);
      i += Borsh.writeVector(_descriptionLink, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 1 + _title.length + _descriptionLink.length;
    }
  }

  public static final Discriminator CREATE_OPTION_PROPOSAL_META_DISCRIMINATOR = toDiscriminator(152, 144, 104, 228, 245, 234, 164, 224);

  /// Creates an OptionProposalMeta.
  ///
  /// @param proposalKey The Proposal.
  /// @param proposerKey Proposer of the proposal.
  /// @param optionProposalMetaKey The ProposalMeta.
  /// @param payerKey Payer of the ProposalMeta.
  /// @param systemProgramKey System program.
  public static List<AccountMeta> createOptionProposalMetaKeys(final PublicKey proposalKey,
                                                               final PublicKey proposerKey,
                                                               final PublicKey optionProposalMetaKey,
                                                               final PublicKey payerKey,
                                                               final PublicKey systemProgramKey,
                                                               final PublicKey eventAuthorityKey,
                                                               final PublicKey programKey) {
    return List.of(
      createRead(proposalKey),
      createReadOnlySigner(proposerKey),
      createWrite(optionProposalMetaKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// Creates an OptionProposalMeta.
  ///
  /// @param proposalKey The Proposal.
  /// @param proposerKey Proposer of the proposal.
  /// @param optionProposalMetaKey The ProposalMeta.
  /// @param payerKey Payer of the ProposalMeta.
  /// @param systemProgramKey System program.
  public static Instruction createOptionProposalMeta(final AccountMeta invokedGovernProgramMeta,
                                                     final PublicKey proposalKey,
                                                     final PublicKey proposerKey,
                                                     final PublicKey optionProposalMetaKey,
                                                     final PublicKey payerKey,
                                                     final PublicKey systemProgramKey,
                                                     final PublicKey eventAuthorityKey,
                                                     final PublicKey programKey,
                                                     final int bump,
                                                     final String[] optionDescriptions) {
    final var keys = createOptionProposalMetaKeys(
      proposalKey,
      proposerKey,
      optionProposalMetaKey,
      payerKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return createOptionProposalMeta(invokedGovernProgramMeta, keys, bump, optionDescriptions);
  }

  /// Creates an OptionProposalMeta.
  ///
  public static Instruction createOptionProposalMeta(final AccountMeta invokedGovernProgramMeta,
                                                     final List<AccountMeta> keys,
                                                     final int bump,
                                                     final String[] optionDescriptions) {
    final byte[] _data = new byte[9 + Borsh.lenVector(optionDescriptions)];
    int i = CREATE_OPTION_PROPOSAL_META_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) bump;
    ++i;
    Borsh.writeVector(optionDescriptions, _data, i);

    return Instruction.createInstruction(invokedGovernProgramMeta, keys, _data);
  }

  public record CreateOptionProposalMetaIxData(Discriminator discriminator, int bump, String[] optionDescriptions, byte[][] _optionDescriptions) implements Borsh {  

    public static CreateOptionProposalMetaIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static CreateOptionProposalMetaIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var bump = _data[i] & 0xFF;
      ++i;
      final var optionDescriptions = Borsh.readStringVector(_data, i);
      return new CreateOptionProposalMetaIxData(discriminator, bump, optionDescriptions, Borsh.getBytes(optionDescriptions));
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) bump;
      ++i;
      i += Borsh.writeVector(optionDescriptions, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 1 + Borsh.lenVector(optionDescriptions);
    }
  }

  private GovernProgram() {
  }
}

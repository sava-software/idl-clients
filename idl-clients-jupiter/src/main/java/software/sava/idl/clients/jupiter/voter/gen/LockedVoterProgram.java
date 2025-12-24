package software.sava.idl.clients.jupiter.voter.gen;

import java.lang.String;

import java.util.Arrays;
import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.jupiter.voter.gen.types.LockerParams;

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

public final class LockedVoterProgram {

  public static final Discriminator NEW_LOCKER_DISCRIMINATOR = toDiscriminator(177, 133, 32, 90, 229, 216, 131, 47);

  /// Creates a new Locker.
  ///
  /// @param baseKey Base.
  /// @param lockerKey Locker.
  /// @param tokenMintKey Mint of the token that can be used to join the Locker.
  /// @param governorKey Governor associated with the Locker.
  /// @param payerKey Payer of the initialization.
  /// @param systemProgramKey System program.
  public static List<AccountMeta> newLockerKeys(final PublicKey baseKey,
                                                final PublicKey lockerKey,
                                                final PublicKey tokenMintKey,
                                                final PublicKey governorKey,
                                                final PublicKey payerKey,
                                                final PublicKey systemProgramKey) {
    return List.of(
      createReadOnlySigner(baseKey),
      createWrite(lockerKey),
      createRead(tokenMintKey),
      createRead(governorKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey)
    );
  }

  /// Creates a new Locker.
  ///
  /// @param baseKey Base.
  /// @param lockerKey Locker.
  /// @param tokenMintKey Mint of the token that can be used to join the Locker.
  /// @param governorKey Governor associated with the Locker.
  /// @param payerKey Payer of the initialization.
  /// @param systemProgramKey System program.
  public static Instruction newLocker(final AccountMeta invokedLockedVoterProgramMeta,
                                      final PublicKey baseKey,
                                      final PublicKey lockerKey,
                                      final PublicKey tokenMintKey,
                                      final PublicKey governorKey,
                                      final PublicKey payerKey,
                                      final PublicKey systemProgramKey,
                                      final LockerParams params) {
    final var keys = newLockerKeys(
      baseKey,
      lockerKey,
      tokenMintKey,
      governorKey,
      payerKey,
      systemProgramKey
    );
    return newLocker(invokedLockedVoterProgramMeta, keys, params);
  }

  /// Creates a new Locker.
  ///
  public static Instruction newLocker(final AccountMeta invokedLockedVoterProgramMeta,
                                      final List<AccountMeta> keys,
                                      final LockerParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = NEW_LOCKER_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLockedVoterProgramMeta, keys, _data);
  }

  public record NewLockerIxData(Discriminator discriminator, LockerParams params) implements SerDe {  

    public static NewLockerIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 33;

    public static NewLockerIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = LockerParams.read(_data, i);
      return new NewLockerIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator NEW_ESCROW_DISCRIMINATOR = toDiscriminator(216, 182, 143, 11, 220, 38, 86, 185);

  /// Creates a new Escrow for an account.
  /// 
  /// A Vote Escrow, or Escrow for short, is an agreement between an account (known as the `authority`) and the DAO to
  /// lock up tokens for a specific period of time, in exchange for voting rights
  /// linearly proportional to the amount of votes given.
  ///
  /// @param lockerKey Locker.
  /// @param escrowKey Escrow.
  /// @param payerKey Payer of the initialization.
  /// @param systemProgramKey System program.
  public static List<AccountMeta> newEscrowKeys(final PublicKey lockerKey,
                                                final PublicKey escrowKey,
                                                final PublicKey escrowOwnerKey,
                                                final PublicKey payerKey,
                                                final PublicKey systemProgramKey) {
    return List.of(
      createWrite(lockerKey),
      createWrite(escrowKey),
      createRead(escrowOwnerKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey)
    );
  }

  /// Creates a new Escrow for an account.
  /// 
  /// A Vote Escrow, or Escrow for short, is an agreement between an account (known as the `authority`) and the DAO to
  /// lock up tokens for a specific period of time, in exchange for voting rights
  /// linearly proportional to the amount of votes given.
  ///
  /// @param lockerKey Locker.
  /// @param escrowKey Escrow.
  /// @param payerKey Payer of the initialization.
  /// @param systemProgramKey System program.
  public static Instruction newEscrow(final AccountMeta invokedLockedVoterProgramMeta,
                                      final PublicKey lockerKey,
                                      final PublicKey escrowKey,
                                      final PublicKey escrowOwnerKey,
                                      final PublicKey payerKey,
                                      final PublicKey systemProgramKey) {
    final var keys = newEscrowKeys(
      lockerKey,
      escrowKey,
      escrowOwnerKey,
      payerKey,
      systemProgramKey
    );
    return newEscrow(invokedLockedVoterProgramMeta, keys);
  }

  /// Creates a new Escrow for an account.
  /// 
  /// A Vote Escrow, or Escrow for short, is an agreement between an account (known as the `authority`) and the DAO to
  /// lock up tokens for a specific period of time, in exchange for voting rights
  /// linearly proportional to the amount of votes given.
  ///
  public static Instruction newEscrow(final AccountMeta invokedLockedVoterProgramMeta,
                                      final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLockedVoterProgramMeta, keys, NEW_ESCROW_DISCRIMINATOR);
  }

  public static final Discriminator INCREASE_LOCKED_AMOUNT_DISCRIMINATOR = toDiscriminator(5, 168, 118, 53, 72, 46, 203, 146);

  /// increase locked amount Escrow.
  ///
  /// @param lockerKey Locker.
  /// @param escrowKey Escrow.
  /// @param escrowTokensKey Token account held by the Escrow.
  /// @param payerKey Authority Self::source_tokens, Anyone can increase amount for user
  /// @param sourceTokensKey The source of deposited tokens.
  /// @param tokenProgramKey Token program.
  public static List<AccountMeta> increaseLockedAmountKeys(final PublicKey lockerKey,
                                                           final PublicKey escrowKey,
                                                           final PublicKey escrowTokensKey,
                                                           final PublicKey payerKey,
                                                           final PublicKey sourceTokensKey,
                                                           final PublicKey tokenProgramKey) {
    return List.of(
      createWrite(lockerKey),
      createWrite(escrowKey),
      createWrite(escrowTokensKey),
      createReadOnlySigner(payerKey),
      createWrite(sourceTokensKey),
      createRead(tokenProgramKey)
    );
  }

  /// increase locked amount Escrow.
  ///
  /// @param lockerKey Locker.
  /// @param escrowKey Escrow.
  /// @param escrowTokensKey Token account held by the Escrow.
  /// @param payerKey Authority Self::source_tokens, Anyone can increase amount for user
  /// @param sourceTokensKey The source of deposited tokens.
  /// @param tokenProgramKey Token program.
  public static Instruction increaseLockedAmount(final AccountMeta invokedLockedVoterProgramMeta,
                                                 final PublicKey lockerKey,
                                                 final PublicKey escrowKey,
                                                 final PublicKey escrowTokensKey,
                                                 final PublicKey payerKey,
                                                 final PublicKey sourceTokensKey,
                                                 final PublicKey tokenProgramKey,
                                                 final long amount) {
    final var keys = increaseLockedAmountKeys(
      lockerKey,
      escrowKey,
      escrowTokensKey,
      payerKey,
      sourceTokensKey,
      tokenProgramKey
    );
    return increaseLockedAmount(invokedLockedVoterProgramMeta, keys, amount);
  }

  /// increase locked amount Escrow.
  ///
  public static Instruction increaseLockedAmount(final AccountMeta invokedLockedVoterProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final long amount) {
    final byte[] _data = new byte[16];
    int i = INCREASE_LOCKED_AMOUNT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedLockedVoterProgramMeta, keys, _data);
  }

  public record IncreaseLockedAmountIxData(Discriminator discriminator, long amount) implements SerDe {  

    public static IncreaseLockedAmountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static IncreaseLockedAmountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amount = getInt64LE(_data, i);
      return new IncreaseLockedAmountIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator EXTEND_LOCK_DURATION_DISCRIMINATOR = toDiscriminator(177, 105, 196, 129, 153, 137, 136, 230);

  /// extend locked duration Escrow.
  ///
  /// @param lockerKey Locker.
  /// @param escrowKey Escrow.
  /// @param escrowOwnerKey Authority of the Escrow and
  public static List<AccountMeta> extendLockDurationKeys(final PublicKey lockerKey,
                                                         final PublicKey escrowKey,
                                                         final PublicKey escrowOwnerKey) {
    return List.of(
      createRead(lockerKey),
      createWrite(escrowKey),
      createReadOnlySigner(escrowOwnerKey)
    );
  }

  /// extend locked duration Escrow.
  ///
  /// @param lockerKey Locker.
  /// @param escrowKey Escrow.
  /// @param escrowOwnerKey Authority of the Escrow and
  public static Instruction extendLockDuration(final AccountMeta invokedLockedVoterProgramMeta,
                                               final PublicKey lockerKey,
                                               final PublicKey escrowKey,
                                               final PublicKey escrowOwnerKey,
                                               final long duration) {
    final var keys = extendLockDurationKeys(
      lockerKey,
      escrowKey,
      escrowOwnerKey
    );
    return extendLockDuration(invokedLockedVoterProgramMeta, keys, duration);
  }

  /// extend locked duration Escrow.
  ///
  public static Instruction extendLockDuration(final AccountMeta invokedLockedVoterProgramMeta,
                                               final List<AccountMeta> keys,
                                               final long duration) {
    final byte[] _data = new byte[16];
    int i = EXTEND_LOCK_DURATION_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, duration);

    return Instruction.createInstruction(invokedLockedVoterProgramMeta, keys, _data);
  }

  public record ExtendLockDurationIxData(Discriminator discriminator, long duration) implements SerDe {  

    public static ExtendLockDurationIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static ExtendLockDurationIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var duration = getInt64LE(_data, i);
      return new ExtendLockDurationIxData(discriminator, duration);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, duration);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator TOGGLE_MAX_LOCK_DISCRIMINATOR = toDiscriminator(163, 157, 161, 132, 179, 107, 127, 143);

  /// toogle max lock Escrow.
  ///
  /// @param lockerKey Locker.
  /// @param escrowKey Escrow.
  /// @param escrowOwnerKey Authority of the Escrow and
  public static List<AccountMeta> toggleMaxLockKeys(final PublicKey lockerKey,
                                                    final PublicKey escrowKey,
                                                    final PublicKey escrowOwnerKey) {
    return List.of(
      createRead(lockerKey),
      createWrite(escrowKey),
      createReadOnlySigner(escrowOwnerKey)
    );
  }

  /// toogle max lock Escrow.
  ///
  /// @param lockerKey Locker.
  /// @param escrowKey Escrow.
  /// @param escrowOwnerKey Authority of the Escrow and
  public static Instruction toggleMaxLock(final AccountMeta invokedLockedVoterProgramMeta,
                                          final PublicKey lockerKey,
                                          final PublicKey escrowKey,
                                          final PublicKey escrowOwnerKey,
                                          final boolean isMaxLock) {
    final var keys = toggleMaxLockKeys(
      lockerKey,
      escrowKey,
      escrowOwnerKey
    );
    return toggleMaxLock(invokedLockedVoterProgramMeta, keys, isMaxLock);
  }

  /// toogle max lock Escrow.
  ///
  public static Instruction toggleMaxLock(final AccountMeta invokedLockedVoterProgramMeta,
                                          final List<AccountMeta> keys,
                                          final boolean isMaxLock) {
    final byte[] _data = new byte[9];
    int i = TOGGLE_MAX_LOCK_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) (isMaxLock ? 1 : 0);

    return Instruction.createInstruction(invokedLockedVoterProgramMeta, keys, _data);
  }

  public record ToggleMaxLockIxData(Discriminator discriminator, boolean isMaxLock) implements SerDe {  

    public static ToggleMaxLockIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static ToggleMaxLockIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var isMaxLock = _data[i] == 1;
      return new ToggleMaxLockIxData(discriminator, isMaxLock);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) (isMaxLock ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_DISCRIMINATOR = toDiscriminator(183, 18, 70, 156, 148, 109, 161, 34);

  /// Exits the DAO; i.e., withdraws all staked tokens in an Escrow if the Escrow is unlocked.
  ///
  /// @param lockerKey The Locker being exited from.
  /// @param escrowKey The Escrow that is being closed.
  /// @param escrowOwnerKey Authority of the Escrow.
  /// @param escrowTokensKey Tokens locked up in the Escrow.
  /// @param destinationTokensKey Destination for the tokens to unlock.
  /// @param payerKey The payer to receive the rent refund.
  /// @param tokenProgramKey Token program.
  public static List<AccountMeta> withdrawKeys(final PublicKey lockerKey,
                                               final PublicKey escrowKey,
                                               final PublicKey escrowOwnerKey,
                                               final PublicKey escrowTokensKey,
                                               final PublicKey destinationTokensKey,
                                               final PublicKey payerKey,
                                               final PublicKey tokenProgramKey) {
    return List.of(
      createWrite(lockerKey),
      createWrite(escrowKey),
      createReadOnlySigner(escrowOwnerKey),
      createWrite(escrowTokensKey),
      createWrite(destinationTokensKey),
      createWritableSigner(payerKey),
      createRead(tokenProgramKey)
    );
  }

  /// Exits the DAO; i.e., withdraws all staked tokens in an Escrow if the Escrow is unlocked.
  ///
  /// @param lockerKey The Locker being exited from.
  /// @param escrowKey The Escrow that is being closed.
  /// @param escrowOwnerKey Authority of the Escrow.
  /// @param escrowTokensKey Tokens locked up in the Escrow.
  /// @param destinationTokensKey Destination for the tokens to unlock.
  /// @param payerKey The payer to receive the rent refund.
  /// @param tokenProgramKey Token program.
  public static Instruction withdraw(final AccountMeta invokedLockedVoterProgramMeta,
                                     final PublicKey lockerKey,
                                     final PublicKey escrowKey,
                                     final PublicKey escrowOwnerKey,
                                     final PublicKey escrowTokensKey,
                                     final PublicKey destinationTokensKey,
                                     final PublicKey payerKey,
                                     final PublicKey tokenProgramKey) {
    final var keys = withdrawKeys(
      lockerKey,
      escrowKey,
      escrowOwnerKey,
      escrowTokensKey,
      destinationTokensKey,
      payerKey,
      tokenProgramKey
    );
    return withdraw(invokedLockedVoterProgramMeta, keys);
  }

  /// Exits the DAO; i.e., withdraws all staked tokens in an Escrow if the Escrow is unlocked.
  ///
  public static Instruction withdraw(final AccountMeta invokedLockedVoterProgramMeta,
                                     final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLockedVoterProgramMeta, keys, WITHDRAW_DISCRIMINATOR);
  }

  public static final Discriminator ACTIVATE_PROPOSAL_DISCRIMINATOR = toDiscriminator(90, 186, 203, 234, 70, 185, 191, 21);

  /// Activates a proposal in token launch phase
  ///
  /// @param lockerKey The Locker.
  /// @param governorKey The Governor.
  /// @param proposalKey The Proposal.
  /// @param governProgramKey The govern program.
  /// @param smartWalletKey The smart wallet on the Governor.
  public static List<AccountMeta> activateProposalKeys(final PublicKey lockerKey,
                                                       final PublicKey governorKey,
                                                       final PublicKey proposalKey,
                                                       final PublicKey governProgramKey,
                                                       final PublicKey smartWalletKey) {
    return List.of(
      createRead(lockerKey),
      createRead(governorKey),
      createWrite(proposalKey),
      createRead(governProgramKey),
      createReadOnlySigner(smartWalletKey)
    );
  }

  /// Activates a proposal in token launch phase
  ///
  /// @param lockerKey The Locker.
  /// @param governorKey The Governor.
  /// @param proposalKey The Proposal.
  /// @param governProgramKey The govern program.
  /// @param smartWalletKey The smart wallet on the Governor.
  public static Instruction activateProposal(final AccountMeta invokedLockedVoterProgramMeta,
                                             final PublicKey lockerKey,
                                             final PublicKey governorKey,
                                             final PublicKey proposalKey,
                                             final PublicKey governProgramKey,
                                             final PublicKey smartWalletKey) {
    final var keys = activateProposalKeys(
      lockerKey,
      governorKey,
      proposalKey,
      governProgramKey,
      smartWalletKey
    );
    return activateProposal(invokedLockedVoterProgramMeta, keys);
  }

  /// Activates a proposal in token launch phase
  ///
  public static Instruction activateProposal(final AccountMeta invokedLockedVoterProgramMeta,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLockedVoterProgramMeta, keys, ACTIVATE_PROPOSAL_DISCRIMINATOR);
  }

  public static final Discriminator CAST_VOTE_DISCRIMINATOR = toDiscriminator(20, 212, 15, 189, 69, 180, 69, 151);

  /// Casts a vote.
  ///
  /// @param lockerKey The Locker.
  /// @param escrowKey The Escrow that is voting.
  /// @param voteDelegateKey Vote delegate of the Escrow.
  /// @param proposalKey The Proposal being voted on.
  /// @param voteKey The Vote.
  /// @param governorKey The Governor.
  /// @param governProgramKey The govern program.
  public static List<AccountMeta> castVoteKeys(final PublicKey lockerKey,
                                               final PublicKey escrowKey,
                                               final PublicKey voteDelegateKey,
                                               final PublicKey proposalKey,
                                               final PublicKey voteKey,
                                               final PublicKey governorKey,
                                               final PublicKey governProgramKey) {
    return List.of(
      createRead(lockerKey),
      createRead(escrowKey),
      createReadOnlySigner(voteDelegateKey),
      createWrite(proposalKey),
      createWrite(voteKey),
      createRead(governorKey),
      createRead(governProgramKey)
    );
  }

  /// Casts a vote.
  ///
  /// @param lockerKey The Locker.
  /// @param escrowKey The Escrow that is voting.
  /// @param voteDelegateKey Vote delegate of the Escrow.
  /// @param proposalKey The Proposal being voted on.
  /// @param voteKey The Vote.
  /// @param governorKey The Governor.
  /// @param governProgramKey The govern program.
  public static Instruction castVote(final AccountMeta invokedLockedVoterProgramMeta,
                                     final PublicKey lockerKey,
                                     final PublicKey escrowKey,
                                     final PublicKey voteDelegateKey,
                                     final PublicKey proposalKey,
                                     final PublicKey voteKey,
                                     final PublicKey governorKey,
                                     final PublicKey governProgramKey,
                                     final int side) {
    final var keys = castVoteKeys(
      lockerKey,
      escrowKey,
      voteDelegateKey,
      proposalKey,
      voteKey,
      governorKey,
      governProgramKey
    );
    return castVote(invokedLockedVoterProgramMeta, keys, side);
  }

  /// Casts a vote.
  ///
  public static Instruction castVote(final AccountMeta invokedLockedVoterProgramMeta,
                                     final List<AccountMeta> keys,
                                     final int side) {
    final byte[] _data = new byte[9];
    int i = CAST_VOTE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) side;

    return Instruction.createInstruction(invokedLockedVoterProgramMeta, keys, _data);
  }

  public record CastVoteIxData(Discriminator discriminator, int side) implements SerDe {  

    public static CastVoteIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static CastVoteIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var side = _data[i] & 0xFF;
      return new CastVoteIxData(discriminator, side);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) side;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_VOTE_DELEGATE_DISCRIMINATOR = toDiscriminator(46, 236, 241, 243, 251, 108, 156, 12);

  /// Delegate escrow vote.
  ///
  /// @param escrowKey The Escrow.
  /// @param escrowOwnerKey The owner of the Escrow.
  public static List<AccountMeta> setVoteDelegateKeys(final PublicKey escrowKey,
                                                      final PublicKey escrowOwnerKey) {
    return List.of(
      createWrite(escrowKey),
      createReadOnlySigner(escrowOwnerKey)
    );
  }

  /// Delegate escrow vote.
  ///
  /// @param escrowKey The Escrow.
  /// @param escrowOwnerKey The owner of the Escrow.
  public static Instruction setVoteDelegate(final AccountMeta invokedLockedVoterProgramMeta,
                                            final PublicKey escrowKey,
                                            final PublicKey escrowOwnerKey,
                                            final PublicKey newDelegate) {
    final var keys = setVoteDelegateKeys(
      escrowKey,
      escrowOwnerKey
    );
    return setVoteDelegate(invokedLockedVoterProgramMeta, keys, newDelegate);
  }

  /// Delegate escrow vote.
  ///
  public static Instruction setVoteDelegate(final AccountMeta invokedLockedVoterProgramMeta,
                                            final List<AccountMeta> keys,
                                            final PublicKey newDelegate) {
    final byte[] _data = new byte[40];
    int i = SET_VOTE_DELEGATE_DISCRIMINATOR.write(_data, 0);
    newDelegate.write(_data, i);

    return Instruction.createInstruction(invokedLockedVoterProgramMeta, keys, _data);
  }

  public record SetVoteDelegateIxData(Discriminator discriminator, PublicKey newDelegate) implements SerDe {  

    public static SetVoteDelegateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static SetVoteDelegateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var newDelegate = readPubKey(_data, i);
      return new SetVoteDelegateIxData(discriminator, newDelegate);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      newDelegate.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_LOCKER_PARAMS_DISCRIMINATOR = toDiscriminator(106, 39, 132, 84, 254, 77, 161, 169);

  /// Set locker params.
  ///
  /// @param lockerKey The Locker.
  /// @param governorKey The Governor.
  /// @param smartWalletKey The smart wallet on the Governor.
  public static List<AccountMeta> setLockerParamsKeys(final PublicKey lockerKey,
                                                      final PublicKey governorKey,
                                                      final PublicKey smartWalletKey) {
    return List.of(
      createWrite(lockerKey),
      createRead(governorKey),
      createReadOnlySigner(smartWalletKey)
    );
  }

  /// Set locker params.
  ///
  /// @param lockerKey The Locker.
  /// @param governorKey The Governor.
  /// @param smartWalletKey The smart wallet on the Governor.
  public static Instruction setLockerParams(final AccountMeta invokedLockedVoterProgramMeta,
                                            final PublicKey lockerKey,
                                            final PublicKey governorKey,
                                            final PublicKey smartWalletKey,
                                            final LockerParams params) {
    final var keys = setLockerParamsKeys(
      lockerKey,
      governorKey,
      smartWalletKey
    );
    return setLockerParams(invokedLockedVoterProgramMeta, keys, params);
  }

  /// Set locker params.
  ///
  public static Instruction setLockerParams(final AccountMeta invokedLockedVoterProgramMeta,
                                            final List<AccountMeta> keys,
                                            final LockerParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = SET_LOCKER_PARAMS_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLockedVoterProgramMeta, keys, _data);
  }

  public record SetLockerParamsIxData(Discriminator discriminator, LockerParams params) implements SerDe {  

    public static SetLockerParamsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 33;

    public static SetLockerParamsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = LockerParams.read(_data, i);
      return new SetLockerParamsIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator OPEN_PARTIAL_UNSTAKING_DISCRIMINATOR = toDiscriminator(201, 137, 207, 175, 79, 95, 220, 27);

  /// Open partial unstaking
  ///
  /// @param lockerKey Locker.
  /// @param escrowKey Escrow.
  /// @param partialUnstakeKey Escrow.
  /// @param systemProgramKey System program.
  public static List<AccountMeta> openPartialUnstakingKeys(final PublicKey lockerKey,
                                                           final PublicKey escrowKey,
                                                           final PublicKey partialUnstakeKey,
                                                           final PublicKey ownerKey,
                                                           final PublicKey systemProgramKey) {
    return List.of(
      createWrite(lockerKey),
      createWrite(escrowKey),
      createWritableSigner(partialUnstakeKey),
      createWritableSigner(ownerKey),
      createRead(systemProgramKey)
    );
  }

  /// Open partial unstaking
  ///
  /// @param lockerKey Locker.
  /// @param escrowKey Escrow.
  /// @param partialUnstakeKey Escrow.
  /// @param systemProgramKey System program.
  public static Instruction openPartialUnstaking(final AccountMeta invokedLockedVoterProgramMeta,
                                                 final PublicKey lockerKey,
                                                 final PublicKey escrowKey,
                                                 final PublicKey partialUnstakeKey,
                                                 final PublicKey ownerKey,
                                                 final PublicKey systemProgramKey,
                                                 final long amount,
                                                 final String memo) {
    final var keys = openPartialUnstakingKeys(
      lockerKey,
      escrowKey,
      partialUnstakeKey,
      ownerKey,
      systemProgramKey
    );
    return openPartialUnstaking(invokedLockedVoterProgramMeta, keys, amount, memo);
  }

  /// Open partial unstaking
  ///
  public static Instruction openPartialUnstaking(final AccountMeta invokedLockedVoterProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final long amount,
                                                 final String memo) {
    final byte[] _memo = memo.getBytes(UTF_8);
    final byte[] _data = new byte[20 + _memo.length];
    int i = OPEN_PARTIAL_UNSTAKING_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);
    i += 8;
    SerDeUtil.writeVector(4, _memo, _data, i);

    return Instruction.createInstruction(invokedLockedVoterProgramMeta, keys, _data);
  }

  public record OpenPartialUnstakingIxData(Discriminator discriminator, long amount, String memo, byte[] _memo) implements SerDe {  

    public static OpenPartialUnstakingIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static OpenPartialUnstakingIxData createRecord(final Discriminator discriminator, final long amount, final String memo) {
      return new OpenPartialUnstakingIxData(discriminator, amount, memo, memo == null ? null : memo.getBytes(UTF_8));
    }

    public static OpenPartialUnstakingIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amount = getInt64LE(_data, i);
      i += 8;
      final int _memoLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _memo = Arrays.copyOfRange(_data, i, i + _memoLength);
      final var memo = new String(_memo, UTF_8);
      return new OpenPartialUnstakingIxData(discriminator, amount, memo, memo == null ? null : memo.getBytes(UTF_8));
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      i += SerDeUtil.writeVector(4, _memo, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 8 + _memo.length;
    }
  }

  public static final Discriminator MERGE_PARTIAL_UNSTAKING_DISCRIMINATOR = toDiscriminator(190, 154, 163, 153, 168, 115, 40, 173);

  /// Merge partial unstaking
  ///
  /// @param lockerKey Locker.
  /// @param escrowKey Escrow.
  /// @param partialUnstakeKey The PartialUnstaking that is being merged.
  public static List<AccountMeta> mergePartialUnstakingKeys(final PublicKey lockerKey,
                                                            final PublicKey escrowKey,
                                                            final PublicKey partialUnstakeKey,
                                                            final PublicKey ownerKey) {
    return List.of(
      createWrite(lockerKey),
      createWrite(escrowKey),
      createWrite(partialUnstakeKey),
      createWritableSigner(ownerKey)
    );
  }

  /// Merge partial unstaking
  ///
  /// @param lockerKey Locker.
  /// @param escrowKey Escrow.
  /// @param partialUnstakeKey The PartialUnstaking that is being merged.
  public static Instruction mergePartialUnstaking(final AccountMeta invokedLockedVoterProgramMeta,
                                                  final PublicKey lockerKey,
                                                  final PublicKey escrowKey,
                                                  final PublicKey partialUnstakeKey,
                                                  final PublicKey ownerKey) {
    final var keys = mergePartialUnstakingKeys(
      lockerKey,
      escrowKey,
      partialUnstakeKey,
      ownerKey
    );
    return mergePartialUnstaking(invokedLockedVoterProgramMeta, keys);
  }

  /// Merge partial unstaking
  ///
  public static Instruction mergePartialUnstaking(final AccountMeta invokedLockedVoterProgramMeta,
                                                  final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLockedVoterProgramMeta, keys, MERGE_PARTIAL_UNSTAKING_DISCRIMINATOR);
  }

  public static final Discriminator WITHDRAW_PARTIAL_UNSTAKING_DISCRIMINATOR = toDiscriminator(201, 202, 137, 124, 2, 3, 245, 87);

  /// Withdraw partial unstaking
  ///
  /// @param lockerKey The Locker being exited from.
  /// @param escrowKey The Escrow that is being closed.
  /// @param partialUnstakeKey The PartialUnstaking that is being withdraw.
  /// @param ownerKey Authority of the Escrow.
  /// @param escrowTokensKey Tokens locked up in the Escrow.
  /// @param destinationTokensKey Destination for the tokens to unlock.
  /// @param payerKey The payer to receive the rent refund.
  /// @param tokenProgramKey Token program.
  public static List<AccountMeta> withdrawPartialUnstakingKeys(final PublicKey lockerKey,
                                                               final PublicKey escrowKey,
                                                               final PublicKey partialUnstakeKey,
                                                               final PublicKey ownerKey,
                                                               final PublicKey escrowTokensKey,
                                                               final PublicKey destinationTokensKey,
                                                               final PublicKey payerKey,
                                                               final PublicKey tokenProgramKey) {
    return List.of(
      createWrite(lockerKey),
      createWrite(escrowKey),
      createWrite(partialUnstakeKey),
      createReadOnlySigner(ownerKey),
      createWrite(escrowTokensKey),
      createWrite(destinationTokensKey),
      createWritableSigner(payerKey),
      createRead(tokenProgramKey)
    );
  }

  /// Withdraw partial unstaking
  ///
  /// @param lockerKey The Locker being exited from.
  /// @param escrowKey The Escrow that is being closed.
  /// @param partialUnstakeKey The PartialUnstaking that is being withdraw.
  /// @param ownerKey Authority of the Escrow.
  /// @param escrowTokensKey Tokens locked up in the Escrow.
  /// @param destinationTokensKey Destination for the tokens to unlock.
  /// @param payerKey The payer to receive the rent refund.
  /// @param tokenProgramKey Token program.
  public static Instruction withdrawPartialUnstaking(final AccountMeta invokedLockedVoterProgramMeta,
                                                     final PublicKey lockerKey,
                                                     final PublicKey escrowKey,
                                                     final PublicKey partialUnstakeKey,
                                                     final PublicKey ownerKey,
                                                     final PublicKey escrowTokensKey,
                                                     final PublicKey destinationTokensKey,
                                                     final PublicKey payerKey,
                                                     final PublicKey tokenProgramKey) {
    final var keys = withdrawPartialUnstakingKeys(
      lockerKey,
      escrowKey,
      partialUnstakeKey,
      ownerKey,
      escrowTokensKey,
      destinationTokensKey,
      payerKey,
      tokenProgramKey
    );
    return withdrawPartialUnstaking(invokedLockedVoterProgramMeta, keys);
  }

  /// Withdraw partial unstaking
  ///
  public static Instruction withdrawPartialUnstaking(final AccountMeta invokedLockedVoterProgramMeta,
                                                     final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLockedVoterProgramMeta, keys, WITHDRAW_PARTIAL_UNSTAKING_DISCRIMINATOR);
  }

  private LockedVoterProgram() {
  }
}

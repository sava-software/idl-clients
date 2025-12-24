package software.sava.idl.clients.squads.v4.gen;

import software.sava.idl.clients.core.gen.ProgramError;

public sealed interface SquadsMultisigProgramError extends ProgramError permits
    SquadsMultisigProgramError.DuplicateMember,
    SquadsMultisigProgramError.EmptyMembers,
    SquadsMultisigProgramError.TooManyMembers,
    SquadsMultisigProgramError.InvalidThreshold,
    SquadsMultisigProgramError.Unauthorized,
    SquadsMultisigProgramError.NotAMember,
    SquadsMultisigProgramError.InvalidTransactionMessage,
    SquadsMultisigProgramError.StaleProposal,
    SquadsMultisigProgramError.InvalidProposalStatus,
    SquadsMultisigProgramError.InvalidTransactionIndex,
    SquadsMultisigProgramError.AlreadyApproved,
    SquadsMultisigProgramError.AlreadyRejected,
    SquadsMultisigProgramError.AlreadyCancelled,
    SquadsMultisigProgramError.InvalidNumberOfAccounts,
    SquadsMultisigProgramError.InvalidAccount,
    SquadsMultisigProgramError.RemoveLastMember,
    SquadsMultisigProgramError.NoVoters,
    SquadsMultisigProgramError.NoProposers,
    SquadsMultisigProgramError.NoExecutors,
    SquadsMultisigProgramError.InvalidStaleTransactionIndex,
    SquadsMultisigProgramError.NotSupportedForControlled,
    SquadsMultisigProgramError.TimeLockNotReleased,
    SquadsMultisigProgramError.NoActions,
    SquadsMultisigProgramError.MissingAccount,
    SquadsMultisigProgramError.InvalidMint,
    SquadsMultisigProgramError.InvalidDestination,
    SquadsMultisigProgramError.SpendingLimitExceeded,
    SquadsMultisigProgramError.DecimalsMismatch,
    SquadsMultisigProgramError.UnknownPermission,
    SquadsMultisigProgramError.ProtectedAccount,
    SquadsMultisigProgramError.TimeLockExceedsMaxAllowed,
    SquadsMultisigProgramError.IllegalAccountOwner,
    SquadsMultisigProgramError.RentReclamationDisabled,
    SquadsMultisigProgramError.InvalidRentCollector,
    SquadsMultisigProgramError.ProposalForAnotherMultisig,
    SquadsMultisigProgramError.TransactionForAnotherMultisig,
    SquadsMultisigProgramError.TransactionNotMatchingProposal,
    SquadsMultisigProgramError.TransactionNotLastInBatch,
    SquadsMultisigProgramError.BatchNotEmpty,
    SquadsMultisigProgramError.SpendingLimitInvalidAmount {

  static SquadsMultisigProgramError getInstance(final int errorCode) {
    return switch (errorCode) {
      case 6000 -> DuplicateMember.INSTANCE;
      case 6001 -> EmptyMembers.INSTANCE;
      case 6002 -> TooManyMembers.INSTANCE;
      case 6003 -> InvalidThreshold.INSTANCE;
      case 6004 -> Unauthorized.INSTANCE;
      case 6005 -> NotAMember.INSTANCE;
      case 6006 -> InvalidTransactionMessage.INSTANCE;
      case 6007 -> StaleProposal.INSTANCE;
      case 6008 -> InvalidProposalStatus.INSTANCE;
      case 6009 -> InvalidTransactionIndex.INSTANCE;
      case 6010 -> AlreadyApproved.INSTANCE;
      case 6011 -> AlreadyRejected.INSTANCE;
      case 6012 -> AlreadyCancelled.INSTANCE;
      case 6013 -> InvalidNumberOfAccounts.INSTANCE;
      case 6014 -> InvalidAccount.INSTANCE;
      case 6015 -> RemoveLastMember.INSTANCE;
      case 6016 -> NoVoters.INSTANCE;
      case 6017 -> NoProposers.INSTANCE;
      case 6018 -> NoExecutors.INSTANCE;
      case 6019 -> InvalidStaleTransactionIndex.INSTANCE;
      case 6020 -> NotSupportedForControlled.INSTANCE;
      case 6021 -> TimeLockNotReleased.INSTANCE;
      case 6022 -> NoActions.INSTANCE;
      case 6023 -> MissingAccount.INSTANCE;
      case 6024 -> InvalidMint.INSTANCE;
      case 6025 -> InvalidDestination.INSTANCE;
      case 6026 -> SpendingLimitExceeded.INSTANCE;
      case 6027 -> DecimalsMismatch.INSTANCE;
      case 6028 -> UnknownPermission.INSTANCE;
      case 6029 -> ProtectedAccount.INSTANCE;
      case 6030 -> TimeLockExceedsMaxAllowed.INSTANCE;
      case 6031 -> IllegalAccountOwner.INSTANCE;
      case 6032 -> RentReclamationDisabled.INSTANCE;
      case 6033 -> InvalidRentCollector.INSTANCE;
      case 6034 -> ProposalForAnotherMultisig.INSTANCE;
      case 6035 -> TransactionForAnotherMultisig.INSTANCE;
      case 6036 -> TransactionNotMatchingProposal.INSTANCE;
      case 6037 -> TransactionNotLastInBatch.INSTANCE;
      case 6038 -> BatchNotEmpty.INSTANCE;
      case 6039 -> SpendingLimitInvalidAmount.INSTANCE;
      default -> null;
    };
  }

  record DuplicateMember(int code, String msg) implements SquadsMultisigProgramError {

    public static final DuplicateMember INSTANCE = new DuplicateMember(
        6000, "Found multiple members with the same pubkey"
    );
  }

  record EmptyMembers(int code, String msg) implements SquadsMultisigProgramError {

    public static final EmptyMembers INSTANCE = new EmptyMembers(
        6001, "Members array is empty"
    );
  }

  record TooManyMembers(int code, String msg) implements SquadsMultisigProgramError {

    public static final TooManyMembers INSTANCE = new TooManyMembers(
        6002, "Too many members, can be up to 65535"
    );
  }

  record InvalidThreshold(int code, String msg) implements SquadsMultisigProgramError {

    public static final InvalidThreshold INSTANCE = new InvalidThreshold(
        6003, "Invalid threshold, must be between 1 and number of members with Vote permission"
    );
  }

  record Unauthorized(int code, String msg) implements SquadsMultisigProgramError {

    public static final Unauthorized INSTANCE = new Unauthorized(
        6004, "Attempted to perform an unauthorized action"
    );
  }

  record NotAMember(int code, String msg) implements SquadsMultisigProgramError {

    public static final NotAMember INSTANCE = new NotAMember(
        6005, "Provided pubkey is not a member of multisig"
    );
  }

  record InvalidTransactionMessage(int code, String msg) implements SquadsMultisigProgramError {

    public static final InvalidTransactionMessage INSTANCE = new InvalidTransactionMessage(
        6006, "TransactionMessage is malformed."
    );
  }

  record StaleProposal(int code, String msg) implements SquadsMultisigProgramError {

    public static final StaleProposal INSTANCE = new StaleProposal(
        6007, "Proposal is stale"
    );
  }

  record InvalidProposalStatus(int code, String msg) implements SquadsMultisigProgramError {

    public static final InvalidProposalStatus INSTANCE = new InvalidProposalStatus(
        6008, "Invalid proposal status"
    );
  }

  record InvalidTransactionIndex(int code, String msg) implements SquadsMultisigProgramError {

    public static final InvalidTransactionIndex INSTANCE = new InvalidTransactionIndex(
        6009, "Invalid transaction index"
    );
  }

  record AlreadyApproved(int code, String msg) implements SquadsMultisigProgramError {

    public static final AlreadyApproved INSTANCE = new AlreadyApproved(
        6010, "Member already approved the transaction"
    );
  }

  record AlreadyRejected(int code, String msg) implements SquadsMultisigProgramError {

    public static final AlreadyRejected INSTANCE = new AlreadyRejected(
        6011, "Member already rejected the transaction"
    );
  }

  record AlreadyCancelled(int code, String msg) implements SquadsMultisigProgramError {

    public static final AlreadyCancelled INSTANCE = new AlreadyCancelled(
        6012, "Member already cancelled the transaction"
    );
  }

  record InvalidNumberOfAccounts(int code, String msg) implements SquadsMultisigProgramError {

    public static final InvalidNumberOfAccounts INSTANCE = new InvalidNumberOfAccounts(
        6013, "Wrong number of accounts provided"
    );
  }

  record InvalidAccount(int code, String msg) implements SquadsMultisigProgramError {

    public static final InvalidAccount INSTANCE = new InvalidAccount(
        6014, "Invalid account provided"
    );
  }

  record RemoveLastMember(int code, String msg) implements SquadsMultisigProgramError {

    public static final RemoveLastMember INSTANCE = new RemoveLastMember(
        6015, "Cannot remove last member"
    );
  }

  record NoVoters(int code, String msg) implements SquadsMultisigProgramError {

    public static final NoVoters INSTANCE = new NoVoters(
        6016, "Members don't include any voters"
    );
  }

  record NoProposers(int code, String msg) implements SquadsMultisigProgramError {

    public static final NoProposers INSTANCE = new NoProposers(
        6017, "Members don't include any proposers"
    );
  }

  record NoExecutors(int code, String msg) implements SquadsMultisigProgramError {

    public static final NoExecutors INSTANCE = new NoExecutors(
        6018, "Members don't include any executors"
    );
  }

  record InvalidStaleTransactionIndex(int code, String msg) implements SquadsMultisigProgramError {

    public static final InvalidStaleTransactionIndex INSTANCE = new InvalidStaleTransactionIndex(
        6019, "`stale_transaction_index` must be <= `transaction_index`"
    );
  }

  record NotSupportedForControlled(int code, String msg) implements SquadsMultisigProgramError {

    public static final NotSupportedForControlled INSTANCE = new NotSupportedForControlled(
        6020, "Instruction not supported for controlled multisig"
    );
  }

  record TimeLockNotReleased(int code, String msg) implements SquadsMultisigProgramError {

    public static final TimeLockNotReleased INSTANCE = new TimeLockNotReleased(
        6021, "Proposal time lock has not been released"
    );
  }

  record NoActions(int code, String msg) implements SquadsMultisigProgramError {

    public static final NoActions INSTANCE = new NoActions(
        6022, "Config transaction must have at least one action"
    );
  }

  record MissingAccount(int code, String msg) implements SquadsMultisigProgramError {

    public static final MissingAccount INSTANCE = new MissingAccount(
        6023, "Missing account"
    );
  }

  record InvalidMint(int code, String msg) implements SquadsMultisigProgramError {

    public static final InvalidMint INSTANCE = new InvalidMint(
        6024, "Invalid mint"
    );
  }

  record InvalidDestination(int code, String msg) implements SquadsMultisigProgramError {

    public static final InvalidDestination INSTANCE = new InvalidDestination(
        6025, "Invalid destination"
    );
  }

  record SpendingLimitExceeded(int code, String msg) implements SquadsMultisigProgramError {

    public static final SpendingLimitExceeded INSTANCE = new SpendingLimitExceeded(
        6026, "Spending limit exceeded"
    );
  }

  record DecimalsMismatch(int code, String msg) implements SquadsMultisigProgramError {

    public static final DecimalsMismatch INSTANCE = new DecimalsMismatch(
        6027, "Decimals don't match the mint"
    );
  }

  record UnknownPermission(int code, String msg) implements SquadsMultisigProgramError {

    public static final UnknownPermission INSTANCE = new UnknownPermission(
        6028, "Member has unknown permission"
    );
  }

  record ProtectedAccount(int code, String msg) implements SquadsMultisigProgramError {

    public static final ProtectedAccount INSTANCE = new ProtectedAccount(
        6029, "Account is protected, it cannot be passed into a CPI as writable"
    );
  }

  record TimeLockExceedsMaxAllowed(int code, String msg) implements SquadsMultisigProgramError {

    public static final TimeLockExceedsMaxAllowed INSTANCE = new TimeLockExceedsMaxAllowed(
        6030, "Time lock exceeds the maximum allowed (90 days)"
    );
  }

  record IllegalAccountOwner(int code, String msg) implements SquadsMultisigProgramError {

    public static final IllegalAccountOwner INSTANCE = new IllegalAccountOwner(
        6031, "Account is not owned by Multisig program"
    );
  }

  record RentReclamationDisabled(int code, String msg) implements SquadsMultisigProgramError {

    public static final RentReclamationDisabled INSTANCE = new RentReclamationDisabled(
        6032, "Rent reclamation is disabled for this multisig"
    );
  }

  record InvalidRentCollector(int code, String msg) implements SquadsMultisigProgramError {

    public static final InvalidRentCollector INSTANCE = new InvalidRentCollector(
        6033, "Invalid rent collector address"
    );
  }

  record ProposalForAnotherMultisig(int code, String msg) implements SquadsMultisigProgramError {

    public static final ProposalForAnotherMultisig INSTANCE = new ProposalForAnotherMultisig(
        6034, "Proposal is for another multisig"
    );
  }

  record TransactionForAnotherMultisig(int code, String msg) implements SquadsMultisigProgramError {

    public static final TransactionForAnotherMultisig INSTANCE = new TransactionForAnotherMultisig(
        6035, "Transaction is for another multisig"
    );
  }

  record TransactionNotMatchingProposal(int code, String msg) implements SquadsMultisigProgramError {

    public static final TransactionNotMatchingProposal INSTANCE = new TransactionNotMatchingProposal(
        6036, "Transaction doesn't match proposal"
    );
  }

  record TransactionNotLastInBatch(int code, String msg) implements SquadsMultisigProgramError {

    public static final TransactionNotLastInBatch INSTANCE = new TransactionNotLastInBatch(
        6037, "Transaction is not last in batch"
    );
  }

  record BatchNotEmpty(int code, String msg) implements SquadsMultisigProgramError {

    public static final BatchNotEmpty INSTANCE = new BatchNotEmpty(
        6038, "Batch is not empty"
    );
  }

  record SpendingLimitInvalidAmount(int code, String msg) implements SquadsMultisigProgramError {

    public static final SpendingLimitInvalidAmount INSTANCE = new SpendingLimitInvalidAmount(
        6039, "Invalid SpendingLimit amount"
    );
  }
}

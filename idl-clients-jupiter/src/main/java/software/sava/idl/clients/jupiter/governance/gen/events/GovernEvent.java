package software.sava.idl.clients.jupiter.governance.gen.events;

import software.sava.idl.clients.core.gen.SerDe;

public sealed interface GovernEvent extends SerDe permits
    ProposalActivateEvent,
    ProposalCancelEvent,
    ClaimRewardEvent,
    GovernorCreateEvent,
    OptionProposalMetaCreateEvent,
    ProposalMetaCreateEvent,
    ProposalCreateEvent,
    ProposalQueueEvent,
    GovernorSetParamsEvent,
    GovernorSetVoterEvent,
    VoteSetEvent,
    GovernorSetVotingReward {

  static GovernEvent read(final byte[] _data, final int _offset) {
    if (ProposalActivateEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ProposalActivateEvent.read(_data, _offset);
    } else if (ProposalCancelEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ProposalCancelEvent.read(_data, _offset);
    } else if (ClaimRewardEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ClaimRewardEvent.read(_data, _offset);
    } else if (GovernorCreateEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return GovernorCreateEvent.read(_data, _offset);
    } else if (OptionProposalMetaCreateEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return OptionProposalMetaCreateEvent.read(_data, _offset);
    } else if (ProposalMetaCreateEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ProposalMetaCreateEvent.read(_data, _offset);
    } else if (ProposalCreateEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ProposalCreateEvent.read(_data, _offset);
    } else if (ProposalQueueEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ProposalQueueEvent.read(_data, _offset);
    } else if (GovernorSetParamsEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return GovernorSetParamsEvent.read(_data, _offset);
    } else if (GovernorSetVoterEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return GovernorSetVoterEvent.read(_data, _offset);
    } else if (VoteSetEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return VoteSetEvent.read(_data, _offset);
    } else if (GovernorSetVotingReward.DISCRIMINATOR.equals(_data, _offset)) {
      return GovernorSetVotingReward.read(_data, _offset);
    } else {
      return null;
    }
  }

  static GovernEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static GovernEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static GovernEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}
package software.sava.idl.clients.jupiter.governance.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// The state of a proposal.
/// 
/// The `expired` state from Compound is missing here, because the
/// Smart Wallet handles execution.
public enum ProposalState implements RustEnum {

  Draft,
  Active,
  Canceled,
  Defeated,
  Succeeded,
  Queued;

  public static ProposalState read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ProposalState.values(), _data, _offset);
  }
}
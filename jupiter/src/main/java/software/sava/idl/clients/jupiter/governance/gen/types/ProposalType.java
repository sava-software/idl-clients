package software.sava.idl.clients.jupiter.governance.gen.types;

import software.sava.core.borsh.Borsh;

/// Proposal type
public enum ProposalType implements Borsh.Enum {

  YesNo,
  Option;

  public static ProposalType read(final byte[] _data, final int _offset) {
    return Borsh.read(ProposalType.values(), _data, _offset);
  }
}
package software.sava.idl.clients.jupiter.governance.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Proposal type
public enum ProposalType implements RustEnum {

  YesNo,
  Option;

  public static ProposalType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ProposalType.values(), _data, _offset);
  }
}
package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum TokenDelegateRole implements RustEnum {

  Sale,
  Transfer,
  Utility,
  Staking,
  Standard,
  LockedTransfer,
  Migration;

  public static TokenDelegateRole read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, TokenDelegateRole.values(), _data, _offset);
  }
}
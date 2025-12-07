package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

public enum TokenDelegateRole implements Borsh.Enum {

  Sale,
  Transfer,
  Utility,
  Staking,
  Standard,
  LockedTransfer,
  Migration;

  public static TokenDelegateRole read(final byte[] _data, final int _offset) {
    return Borsh.read(TokenDelegateRole.values(), _data, _offset);
  }
}
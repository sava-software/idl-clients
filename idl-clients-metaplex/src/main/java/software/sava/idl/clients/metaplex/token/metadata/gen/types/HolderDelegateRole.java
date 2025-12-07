package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

public enum HolderDelegateRole implements Borsh.Enum {

  PrintDelegate;

  public static HolderDelegateRole read(final byte[] _data, final int _offset) {
    return Borsh.read(HolderDelegateRole.values(), _data, _offset);
  }
}
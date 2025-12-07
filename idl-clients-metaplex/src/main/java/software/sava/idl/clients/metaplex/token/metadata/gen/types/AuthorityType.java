package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

public enum AuthorityType implements Borsh.Enum {

  None,
  Metadata,
  Holder,
  MetadataDelegate,
  TokenDelegate;

  public static AuthorityType read(final byte[] _data, final int _offset) {
    return Borsh.read(AuthorityType.values(), _data, _offset);
  }
}
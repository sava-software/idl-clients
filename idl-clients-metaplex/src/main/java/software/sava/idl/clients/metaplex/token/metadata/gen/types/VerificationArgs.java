package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

public enum VerificationArgs implements Borsh.Enum {

  CreatorV1,
  CollectionV1;

  public static VerificationArgs read(final byte[] _data, final int _offset) {
    return Borsh.read(VerificationArgs.values(), _data, _offset);
  }
}
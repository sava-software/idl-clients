package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

public enum MigrationType implements Borsh.Enum {

  CollectionV1,
  ProgrammableV1;

  public static MigrationType read(final byte[] _data, final int _offset) {
    return Borsh.read(MigrationType.values(), _data, _offset);
  }
}
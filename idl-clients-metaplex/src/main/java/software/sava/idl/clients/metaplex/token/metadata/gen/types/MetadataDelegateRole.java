package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

public enum MetadataDelegateRole implements Borsh.Enum {

  AuthorityItem,
  Collection,
  Use,
  Data,
  ProgrammableConfig,
  DataItem,
  CollectionItem,
  ProgrammableConfigItem;

  public static MetadataDelegateRole read(final byte[] _data, final int _offset) {
    return Borsh.read(MetadataDelegateRole.values(), _data, _offset);
  }
}
package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum MetadataDelegateRole implements RustEnum {

  AuthorityItem,
  Collection,
  Use,
  Data,
  ProgrammableConfig,
  DataItem,
  CollectionItem,
  ProgrammableConfigItem;

  public static MetadataDelegateRole read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, MetadataDelegateRole.values(), _data, _offset);
  }
}
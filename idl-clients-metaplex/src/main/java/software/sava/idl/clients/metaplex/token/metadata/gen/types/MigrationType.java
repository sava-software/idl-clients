package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum MigrationType implements RustEnum {

  CollectionV1,
  ProgrammableV1;

  public static MigrationType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, MigrationType.values(), _data, _offset);
  }
}
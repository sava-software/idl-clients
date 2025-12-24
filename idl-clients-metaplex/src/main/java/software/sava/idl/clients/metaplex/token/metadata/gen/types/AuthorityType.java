package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum AuthorityType implements RustEnum {

  None,
  Metadata,
  Holder,
  MetadataDelegate,
  TokenDelegate;

  public static AuthorityType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, AuthorityType.values(), _data, _offset);
  }
}
package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum VerificationArgs implements RustEnum {

  CreatorV1,
  CollectionV1;

  public static VerificationArgs read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, VerificationArgs.values(), _data, _offset);
  }
}
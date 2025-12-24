package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum HolderDelegateRole implements RustEnum {

  PrintDelegate;

  public static HolderDelegateRole read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, HolderDelegateRole.values(), _data, _offset);
  }
}
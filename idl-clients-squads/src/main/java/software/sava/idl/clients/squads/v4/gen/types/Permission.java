package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum Permission implements RustEnum {

  Initiate,
  Vote,
  Execute;

  public static Permission read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, Permission.values(), _data, _offset);
  }
}
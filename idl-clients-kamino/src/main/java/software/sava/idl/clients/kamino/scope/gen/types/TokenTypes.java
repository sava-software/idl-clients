package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum TokenTypes implements RustEnum {

  TokenA,
  TokenB;

  public static TokenTypes read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, TokenTypes.values(), _data, _offset);
  }
}
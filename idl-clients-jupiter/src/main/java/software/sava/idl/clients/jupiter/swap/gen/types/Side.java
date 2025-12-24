package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum Side implements RustEnum {

  Bid,
  Ask;

  public static Side read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, Side.values(), _data, _offset);
  }
}
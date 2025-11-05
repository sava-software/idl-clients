package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.core.borsh.Borsh;

public enum Side implements Borsh.Enum {

  Bid,
  Ask;

  public static Side read(final byte[] _data, final int _offset) {
    return Borsh.read(Side.values(), _data, _offset);
  }
}
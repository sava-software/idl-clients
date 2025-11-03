package software.sava.idl.clients.kamino.farms.gen.types;

import software.sava.core.borsh.Borsh;

public enum RewardType implements Borsh.Enum {

  Proportional,
  Constant;

  public static RewardType read(final byte[] _data, final int _offset) {
    return Borsh.read(RewardType.values(), _data, _offset);
  }
}
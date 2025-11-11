package software.sava.idl.clients.spl.stake.gen.types;

import software.sava.core.borsh.Borsh;

public enum StakeAuthorize implements Borsh.Enum {

  Staker,
  Withdrawer;

  public static StakeAuthorize read(final byte[] _data, final int _offset) {
    return Borsh.read(StakeAuthorize.values(), _data, _offset);
  }
}
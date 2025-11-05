package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.core.borsh.Borsh;

public enum ReserveFarmKind implements Borsh.Enum {

  Collateral,
  Debt;

  public static ReserveFarmKind read(final byte[] _data, final int _offset) {
    return Borsh.read(ReserveFarmKind.values(), _data, _offset);
  }
}
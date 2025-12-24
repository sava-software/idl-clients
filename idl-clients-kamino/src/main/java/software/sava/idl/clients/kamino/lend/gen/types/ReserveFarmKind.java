package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum ReserveFarmKind implements RustEnum {

  Collateral,
  Debt;

  public static ReserveFarmKind read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ReserveFarmKind.values(), _data, _offset);
  }
}
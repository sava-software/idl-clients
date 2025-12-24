package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum ReserveStatus implements RustEnum {

  Active,
  Obsolete,
  Hidden;

  public static ReserveStatus read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ReserveStatus.values(), _data, _offset);
  }
}
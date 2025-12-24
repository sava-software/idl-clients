package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum UpdateGlobalConfigMode implements RustEnum {

  PendingAdmin,
  FeeCollector;

  public static UpdateGlobalConfigMode read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, UpdateGlobalConfigMode.values(), _data, _offset);
  }
}
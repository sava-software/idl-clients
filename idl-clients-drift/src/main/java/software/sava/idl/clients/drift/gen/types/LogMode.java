package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum LogMode implements RustEnum {

  None,
  ExchangeOracle,
  MMOracle,
  SafeMMOracle,
  Margin;

  public static LogMode read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, LogMode.values(), _data, _offset);
  }
}
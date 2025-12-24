package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum ReportDataMarketStatus implements RustEnum {

  Unknown,
  Closed,
  Open;

  public static ReportDataMarketStatus read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ReportDataMarketStatus.values(), _data, _offset);
  }
}
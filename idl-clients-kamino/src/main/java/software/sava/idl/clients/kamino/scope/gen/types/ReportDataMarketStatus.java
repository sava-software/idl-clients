package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.core.borsh.Borsh;

public enum ReportDataMarketStatus implements Borsh.Enum {

  Unknown,
  Closed,
  Open;

  public static ReportDataMarketStatus read(final byte[] _data, final int _offset) {
    return Borsh.read(ReportDataMarketStatus.values(), _data, _offset);
  }
}
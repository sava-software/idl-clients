package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum SettlePnlMode implements Borsh.Enum {

  MustSettle,
  TrySettle;

  public static SettlePnlMode read(final byte[] _data, final int _offset) {
    return Borsh.read(SettlePnlMode.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum MarginMode implements Borsh.Enum {

  Default,
  HighLeverage,
  HighLeverageMaintenance;

  public static MarginMode read(final byte[] _data, final int _offset) {
    return Borsh.read(MarginMode.values(), _data, _offset);
  }
}
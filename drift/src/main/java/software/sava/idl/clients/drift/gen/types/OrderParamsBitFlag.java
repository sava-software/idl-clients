package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum OrderParamsBitFlag implements Borsh.Enum {

  ImmediateOrCancel,
  UpdateHighLeverageMode;

  public static OrderParamsBitFlag read(final byte[] _data, final int _offset) {
    return Borsh.read(OrderParamsBitFlag.values(), _data, _offset);
  }
}
package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// The reset period of the spending limit.
public enum Period implements RustEnum {

  OneTime,
  Day,
  Week,
  Month;

  public static Period read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, Period.values(), _data, _offset);
  }
}
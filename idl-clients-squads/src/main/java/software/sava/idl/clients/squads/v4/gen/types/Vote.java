package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum Vote implements RustEnum {

  Approve,
  Reject,
  Cancel;

  public static Vote read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, Vote.values(), _data, _offset);
  }
}
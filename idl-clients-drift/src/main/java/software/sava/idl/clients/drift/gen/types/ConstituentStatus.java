package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum ConstituentStatus implements RustEnum {

  ReduceOnly,
  Decommissioned;

  public static ConstituentStatus read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ConstituentStatus.values(), _data, _offset);
  }
}
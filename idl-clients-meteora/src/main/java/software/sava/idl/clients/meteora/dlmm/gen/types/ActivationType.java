package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Type of the activation
public enum ActivationType implements RustEnum {

  Slot,
  Timestamp;

  public static ActivationType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ActivationType.values(), _data, _offset);
  }
}
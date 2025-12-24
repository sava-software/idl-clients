package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Pair status. 0 = Enabled, 1 = Disabled. Putting 0 as enabled for backward compatibility.
public enum PairStatus implements RustEnum {

  Enabled,
  Disabled;

  public static PairStatus read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, PairStatus.values(), _data, _offset);
  }
}
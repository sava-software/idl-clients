package software.sava.idl.clients.drift.vaults.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum FeeUpdateStatus implements RustEnum {

  None,
  PendingFeeUpdate;

  public static FeeUpdateStatus read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, FeeUpdateStatus.values(), _data, _offset);
  }
}
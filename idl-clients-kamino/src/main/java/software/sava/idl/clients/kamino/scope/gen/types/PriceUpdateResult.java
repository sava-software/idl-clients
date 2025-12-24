package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum PriceUpdateResult implements RustEnum {

  Updated,
  SuspendExistingPrice;

  public static PriceUpdateResult read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, PriceUpdateResult.values(), _data, _offset);
  }
}
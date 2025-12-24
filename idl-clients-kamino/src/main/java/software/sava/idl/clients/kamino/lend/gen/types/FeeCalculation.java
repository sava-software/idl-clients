package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Calculate fees exlusive or inclusive of an amount
public enum FeeCalculation implements RustEnum {

  Exclusive,
  Inclusive;

  public static FeeCalculation read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, FeeCalculation.values(), _data, _offset);
  }
}
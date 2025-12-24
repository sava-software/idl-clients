package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum InsuranceFundOperation implements RustEnum {

  Init,
  Add,
  RequestRemove,
  Remove;

  public static InsuranceFundOperation read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, InsuranceFundOperation.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.vaults.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum VaultClass implements RustEnum {

  Normal,
  Trusted;

  public static VaultClass read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, VaultClass.values(), _data, _offset);
  }
}
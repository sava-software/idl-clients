package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum AssetType implements RustEnum {

  Base,
  Quote;

  public static AssetType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, AssetType.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum AssetTier implements RustEnum {

  Collateral,
  Protected,
  Cross,
  Isolated,
  Unlisted;

  public static AssetTier read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, AssetTier.values(), _data, _offset);
  }
}
package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.core.borsh.Borsh;

public enum AssetTier implements Borsh.Enum {

  Regular,
  IsolatedCollateral,
  IsolatedDebt;

  public static AssetTier read(final byte[] _data, final int _offset) {
    return Borsh.read(AssetTier.values(), _data, _offset);
  }
}
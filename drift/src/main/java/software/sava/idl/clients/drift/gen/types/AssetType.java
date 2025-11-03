package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum AssetType implements Borsh.Enum {

  Base,
  Quote;

  public static AssetType read(final byte[] _data, final int _offset) {
    return Borsh.read(AssetType.values(), _data, _offset);
  }
}
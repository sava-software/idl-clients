package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum ModifyOrderPolicy implements Borsh.Enum {

  MustModify,
  ExcludePreviousFill;

  public static ModifyOrderPolicy read(final byte[] _data, final int _offset) {
    return Borsh.read(ModifyOrderPolicy.values(), _data, _offset);
  }
}
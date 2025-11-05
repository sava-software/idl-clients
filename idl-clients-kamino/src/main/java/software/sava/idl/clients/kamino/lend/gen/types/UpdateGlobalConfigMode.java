package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.core.borsh.Borsh;

public enum UpdateGlobalConfigMode implements Borsh.Enum {

  PendingAdmin,
  FeeCollector;

  public static UpdateGlobalConfigMode read(final byte[] _data, final int _offset) {
    return Borsh.read(UpdateGlobalConfigMode.values(), _data, _offset);
  }
}
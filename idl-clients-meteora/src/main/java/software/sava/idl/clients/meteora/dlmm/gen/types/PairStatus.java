package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.core.borsh.Borsh;

/// Pair status. 0 = Enabled, 1 = Disabled. Putting 0 as enabled for backward compatibility.
public enum PairStatus implements Borsh.Enum {

  Enabled,
  Disabled;

  public static PairStatus read(final byte[] _data, final int _offset) {
    return Borsh.read(PairStatus.values(), _data, _offset);
  }
}
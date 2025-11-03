package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum SwapReduceOnly implements Borsh.Enum {

  In,
  Out;

  public static SwapReduceOnly read(final byte[] _data, final int _offset) {
    return Borsh.read(SwapReduceOnly.values(), _data, _offset);
  }
}
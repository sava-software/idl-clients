package software.sava.idl.clients.jupiter.merkle_distributor.gen.types;

import software.sava.core.borsh.Borsh;

/// Type of the activation
public enum ActivationType implements Borsh.Enum {

  Slot,
  Timestamp;

  public static ActivationType read(final byte[] _data, final int _offset) {
    return Borsh.read(ActivationType.values(), _data, _offset);
  }
}
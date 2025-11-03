package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum SpotFulfillmentConfigStatus implements Borsh.Enum {

  Enabled,
  Disabled;

  public static SpotFulfillmentConfigStatus read(final byte[] _data, final int _offset) {
    return Borsh.read(SpotFulfillmentConfigStatus.values(), _data, _offset);
  }
}
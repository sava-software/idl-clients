package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.core.borsh.Borsh;

public enum PriceUpdateResult implements Borsh.Enum {

  Updated,
  SuspendExistingPrice;

  public static PriceUpdateResult read(final byte[] _data, final int _offset) {
    return Borsh.read(PriceUpdateResult.values(), _data, _offset);
  }
}
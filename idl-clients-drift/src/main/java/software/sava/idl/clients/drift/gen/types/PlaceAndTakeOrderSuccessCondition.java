package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum PlaceAndTakeOrderSuccessCondition implements Borsh.Enum {

  PartialFill,
  FullFill;

  public static PlaceAndTakeOrderSuccessCondition read(final byte[] _data, final int _offset) {
    return Borsh.read(PlaceAndTakeOrderSuccessCondition.values(), _data, _offset);
  }
}
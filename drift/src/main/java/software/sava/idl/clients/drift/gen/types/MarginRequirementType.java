package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum MarginRequirementType implements Borsh.Enum {

  Initial,
  Fill,
  Maintenance;

  public static MarginRequirementType read(final byte[] _data, final int _offset) {
    return Borsh.read(MarginRequirementType.values(), _data, _offset);
  }
}
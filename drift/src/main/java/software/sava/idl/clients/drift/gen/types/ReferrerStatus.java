package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum ReferrerStatus implements Borsh.Enum {

  IsReferrer,
  IsReferred,
  BuilderReferral;

  public static ReferrerStatus read(final byte[] _data, final int _offset) {
    return Borsh.read(ReferrerStatus.values(), _data, _offset);
  }
}
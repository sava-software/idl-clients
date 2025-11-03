package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum FeatureBitFlags implements Borsh.Enum {

  MmOracleUpdate,
  MedianTriggerPrice,
  BuilderCodes,
  BuilderReferral;

  public static FeatureBitFlags read(final byte[] _data, final int _offset) {
    return Borsh.read(FeatureBitFlags.values(), _data, _offset);
  }
}
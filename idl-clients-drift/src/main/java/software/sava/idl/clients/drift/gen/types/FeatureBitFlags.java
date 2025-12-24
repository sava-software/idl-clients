package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum FeatureBitFlags implements RustEnum {

  MmOracleUpdate,
  MedianTriggerPrice,
  BuilderCodes,
  BuilderReferral;

  public static FeatureBitFlags read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, FeatureBitFlags.values(), _data, _offset);
  }
}
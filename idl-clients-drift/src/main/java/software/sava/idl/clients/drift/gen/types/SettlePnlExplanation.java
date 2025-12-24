package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum SettlePnlExplanation implements RustEnum {

  None,
  ExpiredPosition;

  public static SettlePnlExplanation read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, SettlePnlExplanation.values(), _data, _offset);
  }
}
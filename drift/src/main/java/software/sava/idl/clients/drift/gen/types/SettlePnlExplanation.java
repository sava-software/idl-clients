package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum SettlePnlExplanation implements Borsh.Enum {

  None,
  ExpiredPosition;

  public static SettlePnlExplanation read(final byte[] _data, final int _offset) {
    return Borsh.read(SettlePnlExplanation.values(), _data, _offset);
  }
}
package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum RevenueShareOrderBitFlag implements Borsh.Enum {

  Init,
  Open,
  Completed,
  Referral;

  public static RevenueShareOrderBitFlag read(final byte[] _data, final int _offset) {
    return Borsh.read(RevenueShareOrderBitFlag.values(), _data, _offset);
  }
}
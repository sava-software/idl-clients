package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum TwapPeriod implements Borsh.Enum {

  FundingPeriod,
  FiveMin;

  public static TwapPeriod read(final byte[] _data, final int _offset) {
    return Borsh.read(TwapPeriod.values(), _data, _offset);
  }
}
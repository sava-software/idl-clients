package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum PerpLpOperation implements Borsh.Enum {

  TrackAmmRevenue,
  SettleQuoteOwed;

  public static PerpLpOperation read(final byte[] _data, final int _offset) {
    return Borsh.read(PerpLpOperation.values(), _data, _offset);
  }
}
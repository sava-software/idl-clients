package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.core.borsh.Borsh;

public enum StrategyType implements Borsh.Enum {

  SpotOneSide,
  CurveOneSide,
  BidAskOneSide,
  SpotBalanced,
  CurveBalanced,
  BidAskBalanced,
  SpotImBalanced,
  CurveImBalanced,
  BidAskImBalanced;

  public static StrategyType read(final byte[] _data, final int _offset) {
    return Borsh.read(StrategyType.values(), _data, _offset);
  }
}
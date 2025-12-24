package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum StrategyType implements RustEnum {

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
    return SerDeUtil.read(1, StrategyType.values(), _data, _offset);
  }
}
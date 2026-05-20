package software.sava.idl.clients.jupiter.borrow.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum SourceType implements RustEnum {

  Pyth,
  StakePool,
  MsolPool,
  Redstone,
  Chainlink,
  SinglePool,
  JupLend,
  ChainlinkDataStreams;

  public static SourceType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, SourceType.values(), _data, _offset);
  }
}
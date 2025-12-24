package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum MarketStatusBehavior implements RustEnum {

  AllUpdates,
  Open,
  OpenAndPrePost;

  public static MarketStatusBehavior read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, MarketStatusBehavior.values(), _data, _offset);
  }
}
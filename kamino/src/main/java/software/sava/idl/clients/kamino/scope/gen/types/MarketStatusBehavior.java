package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.core.borsh.Borsh;

public enum MarketStatusBehavior implements Borsh.Enum {

  AllUpdates,
  Open,
  OpenAndPrePost;

  public static MarketStatusBehavior read(final byte[] _data, final int _offset) {
    return Borsh.read(MarketStatusBehavior.values(), _data, _offset);
  }
}
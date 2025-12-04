package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum UserStatsPausedOperations implements Borsh.Enum {

  UpdateBidAskTwap,
  AmmAtomicFill,
  AmmAtomicRiskIncreasingFill;

  public static UserStatsPausedOperations read(final byte[] _data, final int _offset) {
    return Borsh.read(UserStatsPausedOperations.values(), _data, _offset);
  }
}
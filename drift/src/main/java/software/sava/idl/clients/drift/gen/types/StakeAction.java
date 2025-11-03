package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum StakeAction implements Borsh.Enum {

  Stake,
  UnstakeRequest,
  UnstakeCancelRequest,
  Unstake,
  UnstakeTransfer,
  StakeTransfer,
  AdminDeposit;

  public static StakeAction read(final byte[] _data, final int _offset) {
    return Borsh.read(StakeAction.values(), _data, _offset);
  }
}
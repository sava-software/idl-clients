package software.sava.idl.clients.drift.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum StakeAction implements RustEnum {

  Stake,
  UnstakeRequest,
  UnstakeCancelRequest,
  Unstake,
  UnstakeTransfer,
  StakeTransfer,
  AdminDeposit;

  public static StakeAction read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, StakeAction.values(), _data, _offset);
  }
}
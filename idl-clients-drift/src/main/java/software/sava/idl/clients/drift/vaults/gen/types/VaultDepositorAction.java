package software.sava.idl.clients.drift.vaults.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum VaultDepositorAction implements RustEnum {

  Deposit,
  WithdrawRequest,
  CancelWithdrawRequest,
  Withdraw,
  FeePayment,
  TokenizeShares,
  RedeemTokens;

  public static VaultDepositorAction read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, VaultDepositorAction.values(), _data, _offset);
  }
}
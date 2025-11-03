package software.sava.idl.clients.drift.vaults.gen.types;

import software.sava.core.borsh.Borsh;

public enum VaultDepositorAction implements Borsh.Enum {

  Deposit,
  WithdrawRequest,
  CancelWithdrawRequest,
  Withdraw,
  FeePayment,
  TokenizeShares,
  RedeemTokens;

  public static VaultDepositorAction read(final byte[] _data, final int _offset) {
    return Borsh.read(VaultDepositorAction.values(), _data, _offset);
  }
}
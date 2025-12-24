package software.sava.idl.clients.kamino.vaults.gen.events;

import software.sava.idl.clients.core.gen.SerDe;

public sealed interface KaminoVaultEvent extends SerDe permits
    DepositResultEvent,
    DepositUserAtaBalanceEvent,
    SharesToWithdrawEvent,
    WithdrawResultEvent {

  static KaminoVaultEvent read(final byte[] _data, final int _offset) {
    if (DepositResultEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return DepositResultEvent.read(_data, _offset);
    } else if (DepositUserAtaBalanceEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return DepositUserAtaBalanceEvent.read(_data, _offset);
    } else if (SharesToWithdrawEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return SharesToWithdrawEvent.read(_data, _offset);
    } else if (WithdrawResultEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return WithdrawResultEvent.read(_data, _offset);
    } else {
      return null;
    }
  }

  static KaminoVaultEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static KaminoVaultEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static KaminoVaultEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}
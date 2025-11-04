package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.core.borsh.Borsh;

public enum AccountsType implements Borsh.Enum {

  TransferHookA,
  TransferHookB,
  TransferHookReward,
  TransferHookInput,
  TransferHookIntermediate,
  TransferHookOutput,
  SupplementalTickArrays,
  SupplementalTickArraysOne,
  SupplementalTickArraysTwo;

  public static AccountsType read(final byte[] _data, final int _offset) {
    return Borsh.read(AccountsType.values(), _data, _offset);
  }
}
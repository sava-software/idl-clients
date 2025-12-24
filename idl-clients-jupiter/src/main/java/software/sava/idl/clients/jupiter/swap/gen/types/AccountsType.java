package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum AccountsType implements RustEnum {

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
    return SerDeUtil.read(1, AccountsType.values(), _data, _offset);
  }
}
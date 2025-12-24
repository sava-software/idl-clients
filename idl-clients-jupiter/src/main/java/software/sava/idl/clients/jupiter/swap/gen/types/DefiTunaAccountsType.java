package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum DefiTunaAccountsType implements RustEnum {

  TransferHookA,
  TransferHookB,
  TransferHookInput,
  TransferHookIntermediate,
  TransferHookOutput,
  SupplementalTickArrays,
  SupplementalTickArraysOne,
  SupplementalTickArraysTwo;

  public static DefiTunaAccountsType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, DefiTunaAccountsType.values(), _data, _offset);
  }
}
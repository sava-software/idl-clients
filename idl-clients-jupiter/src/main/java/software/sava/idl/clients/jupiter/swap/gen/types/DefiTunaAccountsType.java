package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.core.borsh.Borsh;

public enum DefiTunaAccountsType implements Borsh.Enum {

  TransferHookA,
  TransferHookB,
  TransferHookInput,
  TransferHookIntermediate,
  TransferHookOutput,
  SupplementalTickArrays,
  SupplementalTickArraysOne,
  SupplementalTickArraysTwo;

  public static DefiTunaAccountsType read(final byte[] _data, final int _offset) {
    return Borsh.read(DefiTunaAccountsType.values(), _data, _offset);
  }
}
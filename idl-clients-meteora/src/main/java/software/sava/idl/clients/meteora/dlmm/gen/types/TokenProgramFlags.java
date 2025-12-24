package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum TokenProgramFlags implements RustEnum {

  TokenProgram,
  TokenProgram2022;

  public static TokenProgramFlags read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, TokenProgramFlags.values(), _data, _offset);
  }
}
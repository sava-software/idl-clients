package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.core.borsh.Borsh;

public enum TokenProgramFlags implements Borsh.Enum {

  TokenProgram,
  TokenProgram2022;

  public static TokenProgramFlags read(final byte[] _data, final int _offset) {
    return Borsh.read(TokenProgramFlags.values(), _data, _offset);
  }
}
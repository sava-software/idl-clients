package software.sava.idl.clients.spl.token.gen.types;

import software.sava.core.serial.Serializable;

public enum AccountState implements Serializable {

  uninitialized,
  initialized,
  frozen;

  public static AccountState read(final byte[] _data, final int _offset) {
    return AccountState.values()[_data[_offset] & 0xFF];
  }
  
  @Override
  public int write(final byte[] _data, final int _offset) {
    _data[_offset] = (byte) ordinal();
    return 1;
  }
  
  @Override
  public int l() {
    return 1;
  }
}
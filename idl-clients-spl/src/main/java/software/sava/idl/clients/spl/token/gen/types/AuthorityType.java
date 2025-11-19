package software.sava.idl.clients.spl.token.gen.types;

import software.sava.core.serial.Serializable;

public enum AuthorityType implements Serializable {

  mintTokens,
  freezeAccount,
  accountOwner,
  closeAccount;

  public static AuthorityType read(final byte[] _data, final int _offset) {
    return AuthorityType.values()[_data[_offset] & 0xFF];
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
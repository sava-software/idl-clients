package software.sava.idl.clients.spl.token.gen.types;

import software.sava.core.borsh.Borsh;

public enum AuthorityType implements Borsh.Enum {

  mintTokens,
  freezeAccount,
  accountOwner,
  closeAccount;

  public static AuthorityType read(final byte[] _data, final int _offset) {
    return Borsh.read(AuthorityType.values(), _data, _offset);
  }
}
package software.sava.idl.clients.marinade.stake_pool.gen.types;

import software.sava.core.borsh.Borsh;

public record ValidatorList() implements Borsh {

  private static final ValidatorList INSTANCE = new ValidatorList();

  public static ValidatorList read(final byte[] _data, final int _offset) {
    return INSTANCE;
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    return 0;
  }

  @Override
  public int l() {
    return 0;
  }
}

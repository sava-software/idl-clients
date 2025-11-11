package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.core.borsh.Borsh;

public record QueueResetVaultParams() implements Borsh {

  private static final QueueResetVaultParams INSTANCE = new QueueResetVaultParams();

  public static QueueResetVaultParams read(final byte[] _data, final int _offset) {
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

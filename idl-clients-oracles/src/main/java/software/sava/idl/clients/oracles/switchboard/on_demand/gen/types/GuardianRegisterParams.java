package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record GuardianRegisterParams() implements SerDe {

  private static final GuardianRegisterParams INSTANCE = new GuardianRegisterParams();

  public static GuardianRegisterParams read(final byte[] _data, final int _offset) {
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

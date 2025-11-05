package software.sava.idl.clients.switchboard.on_demand.gen.types;

import software.sava.core.borsh.Borsh;

public record OracleSetOperatorParams() implements Borsh {

  private static final OracleSetOperatorParams INSTANCE = new OracleSetOperatorParams();

  public static OracleSetOperatorParams read(final byte[] _data, final int _offset) {
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

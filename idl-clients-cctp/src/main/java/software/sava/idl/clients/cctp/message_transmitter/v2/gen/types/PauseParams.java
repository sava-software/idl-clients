package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record PauseParams() implements SerDe {

  private static final PauseParams INSTANCE = new PauseParams();

  public static PauseParams read(final byte[] _data, final int _offset) {
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

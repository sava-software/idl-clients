package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record AcceptOwnershipParams() implements SerDe {

  private static final AcceptOwnershipParams INSTANCE = new AcceptOwnershipParams();

  public static AcceptOwnershipParams read(final byte[] _data, final int _offset) {
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

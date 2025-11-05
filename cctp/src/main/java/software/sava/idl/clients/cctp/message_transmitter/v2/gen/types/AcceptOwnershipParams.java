package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import software.sava.core.borsh.Borsh;

public record AcceptOwnershipParams() implements Borsh {

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

package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.core.borsh.Borsh;

public record QueueRemoveMrEnclaveParams(byte[] mrEnclave) implements Borsh {

  public static final int BYTES = 32;
  public static final int MR_ENCLAVE_LEN = 32;

  public static QueueRemoveMrEnclaveParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var mrEnclave = new byte[32];
    Borsh.readArray(mrEnclave, _data, _offset);
    return new QueueRemoveMrEnclaveParams(mrEnclave);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeArrayChecked(mrEnclave, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

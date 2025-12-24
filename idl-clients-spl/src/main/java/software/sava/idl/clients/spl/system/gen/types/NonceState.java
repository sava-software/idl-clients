package software.sava.idl.clients.spl.system.gen.types;

import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.core.gen.SerDe;

public enum NonceState implements SerDe {

  uninitialized,
  initialized;

  public static NonceState read(final byte[] _data, final int _offset) {
    return NonceState.values()[ByteUtil.getInt32LE(_data, _offset)];
  }
  
  @Override
  public int write(final byte[] _data, final int _offset) {
    ByteUtil.putInt32LE(_data, _offset, ordinal());
    return 4;
  }
  
  @Override
  public int l() {
    return 4;
  }
}
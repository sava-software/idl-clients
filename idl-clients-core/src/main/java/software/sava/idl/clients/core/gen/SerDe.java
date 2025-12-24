package software.sava.idl.clients.core.gen;

import software.sava.core.serial.Serializable;

public interface SerDe extends Serializable {

  int l();

  int write(byte[] data, int offset);

  default int write(final byte[] data) {
    return write(data, 0);
  }

  default byte[] write() {
    final byte[] data = new byte[l()];
    write(data);
    return data;
  }
}

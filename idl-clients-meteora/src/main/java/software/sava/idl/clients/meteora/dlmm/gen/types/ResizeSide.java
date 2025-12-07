package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.core.borsh.Borsh;

/// Side of resize, 0 for lower and 1 for upper
public enum ResizeSide implements Borsh.Enum {

  Lower,
  Upper;

  public static ResizeSide read(final byte[] _data, final int _offset) {
    return Borsh.read(ResizeSide.values(), _data, _offset);
  }
}
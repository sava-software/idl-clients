package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Side of resize, 0 for lower and 1 for upper
public enum ResizeSide implements RustEnum {

  Lower,
  Upper;

  public static ResizeSide read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ResizeSide.values(), _data, _offset);
  }
}
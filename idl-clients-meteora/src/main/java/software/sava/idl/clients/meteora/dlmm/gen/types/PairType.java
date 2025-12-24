package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Type of the Pair. 0 = Permissionless, 1 = Permission, 2 = CustomizablePermissionless. Putting 0 as permissionless for backward compatibility.
public enum PairType implements RustEnum {

  Permissionless,
  Permission,
  CustomizablePermissionless,
  PermissionlessV2;

  public static PairType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, PairType.values(), _data, _offset);
  }
}
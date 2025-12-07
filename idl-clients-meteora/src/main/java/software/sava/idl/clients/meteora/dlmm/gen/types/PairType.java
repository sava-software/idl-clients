package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.core.borsh.Borsh;

/// Type of the Pair. 0 = Permissionless, 1 = Permission, 2 = CustomizablePermissionless. Putting 0 as permissionless for backward compatibility.
public enum PairType implements Borsh.Enum {

  Permissionless,
  Permission,
  CustomizablePermissionless,
  PermissionlessV2;

  public static PairType read(final byte[] _data, final int _offset) {
    return Borsh.read(PairType.values(), _data, _offset);
  }
}
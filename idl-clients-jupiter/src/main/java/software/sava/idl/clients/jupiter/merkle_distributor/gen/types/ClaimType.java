package software.sava.idl.clients.jupiter.merkle_distributor.gen.types;

import software.sava.core.borsh.Borsh;

/// Type of the activation
public enum ClaimType implements Borsh.Enum {

  Permissionless,
  Permissioned,
  PermissionlessWithStaking,
  PermissionedWithStaking;

  public static ClaimType read(final byte[] _data, final int _offset) {
    return Borsh.read(ClaimType.values(), _data, _offset);
  }
}
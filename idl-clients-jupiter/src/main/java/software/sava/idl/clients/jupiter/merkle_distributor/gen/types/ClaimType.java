package software.sava.idl.clients.jupiter.merkle_distributor.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Type of the activation
public enum ClaimType implements RustEnum {

  Permissionless,
  Permissioned,
  PermissionlessWithStaking,
  PermissionedWithStaking;

  public static ClaimType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ClaimType.values(), _data, _offset);
  }
}
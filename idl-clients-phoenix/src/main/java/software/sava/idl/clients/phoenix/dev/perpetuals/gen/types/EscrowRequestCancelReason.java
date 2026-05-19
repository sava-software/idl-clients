package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum EscrowRequestCancelReason implements RustEnum {

  Expiration,
  CancelledBySender,
  CancelledByReceiver;

  public static EscrowRequestCancelReason read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, EscrowRequestCancelReason.values(), _data, _offset);
  }
}
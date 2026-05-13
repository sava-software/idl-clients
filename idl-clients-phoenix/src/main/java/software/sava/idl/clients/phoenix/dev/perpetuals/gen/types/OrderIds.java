package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Collection of cancel identifiers used to target specific resting orders.
///
public record OrderIds(CancelId[] orderIds) implements SerDe {

  public static final int ORDER_IDS_OFFSET = 0;

  public static OrderIds read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var orderIds = SerDeUtil.readVector(4, CancelId.class, CancelId::read, _data, _offset);
    return new OrderIds(orderIds);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeVector(4, orderIds, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return SerDeUtil.lenVector(4, orderIds);
  }
}

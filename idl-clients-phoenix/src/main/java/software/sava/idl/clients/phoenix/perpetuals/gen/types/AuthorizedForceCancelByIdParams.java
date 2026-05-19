package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Borsh payload for authority-driven force cancellation by order id.
///
public record AuthorizedForceCancelByIdParams(CancelId[] bidOrderIds, CancelId[] askOrderIds) implements SerDe {

  public static final int BID_ORDER_IDS_OFFSET = 0;

  public static AuthorizedForceCancelByIdParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var bidOrderIds = SerDeUtil.readVector(4, CancelId.class, CancelId::read, _data, i);
    i += SerDeUtil.lenVector(4, bidOrderIds);
    final var askOrderIds = SerDeUtil.readVector(4, CancelId.class, CancelId::read, _data, i);
    return new AuthorizedForceCancelByIdParams(bidOrderIds, askOrderIds);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeVector(4, bidOrderIds, _data, i);
    i += SerDeUtil.writeVector(4, askOrderIds, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return SerDeUtil.lenVector(4, bidOrderIds) + SerDeUtil.lenVector(4, askOrderIds);
  }
}

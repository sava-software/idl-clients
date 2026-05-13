package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Parameters controlling liquidation transfers between traders.
///
public record LiquidationTransferParams(TransferRequest[] transferRequests, boolean executeAllTransfers) implements SerDe {

  public static final int TRANSFER_REQUESTS_OFFSET = 0;

  public static LiquidationTransferParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var transferRequests = SerDeUtil.readVector(4, TransferRequest.class, TransferRequest::read, _data, i);
    i += SerDeUtil.lenVector(4, transferRequests);
    final var executeAllTransfers = _data[i] == 1;
    return new LiquidationTransferParams(transferRequests, executeAllTransfers);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeVector(4, transferRequests, _data, i);
    _data[i] = (byte) (executeAllTransfers ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return SerDeUtil.lenVector(4, transferRequests) + 1;
  }
}

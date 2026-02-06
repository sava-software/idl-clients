package software.sava.idl.clients.kamino.lend.gen.events;

import software.sava.idl.clients.core.gen.SerDe;

public sealed interface KaminoLendingEvent extends SerDe permits
    BorrowOrderCancelEvent,
    BorrowOrderFullFillEvent,
    BorrowOrderPartialFillEvent,
    BorrowOrderPlaceEvent,
    BorrowOrderUpdateEvent {

  static KaminoLendingEvent read(final byte[] _data, final int _offset) {
    if (BorrowOrderCancelEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return BorrowOrderCancelEvent.read(_data, _offset);
    } else if (BorrowOrderFullFillEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return BorrowOrderFullFillEvent.read(_data, _offset);
    } else if (BorrowOrderPartialFillEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return BorrowOrderPartialFillEvent.read(_data, _offset);
    } else if (BorrowOrderPlaceEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return BorrowOrderPlaceEvent.read(_data, _offset);
    } else if (BorrowOrderUpdateEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return BorrowOrderUpdateEvent.read(_data, _offset);
    } else {
      return null;
    }
  }

  static KaminoLendingEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static KaminoLendingEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static KaminoLendingEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}
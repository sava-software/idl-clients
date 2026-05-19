package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import java.util.OptionalInt;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record ConditionalOrderPingSnapshot(ConditionalOrderPingStateSnapshot state,
                                           OptionalFIFOOrderId attachedOrderId,
                                           BaseLots maxSize,
                                           BaseLots fillableSize,
                                           BaseLots filledSize,
                                           OptionalInt coPositionSequenceNumber) implements SerDe {

  public static final int STATE_OFFSET = 0;
  public static final int ATTACHED_ORDER_ID_OFFSET = 1;
  public static final int MAX_SIZE_OFFSET = 17;
  public static final int FILLABLE_SIZE_OFFSET = 25;
  public static final int FILLED_SIZE_OFFSET = 33;
  public static final int CO_POSITION_SEQUENCE_NUMBER_OFFSET = 42;

  public static ConditionalOrderPingSnapshot read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var state = ConditionalOrderPingStateSnapshot.read(_data, i);
    i += state.l();
    final var attachedOrderId = OptionalFIFOOrderId.read(_data, i);
    i += attachedOrderId.l();
    final var maxSize = BaseLots.read(_data, i);
    i += maxSize.l();
    final var fillableSize = BaseLots.read(_data, i);
    i += fillableSize.l();
    final var filledSize = BaseLots.read(_data, i);
    i += filledSize.l();
    final OptionalInt coPositionSequenceNumber;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      coPositionSequenceNumber = OptionalInt.empty();
    } else {
      ++i;
      coPositionSequenceNumber = OptionalInt.of(_data[i] & 0xFF);
    }
    return new ConditionalOrderPingSnapshot(state,
                                            attachedOrderId,
                                            maxSize,
                                            fillableSize,
                                            filledSize,
                                            coPositionSequenceNumber);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += state.write(_data, i);
    i += attachedOrderId.write(_data, i);
    i += maxSize.write(_data, i);
    i += fillableSize.write(_data, i);
    i += filledSize.write(_data, i);
    i += SerDeUtil.writeOptionalbyte(1, coPositionSequenceNumber, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return state.l()
         + attachedOrderId.l()
         + maxSize.l()
         + fillableSize.l()
         + filledSize.l()
         + (coPositionSequenceNumber == null || coPositionSequenceNumber.isEmpty() ? 1 : (1 + 1));
  }
}

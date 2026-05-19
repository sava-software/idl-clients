package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

/// Trader PDA/subaccount metadata stored with escrow requests.
///
public record EscrowParticipantMetadata(int senderPdaIndex,
                                        int senderSubaccountIndex,
                                        int receiverPdaIndex,
                                        int receiverSubaccountIndex) implements SerDe {

  public static final int BYTES = 4;

  public static final int SENDER_PDA_INDEX_OFFSET = 0;
  public static final int SENDER_SUBACCOUNT_INDEX_OFFSET = 1;
  public static final int RECEIVER_PDA_INDEX_OFFSET = 2;
  public static final int RECEIVER_SUBACCOUNT_INDEX_OFFSET = 3;

  public static EscrowParticipantMetadata read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var senderPdaIndex = _data[i] & 0xFF;
    ++i;
    final var senderSubaccountIndex = _data[i] & 0xFF;
    ++i;
    final var receiverPdaIndex = _data[i] & 0xFF;
    ++i;
    final var receiverSubaccountIndex = _data[i] & 0xFF;
    return new EscrowParticipantMetadata(senderPdaIndex,
                                         senderSubaccountIndex,
                                         receiverPdaIndex,
                                         receiverSubaccountIndex);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) senderPdaIndex;
    ++i;
    _data[i] = (byte) senderSubaccountIndex;
    ++i;
    _data[i] = (byte) receiverPdaIndex;
    ++i;
    _data[i] = (byte) receiverSubaccountIndex;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

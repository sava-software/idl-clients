package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// A tracker of ticket-based withdrawals.
///
/// @param queuedCollateralAmount The part of ReserveLiquidity::total_available_amount locked for ticketed withdrawals.
/// @param nextIssuedTicketSequenceNumber The sequence number of the next ticket to be issued when enqueueing to withdraw.
///                                       Note: it is also a number of tickets issued so far.
/// @param nextWithdrawableTicketSequenceNumber The sequence number of the next ticket to be used for actually transferring the withdrawn
///                                             liquidity (assuming it is available in the reserve).
///                                             Note: it is also a number of fully-consumed tickets so far.
public record WithdrawQueue(long queuedCollateralAmount,
                            long nextIssuedTicketSequenceNumber,
                            long nextWithdrawableTicketSequenceNumber) implements SerDe {

  public static final int BYTES = 24;

  public static final int QUEUED_COLLATERAL_AMOUNT_OFFSET = 0;
  public static final int NEXT_ISSUED_TICKET_SEQUENCE_NUMBER_OFFSET = 8;
  public static final int NEXT_WITHDRAWABLE_TICKET_SEQUENCE_NUMBER_OFFSET = 16;

  public static WithdrawQueue read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var queuedCollateralAmount = getInt64LE(_data, i);
    i += 8;
    final var nextIssuedTicketSequenceNumber = getInt64LE(_data, i);
    i += 8;
    final var nextWithdrawableTicketSequenceNumber = getInt64LE(_data, i);
    return new WithdrawQueue(queuedCollateralAmount, nextIssuedTicketSequenceNumber, nextWithdrawableTicketSequenceNumber);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, queuedCollateralAmount);
    i += 8;
    putInt64LE(_data, i, nextIssuedTicketSequenceNumber);
    i += 8;
    putInt64LE(_data, i, nextWithdrawableTicketSequenceNumber);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

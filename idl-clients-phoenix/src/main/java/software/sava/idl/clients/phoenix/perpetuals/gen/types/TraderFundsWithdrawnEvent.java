package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::TraderFundsWithdrawn Borsh variant 14.
/// Payload type: TraderFundsWithdrawnEvent.
///
public record TraderFundsWithdrawnEvent(Discriminator discriminator,
                                        PublicKey trader,
                                        PublicKey authority,
                                        long amount,
                                        long traderSequenceNumber,
                                        long traderPrevSequenceNumberSlot,
                                        long postWithdrawalBudget,
                                        long postQueueSize,
                                        long totalQueuedAmount,
                                        long withdrawQueueSequenceNumber,
                                        long withdrawQueuePrevSequenceNumberSlot) implements EternalEvent {

  public static final int BYTES = 136;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(14, 0, 0, 0, 0, 0, 0, 0);

  public static final int TRADER_OFFSET = 8;
  public static final int AUTHORITY_OFFSET = 40;
  public static final int AMOUNT_OFFSET = 72;
  public static final int TRADER_SEQUENCE_NUMBER_OFFSET = 80;
  public static final int TRADER_PREV_SEQUENCE_NUMBER_SLOT_OFFSET = 88;
  public static final int POST_WITHDRAWAL_BUDGET_OFFSET = 96;
  public static final int POST_QUEUE_SIZE_OFFSET = 104;
  public static final int TOTAL_QUEUED_AMOUNT_OFFSET = 112;
  public static final int WITHDRAW_QUEUE_SEQUENCE_NUMBER_OFFSET = 120;
  public static final int WITHDRAW_QUEUE_PREV_SEQUENCE_NUMBER_SLOT_OFFSET = 128;

  public static TraderFundsWithdrawnEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var trader = readPubKey(_data, i);
    i += 32;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var amount = getInt64LE(_data, i);
    i += 8;
    final var traderSequenceNumber = getInt64LE(_data, i);
    i += 8;
    final var traderPrevSequenceNumberSlot = getInt64LE(_data, i);
    i += 8;
    final var postWithdrawalBudget = getInt64LE(_data, i);
    i += 8;
    final var postQueueSize = getInt64LE(_data, i);
    i += 8;
    final var totalQueuedAmount = getInt64LE(_data, i);
    i += 8;
    final var withdrawQueueSequenceNumber = getInt64LE(_data, i);
    i += 8;
    final var withdrawQueuePrevSequenceNumberSlot = getInt64LE(_data, i);
    return new TraderFundsWithdrawnEvent(discriminator,
                                         trader,
                                         authority,
                                         amount,
                                         traderSequenceNumber,
                                         traderPrevSequenceNumberSlot,
                                         postWithdrawalBudget,
                                         postQueueSize,
                                         totalQueuedAmount,
                                         withdrawQueueSequenceNumber,
                                         withdrawQueuePrevSequenceNumberSlot);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    trader.write(_data, i);
    i += 32;
    authority.write(_data, i);
    i += 32;
    putInt64LE(_data, i, amount);
    i += 8;
    putInt64LE(_data, i, traderSequenceNumber);
    i += 8;
    putInt64LE(_data, i, traderPrevSequenceNumberSlot);
    i += 8;
    putInt64LE(_data, i, postWithdrawalBudget);
    i += 8;
    putInt64LE(_data, i, postQueueSize);
    i += 8;
    putInt64LE(_data, i, totalQueuedAmount);
    i += 8;
    putInt64LE(_data, i, withdrawQueueSequenceNumber);
    i += 8;
    putInt64LE(_data, i, withdrawQueuePrevSequenceNumberSlot);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

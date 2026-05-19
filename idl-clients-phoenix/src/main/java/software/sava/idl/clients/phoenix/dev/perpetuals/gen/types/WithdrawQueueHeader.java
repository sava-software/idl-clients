package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Fixed WithdrawQueueHeader prefix for the Phoenix Eternal withdraw queue account.
/// The deque of pending withdrawal requests follows this header and is left as trailing account data by generic decoders.
///
public record WithdrawQueueHeader(PublicKey _address,
                                  Discriminator discriminator,
                                  long discriminant,
                                  SequenceNumber sequenceNumber,
                                  WithdrawThrottle withdrawThrottle,
                                  QuoteLots totalQueuedAmount,
                                  QuoteLots withdrawalFee,
                                  QuoteLots enqueueingFee,
                                  long[] reserved) implements SerDe {

  public static final int BYTES = 128;
  public static final int RESERVED_LEN = 5;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(125, 59, 94, 108, 171, 16, 43, 228);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int DISCRIMINANT_OFFSET = 8;
  public static final int SEQUENCE_NUMBER_OFFSET = 16;
  public static final int WITHDRAW_THROTTLE_OFFSET = 32;
  public static final int TOTAL_QUEUED_AMOUNT_OFFSET = 64;
  public static final int WITHDRAWAL_FEE_OFFSET = 72;
  public static final int ENQUEUEING_FEE_OFFSET = 80;
  public static final int RESERVED_OFFSET = 88;

  public static Filter createDiscriminantFilter(final long discriminant) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, discriminant);
    return Filter.createMemCompFilter(DISCRIMINANT_OFFSET, _data);
  }

  public static Filter createSequenceNumberFilter(final SequenceNumber sequenceNumber) {
    return Filter.createMemCompFilter(SEQUENCE_NUMBER_OFFSET, sequenceNumber.write());
  }

  public static Filter createWithdrawThrottleFilter(final WithdrawThrottle withdrawThrottle) {
    return Filter.createMemCompFilter(WITHDRAW_THROTTLE_OFFSET, withdrawThrottle.write());
  }

  public static Filter createTotalQueuedAmountFilter(final QuoteLots totalQueuedAmount) {
    return Filter.createMemCompFilter(TOTAL_QUEUED_AMOUNT_OFFSET, totalQueuedAmount.write());
  }

  public static Filter createWithdrawalFeeFilter(final QuoteLots withdrawalFee) {
    return Filter.createMemCompFilter(WITHDRAWAL_FEE_OFFSET, withdrawalFee.write());
  }

  public static Filter createEnqueueingFeeFilter(final QuoteLots enqueueingFee) {
    return Filter.createMemCompFilter(ENQUEUEING_FEE_OFFSET, enqueueingFee.write());
  }

  public static WithdrawQueueHeader read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static WithdrawQueueHeader read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static WithdrawQueueHeader read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], WithdrawQueueHeader> FACTORY = WithdrawQueueHeader::read;

  public static WithdrawQueueHeader read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var discriminant = getInt64LE(_data, i);
    i += 8;
    final var sequenceNumber = SequenceNumber.read(_data, i);
    i += sequenceNumber.l();
    final var withdrawThrottle = WithdrawThrottle.read(_data, i);
    i += withdrawThrottle.l();
    final var totalQueuedAmount = QuoteLots.read(_data, i);
    i += totalQueuedAmount.l();
    final var withdrawalFee = QuoteLots.read(_data, i);
    i += withdrawalFee.l();
    final var enqueueingFee = QuoteLots.read(_data, i);
    i += enqueueingFee.l();
    final var reserved = new long[5];
    SerDeUtil.readArray(reserved, _data, i);
    return new WithdrawQueueHeader(_address,
                                   discriminator,
                                   discriminant,
                                   sequenceNumber,
                                   withdrawThrottle,
                                   totalQueuedAmount,
                                   withdrawalFee,
                                   enqueueingFee,
                                   reserved);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, discriminant);
    i += 8;
    i += sequenceNumber.write(_data, i);
    i += withdrawThrottle.write(_data, i);
    i += totalQueuedAmount.write(_data, i);
    i += withdrawalFee.write(_data, i);
    i += enqueueingFee.write(_data, i);
    i += SerDeUtil.writeArrayChecked(reserved, 5, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

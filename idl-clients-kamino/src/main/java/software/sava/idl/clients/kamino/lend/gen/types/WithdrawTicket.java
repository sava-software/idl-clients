package software.sava.idl.clients.kamino.lend.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// A finite-lifecycle account representing a specific depositor's place in the withdraw queue of
/// a specific reserve.
/// 
/// The lifecycle:
/// 1. The depositor holding ctokens wants to withdraw funds from the reserve, and finds out that
/// the required amount is not available (due to high utilization).
/// 2. The depositor calls the `enqueue_to_withdraw` handler.
/// 3. The handler transfers the depositor's ctokens to the reserve's internal "pending" vault.
/// 4. The handler initializes a new WithdrawTicket account, with the next available sequence
/// number.
/// 5. The depositor waits until his ticket is the next expected one for actual withdraw, and until
/// the reserve has enough liquidity.
/// 6. Anyone (the depositor or a bot) calls the permissionless `withdraw_queued_liquidity`
/// handler. If the ticket became invalid (e.g. destination account no longer exists), then the
/// depositor can call the `recover_invalid_ticket_collateral` handler instead.
/// 7. The handler transfers the liquidity amount according to the current exchange rate.
/// 8. The handler closes the ticket account.
///
/// @param sequenceNumber This ticket's place in the queue; the same as used for PDA derivation.
/// @param owner The funds' owner (the user who called the `enqueue_to_withdraw` handler).
/// @param reserve The reserve to withdraw from.
/// @param userDestinationLiquidityTa The token account to which the finally-available liquidity should be transferred (by the
///                                   `withdraw_queued_liquidity` handler).
/// @param queuedCollateralAmount The amount of collateral still waiting to be withdrawn using this ticket.
/// @param createdAtTimestamp The timestamp at which the queue was entered.
///                           
///                           This is currently only a piece of metadata, not used by the logic.
/// @param invalid Whether the ticket has been found to be invalid (e.g. the Self::user_destination_liquidity
///                has been repurposed) by the `withdraw_queued_liquidity` handler.
///                To be specific: valid = `0`, invalid = `1`.
///                
///                An invalid ticket cannot be made valid again, and can only be passed to the
///                `recover_invalid_ticket_collateral` handler.
/// @param progressCallbackType One of the valid ProgressCallbackType representations.
/// @param alignmentPadding Inner padding, for alignment.
/// @param progressCallbackCustomAccounts The (optional) accounts to be used by Self::progress_callback_types.
/// @param endPadding Trailing padding, for future developments.
public record WithdrawTicket(PublicKey _address,
                             Discriminator discriminator,
                             long sequenceNumber,
                             PublicKey owner,
                             PublicKey reserve,
                             PublicKey userDestinationLiquidityTa,
                             long queuedCollateralAmount,
                             long createdAtTimestamp,
                             int invalid,
                             int progressCallbackType,
                             byte[] alignmentPadding,
                             PublicKey[] progressCallbackCustomAccounts,
                             long[] endPadding) implements SerDe {

  public static final int BYTES = 520;
  public static final int ALIGNMENT_PADDING_LEN = 6;
  public static final int PROGRESS_CALLBACK_CUSTOM_ACCOUNTS_LEN = 2;
  public static final int END_PADDING_LEN = 40;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(237, 23, 164, 58, 53, 248, 240, 94);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int SEQUENCE_NUMBER_OFFSET = 8;
  public static final int OWNER_OFFSET = 16;
  public static final int RESERVE_OFFSET = 48;
  public static final int USER_DESTINATION_LIQUIDITY_TA_OFFSET = 80;
  public static final int QUEUED_COLLATERAL_AMOUNT_OFFSET = 112;
  public static final int CREATED_AT_TIMESTAMP_OFFSET = 120;
  public static final int INVALID_OFFSET = 128;
  public static final int PROGRESS_CALLBACK_TYPE_OFFSET = 129;
  public static final int ALIGNMENT_PADDING_OFFSET = 130;
  public static final int PROGRESS_CALLBACK_CUSTOM_ACCOUNTS_OFFSET = 136;
  public static final int END_PADDING_OFFSET = 200;

  public static Filter createSequenceNumberFilter(final long sequenceNumber) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, sequenceNumber);
    return Filter.createMemCompFilter(SEQUENCE_NUMBER_OFFSET, _data);
  }

  public static Filter createOwnerFilter(final PublicKey owner) {
    return Filter.createMemCompFilter(OWNER_OFFSET, owner);
  }

  public static Filter createReserveFilter(final PublicKey reserve) {
    return Filter.createMemCompFilter(RESERVE_OFFSET, reserve);
  }

  public static Filter createUserDestinationLiquidityTaFilter(final PublicKey userDestinationLiquidityTa) {
    return Filter.createMemCompFilter(USER_DESTINATION_LIQUIDITY_TA_OFFSET, userDestinationLiquidityTa);
  }

  public static Filter createQueuedCollateralAmountFilter(final long queuedCollateralAmount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, queuedCollateralAmount);
    return Filter.createMemCompFilter(QUEUED_COLLATERAL_AMOUNT_OFFSET, _data);
  }

  public static Filter createCreatedAtTimestampFilter(final long createdAtTimestamp) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, createdAtTimestamp);
    return Filter.createMemCompFilter(CREATED_AT_TIMESTAMP_OFFSET, _data);
  }

  public static Filter createInvalidFilter(final int invalid) {
    return Filter.createMemCompFilter(INVALID_OFFSET, new byte[]{(byte) invalid});
  }

  public static Filter createProgressCallbackTypeFilter(final int progressCallbackType) {
    return Filter.createMemCompFilter(PROGRESS_CALLBACK_TYPE_OFFSET, new byte[]{(byte) progressCallbackType});
  }

  public static WithdrawTicket read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static WithdrawTicket read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static WithdrawTicket read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], WithdrawTicket> FACTORY = WithdrawTicket::read;

  public static WithdrawTicket read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var sequenceNumber = getInt64LE(_data, i);
    i += 8;
    final var owner = readPubKey(_data, i);
    i += 32;
    final var reserve = readPubKey(_data, i);
    i += 32;
    final var userDestinationLiquidityTa = readPubKey(_data, i);
    i += 32;
    final var queuedCollateralAmount = getInt64LE(_data, i);
    i += 8;
    final var createdAtTimestamp = getInt64LE(_data, i);
    i += 8;
    final var invalid = _data[i] & 0xFF;
    ++i;
    final var progressCallbackType = _data[i] & 0xFF;
    ++i;
    final var alignmentPadding = new byte[6];
    i += SerDeUtil.readArray(alignmentPadding, _data, i);
    final var progressCallbackCustomAccounts = new PublicKey[2];
    i += SerDeUtil.readArray(progressCallbackCustomAccounts, _data, i);
    final var endPadding = new long[40];
    SerDeUtil.readArray(endPadding, _data, i);
    return new WithdrawTicket(_address,
                              discriminator,
                              sequenceNumber,
                              owner,
                              reserve,
                              userDestinationLiquidityTa,
                              queuedCollateralAmount,
                              createdAtTimestamp,
                              invalid,
                              progressCallbackType,
                              alignmentPadding,
                              progressCallbackCustomAccounts,
                              endPadding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, sequenceNumber);
    i += 8;
    owner.write(_data, i);
    i += 32;
    reserve.write(_data, i);
    i += 32;
    userDestinationLiquidityTa.write(_data, i);
    i += 32;
    putInt64LE(_data, i, queuedCollateralAmount);
    i += 8;
    putInt64LE(_data, i, createdAtTimestamp);
    i += 8;
    _data[i] = (byte) invalid;
    ++i;
    _data[i] = (byte) progressCallbackType;
    ++i;
    i += SerDeUtil.writeArrayChecked(alignmentPadding, 6, _data, i);
    i += SerDeUtil.writeArrayChecked(progressCallbackCustomAccounts, 2, _data, i);
    i += SerDeUtil.writeArrayChecked(endPadding, 40, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// A borrow order.
/// 
/// When the Obligation::borrow_order is populated (i.e. non-zeroed) on an Obligation, then the
/// permissionless "fill" operations may borrow liquidity to the owner according to this
/// specification.
///
/// @param debtLiquidityMint The asset to be borrowed.
///                          The reserves used for Obligation::borrows *must* all provide exactly this asset.
/// @param remainingDebtAmount The amount of debt that still needs to be filled, in lamports.
/// @param filledDebtDestination The token account owned by the Obligation::owner and holding Self::debt_liquidity_mint,
///                              where the filled funds should be transferred to.
/// @param minDebtTermSeconds The minimum allowed debt term that the obligation owner agrees to.
///                           The reserves used to fill this order *cannot* define their debt term *lower* than this.
///                           
///                           If zeroed, then only indefinite-term reserves may be used.
/// @param fillableUntilTimestamp The time until which the borrow order can still be filled.
/// @param placedAtTimestamp The time at which this order was placed.
///                          Currently, this is only a piece of metadata.
/// @param lastUpdatedAtTimestamp The time at which this order was most-recently updated (including: created).
///                               Currently, this is only a piece of metadata.
/// @param requestedDebtAmount The amount of debt that was originally requested when this order was most-recently updated.
///                            In other words: this field holds a value of Self::remaining_debt_amount captured at
///                            Self::last_updated_at_timestamp.
///                            Currently, this is only a piece of metadata.
/// @param maxBorrowRateBps The maximum borrow rate that the obligation owner agrees to.
///                         The reserves used for Obligation::borrows *cannot* define their maximum borrow rate
///                         *higher* than this.
/// @param active Whether the Self::remaining_debt_amount is non-zero.
///               
///               This field is *not* used by smart contract logic (which prefers to treat the above
///               Self::remaining_debt_amount-based definition as the single source of truth). However, it
///               is useful for off-chain bots (order-searchers) to efficiently list (i.e. `memcmp` filter)
///               just the obligations that have active borrow orders.
/// @param padding1 Alignment padding.
/// @param endPadding End padding.
public record BorrowOrder(PublicKey debtLiquidityMint,
                          long remainingDebtAmount,
                          PublicKey filledDebtDestination,
                          long minDebtTermSeconds,
                          long fillableUntilTimestamp,
                          long placedAtTimestamp,
                          long lastUpdatedAtTimestamp,
                          long requestedDebtAmount,
                          int maxBorrowRateBps,
                          int active,
                          byte[] padding1,
                          long[] endPadding) implements SerDe {

  public static final int BYTES = 160;
  public static final int PADDING_1_LEN = 3;
  public static final int END_PADDING_LEN = 5;

  public static final int DEBT_LIQUIDITY_MINT_OFFSET = 0;
  public static final int REMAINING_DEBT_AMOUNT_OFFSET = 32;
  public static final int FILLED_DEBT_DESTINATION_OFFSET = 40;
  public static final int MIN_DEBT_TERM_SECONDS_OFFSET = 72;
  public static final int FILLABLE_UNTIL_TIMESTAMP_OFFSET = 80;
  public static final int PLACED_AT_TIMESTAMP_OFFSET = 88;
  public static final int LAST_UPDATED_AT_TIMESTAMP_OFFSET = 96;
  public static final int REQUESTED_DEBT_AMOUNT_OFFSET = 104;
  public static final int MAX_BORROW_RATE_BPS_OFFSET = 112;
  public static final int ACTIVE_OFFSET = 116;
  public static final int PADDING_1_OFFSET = 117;
  public static final int END_PADDING_OFFSET = 120;

  public static BorrowOrder read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var debtLiquidityMint = readPubKey(_data, i);
    i += 32;
    final var remainingDebtAmount = getInt64LE(_data, i);
    i += 8;
    final var filledDebtDestination = readPubKey(_data, i);
    i += 32;
    final var minDebtTermSeconds = getInt64LE(_data, i);
    i += 8;
    final var fillableUntilTimestamp = getInt64LE(_data, i);
    i += 8;
    final var placedAtTimestamp = getInt64LE(_data, i);
    i += 8;
    final var lastUpdatedAtTimestamp = getInt64LE(_data, i);
    i += 8;
    final var requestedDebtAmount = getInt64LE(_data, i);
    i += 8;
    final var maxBorrowRateBps = getInt32LE(_data, i);
    i += 4;
    final var active = _data[i] & 0xFF;
    ++i;
    final var padding1 = new byte[3];
    i += SerDeUtil.readArray(padding1, _data, i);
    final var endPadding = new long[5];
    SerDeUtil.readArray(endPadding, _data, i);
    return new BorrowOrder(debtLiquidityMint,
                           remainingDebtAmount,
                           filledDebtDestination,
                           minDebtTermSeconds,
                           fillableUntilTimestamp,
                           placedAtTimestamp,
                           lastUpdatedAtTimestamp,
                           requestedDebtAmount,
                           maxBorrowRateBps,
                           active,
                           padding1,
                           endPadding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    debtLiquidityMint.write(_data, i);
    i += 32;
    putInt64LE(_data, i, remainingDebtAmount);
    i += 8;
    filledDebtDestination.write(_data, i);
    i += 32;
    putInt64LE(_data, i, minDebtTermSeconds);
    i += 8;
    putInt64LE(_data, i, fillableUntilTimestamp);
    i += 8;
    putInt64LE(_data, i, placedAtTimestamp);
    i += 8;
    putInt64LE(_data, i, lastUpdatedAtTimestamp);
    i += 8;
    putInt64LE(_data, i, requestedDebtAmount);
    i += 8;
    putInt32LE(_data, i, maxBorrowRateBps);
    i += 4;
    _data[i] = (byte) active;
    ++i;
    i += SerDeUtil.writeArrayChecked(padding1, 3, _data, i);
    i += SerDeUtil.writeArrayChecked(endPadding, 5, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

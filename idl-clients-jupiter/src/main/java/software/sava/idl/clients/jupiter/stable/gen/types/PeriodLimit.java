package software.sava.idl.clients.jupiter.stable.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param durationSeconds Window duration in seconds (0 = disabled)
/// @param maxMintAmount Maximum mint amount in this window
/// @param maxRedeemAmount Maximum redeem amount in this window
/// @param mintedAmount Amount minted in current window
/// @param redeemedAmount Amount redeemed in current window
/// @param windowStart Window start timestamp
public record PeriodLimit(long durationSeconds,
                          long maxMintAmount,
                          long maxRedeemAmount,
                          long mintedAmount,
                          long redeemedAmount,
                          long windowStart) implements SerDe {

  public static final int BYTES = 48;

  public static final int DURATION_SECONDS_OFFSET = 0;
  public static final int MAX_MINT_AMOUNT_OFFSET = 8;
  public static final int MAX_REDEEM_AMOUNT_OFFSET = 16;
  public static final int MINTED_AMOUNT_OFFSET = 24;
  public static final int REDEEMED_AMOUNT_OFFSET = 32;
  public static final int WINDOW_START_OFFSET = 40;

  public static PeriodLimit read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var durationSeconds = getInt64LE(_data, i);
    i += 8;
    final var maxMintAmount = getInt64LE(_data, i);
    i += 8;
    final var maxRedeemAmount = getInt64LE(_data, i);
    i += 8;
    final var mintedAmount = getInt64LE(_data, i);
    i += 8;
    final var redeemedAmount = getInt64LE(_data, i);
    i += 8;
    final var windowStart = getInt64LE(_data, i);
    return new PeriodLimit(durationSeconds,
                           maxMintAmount,
                           maxRedeemAmount,
                           mintedAmount,
                           redeemedAmount,
                           windowStart);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, durationSeconds);
    i += 8;
    putInt64LE(_data, i, maxMintAmount);
    i += 8;
    putInt64LE(_data, i, maxRedeemAmount);
    i += 8;
    putInt64LE(_data, i, mintedAmount);
    i += 8;
    putInt64LE(_data, i, redeemedAmount);
    i += 8;
    putInt64LE(_data, i, windowStart);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

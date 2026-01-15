package software.sava.idl.clients.jupiter.merkle_distributor.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param totalBonus total bonus
/// @param totalClaimedBonus total bonus
public record AirdropBonus(long totalBonus,
                           long vestingDuration,
                           long totalClaimedBonus) implements SerDe {

  public static final int BYTES = 24;

  public static final int TOTAL_BONUS_OFFSET = 0;
  public static final int VESTING_DURATION_OFFSET = 8;
  public static final int TOTAL_CLAIMED_BONUS_OFFSET = 16;

  public static AirdropBonus read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var totalBonus = getInt64LE(_data, i);
    i += 8;
    final var vestingDuration = getInt64LE(_data, i);
    i += 8;
    final var totalClaimedBonus = getInt64LE(_data, i);
    return new AirdropBonus(totalBonus, vestingDuration, totalClaimedBonus);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, totalBonus);
    i += 8;
    putInt64LE(_data, i, vestingDuration);
    i += 8;
    putInt64LE(_data, i, totalClaimedBonus);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

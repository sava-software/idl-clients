package software.sava.idl.clients.drift.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record RevenueShareEscrowFixed(PublicKey authority,
                                      PublicKey referrer,
                                      int referrerBoostExpireTs,
                                      int referrerRewardOffset,
                                      int refereeFeeNumeratorOffset,
                                      int referrerBoostNumerator,
                                      byte[] reservedFixed) implements SerDe {

  public static final int BYTES = 88;
  public static final int RESERVED_FIXED_LEN = 17;

  public static final int AUTHORITY_OFFSET = 0;
  public static final int REFERRER_OFFSET = 32;
  public static final int REFERRER_BOOST_EXPIRE_TS_OFFSET = 64;
  public static final int REFERRER_REWARD_OFFSET_OFFSET = 68;
  public static final int REFEREE_FEE_NUMERATOR_OFFSET_OFFSET = 69;
  public static final int REFERRER_BOOST_NUMERATOR_OFFSET = 70;
  public static final int RESERVED_FIXED_OFFSET = 71;

  public static RevenueShareEscrowFixed read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var referrer = readPubKey(_data, i);
    i += 32;
    final var referrerBoostExpireTs = getInt32LE(_data, i);
    i += 4;
    final var referrerRewardOffset = _data[i];
    ++i;
    final var refereeFeeNumeratorOffset = _data[i];
    ++i;
    final var referrerBoostNumerator = _data[i];
    ++i;
    final var reservedFixed = new byte[17];
    SerDeUtil.readArray(reservedFixed, _data, i);
    return new RevenueShareEscrowFixed(authority,
                                       referrer,
                                       referrerBoostExpireTs,
                                       referrerRewardOffset,
                                       refereeFeeNumeratorOffset,
                                       referrerBoostNumerator,
                                       reservedFixed);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    authority.write(_data, i);
    i += 32;
    referrer.write(_data, i);
    i += 32;
    putInt32LE(_data, i, referrerBoostExpireTs);
    i += 4;
    _data[i] = (byte) referrerRewardOffset;
    ++i;
    _data[i] = (byte) refereeFeeNumeratorOffset;
    ++i;
    _data[i] = (byte) referrerBoostNumerator;
    ++i;
    i += SerDeUtil.writeArrayChecked(reservedFixed, 17, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

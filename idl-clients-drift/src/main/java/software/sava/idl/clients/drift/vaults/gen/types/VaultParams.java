package software.sava.idl.clients.drift.vaults.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record VaultParams(byte[] name,
                          long redeemPeriod,
                          long maxTokens,
                          long managementFee,
                          long minDepositAmount,
                          int profitShare,
                          int hurdleRate,
                          int spotMarketIndex,
                          boolean permissioned) implements SerDe {

  public static final int BYTES = 75;
  public static final int NAME_LEN = 32;

  public static final int NAME_OFFSET = 0;
  public static final int REDEEM_PERIOD_OFFSET = 32;
  public static final int MAX_TOKENS_OFFSET = 40;
  public static final int MANAGEMENT_FEE_OFFSET = 48;
  public static final int MIN_DEPOSIT_AMOUNT_OFFSET = 56;
  public static final int PROFIT_SHARE_OFFSET = 64;
  public static final int HURDLE_RATE_OFFSET = 68;
  public static final int SPOT_MARKET_INDEX_OFFSET = 72;
  public static final int PERMISSIONED_OFFSET = 74;

  public static VaultParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var name = new byte[32];
    i += SerDeUtil.readArray(name, _data, i);
    final var redeemPeriod = getInt64LE(_data, i);
    i += 8;
    final var maxTokens = getInt64LE(_data, i);
    i += 8;
    final var managementFee = getInt64LE(_data, i);
    i += 8;
    final var minDepositAmount = getInt64LE(_data, i);
    i += 8;
    final var profitShare = getInt32LE(_data, i);
    i += 4;
    final var hurdleRate = getInt32LE(_data, i);
    i += 4;
    final var spotMarketIndex = getInt16LE(_data, i);
    i += 2;
    final var permissioned = _data[i] == 1;
    return new VaultParams(name,
                           redeemPeriod,
                           maxTokens,
                           managementFee,
                           minDepositAmount,
                           profitShare,
                           hurdleRate,
                           spotMarketIndex,
                           permissioned);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(name, 32, _data, i);
    putInt64LE(_data, i, redeemPeriod);
    i += 8;
    putInt64LE(_data, i, maxTokens);
    i += 8;
    putInt64LE(_data, i, managementFee);
    i += 8;
    putInt64LE(_data, i, minDepositAmount);
    i += 8;
    putInt32LE(_data, i, profitShare);
    i += 4;
    putInt32LE(_data, i, hurdleRate);
    i += 4;
    putInt16LE(_data, i, spotMarketIndex);
    i += 2;
    _data[i] = (byte) (permissioned ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.meteora.dlmm.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param amountX Amount of token X in the bin. This already excluded protocol fees.
/// @param amountY Amount of token Y in the bin. This already excluded protocol fees.
/// @param price Bin price
/// @param liquiditySupply Liquidities of the bin. This is the same as LP mint supply. q-number
/// @param rewardPerTokenStored reward_a_per_token_stored
/// @param feeAmountXPerTokenStored Swap fee amount of token X per liquidity deposited.
/// @param feeAmountYPerTokenStored Swap fee amount of token Y per liquidity deposited.
/// @param amountXIn Total token X swap into the bin. Only used for tracking purpose.
/// @param amountYIn Total token Y swap into he bin. Only used for tracking purpose.
public record Bin(long amountX,
                  long amountY,
                  BigInteger price,
                  BigInteger liquiditySupply,
                  BigInteger[] rewardPerTokenStored,
                  BigInteger feeAmountXPerTokenStored,
                  BigInteger feeAmountYPerTokenStored,
                  BigInteger amountXIn,
                  BigInteger amountYIn) implements SerDe {

  public static final int BYTES = 144;
  public static final int REWARD_PER_TOKEN_STORED_LEN = 2;

  public static Bin read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var amountX = getInt64LE(_data, i);
    i += 8;
    final var amountY = getInt64LE(_data, i);
    i += 8;
    final var price = getInt128LE(_data, i);
    i += 16;
    final var liquiditySupply = getInt128LE(_data, i);
    i += 16;
    final var rewardPerTokenStored = new BigInteger[2];
    i += SerDeUtil.read128Array(rewardPerTokenStored, _data, i);
    final var feeAmountXPerTokenStored = getInt128LE(_data, i);
    i += 16;
    final var feeAmountYPerTokenStored = getInt128LE(_data, i);
    i += 16;
    final var amountXIn = getInt128LE(_data, i);
    i += 16;
    final var amountYIn = getInt128LE(_data, i);
    return new Bin(amountX,
                   amountY,
                   price,
                   liquiditySupply,
                   rewardPerTokenStored,
                   feeAmountXPerTokenStored,
                   feeAmountYPerTokenStored,
                   amountXIn,
                   amountYIn);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, amountX);
    i += 8;
    putInt64LE(_data, i, amountY);
    i += 8;
    putInt128LE(_data, i, price);
    i += 16;
    putInt128LE(_data, i, liquiditySupply);
    i += 16;
    i += SerDeUtil.write128ArrayChecked(rewardPerTokenStored, 2, _data, i);
    putInt128LE(_data, i, feeAmountXPerTokenStored);
    i += 16;
    putInt128LE(_data, i, feeAmountYPerTokenStored);
    i += 16;
    putInt128LE(_data, i, amountXIn);
    i += 16;
    putInt128LE(_data, i, amountYIn);
    i += 16;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

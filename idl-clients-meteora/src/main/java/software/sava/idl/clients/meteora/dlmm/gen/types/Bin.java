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
/// @param functionBytes function bytes, could be used for liquidity mining or other functions in future
/// @param feeAmountXPerTokenStored Swap fee amount of token X per liquidity deposited.
/// @param feeAmountYPerTokenStored Swap fee amount of token Y per liquidity deposited.
/// @param padding0 _padding_0, previous amount_x_in, BE CAREFUL FOR TOMBSTONE WHEN REUSE !!
/// @param padding1 _padding_1, previous amount_y_in, BE CAREFUL FOR TOMBSTONE WHEN REUSE !!
public record Bin(long amountX,
                  long amountY,
                  BigInteger price,
                  BigInteger liquiditySupply,
                  BigInteger[] functionBytes,
                  BigInteger feeAmountXPerTokenStored,
                  BigInteger feeAmountYPerTokenStored,
                  BigInteger padding0,
                  BigInteger padding1) implements SerDe {

  public static final int BYTES = 144;
  public static final int FUNCTION_BYTES_LEN = 2;

  public static final int AMOUNT_X_OFFSET = 0;
  public static final int AMOUNT_Y_OFFSET = 8;
  public static final int PRICE_OFFSET = 16;
  public static final int LIQUIDITY_SUPPLY_OFFSET = 32;
  public static final int FUNCTION_BYTES_OFFSET = 48;
  public static final int FEE_AMOUNT_X_PER_TOKEN_STORED_OFFSET = 80;
  public static final int FEE_AMOUNT_Y_PER_TOKEN_STORED_OFFSET = 96;
  public static final int PADDING_0_OFFSET = 112;
  public static final int PADDING_1_OFFSET = 128;

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
    final var functionBytes = new BigInteger[2];
    i += SerDeUtil.read128Array(functionBytes, _data, i);
    final var feeAmountXPerTokenStored = getInt128LE(_data, i);
    i += 16;
    final var feeAmountYPerTokenStored = getInt128LE(_data, i);
    i += 16;
    final var padding0 = getInt128LE(_data, i);
    i += 16;
    final var padding1 = getInt128LE(_data, i);
    return new Bin(amountX,
                   amountY,
                   price,
                   liquiditySupply,
                   functionBytes,
                   feeAmountXPerTokenStored,
                   feeAmountYPerTokenStored,
                   padding0,
                   padding1);
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
    i += SerDeUtil.write128ArrayChecked(functionBytes, 2, _data, i);
    putInt128LE(_data, i, feeAmountXPerTokenStored);
    i += 16;
    putInt128LE(_data, i, feeAmountYPerTokenStored);
    i += 16;
    putInt128LE(_data, i, padding0);
    i += 16;
    putInt128LE(_data, i, padding1);
    i += 16;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

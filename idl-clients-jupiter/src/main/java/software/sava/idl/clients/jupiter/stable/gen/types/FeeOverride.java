package software.sava.idl.clients.jupiter.stable.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

public record FeeOverride(PublicKey mint,
                          int mintFeeRate,
                          int redeemFeeRate,
                          byte[] padding) implements SerDe {

  public static final int BYTES = 40;
  public static final int PADDING_LEN = 4;

  public static final int MINT_OFFSET = 0;
  public static final int MINT_FEE_RATE_OFFSET = 32;
  public static final int REDEEM_FEE_RATE_OFFSET = 34;
  public static final int PADDING_OFFSET = 36;

  public static FeeOverride read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var mint = readPubKey(_data, i);
    i += 32;
    final var mintFeeRate = getInt16LE(_data, i);
    i += 2;
    final var redeemFeeRate = getInt16LE(_data, i);
    i += 2;
    final var padding = new byte[4];
    SerDeUtil.readArray(padding, _data, i);
    return new FeeOverride(mint,
                           mintFeeRate,
                           redeemFeeRate,
                           padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    mint.write(_data, i);
    i += 32;
    putInt16LE(_data, i, mintFeeRate);
    i += 2;
    putInt16LE(_data, i, redeemFeeRate);
    i += 2;
    i += SerDeUtil.writeArrayChecked(padding, 4, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

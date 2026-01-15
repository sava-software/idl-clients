package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record SwapEventV2(PublicKey inputMint,
                          long inputAmount,
                          PublicKey outputMint,
                          long outputAmount) implements SerDe {

  public static final int BYTES = 80;

  public static final int INPUT_MINT_OFFSET = 0;
  public static final int INPUT_AMOUNT_OFFSET = 32;
  public static final int OUTPUT_MINT_OFFSET = 40;
  public static final int OUTPUT_AMOUNT_OFFSET = 72;

  public static SwapEventV2 read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var inputMint = readPubKey(_data, i);
    i += 32;
    final var inputAmount = getInt64LE(_data, i);
    i += 8;
    final var outputMint = readPubKey(_data, i);
    i += 32;
    final var outputAmount = getInt64LE(_data, i);
    return new SwapEventV2(inputMint,
                           inputAmount,
                           outputMint,
                           outputAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    inputMint.write(_data, i);
    i += 32;
    putInt64LE(_data, i, inputAmount);
    i += 8;
    outputMint.write(_data, i);
    i += 32;
    putInt64LE(_data, i, outputAmount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

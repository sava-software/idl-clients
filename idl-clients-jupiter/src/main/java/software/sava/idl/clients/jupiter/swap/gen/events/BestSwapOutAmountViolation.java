package software.sava.idl.clients.jupiter.swap.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record BestSwapOutAmountViolation(Discriminator discriminator, long expectedOutAmount, long outAmount) implements JupiterEvent {

  public static final int BYTES = 24;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(124, 66, 196, 51, 218, 173, 46, 93);

  public static final int EXPECTED_OUT_AMOUNT_OFFSET = 8;
  public static final int OUT_AMOUNT_OFFSET = 16;

  public static BestSwapOutAmountViolation read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var expectedOutAmount = getInt64LE(_data, i);
    i += 8;
    final var outAmount = getInt64LE(_data, i);
    return new BestSwapOutAmountViolation(discriminator, expectedOutAmount, outAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, expectedOutAmount);
    i += 8;
    putInt64LE(_data, i, outAmount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

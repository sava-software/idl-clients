package software.sava.idl.clients.jupiter.swap.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record SwapEvent(Discriminator discriminator,
                        PublicKey amm,
                        PublicKey inputMint,
                        long inputAmount,
                        PublicKey outputMint,
                        long outputAmount) implements JupiterEvent {

  public static final int BYTES = 120;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(64, 198, 205, 232, 38, 8, 113, 226);

  public static SwapEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var amm = readPubKey(_data, i);
    i += 32;
    final var inputMint = readPubKey(_data, i);
    i += 32;
    final var inputAmount = getInt64LE(_data, i);
    i += 8;
    final var outputMint = readPubKey(_data, i);
    i += 32;
    final var outputAmount = getInt64LE(_data, i);
    return new SwapEvent(discriminator,
                         amm,
                         inputMint,
                         inputAmount,
                         outputMint,
                         outputAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    amm.write(_data, i);
    i += 32;
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

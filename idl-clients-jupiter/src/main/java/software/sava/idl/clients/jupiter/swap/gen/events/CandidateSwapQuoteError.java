package software.sava.idl.clients.jupiter.swap.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record CandidateSwapQuoteError(Discriminator discriminator,
                                      long candidateIndex,
                                      long inAmount,
                                      long errorCode) implements JupiterEvent {

  public static final int BYTES = 32;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(248, 134, 37, 55, 145, 177, 114, 79);

  public static final int CANDIDATE_INDEX_OFFSET = 8;
  public static final int IN_AMOUNT_OFFSET = 16;
  public static final int ERROR_CODE_OFFSET = 24;

  public static CandidateSwapQuoteError read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var candidateIndex = getInt64LE(_data, i);
    i += 8;
    final var inAmount = getInt64LE(_data, i);
    i += 8;
    final var errorCode = getInt64LE(_data, i);
    return new CandidateSwapQuoteError(discriminator, candidateIndex, inAmount, errorCode);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, candidateIndex);
    i += 8;
    putInt64LE(_data, i, inAmount);
    i += 8;
    putInt64LE(_data, i, errorCode);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

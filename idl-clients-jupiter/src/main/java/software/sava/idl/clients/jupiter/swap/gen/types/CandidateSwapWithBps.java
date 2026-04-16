package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record CandidateSwapWithBps(CandidateSwap candidateSwap, int bps) implements SerDe {

  public static final int CANDIDATE_SWAP_OFFSET = 0;

  public static CandidateSwapWithBps read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var candidateSwap = CandidateSwap.read(_data, i);
    i += candidateSwap.l();
    final var bps = getInt32LE(_data, i);
    return new CandidateSwapWithBps(candidateSwap, bps);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += candidateSwap.write(_data, i);
    putInt32LE(_data, i, bps);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return candidateSwap.l() + 4;
  }
}

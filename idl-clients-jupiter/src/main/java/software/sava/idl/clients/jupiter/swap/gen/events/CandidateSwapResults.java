package software.sava.idl.clients.jupiter.swap.gen.events;

import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.jupiter.swap.gen.types.CandidateSwapResult;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record CandidateSwapResults(Discriminator discriminator, CandidateSwapResult[] results) implements JupiterEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(45, 9, 244, 30, 229, 52, 168, 123);

  public static CandidateSwapResults read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var results = Borsh.readVector(CandidateSwapResult.class, CandidateSwapResult::read, _data, i);
    return new CandidateSwapResults(discriminator, results);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += Borsh.writeVector(results, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + Borsh.lenVector(results);
  }
}

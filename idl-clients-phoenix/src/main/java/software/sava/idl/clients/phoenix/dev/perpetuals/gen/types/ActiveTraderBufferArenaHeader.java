package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Discriminator-only account definition for viewer compatibility.
/// The full active trader buffer arena layout is not modeled in this IDL.
public record ActiveTraderBufferArenaHeader(Discriminator discriminator) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(6, 249, 57, 137, 82, 121, 191, 107);

  public static ActiveTraderBufferArenaHeader read(final byte[] _data, final int _offset) {
    return _data == null || _data.length == 0
        ? null
        : new ActiveTraderBufferArenaHeader(createAnchorDiscriminator(_data, _offset));
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    discriminator.write(_data, _offset);
    return 8;
  }

  @Override
  public int l() {
    return 8;
  }
}

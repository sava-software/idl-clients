package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Discriminator-only account definition for viewer compatibility.
/// The full active trader buffer layout is not modeled in this IDL.
public record ActiveTraderBufferHeader(Discriminator discriminator) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(192, 255, 205, 165, 80, 154, 131, 5);

  public static ActiveTraderBufferHeader read(final byte[] _data, final int _offset) {
    return _data == null || _data.length == 0
        ? null
        : new ActiveTraderBufferHeader(createAnchorDiscriminator(_data, _offset));
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

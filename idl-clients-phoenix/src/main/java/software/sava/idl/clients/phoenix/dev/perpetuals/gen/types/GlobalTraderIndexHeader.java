package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Discriminator-only account definition for viewer compatibility.
/// The full global trader index layout is not modeled in this IDL.
public record GlobalTraderIndexHeader(Discriminator discriminator) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(145, 92, 169, 6, 5, 144, 1, 205);

  public static GlobalTraderIndexHeader read(final byte[] _data, final int _offset) {
    return _data == null || _data.length == 0
        ? null
        : new GlobalTraderIndexHeader(createAnchorDiscriminator(_data, _offset));
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

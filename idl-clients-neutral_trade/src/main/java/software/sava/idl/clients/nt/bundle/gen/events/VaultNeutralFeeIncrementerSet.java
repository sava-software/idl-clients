package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record VaultNeutralFeeIncrementerSet(Discriminator discriminator, PublicKey neutralFeeIncrementer) implements NtbundleEvent {

  public static final int BYTES = 40;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(7, 53, 169, 104, 118, 22, 134, 207);

  public static final int NEUTRAL_FEE_INCREMENTER_OFFSET = 8;

  public static VaultNeutralFeeIncrementerSet read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var neutralFeeIncrementer = readPubKey(_data, i);
    return new VaultNeutralFeeIncrementerSet(discriminator, neutralFeeIncrementer);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    neutralFeeIncrementer.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

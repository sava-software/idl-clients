package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record PausedDepositsWithdrawals(Discriminator discriminator, boolean paused, PublicKey bundleAccountKey) implements NtbundleEvent {

  public static final int BYTES = 41;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(131, 139, 248, 233, 29, 255, 148, 98);

  public static final int PAUSED_OFFSET = 8;
  public static final int BUNDLE_ACCOUNT_KEY_OFFSET = 9;

  public static PausedDepositsWithdrawals read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var paused = _data[i] == 1;
    ++i;
    final var bundleAccountKey = readPubKey(_data, i);
    return new PausedDepositsWithdrawals(discriminator, paused, bundleAccountKey);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    _data[i] = (byte) (paused ? 1 : 0);
    ++i;
    bundleAccountKey.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

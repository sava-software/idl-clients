package software.sava.idl.clients.jupiter.borrow.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogInitVaultConfig(Discriminator discriminator, PublicKey vaultConfig) implements VaultsEvent {

  public static final int BYTES = 40;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(194, 158, 35, 55, 179, 48, 174, 46);

  public static final int VAULT_CONFIG_OFFSET = 8;

  public static LogInitVaultConfig read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var vaultConfig = readPubKey(_data, i);
    return new LogInitVaultConfig(discriminator, vaultConfig);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    vaultConfig.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

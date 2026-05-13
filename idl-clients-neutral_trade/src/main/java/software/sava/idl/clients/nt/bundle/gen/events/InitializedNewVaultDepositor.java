package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record InitializedNewVaultDepositor(Discriminator discriminator,
                                           PublicKey vault,
                                           PublicKey vaultDepositor,
                                           PublicKey keeper) implements NtbundleEvent {

  public static final int BYTES = 104;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(16, 182, 169, 250, 42, 81, 202, 173);

  public static final int VAULT_OFFSET = 8;
  public static final int VAULT_DEPOSITOR_OFFSET = 40;
  public static final int KEEPER_OFFSET = 72;

  public static InitializedNewVaultDepositor read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var vault = readPubKey(_data, i);
    i += 32;
    final var vaultDepositor = readPubKey(_data, i);
    i += 32;
    final var keeper = readPubKey(_data, i);
    return new InitializedNewVaultDepositor(discriminator, vault, vaultDepositor, keeper);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    vault.write(_data, i);
    i += 32;
    vaultDepositor.write(_data, i);
    i += 32;
    keeper.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.loopscale.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record ExternalYieldAccounts(PublicKey externalYieldAccount, PublicKey externalYieldVault) implements SerDe {

  public static final int BYTES = 64;

  public static final int EXTERNAL_YIELD_ACCOUNT_OFFSET = 0;
  public static final int EXTERNAL_YIELD_VAULT_OFFSET = 32;

  public static ExternalYieldAccounts read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var externalYieldAccount = readPubKey(_data, i);
    i += 32;
    final var externalYieldVault = readPubKey(_data, i);
    return new ExternalYieldAccounts(externalYieldAccount, externalYieldVault);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    externalYieldAccount.write(_data, i);
    i += 32;
    externalYieldVault.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

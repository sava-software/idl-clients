package software.sava.idl.clients.marinade.stake_pool.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record ChangeAuthorityData(PublicKey admin,
                                  PublicKey validatorManager,
                                  PublicKey operationalSolAccount,
                                  PublicKey treasuryMsolAccount,
                                  PublicKey pauseAuthority) implements Borsh {

  public static ChangeAuthorityData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final PublicKey admin;
    if (_data[i] == 0) {
      admin = null;
      ++i;
    } else {
      ++i;
      admin = readPubKey(_data, i);
      i += 32;
    }
    final PublicKey validatorManager;
    if (_data[i] == 0) {
      validatorManager = null;
      ++i;
    } else {
      ++i;
      validatorManager = readPubKey(_data, i);
      i += 32;
    }
    final PublicKey operationalSolAccount;
    if (_data[i] == 0) {
      operationalSolAccount = null;
      ++i;
    } else {
      ++i;
      operationalSolAccount = readPubKey(_data, i);
      i += 32;
    }
    final PublicKey treasuryMsolAccount;
    if (_data[i] == 0) {
      treasuryMsolAccount = null;
      ++i;
    } else {
      ++i;
      treasuryMsolAccount = readPubKey(_data, i);
      i += 32;
    }
    final PublicKey pauseAuthority;
    if (_data[i] == 0) {
      pauseAuthority = null;
    } else {
      ++i;
      pauseAuthority = readPubKey(_data, i);
    }
    return new ChangeAuthorityData(admin,
                                   validatorManager,
                                   operationalSolAccount,
                                   treasuryMsolAccount,
                                   pauseAuthority);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeOptional(admin, _data, i);
    i += Borsh.writeOptional(validatorManager, _data, i);
    i += Borsh.writeOptional(operationalSolAccount, _data, i);
    i += Borsh.writeOptional(treasuryMsolAccount, _data, i);
    i += Borsh.writeOptional(pauseAuthority, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (admin == null ? 1 : (1 + 32))
         + (validatorManager == null ? 1 : (1 + 32))
         + (operationalSolAccount == null ? 1 : (1 + 32))
         + (treasuryMsolAccount == null ? 1 : (1 + 32))
         + (pauseAuthority == null ? 1 : (1 + 32));
  }
}

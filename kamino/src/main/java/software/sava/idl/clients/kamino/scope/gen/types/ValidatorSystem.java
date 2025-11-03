package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param totalActiveBalance sum of all active lamports staked
/// @param autoAddValidatorEnabled allow & auto-add validator when a user deposits a stake-account of a non-listed validator
public record ValidatorSystem(List validatorList,
                              PublicKey managerAuthority,
                              int totalValidatorScore,
                              long totalActiveBalance,
                              int autoAddValidatorEnabled) implements Borsh {

  public static final int BYTES = 121;

  public static ValidatorSystem read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var validatorList = List.read(_data, i);
    i += Borsh.len(validatorList);
    final var managerAuthority = readPubKey(_data, i);
    i += 32;
    final var totalValidatorScore = getInt32LE(_data, i);
    i += 4;
    final var totalActiveBalance = getInt64LE(_data, i);
    i += 8;
    final var autoAddValidatorEnabled = _data[i] & 0xFF;
    return new ValidatorSystem(validatorList,
                               managerAuthority,
                               totalValidatorScore,
                               totalActiveBalance,
                               autoAddValidatorEnabled);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.write(validatorList, _data, i);
    managerAuthority.write(_data, i);
    i += 32;
    putInt32LE(_data, i, totalValidatorScore);
    i += 4;
    putInt64LE(_data, i, totalActiveBalance);
    i += 8;
    _data[i] = (byte) autoAddValidatorEnabled;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.marinade.stake_pool.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param totalActiveBalance sum of all active lamports staked
/// @param autoAddValidatorEnabled DEPRECATED, no longer used
public record ValidatorSystem(List validatorList,
                              PublicKey managerAuthority,
                              int totalValidatorScore,
                              long totalActiveBalance,
                              int autoAddValidatorEnabled) implements SerDe {

  public static final int BYTES = 121;

  public static final int VALIDATOR_LIST_OFFSET = 0;
  public static final int MANAGER_AUTHORITY_OFFSET = 76;
  public static final int TOTAL_VALIDATOR_SCORE_OFFSET = 108;
  public static final int TOTAL_ACTIVE_BALANCE_OFFSET = 112;
  public static final int AUTO_ADD_VALIDATOR_ENABLED_OFFSET = 120;

  public static ValidatorSystem read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var validatorList = List.read(_data, i);
    i += validatorList.l();
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
    i += validatorList.write(_data, i);
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

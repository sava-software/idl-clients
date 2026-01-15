package software.sava.idl.clients.marinade.stake_pool.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record StakeRecord(PublicKey stakeAccount,
                          long lastUpdateDelegatedLamports,
                          long lastUpdateEpoch,
                          int isEmergencyUnstaking) implements SerDe {

  public static final int BYTES = 49;

  public static final int STAKE_ACCOUNT_OFFSET = 0;
  public static final int LAST_UPDATE_DELEGATED_LAMPORTS_OFFSET = 32;
  public static final int LAST_UPDATE_EPOCH_OFFSET = 40;
  public static final int IS_EMERGENCY_UNSTAKING_OFFSET = 48;

  public static StakeRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var stakeAccount = readPubKey(_data, i);
    i += 32;
    final var lastUpdateDelegatedLamports = getInt64LE(_data, i);
    i += 8;
    final var lastUpdateEpoch = getInt64LE(_data, i);
    i += 8;
    final var isEmergencyUnstaking = _data[i] & 0xFF;
    return new StakeRecord(stakeAccount,
                           lastUpdateDelegatedLamports,
                           lastUpdateEpoch,
                           isEmergencyUnstaking);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    stakeAccount.write(_data, i);
    i += 32;
    putInt64LE(_data, i, lastUpdateDelegatedLamports);
    i += 8;
    putInt64LE(_data, i, lastUpdateEpoch);
    i += 8;
    _data[i] = (byte) isEmergencyUnstaking;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

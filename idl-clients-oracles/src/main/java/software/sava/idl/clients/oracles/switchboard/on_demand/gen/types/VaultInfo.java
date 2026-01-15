package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record VaultInfo(PublicKey vaultKey, long lastRewardEpoch) implements SerDe {

  public static final int BYTES = 40;

  public static final int VAULT_KEY_OFFSET = 0;
  public static final int LAST_REWARD_EPOCH_OFFSET = 32;

  public static VaultInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var vaultKey = readPubKey(_data, i);
    i += 32;
    final var lastRewardEpoch = getInt64LE(_data, i);
    return new VaultInfo(vaultKey, lastRewardEpoch);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    vaultKey.write(_data, i);
    i += 32;
    putInt64LE(_data, i, lastRewardEpoch);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

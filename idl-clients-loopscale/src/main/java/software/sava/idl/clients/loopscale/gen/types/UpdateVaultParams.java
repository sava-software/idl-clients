package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record UpdateVaultParams(boolean depositsEnabled, boolean initVaultRewardsInfo) implements SerDe {

  public static final int BYTES = 2;

  public static final int DEPOSITS_ENABLED_OFFSET = 0;
  public static final int INIT_VAULT_REWARDS_INFO_OFFSET = 1;

  public static UpdateVaultParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var depositsEnabled = _data[i] == 1;
    ++i;
    final var initVaultRewardsInfo = _data[i] == 1;
    return new UpdateVaultParams(depositsEnabled, initVaultRewardsInfo);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) (depositsEnabled ? 1 : 0);
    ++i;
    _data[i] = (byte) (initVaultRewardsInfo ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

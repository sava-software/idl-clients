package software.sava.idl.clients.kamino.vaults.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record GlobalConfig(PublicKey _address,
                           Discriminator discriminator,
                           PublicKey globalAdmin,
                           PublicKey pendingAdmin,
                           long withdrawalPenaltyLamports,
                           long withdrawalPenaltyBps,
                           byte[] padding) implements Borsh {

  public static final int BYTES = 1032;
  public static final int PADDING_LEN = 944;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(149, 8, 156, 202, 160, 252, 176, 217);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int GLOBAL_ADMIN_OFFSET = 8;
  public static final int PENDING_ADMIN_OFFSET = 40;
  public static final int WITHDRAWAL_PENALTY_LAMPORTS_OFFSET = 72;
  public static final int WITHDRAWAL_PENALTY_BPS_OFFSET = 80;
  public static final int PADDING_OFFSET = 88;

  public static Filter createGlobalAdminFilter(final PublicKey globalAdmin) {
    return Filter.createMemCompFilter(GLOBAL_ADMIN_OFFSET, globalAdmin);
  }

  public static Filter createPendingAdminFilter(final PublicKey pendingAdmin) {
    return Filter.createMemCompFilter(PENDING_ADMIN_OFFSET, pendingAdmin);
  }

  public static Filter createWithdrawalPenaltyLamportsFilter(final long withdrawalPenaltyLamports) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, withdrawalPenaltyLamports);
    return Filter.createMemCompFilter(WITHDRAWAL_PENALTY_LAMPORTS_OFFSET, _data);
  }

  public static Filter createWithdrawalPenaltyBpsFilter(final long withdrawalPenaltyBps) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, withdrawalPenaltyBps);
    return Filter.createMemCompFilter(WITHDRAWAL_PENALTY_BPS_OFFSET, _data);
  }

  public static GlobalConfig read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static GlobalConfig read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static GlobalConfig read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], GlobalConfig> FACTORY = GlobalConfig::read;

  public static GlobalConfig read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var globalAdmin = readPubKey(_data, i);
    i += 32;
    final var pendingAdmin = readPubKey(_data, i);
    i += 32;
    final var withdrawalPenaltyLamports = getInt64LE(_data, i);
    i += 8;
    final var withdrawalPenaltyBps = getInt64LE(_data, i);
    i += 8;
    final var padding = new byte[944];
    Borsh.readArray(padding, _data, i);
    return new GlobalConfig(_address,
                            discriminator,
                            globalAdmin,
                            pendingAdmin,
                            withdrawalPenaltyLamports,
                            withdrawalPenaltyBps,
                            padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    globalAdmin.write(_data, i);
    i += 32;
    pendingAdmin.write(_data, i);
    i += 32;
    putInt64LE(_data, i, withdrawalPenaltyLamports);
    i += 8;
    putInt64LE(_data, i, withdrawalPenaltyBps);
    i += 8;
    i += Borsh.writeArrayChecked(padding, 944, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

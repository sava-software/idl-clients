package software.sava.idl.clients.kamino.lend.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// @param globalAdmin Global admin of the program
/// @param pendingAdmin Pending admin must sign a specific transaction to become the global admin
/// @param feeCollector Fee collector is the only allowed owner of token accounts receiving protocol fees
/// @param padding Padding to make the struct size 1024 bytes
public record GlobalConfig(PublicKey _address,
                           Discriminator discriminator,
                           PublicKey globalAdmin,
                           PublicKey pendingAdmin,
                           PublicKey feeCollector,
                           byte[] padding) implements SerDe {

  public static final int BYTES = 1032;
  public static final int PADDING_LEN = 928;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(149, 8, 156, 202, 160, 252, 176, 217);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int GLOBAL_ADMIN_OFFSET = 8;
  public static final int PENDING_ADMIN_OFFSET = 40;
  public static final int FEE_COLLECTOR_OFFSET = 72;
  public static final int PADDING_OFFSET = 104;

  public static Filter createGlobalAdminFilter(final PublicKey globalAdmin) {
    return Filter.createMemCompFilter(GLOBAL_ADMIN_OFFSET, globalAdmin);
  }

  public static Filter createPendingAdminFilter(final PublicKey pendingAdmin) {
    return Filter.createMemCompFilter(PENDING_ADMIN_OFFSET, pendingAdmin);
  }

  public static Filter createFeeCollectorFilter(final PublicKey feeCollector) {
    return Filter.createMemCompFilter(FEE_COLLECTOR_OFFSET, feeCollector);
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
    final var feeCollector = readPubKey(_data, i);
    i += 32;
    final var padding = new byte[928];
    SerDeUtil.readArray(padding, _data, i);
    return new GlobalConfig(_address,
                            discriminator,
                            globalAdmin,
                            pendingAdmin,
                            feeCollector,
                            padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    globalAdmin.write(_data, i);
    i += 32;
    pendingAdmin.write(_data, i);
    i += 32;
    feeCollector.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(padding, 928, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

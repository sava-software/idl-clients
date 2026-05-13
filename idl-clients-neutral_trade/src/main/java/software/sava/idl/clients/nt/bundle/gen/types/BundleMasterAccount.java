package software.sava.idl.clients.nt.bundle.gen.types;

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

public record BundleMasterAccount(PublicKey _address, Discriminator discriminator, PublicKey admin, byte[] padding) implements SerDe {

  public static final int BYTES = 136;
  public static final int PADDING_LEN = 96;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(252, 50, 148, 252, 178, 231, 4, 149);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int ADMIN_OFFSET = 8;
  public static final int PADDING_OFFSET = 40;

  public static Filter createAdminFilter(final PublicKey admin) {
    return Filter.createMemCompFilter(ADMIN_OFFSET, admin);
  }

  public static BundleMasterAccount read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static BundleMasterAccount read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static BundleMasterAccount read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], BundleMasterAccount> FACTORY = BundleMasterAccount::read;

  public static BundleMasterAccount read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var admin = readPubKey(_data, i);
    i += 32;
    final var padding = new byte[96];
    SerDeUtil.readArray(padding, _data, i);
    return new BundleMasterAccount(_address, discriminator, admin, padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    admin.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(padding, 96, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

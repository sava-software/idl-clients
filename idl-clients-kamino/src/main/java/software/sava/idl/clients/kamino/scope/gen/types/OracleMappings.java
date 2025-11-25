package software.sava.idl.clients.kamino.scope.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// @param refPrice reference price against which we check confidence within 5%
public record OracleMappings(PublicKey _address,
                             Discriminator discriminator,
                             PublicKey[] priceInfoAccounts,
                             byte[] priceTypes,
                             short[] twapSource,
                             byte[] twapEnabledd,
                             short[] refPrice,
                             byte[][] generic) implements Borsh {

  public static final int BYTES = 29704;
  public static final int PRICE_INFO_ACCOUNTS_LEN = 512;
  public static final int PRICE_TYPES_LEN = 512;
  public static final int TWAP_SOURCE_LEN = 512;
  public static final int TWAP_ENABLED_LEN = 512;
  public static final int REF_PRICE_LEN = 512;
  public static final int GENERIC_LEN = 512;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(40, 244, 110, 80, 255, 214, 243, 188);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int PRICE_INFO_ACCOUNTS_OFFSET = 8;
  public static final int PRICE_TYPES_OFFSET = 16392;
  public static final int TWAP_SOURCE_OFFSET = 16904;
  public static final int TWAP_ENABLED_OFFSET = 17928;
  public static final int REF_PRICE_OFFSET = 18440;
  public static final int GENERIC_OFFSET = 19464;

  public static OracleMappings read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static OracleMappings read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static OracleMappings read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], OracleMappings> FACTORY = OracleMappings::read;

  public static OracleMappings read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var priceInfoAccounts = new PublicKey[512];
    i += Borsh.readArray(priceInfoAccounts, _data, i);
    final var priceTypes = new byte[512];
    i += Borsh.readArray(priceTypes, _data, i);
    final var twapSource = new short[512];
    i += Borsh.readArray(twapSource, _data, i);
    final var twapEnabledd = new byte[512];
    i += Borsh.readArray(twapEnabledd, _data, i);
    final var refPrice = new short[512];
    i += Borsh.readArray(refPrice, _data, i);
    final var generic = new byte[512][20];
    Borsh.readArray(generic, _data, i);
    return new OracleMappings(_address,
                              discriminator,
                              priceInfoAccounts,
                              priceTypes,
                              twapSource,
                              twapEnabledd,
                              refPrice,
                              generic);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += Borsh.writeArrayChecked(priceInfoAccounts, 512, _data, i);
    i += Borsh.writeArrayChecked(priceTypes, 512, _data, i);
    i += Borsh.writeArrayChecked(twapSource, 512, _data, i);
    i += Borsh.writeArrayChecked(twapEnabledd, 512, _data, i);
    i += Borsh.writeArrayChecked(refPrice, 512, _data, i);
    i += Borsh.writeArrayChecked(generic, 512, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

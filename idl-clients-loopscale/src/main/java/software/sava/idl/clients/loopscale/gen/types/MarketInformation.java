package software.sava.idl.clients.loopscale.gen.types;

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

public record MarketInformation(PublicKey _address,
                                Discriminator discriminator,
                                PublicKey authority,
                                PublicKey delegate,
                                PublicKey principalMint,
                                AssetData[] assetData,
                                PrincipalCaps borrowCaps,
                                PrincipalCaps withdrawCaps,
                                PrincipalCaps supplyCaps,
                                int version) implements SerDe {

  public static final int BYTES = 25777;
  public static final int ASSET_DATA_LEN = 200;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(194, 154, 190, 99, 64, 111, 37, 205);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int AUTHORITY_OFFSET = 8;
  public static final int DELEGATE_OFFSET = 40;
  public static final int PRINCIPAL_MINT_OFFSET = 72;
  public static final int ASSET_DATA_OFFSET = 104;
  public static final int BORROW_CAPS_OFFSET = 25704;
  public static final int WITHDRAW_CAPS_OFFSET = 25728;
  public static final int SUPPLY_CAPS_OFFSET = 25752;
  public static final int VERSION_OFFSET = 25776;

  public static Filter createAuthorityFilter(final PublicKey authority) {
    return Filter.createMemCompFilter(AUTHORITY_OFFSET, authority);
  }

  public static Filter createDelegateFilter(final PublicKey delegate) {
    return Filter.createMemCompFilter(DELEGATE_OFFSET, delegate);
  }

  public static Filter createPrincipalMintFilter(final PublicKey principalMint) {
    return Filter.createMemCompFilter(PRINCIPAL_MINT_OFFSET, principalMint);
  }

  public static Filter createBorrowCapsFilter(final PrincipalCaps borrowCaps) {
    return Filter.createMemCompFilter(BORROW_CAPS_OFFSET, borrowCaps.write());
  }

  public static Filter createWithdrawCapsFilter(final PrincipalCaps withdrawCaps) {
    return Filter.createMemCompFilter(WITHDRAW_CAPS_OFFSET, withdrawCaps.write());
  }

  public static Filter createSupplyCapsFilter(final PrincipalCaps supplyCaps) {
    return Filter.createMemCompFilter(SUPPLY_CAPS_OFFSET, supplyCaps.write());
  }

  public static Filter createVersionFilter(final int version) {
    return Filter.createMemCompFilter(VERSION_OFFSET, new byte[]{(byte) version});
  }

  public static MarketInformation read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static MarketInformation read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static MarketInformation read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], MarketInformation> FACTORY = MarketInformation::read;

  public static MarketInformation read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var authority = readPubKey(_data, i);
    i += 32;
    final var delegate = readPubKey(_data, i);
    i += 32;
    final var principalMint = readPubKey(_data, i);
    i += 32;
    final var assetData = new AssetData[200];
    i += SerDeUtil.readArray(assetData, AssetData::read, _data, i);
    final var borrowCaps = PrincipalCaps.read(_data, i);
    i += borrowCaps.l();
    final var withdrawCaps = PrincipalCaps.read(_data, i);
    i += withdrawCaps.l();
    final var supplyCaps = PrincipalCaps.read(_data, i);
    i += supplyCaps.l();
    final var version = _data[i] & 0xFF;
    return new MarketInformation(_address,
                                 discriminator,
                                 authority,
                                 delegate,
                                 principalMint,
                                 assetData,
                                 borrowCaps,
                                 withdrawCaps,
                                 supplyCaps,
                                 version);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    authority.write(_data, i);
    i += 32;
    delegate.write(_data, i);
    i += 32;
    principalMint.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(assetData, 200, _data, i);
    i += borrowCaps.write(_data, i);
    i += withdrawCaps.write(_data, i);
    i += supplyCaps.write(_data, i);
    _data[i] = (byte) version;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.spl.token.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.rpc.Filter;
import software.sava.core.serial.Serializable;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param mintAuthority Optional authority used to mint new tokens. The mint authority may only
///                      be provided during mint creation. If no mint authority is present
///                      then the mint has a fixed supply and no further tokens may be minted.
/// @param supply Total supply of tokens.
/// @param decimals Number of base 10 digits to the right of the decimal place.
/// @param isInitialized Is `true` if this structure has been initialized.
/// @param freezeAuthority Optional authority to freeze token accounts.
public record Mint(PublicKey _address,
                   PublicKey mintAuthority,
                   long supply,
                   int decimals,
                   boolean isInitialized,
                   PublicKey freezeAuthority) implements Serializable {

  public static final int BYTES = 82;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final int MINT_AUTHORITY_OFFSET = 4;
  public static final int SUPPLY_OFFSET = 36;
  public static final int DECIMALS_OFFSET = 44;
  public static final int IS_INITIALIZED_OFFSET = 45;
  public static final int FREEZE_AUTHORITY_OFFSET = 50;

  public static Filter createMintAuthorityFilter(final PublicKey mintAuthority) {
    return Filter.createMemCompFilter(MINT_AUTHORITY_OFFSET, mintAuthority);
  }

  public static Filter createSupplyFilter(final long supply) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, supply);
    return Filter.createMemCompFilter(SUPPLY_OFFSET, _data);
  }

  public static Filter createDecimalsFilter(final int decimals) {
    return Filter.createMemCompFilter(DECIMALS_OFFSET, new byte[]{(byte) decimals});
  }

  public static Filter createIsInitializedFilter(final boolean isInitialized) {
    return Filter.createMemCompFilter(IS_INITIALIZED_OFFSET, new byte[]{(byte) (isInitialized ? 1 : 0)});
  }

  public static Filter createFreezeAuthorityFilter(final PublicKey freezeAuthority) {
    return Filter.createMemCompFilter(FREEZE_AUTHORITY_OFFSET, freezeAuthority);
  }

  public static Mint read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Mint read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Mint read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Mint> FACTORY = Mint::read;

  public static Mint read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }

    int i = _offset;
    final PublicKey mintAuthority;
    if (getInt32LE(_data, i) == 0) {
      mintAuthority = null;
      i += 32 + 4;
    } else {
      i += 4;
      mintAuthority = readPubKey(_data, i);
      i += 32;
    }
    final var supply = getInt64LE(_data, i);
    i += 8;
    final var decimals = _data[i] & 0xFF;
    ++i;
    final var isInitialized = _data[i] == 1;
    ++i;
    final PublicKey freezeAuthority;
    if (getInt32LE(_data, i) == 0) {
      freezeAuthority = null;
    } else {
      i += 4;
      freezeAuthority = readPubKey(_data, i);
    }
    return new Mint(_address,
                    mintAuthority,
                    supply,
                    decimals,
                    isInitialized,
                    freezeAuthority);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    if (mintAuthority != null) {
      putInt32LE(_data, i, 1);
      i += 4;
      mintAuthority.write(_data, i);
    } else {
      i += 4;
    }
    i += 32;
    putInt64LE(_data, i, supply);
    i += 8;
    _data[i] = (byte) decimals;
    ++i;
    _data[i] = (byte) (isInitialized ? 1 : 0);
    ++i;
    if (freezeAuthority != null) {
      putInt32LE(_data, i, 1);
      i += 4;
      freezeAuthority.write(_data, i);
    } else {
      i += 4;
    }
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

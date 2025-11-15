package software.sava.idl.clients.spl.token.gen.types;

import java.util.OptionalLong;
import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.encoding.ByteUtil;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param mint The mint associated with this account.
/// @param owner The owner of this account.
/// @param amount The amount of tokens this account holds.
/// @param delegate If `delegate` is `Some` then `delegated_amount` represents
///                 the amount authorized by the delegate.
/// @param state The account's state.
/// @param isNative If is_native.is_some, this is a native token, and the value logs the
///                 rent-exempt reserve. An Account is required to be rent-exempt, so
///                 the value is used by the Processor to ensure that wrapped SOL
///                 accounts do not drop below this threshold.
/// @param delegatedAmount The amount delegated.
/// @param closeAuthority Optional authority to close the account.
public record Token(PublicKey _address,
                    PublicKey mint,
                    PublicKey owner,
                    long amount,
                    PublicKey delegate,
                    AccountState state,
                    OptionalLong isNative,
                    long delegatedAmount,
                    PublicKey closeAuthority) implements Borsh {

  public static final int BYTES = 165;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final int MINT_OFFSET = 0;
  public static final int OWNER_OFFSET = 32;
  public static final int AMOUNT_OFFSET = 64;
  public static final int DELEGATE_OFFSET = 76;
  public static final int STATE_OFFSET = 108;
  public static final int IS_NATIVE_OFFSET = 113;
  public static final int DELEGATED_AMOUNT_OFFSET = 121;
  public static final int CLOSE_AUTHORITY_OFFSET = 133;

  public static Filter createMintFilter(final PublicKey mint) {
    return Filter.createMemCompFilter(MINT_OFFSET, mint);
  }

  public static Filter createOwnerFilter(final PublicKey owner) {
    return Filter.createMemCompFilter(OWNER_OFFSET, owner);
  }

  public static Filter createAmountFilter(final long amount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, amount);
    return Filter.createMemCompFilter(AMOUNT_OFFSET, _data);
  }

  public static Filter createDelegateFilter(final PublicKey delegate) {
    return Filter.createMemCompFilter(DELEGATE_OFFSET, delegate);
  }

  public static Filter createStateFilter(final AccountState state) {
    return Filter.createMemCompFilter(STATE_OFFSET, state.write());
  }

  public static Filter createIsNativeFilter(final long isNative) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, isNative);
    return Filter.createMemCompFilter(IS_NATIVE_OFFSET, _data);
  }

  public static Filter createDelegatedAmountFilter(final long delegatedAmount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, delegatedAmount);
    return Filter.createMemCompFilter(DELEGATED_AMOUNT_OFFSET, _data);
  }

  public static Filter createCloseAuthorityFilter(final PublicKey closeAuthority) {
    return Filter.createMemCompFilter(CLOSE_AUTHORITY_OFFSET, closeAuthority);
  }

  public static Token read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Token read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Token read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Token> FACTORY = Token::read;

  public static Token read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }

    int i = _offset;
    final var mint = readPubKey(_data, i);
    i += 32;
    final var owner = readPubKey(_data, i);
    i += 32;
    final var amount = getInt64LE(_data, i);
    i += 8;
    final PublicKey delegate;
    if (getInt32LE(_data, i) == 0) {
      delegate = null;
      i += 32 + 4;
    } else {
      i += 4;
      delegate = readPubKey(_data, i);
      i += 32;
    }
    final var state = AccountState.read(_data, i);
    i += Borsh.len(state);
    final OptionalLong isNative;
    if (getInt32LE(_data, i) == 0) {
      isNative = OptionalLong.empty();
      i += 8 + 4;
    } else {
      i += 4;
      isNative = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final var delegatedAmount = getInt64LE(_data, i);
    i += 8;
    final PublicKey closeAuthority;
    if (getInt32LE(_data, i) == 0) {
      closeAuthority = null;
    } else {
      i += 4;
      closeAuthority = readPubKey(_data, i);
    }
    return new Token(_address,
                     mint,
                     owner,
                     amount,
                     delegate,
                     state,
                     isNative,
                     delegatedAmount,
                     closeAuthority);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    mint.write(_data, i);
    i += 32;
    owner.write(_data, i);
    i += 32;
    putInt64LE(_data, i, amount);
    i += 8;
    if (delegate != null) {
      putInt32LE(_data, i, 1);
      i += 4;
      delegate.write(_data, i);
    } else {
      i += 4;
    }
    i += 32;
    i += Borsh.write(state, _data, i);
    if (isNative.isPresent()) {
      putInt32LE(_data, i, 1);
      i += 4;
      ByteUtil.putFloat64LE(_data, i, isNative.getAsLong());
    } else {
      i += 4;
    }
    i += 8;
    putInt64LE(_data, i, delegatedAmount);
    i += 8;
    if (closeAuthority != null) {
      putInt32LE(_data, i, 1);
      i += 4;
      closeAuthority.write(_data, i);
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

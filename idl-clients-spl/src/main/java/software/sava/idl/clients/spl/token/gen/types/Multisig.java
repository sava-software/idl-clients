package software.sava.idl.clients.spl.token.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

/// @param m Number of signers required.
/// @param n Number of valid signers.
/// @param isInitialized Is `true` if this structure has been initialized.
/// @param signers Signer public keys.
public record Multisig(PublicKey _address,
                       int m,
                       int n,
                       boolean isInitialized,
                       PublicKey[] signers) implements Borsh {

  public static final int BYTES = 355;
  public static final int SIGNERS_LEN = 11;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final int M_OFFSET = 0;
  public static final int N_OFFSET = 1;
  public static final int IS_INITIALIZED_OFFSET = 2;
  public static final int SIGNERS_OFFSET = 3;

  public static Filter createMFilter(final int m) {
    return Filter.createMemCompFilter(M_OFFSET, new byte[]{(byte) m});
  }

  public static Filter createNFilter(final int n) {
    return Filter.createMemCompFilter(N_OFFSET, new byte[]{(byte) n});
  }

  public static Filter createIsInitializedFilter(final boolean isInitialized) {
    return Filter.createMemCompFilter(IS_INITIALIZED_OFFSET, new byte[]{(byte) (isInitialized ? 1 : 0)});
  }

  public static Multisig read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Multisig read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Multisig read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Multisig> FACTORY = Multisig::read;

  public static Multisig read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }

    int i = _offset;
    final var m = _data[i] & 0xFF;
    ++i;
    final var n = _data[i] & 0xFF;
    ++i;
    final var isInitialized = _data[i] == 1;
    ++i;
    final var signers = new PublicKey[11];
    Borsh.readArray(signers, _data, i);
    return new Multisig(_address,
                        m,
                        n,
                        isInitialized,
                        signers);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) m;
    ++i;
    _data[i] = (byte) n;
    ++i;
    _data[i] = (byte) (isInitialized ? 1 : 0);
    ++i;
    i += Borsh.writeArrayChecked(signers, 11, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.kamino.lend.gen.types;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Reserve collateral
///
/// @param mintPubkey Reserve collateral mint address
/// @param mintTotalSupply Reserve collateral mint supply, used for exchange rate
/// @param supplyVault Reserve collateral supply address
public record ReserveCollateral(PublicKey mintPubkey,
                                long mintTotalSupply,
                                PublicKey supplyVault,
                                BigInteger[] padding1,
                                BigInteger[] padding2) implements Borsh {

  public static final int BYTES = 1096;
  public static final int PADDING_1_LEN = 32;
  public static final int PADDING_2_LEN = 32;

  public static ReserveCollateral read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var mintPubkey = readPubKey(_data, i);
    i += 32;
    final var mintTotalSupply = getInt64LE(_data, i);
    i += 8;
    final var supplyVault = readPubKey(_data, i);
    i += 32;
    final var padding1 = new BigInteger[32];
    i += Borsh.read128Array(padding1, _data, i);
    final var padding2 = new BigInteger[32];
    Borsh.read128Array(padding2, _data, i);
    return new ReserveCollateral(mintPubkey,
                                 mintTotalSupply,
                                 supplyVault,
                                 padding1,
                                 padding2);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    mintPubkey.write(_data, i);
    i += 32;
    putInt64LE(_data, i, mintTotalSupply);
    i += 8;
    supplyVault.write(_data, i);
    i += 32;
    i += Borsh.write128ArrayChecked(padding1, 32, _data, i);
    i += Borsh.write128ArrayChecked(padding2, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

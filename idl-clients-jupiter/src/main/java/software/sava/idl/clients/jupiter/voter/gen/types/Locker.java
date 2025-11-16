package software.sava.idl.clients.jupiter.voter.gen.types;

import java.math.BigInteger;

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

/// A group of Escrows.
///
/// @param base Base account used to generate signer seeds.
/// @param bump Bump seed.
/// @param tokenMint Mint of the token that must be locked in the Locker.
/// @param lockedSupply Total number of tokens locked in Escrows.
/// @param totalEscrow Total number of escrow
/// @param governor Governor associated with the Locker.
/// @param params Mutable parameters of how a Locker should behave.
/// @param buffers buffer for further use
public record Locker(PublicKey _address,
                     Discriminator discriminator,
                     PublicKey base,
                     int bump,
                     PublicKey tokenMint,
                     long lockedSupply,
                     long totalEscrow,
                     PublicKey governor,
                     LockerParams params,
                     BigInteger[] buffers) implements Borsh {

  public static final int BYTES = 658;
  public static final int BUFFERS_LEN = 32;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(74, 246, 6, 113, 249, 228, 75, 169);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int BASE_OFFSET = 8;
  public static final int BUMP_OFFSET = 40;
  public static final int TOKEN_MINT_OFFSET = 41;
  public static final int LOCKED_SUPPLY_OFFSET = 73;
  public static final int TOTAL_ESCROW_OFFSET = 81;
  public static final int GOVERNOR_OFFSET = 89;
  public static final int PARAMS_OFFSET = 121;
  public static final int BUFFERS_OFFSET = 146;

  public static Filter createBaseFilter(final PublicKey base) {
    return Filter.createMemCompFilter(BASE_OFFSET, base);
  }

  public static Filter createBumpFilter(final int bump) {
    return Filter.createMemCompFilter(BUMP_OFFSET, new byte[]{(byte) bump});
  }

  public static Filter createTokenMintFilter(final PublicKey tokenMint) {
    return Filter.createMemCompFilter(TOKEN_MINT_OFFSET, tokenMint);
  }

  public static Filter createLockedSupplyFilter(final long lockedSupply) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, lockedSupply);
    return Filter.createMemCompFilter(LOCKED_SUPPLY_OFFSET, _data);
  }

  public static Filter createTotalEscrowFilter(final long totalEscrow) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalEscrow);
    return Filter.createMemCompFilter(TOTAL_ESCROW_OFFSET, _data);
  }

  public static Filter createGovernorFilter(final PublicKey governor) {
    return Filter.createMemCompFilter(GOVERNOR_OFFSET, governor);
  }

  public static Filter createParamsFilter(final LockerParams params) {
    return Filter.createMemCompFilter(PARAMS_OFFSET, params.write());
  }

  public static Locker read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Locker read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Locker read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Locker> FACTORY = Locker::read;

  public static Locker read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var base = readPubKey(_data, i);
    i += 32;
    final var bump = _data[i] & 0xFF;
    ++i;
    final var tokenMint = readPubKey(_data, i);
    i += 32;
    final var lockedSupply = getInt64LE(_data, i);
    i += 8;
    final var totalEscrow = getInt64LE(_data, i);
    i += 8;
    final var governor = readPubKey(_data, i);
    i += 32;
    final var params = LockerParams.read(_data, i);
    i += params.l();
    final var buffers = new BigInteger[32];
    Borsh.read128Array(buffers, _data, i);
    return new Locker(_address,
                      discriminator,
                      base,
                      bump,
                      tokenMint,
                      lockedSupply,
                      totalEscrow,
                      governor,
                      params,
                      buffers);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    base.write(_data, i);
    i += 32;
    _data[i] = (byte) bump;
    ++i;
    tokenMint.write(_data, i);
    i += 32;
    putInt64LE(_data, i, lockedSupply);
    i += 8;
    putInt64LE(_data, i, totalEscrow);
    i += 8;
    governor.write(_data, i);
    i += 32;
    i += params.write(_data, i);
    i += Borsh.write128ArrayChecked(buffers, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

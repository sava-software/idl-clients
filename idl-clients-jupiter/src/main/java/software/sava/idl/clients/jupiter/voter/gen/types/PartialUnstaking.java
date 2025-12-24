package software.sava.idl.clients.jupiter.voter.gen.types;

import java.lang.String;

import java.math.BigInteger;

import java.util.Arrays;
import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Account to store infor for partial unstaking
///
/// @param escrow The Escrow pubkey.
/// @param amount Amount of this partial unstaking
/// @param expiration Timestamp when owner can withdraw the partial unstaking amount
/// @param buffers buffer for further use
/// @param memo Memo
public record PartialUnstaking(PublicKey _address,
                               Discriminator discriminator,
                               PublicKey escrow,
                               long amount,
                               long expiration,
                               BigInteger[] buffers,
                               String memo, byte[] _memo) implements SerDe {

  public static final int BUFFERS_LEN = 6;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(172, 146, 58, 213, 40, 250, 107, 63);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int ESCROW_OFFSET = 8;
  public static final int AMOUNT_OFFSET = 40;
  public static final int EXPIRATION_OFFSET = 48;
  public static final int BUFFERS_OFFSET = 56;
  public static final int MEMO_OFFSET = 152;

  public static Filter createEscrowFilter(final PublicKey escrow) {
    return Filter.createMemCompFilter(ESCROW_OFFSET, escrow);
  }

  public static Filter createAmountFilter(final long amount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, amount);
    return Filter.createMemCompFilter(AMOUNT_OFFSET, _data);
  }

  public static Filter createExpirationFilter(final long expiration) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, expiration);
    return Filter.createMemCompFilter(EXPIRATION_OFFSET, _data);
  }

  public static PartialUnstaking createRecord(final PublicKey _address,
                                              final Discriminator discriminator,
                                              final PublicKey escrow,
                                              final long amount,
                                              final long expiration,
                                              final BigInteger[] buffers,
                                              final String memo) {
    return new PartialUnstaking(_address,
                                discriminator,
                                escrow,
                                amount,
                                expiration,
                                buffers,
                                memo, memo == null ? null : memo.getBytes(UTF_8));
  }

  public static PartialUnstaking read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static PartialUnstaking read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static PartialUnstaking read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], PartialUnstaking> FACTORY = PartialUnstaking::read;

  public static PartialUnstaking read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var escrow = readPubKey(_data, i);
    i += 32;
    final var amount = getInt64LE(_data, i);
    i += 8;
    final var expiration = getInt64LE(_data, i);
    i += 8;
    final var buffers = new BigInteger[6];
    i += SerDeUtil.read128Array(buffers, _data, i);
    final int _memoLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _memo = Arrays.copyOfRange(_data, i, i + _memoLength);
    final var memo = new String(_memo, UTF_8);
    return new PartialUnstaking(_address,
                                discriminator,
                                escrow,
                                amount,
                                expiration,
                                buffers,
                                memo, memo == null ? null : memo.getBytes(UTF_8));
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    escrow.write(_data, i);
    i += 32;
    putInt64LE(_data, i, amount);
    i += 8;
    putInt64LE(_data, i, expiration);
    i += 8;
    i += SerDeUtil.write128ArrayChecked(buffers, 6, _data, i);
    i += SerDeUtil.writeVector(4, _memo, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32
         + 8
         + 8
         + SerDeUtil.len128Array(buffers)
         + _memo.length;
  }
}

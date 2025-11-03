package software.sava.idl.clients.drift.gen.events;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record FundingPaymentRecord(Discriminator discriminator,
                                   long ts,
                                   PublicKey userAuthority,
                                   PublicKey user,
                                   int marketIndex,
                                   long fundingPayment,
                                   long baseAssetAmount,
                                   long userLastCumulativeFunding,
                                   BigInteger ammCumulativeFundingLong,
                                   BigInteger ammCumulativeFundingShort) implements DriftEvent {

  public static final int BYTES = 138;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static FundingPaymentRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var userAuthority = readPubKey(_data, i);
    i += 32;
    final var user = readPubKey(_data, i);
    i += 32;
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var fundingPayment = getInt64LE(_data, i);
    i += 8;
    final var baseAssetAmount = getInt64LE(_data, i);
    i += 8;
    final var userLastCumulativeFunding = getInt64LE(_data, i);
    i += 8;
    final var ammCumulativeFundingLong = getInt128LE(_data, i);
    i += 16;
    final var ammCumulativeFundingShort = getInt128LE(_data, i);
    return new FundingPaymentRecord(discriminator,
                                    ts,
                                    userAuthority,
                                    user,
                                    marketIndex,
                                    fundingPayment,
                                    baseAssetAmount,
                                    userLastCumulativeFunding,
                                    ammCumulativeFundingLong,
                                    ammCumulativeFundingShort);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    userAuthority.write(_data, i);
    i += 32;
    user.write(_data, i);
    i += 32;
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt64LE(_data, i, fundingPayment);
    i += 8;
    putInt64LE(_data, i, baseAssetAmount);
    i += 8;
    putInt64LE(_data, i, userLastCumulativeFunding);
    i += 8;
    putInt128LE(_data, i, ammCumulativeFundingLong);
    i += 16;
    putInt128LE(_data, i, ammCumulativeFundingShort);
    i += 16;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.drift.gen.events;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.drift.gen.types.SettlePnlExplanation;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record SettlePnlRecord(Discriminator discriminator,
                              long ts,
                              PublicKey user,
                              int marketIndex,
                              BigInteger pnl,
                              long baseAssetAmount,
                              long quoteAssetAmountAfter,
                              long quoteEntryAmount,
                              long settlePrice,
                              SettlePnlExplanation explanation) implements DriftEvent {

  public static final int BYTES = 99;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static SettlePnlRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var user = readPubKey(_data, i);
    i += 32;
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var pnl = getInt128LE(_data, i);
    i += 16;
    final var baseAssetAmount = getInt64LE(_data, i);
    i += 8;
    final var quoteAssetAmountAfter = getInt64LE(_data, i);
    i += 8;
    final var quoteEntryAmount = getInt64LE(_data, i);
    i += 8;
    final var settlePrice = getInt64LE(_data, i);
    i += 8;
    final var explanation = SettlePnlExplanation.read(_data, i);
    return new SettlePnlRecord(discriminator,
                               ts,
                               user,
                               marketIndex,
                               pnl,
                               baseAssetAmount,
                               quoteAssetAmountAfter,
                               quoteEntryAmount,
                               settlePrice,
                               explanation);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    user.write(_data, i);
    i += 32;
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt128LE(_data, i, pnl);
    i += 16;
    putInt64LE(_data, i, baseAssetAmount);
    i += 8;
    putInt64LE(_data, i, quoteAssetAmountAfter);
    i += 8;
    putInt64LE(_data, i, quoteEntryAmount);
    i += 8;
    putInt64LE(_data, i, settlePrice);
    i += 8;
    i += explanation.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

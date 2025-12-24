package software.sava.idl.clients.drift.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.drift.gen.types.MarketType;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record RevenueShareSettleRecord(Discriminator discriminator,
                                       long ts,
                                       PublicKey builder,
                                       PublicKey referrer,
                                       long feeSettled,
                                       int marketIndex,
                                       MarketType marketType,
                                       int builderSubAccountId,
                                       long builderTotalReferrerRewards,
                                       long builderTotalBuilderRewards) implements DriftEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static RevenueShareSettleRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final PublicKey builder;
    if (_data[i] == 0) {
      builder = null;
      ++i;
    } else {
      ++i;
      builder = readPubKey(_data, i);
      i += 32;
    }
    final PublicKey referrer;
    if (_data[i] == 0) {
      referrer = null;
      ++i;
    } else {
      ++i;
      referrer = readPubKey(_data, i);
      i += 32;
    }
    final var feeSettled = getInt64LE(_data, i);
    i += 8;
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var marketType = MarketType.read(_data, i);
    i += marketType.l();
    final var builderSubAccountId = getInt16LE(_data, i);
    i += 2;
    final var builderTotalReferrerRewards = getInt64LE(_data, i);
    i += 8;
    final var builderTotalBuilderRewards = getInt64LE(_data, i);
    return new RevenueShareSettleRecord(discriminator,
                                        ts,
                                        builder,
                                        referrer,
                                        feeSettled,
                                        marketIndex,
                                        marketType,
                                        builderSubAccountId,
                                        builderTotalReferrerRewards,
                                        builderTotalBuilderRewards);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    i += SerDeUtil.writeOptional(1, builder, _data, i);
    i += SerDeUtil.writeOptional(1, referrer, _data, i);
    putInt64LE(_data, i, feeSettled);
    i += 8;
    putInt16LE(_data, i, marketIndex);
    i += 2;
    i += marketType.write(_data, i);
    putInt16LE(_data, i, builderSubAccountId);
    i += 2;
    putInt64LE(_data, i, builderTotalReferrerRewards);
    i += 8;
    putInt64LE(_data, i, builderTotalBuilderRewards);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 8
         + (builder == null ? 1 : (1 + 32))
         + (referrer == null ? 1 : (1 + 32))
         + 8
         + 2
         + marketType.l()
         + 2
         + 8
         + 8;
  }
}

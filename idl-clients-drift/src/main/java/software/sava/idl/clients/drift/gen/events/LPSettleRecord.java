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

public record LPSettleRecord(Discriminator discriminator,
                             long recordId,
                             long lastTs,
                             long lastSlot,
                             long ts,
                             long slot,
                             int perpMarketIndex,
                             long settleToLpAmount,
                             long perpAmmPnlDelta,
                             long perpAmmExFeeDelta,
                             BigInteger lpAum,
                             BigInteger lpPrice,
                             PublicKey lpPool) implements DriftEvent {

  public static final int BYTES = 138;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static LPSettleRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var recordId = getInt64LE(_data, i);
    i += 8;
    final var lastTs = getInt64LE(_data, i);
    i += 8;
    final var lastSlot = getInt64LE(_data, i);
    i += 8;
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var slot = getInt64LE(_data, i);
    i += 8;
    final var perpMarketIndex = getInt16LE(_data, i);
    i += 2;
    final var settleToLpAmount = getInt64LE(_data, i);
    i += 8;
    final var perpAmmPnlDelta = getInt64LE(_data, i);
    i += 8;
    final var perpAmmExFeeDelta = getInt64LE(_data, i);
    i += 8;
    final var lpAum = getInt128LE(_data, i);
    i += 16;
    final var lpPrice = getInt128LE(_data, i);
    i += 16;
    final var lpPool = readPubKey(_data, i);
    return new LPSettleRecord(discriminator,
                              recordId,
                              lastTs,
                              lastSlot,
                              ts,
                              slot,
                              perpMarketIndex,
                              settleToLpAmount,
                              perpAmmPnlDelta,
                              perpAmmExFeeDelta,
                              lpAum,
                              lpPrice,
                              lpPool);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, recordId);
    i += 8;
    putInt64LE(_data, i, lastTs);
    i += 8;
    putInt64LE(_data, i, lastSlot);
    i += 8;
    putInt64LE(_data, i, ts);
    i += 8;
    putInt64LE(_data, i, slot);
    i += 8;
    putInt16LE(_data, i, perpMarketIndex);
    i += 2;
    putInt64LE(_data, i, settleToLpAmount);
    i += 8;
    putInt64LE(_data, i, perpAmmPnlDelta);
    i += 8;
    putInt64LE(_data, i, perpAmmExFeeDelta);
    i += 8;
    putInt128LE(_data, i, lpAum);
    i += 16;
    putInt128LE(_data, i, lpPrice);
    i += 16;
    lpPool.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record Rebalancing(Discriminator discriminator,
                          PublicKey lbPair,
                          PublicKey position,
                          PublicKey owner,
                          int activeBinId,
                          long xWithdrawnAmount,
                          long xAddedAmount,
                          long yWithdrawnAmount,
                          long yAddedAmount,
                          long xFeeAmount,
                          long yFeeAmount,
                          int oldMinId,
                          int oldMaxId,
                          int newMinId,
                          int newMaxId,
                          long[] rewards) implements LbClmmEvent {

  public static final int BYTES = 188;
  public static final int REWARDS_LEN = 2;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(0, 109, 117, 179, 61, 91, 199, 200);

  public static Rebalancing read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var position = readPubKey(_data, i);
    i += 32;
    final var owner = readPubKey(_data, i);
    i += 32;
    final var activeBinId = getInt32LE(_data, i);
    i += 4;
    final var xWithdrawnAmount = getInt64LE(_data, i);
    i += 8;
    final var xAddedAmount = getInt64LE(_data, i);
    i += 8;
    final var yWithdrawnAmount = getInt64LE(_data, i);
    i += 8;
    final var yAddedAmount = getInt64LE(_data, i);
    i += 8;
    final var xFeeAmount = getInt64LE(_data, i);
    i += 8;
    final var yFeeAmount = getInt64LE(_data, i);
    i += 8;
    final var oldMinId = getInt32LE(_data, i);
    i += 4;
    final var oldMaxId = getInt32LE(_data, i);
    i += 4;
    final var newMinId = getInt32LE(_data, i);
    i += 4;
    final var newMaxId = getInt32LE(_data, i);
    i += 4;
    final var rewards = new long[2];
    SerDeUtil.readArray(rewards, _data, i);
    return new Rebalancing(discriminator,
                           lbPair,
                           position,
                           owner,
                           activeBinId,
                           xWithdrawnAmount,
                           xAddedAmount,
                           yWithdrawnAmount,
                           yAddedAmount,
                           xFeeAmount,
                           yFeeAmount,
                           oldMinId,
                           oldMaxId,
                           newMinId,
                           newMaxId,
                           rewards);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    position.write(_data, i);
    i += 32;
    owner.write(_data, i);
    i += 32;
    putInt32LE(_data, i, activeBinId);
    i += 4;
    putInt64LE(_data, i, xWithdrawnAmount);
    i += 8;
    putInt64LE(_data, i, xAddedAmount);
    i += 8;
    putInt64LE(_data, i, yWithdrawnAmount);
    i += 8;
    putInt64LE(_data, i, yAddedAmount);
    i += 8;
    putInt64LE(_data, i, xFeeAmount);
    i += 8;
    putInt64LE(_data, i, yFeeAmount);
    i += 8;
    putInt32LE(_data, i, oldMinId);
    i += 4;
    putInt32LE(_data, i, oldMaxId);
    i += 4;
    putInt32LE(_data, i, newMinId);
    i += 4;
    putInt32LE(_data, i, newMaxId);
    i += 4;
    i += SerDeUtil.writeArrayChecked(rewards, 2, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

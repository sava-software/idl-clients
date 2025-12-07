package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record WithdrawIneligibleReward(Discriminator discriminator,
                                       PublicKey lbPair,
                                       PublicKey rewardMint,
                                       long amount) implements LbClmmEvent {

  public static final int BYTES = 80;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(231, 189, 65, 149, 102, 215, 154, 244);

  public static WithdrawIneligibleReward read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var rewardMint = readPubKey(_data, i);
    i += 32;
    final var amount = getInt64LE(_data, i);
    return new WithdrawIneligibleReward(discriminator, lbPair, rewardMint, amount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    rewardMint.write(_data, i);
    i += 32;
    putInt64LE(_data, i, amount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

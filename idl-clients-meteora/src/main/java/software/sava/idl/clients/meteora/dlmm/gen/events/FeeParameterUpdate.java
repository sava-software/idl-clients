package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record FeeParameterUpdate(Discriminator discriminator,
                                 PublicKey lbPair,
                                 int protocolShare,
                                 int baseFactor) implements LbClmmEvent {

  public static final int BYTES = 44;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(48, 76, 241, 117, 144, 215, 242, 44);

  public static final int LB_PAIR_OFFSET = 8;
  public static final int PROTOCOL_SHARE_OFFSET = 40;
  public static final int BASE_FACTOR_OFFSET = 42;

  public static FeeParameterUpdate read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var protocolShare = getInt16LE(_data, i);
    i += 2;
    final var baseFactor = getInt16LE(_data, i);
    return new FeeParameterUpdate(discriminator, lbPair, protocolShare, baseFactor);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    putInt16LE(_data, i, protocolShare);
    i += 2;
    putInt16LE(_data, i, baseFactor);
    i += 2;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

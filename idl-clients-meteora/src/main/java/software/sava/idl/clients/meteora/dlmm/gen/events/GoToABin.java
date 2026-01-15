package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record GoToABin(Discriminator discriminator,
                       PublicKey lbPair,
                       int fromBinId,
                       int toBinId) implements LbClmmEvent {

  public static final int BYTES = 48;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(59, 138, 76, 68, 138, 131, 176, 67);

  public static final int LB_PAIR_OFFSET = 8;
  public static final int FROM_BIN_ID_OFFSET = 40;
  public static final int TO_BIN_ID_OFFSET = 44;

  public static GoToABin read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var fromBinId = getInt32LE(_data, i);
    i += 4;
    final var toBinId = getInt32LE(_data, i);
    return new GoToABin(discriminator, lbPair, fromBinId, toBinId);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    putInt32LE(_data, i, fromBinId);
    i += 4;
    putInt32LE(_data, i, toBinId);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

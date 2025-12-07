package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record AddLiquidity(Discriminator discriminator,
                           PublicKey lbPair,
                           PublicKey from,
                           PublicKey position,
                           long[] amounts,
                           int activeBinId) implements LbClmmEvent {

  public static final int BYTES = 124;
  public static final int AMOUNTS_LEN = 2;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(31, 94, 125, 90, 227, 52, 61, 186);

  public static AddLiquidity read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var from = readPubKey(_data, i);
    i += 32;
    final var position = readPubKey(_data, i);
    i += 32;
    final var amounts = new long[2];
    i += Borsh.readArray(amounts, _data, i);
    final var activeBinId = getInt32LE(_data, i);
    return new AddLiquidity(discriminator,
                            lbPair,
                            from,
                            position,
                            amounts,
                            activeBinId);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    from.write(_data, i);
    i += 32;
    position.write(_data, i);
    i += 32;
    i += Borsh.writeArrayChecked(amounts, 2, _data, i);
    putInt32LE(_data, i, activeBinId);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

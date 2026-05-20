package software.sava.idl.clients.jupiter.borrow.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogInitTickIdLiquidation(Discriminator discriminator, PublicKey tickIdLiquidation, int tick) implements VaultsEvent {

  public static final int BYTES = 44;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(172, 64, 170, 238, 39, 153, 185, 225);

  public static final int TICK_ID_LIQUIDATION_OFFSET = 8;
  public static final int TICK_OFFSET = 40;

  public static LogInitTickIdLiquidation read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var tickIdLiquidation = readPubKey(_data, i);
    i += 32;
    final var tick = getInt32LE(_data, i);
    return new LogInitTickIdLiquidation(discriminator, tickIdLiquidation, tick);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    tickIdLiquidation.write(_data, i);
    i += 32;
    putInt32LE(_data, i, tick);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

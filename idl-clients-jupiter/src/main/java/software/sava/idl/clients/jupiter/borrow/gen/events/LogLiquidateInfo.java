package software.sava.idl.clients.jupiter.borrow.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogLiquidateInfo(Discriminator discriminator,
                               int vaultId,
                               int startTick,
                               int endTick) implements VaultsEvent {

  public static final int BYTES = 18;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(169, 150, 46, 42, 178, 89, 98, 83);

  public static final int VAULT_ID_OFFSET = 8;
  public static final int START_TICK_OFFSET = 10;
  public static final int END_TICK_OFFSET = 14;

  public static LogLiquidateInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var vaultId = getInt16LE(_data, i);
    i += 2;
    final var startTick = getInt32LE(_data, i);
    i += 4;
    final var endTick = getInt32LE(_data, i);
    return new LogLiquidateInfo(discriminator, vaultId, startTick, endTick);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt16LE(_data, i, vaultId);
    i += 2;
    putInt32LE(_data, i, startTick);
    i += 4;
    putInt32LE(_data, i, endTick);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

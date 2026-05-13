package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getFloat32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putFloat32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record DelaysSet(Discriminator discriminator,
                        long withdrawalDelay,
                        long withdrawalTMin,
                        long withdrawalTMax,
                        float withdrawalCurve) implements NtbundleEvent {

  public static final int BYTES = 36;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(241, 35, 245, 242, 188, 53, 99, 208);

  public static final int WITHDRAWAL_DELAY_OFFSET = 8;
  public static final int WITHDRAWAL_T_MIN_OFFSET = 16;
  public static final int WITHDRAWAL_T_MAX_OFFSET = 24;
  public static final int WITHDRAWAL_CURVE_OFFSET = 32;

  public static DelaysSet read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var withdrawalDelay = getInt64LE(_data, i);
    i += 8;
    final var withdrawalTMin = getInt64LE(_data, i);
    i += 8;
    final var withdrawalTMax = getInt64LE(_data, i);
    i += 8;
    final var withdrawalCurve = getFloat32LE(_data, i);
    return new DelaysSet(discriminator,
                         withdrawalDelay,
                         withdrawalTMin,
                         withdrawalTMax,
                         withdrawalCurve);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, withdrawalDelay);
    i += 8;
    putInt64LE(_data, i, withdrawalTMin);
    i += 8;
    putInt64LE(_data, i, withdrawalTMax);
    i += 8;
    putFloat32LE(_data, i, withdrawalCurve);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

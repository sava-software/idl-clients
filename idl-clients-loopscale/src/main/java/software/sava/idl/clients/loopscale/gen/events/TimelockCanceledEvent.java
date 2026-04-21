package software.sava.idl.clients.loopscale.gen.events;

import java.lang.String;

import java.util.Arrays;

import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record TimelockCanceledEvent(Discriminator discriminator, String timelock, byte[] _timelock) implements LoopscaleEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(67, 213, 255, 46, 1, 27, 230, 3);

  public static final int TIMELOCK_OFFSET = 8;

  public static TimelockCanceledEvent createRecord(final Discriminator discriminator, final String timelock) {
    return new TimelockCanceledEvent(discriminator, timelock, timelock == null ? null : timelock.getBytes(UTF_8));
  }

  public static TimelockCanceledEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final int _timelockLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _timelock = Arrays.copyOfRange(_data, i, i + _timelockLength);
    final var timelock = new String(_timelock, UTF_8);
    return new TimelockCanceledEvent(discriminator, timelock, timelock == null ? null : timelock.getBytes(UTF_8));
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += SerDeUtil.writeVector(4, _timelock, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + _timelock.length;
  }
}

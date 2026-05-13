package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record OracleUpdateTimeLimitSet(Discriminator discriminator, long oracleUpdateTimeLimit) implements NtbundleEvent {

  public static final int BYTES = 16;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(196, 182, 75, 225, 80, 5, 111, 103);

  public static final int ORACLE_UPDATE_TIME_LIMIT_OFFSET = 8;

  public static OracleUpdateTimeLimitSet read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var oracleUpdateTimeLimit = getInt64LE(_data, i);
    return new OracleUpdateTimeLimitSet(discriminator, oracleUpdateTimeLimit);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, oracleUpdateTimeLimit);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

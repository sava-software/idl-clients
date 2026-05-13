package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record OracleMaxAgeSet(Discriminator discriminator, long oracleMaxAge) implements NtbundleEvent {

  public static final int BYTES = 16;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(51, 17, 80, 99, 8, 112, 166, 44);

  public static final int ORACLE_MAX_AGE_OFFSET = 8;

  public static OracleMaxAgeSet read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var oracleMaxAge = getInt64LE(_data, i);
    return new OracleMaxAgeSet(discriminator, oracleMaxAge);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, oracleMaxAge);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

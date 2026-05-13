package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record OracleUpdated(Discriminator discriminator, long averageExternalEquity) implements NtbundleEvent {

  public static final int BYTES = 16;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(138, 9, 51, 219, 228, 198, 11, 147);

  public static final int AVERAGE_EXTERNAL_EQUITY_OFFSET = 8;

  public static OracleUpdated read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var averageExternalEquity = getInt64LE(_data, i);
    return new OracleUpdated(discriminator, averageExternalEquity);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, averageExternalEquity);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

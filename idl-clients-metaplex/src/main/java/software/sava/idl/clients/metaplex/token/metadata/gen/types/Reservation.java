package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record Reservation(PublicKey address,
                          long spotsRemaining,
                          long totalSpots) implements SerDe {

  public static final int BYTES = 48;

  public static final int ADDRESS_OFFSET = 0;
  public static final int SPOTS_REMAINING_OFFSET = 32;
  public static final int TOTAL_SPOTS_OFFSET = 40;

  public static Reservation read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var address = readPubKey(_data, i);
    i += 32;
    final var spotsRemaining = getInt64LE(_data, i);
    i += 8;
    final var totalSpots = getInt64LE(_data, i);
    return new Reservation(address, spotsRemaining, totalSpots);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    address.write(_data, i);
    i += 32;
    putInt64LE(_data, i, spotsRemaining);
    i += 8;
    putInt64LE(_data, i, totalSpots);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

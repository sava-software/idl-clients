package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record OracleInitParams(long recentSlot,
                               PublicKey authority,
                               PublicKey queue,
                               byte[] secpAuthority) implements SerDe {

  public static final int RECENT_SLOT_OFFSET = 0;
  public static final int AUTHORITY_OFFSET = 8;
  public static final int QUEUE_OFFSET = 40;
  public static final int SECP_AUTHORITY_OFFSET = 73;

  public static OracleInitParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var recentSlot = getInt64LE(_data, i);
    i += 8;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var queue = readPubKey(_data, i);
    i += 32;
    final byte[] secpAuthority;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      secpAuthority = null;
    } else {
      ++i;
      secpAuthority = new byte[64];
      SerDeUtil.readArray(secpAuthority, _data, i);
    }
    return new OracleInitParams(recentSlot,
                                authority,
                                queue,
                                secpAuthority);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, recentSlot);
    i += 8;
    authority.write(_data, i);
    i += 32;
    queue.write(_data, i);
    i += 32;
    if (secpAuthority == null || secpAuthority.length == 0) {
      _data[i++] = 0;
    } else {
      _data[i++] = 1;
      i += SerDeUtil.writeArrayChecked(secpAuthority, 64, _data, i);
    }
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32 + 32 + (secpAuthority == null || secpAuthority.length == 0 ? 1 : (1 + SerDeUtil.lenArray(secpAuthority)));
  }
}

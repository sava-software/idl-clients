package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record OracleSetConfigsParams(PublicKey newAuthority, byte[] newSecpAuthority) implements SerDe {

  public static final int NEW_AUTHORITY_OFFSET = 1;

  public static OracleSetConfigsParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final PublicKey newAuthority;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      newAuthority = null;
      ++i;
    } else {
      ++i;
      newAuthority = readPubKey(_data, i);
      i += 32;
    }
    final byte[] newSecpAuthority;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      newSecpAuthority = null;
    } else {
      ++i;
      newSecpAuthority = new byte[64];
      SerDeUtil.readArray(newSecpAuthority, _data, i);
    }
    return new OracleSetConfigsParams(newAuthority, newSecpAuthority);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeOptional(1, newAuthority, _data, i);
    if (newSecpAuthority == null || newSecpAuthority.length == 0) {
      _data[i++] = 0;
    } else {
      _data[i++] = 1;
      i += SerDeUtil.writeArrayChecked(newSecpAuthority, 64, _data, i);
    }
    return i - _offset;
  }

  @Override
  public int l() {
    return (newAuthority == null ? 1 : (1 + 32)) + (newSecpAuthority == null || newSecpAuthority.length == 0 ? 1 : (1 + SerDeUtil.lenArray(newSecpAuthority)));
  }
}

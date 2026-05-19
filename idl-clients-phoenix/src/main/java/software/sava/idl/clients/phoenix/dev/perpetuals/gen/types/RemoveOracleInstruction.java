package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Borsh payload for removing oracle public keys from the perp asset map.
///
public record RemoveOracleInstruction(PublicKey[] oracleKeys) implements SerDe {

  public static final int ORACLE_KEYS_OFFSET = 0;

  public static RemoveOracleInstruction read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var oracleKeys = SerDeUtil.readPublicKeyVector(4, _data, _offset);
    return new RemoveOracleInstruction(oracleKeys);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeVector(4, oracleKeys, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return SerDeUtil.lenVector(4, oracleKeys);
  }
}

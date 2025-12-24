package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record OracleHeartbeatV2Params(byte[] gatewayUri) implements SerDe {

  public static OracleHeartbeatV2Params read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final byte[] gatewayUri;
    if (SerDeUtil.isAbsent(1, _data, _offset)) {
      gatewayUri = null;
    } else {
      gatewayUri = new byte[64];
      SerDeUtil.readArray(gatewayUri, _data, _offset + 1);
    }
    return new OracleHeartbeatV2Params(gatewayUri);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    if (gatewayUri == null || gatewayUri.length == 0) {
      _data[i++] = 0;
    } else {
      _data[i++] = 1;
      i += SerDeUtil.writeArrayChecked(gatewayUri, 64, _data, i);
    }
    return i - _offset;
  }

  @Override
  public int l() {
    return (gatewayUri == null || gatewayUri.length == 0 ? 1 : (1 + SerDeUtil.lenArray(gatewayUri)));
  }
}

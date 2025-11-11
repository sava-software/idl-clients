package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.core.borsh.Borsh;

public record OracleHeartbeatV2Params(byte[] gatewayUri) implements Borsh {

  public static OracleHeartbeatV2Params read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final byte[] gatewayUri;
    if (_data[_offset] == 0) {
      gatewayUri = null;
    } else {
      gatewayUri = new byte[64];
      Borsh.readArray(gatewayUri, _data, _offset + 1);
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
      i += Borsh.writeArrayChecked(gatewayUri, 64, _data, i);
    }
    return i - _offset;
  }

  @Override
  public int l() {
    return (gatewayUri == null || gatewayUri.length == 0 ? 1 : (1 + Borsh.lenArray(gatewayUri)));
  }
}

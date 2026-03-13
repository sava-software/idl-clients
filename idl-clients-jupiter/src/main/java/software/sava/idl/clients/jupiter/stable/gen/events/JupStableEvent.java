package software.sava.idl.clients.jupiter.stable.gen.events;

import software.sava.idl.clients.core.gen.SerDe;

public sealed interface JupStableEvent extends SerDe permits
    MintV0Event,
    RedeemV0Event {

  static JupStableEvent read(final byte[] _data, final int _offset) {
    if (MintV0Event.DISCRIMINATOR.equals(_data, _offset)) {
      return MintV0Event.read(_data, _offset);
    } else if (RedeemV0Event.DISCRIMINATOR.equals(_data, _offset)) {
      return RedeemV0Event.read(_data, _offset);
    } else {
      return null;
    }
  }

  static JupStableEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static JupStableEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static JupStableEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}
package software.sava.idl.clients.cctp.message_transmitter.v2.gen.events;

import software.sava.core.borsh.Borsh;

public sealed interface MessageTransmitterV2Event extends Borsh permits
    AttesterDisabled,
    AttesterEnabled,
    AttesterManagerUpdated,
    MaxMessageBodySizeUpdated,
    MessageReceived,
    OwnershipTransferStarted,
    OwnershipTransferred,
    Pause,
    PauserChanged,
    SignatureThresholdUpdated,
    Unpause {

  static MessageTransmitterV2Event read(final byte[] _data, final int _offset) {
    if (AttesterDisabled.DISCRIMINATOR.equals(_data, _offset)) {
      return AttesterDisabled.read(_data, _offset);
    } else if (AttesterEnabled.DISCRIMINATOR.equals(_data, _offset)) {
      return AttesterEnabled.read(_data, _offset);
    } else if (AttesterManagerUpdated.DISCRIMINATOR.equals(_data, _offset)) {
      return AttesterManagerUpdated.read(_data, _offset);
    } else if (MaxMessageBodySizeUpdated.DISCRIMINATOR.equals(_data, _offset)) {
      return MaxMessageBodySizeUpdated.read(_data, _offset);
    } else if (MessageReceived.DISCRIMINATOR.equals(_data, _offset)) {
      return MessageReceived.read(_data, _offset);
    } else if (OwnershipTransferStarted.DISCRIMINATOR.equals(_data, _offset)) {
      return OwnershipTransferStarted.read(_data, _offset);
    } else if (OwnershipTransferred.DISCRIMINATOR.equals(_data, _offset)) {
      return OwnershipTransferred.read(_data, _offset);
    } else if (Pause.DISCRIMINATOR.equals(_data, _offset)) {
      return Pause.read(_data, _offset);
    } else if (PauserChanged.DISCRIMINATOR.equals(_data, _offset)) {
      return PauserChanged.read(_data, _offset);
    } else if (SignatureThresholdUpdated.DISCRIMINATOR.equals(_data, _offset)) {
      return SignatureThresholdUpdated.read(_data, _offset);
    } else if (Unpause.DISCRIMINATOR.equals(_data, _offset)) {
      return Unpause.read(_data, _offset);
    } else {
      return null;
    }
  }

  static MessageTransmitterV2Event read(final byte[] _data) {
    return read(_data, 0);
  }

  static MessageTransmitterV2Event readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static MessageTransmitterV2Event readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}
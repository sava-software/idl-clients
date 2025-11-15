package software.sava.idl.clients.jupiter.swap.gen.events;

import software.sava.core.borsh.Borsh;

public sealed interface JupiterEvent extends Borsh permits
    FeeEvent,
    SwapEvent,
    SwapsEvent,
    CandidateSwapResults,
    BestSwapOutAmountViolation {

  static JupiterEvent read(final byte[] _data, final int _offset) {
    if (FeeEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return FeeEvent.read(_data, _offset);
    } else if (SwapEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return SwapEvent.read(_data, _offset);
    } else if (SwapsEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return SwapsEvent.read(_data, _offset);
    } else if (CandidateSwapResults.DISCRIMINATOR.equals(_data, _offset)) {
      return CandidateSwapResults.read(_data, _offset);
    } else if (BestSwapOutAmountViolation.DISCRIMINATOR.equals(_data, _offset)) {
      return BestSwapOutAmountViolation.read(_data, _offset);
    } else {
      return null;
    }
  }

  static JupiterEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static JupiterEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static JupiterEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}
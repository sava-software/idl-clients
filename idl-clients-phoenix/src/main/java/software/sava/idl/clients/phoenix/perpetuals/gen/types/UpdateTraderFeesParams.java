package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

/// Borsh payload for updating trader fee override multipliers.
///
public record UpdateTraderFeesParams(int makerFeeOverrideMultiplier, int takerFeeOverrideMultiplier) implements SerDe {

  public static final int BYTES = 2;

  public static final int MAKER_FEE_OVERRIDE_MULTIPLIER_OFFSET = 0;
  public static final int TAKER_FEE_OVERRIDE_MULTIPLIER_OFFSET = 1;

  public static UpdateTraderFeesParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var makerFeeOverrideMultiplier = _data[i];
    ++i;
    final var takerFeeOverrideMultiplier = _data[i];
    return new UpdateTraderFeesParams(makerFeeOverrideMultiplier, takerFeeOverrideMultiplier);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) makerFeeOverrideMultiplier;
    ++i;
    _data[i] = (byte) takerFeeOverrideMultiplier;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

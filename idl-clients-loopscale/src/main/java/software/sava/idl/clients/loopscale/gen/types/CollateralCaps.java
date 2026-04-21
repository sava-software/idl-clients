package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record CollateralCaps(PodU64CBPS maxAllocationPct, PodU64 currentAllocationAmount) implements SerDe {

  public static final int BYTES = 16;

  public static final int MAX_ALLOCATION_PCT_OFFSET = 0;
  public static final int CURRENT_ALLOCATION_AMOUNT_OFFSET = 8;

  public static CollateralCaps read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var maxAllocationPct = PodU64CBPS.read(_data, i);
    i += maxAllocationPct.l();
    final var currentAllocationAmount = PodU64.read(_data, i);
    return new CollateralCaps(maxAllocationPct, currentAllocationAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += maxAllocationPct.write(_data, i);
    i += currentAllocationAmount.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

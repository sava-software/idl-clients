package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record LeverageTiers(LeverageTier[] tiers) implements SerDe {

  public static final int BYTES = 96;
  public static final int TIERS_LEN = 4;

  public static final int TIERS_OFFSET = 0;

  public static LeverageTiers read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var tiers = new LeverageTier[4];
    SerDeUtil.readArray(tiers, LeverageTier::read, _data, _offset);
    return new LeverageTiers(tiers);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(tiers, 4, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

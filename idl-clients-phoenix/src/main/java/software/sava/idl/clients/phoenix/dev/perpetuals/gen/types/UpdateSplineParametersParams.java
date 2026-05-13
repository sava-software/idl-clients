package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record UpdateSplineParametersParams(TickRegionParams[] bidRegions, TickRegionParams[] askRegions) implements SerDe {

  public static final int BID_REGIONS_OFFSET = 0;

  public static UpdateSplineParametersParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var bidRegions = SerDeUtil.readVector(4, TickRegionParams.class, TickRegionParams::read, _data, i);
    i += SerDeUtil.lenVector(4, bidRegions);
    final var askRegions = SerDeUtil.readVector(4, TickRegionParams.class, TickRegionParams::read, _data, i);
    return new UpdateSplineParametersParams(bidRegions, askRegions);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeVector(4, bidRegions, _data, i);
    i += SerDeUtil.writeVector(4, askRegions, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return SerDeUtil.lenVector(4, bidRegions) + SerDeUtil.lenVector(4, askRegions);
  }
}

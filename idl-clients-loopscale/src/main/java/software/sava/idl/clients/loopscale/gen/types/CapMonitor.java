package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public record CapMonitor(PodU64 startTime1hr,
                         PodU64 startTime24hr,
                         PodU64 principal1hr,
                         PodU64 principal24hr) implements SerDe {

  public static final int BYTES = 32;

  public static final int START_TIME_1HR_OFFSET = 0;
  public static final int START_TIME_22HR_OFFSET = 8;
  public static final int PRINCIPAL_1HR_OFFSET = 16;
  public static final int PRINCIPAL_22HR_OFFSET = 24;

  public static CapMonitor read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var startTime1hr = PodU64.read(_data, i);
    i += startTime1hr.l();
    final var startTime24hr = PodU64.read(_data, i);
    i += startTime24hr.l();
    final var principal1hr = PodU64.read(_data, i);
    i += principal1hr.l();
    final var principal24hr = PodU64.read(_data, i);
    return new CapMonitor(startTime1hr,
                          startTime24hr,
                          principal1hr,
                          principal24hr);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += startTime1hr.write(_data, i);
    i += startTime24hr.write(_data, i);
    i += principal1hr.write(_data, i);
    i += principal24hr.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// # Ripcord Flag
/// - `0` (false): Feed's data provider is OK. Fund's data provider and accuracy is as expected.
/// - `1` (true): Feed's data provider is flagging a pause. Data provider detected outliers,
/// deviated thresholds, or operational issues. **DO NOT consume NAV data when ripcord=1.**
public enum ReportDataV9RipcordFlag implements RustEnum {

  Normal,
  Paused;

  public static ReportDataV9RipcordFlag read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ReportDataV9RipcordFlag.values(), _data, _offset);
  }
}
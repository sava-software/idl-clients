package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record UpdateCapsParams(ParsedPrincipalCaps borrowCaps,
                               ParsedPrincipalCaps withdrawCaps,
                               ParsedPrincipalCaps supplyCaps) implements SerDe {

  public static final int BORROW_CAPS_OFFSET = 1;

  public static UpdateCapsParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final ParsedPrincipalCaps borrowCaps;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      borrowCaps = null;
      ++i;
    } else {
      ++i;
      borrowCaps = ParsedPrincipalCaps.read(_data, i);
      i += borrowCaps.l();
    }
    final ParsedPrincipalCaps withdrawCaps;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      withdrawCaps = null;
      ++i;
    } else {
      ++i;
      withdrawCaps = ParsedPrincipalCaps.read(_data, i);
      i += withdrawCaps.l();
    }
    final ParsedPrincipalCaps supplyCaps;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      supplyCaps = null;
    } else {
      ++i;
      supplyCaps = ParsedPrincipalCaps.read(_data, i);
    }
    return new UpdateCapsParams(borrowCaps, withdrawCaps, supplyCaps);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeOptional(1, borrowCaps, _data, i);
    i += SerDeUtil.writeOptional(1, withdrawCaps, _data, i);
    i += SerDeUtil.writeOptional(1, supplyCaps, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (borrowCaps == null ? 1 : (1 + borrowCaps.l())) + (withdrawCaps == null ? 1 : (1 + withdrawCaps.l())) + (supplyCaps == null ? 1 : (1 + supplyCaps.l()));
  }
}

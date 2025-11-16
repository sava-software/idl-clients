package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public record OracleGuardRails(PriceDivergenceGuardRails priceDivergence, ValidityGuardRails validity) implements Borsh {

  public static final int BYTES = 48;

  public static OracleGuardRails read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var priceDivergence = PriceDivergenceGuardRails.read(_data, i);
    i += priceDivergence.l();
    final var validity = ValidityGuardRails.read(_data, i);
    return new OracleGuardRails(priceDivergence, validity);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += priceDivergence.write(_data, i);
    i += validity.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

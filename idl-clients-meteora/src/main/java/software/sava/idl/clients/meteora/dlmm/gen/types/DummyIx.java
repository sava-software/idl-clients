package software.sava.idl.clients.meteora.dlmm.gen.types;

import software.sava.core.borsh.Borsh;

public record DummyIx(PairStatus pairStatus,
                      PairType pairType,
                      ActivationType activationType,
                      TokenProgramFlags tokenProgramFlag,
                      ResizeSide resizeSide,
                      Rounding rounding) implements Borsh {

  public static final int BYTES = 6;

  public static DummyIx read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var pairStatus = PairStatus.read(_data, i);
    i += pairStatus.l();
    final var pairType = PairType.read(_data, i);
    i += pairType.l();
    final var activationType = ActivationType.read(_data, i);
    i += activationType.l();
    final var tokenProgramFlag = TokenProgramFlags.read(_data, i);
    i += tokenProgramFlag.l();
    final var resizeSide = ResizeSide.read(_data, i);
    i += resizeSide.l();
    final var rounding = Rounding.read(_data, i);
    return new DummyIx(pairStatus,
                       pairType,
                       activationType,
                       tokenProgramFlag,
                       resizeSide,
                       rounding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += pairStatus.write(_data, i);
    i += pairType.write(_data, i);
    i += activationType.write(_data, i);
    i += tokenProgramFlag.write(_data, i);
    i += resizeSide.write(_data, i);
    i += rounding.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}

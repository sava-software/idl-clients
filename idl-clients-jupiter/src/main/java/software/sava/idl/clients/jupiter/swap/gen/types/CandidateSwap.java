package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public sealed interface CandidateSwap extends RustEnum permits
  CandidateSwap.HumidiFi,
  CandidateSwap.TesseraV,
  CandidateSwap.HumidiFiV2,
  CandidateSwap.RaydiumV2,
  CandidateSwap.RaydiumClmm,
  CandidateSwap.Whirlpool,
  CandidateSwap.ZeroFi,
  CandidateSwap.BisonFiV2,
  CandidateSwap.GoonFiV2 {

  static CandidateSwap read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> HumidiFi.read(_data, i);
      case 1 -> TesseraV.read(_data, i);
      case 2 -> HumidiFiV2.read(_data, i);
      case 3 -> RaydiumV2.INSTANCE;
      case 4 -> RaydiumClmm.INSTANCE;
      case 5 -> Whirlpool.read(_data, i);
      case 6 -> ZeroFi.INSTANCE;
      case 7 -> BisonFiV2.read(_data, i);
      case 8 -> GoonFiV2.read(_data, i);
      default -> null;
    };
  }

  record HumidiFi(long swapId, boolean isBaseToQuote) implements CandidateSwap {

    public static final int BYTES = 9;

    public static final int SWAP_ID_OFFSET = 0;
    public static final int IS_BASE_TO_QUOTE_OFFSET = 8;

    public static HumidiFi read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var swapId = getInt64LE(_data, i);
      i += 8;
      final var isBaseToQuote = _data[i] == 1;
      return new HumidiFi(swapId, isBaseToQuote);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + writeOrdinal(_data, _offset);
      putInt64LE(_data, i, swapId);
      i += 8;
      _data[i] = (byte) (isBaseToQuote ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record TesseraV(Side val) implements SerDeEnum, CandidateSwap {

    public static TesseraV read(final byte[] _data, final int _offset) {
      return new TesseraV(Side.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record HumidiFiV2(long swapId, boolean isBaseToQuote) implements CandidateSwap {

    public static final int BYTES = 9;

    public static final int SWAP_ID_OFFSET = 0;
    public static final int IS_BASE_TO_QUOTE_OFFSET = 8;

    public static HumidiFiV2 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var swapId = getInt64LE(_data, i);
      i += 8;
      final var isBaseToQuote = _data[i] == 1;
      return new HumidiFiV2(swapId, isBaseToQuote);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + writeOrdinal(_data, _offset);
      putInt64LE(_data, i, swapId);
      i += 8;
      _data[i] = (byte) (isBaseToQuote ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }

  record RaydiumV2() implements EnumNone, CandidateSwap {

    public static final RaydiumV2 INSTANCE = new RaydiumV2();

    @Override
    public int ordinal() {
      return 3;
    }
  }

  record RaydiumClmm() implements EnumNone, CandidateSwap {

    public static final RaydiumClmm INSTANCE = new RaydiumClmm();

    @Override
    public int ordinal() {
      return 4;
    }
  }

  record Whirlpool(boolean val) implements EnumBool, CandidateSwap {

    public static final Whirlpool TRUE = new Whirlpool(true);
    public static final Whirlpool FALSE = new Whirlpool(false);

    public static Whirlpool read(final byte[] _data, int i) {
      return _data[i] == 1 ? Whirlpool.TRUE : Whirlpool.FALSE;
    }

    @Override
    public int ordinal() {
      return 5;
    }
  }

  record ZeroFi() implements EnumNone, CandidateSwap {

    public static final ZeroFi INSTANCE = new ZeroFi();

    @Override
    public int ordinal() {
      return 6;
    }
  }

  record BisonFiV2(boolean val) implements EnumBool, CandidateSwap {

    public static final BisonFiV2 TRUE = new BisonFiV2(true);
    public static final BisonFiV2 FALSE = new BisonFiV2(false);

    public static BisonFiV2 read(final byte[] _data, int i) {
      return _data[i] == 1 ? BisonFiV2.TRUE : BisonFiV2.FALSE;
    }

    @Override
    public int ordinal() {
      return 7;
    }
  }

  record GoonFiV2(boolean val) implements EnumBool, CandidateSwap {

    public static final GoonFiV2 TRUE = new GoonFiV2(true);
    public static final GoonFiV2 FALSE = new GoonFiV2(false);

    public static GoonFiV2 read(final byte[] _data, int i) {
      return _data[i] == 1 ? GoonFiV2.TRUE : GoonFiV2.FALSE;
    }

    @Override
    public int ordinal() {
      return 8;
    }
  }
}

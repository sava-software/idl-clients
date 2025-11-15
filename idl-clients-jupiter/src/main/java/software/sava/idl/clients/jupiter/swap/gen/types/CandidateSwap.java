package software.sava.idl.clients.jupiter.swap.gen.types;

import software.sava.core.borsh.RustEnum;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public sealed interface CandidateSwap extends RustEnum permits
  CandidateSwap.HumidiFi,
  CandidateSwap.TesseraV {

  static CandidateSwap read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> HumidiFi.read(_data, i);
      case 1 -> TesseraV.read(_data, i);
      default -> throw new IllegalStateException(java.lang.String.format(
          "Unexpected ordinal [%d] for enum [CandidateSwap]", ordinal
      ));
    };
  }

  record HumidiFi(long swapId, boolean isBaseToQuote) implements CandidateSwap {

    public static final int BYTES = 9;

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
      int i = writeOrdinal(_data, _offset);
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

  record TesseraV(Side val) implements BorshEnum, CandidateSwap {

    public static TesseraV read(final byte[] _data, final int _offset) {
      return new TesseraV(Side.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }
}

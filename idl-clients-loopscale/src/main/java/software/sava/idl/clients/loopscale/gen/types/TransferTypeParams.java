package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;

public sealed interface TransferTypeParams extends RustEnum permits
  TransferTypeParams.Liquidity,
  TransferTypeParams.TokenAmounts {

  static TransferTypeParams read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> Liquidity.read(_data, i);
      case 1 -> TokenAmounts.read(_data, i);
      default -> null;
    };
  }

  record Liquidity(LiquidityParams val) implements SerDeEnum, TransferTypeParams {

    public static Liquidity read(final byte[] _data, final int _offset) {
      return new Liquidity(LiquidityParams.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record TokenAmounts(TokenAmountsParams val) implements SerDeEnum, TransferTypeParams {

    public static TokenAmounts read(final byte[] _data, final int _offset) {
      return new TokenAmounts(TokenAmountsParams.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }
}

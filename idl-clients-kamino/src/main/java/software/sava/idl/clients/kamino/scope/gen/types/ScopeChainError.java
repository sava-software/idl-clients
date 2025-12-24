package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Errors that can be raised while creating or manipulating a scope chain
public enum ScopeChainError implements RustEnum {

  PriceChainTooLong,
  PriceChainConversionFailure,
  NoChainForToken,
  InvalidPricesInChain,
  MathOverflow,
  IntegerConversionOverflow;

  public static ScopeChainError read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ScopeChainError.values(), _data, _offset);
  }
}
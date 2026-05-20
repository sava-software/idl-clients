package software.sava.idl.clients.orca.quote;

import java.math.BigInteger;

/// Quote for `decreaseLiquidity` / `decreaseLiquidityV2`. `liquidityDelta` is
/// the u128 liquidity amount; `tokenEstA`/`tokenEstB` are the post-fee
/// estimates the caller will receive; `tokenMinA`/`tokenMinB` are the
/// slippage-tolerance lower bounds suitable for the `tokenMinA`/`tokenMinB`
/// instruction args. Mirrors Rust `DecreaseLiquidityQuote`.
public record DecreaseLiquidityQuote(BigInteger liquidityDelta,
                                     long tokenEstA,
                                     long tokenEstB,
                                     long tokenMinA,
                                     long tokenMinB) {

  public static final DecreaseLiquidityQuote ZERO =
      new DecreaseLiquidityQuote(BigInteger.ZERO, 0L, 0L, 0L, 0L);
}

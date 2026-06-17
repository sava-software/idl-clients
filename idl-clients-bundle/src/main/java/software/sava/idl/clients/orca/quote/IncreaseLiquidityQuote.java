package software.sava.idl.clients.orca.quote;

import java.math.BigInteger;

/// Quote for `increaseLiquidity` / `increaseLiquidityV2` /
/// `increaseLiquidityByTokenAmountsV2`. `liquidityDelta` is the u128 liquidity
/// amount; `tokenEstA`/`tokenEstB` are the post-transfer-fee estimates the
/// caller needs to send; `tokenMaxA`/`tokenMaxB` are the slippage-tolerance
/// upper bounds suitable for the `tokenMaxA`/`tokenMaxB` instruction args.
/// Mirrors Rust `IncreaseLiquidityQuote`.
public record IncreaseLiquidityQuote(BigInteger liquidityDelta,
                                     long tokenEstA,
                                     long tokenEstB,
                                     long tokenMaxA,
                                     long tokenMaxB) {

  public static final IncreaseLiquidityQuote ZERO =
      new IncreaseLiquidityQuote(BigInteger.ZERO, 0L, 0L, 0L, 0L);
}

package software.sava.idl.clients.jupiter.swap.rest.request;

import software.sava.core.accounts.PublicKey;

/// @deprecated Part of [JupiterSwapRequest]. On /order, use [JupiterSwapOrderRequest.Builder#jitoTipLamports(long)]
/// (at least 1000, observed 2026-10-04 and documented in `ultra/manual-mode.mdx` of Jupiter's docs) together with
/// [JupiterSwapOrderRequest.Builder#broadcastFeeType(BroadcastFeeType)]: a tip sent without one would not be added
/// (observed 2026-10-04), so [JupiterSwapOrderRequest] rejects it. [BroadcastFeeType#exactFee] keeps the tip exact, as
/// it was on /swap/v1/swap, and
/// [BroadcastFeeType#maxCap] lets Jupiter choose a smaller one. The one `broadcastFeeType` is shared with
/// [JupiterSwapOrderRequest.Builder#priorityFeeLamports(long)].
/// /build has no Jito-tip parameter: add your own System transfer to a Jito tip account, either at the end of a copy of
/// [software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapBuild#instructionsWithoutComputeBudget()], whose
/// list is unmodifiable, or as the `afterSwap` of
/// [its `Collection` overload][software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapBuild#
/// instructionsWithoutComputeBudget(java.util.Collection)], which places it right after the swap; a v0
/// transaction also puts its own SetComputeUnitLimit and a capped SetComputeUnitPrice in front, as the
/// [software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapBuild] documentation describes. Set
/// [JupiterSwapBuildRequest.Builder#forJitoBundle(boolean)] for bundle-compatible routing.
/// [JupiterSwapBuildRequest.Builder#tipAmount(long)] is Jupiter's /submit tip, paid to Jupiter's own receivers, not a
/// Jito tip.
@Deprecated
public record JitoTip(long lamports, PublicKey payer) {

  String toJson() {
    if (payer == null) {
      return String.format("""
          "jitoTipLamports": {"lamports": %d}
          """, lamports
      );
    } else {
      return String.format("""
          "jitoTipLamportsWithPayer": {"lamports": %d, "payer": "%s"}
          """, lamports, payer
      );
    }
  }
}

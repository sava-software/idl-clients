package software.sava.idl.clients.jupiter.swap.rest.request;

/// @deprecated Part of [JupiterSwapRequest]. On /order, use [JupiterSwapOrderRequest.Builder#priorityFeeLamports(long)]
/// with `maxLamports` and [JupiterSwapOrderRequest.Builder#broadcastFeeType(BroadcastFeeType)] set to
/// [BroadcastFeeType#maxCap], which keeps the fee a maximum over Jupiter's estimate. Sent without a `broadcastFeeType`
/// the fee would be ignored and Jupiter would charge its own estimate (observed 2026-10-04), so
/// [JupiterSwapOrderRequest] rejects a fee without one; [BroadcastFeeType#exactFee] charges it in full. On /build,
/// [JupiterSwapBuildRequest.Builder#computeUnitPricePercentile(ComputeUnitPriceLevel)] chooses
/// the estimate's percentile but is not a cap: cap
/// [software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapBuild#computeUnitPriceMicroLamports()] yourself.
/// `global` has no V2 equivalent.
@Deprecated
public record PriorityFeeLamports(String priorityLevel, long maxLamports, boolean global) {

  String toJson() {
    return String.format("""
            "priorityLevelWithMaxLamports": {"priorityLevel": "%s", "maxLamports": %d, "global": %b}""",
        priorityLevel, maxLamports, global
    );
  }
}

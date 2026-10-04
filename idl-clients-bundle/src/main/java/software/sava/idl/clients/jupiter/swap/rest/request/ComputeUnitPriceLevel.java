package software.sava.idl.clients.jupiter.swap.rest.request;

/// Named `computeUnitPricePercentile` levels of `/swap/v2/build`; the constants are the wire values.
public enum ComputeUnitPriceLevel {
  /// The 25th percentile of recent priority fees.
  medium,
  /// The 50th percentile, Jupiter's default (the 90th applies under `mode=fast` when no percentile is sent).
  high,
  /// The 75th percentile.
  veryHigh
}

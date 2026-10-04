package software.sava.idl.clients.jupiter.swap.rest.request;

/// `broadcastFeeType` of `/swap/v2/order`; the constants are the wire values.
public enum BroadcastFeeType {
  /// The priority fee or tip is a maximum.
  maxCap,
  /// The priority fee or tip is charged exactly.
  exactFee
}

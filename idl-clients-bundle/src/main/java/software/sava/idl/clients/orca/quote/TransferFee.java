package software.sava.idl.clients.orca.quote;

import software.sava.idl.clients.orca.OrcaUtil;

/// Token-2022 transfer-fee parameters used by the Whirlpool quote helpers to
/// pre/post-process token amounts. Mirrors the Rust SDK's `TransferFee`.
///
/// `feeBps` is in basis points (`0..=10_000`). `maxFee` caps the absolute fee
/// charged (as an unsigned `long`); pass `-1L` to disable the cap (matches
/// Rust's `u64::MAX`).
public record TransferFee(int feeBps, long maxFee) {

  /// A `TransferFee` with the given bps and no maximum cap.
  public static TransferFee of(final int feeBps) {
    return new TransferFee(feeBps, -1L);
  }

  /// A `TransferFee` with the given bps and an explicit (unsigned) maximum cap.
  public static TransferFee of(final int feeBps, final long maxFee) {
    return new TransferFee(feeBps, maxFee);
  }

  /// Default zero-fee instance (matches `TransferFee::default()` in Rust).
  public static final TransferFee NONE = new TransferFee(0, -1L);

  public TransferFee {
    if (feeBps < 0 || feeBps > OrcaUtil.BPS_DENOMINATOR) {
      throw new IllegalArgumentException("feeBps out of range: " + feeBps);
    }
  }
}

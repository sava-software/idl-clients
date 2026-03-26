package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// A discriminator of a user-configurable piece of Obligation.
/// 
/// Implementation note: due to TS-side codegen quirks (and a "convention" currently seen e.g.
/// within reserve and market update operations), this is not a true Rust enum. The new value of
/// a config item is provided in a separate handler argument (borsh-serialized), and its expected
/// type is defined by each discriminator here. Additionally, each update mode acts on a specific
/// ObligationConfigUpdateSubject (e.g. the auto-rollover of fixed-term borrows is configured on
/// a per-borrow basis), which is also specified by separate handler arguments.
public enum UpdateObligationConfigMode implements RustEnum {

  FixedTermRolloverEnabled,
  FixedTermRolloverMaxBorrowRateBps,
  FixedTermRolloverMinDebtTermSeconds,
  FixedTermRolloverOpenTermAllowed,
  MigrationToFixedEnabled;

  public static UpdateObligationConfigMode read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, UpdateObligationConfigMode.values(), _data, _offset);
  }
}
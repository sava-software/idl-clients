package software.sava.idl.clients.marginfi.v2;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.marginfi.v2.gen.types.BankConfig;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the one field 0.1.11 moved into `BankConfig`.
///
/// `_padding0` became `scope_entry_index` in place — same offset, same two bytes,
/// same record arity. That is the shape a decode bug hides in: padding reads as
/// zero, and so does the entry index on every bank that is not a Scope bank, so a
/// field read from the wrong offset still produces a plausible answer on almost
/// every account on chain. Only a known non-zero value at a pinned offset
/// separates the two.
final class MarginfiBankConfigTests {

  /// The entry index decodes from the bytes padding used to occupy, unsigned, and
  /// takes nothing from its neighbours — `oracleMaxAge` sits immediately before it
  /// and `oracleMaxConfidence` immediately after, so a one-field slip in either
  /// direction shows up here.
  @Test
  void theScopeEntryIndexDecodesFromTheBytesPaddingUsedToOccupy() {
    assertEquals(506, BankConfig.SCOPE_ENTRY_INDEX_OFFSET);
    assertEquals(504, BankConfig.ORACLE_MAX_AGE_OFFSET);
    assertEquals(508, BankConfig.ORACLE_MAX_CONFIDENCE_OFFSET);

    final byte[] data = new byte[BankConfig.BYTES];
    // 500, a legitimate entry: a Scope feed holds 512 of them
    data[BankConfig.SCOPE_ENTRY_INDEX_OFFSET] = (byte) 0xF4;
    data[BankConfig.SCOPE_ENTRY_INDEX_OFFSET + 1] = 0x01;

    final var config = BankConfig.read(data, 0);
    assertEquals(500, config.scopeEntryIndex());
    assertEquals(0, config.oracleMaxAge(), "the u16 before it");
    assertEquals(0L, config.oracleMaxConfidence(), "the u32 after it");

    // a bank that never saw a Scope oracle reads zero, which is why the offset
    // cannot be checked against real mainnet bytes alone
    assertEquals(0, BankConfig.read(new byte[BankConfig.BYTES], 0).scopeEntryIndex());
  }

  /// The field is a `u16`, so the top half of its range must not come back negative.
  @Test
  void theScopeEntryIndexIsUnsigned() {
    final byte[] data = new byte[BankConfig.BYTES];
    data[BankConfig.SCOPE_ENTRY_INDEX_OFFSET] = (byte) 0xFF;
    data[BankConfig.SCOPE_ENTRY_INDEX_OFFSET + 1] = (byte) 0xFF;

    assertEquals(65535, BankConfig.read(data, 0).scopeEntryIndex(), "u16, not a sign-extended short");
  }
}

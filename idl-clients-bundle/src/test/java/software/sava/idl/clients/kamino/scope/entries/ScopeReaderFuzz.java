package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleMappings;

/// Jazzer entry point for the Kamino Scope OracleMappings parser, which turns a ~29KB
/// on-chain account into a graph of price entries. The oracle-type, condition, and
/// ref-price indices all come from attacker-controlled bytes and drive enum lookups,
/// fixed-slice copies, and — the reason this target exists — recursive `entry()`
/// resolution across refPrice/source indices.
///
/// Malformed-input contract: garbage in -> [RuntimeException] out. Any [RuntimeException]
/// from `OracleMappings.read` or `parseEntries` is tolerated; Jazzer flags what the
/// contract forbids — hangs, memory exhaustion, and any non-[RuntimeException] throwable.
/// A cyclic refPrice/source index used to recurse into a [StackOverflowError] here (an
/// Error, not a RuntimeException) until the reader gained a cycle guard; this harness
/// keeps that regression covered and the whole recursion surface exercised.
///
/// Seeded from real OracleMappings account dumps under
/// src/test/resources/fuzz/scopeReader — the fixed 29704-byte layout is unreachable from
/// scratch, so a mutator only makes progress from a real seed.
///
/// Deliberately free of Jazzer imports so it compiles with the regular test sources.
///
/// Run with `./gradlew :idl-clients-bundle:fuzzScopeReader [-PmaxFuzzTime=<seconds>]`.
public final class ScopeReaderFuzz {

  public static void fuzzerTestOneInput(final byte[] data) {
    final OracleMappings mappings;
    try {
      mappings = OracleMappings.read(data, 0);
    } catch (final RuntimeException tolerated) {
      // truncated or otherwise malformed account bytes — rejection is in contract
      return;
    }
    if (mappings == null) {
      return;
    }

    final ScopeEntries entries;
    try {
      entries = ScopeReader.parseEntries(-1L, mappings);
    } catch (final RuntimeException tolerated) {
      // hostile indices/enum ordinals/slice lengths — a thrown RuntimeException is fine;
      // a StackOverflowError from cyclic recursion is NOT and would surface as a finding
      return;
    }

    // OracleMappings is a fixed 512-slot table, so a successful parse always yields one
    // entry per slot (null slots included) — never a truncated or over-long result.
    if (entries.numEntries() != OracleMappings.PRICE_INFO_ACCOUNTS_LEN) {
      throw new AssertionError("numEntries " + entries.numEntries()
          + " != " + OracleMappings.PRICE_INFO_ACCOUNTS_LEN);
    }

    // Parsing is a pure function of the bytes: a second pass over the same mappings must
    // produce the same slot count (and must not overflow the stack on the second walk).
    final var again = ScopeReader.parseEntries(-1L, mappings);
    if (again.numEntries() != entries.numEntries()) {
      throw new AssertionError("parseEntries is not deterministic");
    }
  }

  private ScopeReaderFuzz() {
  }
}

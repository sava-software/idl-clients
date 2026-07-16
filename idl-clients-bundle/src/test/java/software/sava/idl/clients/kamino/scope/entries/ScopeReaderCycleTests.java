package software.sava.idl.clients.kamino.scope.entries;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.gen.types.OracleMappings;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;
import software.sava.idl.clients.kamino.scope.gen.types.TwapEnabledBitmask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/// A cyclic refPrice/source index in hostile Scope account data must resolve the
/// back-reference to an absent entry rather than recurse into a StackOverflowError.
final class ScopeReaderCycleTests {

  private static final int SLOTS = OracleMappings.PRICE_INFO_ACCOUNTS_LEN;

  private static OracleMappings mappings(final int[] refPrice, final byte oracleType) {
    final var priceInfoAccounts = new PublicKey[SLOTS];
    final var priceTypes = new byte[SLOTS];
    final var tolerance = new int[SLOTS];
    final var twapBitmasks = new TwapEnabledBitmask[SLOTS];
    final var generic = new byte[SLOTS][20];
    for (int i = 0; i < SLOTS; ++i) {
      priceInfoAccounts[i] = PublicKey.NONE;
      priceTypes[i] = oracleType;
      tolerance[i] = 0xFFFF; // NO_REF_PRICE_TOLERANCE
      twapBitmasks[i] = new TwapEnabledBitmask(0);
    }
    return new OracleMappings(PublicKey.NONE, null, priceInfoAccounts, priceTypes, tolerance, twapBitmasks, refPrice, generic);
  }

  @Test
  void selfReferentialRefPriceResolvesToNull() {
    final int[] refPrice = new int[SLOTS];
    refPrice[0] = 0; // entry 0's ref price is itself
    final var entries = ScopeReader.parseEntries(-1L, mappings(refPrice, (byte) OracleType.PythPull.ordinal()));

    assertEquals(SLOTS, entries.numEntries());
    final var entry = entries.scopeEntry(0);
    assertNotNull(entry);
    // PythPull is a ReferencesEntry: its refPrice back-reference is broken to null
    assertNull(((PythPull) entry).refPrice());
  }

  @Test
  void twoNodeCycleResolvesGracefully() {
    final int[] refPrice = new int[SLOTS];
    refPrice[3] = 7;
    refPrice[7] = 3;
    final var entries = ScopeReader.parseEntries(-1L, mappings(refPrice, (byte) OracleType.PythPull.ordinal()));

    assertEquals(SLOTS, entries.numEntries());
    // whichever of the pair is resolved first breaks the cycle by returning null for the
    // other; both slots still produce an entry.
    assertNotNull(entries.scopeEntry(3));
    assertNotNull(entries.scopeEntry(7));
  }
}

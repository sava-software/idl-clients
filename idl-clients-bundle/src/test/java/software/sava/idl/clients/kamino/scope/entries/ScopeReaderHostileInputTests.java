package software.sava.idl.clients.kamino.scope.entries;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.kamino.scope.gen.types.OracleMappings;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;
import software.sava.idl.clients.kamino.scope.gen.types.TwapEnabledBitmask;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

/// Hostile Scope account bytes must parse in bounded time, not just without crashing, and what
/// the parse returns must compare, hash and print in bounded time too: the reader shares one entry
/// per slot between every slot that names it, so a graph walked path by path is exponential.
/// The FixedPrice exponent and the CappedFloored fan-out were fuzzScopeReader findings (the
/// exponent's minimized inputs live in the seed corpus at src/test/resources/fuzz/scopeReader;
/// the fan-out has no seed there). The repeated MultiplicationChain and the banded chain are
/// synthetic graphs that pin the comparison, hash and rendering bounds. The synthetic mappings
/// below pin each mechanism independently of the corpus bytes. The unmutated code parses
/// each in well under a second — the generous preemptive timeouts are not a
/// thin-margin harness, they separate microseconds from minutes. They are not what
/// catches a walk or a rendering gone path by path, though: each such test first
/// climbs a rung small enough that the exponential version still finishes at once,
/// and counts its work there, so the regression fails deterministically before the
/// full-size graph is touched, whatever a mutation run happens to execute first.
final class ScopeReaderHostileInputTests {

  private static final int SLOTS = OracleMappings.PRICE_INFO_ACCOUNTS_LEN;
  private static final int NO_REF = 0xFFFF;

  /// The walk's memo, counting its lookups: one each time the walk meets a pair of entries, and
  /// one more for each pair it records as equal, while `IdentityHashMap` keeps `Map`'s default
  /// `computeIfAbsent`, which looks the key up before adding it.
  private static final class CountingMemo extends IdentityHashMap<ScopeEntry, Set<ScopeEntry>> {

    private int lookups;

    @Override
    public Set<ScopeEntry> get(final Object key) {
      ++lookups;
      return super.get(key);
    }
  }

  private static OracleMappings mappings(final byte[] priceTypes, final byte[][] generic) {
    final var refPrice = new int[SLOTS];
    Arrays.fill(refPrice, NO_REF); // out of range -> resolves to no ref price entry
    return mappings(priceTypes, generic, refPrice);
  }

  /// As above, with `refPrice[i]` the slot slot `i`'s band references.
  private static OracleMappings mappings(final byte[] priceTypes, final byte[][] generic, final int[] refPrice) {
    final var priceInfoAccounts = new PublicKey[SLOTS];
    final var tolerance = new int[SLOTS];
    final var twapBitmasks = new TwapEnabledBitmask[SLOTS];
    for (int i = 0; i < SLOTS; ++i) {
      priceInfoAccounts[i] = PublicKey.NONE;
      tolerance[i] = NO_REF;
      twapBitmasks[i] = new TwapEnabledBitmask(0);
    }
    return new OracleMappings(PublicKey.NONE, null, priceInfoAccounts, priceTypes, tolerance, twapBitmasks, refPrice, generic);
  }

  /// A FixedPrice entry with a huge negative exponent used to take ~50s (and can
  /// exhaust memory): movePointLeft normalizes the negative resulting scale through
  /// setScale(0), materializing value*10^|exp| as a ~billion-digit BigInteger.
  /// scaleByPowerOfTen keeps the scale symbolic; the parse must stay instant.
  @Test
  void hostileFixedPriceExponentParsesInBoundedTime() {
    final var priceTypes = new byte[SLOTS];
    final var generic = new byte[SLOTS][20];
    priceTypes[0] = (byte) OracleType.FixedPrice.ordinal();
    ByteUtil.putInt64LE(generic[0], 0, 5L);              // Price.value
    ByteUtil.putInt64LE(generic[0], 8, -2_000_000_000L); // Price.exp
    for (int i = 1; i < SLOTS; ++i) {
      priceTypes[i] = (byte) OracleType.Unused.ordinal();
    }

    final var entries = assertTimeoutPreemptively(
        Duration.ofSeconds(10),
        () -> ScopeReader.parseEntries(-1L, mappings(priceTypes, generic)));

    assertEquals(SLOTS, entries.numEntries());
    final var fixedPrice = assertInstanceOf(FixedPrice.class, entries.scopeEntry(0));
    // 5 * 10^-(-2e9): the scale stays symbolic instead of expanding to 2e9 digits
    assertEquals(new BigDecimal("5E+2000000000"), fixedPrice.decimal());
  }

  /// A chain of CappedFloored entries whose source, cap, and floor all reference the
  /// next slot fans out 3 ways per level: without memoizing computed entries the walk
  /// is 3^511, with it each slot is computed once. Guards the entries[] write-through
  /// in ScopeReaderRecord.entry.
  ///
  /// The three-slot fixture runs first, and deliberately. What the memo actually
  /// promises is that a slot is resolved *once* and every reference to it observes
  /// that one entry — a property two references to a shared slot already settle, in
  /// microseconds, by object identity. Asserting it up front means a parse that lost
  /// the write-through fails on an assertion here rather than racing the 3^511 chain
  /// against whichever watchdog gets there first; the deep chain stays behind it as
  /// the complexity-class guard, which is a different claim and needs the depth.
  /// Identity is the assertion that carries this: the entries are records, so a
  /// re-resolved slot is `equals` to the one already published and only `!=` to it.
  @Test
  void forwardReferenceFanOutParsesInBoundedTime() {
    final var shallow = ScopeReader.parseEntries(-1L, cappedFlooredChain(3));
    final var shallowHead = assertInstanceOf(CappedFloored.class, shallow.scopeEntry(0));
    final var shallowNext = shallow.scopeEntry(1);
    assertSame(shallowNext, shallowHead.sourceEntry());
    assertSame(shallowNext, shallowHead.capEntry());
    assertSame(shallowNext, shallowHead.flooredEntry());

    final var entries = assertTimeoutPreemptively(
        Duration.ofSeconds(10),
        () -> ScopeReader.parseEntries(-1L, cappedFlooredChain(SLOTS)));

    assertEquals(SLOTS, entries.numEntries());
    final var head = assertInstanceOf(CappedFloored.class, entries.scopeEntry(0));
    // all three branches resolve to the same memoized instance of slot 1
    final var next = entries.scopeEntry(1);
    assertSame(next, head.sourceEntry());
    assertSame(next, head.capEntry());
    assertSame(next, head.flooredEntry());
  }

  /// Two parses of the 3^511-path CappedFloored chain are different instances slot for slot, so
  /// comparing them walks the graph rather than stopping at identity. Each pair of slots is
  /// compared once, a hash reads each input's slot and type, and a rendering spells each slot out
  /// once: path by path, any of the three would never finish. A chain that differs only at its
  /// far end must still compare unequal.
  @Test
  void aSharedGraphComparesHashesAndPrintsInBoundedTime() {
    // The rung: 7 composites over an Unused leaf. Two parses meet each composite and the leaf
    // first through a source (8 lookups), and every cap and floor is the pair just found equal
    // (14), so 22 lookups, plus 8 as computeIfAbsent looks each first meeting up again. Path by
    // path the walk makes (3^8 - 1) / 2 = 3,280.
    final var rung = ScopeReader.parseEntries(-1L, cappedFlooredChain(8)).scopeEntry(0);
    final var memo = new CountingMemo();
    assertTrue(EntryGraph.equal(rung, ScopeReader.parseEntries(-1L, cappedFlooredChain(8)).scopeEntry(0), memo));
    assertTrue(memo.lookups <= 30, () -> memo.lookups + " memo lookups");
    // Each composite is spelled out once: 80 characters of its own, then its source in full and
    // its cap and floor as CappedFloored#n, 15 each. The last one prints the leaf, Unused[index=7],
    // three times. That is 6 * 110 + 125 = 785 characters. Path by path it renders
    // (3^7 - 1) / 2 = 1,093 composites at 80 characters or more each.
    assertTrue(rung.toString().length() < 2_000, () -> rung.toString().length() + " characters");

    final var head = ScopeReader.parseEntries(-1L, cappedFlooredChain(SLOTS)).scopeEntry(0);
    final var twin = ScopeReader.parseEntries(-1L, cappedFlooredChain(SLOTS)).scopeEntry(0);
    final var farEndDiffers = ScopeReader.parseEntries(-1L, cappedFlooredChain(SLOTS, 60L)).scopeEntry(0);

    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
      assertEquals(head, twin);
      assertEquals(head.hashCode(), twin.hashCode());
      assertNotEquals(head, farEndDiffers);
      // each of the 511 composites once, ~100 characters apiece, rather than 3^511 copies
      assertTrue(head.toString().length() < 200_000, () -> head.toString().length() + " characters");
    });
  }

  /// A MultiplicationChain may list one slot several times, which the program's source check
  /// allows for any slot but 0: twenty slots each listing the next six times, ending in a
  /// FixedPrice, have 6^20 paths, and two parses must still compare, hash and print at once.
  @Test
  void aMultiplicationChainListingASlotRepeatedlyStaysLinear() {
    // The rung: 4 composites over the FixedPrice leaf. Two parses meet each composite and the
    // leaf first through a first source (5 lookups), and every other source is the pair just
    // found equal (20), so 25 lookups, plus 5 as computeIfAbsent looks each first meeting up
    // again. Path by path the walk makes 1 + 6 + 36 + 216 + 1,296 = 1,555.
    final var rung = ScopeReader.parseEntries(-1L, repeatedMultiplicationChain(4)).scopeEntry(0);
    final var memo = new CountingMemo();
    assertTrue(EntryGraph.equal(rung, ScopeReader.parseEntries(-1L, repeatedMultiplicationChain(4)).scopeEntry(0), memo));
    assertTrue(memo.lookups <= 30, () -> memo.lookups + " memo lookups");
    // Each composite is spelled out once: 74 characters of its own, its first source in full and
    // five MultiplicationChain#n of 21. The last one prints the leaf,
    // FixedPrice[index=4, value=1, exp=0, decimal=1] (46), six times. That is
    // 3 * 179 + 350 = 887 characters. Path by path it renders 1 + 6 + 36 + 216 = 259 composites at
    // 74 characters or more each, and 1,296 copies of the leaf.
    assertTrue(rung.toString().length() < 2_000, () -> rung.toString().length() + " characters");

    final var head = ScopeReader.parseEntries(-1L, repeatedMultiplicationChain(20)).scopeEntry(0);
    final var twin = ScopeReader.parseEntries(-1L, repeatedMultiplicationChain(20)).scopeEntry(0);

    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
      assertEquals(head, twin);
      assertEquals(head.hashCode(), twin.hashCode());
      assertTrue(head.toString().length() < 20_000, () -> head.toString().length() + " characters");
    });
  }

  /// A band reaches the slot it references through the banded record's own equals, and the
  /// reader shares that slot as well. Here each CappedFloored has two PythPull inputs, both banded
  /// to the next level's CappedFloored, so a comparison that started over at each composite a band
  /// reaches would double its work per level, 2^170 times over at full size. The rungs come
  /// first, at eight levels, as in the tests above.
  @Test
  void bandsBetweenCompositesStayLinear() {
    // The rung's comparison meets each level's CappedFloored and its two PythPulls first: 3 pairs
    // a level, 24 in all, each remembered by this one comparison, the composites a band reaches
    // included. Lookups: those 3 meetings, 3 more as computeIfAbsent looks each pair up again,
    // and 1 as the second band meets the next CappedFloored again. That is 7 a level, less 1 for
    // the last level, whose bands reference a leaf: 55. Path by path, a comparison that started
    // over at each composite would meet 2^8 - 1 = 255 of them.
    final var rung = ScopeReader.parseEntries(-1L, bandedChain(8)).scopeEntry(0);
    final var memo = new CountingMemo();
    assertTrue(EntryGraph.equal(rung, ScopeReader.parseEntries(-1L, bandedChain(8)).scopeEntry(0), memo));
    assertEquals(24, memo.size());
    assertTrue(memo.lookups <= 56, () -> memo.lookups + " memo lookups");
    // Each composite is spelled out once: under 90 characters of its own and two PythPulls of
    // about 125 each, the second band naming the next composite as CappedFloored#n, so under 400
    // a level. Path by path the rendering spells out those 255 composites.
    assertTrue(rung.toString().length() < 5_000, () -> rung.toString().length() + " characters");

    final var head = ScopeReader.parseEntries(-1L, bandedChain(170)).scopeEntry(0);
    final var twin = ScopeReader.parseEntries(-1L, bandedChain(170)).scopeEntry(0);
    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
      assertEquals(head, twin);
      assertEquals(head.hashCode(), twin.hashCode());
      assertTrue(head.toString().length() < 200_000, () -> head.toString().length() + " characters");
    });
  }

  /// `levels` CappedFloored slots at 3k, each with the PythPull at 3k + 1 as its source and the one
  /// at 3k + 2 as its cap, both banded to the next level's CappedFloored at 3k + 3. The last
  /// level's bands reference the Unused slot after it, as every later slot is.
  private static OracleMappings bandedChain(final int levels) {
    final byte[] priceTypes = new byte[SLOTS];
    final byte[][] generic = new byte[SLOTS][20];
    final int[] refPrice = new int[SLOTS];
    Arrays.fill(refPrice, NO_REF);
    for (int k = 0; k < levels; ++k) {
      final int slot = 3 * k;
      priceTypes[slot] = (byte) OracleType.CappedFloored.ordinal();
      ByteUtil.putInt16LE(generic[slot], 0, slot + 1); // sourceEntry
      generic[slot][2] = 1;                             // capEntry present
      ByteUtil.putInt16LE(generic[slot], 3, slot + 2);  // capEntry; byte 5, the floorEntry tag, stays 0
      priceTypes[slot + 1] = (byte) OracleType.PythPull.ordinal();
      priceTypes[slot + 2] = (byte) OracleType.PythPull.ordinal();
      refPrice[slot + 1] = slot + 3;
      refPrice[slot + 2] = slot + 3;
    }
    for (int i = 3 * levels; i < SLOTS; ++i) {
      priceTypes[i] = (byte) OracleType.Unused.ordinal();
    }
    return mappings(priceTypes, generic, refPrice);
  }

  /// `depth` slots of MultiplicationChain, each listing the next slot six times, then a FixedPrice
  /// of 1; every slot after it is Unused.
  private static OracleMappings repeatedMultiplicationChain(final int depth) {
    final byte[] priceTypes = new byte[SLOTS];
    final byte[][] generic = new byte[SLOTS][20];
    for (int i = 0; i < depth; ++i) {
      priceTypes[i] = (byte) OracleType.MultiplicationChain.ordinal();
      for (int s = 0; s < 6; ++s) {
        ByteUtil.putInt16LE(generic[i], 2 * s, i + 1); // sourceEntries[s]
      }
    }
    priceTypes[depth] = (byte) OracleType.FixedPrice.ordinal();
    ByteUtil.putInt64LE(generic[depth], 0, 1L); // Price.value, exponent 0
    for (int i = depth + 1; i < SLOTS; ++i) {
      priceTypes[i] = (byte) OracleType.Unused.ordinal();
    }
    return mappings(priceTypes, generic);
  }

  private static OracleMappings cappedFlooredChain(final int depth) {
    return cappedFlooredChain(depth, 0L);
  }

  /// `depth` slots of CappedFloored, each pointing source, cap and floor at the next;
  /// the slot that terminates the chain and every slot after it is Unused. The last
  /// CappedFloored carries `lastSourcesMaxAgeS`, every other one zero.
  private static OracleMappings cappedFlooredChain(final int depth, final long lastSourcesMaxAgeS) {
    final byte[] priceTypes = new byte[SLOTS];
    final byte[][] generic = new byte[SLOTS][20];
    for (int i = 0; i < depth - 1; ++i) {
      priceTypes[i] = (byte) OracleType.CappedFloored.ordinal();
      final int next = i + 1;
      ByteUtil.putInt16LE(generic[i], 0, next);  // sourceEntry
      generic[i][2] = 1;                          // capEntry present
      ByteUtil.putInt16LE(generic[i], 3, next);
      generic[i][5] = 1;                          // floorEntry present
      ByteUtil.putInt16LE(generic[i], 6, next);
    }
    ByteUtil.putInt64LE(generic[depth - 2], 8, lastSourcesMaxAgeS); // sourcesMaxAgeS
    for (int i = depth - 1; i < SLOTS; ++i) {
      priceTypes[i] = (byte) OracleType.Unused.ordinal();
    }
    return mappings(priceTypes, generic);
  }
}

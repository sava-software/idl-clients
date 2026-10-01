package software.sava.idl.clients.kamino.scope.entries;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Pins how a composite entry renders when the reader shares its inputs: each composite is
/// spelled out the first time a rendering reaches it and named by type and slot where it recurs,
/// so a tree, which never recurs, renders exactly as the hand-written formats always have. And how
/// a comparison walks two graphs: each pair once, remembering only the pairs it found equal, the
/// composites a record's own equals reaches included, and only until the comparison ends.
final class EntryGraphTests {

  /// No composite recurs in a tree, so every one is spelled out in its own format.
  @Test
  void aTreeRendersInFull() {
    final var chain = new MultiplicationChain(5, new ScopeEntry[]{new Unused(7), new Unused(8)}, 120L);
    final var capped = new CappedFloored(4, chain, new Unused(9), null, 60L);
    assertEquals(
        "CappedFloored[index=4, sourceEntry=MultiplicationChain{index=5, sourceEntries=[Unused[index=7], Unused[index=8]], sourcesMaxAgeS=120}, capEntry=Unused[index=9], flooredEntry=null, sourcesMaxAgeS=60]",
        capped.toString()
    );
  }

  /// A composite reached twice in one rendering is spelled out the first time and named by its
  /// type and slot the second.
  @Test
  void aRecurringCompositeIsNamedBySlot() {
    final var shared = new MultiplicationChain(5, new ScopeEntry[]{new Unused(7)}, 0L);
    final var capped = new CappedFloored(4, shared, shared, null, 60L);
    assertEquals(
        "CappedFloored[index=4, sourceEntry=MultiplicationChain{index=5, sourceEntries=[Unused[index=7]], sourcesMaxAgeS=0}, capEntry=MultiplicationChain#5, flooredEntry=null, sourcesMaxAgeS=60]",
        capped.toString()
    );
    final var mostRecent = new MostRecentOfEntry(3, new ScopeEntry[]{capped, shared}, 50, 600L, shared, OptionalInt.of(500));
    assertEquals(
        "MostRecentOfEntry{index=3, sources=[" + capped + ", MultiplicationChain#5], maxDivergenceBps=50, sourcesMaxAgeS=600, refPrice=MultiplicationChain#5, refPriceToleranceBps=OptionalInt[500]}",
        mostRecent.toString()
    );
  }

  /// What one rendering spelled out does not carry into the next: the same entry renders the same
  /// way every time, and an input rendered on its own is spelled out in full.
  @Test
  void eachRenderingStartsAfresh() {
    final var shared = new CappedMostRecentOf(6, new ScopeEntry[]{new Unused(7)}, 25, 30L, new Unused(8));
    final var conditional = new Conditional(2, null, 10, new ScopeEntry[]{shared, shared});
    final String first = conditional.toString();
    assertEquals(first, conditional.toString());
    assertEquals(
        "CappedMostRecentOf{index=6, sources=[Unused[index=7]], maxDivergenceBps=25, sourcesMaxAgeS=30, capEntry=Unused[index=8]}",
        shared.toString()
    );
  }

  /// A pair found equal is remembered only for the comparison that found it, and only as equal: a
  /// different pair sharing one side still compares its fields.
  @Test
  void aRememberedPairDoesNotStandInForAnother() {
    final var source = new MultiplicationChain(5, new ScopeEntry[]{new Unused(7)}, 0L);
    final var sameSource = new MultiplicationChain(5, new ScopeEntry[]{new Unused(7)}, 0L);
    final var otherSource = new MultiplicationChain(5, new ScopeEntry[]{new Unused(9)}, 0L);
    final var both = new CappedFloored(4, source, source, null, 60L);
    assertEquals(both, new CappedFloored(4, sameSource, sameSource, null, 60L));
    assertNotEquals(both, new CappedFloored(4, sameSource, otherSource, null, 60L));
    assertNotEquals(both, new CappedFloored(4, otherSource, sameSource, null, 60L));

    // and only for the comparison that found it: an array edited after one is seen by the next
    final var sources = new ScopeEntry[]{new Unused(7)};
    final var chain = new MultiplicationChain(5, sources, 0L);
    final var sameChain = new MultiplicationChain(5, new ScopeEntry[]{new Unused(7)}, 0L);
    assertEquals(chain, sameChain);
    sources[0] = new Unused(9);
    assertNotEquals(chain, sameChain);
  }

  /// A pair the walk has already found equal is not compared again. Seeded with a pair that is
  /// not in fact equal, the walk believes the seed: proof the memo is consulted, here at the top
  /// and, through an input, below it, so the walk threads one memo through every level.
  @Test
  void aRememberedPairIsTrustedAtEveryLevel() {
    final var source = new MultiplicationChain(5, new ScopeEntry[]{new Unused(7)}, 0L);
    final var otherSource = new MultiplicationChain(5, new ScopeEntry[]{new Unused(9)}, 0L);

    final Map<ScopeEntry, Set<ScopeEntry>> seeded = new IdentityHashMap<>();
    seeded.computeIfAbsent(source, _ -> Collections.newSetFromMap(new IdentityHashMap<>())).add(otherSource);
    assertTrue(EntryGraph.equal(source, otherSource, seeded));

    final var capped = new CappedFloored(4, source, null, null, 60L);
    final var otherCapped = new CappedFloored(4, otherSource, null, null, 60L);
    assertNotEquals(capped, otherCapped);
    assertTrue(EntryGraph.equal(capped, otherCapped, seeded));
  }

  /// The walk records each pair it finds equal, its inputs' pairs included, and never a pair it
  /// found to differ.
  @Test
  void theWalkRemembersOnlyThePairsItFoundEqual() {
    final var source = new MultiplicationChain(5, new ScopeEntry[]{new Unused(7)}, 0L);
    final var sameSource = new MultiplicationChain(5, new ScopeEntry[]{new Unused(7)}, 0L);
    final var capped = new CappedFloored(4, source, null, null, 60L);
    final var sameCapped = new CappedFloored(4, sameSource, null, null, 60L);

    final Map<ScopeEntry, Set<ScopeEntry>> equal = new IdentityHashMap<>();
    assertTrue(EntryGraph.equal(capped, sameCapped, equal));
    assertTrue(equal.get(capped).contains(sameCapped));
    assertTrue(equal.get(source).contains(sameSource));

    final var differs = new CappedFloored(4, sameSource, null, null, 61L);
    final Map<ScopeEntry, Set<ScopeEntry>> unequal = new IdentityHashMap<>();
    assertFalse(EntryGraph.equal(capped, differs, unequal));
    assertNull(unequal.get(capped));
  }

  /// A record that compares an entry field with the field's own equals, a band's refPrice here,
  /// reaches the composite there from inside the comparison already running, and the composite
  /// joins it. Seeded with a pair that is not in fact equal, the comparison believes the seed
  /// through the band too.
  @Test
  void aCompositeReachedThroughARecordJoinsTheRunningComparison() {
    final var band = new MultiplicationChain(5, new ScopeEntry[]{new Unused(7)}, 0L);
    final var otherBand = new MultiplicationChain(5, new ScopeEntry[]{new Unused(9)}, 0L);
    final var capped = new CappedFloored(2, new PythPull(3, PublicKey.NONE, Set.of(), band, OptionalInt.empty()), null, new Unused(8), 60L);
    final var otherCapped = new CappedFloored(2, new PythPull(3, PublicKey.NONE, Set.of(), otherBand, OptionalInt.empty()), null, new Unused(8), 60L);
    assertNotEquals(capped, otherCapped);

    final Map<ScopeEntry, Set<ScopeEntry>> seeded = new IdentityHashMap<>();
    seeded.computeIfAbsent(band, _ -> Collections.newSetFromMap(new IdentityHashMap<>())).add(otherBand);
    assertTrue(EntryGraph.equal(capped, otherCapped, seeded));
  }

  /// What a comparison remembers ends with it: a pair seeded as equal for one comparison is
  /// compared afresh by the next.
  @Test
  void aComparisonsMemoEndsWithIt() {
    final var source = new MultiplicationChain(5, new ScopeEntry[]{new Unused(7)}, 0L);
    final var otherSource = new MultiplicationChain(5, new ScopeEntry[]{new Unused(9)}, 0L);

    final Map<ScopeEntry, Set<ScopeEntry>> seeded = new IdentityHashMap<>();
    seeded.computeIfAbsent(source, _ -> Collections.newSetFromMap(new IdentityHashMap<>())).add(otherSource);
    assertTrue(EntryGraph.equal(source, otherSource, seeded));
    assertNotEquals(source, otherSource);
  }
}

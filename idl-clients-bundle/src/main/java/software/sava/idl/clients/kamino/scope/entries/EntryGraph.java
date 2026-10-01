package software.sava.idl.clients.kamino.scope.entries;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/// Equality, hashing and rendering for the composite entries, whose inputs are entries the reader
/// shares between every slot that names them. Taken path by path, a graph whose slots share inputs
/// is exponential in its depth: CappedFloored entries naming the next slot as source, cap and floor
/// have 3^n paths through n slots, and a MultiplicationChain may list one slot six times. So:
/// - a comparison walks the two graphs together, each pair once: a pair found equal is remembered
///   for the rest of the comparison, as is a composite a record's own `equals` reaches (a leaf so
///   reached, with one entry field at most, is compared as met), and the first difference ends it;
/// - a hash reads each input's slot and type, which equal entries share, rather than its subgraph;
/// - a rendering spells each composite out once and names it by type and slot wherever it recurs.
///
/// Entries are immutable, so a pair found equal stays equal: remembering one can only save work,
/// never change an answer.
final class EntryGraph {

  /// The composites the outermost rendering running on this thread has spelled out.
  private static final ThreadLocal<Set<ScopeEntry>> RENDERED = new ThreadLocal<>();
  /// The pairs the outermost comparison running on this thread has found equal. A record that
  /// compares an entry field with the field's own `equals` (a band's `refPrice`, a TWAP's or an
  /// EMA's `sourceEntry`) reaches a composite's `equals` from inside that comparison, and the
  /// composite walks on with this memo rather than starting one of its own. Otherwise a chain
  /// that alternates composites with two such records apiece doubles its work at every level.
  private static final ThreadLocal<Map<ScopeEntry, Set<ScopeEntry>>> EQUAL_PAIRS = new ThreadLocal<>();

  /// Whether `entry` equals `other`: the composites compare their fields here, walking the two
  /// graphs together and each pair of entries once, as part of the comparison already running on
  /// this thread if there is one.
  static boolean equal(final ScopeEntry entry, final ScopeEntry other) {
    final var running = EQUAL_PAIRS.get();
    return running == null ? equal(entry, other, new IdentityHashMap<>()) : compare(entry, other, running);
  }

  /// Compares `entry` with `other` as the outermost comparison on this thread, `equal` holding the
  /// pairs it finds equal; package-private for tests.
  static boolean equal(final ScopeEntry entry,
                       final ScopeEntry other,
                       final Map<ScopeEntry, Set<ScopeEntry>> equal) {
    EQUAL_PAIRS.set(equal);
    try {
      return compare(entry, other, equal);
    } finally {
      EQUAL_PAIRS.remove();
    }
  }

  private static boolean compare(final ScopeEntry entry,
                                 final ScopeEntry other,
                                 final Map<ScopeEntry, Set<ScopeEntry>> equal) {
    if (entry == null) {
      return other == null;
    }
    // a null other matches no composite case below and fails the entry's own equals
    final var equalToEntry = equal.get(entry);
    if (equalToEntry != null && equalToEntry.contains(other)) {
      return true;
    }
    final boolean same = switch (entry) {
      case CappedFloored a when other instanceof CappedFloored b -> a.index() == b.index()
          && a.sourcesMaxAgeS() == b.sourcesMaxAgeS()
          && compare(a.sourceEntry(), b.sourceEntry(), equal)
          && compare(a.capEntry(), b.capEntry(), equal)
          && compare(a.flooredEntry(), b.flooredEntry(), equal);
      case CappedMostRecentOf a when other instanceof CappedMostRecentOf b -> a.index() == b.index()
          && a.sourcesMaxAgeS() == b.sourcesMaxAgeS()
          && a.maxDivergenceBps() == b.maxDivergenceBps()
          && compare(a.capEntry(), b.capEntry(), equal)
          && compare(a.sources(), b.sources(), equal);
      case MostRecentOfEntry a when other instanceof MostRecentOfEntry b -> a.index() == b.index()
          && a.sourcesMaxAgeS() == b.sourcesMaxAgeS()
          && a.maxDivergenceBps() == b.maxDivergenceBps()
          && a.refPriceToleranceBps().equals(b.refPriceToleranceBps())
          && compare(a.refPrice(), b.refPrice(), equal)
          && compare(a.sources(), b.sources(), equal);
      case Conditional a when other instanceof Conditional b -> a.index() == b.index()
          && a.toleranceBps() == b.toleranceBps()
          && a.condition() == b.condition()
          && compare(a.sources(), b.sources(), equal);
      case MultiplicationChain a when other instanceof MultiplicationChain b -> a.index() == b.index()
          && a.sourcesMaxAgeS() == b.sourcesMaxAgeS()
          && compare(a.sourceEntries(), b.sourceEntries(), equal);
      // a leaf, a composite against an entry of another type, or a record that compares its entry
      // fields with their own equals, which rejoin this comparison through EQUAL_PAIRS
      default -> entry.equals(other);
    };
    if (same) {
      equal.computeIfAbsent(entry, _ -> Collections.newSetFromMap(new IdentityHashMap<>())).add(other);
    }
    return same;
  }

  private static boolean compare(final ScopeEntry[] entries,
                                 final ScopeEntry[] others,
                                 final Map<ScopeEntry, Set<ScopeEntry>> equal) {
    if (entries == null || others == null) {
      return entries == others;
    }
    if (entries.length != others.length) {
      return false;
    }
    for (int i = 0; i < entries.length; ++i) {
      if (!compare(entries[i], others[i], equal)) {
        return false;
      }
    }
    return true;
  }

  /// An input as a composite's hash reads it: its slot and type, which equal inputs share.
  static int hash(final ScopeEntry input) {
    return input == null ? 0 : 31 * input.index() + Objects.hashCode(input.oracleType());
  }

  static int hash(final ScopeEntry[] inputs) {
    if (inputs == null) {
      return 0;
    }
    int result = 1;
    for (final var input : inputs) {
      result = 31 * result + hash(input);
    }
    return result;
  }

  /// `entry` as its `toString` spells it out, the first time the outermost rendering running on this
  /// thread reaches it; where it recurs within that rendering, its type and slot.
  static String render(final ScopeEntry entry, final Supplier<String> spelledOut) {
    final var outer = RENDERED.get();
    final var rendered = outer == null ? Collections.newSetFromMap(new IdentityHashMap<ScopeEntry, Boolean>()) : outer;
    RENDERED.set(rendered); // within an outer rendering, the set it already holds
    try {
      return rendered.add(entry) ? spelledOut.get() : entry.getClass().getSimpleName() + '#' + entry.index();
    } finally {
      if (outer == null) {
        RENDERED.remove();
      }
    }
  }

  private EntryGraph() {
  }
}

package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

/// Bounds [#sourceEntry()]'s price by the prices of [#capEntry()] and [#flooredEntry()],
/// either of which may be absent but not both.
///
/// `sourcesMaxAgeS` is the age the refresh allows the source and each configured bound
/// to have (`capped_floored::check_entries_age`). Zero means the entry was configured
/// before Scope 0.42.0 introduced the field: the program skips the age check for it, and
/// refuses zero on any new configuration.
public record CappedFloored(int index,
                            ScopeEntry sourceEntry,
                            ScopeEntry capEntry,
                            ScopeEntry flooredEntry,
                            long sourcesMaxAgeS) implements ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.CappedFloored;
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof CappedFloored other && EntryGraph.equal(this, other);
  }

  @Override
  public int hashCode() {
    int result = Integer.hashCode(index);
    result = 31 * result + EntryGraph.hash(sourceEntry);
    result = 31 * result + EntryGraph.hash(capEntry);
    result = 31 * result + EntryGraph.hash(flooredEntry);
    result = 31 * result + Long.hashCode(sourcesMaxAgeS);
    return result;
  }

  /// The record's own format, with each composite input spelled out once.
  @Override
  public String toString() {
    return EntryGraph.render(this, () -> "CappedFloored[" +
        "index=" + index +
        ", sourceEntry=" + sourceEntry +
        ", capEntry=" + capEntry +
        ", flooredEntry=" + flooredEntry +
        ", sourcesMaxAgeS=" + sourcesMaxAgeS +
        ']');
  }
}

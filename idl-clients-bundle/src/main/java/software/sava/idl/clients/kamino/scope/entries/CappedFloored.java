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
}

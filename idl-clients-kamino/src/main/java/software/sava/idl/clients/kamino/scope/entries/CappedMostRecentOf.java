package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Arrays;
import java.util.Objects;

public record CappedMostRecentOf(ScopeEntry[] sources,
                                 int maxDivergenceBps,
                                 long sourcesMaxAgeS,
                                 ScopeEntry capEntry) implements MostRecentOf {

  @Override
  public OracleType oracleType() {
    return OracleType.CappedMostRecentOf;
  }

  @Override
  public boolean equals(final Object o) {
    if (o instanceof CappedMostRecentOf(
        final ScopeEntry[] oSources, final int oMaxDivergenceBps, final long oSourcesMaxAgeS, final ScopeEntry oCapEntry
    )) {
      return sourcesMaxAgeS == oSourcesMaxAgeS
          && maxDivergenceBps == oMaxDivergenceBps
          && Objects.equals(capEntry, oCapEntry)
          && Arrays.equals(sources, oSources);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int result = Arrays.hashCode(sources);
    result = 31 * result + maxDivergenceBps;
    result = 31 * result + Long.hashCode(sourcesMaxAgeS);
    result = 31 * result + Objects.hashCode(capEntry);
    return result;
  }

  @Override
  public String toString() {
    return "CappedMostRecentOf{" +
        "sources=" + Arrays.toString(sources) +
        ", maxDivergenceBps=" + maxDivergenceBps +
        ", sourcesMaxAgeS=" + sourcesMaxAgeS +
        ", capEntry=" + capEntry +
        '}';
  }
}

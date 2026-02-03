package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Arrays;
import java.util.Objects;

public record CappedMostRecentOf(int index,
                                 ScopeEntry[] sources,
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
        final int i, final ScopeEntry[] oSources, final int oMaxDivergenceBps, final long oSourcesMaxAgeS, final ScopeEntry oCapEntry
    )) {
      return index == i
          && sourcesMaxAgeS == oSourcesMaxAgeS
          && maxDivergenceBps == oMaxDivergenceBps
          && Objects.equals(capEntry, oCapEntry)
          && Arrays.equals(sources, oSources);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int result = Integer.hashCode(index);
    result = 31 * result + Arrays.hashCode(sources);
    result = 31 * result + maxDivergenceBps;
    result = 31 * result + Long.hashCode(sourcesMaxAgeS);
    result = 31 * result + Objects.hashCode(capEntry);
    return result;
  }

  @Override
  public String toString() {
    return "CappedMostRecentOf{" +
        "index=" + index +
        ", sources=" + Arrays.toString(sources) +
        ", maxDivergenceBps=" + maxDivergenceBps +
        ", sourcesMaxAgeS=" + sourcesMaxAgeS +
        ", capEntry=" + capEntry +
        '}';
  }
}

package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Arrays;
import java.util.Objects;

public record MostRecentOfEntry(ScopeEntry[] sources,
                                int maxDivergenceBps,
                                long sourcesMaxAgeS,
                                ScopeEntry refPrice) implements MostRecentOf {

  @Override
  public OracleType oracleType() {
    return OracleType.MostRecentOf;
  }

  @Override
  public boolean equals(final Object o) {
    if (o instanceof MostRecentOfEntry(
        final ScopeEntry[] oSources, final int oMaxDivergenceBps, final long oSourcesMaxAgeS, final ScopeEntry oRefPrice
    )) {
      return sourcesMaxAgeS == oSourcesMaxAgeS
          && maxDivergenceBps == oMaxDivergenceBps
          && Objects.equals(refPrice, oRefPrice)
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
    result = 31 * result + Objects.hashCode(refPrice);
    return result;
  }

  @Override
  public String toString() {
    return "MostRecentOfEntry{" +
        "sources=" + Arrays.toString(sources) +
        ", maxDivergenceBps=" + maxDivergenceBps +
        ", sourcesMaxAgeS=" + sourcesMaxAgeS +
        ", refPrice=" + refPrice +
        '}';
  }
}

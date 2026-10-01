package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Arrays;

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
    return o instanceof CappedMostRecentOf other && EntryGraph.equal(this, other);
  }

  @Override
  public int hashCode() {
    int result = Integer.hashCode(index);
    result = 31 * result + EntryGraph.hash(sources);
    result = 31 * result + maxDivergenceBps;
    result = 31 * result + Long.hashCode(sourcesMaxAgeS);
    result = 31 * result + EntryGraph.hash(capEntry);
    return result;
  }

  @Override
  public String toString() {
    return EntryGraph.render(this, () -> "CappedMostRecentOf{" +
        "index=" + index +
        ", sources=" + Arrays.toString(sources) +
        ", maxDivergenceBps=" + maxDivergenceBps +
        ", sourcesMaxAgeS=" + sourcesMaxAgeS +
        ", capEntry=" + capEntry +
        '}');
  }
}

package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Arrays;
import java.util.OptionalInt;

public record MostRecentOfEntry(int index,
                                ScopeEntry[] sources,
                                int maxDivergenceBps,
                                long sourcesMaxAgeS,
                                ScopeEntry refPrice,
                                OptionalInt refPriceToleranceBps) implements MostRecentOf {

  @Override
  public OracleType oracleType() {
    return OracleType.MostRecentOf;
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof MostRecentOfEntry other && EntryGraph.equal(this, other);
  }

  @Override
  public int hashCode() {
    int result = Integer.hashCode(index);
    result = 31 * result + EntryGraph.hash(sources);
    result = 31 * result + maxDivergenceBps;
    result = 31 * result + Long.hashCode(sourcesMaxAgeS);
    result = 31 * result + EntryGraph.hash(refPrice);
    result = 31 * result + refPriceToleranceBps.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return EntryGraph.render(this, () -> "MostRecentOfEntry{" +
        "index=" + index +
        ", sources=" + Arrays.toString(sources) +
        ", maxDivergenceBps=" + maxDivergenceBps +
        ", sourcesMaxAgeS=" + sourcesMaxAgeS +
        ", refPrice=" + refPrice +
        ", refPriceToleranceBps=" + refPriceToleranceBps +
        '}');
  }
}

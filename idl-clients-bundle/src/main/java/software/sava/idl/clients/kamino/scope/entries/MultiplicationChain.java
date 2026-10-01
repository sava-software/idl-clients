package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Arrays;

public record MultiplicationChain(int index,
                                  ScopeEntry[] sourceEntries,
                                  long sourcesMaxAgeS) implements ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.MultiplicationChain;
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof MultiplicationChain other && EntryGraph.equal(this, other);
  }

  @Override
  public int hashCode() {
    int result = Integer.hashCode(index);
    result = 31 * result + EntryGraph.hash(sourceEntries);
    result = 31 * result + Long.hashCode(sourcesMaxAgeS);
    return result;
  }

  @Override
  public String toString() {
    return EntryGraph.render(this, () -> "MultiplicationChain{" +
        "index=" + index +
        ", sourceEntries=" + Arrays.toString(sourceEntries) +
        ", sourcesMaxAgeS=" + sourcesMaxAgeS +
        '}');
  }
}

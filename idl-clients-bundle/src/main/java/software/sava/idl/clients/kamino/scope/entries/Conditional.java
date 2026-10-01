package software.sava.idl.clients.kamino.scope.entries;

import software.sava.idl.clients.kamino.scope.gen.types.Condition;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.util.Arrays;

public record Conditional(int index,
                          Condition condition,
                          int toleranceBps,
                          ScopeEntry[] sources) implements ScopeEntry {

  @Override
  public OracleType oracleType() {
    return OracleType.Conditional;
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof Conditional other && EntryGraph.equal(this, other);
  }

  @Override
  public int hashCode() {
    int result = Integer.hashCode(index);
    result = 31 * result + (condition == null ? 0 : condition.hashCode());
    result = 31 * result + toleranceBps;
    result = 31 * result + EntryGraph.hash(sources);
    return result;
  }

  @Override
  public String toString() {
    return EntryGraph.render(this, () -> "Conditional{" +
        "index=" + index +
        ", condition=" + condition +
        ", toleranceBps=" + toleranceBps +
        ", sources=" + Arrays.toString(sources) +
        '}');
  }
}

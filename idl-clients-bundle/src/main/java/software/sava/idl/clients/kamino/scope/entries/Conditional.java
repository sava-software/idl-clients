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
    if (o instanceof Conditional(
        final int i, final Condition oCondition, final int oToleranceBps, final ScopeEntry[] oSources
    )) {
      return index == i
          && toleranceBps == oToleranceBps
          && condition == oCondition
          && Arrays.equals(sources, oSources);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int result = Integer.hashCode(index);
    result = 31 * result + (condition == null ? 0 : condition.hashCode());
    result = 31 * result + toleranceBps;
    result = 31 * result + Arrays.hashCode(sources);
    return result;
  }

  @Override
  public String toString() {
    return "Conditional{" +
        "index=" + index +
        ", condition=" + condition +
        ", toleranceBps=" + toleranceBps +
        ", sources=" + Arrays.toString(sources) +
        '}';
  }
}

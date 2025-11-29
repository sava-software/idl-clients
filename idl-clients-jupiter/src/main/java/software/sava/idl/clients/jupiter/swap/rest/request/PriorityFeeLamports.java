package software.sava.idl.clients.jupiter.swap.rest.request;

public record PriorityFeeLamports(String priorityLevel, long maxLamports, boolean global) {

  String toJson() {
    return String.format("""
            "priorityLevelWithMaxLamports": {"priorityLevel": "%s", "maxLamports": %d, "global": %b}""",
        priorityLevel, maxLamports, global
    );
  }
}

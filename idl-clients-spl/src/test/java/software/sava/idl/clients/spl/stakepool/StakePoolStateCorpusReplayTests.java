package software.sava.idl.clients.spl.stakepool;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/// Deterministically replays the committed stakePoolState seed corpus through
/// [StakePoolStateFuzz] on every `check`, so the corpus cannot rot between
/// fuzz runs and PIT's mutants face the same structural invariants as the
/// fuzzer — a real mainnet stake-pool account whose `Option`-steered offsets
/// from-scratch tests don't assemble. New seeds, including minimized fuzz
/// findings, replay here automatically.
final class StakePoolStateCorpusReplayTests {

  @Test
  void stakePoolStateSeedCorpusReplay() throws IOException, URISyntaxException {
    final var url = StakePoolStateCorpusReplayTests.class.getResource("/fuzz/stakePoolState");
    assumeTrue(url != null && "file".equals(url.getProtocol()), "seed corpus not on the classpath as a directory");
    final var dir = Path.of(url.toURI());
    try (final var files = Files.list(dir)) {
      final var seeds = files.filter(Files::isRegularFile).sorted().toList();
      assertFalse(seeds.isEmpty(), "empty seed corpus at " + dir);
      for (final var seed : seeds) {
        final byte[] data = Files.readAllBytes(seed);
        assertDoesNotThrow(() -> StakePoolStateFuzz.fuzzerTestOneInput(data), seed.getFileName().toString());
      }
    }
  }
}

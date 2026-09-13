package software.sava.idl.clients.spl.token_2022;

import org.junit.jupiter.api.Test;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

/// Holds the `token2022Account` fuzz seed corpus to the account corpus it was cut from.
///
/// Each seed is a copy — one selector byte followed by an account's raw bytes — so the two
/// directories can drift: an account re-fetched at a later slot, or a seed minimized by
/// hand, would leave the corpus claiming a provenance the seed no longer has. This test
/// re-derives the whole seed corpus from `/token_2022/accounts/manifest.json` and compares
/// it byte for byte, in both directions: a seed naming no account fails, and an account
/// with no seed fails.
///
/// It also replays every seed through [Token2022AccountFuzz#fuzzerTestOneInput] so the
/// harness's own assertions — length, round trip and the sava-core differential — run
/// under `test` and not only during a fuzz campaign, and it requires the differential to
/// actually fire on the corpus. The hardening plugin generates its own replay test from
/// the registered target; this one is about the two copies agreeing, and the replay comes
/// along because there is nothing else worth doing with the bytes it just read.
final class Token2022AccountFuzzSeedTests {

  private static final String ACCOUNTS = "/token_2022/accounts/";
  private static final String SEEDS = "/fuzz/token2022Account";

  /// The selector byte each account kind is seeded under: the harness reads an even first
  /// byte as [software.sava.idl.clients.spl.token_2022.gen.types.Mint] and an odd one as
  /// [software.sava.idl.clients.spl.token_2022.gen.types.Token]. A multisig is neither, so
  /// it is seeded once under each — both readers have to refuse it, and which one refuses
  /// it where is exactly what a seed pins.
  private static List<Seed> expectedSeeds() {
    final var manifest = new ManifestParser();
    JsonIterator.parse(resource(ACCOUNTS + "manifest.json")).testObject(manifest);
    final var accounts = manifest.accounts;
    assertEquals(25, accounts.size(), "the account corpus manifest changed size");
    final var seeds = new ArrayList<Seed>();
    for (final var account : accounts) {
      final byte[] data = resource(ACCOUNTS + account.address());
      assertEquals(account.length(), data.length,
                   account.address() + " is not the length its manifest entry claims");
      switch (account.kind()) {
        case "mint" -> seeds.add(new Seed(account.address(), 0, data));
        case "account" -> seeds.add(new Seed(account.address(), 1, data));
        case "multisig" -> {
          seeds.add(new Seed(account.address() + "-mint", 0, data));
          seeds.add(new Seed(account.address() + "-account", 1, data));
        }
        default -> throw new AssertionError("unknown account kind " + account.kind());
      }
    }
    return seeds;
  }

  @Test
  void seedsAreTheAccountCorpusBehindASelectorByte() throws Exception {
    final var expected = new TreeMap<String, Seed>();
    for (final var seed : expectedSeeds()) {
      assertNull(expected.put(seed.name(), seed), "two accounts claim the seed name " + seed.name());
    }
    final var actual = seedFiles();
    assertEquals(expected.navigableKeySet().toString(), actual.navigableKeySet().toString(),
                 "the seed corpus and the account corpus name different things");
    for (final var seed : expected.values()) {
      final byte[] bytes = actual.get(seed.name());
      assertEquals(seed.data().length + 1, bytes.length, seed.name() + " is not one selector byte longer");
      assertEquals(seed.selector(), bytes[0] & 0xFF, seed.name() + " carries the wrong selector byte");
      assertArrayEquals(seed.data(), Arrays.copyOfRange(bytes, 1, bytes.length),
                        seed.name() + " has drifted from " + ACCOUNTS + seed.address());
    }
  }

  @Test
  void replaysEverySeedThroughTheHarness() throws Exception {
    final var seeds = seedFiles();
    assertEquals(26, seeds.size(), "expected one seed per mint and token account plus two for the multisig");
    int differential = 0;
    final var outside = new ArrayList<String>();
    for (final var seed : seeds.entrySet()) {
      final byte[] bytes = seed.getValue();
      try {
        Token2022AccountFuzz.fuzzerTestOneInput(bytes);
      } catch (final RuntimeException | AssertionError finding) {
        throw new AssertionError("seed " + seed.getKey() + " fails the harness", finding);
      }
      if (Token2022AccountFuzz.differentialApplies(bytes)) {
        ++differential;
      } else {
        outside.add(seed.getKey());
      }
    }
    // The differential is the harness's strongest oracle and the easiest to render inert:
    // every one of its guards is a reason to skip, so a corpus none of them admits would
    // pass while comparing nothing. Twenty-four of the twenty-six seeds are inside it, and
    // the two that are not are named rather than counted: both are the multisig, whose 355
    // bytes sava-core refuses by length before reading them. The four extension-free
    // accounts were outside it too while the catalog pinned sava-core 25.11.0, which threw
    // on Mint::LEN and Account::LEN exactly; 25.11.1 decodes them.
    assertEquals(List.of("et1Arzfg3zufiKMyNtudiM7QVWzG4F9e3ukWxiHAZMs-account",
                         "et1Arzfg3zufiKMyNtudiM7QVWzG4F9e3ukWxiHAZMs-mint"),
                 outside,
                 "the seeds outside the sava-core differential changed");
    assertEquals(24, differential, "the sava-core differential fires on a different number of seeds");
  }

  private static TreeMap<String, byte[]> seedFiles() throws IOException, URISyntaxException {
    final var url = Token2022AccountFuzzSeedTests.class.getResource(SEEDS);
    assertNotNull(url, "seed corpus missing from test resources: " + SEEDS);
    final var dir = Path.of(url.toURI());
    final var seeds = new TreeMap<String, byte[]>();
    try (final Stream<Path> files = Files.list(dir)) {
      for (final var file : files.filter(Files::isRegularFile).toList()) {
        seeds.put(file.getFileName().toString(), Files.readAllBytes(file));
      }
    }
    assertTrue(!seeds.isEmpty(), "empty seed corpus: " + dir);
    return seeds;
  }

  private static byte[] resource(final String name) {
    try (final var in = Token2022AccountFuzzSeedTests.class.getResourceAsStream(name)) {
      assertNotNull(in, "corpus file " + name + " is missing");
      return in.readAllBytes();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private record Seed(String name, String address, int selector, byte[] data) {

    Seed(final String name, final int selector, final byte[] data) {
      this(name, name.endsWith("-mint") || name.endsWith("-account")
              ? name.substring(0, name.lastIndexOf('-'))
              : name,
           selector, data);
    }
  }

  private record Account(String address, String kind, int length) {
  }

  private static final class AccountParser implements FieldBufferPredicate {

    private String address;
    private String kind;
    private int length;

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("address", buf, offset, len)) {
        address = ji.readString();
      } else if (fieldEquals("kind", buf, offset, len)) {
        kind = ji.readString();
      } else if (fieldEquals("length", buf, offset, len)) {
        length = ji.readInt();
      } else {
        ji.skip();
      }
      return true;
    }

    Account get() {
      return new Account(address, kind, length);
    }
  }

  private static final class ManifestParser implements FieldBufferPredicate {

    private final List<Account> accounts = new ArrayList<>();

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("accounts", buf, offset, len)) {
        while (ji.readArray()) {
          accounts.add(ji.parseObject(new AccountParser(), AccountParser::get));
        }
      } else {
        ji.skip();
      }
      return true;
    }
  }
}

plugins {
  id("software.sava.build.feature.hardening")
}

testModuleInfo {
  requires("jdk.httpserver")
  requires("org.junit.jupiter.api")
  runtimeOnly("org.junit.jupiter.engine")
}

dependencies {
  project(":idl-clients-spl")
}

hardening {
  // Suites are split for inner-loop speed, not for coverage: 'clients' is a
  // catch-all by exclusion, so a new hand-written class lands in some suite by
  // default. Every suite drops the same three families — generated code (owned
  // by idl-src-gen), test/fuzz sources sharing the recompiled root, and the
  // git-ignored 'Integ.*' scratch files (present locally, absent in CI, so
  // mutating them would make the baseline machine-dependent).
  val notMutated = listOf(
    "software.sava.idl.clients.*.gen.*",
    "software.sava.idl.clients.*Test*",
    "software.sava.idl.clients.*Fuzz*",
    // a test helper whose name matches neither glob above; PIT cannot tell the
    // test root from the main root once they are merged for the recompile, so
    // any such helper has to be named here or it gets mutated
    "software.sava.idl.clients.kamino.scope.entries.ResourceUtil",
    "software.sava.idl.clients.*.Integ"
  )
  mutation.register("orca") {
    // BigInteger arithmetic is method calls, which MathMutator (primitive
    // bytecode ops) cannot reach — see config/pitest/README.md. Requires
    // pitest >= 1.25.8 on Java 25. BIG_DECIMAL is omitted deliberately: it
    // fires zero times anywhere in this module. NAKED_RECEIVER makes dropped
    // fluent calls (receiver-returning expressions) expressible — trial
    // numbers in config/pitest/README.md.
    mutators = "STRONGER,EXPERIMENTAL_BIG_INTEGER,EXPERIMENTAL_NAKED_RECEIVER"
    // quote math and tick/PDA derivation
    targetClasses = listOf("software.sava.idl.clients.orca.*")
    excludedClasses = notMutated
    targetTests = "software.sava.idl.clients.orca.*Test*"
  }
  mutation.register("scope") {
    // the oracle price readers: a fixed-layout account walked by offset, where a
    // wrong branch yields a plausible wrong price rather than a failure.
    // NAKED_RECEIVER fired 6 times at zero baseline cost — see
    // config/pitest/README.md.
    mutators = "STRONGER,EXPERIMENTAL_NAKED_RECEIVER"
    targetClasses = listOf("software.sava.idl.clients.kamino.scope.*")
    excludedClasses = notMutated
    targetTests = "software.sava.idl.clients.kamino.*Test*"
  }
  mutation.register("clients") {
    // BigInteger arithmetic is method calls, which MathMutator (primitive
    // bytecode ops) cannot reach — see config/pitest/README.md. Requires
    // pitest >= 1.25.8 on Java 25. BIG_DECIMAL is omitted deliberately: it
    // fires zero times anywhere in this module. NAKED_RECEIVER makes dropped
    // fluent calls (receiver-returning expressions) expressible — trial
    // numbers in config/pitest/README.md.
    mutators = "STRONGER,EXPERIMENTAL_BIG_INTEGER,EXPERIMENTAL_NAKED_RECEIVER"
    // everything else hand-written in this module
    targetClasses = listOf("software.sava.idl.clients.*")
    excludedClasses = notMutated + listOf(
      // owned by the suites above
      "software.sava.idl.clients.orca.*",
      "software.sava.idl.clients.kamino.scope.*",
      // owned by idl-clients-spl's own suite; it reaches this classpath as a
      // project dependency, so a bare wildcard would mutate it twice
      "software.sava.idl.clients.spl.*"
    )
    targetTests = "software.sava.idl.clients.*Test*"
  }
  fuzz.register("routeV2") {
    targetClass = "software.sava.idl.clients.jupiter.swap.RouteV2DataFuzz"
    // route instructions are a few hundred bytes on-chain; the harness supplies
    // the discriminator, so the budget is all payload
    maxLen = 1024
  }
  fuzz.register("scopeReader") {
    targetClass = "software.sava.idl.clients.kamino.scope.entries.ScopeReaderFuzz"
    // OracleMappings is a fixed 29704-byte account; a little headroom lets the mutator
    // probe over-long inputs. Seeds are real account dumps — the layout is unreachable
    // from scratch
    maxLen = 30000
    seedCorpus = layout.projectDirectory.dir("src/test/resources/fuzz/scopeReader")
  }
  fuzz.register("orcaTickMath") {
    targetClass = "software.sava.idl.clients.orca.OrcaTickMathFuzz"
    // the harness folds the bytes into a u128 sqrt price; 17 bytes covers the
    // whole domain (selector byte + 16-byte value), and every prefix is valid,
    // so no seed corpus
    maxLen = 32
  }
  fuzz.register("jupiterResponse") {
    targetClass = "software.sava.idl.clients.jupiter.swap.rest.response.JupiterResponseFuzz"
    // REST bodies: a selector byte then JSON. Real responses run to a few KB;
    // seeds are the test-fixture bodies for each parser, prefixed with their
    // selector, since structured JSON is slow to reach from scratch
    maxLen = 8192
    seedCorpus = layout.projectDirectory.dir("src/test/resources/fuzz/jupiterResponse")
  }
  fuzz.register("whirlpoolQuote") {
    targetClass = "software.sava.idl.clients.orca.quote.WhirlpoolQuoteFuzz"
    // the harness carves a fixed 64-byte tuple (liquidity, price, ticks,
    // slippage, fees); every prefix is valid, so no seed corpus
    maxLen = 64
  }
  fuzz.register("dlmmPrice") {
    targetClass = "software.sava.idl.clients.meteora.dlmm.DlmmPriceFuzz"
    // (binStep, binId) packs into 7 bytes; the space is reachable from scratch —
    // the committed seeds are minimized findings pinning the truncation envelope
    maxLen = 8
    seedCorpus = layout.projectDirectory.dir("src/test/resources/fuzz/dlmmPrice")
  }
}

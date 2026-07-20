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
    "software.sava.idl.clients.*Fuzz",
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
    // fires zero times anywhere in this module.
    mutators = "STRONGER,EXPERIMENTAL_BIG_INTEGER"
    // quote math and tick/PDA derivation
    targetClasses = listOf("software.sava.idl.clients.orca.*")
    excludedClasses = notMutated
    targetTests = "software.sava.idl.clients.orca.*Test*"
  }
  mutation.register("scope") {
    // the oracle price readers: a fixed-layout account walked by offset, where a
    // wrong branch yields a plausible wrong price rather than a failure
    targetClasses = listOf("software.sava.idl.clients.kamino.scope.*")
    excludedClasses = notMutated
    targetTests = "software.sava.idl.clients.kamino.*Test*"
  }
  mutation.register("clients") {
    // BigInteger arithmetic is method calls, which MathMutator (primitive
    // bytecode ops) cannot reach — see config/pitest/README.md. Requires
    // pitest >= 1.25.8 on Java 25. BIG_DECIMAL is omitted deliberately: it
    // fires zero times anywhere in this module.
    mutators = "STRONGER,EXPERIMENTAL_BIG_INTEGER"
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
}

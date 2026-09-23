plugins {
  id("software.sava.build.feature.hardening")
}

testModuleInfo {
  requires("jdk.httpserver")
  // a test whose point is a parse failure silences sava-rpc's SEVERE log for the duration
  requires("java.logging")
  requires("org.junit.jupiter.api")
  runtimeOnly("org.junit.jupiter.engine")
}

dependencies {
  project(":idl-clients-spl")
}

// Published contents. Maven Central counts every published file and byte against the
// organization's monthly allowance, so a release carries only what a consumer of the
// library needs, and GitHub Packages receives the same bytes so one version resolves
// identically from either. Keep in sync with idl-clients-spl/build.gradle.kts;
// ModuleJarAuditTests, run by the moduleJarAudit task below, holds module-info's exports
// equal to the packages each jar carries, so an exclusion and an export cannot drift apart.
// - A staged "next" client previews an undeployed IDL and is never exported, but an
//   unexported package is still reachable on the class path, so the jar leaves it out.
// - The idl.json and channel records beside gen/ are generation provenance; the sources
//   jar carries no JSON at all (the binary jar never did).
// - Git-ignored Integ.java scratch mains stay out of a locally built jar, as they stay
//   out of the hardening recompile below; a CI release builds from a tag and has none.
// - The javadoc jar documents the hand-written layer only. Generated sources carry the
//   IDL's own documentation, some 22,700 /// lines here, and it ships in the sources jar,
//   where IDEs read it; rendered as HTML it made this jar 35 MB rather than 1.3 MB. The
//   compiled classes are patched into the module so hand-written pages still resolve
//   generated types, element-list is pruned to the packages that have pages so a -link
//   consumer renders generated types as text rather than dead links, and every page
//   says where the generated documentation is.
val unpublished = listOf(
  "**/next/gen/**",
)
tasks.jar {
  exclude(unpublished + listOf("**/Integ.class", "**/Integ\$*.class"))
  includeEmptyDirs = false
}
tasks.named<Jar>("sourcesJar") {
  exclude(unpublished + listOf("**/*.json", "**/Integ.java"))
  includeEmptyDirs = false
}
tasks.javadoc {
  exclude("**/gen/**", "**/Integ.java")
  // Already on the javadoc classpath, and so tracked as an input. Relative to the project
  // directory the javadoc tool runs in, so the option does not tie the cache key to a checkout.
  val moduleClasses = tasks.compileJava.get().destinationDirectory.get().asFile
  val docletOptions = options as StandardJavadocDocletOptions
  docletOptions.addStringOption(
    "-patch-module", "software.sava.idl.clients.bundle=${moduleClasses.relativeTo(projectDir).invariantSeparatorsPath}"
  )
  docletOptions.bottom = "Generated program packages (&hellip;gen) are documented in the sources jar, " +
      "which carries the IDL's own documentation; only the hand-written layer is documented here."
  doLast {
    val docs = (this as Javadoc).destinationDir!!
    val elementList = docs.resolve("element-list")
    var module = ""
    val documented = elementList.readLines().filter { line ->
      if (line.startsWith("module:")) {
        module = line.removePrefix("module:")
        true
      } else {
        docs.resolve(module).resolve(line.replace('.', '/')).listFiles()?.any {
          it.name.endsWith(".html") && it.name != "package-summary.html" && it.name != "package-tree.html"
        } == true
      }
    }
    elementList.writeText(documented.joinToString("\n", postfix = "\n"))
  }
}

// Nothing else in the build loads the bundle's own jar, and spl's is loaded only as a
// dependency; ModuleJarAuditTests says what a mismatch costs a consumer.
val moduleJarAudit = tasks.register<Test>("moduleJarAudit") {
  group = "verification"
  description = "Checks that module-info exports exactly the packages each published jar carries"
  val jars = files(tasks.jar.flatMap { it.archiveFile }, configurations.runtimeClasspath).filter {
    it.name.startsWith("idl-clients-")
  }
  inputs.files(jars)
  testClassesDirs = sourceSets.test.get().output.classesDirs
  classpath = sourceSets.test.get().runtimeClasspath
  useJUnitPlatform()
  filter { includeTestsMatching("software.sava.idl.clients.ModuleJarAuditTests") }
  jvmArgumentProviders.add(CommandLineArgumentProvider { listOf("-Dsava.moduleJars=" + jars.joinToString(",")) })
}
tasks.check { dependsOn(moduleJarAudit) }

hardening {
  // Ignored live-RPC scratch drivers stay available to the normal local compile,
  // but must not enter the PIT/Jazzer recompile or its production-class audit.
  recompileExcludes = listOf("Integ.java")

  // Suites are split for inner-loop speed, not for coverage: 'clients' is a
  // catch-all by exclusion, so a new hand-written class lands in some suite by
  // default. The shared exclusions drop generated code (owned by idl-src-gen),
  // test/fuzz sources sharing the recompiled root, and Integ classes; the
  // recompile exclusion above keeps ignored Integ sources out of that root.
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
  // The exclusion audit reports production classes a glob swallows, per suite, and
  // only against the FIRST glob that matches — so the generated-code argument has to
  // be repeated in each suite that swallows generated code, and only there. Every
  // other glob above swallows zero (the test/fuzz roots and ResourceUtil are
  // subtracted as test sources, Integ.java is absent from the tool recompile, and
  // orca.* and kamino.scope.* are sibling-owned), and a decline that matches nothing
  // is itself reported.
  val generatedDecline =
      "idl-src-gen output: per-program instruction builders, account and type " +
          "(de)serialization, PDA helpers and error enums generated from each program's IDL, " +
          "with the idl.json they were generated from committed beside them in gen/. Generated " +
          "Java sources carry '@generated by sava-software idl-src-gen — DO NOT EDIT' and are " +
          "never hand-edited here (AGENTS.md: fixes belong in the generator, then regenerate), " +
          "so a ratchet on them would pin code this repo cannot change. Correctness is carried " +
          "instead by the generator's own tests; by tools/GroundTruth.java, which diffs generated " +
          "account order against the program's Rust (docs/PROGRAM_VERIFICATION.md); and by " +
          "execution rather than mutation — the " +
          "test sources in this module build and decode through generated types, several of them " +
          "against account, instruction and event bytes captured from mainnet (Phoenix " +
          "MarketEvent payloads, Metaplex Core BaseAssetV1, Exponent), and two fuzz harnesses " +
          "round-trip them (RouteV2DataFuzz through jupiter.swap.gen, ScopeReaderFuzz through " +
          "kamino.scope.gen.types.OracleMappings). The hand-written layer that interprets them " +
          "stays in the mutant population."
  mutation.register("orca") {
    // BigInteger arithmetic is method calls, which MathMutator (primitive
    // bytecode ops) cannot reach — see config/pitest/README.md. Requires
    // pitest >= 1.25.8 on Java 25. BIG_DECIMAL is omitted because these classes
    // hold no BigDecimal arithmetic at all — the blind-spot scan finds nothing
    // to advise here, so there is nothing to decline either. NAKED_RECEIVER
    // makes dropped fluent calls (receiver-returning expressions) expressible —
    // trial numbers in config/pitest/README.md.
    mutators = "STRONGER,EXPERIMENTAL_BIG_INTEGER,EXPERIMENTAL_NAKED_RECEIVER"
    // quote math and tick/PDA derivation
    targetClasses = listOf("software.sava.idl.clients.orca.*")
    excludedClasses = notMutated
    // 41 orca.whirlpools.gen.* classes; no orca Integ.java, so no Integ decline here
    declineExclusionAudit("software.sava.idl.clients.*.gen.*", generatedDecline)
    targetTests = "software.sava.idl.clients.orca.*Test*"
    excludeTestClass(
      "software.sava.idl.clients.orca.OrcaTickMarginSweepTests",
      "Accepted-equivalence sweep: it runs under the normal test task, but its roughly " +
          "900k-iteration work must not run against every Orca mutant or distort the " +
          "suite's audited timeout budget."
    )
    excludeTestClass(
      "software.sava.idl.clients.orca.OrcaSqrtFloorSweepTests",
      "Accepted-equivalence sweep: it runs under the normal test task, but its roughly " +
          "900k-iteration work must not run against every Orca mutant or distort the " +
          "suite's audited timeout budget."
    )
  }
  mutation.register("scope") {
    // the oracle price readers: a fixed-layout account walked by offset, where a
    // wrong branch yields a plausible wrong price rather than a failure.
    // NAKED_RECEIVER fired 6 times at zero baseline cost — see
    // config/pitest/README.md.
    mutators = "STRONGER,EXPERIMENTAL_NAKED_RECEIVER"
    targetClasses = listOf("software.sava.idl.clients.kamino.scope.*")
    excludedClasses = notMutated
    // 45 kamino.scope.gen.* classes; Integ.java stays out of the tool recompile
    declineExclusionAudit("software.sava.idl.clients.*.gen.*", generatedDecline)
    targetTests = "software.sava.idl.clients.kamino.*Test*"
  }
  mutation.register("clients") {
    // BigInteger and BigDecimal arithmetic are method calls, which MathMutator
    // (primitive bytecode ops) cannot reach — see config/pitest/README.md.
    // Requires pitest >= 1.25.8 on Java 25. NAKED_RECEIVER makes dropped fluent
    // calls (receiver-returning expressions) expressible — trial numbers in
    // config/pitest/README.md.
    mutators = "STRONGER,EXPERIMENTAL_BIG_INTEGER,EXPERIMENTAL_BIG_DECIMAL,EXPERIMENTAL_NAKED_RECEIVER"
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
    // every bundled program's gen.* (the orca and kamino.scope ones included, since
    // no suite mutates those either); Integ.java stays out of the tool recompile
    declineExclusionAudit("software.sava.idl.clients.*.gen.*", generatedDecline)
    targetTests = "software.sava.idl.clients.*Test*"
  }
  fuzz.register("routeV2") {
    targetClass = "software.sava.idl.clients.jupiter.swap.RouteV2DataFuzz"
    // route instructions are a few hundred bytes on-chain; the harness supplies
    // the discriminator, so the budget is all payload. Seeded with real mainnet
    // route payloads: the Swap enum has ~150 ordinals, most carrying their own
    // fields, so a valid route plan is slow to reach from scratch
    maxLen = 1024
    seedCorpus = layout.projectDirectory.dir("src/test/resources/fuzz/routeV2")
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
    // so the corpus pins the domain edges and holds findings rather than
    // bootstrapping coverage
    maxLen = 32
    seedCorpus = layout.projectDirectory.dir("src/test/resources/fuzz/orcaTickMath")
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
    // slippage, fees); every prefix is valid, so the corpus pins the position
    // shapes and holds findings rather than bootstrapping coverage
    maxLen = 64
    seedCorpus = layout.projectDirectory.dir("src/test/resources/fuzz/whirlpoolQuote")
  }
  fuzz.register("dlmmPrice") {
    targetClass = "software.sava.idl.clients.meteora.dlmm.DlmmPriceFuzz"
    // (binStep, binId) packs into 7 bytes; the space is reachable from scratch —
    // the committed seeds are minimized findings pinning the truncation envelope
    maxLen = 8
    seedCorpus = layout.projectDirectory.dir("src/test/resources/fuzz/dlmmPrice")
  }
}

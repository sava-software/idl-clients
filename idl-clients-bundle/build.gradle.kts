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
  mutation.register("orcaQuotes") {
    targetClasses = listOf(
      "software.sava.idl.clients.orca.quote.WhirlpoolQuote",
      "software.sava.idl.clients.orca.OrcaUtil"
    )
    targetTests = "software.sava.idl.clients.orca.*Test*"
  }
  mutation.register("utils") {
    targetClasses = listOf(
      "software.sava.idl.clients.meteora.dlmm.DlmmUtils",
      "software.sava.idl.clients.kamino.KaminoUtil",
      "software.sava.idl.clients.oracles.OracleUtil",
      "software.sava.idl.clients.jupiter.swap.JupiterSwapUtil"
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

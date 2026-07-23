plugins {
  id("software.sava.build.feature.hardening")
}

testModuleInfo {
  requires("org.junit.jupiter.api")
  runtimeOnly("org.junit.jupiter.engine")
}

hardening {
  mutation.register("spl") {
    // catch-all by exclusion, so a new hand-written class is mutated by default
    // instead of silently skipped — the whole module is account encode/decode
    // and instruction building, which is exactly the money-critical shape.
    // NAKED_RECEIVER makes dropped fluent calls (receiver-returning
    // expressions) expressible — trial numbers in config/pitest/README.md.
    mutators = "STRONGER,EXPERIMENTAL_NAKED_RECEIVER"
    targetClasses = listOf("software.sava.idl.clients.spl.*")
    excludedClasses = listOf(
      // generated per-program code: correctness belongs to idl-src-gen, and
      // mutating the boilerplate would bury the hand-written signal
      "software.sava.idl.clients.*.gen.*",
      // test and fuzz sources share the recompiled root
      "software.sava.idl.clients.spl.*Test*",
      "software.sava.idl.clients.spl.*Fuzz*",
      // 'Integ.*' scratch files are git-ignored: present on a dev machine and
      // absent in CI, so mutating them would make the baseline machine-dependent
      "software.sava.idl.clients.*.Integ"
    )
    targetTests = "software.sava.idl.clients.spl.*Test*"
  }
  fuzz.register("validatorList") {
    targetClass = "software.sava.idl.clients.spl.stakepool.ValidatorListFuzz"
    // header (9) plus a few dozen 73-byte validator entries reaches every parse
    // path; larger inputs only slow executions down
    maxLen = 4096
  }
  fuzz.register("stakePoolState") {
    targetClass = "software.sava.idl.clients.spl.stakepool.StakePoolStateFuzz"
    // the packed account is ~600 bytes; a little headroom lets the mutator probe the
    // option-byte branches past a seed. Seeded with a real mainnet account
    maxLen = 1024
    seedCorpus = layout.projectDirectory.dir("src/test/resources/fuzz/stakePoolState")
  }
  fuzz.register("precompileOffsets") {
    targetClass = "software.sava.idl.clients.spl.precompiles.PrecompileOffsetsFuzz"
    // a u8 count (max 255) with 11/14-byte records: 2 + 255*14 ~= 3.5KB covers every
    // record; the whole space is reachable from scratch, so no seed corpus
    maxLen = 256
  }
}

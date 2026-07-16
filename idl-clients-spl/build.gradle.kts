plugins {
  id("software.sava.build.feature.hardening")
}

testModuleInfo {
  requires("org.junit.jupiter.api")
  runtimeOnly("org.junit.jupiter.engine")
}

hardening {
  mutation.register("encoders") {
    targetClasses = listOf(
      "software.sava.idl.clients.spl.precompiles.SignatureVerifyProgram",
      "software.sava.idl.clients.spl.token_2022.Token2022Instructions",
      "software.sava.idl.clients.spl.stakepool.StakePoolProgram",
      "software.sava.idl.clients.spl.stakepool.StakePoolState",
      "software.sava.idl.clients.spl.stakepool.ValidatorList",
      "software.sava.idl.clients.spl.stakepool.ValidatorStakeInfo"
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

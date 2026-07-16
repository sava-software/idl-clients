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
}

testModuleInfo {
  requires("org.junit.jupiter.api")
  runtimeOnly("org.junit.jupiter.engine")
}

dependencies {
  project(":idl-clients-core")
  project(":idl-clients-spl")

//  project(":sava:sava-core")
//  project(":sava:sava-rpc")
}

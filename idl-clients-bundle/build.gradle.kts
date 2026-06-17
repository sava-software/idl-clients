testModuleInfo {
  requires("jdk.httpserver")
  requires("org.junit.jupiter.api")
  runtimeOnly("org.junit.jupiter.engine")
}

dependencies {
  project(":idl-clients-spl")
}

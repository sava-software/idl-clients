plugins {
  id("software.sava.build.feature.publish-maven-central")
}

val idlClientModules = setOf(
  "idl-clients-bundle",
  "idl-clients-spl"
)

dependencies {
  for (module in idlClientModules) {
    centralPortalAggregation(project(":$module"))
  }
}

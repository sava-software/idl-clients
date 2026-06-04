plugins {
  id("software.sava.build.feature.publish-maven-central")
}

val idlClientModules = setOf(
  "idl-clients-cctp",
  "idl-clients-core",
  "idl-clients-drift",
  "idl-clients-jupiter",
  "idl-clients-kamino",
  "idl-clients-loopscale",
  "idl-clients-marinade",
  "idl-clients-metaplex",
  "idl-clients-meteora",
  "idl-clients-neutral_trade",
  "idl-clients-oracles",
  "idl-clients-orca",
  "idl-clients-phoenix",
  "idl-clients-spl",
  "idl-clients-squads",
)

dependencies {
  for (module in idlClientModules) {
    nmcpAggregation(project(":$module"))
  }
}

tasks.register("publishToGitHubPackages") {
  group = "publishing"
  val publishTasks = idlClientModules.map { ":$it:publishMavenJavaPublicationToSavaGithubPackagesPublishRepository" }
  dependsOn(publishTasks)
}

plugins {
  id("software.sava.build.feature.publish-maven-central")
}

val idlClientModules = setOf(
  "idl-clients-cctp",
  "idl-clients-core",
  "idl-clients-drift",
  "idl-clients-jupiter",
  "idl-clients-kamino",
  "idl-clients-marinade",
  "idl-clients-metaplex",
  "idl-clients-meteora",
  "idl-clients-oracles",
  "idl-clients-spl",
)

val selectedModule: String? = providers
  .gradleProperty("nmcp.publish.project")
  .orElse(providers.environmentVariable("NMCP_PUBLISH_PROJECT"))
  .orNull

dependencies {
  for (module in idlClientModules) {
    nmcpAggregation(project(":$module"))
  }
}

tasks.register("publishToGitHubPackages") {
  group = "publishing"
  val publishTasks = idlClientModules.map { ":$it:publishMavenJavaPublicationToSavaGithubPackagesRepository" }
  dependsOn(publishTasks)
}

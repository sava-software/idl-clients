plugins {
  id("software.sava.build.feature.publish-maven-central")
}

val selectedModule: String? = providers
  .gradleProperty("nmcp.publish.project")
  .orElse(providers.environmentVariable("NMCP_PUBLISH_PROJECT"))
  .orNull

dependencies {
  if (selectedModule != null) {
    nmcpAggregation(project(selectedModule.takeIf { it.startsWith(":") } ?: ":idl-clients-$selectedModule"))
  } else {
    nmcpAggregation(project(":idl-clients-core"))
  }
}

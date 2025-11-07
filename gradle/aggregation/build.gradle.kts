plugins {
  id("software.sava.build.feature.publish-maven-central")
}

dependencies {
  nmcpAggregation(project(":idl-clients-cctp"))
  nmcpAggregation(project(":idl-clients-core"))
  nmcpAggregation(project(":idl-clients-drift"))
  nmcpAggregation(project(":idl-clients-jupiter"))
  nmcpAggregation(project(":idl-clients-kamino"))
  nmcpAggregation(project(":idl-clients-oracles"))
  nmcpAggregation(project(":idl-clients-spl"))
}

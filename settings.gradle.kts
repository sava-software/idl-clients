rootProject.name = "idl-clients"

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    if (
      providers.gradleProperty("savaGithubPackagesUsername").isPresent &&
      providers.gradleProperty("savaGithubPackagesPassword").isPresent
    ) {
      maven {
        name = "savaGithubPackages"
        url = uri("https://maven.pkg.github.com/sava-software/sava-build")
        credentials(PasswordCredentials::class)
      }
    }
//  includeBuild("../sava-build")
  }
}

plugins {
  id("software.sava.build") version "21.3.2"
}

apply(plugin = "software.sava.build.feature-jdk-provisioning")

javaModules {
  directory(".") {
    group = "software.sava"
    plugin("software.sava.build.java-module")
  }
}

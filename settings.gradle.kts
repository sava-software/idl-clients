rootProject.name = "idl-clients"

pluginManagement {
  // Point '-PsavaBuildLocalRepo=<sava-build>/build/sava-test-repo' (or set it in
  // ~/.gradle/gradle.properties) at a local sava-build checkout to build against an
  // unpublished plugin change; sava-build publishes that repo with
  //   ./gradlew publishSavaBuildTestPublicationToSavaTestRepoRepository
  // and every id below then resolves to the 0.0.0-test module regardless of the
  // version the plugins block requests — which the plugin announces at the end of
  // each build, so this block stays silent. The useModule call also bypasses plugin
  // markers, which the test repo does not contain. Pass '--dependency-verification
  // lenient' with the property, on every command: the test repo's artifacts are unsigned
  // local build output, so the strict verification policy (gradle/verification-metadata.xml)
  // refuses them, and lenient reports the refusals instead of failing. Never relax the mode
  // in a committed or user-wide gradle.properties, and never add trust for 0.0.0-test.
  val savaBuildLocalRepo = providers.gradleProperty("savaBuildLocalRepo")
    .orNull?.takeIf { it.isNotBlank() }
  if (savaBuildLocalRepo != null) {
    resolutionStrategy.eachPlugin {
      if (requested.id.id.startsWith("software.sava.build")) {
        useModule("software.sava:sava-build:0.0.0-test")
      }
    }
  }
  repositories {
    if (savaBuildLocalRepo != null) {
      maven(url = savaBuildLocalRepo) {
        content {
          includeModule("software.sava", "sava-build")
          includeGroupByRegex("software[.]sava[.]build($|[.].*)")
        }
      }
    }
    gradlePluginPortal()
    mavenCentral()
    val gprUser = providers.gradleProperty("savaGithubPackagesUsername")
      .orNull?.takeIf { it.isNotBlank() }
    val gprToken = providers.gradleProperty("savaGithubPackagesPassword")
      .orNull?.takeIf { it.isNotBlank() }
    if (gprUser != null && gprToken != null) {
      maven {
        name = "savaGithubPackages"
        url = uri("https://maven.pkg.github.com/sava-software/sava-build")
        content {
          includeModule("software.sava", "sava-build")
          includeGroupByRegex("software[.]sava[.]build($|[.].*)")
        }
        credentials {
          username = gprUser
          password = gprToken
        }
      }
    }
  }
}

// sava-build does not ship the dependency-analysis plugin. '-PdependencyAudit' puts it on the
// settings classpath for that invocation alone, where sava-build's check.dependencies wires
// 'checkModuleDirectivesScope' into 'check'; an ordinary build, a release build among them,
// resolves and loads nothing of it. The version is the one sava-build is tested against
// ('dependency-analysis-gradle-plugin' in its gradle/libs.versions.toml).
buildscript {
  if (providers.gradleProperty("dependencyAudit").isPresent) {
    dependencies { classpath("com.autonomousapps:dependency-analysis-gradle-plugin:3.20.4") }
  }
}

plugins {
  id("software.sava.build") version "21.8.9"
  id("software.sava.build.feature.jdk-provisioning") version "21.8.9"
}

if (providers.gradleProperty("dependencyAudit").isPresent) {
  apply(plugin = "com.autonomousapps.build-health")
}

javaModules {
  directory(".") {
    group = "software.sava"
    plugin("software.sava.build.java-module")
    // idl-clients-drift is dead code: Drift's programs no longer work on-chain.
    // Assigning replaces the 'build' and '.*' conventions, so they are restated.
    exclusions = listOf("build", "\\..*", "idl-clients-drift")
  }
}

//includeBuild("../sava")
//includeBuild("../json-iterator")

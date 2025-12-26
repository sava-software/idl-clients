# IDL Clients [![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/sava-software/idl-clients) [![Gradle Check](https://github.com/sava-software/idl-clients/actions/workflows/build.yml/badge.svg)](https://github.com/sava-software/idl-clients/actions/workflows/build.yml) [![Publish Release](https://github.com/sava-software/idl-clients/actions/workflows/publish-all.yml/badge.svg)](https://github.com/sava-software/idl-clients/actions/workflows/publish-all.yml)

Generated source to (de)serialize instructions and accounts for common Solana programs, as well as convenience methods
and clients to ease integration.

## Build

[Generate a classic token](https://github.com/settings/tokens) with the `read:packages` scope needed to access
dependencies hosted on GitHub Package Repository.

#### ~/.gradle/gradle.properties

```properties
savaGithubPackagesUsername=GITHUB_USERNAME
savaGithubPackagesPassword=GITHUB_TOKEN
```

```shell
./gradlew check
```

# IDL Clients [![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/sava-software/idl-clients) [![Gradle Check](https://github.com/sava-software/idl-clients/actions/workflows/build.yml/badge.svg)](https://github.com/sava-software/idl-clients/actions/workflows/build.yml)

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

## Primitive Widening

| Rust  | Scalar | Collection | Java type | Java collection |
|-------|--------|------------|-----------|-----------------|
| `u8`  | ✅      | ❌          | `int`     | `byte[]`        |
| `u16` | ✅      | ✅          | `int`     | `int[]`         |
| `u32` | ✅      | ✅          | `long`    | `long[]`        |
| `u64` | ❌      | ❌          | `long`    | `long[]`        |

- For **`u64`** use the  `SerDeUtil.toUnsignedBigInteger(long)` convenience method to get an unsigned
  `java.math.BigInteger`, or the JDK helpers `Long.toUnsignedString`, `Long.compareUnsigned`, and `Long.divideUnsigned`.
- Unlike scalar **`u8`** values, **`u8`** arrays and vectors usually model raw bytes or opaque data, so preserving the
  compact byte representation is more useful than widening each element.

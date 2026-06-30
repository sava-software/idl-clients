# IDL Clients [![Gradle Check](https://github.com/sava-software/idl-clients/actions/workflows/build.yml/badge.svg)](https://github.com/sava-software/idl-clients/actions/workflows/build.yml)

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

## Rust to Java Type Conversion

Solana programs are written in Rust, so an IDL describes a program's data using Rust types. The generated Java
source maps each Rust type to the most appropriate Java type. The tables below describe how each Rust type is
represented in Java.

Because Java has no unsigned integer types, unsigned Rust types are widened to the next-larger signed Java type so
their full value range is preserved — see [Primitive Widening](#primitive-widening) below for details and helpers.

### Booleans

| Rust   | Java      |
|--------|-----------|
| `bool` | `boolean` |

### Numbers

Scalar number fields map to the following Java types.

| Rust            | Java                   |
|-----------------|------------------------|
| `i8`            | `int`                  |
| `u8`            | `int`                  |
| `i16`           | `int`                  |
| `u16`           | `int`                  |
| `i32`           | `int`                  |
| `u32`           | `long`                 |
| `i64`           | `long`                 |
| `u64`           | `long`                 |
| `usize`         | `long`                 |
| `i128` / `u128` | `java.math.BigInteger` |
| `i256` / `u256` | `java.math.BigInteger` |
| `f32`           | `float`                |
| `f64`           | `double`               |

- `u64` and `usize` have no larger signed Java integer to widen into, so the `long` must be treated as unsigned
  (see [Primitive Widening](#primitive-widening)).

### Strings

| Rust     | Java     |
|----------|----------|
| `String` | `String` |

- Each `String` field is paired with a generated `byte[] _name` companion holding the raw UTF-8 bytes.

### Public Keys

| Rust     | Java                                      |
|----------|-------------------------------------------|
| `pubkey` | `software.sava.core.accounts.PublicKey`   |

### Bytes

| Rust    | Java     |
|---------|----------|
| `bytes` | `byte[]` |

### Collections

Fixed arrays (`[T; N]`) and vectors (`Vec<T>`) are both represented as Java arrays. Element types follow the same
widening rules as scalar fields, except that `u8` / `i8` collections preserve their compact `byte[]` representation
rather than widening each element (see [Primitive Widening](#primitive-widening)).

| Rust element            | `[T; N]` / `Vec<T>` |
|-------------------------|---------------------|
| `i8` / `u8`             | `byte[]`            |
| `i16`                   | `short[]`           |
| `u16` / `i32`           | `int[]`             |
| `u32` / `i64` / `u64`   | `long[]`            |
| `i128` / `u128`         | `BigInteger[]`      |
| `i256` / `u256`         | `BigInteger[]`      |
| `f32`                   | `float[]`           |
| `f64`                   | `double[]`          |
| `bool`                  | `boolean[]`         |
| `pubkey`                | `PublicKey[]`       |
| `String`                | `String[]`          |
| defined type `T`        | `T[]`               |

### Optional Values

`Option<T>` maps to a Java type that depends on the inner type:

| Rust                                                                            | Java                       |
|---------------------------------------------------------------------------------|----------------------------|
| `Option<i8>` / `Option<u8>` / `Option<i16>` / `Option<u16>` / `Option<i32>`     | `OptionalInt`              |
| `Option<u32>` / `Option<i64>` / `Option<u64>` / `Option<usize>`                 | `OptionalLong`             |
| `Option<f32>` / `Option<f64>`                                                   | `OptionalDouble`           |
| `Option<T>` for reference types (`BigInteger`, `PublicKey`, `String`, `bool`, structs, enums) | nullable `T` (`null` = `None`) |

### Complex Types

| Rust     | Java                                                                       |
|----------|----------------------------------------------------------------------------|
| `struct` | generated `record` implementing the (de)serialization interface            |
| `enum`   | generated type modeling each variant (unit, tuple, and named variants)     |

## Primitive Widening

To support the full numerical range of Rust types, the following primitive widening conversions are made:

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

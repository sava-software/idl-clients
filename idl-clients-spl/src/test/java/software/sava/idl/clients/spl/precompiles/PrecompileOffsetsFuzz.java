package software.sava.idl.clients.spl.precompiles;

import java.util.Arrays;

/// Jazzer entry point for the three native precompile offset parsers (Ed25519, Secp256k1,
/// Secp256r1). Each reads a `u8 count` from attacker bytes, allocates `count` records, and
/// loops fixed-size reads — so truncation and a count larger than the buffer can back are
/// the hostile cases.
///
/// Malformed-input contract: garbage in -> [RuntimeException] out. An
/// `ArrayIndexOutOfBoundsException` from a truncated buffer is tolerated; Jazzer flags any
/// non-[RuntimeException] throwable, plus the invariants that must hold on a successful
/// parse — the array length equals the header count, re-parsing is deterministic, and each
/// element re-derives from its own computed offset.
///
/// The header count is a single byte (max 255) with fixed 11/14-byte records, so the whole
/// input space is reachable from scratch; no seed corpus is needed.
///
/// Deliberately free of Jazzer imports so it compiles with the regular test sources.
///
/// Run with `./gradlew :idl-clients-spl:fuzzPrecompileOffsets [-PmaxFuzzTime=<seconds>]`.
public final class PrecompileOffsetsFuzz {

  public static void fuzzerTestOneInput(final byte[] data) {
    if (data.length == 0) {
      return;
    }
    final int count = data[0] & 0xFF;

    fuzzEd25519(data, count);
    fuzzSecp256k1(data, count);
    fuzzSecp256r1(data, count);
  }

  private static void fuzzEd25519(final byte[] data, final int count) {
    final Ed25519Offsets[] offsets;
    try {
      offsets = Ed25519Offsets.parseOffsets(data, 0);
    } catch (final RuntimeException tolerated) {
      return;
    }
    checkLength(offsets.length, count, "Ed25519");
    if (!Arrays.equals(offsets, Ed25519Offsets.parseOffsets(data, 0))) {
      throw new AssertionError("Ed25519 parseOffsets is not deterministic");
    }
    for (int s = 0; s < offsets.length; ++s) {
      final int recordOffset = SignatureVerifyProgram.ED25519_DATA_HEADER_LEN
          + (s * SignatureVerifyProgram.ED25519_OFFSETS_LEN);
      if (!offsets[s].equals(Ed25519Offsets.read(data, recordOffset))) {
        throw new AssertionError("Ed25519 element " + s + " disagrees with read() at its offset");
      }
    }
  }

  private static void fuzzSecp256k1(final byte[] data, final int count) {
    final Secp256k1Offsets[] offsets;
    try {
      offsets = Secp256k1Offsets.parseOffsets(data, 0);
    } catch (final RuntimeException tolerated) {
      return;
    }
    checkLength(offsets.length, count, "Secp256k1");
    if (!Arrays.equals(offsets, Secp256k1Offsets.parseOffsets(data, 0))) {
      throw new AssertionError("Secp256k1 parseOffsets is not deterministic");
    }
    for (int s = 0; s < offsets.length; ++s) {
      final int recordOffset = SignatureVerifyProgram.SECP256K1_DATA_HEADER_LEN
          + (s * SignatureVerifyProgram.SECP256K1_OFFSETS_LEN);
      if (!offsets[s].equals(Secp256k1Offsets.read(data, recordOffset))) {
        throw new AssertionError("Secp256k1 element " + s + " disagrees with read() at its offset");
      }
    }
  }

  private static void fuzzSecp256r1(final byte[] data, final int count) {
    final Secp256r1Offsets[] offsets;
    try {
      offsets = Secp256r1Offsets.parseOffsets(data, 0);
    } catch (final RuntimeException tolerated) {
      return;
    }
    checkLength(offsets.length, count, "Secp256r1");
    if (!Arrays.equals(offsets, Secp256r1Offsets.parseOffsets(data, 0))) {
      throw new AssertionError("Secp256r1 parseOffsets is not deterministic");
    }
    for (int s = 0; s < offsets.length; ++s) {
      final int recordOffset = SignatureVerifyProgram.SECP256R1_DATA_HEADER_LEN
          + (s * SignatureVerifyProgram.SECP256R1_OFFSETS_LEN);
      if (!offsets[s].equals(Secp256r1Offsets.read(data, recordOffset))) {
        throw new AssertionError("Secp256r1 element " + s + " disagrees with read() at its offset");
      }
    }
  }

  private static void checkLength(final int actual, final int count, final String name) {
    if (actual != count) {
      throw new AssertionError(name + " parsed " + actual + " records but the header count is " + count);
    }
  }

  private PrecompileOffsetsFuzz() {
  }
}

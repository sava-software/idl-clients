package software.sava.idl.clients.spl.stakepool;

import software.sava.core.encoding.ByteUtil;

import java.util.Arrays;

/// Jazzer entry point exercising the stake-pool ValidatorList account parser: any input
/// either parses and re-serializes byte-identically, or is rejected with a runtime
/// exception.
///
/// Deliberately has no Jazzer imports so it compiles with the regular test sources;
/// the raw `byte[]` signature is all the driver needs.
///
/// Run with `./gradlew :idl-clients-spl:fuzzValidatorList [-PmaxFuzzTime=<seconds>]`.
public final class ValidatorListFuzz {

  private static final int HEADER_LENGTH = 1 + Integer.BYTES + Integer.BYTES;

  public static void fuzzerTestOneInput(final byte[] data) {
    if (data.length >= HEADER_LENGTH) {
      // the parser trusts the in-band u32 count and allocates before any bounds check,
      // so a wild count burns hundreds of MB per execution just to fail on the first
      // element reads. Counts modestly beyond what the data can back still exercise the
      // truncation path; anything larger only throttles the fuzzer.
      final int numValidators = ByteUtil.getInt32LE(data, 1 + Integer.BYTES);
      if (numValidators < 0 || numValidators > (data.length / ValidatorStakeInfo.BYTES) + 2) {
        return;
      }
    }
    final ValidatorList list;
    try {
      list = ValidatorList.read(data, 0);
    } catch (final IndexOutOfBoundsException rejected) {
      // truncated data, an out-of-range AccountType/StakeStatus ordinal, or a count
      // the buffer cannot back — rejection is the documented behavior today
      return;
    }
    // accepted input must re-serialize byte-identically over the consumed prefix. The
    // prefix may be shorter than l(): readPubKey in sava-core releases up to the
    // current BOM zero-pads a truncated trailing key, so truncation inside the final
    // vote-account key is silently accepted rather than rejected. Fixed upstream to
    // throw IndexOutOfBoundsException; the min() below stays correct either way.
    final int length = list.l();
    final byte[] written = new byte[length];
    final int wrote = list.write(written, 0);
    if (wrote != length) {
      throw new IllegalStateException("write returned " + wrote + " but l() is " + length);
    }
    final int consumed = Math.min(length, data.length);
    if (!Arrays.equals(written, 0, consumed, data, 0, consumed)) {
      throw new IllegalStateException("write(read(data)) is not canonical");
    }
    // and writing at a shifted offset must produce the same bytes
    final byte[] shifted = new byte[length + 7];
    final int shiftedWrote = list.write(shifted, 7);
    if (shiftedWrote != length || !Arrays.equals(shifted, 7, 7 + length, written, 0, length)) {
      throw new IllegalStateException("write at a non-zero offset disagrees");
    }
    // a re-read of the canonical bytes must reproduce every field
    final var reRead = ValidatorList.read(written, 0);
    if (reRead.accountType() != list.accountType()
        || reRead.maxValidators() != list.maxValidators()
        || !Arrays.equals(reRead.validators(), list.validators())) {
      throw new IllegalStateException("read(write(list)) does not round trip");
    }
  }

  private ValidatorListFuzz() {
  }
}

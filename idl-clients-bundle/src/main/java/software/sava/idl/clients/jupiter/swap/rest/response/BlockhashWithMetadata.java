package software.sava.idl.clients.jupiter.swap.rest.response;

import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;
import systems.comodal.jsoniter.ValueType;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.function.Supplier;

import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

/// The blockhash a `/build` response was assembled against.
///
/// @param blockhash            exactly 32 bytes as `Transaction.sign(byte[], Signer)` takes them; null when Jupiter omits it
/// @param lastValidBlockHeight the last block height at which a transaction using it can land; 0 when absent
/// @param fetchedAt            when Jupiter fetched it; null when absent, as the docs' own type omits it, and null for
///                             a time outside `Instant`'s range whose numbers fit a long
public record BlockhashWithMetadata(byte[] blockhash, long lastValidBlockHeight, Instant fetchedAt) {

  /// Parses the object; unknown fields are skipped.
  ///
  /// The numbers are serde integers from Jupiter's Rust router, so each must be integral; `7.0`
  /// and `"7"` are accepted. An absent, JSON null or `""` `secs_since_epoch` gives a null
  /// `fetchedAt`, and an absent, JSON null or `""` `nanos_since_epoch` reads as 0. A `fetchedAt`
  /// outside `Instant`'s range reads as null too, as long as each of its numbers fits a long: it is
  /// informational, so it never costs the blockhash or the rest of the response. No serde
  /// `SystemTime` carries a number beyond a long, so one throws ArithmeticException, as a fraction
  /// does.
  ///
  /// @throws IllegalStateException if a `blockhash` array has other than 32 elements, or an element that is
  ///                               JSON null, `""` or an int outside 0..255
  /// @throws ArithmeticException   if a number is fractional or beyond a long, or a `blockhash` element beyond an int
  /// @throws NumberFormatException if a number is one `BigDecimal` cannot parse
  /// @throws systems.comodal.jsoniter.JsonException if `blockhash` is neither an array nor JSON null, or a number
  ///                                                is a boolean, object or array
  public static BlockhashWithMetadata parse(final JsonIterator ji) {
    return ji.parseObject(new Parser());
  }

  private static Instant parseFetchedAt(final JsonIterator ji) {
    return ji.parseObject(new FetchedAtParser());
  }

  private static final class Parser implements FieldBufferPredicate, Supplier<BlockhashWithMetadata> {

    private byte[] blockhash;
    private long lastValidBlockHeight;
    private Instant fetchedAt;

    private Parser() {
    }

    @Override
    public BlockhashWithMetadata get() {
      return new BlockhashWithMetadata(blockhash, lastValidBlockHeight, fetchedAt);
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("blockhash", buf, offset, len)) {
        blockhash = SwapV2Json.readBlockhash(ji);
      } else if (fieldEquals("lastValidBlockHeight", buf, offset, len)) {
        lastValidBlockHeight = SwapV2Json.readIntegral(ji);
      } else if (fieldEquals("fetchedAt", buf, offset, len)) {
        fetchedAt = ji.readOrNull(ValueType.OBJECT, BlockhashWithMetadata::parseFetchedAt);
      } else {
        ji.skip();
      }
      return true;
    }
  }

  /// A serde `SystemTime`: `{"secs_since_epoch": n, "nanos_since_epoch": n}`.
  private static final class FetchedAtParser implements FieldBufferPredicate, Supplier<Instant> {

    private boolean hasSeconds;
    private long seconds;
    private long nanos;

    private FetchedAtParser() {
    }

    /// Null without a `secs_since_epoch` value, and for a time outside `Instant`'s range:
    /// `ofEpochSecond` throws DateTimeException past either end of the range, and
    /// ArithmeticException when the nanosecond carry overflows a long.
    @Override
    public Instant get() {
      if (!hasSeconds) {
        return null;
      }
      try {
        return Instant.ofEpochSecond(seconds, nanos);
      } catch (final DateTimeException | ArithmeticException outsideInstantsRange) {
        return null;
      }
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("secs_since_epoch", buf, offset, len)) {
        final var secs = ji.readNumberOrNumberString();
        hasSeconds = secs != null && !secs.isEmpty();
        if (hasSeconds) {
          seconds = new BigDecimal(secs).longValueExact();
        }
      } else if (fieldEquals("nanos_since_epoch", buf, offset, len)) {
        nanos = SwapV2Json.readIntegral(ji);
      } else {
        ji.skip();
      }
      return true;
    }
  }
}

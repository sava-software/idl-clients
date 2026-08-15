package software.sava.idl.clients.core.gen;

import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/// An empty optional vector goes on the wire as absent, on purpose.
///
/// Borsh distinguishes `None` (`00`) from `Some(vec![])` (`01 00 00 00 00`). `SerDeUtil` collapses
/// the second into the first: four bytes saved against a 1232-byte transaction packet, for a
/// distinction the programs this generator targets do not draw. Every call site today is a Squads
/// `memo` — fourteen of them — where an absent memo and a present empty memo are the same thing.
///
/// This is written down because it does not look deliberate from the inside. A reader who finds
/// `l()` returning 1 for a non-null value, or who writes `Some("")` and reads back `null`, has
/// every reason to file it as a round-trip bug. It is a decision, and this test is where the
/// decision lives.
///
/// **When it would stop being right.** A program that distinguishes absent from present-but-empty
/// — `None` meaning *leave this field alone* and `Some(empty)` meaning *clear it*. Stake's
/// `SetLockup` has exactly that shape, where a null timestamp means "do not change the lockup"
/// rather than "set it to zero". No optional vector in the corpus is that shape today. If one
/// appears, this rule has to become per-field rather than a property of `SerDeUtil`, because the
/// collapse would then send "leave it alone" when the caller asked to clear it.
final class SerDeUtilOptionalEmptyTests {

  private static final int OPTIONAL_BYTES = 1;
  private static final int PREFIX_BYTES = 4;

  @Test
  void anEmptyVectorIsWrittenAsAbsent() {
    final byte[] data = new byte[16];
    final int written = SerDeUtil.writeOptionalVector(OPTIONAL_BYTES, PREFIX_BYTES, "".getBytes(UTF_8), data, 0);

    assertEquals(1, written, "one presence byte and nothing else");
    assertEquals(0, data[0], "the presence byte says absent");
    assertEquals(1, SerDeUtil.lenOptionalVector(OPTIONAL_BYTES, PREFIX_BYTES, "".getBytes(UTF_8)),
        "l() has to agree with what write() emits, or the caller under-allocates");

    // null takes the same path and produces the same byte, which is the point.
    final byte[] viaNull = new byte[16];
    assertEquals(written, SerDeUtil.writeOptionalVector(OPTIONAL_BYTES, PREFIX_BYTES, null, viaNull, 0));
    assertArrayEquals(data, viaNull, "an empty value and no value are indistinguishable on the wire");
  }

  @Test
  void aNonEmptyVectorCarriesItsTagLengthAndBytes() {
    final byte[] data = new byte[16];
    final int written = SerDeUtil.writeOptionalVector(OPTIONAL_BYTES, PREFIX_BYTES, "hi".getBytes(UTF_8), data, 0);

    assertEquals(1 + 4 + 2, written);
    assertEquals(1, data[0], "present");
    assertEquals(2, data[1], "length prefix, little-endian");
    assertEquals('h', data[5]);
    assertEquals('i', data[6]);
    assertEquals(written, SerDeUtil.lenOptionalVector(OPTIONAL_BYTES, PREFIX_BYTES, "hi".getBytes(UTF_8)));
  }

  /// The consequence, stated so nobody has to discover it: the round trip is not the identity.
  @Test
  void anEmptyVectorReadsBackAsAbsentRatherThanEmpty() {
    final byte[] data = new byte[16];
    SerDeUtil.writeOptionalVector(OPTIONAL_BYTES, PREFIX_BYTES, "".getBytes(UTF_8), data, 0);

    assertEquals(true, SerDeUtil.isAbsent(OPTIONAL_BYTES, data, 0));
    final byte[] readBack = SerDeUtil.isAbsent(OPTIONAL_BYTES, data, 0)
        ? null
        : SerDeUtil.readbyteVector(PREFIX_BYTES, data, OPTIONAL_BYTES);
    assertNull(readBack, "Some(empty) in, None out — by design, not by accident");
  }
}

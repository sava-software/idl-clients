package software.sava.idl.clients.core.gen;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Only null is an absent optional vector. An empty one goes on the wire as present and empty.
///
/// Borsh distinguishes `None` (`00`) from `Some(vec![])` (`01 00 00 00 00`), and so do programs
/// that read them. Marginfi's `marginfi_account_set_keeper_close_flags` takes `None` as every
/// balance and `Some([])` as none (`order.rs:250-262` in 0dotxyz/marginfi-v2), so an empty list
/// sent as `None` cleared the keeper-close flag on every balance. Its `write_bank_metadata` takes
/// `None` as leaving a field alone and `Some` of empty bytes as clearing it.
///
/// `SerDeUtil` used to collapse `Some(empty)` into `None`. This class recorded that as a decision
/// that would stop being right once a program drew the distinction, and Marginfi does.
final class SerDeUtilOptionalEmptyTests {

  private static final int OPTIONAL_BYTES = 1;
  private static final int PREFIX_BYTES = 4;

  @Test
  void anEmptyVectorIsWrittenAsPresentAndEmpty() {
    final byte[] data = new byte[16];
    final int written = SerDeUtil.writeOptionalVector(OPTIONAL_BYTES, PREFIX_BYTES, "".getBytes(UTF_8), data, 0);

    assertEquals(OPTIONAL_BYTES + PREFIX_BYTES, written, "the presence byte, then a zero length");
    assertEquals(1, data[0], "the presence byte says present");
    assertArrayEquals(new byte[PREFIX_BYTES], Arrays.copyOfRange(data, 1, 1 + PREFIX_BYTES), "length 0");
    assertEquals(written, SerDeUtil.lenOptionalVector(OPTIONAL_BYTES, PREFIX_BYTES, "".getBytes(UTF_8)),
        "l() has to agree with what write() emits, or the caller under-allocates");
  }

  @Test
  void nullIsTheOnlyAbsentValue() {
    final byte[] data = new byte[16];
    final int written = SerDeUtil.writeOptionalVector(OPTIONAL_BYTES, PREFIX_BYTES, null, data, 0);

    assertEquals(OPTIONAL_BYTES, written, "one presence byte and nothing else");
    assertEquals(0, data[0], "the presence byte says absent");
    assertEquals(written, SerDeUtil.lenOptionalVector(OPTIONAL_BYTES, PREFIX_BYTES, null));
    assertTrue(SerDeUtil.isAbsent(OPTIONAL_BYTES, data, 0));
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

  @Test
  void anEmptyVectorReadsBackAsEmptyRatherThanAbsent() {
    final byte[] data = new byte[16];
    SerDeUtil.writeOptionalVector(OPTIONAL_BYTES, PREFIX_BYTES, "".getBytes(UTF_8), data, 0);

    assertFalse(SerDeUtil.isAbsent(OPTIONAL_BYTES, data, 0));
    assertArrayEquals(new byte[0], SerDeUtil.readbyteVector(PREFIX_BYTES, data, OPTIONAL_BYTES),
        "Some(empty) in, Some(empty) out");
  }
}

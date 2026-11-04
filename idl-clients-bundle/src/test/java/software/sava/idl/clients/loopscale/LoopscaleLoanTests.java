package software.sava.idl.clients.loopscale;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.loopscale.gen.types.Loan;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Loopscale's `Loan` holds three 5x5 u32 matrices: weights, LTVs and liquidation thresholds. The
/// generated record wrote them eight bytes per element, so `write` overran every live loan. These
/// are mainnet loans owned by the program, read at slot 465277242, each holding matrix values
/// above 65535. Each one round-trips through the generated reader and writer, byte for byte.
///
/// A live loan is 1658 bytes, 24 more than `Loan.BYTES`, and the extra bytes are zero. The
/// deployed struct is larger than the IDL's, as the Loopscale review verdict records.
final class LoopscaleLoanTests {

  private static final int LIVE_LENGTH = 1658;

  private static byte[] fixture(final String address) {
    final var resource = "/loopscale/loan-" + address + ".base64";
    try (final var in = LoopscaleLoanTests.class.getResourceAsStream(resource)) {
      assertNotNull(in, resource);
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Test
  void liveLoansRoundTripThroughTheGeneratedRecord() {
    assertRoundTrips("8M3jXwh4imZxfqjBBiNB3eAsk8jB1HRYUW4hvFZGpvQ");
    assertRoundTrips("86CMUTsZfPz3mzBWCqZBFgQ1fWAGDAVoaAxu8fdrRgJ");
  }

  private static void assertRoundTrips(final String address) {
    final byte[] data = fixture(address);
    assertEquals(LIVE_LENGTH, data.length, "the length every live loan has");
    assertTrue(Loan.DISCRIMINATOR.equals(data, 0), "a Loan account");
    for (int i = Loan.BYTES; i < data.length; ++i) {
      assertEquals(0, data[i], "the bytes past the IDL's layout are zero, at offset " + i);
    }

    final var loan = Loan.read(PublicKey.fromBase58Encoded(address), data);
    assertEquals(Loan.BYTES, loan.l());
    assertTrue(Stream.of(loan.weightMatrix(), loan.ltvMatrix(), loan.lqtMatrix())
            .flatMap(Arrays::stream).flatMapToLong(Arrays::stream).anyMatch(v -> v > 0xFFFF),
        "the fixture exercises the four-byte encoding");

    final byte[] written = new byte[Loan.BYTES];
    assertEquals(Loan.BYTES, loan.write(written, 0));
    assertArrayEquals(Arrays.copyOf(data, Loan.BYTES), written, "the record writes back what it read");
  }
}

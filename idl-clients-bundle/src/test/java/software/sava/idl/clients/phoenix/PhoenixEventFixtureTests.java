package software.sava.idl.clients.phoenix;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.EternalEvent;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.SlotContextEvent;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

/// Decodes real Phoenix Perpetuals events captured from mainnet.
///
/// Phoenix declares all 67 of its events as **one-byte** discriminators, tags 0..66 — those are
/// Borsh enum variant indices, not Anchor's eight-byte `sha256("event:Name")`. Upstream serializes
/// `MarketEvent` with `#[derive(BorshSerialize)]`, and a Borsh enum tag is one byte, so an event's
/// wire form is a single tag byte followed by the variant's fields.
///
/// The generator used to read every declared discriminator into a fixed eight-byte buffer, which
/// zero-padded these. Every generated reader then started its first field at offset 8 instead of 1
/// and reported a length seven bytes too large — `SlotContextEvent.BYTES` was 24 where the program
/// itself says 17.
///
/// What makes this a real test rather than a restatement of our own decoding: the expected length
/// of each event is the **program's own `u16`**, taken from the `LogEventLengths` instruction it
/// emits alongside the payload. Nothing in this repository computed it. The fixture was captured
/// with `getTransaction` and de-framed from those instructions; see the header of
/// `/phoenix/market-events.txt`.
///
/// Coverage is 24 of the 67 variants — whichever the ten captured transactions happened to emit.
/// `AuthorityChangedEvent` (tag 46), where the defect was first spotted, is not among them; the
/// evidence for that one stays structural.
final class PhoenixEventFixtureTests {

  private record Event(int tag, int declaredLength, byte[] payload) {
  }

  private record Transaction(String signature, long slot, long blockTime, List<Event> events) {
  }

  private static List<Transaction> fixture() {
    final String text;
    try (var in = PhoenixEventFixtureTests.class.getResourceAsStream("/phoenix/market-events.txt")) {
      assertNotNull(in, "fixture /phoenix/market-events.txt is missing");
      text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
    final var decoder = Base64.getDecoder();
    final var transactions = new ArrayList<Transaction>();
    for (final var line : text.split("\n")) {
      if (line.isBlank() || line.charAt(0) == '#') {
        continue;
      }
      final var parts = line.split(" ");
      switch (parts[0]) {
        case "tx" -> transactions.add(new Transaction(
            parts[1], Long.parseLong(parts[2]), Long.parseLong(parts[3]), new ArrayList<>()
        ));
        case "ev" -> transactions.getLast().events().add(new Event(
            Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), decoder.decode(parts[3])
        ));
        default -> fail("unparsed fixture line: " + line);
      }
    }
    return transactions;
  }

  @Test
  void theFixtureCarriesWhatWasCaptured() {
    final var transactions = fixture();
    assertEquals(10, transactions.size());
    assertEquals(96, transactions.stream().mapToInt(tx -> tx.events().size()).sum());
    // every payload is exactly as long as the program said it would be
    for (final var tx : transactions) {
      for (final var event : tx.events()) {
        assertEquals(event.declaredLength(), event.payload().length, tx.signature());
      }
    }
  }

  /// Every captured event dispatches. Before the width fix this returned null for all 96 — the
  /// length-guarded `Discriminator.equals` rejected each one, because no real payload happens to
  /// carry seven zero bytes after its tag. Worth stating precisely: the hazard was never that
  /// decoding failed loudly, it is that a payload which *did* match would then read its fields
  /// seven bytes late.
  @Test
  void everyCapturedEventDispatches() {
    for (final var tx : fixture()) {
      for (final var event : tx.events()) {
        final var decoded = EternalEvent.read(event.payload(), 0);
        assertNotNull(decoded, () -> "tag " + event.tag() + " in " + tx.signature() + " did not dispatch");
        // dispatch is by discriminator, so re-serializing must lead with the same tag byte
        assertEquals(event.tag(), Byte.toUnsignedInt(decoded.write()[0]), () -> "tag " + event.tag());
      }
    }
  }

  /// The load-bearing assertion: what the decoder thinks the event spans must equal what the
  /// program declared. This is the number the bug moved, and it is +7 on every fixed-size variant
  /// under the old generator.
  @Test
  void decodedLengthMatchesTheLengthTheProgramDeclared() {
    final var byTag = new TreeMap<Integer, Integer>();
    for (final var tx : fixture()) {
      for (final var event : tx.events()) {
        final var decoded = EternalEvent.read(event.payload(), 0);
        assertNotNull(decoded);
        assertEquals(
            event.declaredLength(), decoded.l(),
            () -> "tag " + event.tag() + " in " + tx.signature()
                + ": the program declared " + event.declaredLength()
                + " bytes, the decoder computed " + decoded.l()
        );
        byTag.merge(event.tag(), 1, Integer::sum);
      }
    }
    assertEquals(24, byTag.size(), () -> "distinct variants covered: " + byTag);
  }

  /// A round-trip through `write` must reproduce the bytes the chain carried. This catches an
  /// offset error that happens to preserve the total length.
  @Test
  void everyEventReserializesToTheCapturedBytes() {
    for (final var tx : fixture()) {
      for (final var event : tx.events()) {
        final var decoded = EternalEvent.read(event.payload(), 0);
        assertNotNull(decoded);
        final byte[] out = new byte[event.payload().length];
        final int written = decoded.write(out, 0);
        assertEquals(event.payload().length, written, () -> "tag " + event.tag());
        assertArrayEquals(event.payload(), out, () -> "tag " + event.tag() + " in " + tx.signature());
      }
    }
  }

  /// An independent, semantic cross-check that the fields land where the decoder thinks: the slot
  /// and timestamp a `SlotContextEvent` carries are the transaction's own. Reading these one byte
  /// late yields values that are not remotely the block's.
  @Test
  void slotContextCarriesTheTransactionsOwnSlotAndTime() {
    final var checked = new ArrayList<String>();
    for (final var tx : fixture()) {
      for (final var event : tx.events()) {
        if (event.tag() != 0) {
          continue;
        }
        final var decoded = EternalEvent.read(event.payload(), 0);
        final var slotContext = assertInstanceOf(SlotContextEvent.class, decoded);
        assertEquals(tx.slot(), slotContext.slot(), tx.signature());
        assertEquals(tx.blockTime(), slotContext.timestamp(), tx.signature());
        checked.add(tx.signature());
      }
    }
    assertTrue(checked.size() >= 10, () -> "expected a SlotContextEvent per transaction, saw " + checked.size());
  }

  /// The sharpest edge case the capture contains: a one-byte event, whose entire wire form is its
  /// tag. Under the old eight-byte reader this could not be decoded at all — and a reader that
  /// still tried to take eight bytes from it would run off the end of the array.
  @Test
  void aSingleByteEventDecodes() {
    final var singleByte = new ArrayList<Map.Entry<String, Integer>>();
    for (final var tx : fixture()) {
      for (final var event : tx.events()) {
        if (event.declaredLength() != 1) {
          continue;
        }
        assertDoesNotThrow(() -> {
          final var decoded = EternalEvent.read(event.payload(), 0);
          assertNotNull(decoded, () -> "one-byte tag " + event.tag() + " did not dispatch");
          assertEquals(1, decoded.l());
        }, () -> "tag " + event.tag() + " in " + tx.signature());
        singleByte.add(Map.entry(tx.signature(), event.tag()));
      }
    }
    assertFalse(singleByte.isEmpty(), "the capture is expected to contain a one-byte event");
  }
}

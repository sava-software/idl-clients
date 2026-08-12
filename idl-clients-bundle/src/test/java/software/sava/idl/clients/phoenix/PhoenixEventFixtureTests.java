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

  /// `AdminParameterUpdatedEvent`. Excluded from the bulk assertions below because decoding it
  /// overflows the stack — a *separate*, pre-existing generator defect this capture uncovered, not
  /// anything the discriminator-width work changed. `aLeverageTiersUpdateStillOverflowsTheStack`
  /// pins it, and will fail the moment it is fixed, which is the signal to delete this constant.
  private static final int ADMIN_PARAMETER_UPDATED = 48;

  private static boolean decodable(final Event event) {
    return event.tag() != ADMIN_PARAMETER_UPDATED;
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
        if (!decodable(event)) {
          continue;
        }
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
        if (!decodable(event)) {
          continue;
        }
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
    assertEquals(23, byTag.size(), () -> "distinct decodable variants covered: " + byTag);
  }

  /// A round-trip through `write` must reproduce the bytes the chain carried. This catches an
  /// offset error that happens to preserve the total length.
  @Test
  void everyEventReserializesToTheCapturedBytes() {
    for (final var tx : fixture()) {
      for (final var event : tx.events()) {
        if (!decodable(event)) {
          continue;
        }
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
        if (event.declaredLength() != 1 || !decodable(event)) {
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

  /// Pins a **separate, pre-existing** generator defect that this capture uncovered — nothing to do
  /// with discriminator width, and present in the committed clients before any of that work.
  ///
  /// `AdminParameterUpdateKind` has a struct variant named `LeverageTiers` whose two fields are of
  /// the defined type *also* named `LeverageTiers`. The generator emits the variant as a nested
  /// record of that name, which shadows the top-level type inside its own scope, so the generated
  /// `LeverageTiers.read(...)` calls itself forever. The sizing is right (`BYTES = 192` = 2 × 96);
  /// only the name resolution is wrong.
  ///
  /// The generator already solves exactly this for *tuple* variants —
  /// `BaseDefinedTypeDefinition.generateEnumRecord` qualifies the field type when it collides with
  /// the variant name, which is why `UpdateLendingMarketConfigValue.ElevationGroup` and
  /// `AssetFilter.NoFilter` emit fully-qualified names and work. The struct-variant path has no
  /// equivalent. Corpus-wide there are five colliding variants and this is the only struct one.
  ///
  /// When the generator is fixed this test fails — which is the signal to delete it along with
  /// [#ADMIN_PARAMETER_UPDATED] and re-enable tag 48 in the assertions above.
  @Test
  void aLeverageTiersUpdateStillOverflowsTheStack() {
    // Only the payloads carrying the LeverageTiers variant recurse; the other tag-48 shapes decode
    // fine, which is why the exclusion above is by tag and this search is by behaviour.
    final var admin = fixture().stream()
        .flatMap(tx -> tx.events().stream())
        .filter(event -> event.tag() == ADMIN_PARAMETER_UPDATED)
        .toList();
    assertFalse(admin.isEmpty(), "the capture is expected to contain tag 48");

    final boolean anyOverflows = admin.stream().anyMatch(event -> {
      try {
        EternalEvent.read(event.payload(), 0);
        return false;
      } catch (final StackOverflowError e) {
        return true;
      }
    });
    assertTrue(
        anyOverflows,
        "AdminParameterUpdateKind.LeverageTiers no longer self-recurses — remove this test, the "
            + "ADMIN_PARAMETER_UPDATED constant, and the decodable() filter"
    );
  }
}

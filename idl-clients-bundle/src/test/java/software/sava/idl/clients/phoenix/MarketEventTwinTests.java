package software.sava.idl.clients.phoenix;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.phoenix.perpetuals.gen.events.EternalEvent;
import software.sava.idl.clients.phoenix.perpetuals.gen.events.SlotContextEvent;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.MarketEvent;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Two generated decoders for one wire format, held to each other and to the program's own bytes.
///
/// The program emits a Borsh `MarketEvent`: a one-byte enum tag then the variant's payload. This
/// package decodes it two ways — the standalone `EternalEvent` record, and the `MarketEvent` enum
/// that `EternalProgram.LogIxData` reaches through `OffChainMarketEvent`. The enum once counted
/// the tag twice and spanned 18 bytes for an event the program declared as 17, compounding one
/// byte per element through `SerDeUtil.readArray`.
///
/// Every expected value below comes from the fixture — the length the program itself declared —
/// so the two decoders cannot drift into agreeing on a wrong one.
final class MarketEventTwinTests {

  /// One captured event: the tag, the length the *program* declared for it in its own
  /// `LogEventLengths` instruction, and the payload. Nothing here is computed by this repository.
  private record Event(int tag, int declaredLength, byte[] payload) {
  }

  private static List<Event> slotContextEvents() {
    final String text;
    try (var in = MarketEventTwinTests.class.getResourceAsStream("/phoenix/market-events.txt")) {
      assertNotNull(in, "fixture /phoenix/market-events.txt is missing");
      text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
    final var decoder = Base64.getDecoder();
    final var events = new ArrayList<Event>();
    for (final var line : text.split("\n")) {
      if (line.isBlank() || line.charAt(0) == '#' || !line.startsWith("ev 0 ")) {
        continue;
      }
      final var parts = line.split(" ");
      events.add(new Event(
          Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), decoder.decode(parts[3])
      ));
    }
    // a row count, not a byte length — 18 tag-0 events among the 96 captured
    assertEquals(18, events.size(), "tag 0 (SlotContext) events in the fixture");
    return events;
  }

  /// The standalone decoder, unchanged by the twin work: the program said 17, the payload is 17,
  /// and `EternalEvent` agrees. Everything below is measured against this line.
  @Test
  void theStandaloneDecoderAgreesWithTheProgramsOwnLength() {
    assertEquals(17, SlotContextEvent.BYTES, "tag byte + two u64s");
    for (final var event : slotContextEvents()) {
      assertEquals(17, event.declaredLength());
      assertEquals(event.declaredLength(), event.payload().length, "the payload is what was declared");

      final var decoded = EternalEvent.read(event.payload(), 0);
      assertInstanceOf(SlotContextEvent.class, decoded);
      assertEquals(event.declaredLength(), decoded.l(), "the standalone decoder spans the whole event");
    }
  }

  /// The number the fix moved: 18 before, the program's own 17 now. The enum contributes its one
  /// ordinal byte and delegates to a payload that no longer restates it.
  @Test
  void theEnumSpansExactlyWhatTheProgramDeclared() {
    for (final var event : slotContextEvents()) {
      final var twin = MarketEvent.read(event.payload(), 0);
      assertInstanceOf(MarketEvent.SlotContext.class, twin);
      assertEquals(
          event.declaredLength(), twin.l(),
          "ordinalBytes() + the plain twin's BYTES, with the tag counted once"
      );
    }
  }

  /// Decoding the exact bytes the program emitted, with nothing after them. This used to index
  /// past the end — the enum needed 18 bytes of a 17-byte array — and in a batch it would instead
  /// have read into its neighbour, which is the quieter and worse case.
  @Test
  void theEnumDecodesTheExactBytesTheProgramEmitted() {
    for (final var event : slotContextEvents()) {
      final var decoded = MarketEvent.read(event.payload(), 0);
      assertInstanceOf(MarketEvent.SlotContext.class, decoded);
      assertEquals(event.payload().length, decoded.l(), "consumes the event and no more");
    }
  }

  /// And the field-level agreement, which is what would have reached a user as a wrong number.
  /// Both decoders read the timestamp at absolute offset 1 and the slot at absolute offset 9 of the
  /// same payload, by different routes: the standalone record consumes its own one-byte
  /// discriminator, the enum consumes the ordinal and enters the plain twin at offset 1.
  @Test
  void bothDecodersReadTheSameFields() {
    for (final var event : slotContextEvents()) {
      final var standalone = (SlotContextEvent) EternalEvent.read(event.payload(), 0);
      final var viaEnum = ((MarketEvent.SlotContext) MarketEvent.read(event.payload(), 0)).val();

      assertEquals(standalone.timestamp(), viaEnum.timestamp());
      assertEquals(standalone.slot(), viaEnum.slot());
    }
  }
}

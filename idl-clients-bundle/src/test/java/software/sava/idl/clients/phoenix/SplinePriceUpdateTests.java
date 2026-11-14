package software.sava.idl.clients.phoenix;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.EternalProgram;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.UpdateSplinePriceParamsWithOrdering;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;

/// `UpdateSplinePrice` in the forms the program takes: with and without ordering, each with and
/// without an update slot.
///
/// Three of the four payloads are instructions that executed: the short form with a slot and the
/// ordered form with one on mainnet, the ordered form without one on Phoenix's Dev deployment.
/// No short update without a slot was found on chain, so that one is the payload Phoenix's SDK
/// writes for it.
///
/// The lengths are the ones mainnet took in simulation on 2026-11-14, slots 469335405 to
/// 469344443, for one spline with and without an update slot, the splines account read back to
/// see the price written. Without a slot: every length from 18 to 26 bytes wrote the price as
/// the short form and 43 as the ordered one, 8, 16 and 17 panicked, and 27, 34, 35, 42 and 44
/// failed "Invalid instruction data length for UpdateSplinePriceParamsWithOrdering". With one:
/// 26 and 51 wrote it, 25 panicked, and 27, 43, 50 and 52 failed that way.
final class SplinePriceUpdateTests {

  /// Phoenix's SDK, `instructions.json` entry `UpdateSplinePrice` in Ellipsis-Labs/rise-public at
  /// `f67e8b4`, written by `rust/sdk/examples/build_all_ix.rs` from `create_update_spline_price_ix`.
  private static final String SHORT_WITHOUT_SLOT = "2d9bc83425d6bb8ce8030000000000000001";

  /// Mainnet `RZrsejD1BCnDbFFKzh7Q95y4bR6QjUixhbRdMsoAdQHCJJJ2QaVkevLK2fSbMx7jJ5x1sod41nPcFFgbXNystXg`,
  /// slot 469317139, instruction 0. Its sender counts update slots in microseconds.
  private static final String SHORT_WITH_SLOT = "2d9bc83425d6bb8c80970000000000000108b351253b60060000";

  /// Dev `3FQEsA7ncPEEiZ3iUx5wje3NVAk9HboNcj8wbP7nEN8S3tQgDzh5xT4otKcTBADcBo7MwYHbXdgBmwDe4rBAvAgU`,
  /// slot 469279238, instruction 0.
  private static final String ORDERED_WITHOUT_SLOT =
      "2d9bc83425d6bb8c981d0000000000000001cb731800000000007072696365000000cb7318000000000000";

  /// Mainnet `38GQLbFrsfmiBYgRm7CdGgNzicQP7YdWseZPgWkB92tbrRFSEMV15z1TE23tgcWHRPup1RXBm3rv9a49m5YU3nrz`,
  /// slot 469279249, instruction 3.
  private static final String ORDERED_WITH_SLOT =
      "2d9bc83425d6bb8c8a060100000000000111a2f81b000000000131d98618a0dfe7180000000000000000000000000000000000";

  private static byte[] hex(final String hex) {
    return HexFormat.of().parseHex(hex);
  }

  private static SplinePriceUpdate read(final String hex) {
    final byte[] data = hex(hex);
    return SplinePriceUpdate.read(data, 0, data.length);
  }

  private static void assertRefused(final byte[] data, final int offset, final int length, final String message) {
    final var refused = assertThrows(
        IllegalArgumentException.class, () -> SplinePriceUpdate.read(data, offset, length));
    assertEquals(message, refused.getMessage());
  }

  private static String tooShort(final int length) {
    return "UpdateSplinePrice is at least 18 bytes, not " + length + '.';
  }

  private static String withoutSlot(final int length) {
    return "UpdateSplinePrice without an update slot is 18 to 26 bytes, or 43 with ordering, not " + length + '.';
  }

  private static String withSlot(final int length) {
    return "UpdateSplinePrice with an update slot is 26 bytes, or 51 with ordering, not " + length + '.';
  }

  private static void assertSameUpdate(final SplinePriceUpdate expected, final SplinePriceUpdate update) {
    assertEquals(expected.newMidPrice(), update.newMidPrice());
    assertEquals(expected.userUpdateSlot(), update.userUpdateSlot());
    assertEquals(expected.refreshRegions(), update.refreshRegions());
    if (expected.ordering() == null) {
      assertNull(update.ordering());
    } else {
      assertEquals(expected.ordering().userSequenceNumber(), update.ordering().userSequenceNumber());
      assertArrayEquals(expected.ordering().clientOrderId(), update.ordering().clientOrderId());
      assertEquals(expected.ordering().overrideSequenceNumber(), update.ordering().overrideSequenceNumber());
    }
  }

  @Test
  void readsTheShortFormWithoutAnUpdateSlot() {
    final var update = read(SHORT_WITHOUT_SLOT);
    assertEquals(1_000L, update.newMidPrice());
    assertEquals(OptionalLong.empty(), update.userUpdateSlot());
    assertTrue(update.refreshRegions());
    assertNull(update.ordering());
  }

  @Test
  void readsTheShortFormAMainnetUpdateSent() {
    final var update = read(SHORT_WITH_SLOT);
    assertEquals(38_784L, update.newMidPrice());
    assertEquals(OptionalLong.of(1_794_657_005_712_136L), update.userUpdateSlot());
    assertFalse(update.refreshRegions());
    assertNull(update.ordering());
  }

  @Test
  void readsTheOrderedFormWithoutAnUpdateSlot() {
    final var update = read(ORDERED_WITHOUT_SLOT);
    assertEquals(7_576L, update.newMidPrice());
    assertEquals(OptionalLong.empty(), update.userUpdateSlot());
    assertTrue(update.refreshRegions());
    assertEquals(1_602_507L, update.ordering().userSequenceNumber());
    assertArrayEquals(hex("7072696365000000cb73180000000000"), update.ordering().clientOrderId());
    assertFalse(update.ordering().overrideSequenceNumber());
  }

  @Test
  void readsTheOrderedFormAMainnetUpdateSent() {
    final var update = read(ORDERED_WITH_SLOT);
    assertEquals(67_210L, update.newMidPrice());
    assertEquals(OptionalLong.of(469_279_249L), update.userUpdateSlot());
    assertTrue(update.refreshRegions());
    assertEquals(1_794_648_855_229_684_017L, update.ordering().userSequenceNumber());
    assertArrayEquals(new byte[SplinePriceUpdate.Ordering.CLIENT_ORDER_ID_LEN], update.ordering().clientOrderId());
    assertFalse(update.ordering().overrideSequenceNumber());
  }

  /// The ordered forms are the ones the IDL declares, so the generated reader is the reference
  /// for them, field for field.
  @Test
  void orderedFormsReadAsTheGeneratedReaderReadsThem() {
    for (final var ordered : List.of(ORDERED_WITHOUT_SLOT, ORDERED_WITH_SLOT)) {
      final var generated = EternalProgram.UpdateSplinePriceIxData.read(hex(ordered), 0).params();
      final var update = read(ordered);
      assertEquals(generated.newMidPrice(), update.newMidPrice());
      assertEquals(generated.userUpdateSlot(), update.userUpdateSlot());
      assertEquals(generated.refreshRegions(), update.refreshRegions());
      assertEquals(generated.userSequenceNumber(), update.ordering().userSequenceNumber());
      assertArrayEquals(generated.clientOrderId(), update.ordering().clientOrderId());
      assertEquals(generated.overrideSequenceNumber(), update.ordering().overrideSequenceNumber());
    }
  }

  /// And what the generated builder writes is read back, the orderbook it leaves out included.
  @Test
  void readsWhatTheGeneratedBuilderWrites() {
    final var accounts = PhoenixAccounts.MAIN_NET;
    final var key = PublicKey.fromBase58Encoded("sp1nLqqjVZ1kTusFjJmHp1xHDpH6M4F7KXCr1iJ3xpX");
    final byte[] clientOrderId = hex("000102030405060708090a0b0c0d0e0f");
    for (final var slot : List.of(OptionalLong.empty(), OptionalLong.of(469_337_012L))) {
      final var update = SplinePriceUpdate.read(EternalProgram.updateSplinePrice(
          accounts.invokedEternalProgram(), accounts.invokedEternalProgram().publicKey(), accounts.eternalLogAuthority(),
          key, key, key, null,
          new UpdateSplinePriceParamsWithOrdering(8_643L, slot, false, 1L << 62, clientOrderId, true)));
      assertEquals(8_643L, update.newMidPrice());
      assertEquals(slot, update.userUpdateSlot());
      assertFalse(update.refreshRegions());
      assertEquals(1L << 62, update.ordering().userSequenceNumber());
      assertArrayEquals(clientOrderId, update.ordering().clientOrderId());
      assertTrue(update.ordering().overrideSequenceNumber());
    }
  }

  /// Why this reader exists. If the generated reader starts reading the short form, the IDL or
  /// the generator has caught up and `SplinePriceUpdate` is due a second look.
  @Test
  void generatedReaderDoesNotReadTheShortForm() {
    for (final var shortForm : List.of(SHORT_WITHOUT_SLOT, SHORT_WITH_SLOT)) {
      assertThrows(
          IndexOutOfBoundsException.class,
          () -> EternalProgram.UpdateSplinePriceIxData.read(hex(shortForm), 0));
    }
  }

  /// Each flag is its own byte: the mainnet ordered update with its last byte set and with its
  /// refresh byte cleared, and the mainnet short update with its last byte set.
  @Test
  void flagsAreReadFromTheirOwnBytes() {
    final byte[] overriding = hex(ORDERED_WITH_SLOT);
    overriding[overriding.length - 1] = 1;
    final var override = SplinePriceUpdate.read(overriding, 0, overriding.length);
    assertTrue(override.ordering().overrideSequenceNumber());
    assertTrue(override.refreshRegions());

    final byte[] notRefreshing = hex(ORDERED_WITH_SLOT);
    notRefreshing[25] = 0;
    final var noRefresh = SplinePriceUpdate.read(notRefreshing, 0, notRefreshing.length);
    assertFalse(noRefresh.refreshRegions());
    assertFalse(noRefresh.ordering().overrideSequenceNumber());

    final byte[] refreshing = hex(SHORT_WITH_SLOT);
    refreshing[refreshing.length - 1] = 1;
    assertTrue(SplinePriceUpdate.read(refreshing, 0, refreshing.length).refreshRegions());
  }

  /// The instruction's bytes are a region of what it is read from: nothing before `offset` or
  /// from `offset + length` on is the instruction's, whatever it holds.
  @Test
  void readsARegionOfALargerBuffer() {
    for (final var form : List.of(SHORT_WITHOUT_SLOT, SHORT_WITH_SLOT, ORDERED_WITHOUT_SLOT, ORDERED_WITH_SLOT)) {
      final byte[] data = hex(form);
      final byte[] buffer = new byte[5 + data.length + 40];
      Arrays.fill(buffer, (byte) 1);
      System.arraycopy(data, 0, buffer, 5, data.length);

      assertSameUpdate(SplinePriceUpdate.read(data, 0, data.length), SplinePriceUpdate.read(buffer, 5, data.length));
    }

    // a byte fewer than the instruction has is not a length the program takes, though the
    // buffer holds a byte there, and the forms that end where their fields end take no more
    final byte[] buffer = new byte[5 + 51 + 40];
    Arrays.fill(buffer, (byte) 1);
    System.arraycopy(hex(SHORT_WITHOUT_SLOT), 0, buffer, 5, 18);
    assertRefused(buffer, 5, 17, tooShort(17));
    System.arraycopy(hex(SHORT_WITH_SLOT), 0, buffer, 5, 26);
    assertRefused(buffer, 5, 25, withSlot(25));
    assertRefused(buffer, 5, 27, withSlot(27));
    System.arraycopy(hex(ORDERED_WITHOUT_SLOT), 0, buffer, 5, 43);
    assertRefused(buffer, 5, 42, withoutSlot(42));
    assertRefused(buffer, 5, 44, withoutSlot(44));
    System.arraycopy(hex(ORDERED_WITH_SLOT), 0, buffer, 5, 51);
    assertRefused(buffer, 5, 50, withSlot(50));
    assertRefused(buffer, 5, 52, withSlot(52));
  }

  @Test
  void readsAnInstruction() {
    final var program = PhoenixAccounts.MAIN_NET.invokedEternalProgram();

    final var whole = SplinePriceUpdate.read(Instruction.createInstruction(program, List.of(), hex(SHORT_WITH_SLOT)));
    assertEquals(38_784L, whole.newMidPrice());
    assertNull(whole.ordering());

    final byte[] data = hex(ORDERED_WITHOUT_SLOT);
    final byte[] buffer = new byte[3 + data.length + 7];
    System.arraycopy(data, 0, buffer, 3, data.length);
    final var region = SplinePriceUpdate.read(Instruction.createInstruction(program, List.of(), buffer, 3, data.length));
    assertEquals(7_576L, region.newMidPrice());
    assertEquals(1_602_507L, region.ordering().userSequenceNumber());
  }

  /// Without an update slot the short form's fields end at 18 bytes, and the program takes up
  /// to 26, ignoring the rest. The bytes that follow here are an ordered update's sequence
  /// number, which the short form does not read.
  @Test
  void shortFormWithoutAnUpdateSlotIgnoresWhatFollowsItsFields() {
    final byte[] ordered = hex(ORDERED_WITHOUT_SLOT);
    for (int length = 18; length <= 26; ++length) {
      final var update = SplinePriceUpdate.read(Arrays.copyOf(ordered, length), 0, length);
      assertEquals(7_576L, update.newMidPrice());
      assertEquals(OptionalLong.empty(), update.userUpdateSlot());
      assertTrue(update.refreshRegions());
      assertNull(update.ordering());
    }
  }

  /// Every length from a bare discriminator to 64 bytes, for an update without a slot and for
  /// one with: the lengths the program took are read, as the form the program took them for, and
  /// every other is refused.
  @Test
  void takesTheLengthsTheProgramTakesAndNoOthers() {
    final byte[] withoutSlot = Arrays.copyOf(hex(ORDERED_WITHOUT_SLOT), 64);
    final byte[] withSlot = Arrays.copyOf(hex(ORDERED_WITH_SLOT), 64);
    for (int length = 8; length <= 64; ++length) {
      final byte[] noSlot = Arrays.copyOf(withoutSlot, length);
      if (length < 18) {
        assertRefused(noSlot, 0, length, tooShort(length));
      } else if (length <= 26) {
        assertNull(SplinePriceUpdate.read(noSlot, 0, length).ordering());
      } else if (length == 43) {
        assertEquals(1_602_507L, SplinePriceUpdate.read(noSlot, 0, length).ordering().userSequenceNumber());
      } else {
        assertRefused(noSlot, 0, length, withoutSlot(length));
      }

      final byte[] slot = Arrays.copyOf(withSlot, length);
      if (length < 18) {
        assertRefused(slot, 0, length, tooShort(length));
      } else if (length == 26) {
        final var update = SplinePriceUpdate.read(slot, 0, length);
        assertEquals(OptionalLong.of(469_279_249L), update.userUpdateSlot());
        assertNull(update.ordering());
      } else if (length == 51) {
        assertEquals(
            1_794_648_855_229_684_017L, SplinePriceUpdate.read(slot, 0, length).ordering().userSequenceNumber());
      } else {
        assertRefused(slot, 0, length, withSlot(length));
      }
    }
  }

  @Test
  void refusesAnotherInstruction() {
    final String notUpdateSplinePrice = "Not an UpdateSplinePrice instruction.";

    // UpdateSplinePriceWithMetadata's discriminator over an otherwise valid short update
    final byte[] metadata = hex(SHORT_WITH_SLOT);
    EternalProgram.UPDATE_SPLINE_PRICE_WITH_METADATA_DISCRIMINATOR.write(metadata, 0);
    assertRefused(metadata, 0, metadata.length, notUpdateSplinePrice);

    // the discriminator is the region's first eight bytes, not the buffer's
    final byte[] data = hex(SHORT_WITH_SLOT);
    final byte[] buffer = new byte[4 + data.length];
    System.arraycopy(data, 0, buffer, 0, data.length);
    assertRefused(buffer, 4, data.length, notUpdateSplinePrice);

    // too short to hold a discriminator
    assertRefused(data, 0, 7, notUpdateSplinePrice);
    assertRefused(data, 0, 0, notUpdateSplinePrice);
  }

  /// The update slot is a Borsh option, whose tag is 0 or 1. Anything else is refused, as the
  /// generated readers refuse it.
  @Test
  void refusesAnOptionTagThatIsNeitherAbsentNorPresent() {
    final byte[] data = hex(SHORT_WITH_SLOT);
    data[16] = 2;
    assertThrows(IllegalArgumentException.class, () -> SplinePriceUpdate.read(data, 0, data.length));
  }
}

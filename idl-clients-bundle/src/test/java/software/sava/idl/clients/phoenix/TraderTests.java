package software.sava.idl.clients.phoenix;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.Trader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.RecordComponent;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static software.sava.core.encoding.ByteUtil.getInt32LE;

/// The Eternal IDL still declares the trader account's `maxPositions` as one `u64`, and this pins
/// how far we go about it.
///
/// The program splits those eight bytes into `max_positions: u32` and `trader_preference_bits: u32`
/// (`TraderHeader` in rise-public `rust/accounts/src/trader/mod.rs`). The IDL republished on
/// 2026-09-16 carries that split into `RegisterTraderParams` and `TraderRegisteredEvent`, but not
/// into the `Trader` account, so the generated `Trader.maxPositions()` folds the preference bits
/// into its high half. The width is unchanged, so every other field still reads correctly.
///
/// The layout is **deliberately not overridden** here, for the reason [EmberStateTests] gives: a
/// local field-list override is a private fork of someone else's account definition. That fix
/// belongs upstream. Until it lands, read the two halves from the raw bytes at
/// `Trader.MAX_POSITIONS_OFFSET` and four bytes after it.
///
/// So the assertions record the gap and act as a tripwire: if `Trader` gains a
/// `traderPreferenceBits` component, upstream has corrected the IDL, and this class should be
/// rewritten to assert the split.
final class TraderTests {

  private static final String ADDRESS = "FajFEg2wqJdvsWZtFVbWYzw2bJ1bqwpXhTUbsSrsQCwM";

  /// Captured with `getAccountInfo` from mainnet at slot 447613095. The account was registered in
  /// `3iKV7GNv…`, whose `TraderRegisteredEvent` is in `/phoenix/market-events.txt`, with one max
  /// position and preference bit 0 set.
  private static byte[] accountData() {
    try (var in = TraderTests.class.getResourceAsStream("/phoenix/trader-" + ADDRESS + ".base64")) {
      assertNotNull(in, "fixture for " + ADDRESS + " is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /// The fixture is the ground truth, so this holds regardless of what the client declares: two
  /// `u32`s, both 1, where the record sees one field.
  @Test
  void theCapturedAccountHoldsTwoU32s() {
    final byte[] data = accountData();
    assertEquals(280, data.length);
    assertArrayEquals(Trader.DISCRIMINATOR.data(), Arrays.copyOf(data, 8));

    assertEquals(1, getInt32LE(data, Trader.MAX_POSITIONS_OFFSET), "max_positions");
    assertEquals(1, getInt32LE(data, Trader.MAX_POSITIONS_OFFSET + Integer.BYTES), "trader_preference_bits");
  }

  /// The gap we are choosing not to close, stated exactly so it cannot be mistaken for an
  /// oversight.
  @Test
  void theRecordReadsBothHalvesAsOneU64() {
    assertFalse(
        Arrays.stream(Trader.class.getRecordComponents())
            .map(RecordComponent::getName)
            .anyMatch("traderPreferenceBits"::equals),
        "the IDL now declares the split; rewrite this class to assert it"
    );

    final var trader = Trader.read(PublicKey.fromBase58Encoded(ADDRESS), accountData());
    assertEquals((1L << 32) | 1L, trader.maxPositions(), "preference bit 0 lands in the high half");
  }

  /// The practical consequence for a scan: `createMaxPositionsFilter` compares all eight bytes, so it
  /// only finds traders with no preference bits set. This account has one max position and is not
  /// matched by the filter for one; a four-byte comparison at the same offset does match it.
  @Test
  void theMaxPositionsFilterMissesTradersWithPreferenceBits() {
    final byte[] data = accountData();
    final byte[] eightBytes = {1, 0, 0, 0, 0, 0, 0, 0};

    assertEquals(Filter.createMemCompFilter(Trader.MAX_POSITIONS_OFFSET, eightBytes), Trader.createMaxPositionsFilter(1L),
        "the generated filter is an eight-byte u64 comparison");
    assertFalse(Arrays.equals(eightBytes, Arrays.copyOfRange(data, Trader.MAX_POSITIONS_OFFSET, Trader.MAX_POSITIONS_OFFSET + Long.BYTES)),
        "so it does not match this account");
    assertArrayEquals(new byte[]{1, 0, 0, 0},
        Arrays.copyOfRange(data, Trader.MAX_POSITIONS_OFFSET, Trader.MAX_POSITIONS_OFFSET + Integer.BYTES),
        "while a four-byte comparison would");
  }

  /// Everything around the folded field agrees with the registration event for this account, so
  /// the misread is confined to those eight bytes.
  @Test
  void theFieldsAroundItReadWhatTheRegistrationEventSaid() {
    final var trader = Trader.read(PublicKey.fromBase58Encoded(ADDRESS), accountData());

    assertEquals(ADDRESS, trader.key().toBase58());
    assertEquals("8TcjkkM5wDWtPCZv36i66d4KYG5aeLhMPALc3nTYZaXP", trader.authority().toBase58());
    assertEquals(1, trader.traderPdaIndex());
    assertEquals(74, trader.traderSubaccountIndex());
  }
}

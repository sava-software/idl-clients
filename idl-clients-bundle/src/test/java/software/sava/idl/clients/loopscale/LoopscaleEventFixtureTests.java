package software.sava.idl.clients.loopscale;

import org.junit.jupiter.api.Test;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.loopscale.gen.events.LoopscaleEvent;
import software.sava.idl.clients.loopscale.gen.events.RewardsClaimedEvent;
import software.sava.idl.clients.loopscale.gen.events.StakeEvent;
import software.sava.idl.clients.loopscale.gen.events.TimelockCanceledEvent;
import software.sava.idl.clients.loopscale.gen.events.TimelockCreatedEvent;
import software.sava.idl.clients.loopscale.gen.events.TimelockExecutedEvent;
import software.sava.idl.clients.loopscale.gen.events.UserRewardsInfoClosed;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

/// Loopscale emits its events in an envelope no published IDL describes. This class holds the
/// captured events against the client generated from the program's on-chain IDL, and pins
/// that gap rather than closing it.
///
/// Each event is an instruction the program invokes on itself, with its event authority as the
/// only account, the way Anchor's `emit_cpi!` emits one. Its data is the eight-byte
/// `EVENT_IX_TAG_LE` `e445a52e51cb9a1d`, an eight-byte event discriminator, then the event, and
/// `LoopscaleEvent.readCPI` dispatches on that discriminator. Every captured event carries the
/// same one, `ec7be27dddc8e438`, then a one-byte variant tag, the way Borsh writes an enum, then
/// that variant's fields. No published IDL declares that discriminator or the enum behind it:
/// not the anchor IDL account `8jaPDEbz…` this client is generated from, nor, as of 2026-11-04,
/// any public copy, the newest of which (`@exponent-labs/loopscale-deserializer` 0.9.32) is the
/// same document. The six events that document declares each keep the
/// `sha256("event:<Name>")[0..8]` Anchor derives from the name, so `readCPI` returns null for
/// every live event.
///
/// A local decoder for the envelope would mean maintaining an event definition nobody
/// publishes, with nothing upstream to diff it against. That fix belongs upstream.
/// `EmberStateTests` makes the same call for Ember's short account layout, and the assertions
/// here are split the same way:
///
/// - the fixture's own shape, which holds whatever the client declares;
/// - the tripwire: `readCPI` decodes none of the captured events. Once it decodes one, an IDL
///   describing the event enum has shipped, and this class should be rewritten to assert the
///   full layout rather than the gap;
/// - the gap, measured: behind the variant byte, variants 5, 10 and 11 are byte for byte the
///   IDL's `StakeEvent`, `RewardsClaimedEvent` and `UserRewardsInfoClosed`, while variants 3
///   and 6 fit none of the six, so reading past the variant byte is no substitute for a decoder.
///
/// Field values are held to the emitting instruction rather than to the reader. An account is
/// named by its 0-based position in that instruction's account list, in the order the
/// `LoopscaleProgram` `*Keys` builders use. The fixture was captured from mainnet with
/// `getTransaction`; see the header of `/loopscale/event-cpis.txt`.
final class LoopscaleEventFixtureTests {

  /// `deposit_collateral`, slot 465207273.
  private static final String DEPOSIT_TX =
      "2yhdNR2cNeA5tLQCd6ueBNZuFr3xifwoEbfNFwCAiLK2ApVuA92wzBc9Cg2ctgMmvbiKMMAXDHKCvZEE98cJm551";
  /// `claim_vault_rewards`, slot 465174521.
  private static final String CLAIM_TX =
      "3Mhx4VrGJXc11wfKgKpj49HHSiqorjEB7zaTxqGsWMM4yuJHDznCsQSeuXDdEL3ey18K931QKAQj76msHCeTrsNN";
  /// `unstake_user_vault_lp` as outer instruction 2, then `stake_user_vault_lp` as outer
  /// instruction 4, slot 465225154.
  private static final String RESTAKE_TX =
      "4eAm7FhLNx3Jthf2t2BfHMEyP7yLWSY4mGG2WMLqewT9SNpVZtp4io8F7i4phBQ1rS7LdZUDSkzn3FtWbvyUNxAC";

  private static final HexFormat HEX = HexFormat.of();
  /// Anchor's `EVENT_IX_TAG_LE`, the first eight bytes of every event instruction.
  private static final byte[] EVENT_IX_TAG = HEX.parseHex("e445a52e51cb9a1d");
  /// The event discriminator every captured event carries and no published IDL declares.
  private static final byte[] LIVE_DISCRIMINATOR = HEX.parseHex("ec7be27dddc8e438");
  /// The variant tag follows the event tag and the event discriminator, and the variant's
  /// fields follow the tag.
  private static final int VARIANT_OFFSET = 16;
  private static final int PAYLOAD_OFFSET = VARIANT_OFFSET + 1;

  private static final String TRIPWIRE = "LoopscaleEvent.readCPI decoded a live Loopscale event: the client"
      + " now declares the discriminator ec7be27dddc8e438, so an IDL describing the program's event enum"
      + " has shipped. Rewrite LoopscaleEventFixtureTests to assert the full event layout instead of"
      + " recording the gap.";

  private record Cpi(int outer, int inner, int stackHeight, String emitter, byte[] data) {
  }

  private record Transaction(String signature, long slot, long blockTime, List<Cpi> cpis) {
  }

  /// What one captured event measured: its whole length and the variant tag at `VARIANT_OFFSET`.
  private record Envelope(String signature, int outer, int inner, String emitter, int length, int variant) {
  }

  /// One of the six events the IDL declares, with the reader generated for it.
  private record IdlEvent(String name,
                          Discriminator discriminator,
                          BiFunction<byte[], Integer, LoopscaleEvent> reader) {
  }

  private static final List<IdlEvent> IDL_EVENTS = List.of(
      new IdlEvent("StakeEvent", StakeEvent.DISCRIMINATOR, StakeEvent::read),
      new IdlEvent("RewardsClaimedEvent", RewardsClaimedEvent.DISCRIMINATOR, RewardsClaimedEvent::read),
      new IdlEvent("UserRewardsInfoClosed", UserRewardsInfoClosed.DISCRIMINATOR, UserRewardsInfoClosed::read),
      new IdlEvent("TimelockCreatedEvent", TimelockCreatedEvent.DISCRIMINATOR, TimelockCreatedEvent::read),
      new IdlEvent("TimelockCanceledEvent", TimelockCanceledEvent.DISCRIMINATOR, TimelockCanceledEvent::read),
      new IdlEvent("TimelockExecutedEvent", TimelockExecutedEvent.DISCRIMINATOR, TimelockExecutedEvent::read)
  );

  private static List<Transaction> fixture() {
    final String text;
    try (var in = LoopscaleEventFixtureTests.class.getResourceAsStream("/loopscale/event-cpis.txt")) {
      assertNotNull(in, "fixture /loopscale/event-cpis.txt is missing");
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
        case "cpi" -> transactions.getLast().cpis().add(new Cpi(
            Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]),
            parts[4], decoder.decode(parts[5])
        ));
        default -> fail("unparsed fixture line: " + line);
      }
    }
    return transactions;
  }

  private static Transaction transaction(final List<Transaction> fixture, final String signature) {
    final var matches = fixture.stream().filter(tx -> tx.signature().equals(signature)).toList();
    assertEquals(1, matches.size(), () -> "captured transactions " + signature);
    return matches.getFirst();
  }

  /// The event at position `inner` of the inner instructions under outer instruction `outer`.
  private static Cpi cpi(final List<Transaction> fixture, final String signature, final int outer, final int inner) {
    final var matches = transaction(fixture, signature).cpis().stream()
        .filter(cpi -> cpi.outer() == outer && cpi.inner() == inner)
        .toList();
    assertEquals(1, matches.size(), () -> "captured events at outer " + outer + " inner " + inner + " of " + signature);
    return matches.getFirst();
  }

  /// `discriminator`'s eight bytes, then the payload after the variant byte: the bytes an IDL
  /// struct's reader expects, if the variant is that struct.
  private static byte[] rewrap(final Discriminator discriminator, final byte[] data) {
    assertEquals(8, discriminator.length());
    final byte[] rewrapped = new byte[discriminator.length() + data.length - PAYLOAD_OFFSET];
    final int i = discriminator.write(rewrapped, 0);
    System.arraycopy(data, PAYLOAD_OFFSET, rewrapped, i, data.length - PAYLOAD_OFFSET);
    return rewrapped;
  }

  /// The reader consumed exactly the rewrapped bytes, and writes the same bytes back, so no
  /// captured byte is lost in the reading.
  private static void assertSpansExactly(final int length, final byte[] rewrapped, final LoopscaleEvent event) {
    final var name = event.getClass().getSimpleName();
    assertEquals(length, rewrapped.length, () -> name + ": 8 + the payload after the variant byte");
    assertEquals(rewrapped.length, event.l(), () -> name + " spans the whole payload");
    final byte[] written = new byte[event.l()];
    assertEquals(written.length, event.write(written, 0), name);
    assertArrayEquals(rewrapped, written, () -> name + " writes the captured payload back");
  }

  /// The fixture's ground truth, which holds whatever the client declares, so a failure below is
  /// the client's and not a stale capture. Every captured event is the envelope, emitted from a
  /// top-level instruction, with the length and variant it had when captured, and the five rows
  /// account for every event in the file.
  @Test
  void everyCapturedCpiIsTheUndeclaredEnvelope() {
    final var fixture = fixture();
    final var envelopes = List.of(
        new Envelope(DEPOSIT_TX, 2, 2, "deposit_collateral", 123, 3),
        new Envelope(CLAIM_TX, 2, 1, "claim_vault_rewards", 161, 10),
        new Envelope(CLAIM_TX, 2, 2, "claim_vault_rewards", 49, 11),
        new Envelope(RESTAKE_TX, 2, 7, "unstake_user_vault_lp", 179, 6),
        new Envelope(RESTAKE_TX, 4, 9, "stake_user_vault_lp", 163, 5)
    );
    assertEquals(3, fixture.size(), "captured transactions");
    assertEquals(envelopes.size(), fixture.stream().mapToInt(tx -> tx.cpis().size()).sum(), "captured events");
    for (final var envelope : envelopes) {
      final var cpi = cpi(fixture, envelope.signature(), envelope.outer(), envelope.inner());
      final byte[] data = cpi.data();
      final var at = envelope.emitter() + " in " + envelope.signature();
      assertEquals(envelope.emitter(), cpi.emitter(), at);
      assertEquals(2, cpi.stackHeight(), at + ": invoked by a top-level instruction");
      assertArrayEquals(EVENT_IX_TAG, Arrays.copyOfRange(data, 0, 8), at + ": Anchor's EVENT_IX_TAG_LE");
      assertArrayEquals(LIVE_DISCRIMINATOR, Arrays.copyOfRange(data, 8, VARIANT_OFFSET), at + ": event discriminator");
      assertEquals(envelope.length(), data.length, at);
      assertEquals(envelope.variant(), data[VARIANT_OFFSET] & 0xFF, at + ": variant tag");
    }
  }

  /// The tripwire. The generated dispatcher knows only the six discriminators the IDL declares,
  /// none of which is the one the program writes, so it decodes no live event. Once it decodes
  /// one, the IDL the client is generated from describes the program's event enum, and the gap
  /// this class records has closed.
  @Test
  void theGeneratedReaderDoesNotKnowTheLiveDiscriminator() {
    for (final var tx : fixture()) {
      for (final var cpi : tx.cpis()) {
        assertNull(LoopscaleEvent.readCPI(cpi.data()), TRIPWIRE);
        for (final var event : IDL_EVENTS) {
          assertFalse(event.discriminator().equals(cpi.data(), 8),
              () -> event.name() + " declares the live discriminator ec7be27dddc8e438");
        }
      }
    }
  }

  /// The gap, measured on the three variants an IDL struct does fit. With the struct's own
  /// discriminator in place of the envelope's seventeen bytes, the generated reader spans the
  /// payload exactly, writes it back byte for byte, and returns fields the emitting instruction
  /// confirms.
  ///
  /// `StakeEvent.amount`, 41661469, is the LP amount the program actually staked: the amount of
  /// the LP `TransferChecked` the instruction invoked. It is not the instruction's `amount`
  /// argument, 40764025, because the instruction sets `stake_all = Some(true)` and the program
  /// staked the user's whole LP balance. Nobody should "correct" 41661469 to the argument.
  @Test
  void variantsFiveTenAndElevenAreIdlStructsBehindAVariantByte() {
    final var fixture = fixture();

    final byte[] staked = rewrap(StakeEvent.DISCRIMINATOR, cpi(fixture, RESTAKE_TX, 4, 9).data());
    final var stake = StakeEvent.read(staked, 0);
    assertSpansExactly(154, staked, stake);
    assertEquals("DKYiurDc3LizGuGANroduzc7ZDeBcET3WMeTWuoNAabJ", stake.user().toBase58(),
        "stake_user_vault_lp account 2, user; account 1, payer, holds the same key here");
    assertEquals("HDDUazSGtgrqYLwK8HWfRzHgc3SJhumKHvwGx3Hovnqf", stake.address().toBase58(),
        "stake_user_vault_lp account 5, vault_stake");
    assertEquals("AXanCP4dJHtWd7zY4X7nwxN5t5Gysfy2uG3XTxSmXdaB", stake.vaultAddress().toBase58(),
        "stake_user_vault_lp account 4, vault");
    assertEquals("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", stake.principalMint().toBase58(),
        "USDC, which is none of the instruction's accounts, so nothing independent confirms it");
    assertEquals(0, stake.durationType());
    assertEquals(41_661_469L, stake.amount(),
        "the LP TransferChecked under the instruction, not its amount argument; see the javadoc");
    assertEquals(1, stake.actionType());
    assertEquals(1_000_000L, stake.principalAmount(), "the instruction's VaultStakeParams.principal_amount");

    final byte[] claimed = rewrap(RewardsClaimedEvent.DISCRIMINATOR, cpi(fixture, CLAIM_TX, 2, 1).data());
    final var rewards = RewardsClaimedEvent.read(claimed, 0);
    assertSpansExactly(152, claimed, rewards);
    assertEquals("AXanCP4dJHtWd7zY4X7nwxN5t5Gysfy2uG3XTxSmXdaB", rewards.vaultAddress().toBase58(),
        "claim_vault_rewards account 3, vault");
    assertEquals("bs1PuRvB9rBBZkryBjADYvxc2qYh51EVW2fsb1uTiBN", rewards.userAddress().toBase58(),
        "claim_vault_rewards account 2, user, the same key as payer");
    assertEquals("ByhzKrXsvvAkMfMjYcnUFLTojMpe2s4MipPfE3axbiW5", rewards.stakeAccountAddress().toBase58(),
        "claim_vault_rewards account 6, stake_account");
    assertEquals(1L, rewards.amount());
    assertEquals("5YMkXAYccHSGnHn9nob9xEvv6Pvka9DZWH7nTbotTu9E", rewards.mint().toBase58(),
        "the instruction's mints[0]");
    assertEquals(transaction(fixture, CLAIM_TX).blockTime(), rewards.timestamp(), "the transaction's blockTime");

    final byte[] closed = rewrap(UserRewardsInfoClosed.DISCRIMINATOR, cpi(fixture, CLAIM_TX, 2, 2).data());
    final var close = UserRewardsInfoClosed.read(closed, 0);
    assertSpansExactly(40, closed, close);
    assertEquals("BwuqzcziGHPaTpwXenkiqp76NXWfmuQuzjeDF3HD1S1d", close.address().toBase58(),
        "claim_vault_rewards account 5, user_rewards_info");
  }

  /// Why reading past the variant byte is no decoder: the other two captured variants fit none
  /// of the six IDL events. Each reader, given its own discriminator before the payload, either
  /// runs off the end or stops short of it; which exception it throws is not the point.
  ///
  /// Observed, and asserted nowhere: variant 3, from `deposit_collateral`, reads as the `loan`
  /// account, a zero byte, the deposit mint, a one byte, the same key again, and a `u64` equal
  /// to `DepositCollateralParams.amount`. Variant 6, from `unstake_user_vault_lp`, holds the
  /// user, the vault stake, the vault and the USDC mint, then the block time at `[128, 136)` and
  /// an LP amount at `[145, 153)` of the payload after the variant byte.
  @Test
  void variantsThreeAndSixFitNoIdlStruct() {
    final var fixture = fixture();
    for (final var cpi : List.of(cpi(fixture, DEPOSIT_TX, 2, 2), cpi(fixture, RESTAKE_TX, 2, 7))) {
      for (final var event : IDL_EVENTS) {
        final byte[] rewrapped = rewrap(event.discriminator(), cpi.data());
        final LoopscaleEvent read;
        try {
          read = event.reader().apply(rewrapped, 0);
        } catch (final RuntimeException ranOffTheEnd) {
          continue;
        }
        assertNotNull(read, event.name());
        assertNotEquals(rewrapped.length, read.l(),
            () -> event.name() + " spans the whole " + cpi.emitter() + " payload");
      }
    }
  }
}

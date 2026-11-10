package software.sava.idl.clients.nt.bundle;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.nt.bundle.gen.NtbundlePDAs;
import software.sava.idl.clients.nt.bundle.gen.NtbundleProgram;
import software.sava.idl.clients.nt.bundle.gen.events.NtbundleEvent;
import software.sava.idl.clients.nt.bundle.gen.events.TokenSwitchRequested;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/// Pins the additions in neutral-trade/sdk v2.4.0, commit
/// 7d8b41044e757c378d9a20eddfafa507ee2ec15c. Instruction bytes, order and flags follow
/// src/generated/instructions/requestTokenSwitch.ts; the synthetic event fixtures follow
/// TokenSwitchRequested in src/idl/ntbundle.json. They are encoding vectors, not captured events
/// or evidence that a switch completes successfully on chain.
final class TokenSwitchTests {

  private static final HexFormat HEX = HexFormat.of();
  private static final BigInteger MIN_SHARES = new BigInteger("ffeeddccbbaa99887766554433221100", 16);
  private static final BigInteger SHARES_AFTER = new BigInteger("102132435465768798a9bacbdcedfe0f", 16);
  private static final BigInteger USER_SHARES = new BigInteger("112233445566778899aabbccddeeff00", 16);
  private static final BigInteger TOTAL_SHARES = new BigInteger("fedcba98765432100123456789abcdef", 16);
  private static final BigInteger REFERRED_DEPOSITS = BigInteger.ONE.shiftLeft(100).add(BigInteger.valueOf(7)).negate();

  private static byte[] keyBytes(final int fill) {
    final byte[] bytes = new byte[32];
    Arrays.fill(bytes, (byte) fill);
    return bytes;
  }

  private static PublicKey key(final int fill) {
    return PublicKey.createPubKey(keyBytes(fill));
  }

  @Test
  void requestTokenSwitchMatchesTheUpstreamBuilder() {
    final var invokedProgram = AccountMeta.createInvoked(key(99));
    // Every account is distinct so swapping any two positions fails the assertion below.
    final var ix = NtbundleProgram.requestTokenSwitch(
        invokedProgram,
        key(1), key(2), key(3), key(4), key(5), key(6), key(7), key(8), key(9), key(10), key(11),
        0x8877665544332211L, MIN_SHARES
    );
    assertEquals(List.of(key(1), key(2), key(3), key(4), key(5), key(6), key(7), key(8), key(9), key(10), key(11)),
        ix.accounts().stream().map(AccountMeta::publicKey).toList());
    final var writable = Set.of(0, 1, 2, 3, 5, 6, 7);
    for (int i = 0; i < ix.accounts().size(); ++i) {
      final var meta = ix.accounts().get(i);
      assertEquals(writable.contains(i), meta.write(), "writable account " + i);
      assertEquals(i == 0, meta.signer(), "signer account " + i);
    }
    final byte[] expected = HEX.parseHex(
        "bbe8ea18d7601435" + "1122334455667788" + "00112233445566778899aabbccddeeff");
    assertEquals(32, ix.data().length);
    assertArrayEquals(expected, ix.data());

    // Read independently specified bytes, rather than just reading the builder's output.
    final var args = NtbundleProgram.RequestTokenSwitchIxData.read(expected, 0);
    assertEquals(0x8877665544332211L, args.tokensToBurn());
    assertEquals(MIN_SHARES, args.minSharesOut());
  }

  @Test
  void tokenSwitchEventDecodesWithoutReferredDeposits() {
    assertEvent(false);
  }

  @Test
  void tokenSwitchEventDecodesNegativeReferredDeposits() {
    assertEvent(true);
  }

  /// Existing mainnet accounts sampled on 2026-11-10 for the unsigned zero-amount simulation
  /// that reached TokenAmountZero. Their owner and bundle links supply the seed inputs;
  /// expected addresses are the accounts themselves, not values computed by these helpers.
  /// Seed construction was also checked against the SDK's src/generated/pdas helpers.
  @Test
  void switchPdasMatchExistingMainnetAccounts() {
    final var program = PublicKey.fromBase58Encoded("BUNDDh4P5XviMm1f3gCvnq2qKx6TGosAGnoUK12e7cXU");
    final var user = PublicKey.fromBase58Encoded("CssAA4iigdYKNQzxZwJjWgguHVu2cqdXTgSh5gStS56p");
    final var source = PublicKey.fromBase58Encoded("J7qhMAKnB6G5dvoAN9281ufabajKbyGQxxd2bq6R7fPJ");
    final var target = PublicKey.fromBase58Encoded("nE1x7KQq2sm3GQrafQUUdBkSPPT52FmiMM9qAS1dgnC");
    assertEquals("5Lnd4QhMuLr9cNUk19cgAVEBuegm7Lru9AD3gb151Mfs",
        NtbundlePDAs.targetUserBundleAccount2PDA(program, user, target).publicKey().toBase58());
    assertEquals("EptaZDtXvoGv7Re6wB5Gftuk5YhLnwFmKpVzdF8xgbE",
        NtbundlePDAs.tokenWithdrawalRequestUserBundleAccountPDA(program, user, source).publicKey().toBase58());
    assertEquals("B5DPykNjTXxUrkALTFvzX4j311R7UmC6kYAh45S5FDYH",
        NtbundlePDAs.tokenWithdrawalRequestBundleTempDataPDA(program, source).publicKey().toBase58());
    assertEquals("9mtE6vhftC3wZ7yn5UYnDQE5jhiBdCHHVvkpikfAA9oJ",
        NtbundlePDAs.tokenWithdrawalRequestOracleDataPDA(program, source).publicKey().toBase58());
    assertEquals("Eq8eRPkfBtEqM9Gnxa7n1a3DqYVVcBXD2QDYVc28bVw",
        NtbundlePDAs.tokenWithdrawalRequestMintPDA(program, source).publicKey().toBase58());
    assertEquals("HVQGU7rBmVEA7vCvhMZfUyhq7jMhHVw2GdohtvDfE1hV",
        NtbundlePDAs.tokenWithdrawalRequestTokenizedBundleDepositorPDA(program, source).publicKey().toBase58());
  }

  /// The Option tag is at 272; Some carries a negative, non-i64 i128 at 273.
  /// ByteBuffer supplies only standard little-endian primitive encoding; the 128-bit values
  /// below are explicit wire bytes, independent of the generated serializers and ByteUtil.
  private static byte[] eventFixture(final boolean referredDeposits) {
    final var buffer = ByteBuffer.allocate(referredDeposits ? 289 : 273).order(ByteOrder.LITTLE_ENDIAN);
    buffer.put(HEX.parseHex("0e98d4e252ffc9fc"));
    buffer.put(keyBytes(21)); // user
    buffer.put(keyBytes(22)); // source_bundle
    buffer.put(keyBytes(23)); // target_bundle
    buffer.put(HEX.parseHex("00112233445566778899aabbccddeeff")); // shares_amount, unsigned
    buffer.putLong(101); // estimated_gross_redemption
    buffer.putLong(102); // estimated_net_to_destination
    buffer.putLong(103); // estimated_destination_deposit_fee
    buffer.putLong(104); // estimated_destination_pending_deposit
    buffer.putLong(-105); // cooldown_end_timestamp
    buffer.putLong(-106); // timestamp
    buffer.put(keyBytes(24)); // mint
    buffer.putLong(0x8877665544332211L); // tokens_burned, unsigned
    buffer.putLong(108); // export_value
    buffer.put(HEX.parseHex("0ffeeddccbbaa9988776655443322110")); // tokenized_depositor_shares_after
    buffer.putLong(109); // token_supply_after
    buffer.put(HEX.parseHex("00ffeeddccbbaa998877665544332211")); // user_shares_after
    buffer.put(HEX.parseHex("efcdab89674523011032547698badcfe")); // total_shares, unsigned
    buffer.put((byte) (referredDeposits ? 1 : 0));
    if (referredDeposits) {
      buffer.put(HEX.parseHex("f9ffffffffffffffffffffffefffffff")); // -(2^100 + 7)
    }
    assertFalse(buffer.hasRemaining(), "every fixture byte is specified");
    return buffer.array();
  }

  private static void assertEvent(final boolean referredDeposits) {
    final byte[] fixture = eventFixture(referredDeposits);
    final var event = assertInstanceOf(TokenSwitchRequested.class, NtbundleEvent.read(fixture));
    assertEquals(key(21), event.user());
    assertEquals(key(22), event.sourceBundle());
    assertEquals(key(23), event.targetBundle());
    assertEquals(MIN_SHARES, event.sharesAmount());
    assertEquals(101, event.estimatedGrossRedemption());
    assertEquals(102, event.estimatedNetToDestination());
    assertEquals(103, event.estimatedDestinationDepositFee());
    assertEquals(104, event.estimatedDestinationPendingDeposit());
    assertEquals(-105, event.cooldownEndTimestamp());
    assertEquals(-106, event.timestamp());
    assertEquals(key(24), event.mint());
    assertEquals(0x8877665544332211L, event.tokensBurned());
    assertEquals(108, event.exportValue());
    assertEquals(SHARES_AFTER, event.tokenizedDepositorSharesAfter());
    assertEquals(109, event.tokenSupplyAfter());
    assertEquals(USER_SHARES, event.userSharesAfter());
    assertEquals(TOTAL_SHARES, event.totalShares());
    assertEquals(referredDeposits ? REFERRED_DEPOSITS : null, event.referredNetDepositsAfter());
    assertEquals(referredDeposits ? 289 : 273, event.l());
    final byte[] written = new byte[fixture.length];
    assertEquals(fixture.length, event.write(written, 0));
    assertArrayEquals(fixture, written, "the writer reproduces the independently specified event");

    // Both the union dispatch and the concrete reader honor a nonzero start offset.
    final byte[] framed = new byte[fixture.length + 7];
    Arrays.fill(framed, (byte) 0x5a);
    System.arraycopy(fixture, 0, framed, 7, fixture.length);
    assertEquals(event, NtbundleEvent.read(framed, 7));
    assertEquals(event, TokenSwitchRequested.read(framed, 7));
  }
}

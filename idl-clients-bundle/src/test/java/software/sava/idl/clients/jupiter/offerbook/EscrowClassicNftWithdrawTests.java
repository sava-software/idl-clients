package software.sava.idl.clients.jupiter.offerbook;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.jupiter.offerbook.gen.OfferbookProgram;

import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/// `escrow_classic_nft_withdraw` moves a classic NFT from its owner's escrow to their wallet with
/// a token `TransferChecked` from `user_escrow_token_account` (position 4) into
/// `signer_nft_token_account` (position 1), so that account must reach the program writable: a
/// callee cannot write an account the caller holds read-only. The IDL republished at slot
/// 464256975 marks it writable and the one before did not, so a builder generated from the
/// earlier document worked only beside another instruction that marked it writable, as each live
/// call found does with the associated token account create it adds.
final class EscrowClassicNftWithdrawTests {

  /// A call the program accepted, top-level in
  /// `DDFCrJ8mKirgrQVMymmdpJs8jyRaYb3d3TNuCP7YmuERJdgedTEYJoNQLbfNqrNofSJqjxbrGwxKacmkqczkmmq`
  /// (slot 463833043, on the program deployed at slot 456774686): its data, the bare
  /// discriminator, its 8 accounts in order, and the flags the runtime handed the program.
  private static final byte[] LIVE_DATA = HexFormat.of().parseHex("3a664f10d0b6d923");
  private static final List<String> LIVE_ACCOUNTS = List.of(
      "GBHjPxtyiTz94Kta1QnA8B7HkP99S8ywAf2Si4D5demN",
      "CUuDJgpJLqfKsrSHhrDk6s2uPUgUCYxH6k6nyfmChytW",
      "CWgLbkVNZCaiZsfCRZpzYsfnoNvzxo1BmpgPiqecSTsb",
      "2hHmuubkN2se5yXC1GDDZnMshtfTiRshbjrQT42bCcoE",
      "61pT7VX6mshjqZSifhkGCcssCDNSZziAwNQQdyCjRCEZ",
      "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA",
      "4sZptGwLvk2ywxa2E8UosgogwrjTxjPfFAK7o2rcKHb9",
      "offerbkFMvVfpQhL8ZQ5iromnjct5rz3r52B9ewu3ie"
  );
  private static final List<String> LIVE_FLAGS = List.of("WS", "W", "W", "R", "W", "R", "R", "R");
  /// `signer_user` reached the program writable only because the same transaction's
  /// `repay_non_fungible_loan` declares it so; every classic withdraw found bundles one, and the
  /// withdraw itself only signs with it.
  private static final int BUNDLED_REPAY_SIGNER_USER = 2;

  private static PublicKey live(final int index) {
    return PublicKey.fromBase58Encoded(LIVE_ACCOUNTS.get(index));
  }

  @Test
  void theGeneratedBuilderReproducesTheLiveCall() {
    final var ix = OfferbookProgram.escrowClassicNftWithdraw(
        AccountMeta.createInvoked(live(7)),
        live(0), live(1), live(2), live(3), live(4), live(5), live(6), live(7)
    );

    assertEquals(LIVE_ACCOUNTS, ix.accounts().stream().map(meta -> meta.publicKey().toBase58()).toList(),
        "the accounts are the ones the program accepted");
    assertArrayEquals(LIVE_DATA, ix.data(), "the discriminator alone");

    final var flags = ix.accounts().stream()
        .map(meta -> (meta.write() ? "W" : "R") + (meta.signer() ? "S" : ""))
        .toList();
    assertEquals(LIVE_FLAGS.size(), flags.size());
    for (int i = 0; i < flags.size(); ++i) {
      if (i != BUNDLED_REPAY_SIGNER_USER) {
        assertEquals(LIVE_FLAGS.get(i), flags.get(i), "the flags the program received, at position " + i);
      }
    }
    assertEquals("W", flags.get(1), "signer_nft_token_account, which the transfer writes");
  }
}

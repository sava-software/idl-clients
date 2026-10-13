package software.sava.idl.clients.jupiter.offerbook;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.jupiter.offerbook.gen.OfferbookProgram;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Jupiter Offerbook's upgrade at slot 456376677 made `escrow_programmable_nft_withdraw` read a
/// trailing Borsh `Option<Vec<u8>>` that the program-metadata IDL this client is generated from
/// does not declare. The generated builder therefore sends the discriminator alone, and the program
/// rejects every such call with `InstructionDidNotDeserialize` (102) before it checks an account;
/// the same call with one more zero byte (`None`) reaches account validation.
///
/// A published IDL's field list is upstream's to correct, not a local patch's, so this pins the
/// shortfall rather than closing it. When Jupiter republishes the IDL the regenerated builder
/// changes and this test fails: rewrite it then to assert the full encoding.
final class EscrowProgrammableNftWithdrawTests {

  /// A post-upgrade call the program accepted, top-level in
  /// `5R8AQypDqEfCCMRqPVRCoiohUTe5qNjUgaz2K3kCyNjer9w4cCBGb3U4JxPoazswpfYwAVWLG8fGLV5dy3hoboSs`
  /// (slot 456377081): its instruction data and its 18 accounts, in order.
  private static final byte[] LIVE_DATA = HexFormat.of().parseHex("1c9d15ea3bdeaab200");
  private static final List<String> LIVE_ACCOUNTS = List.of(
      "BAosUdFfknnnd22KPms5DQAHrQ6ynjVmRJpfVcMtiXNN",
      "rpJC37Zx5rskxgeLJ6dMfYKSxN1w6o8C5CuGds2sogz",
      "F7F7iftWKZwfP4ZX6pd2P9DNjz46HjvGLNX6q8i4zAKY",
      "9ztpZUihnramkxSUJd29V7mCggaw7Fxvh6tLiKXtsU8e",
      "5tsx69ZBSjUQBn28sgA58xBBSUMTSJFCVDNXHmQHuZB3",
      "ErETm1QjwbsTPSZcZSp3im99MPfrhLQmrziFpUvRNeaw",
      "3goh6rqfVSuuobtUhJ6LKKnfmj4nxkYKGcj2CamkrRie",
      "FzkCKhtHhPPFBaUW9kKL4bTAWHBGXG7qbkDv3FftEif7",
      "5XJhDDvNnJX1mGq4Lh2dqvUVe7hgNRBa7Jq4GtVFbHtB",
      "eBJLFYPxJmMGKuFwpDWkzxZeUrad92kZRC5BJLpzyT9",
      "metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s",
      "Sysvar1nstructions1111111111111111111111111",
      "auth9SigNpDKz4sJJ1DfCTuZrZNSAgh9sFD3rboVmgg",
      "ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL",
      "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA",
      "11111111111111111111111111111111",
      "4sZptGwLvk2ywxa2E8UosgogwrjTxjPfFAK7o2rcKHb9",
      "offerbkFMvVfpQhL8ZQ5iromnjct5rz3r52B9ewu3ie"
  );

  private static PublicKey live(final int index) {
    return PublicKey.fromBase58Encoded(LIVE_ACCOUNTS.get(index));
  }

  @Test
  void theGeneratedBuilderStopsOneByteShortOfWhatTheProgramReads() {
    final var program = AccountMeta.createInvoked(live(17));
    final var ix = OfferbookProgram.escrowProgrammableNftWithdraw(
        program,
        SolanaAccounts.MAIN_NET,
        live(0), live(1), live(2), live(3), live(4), live(5), live(6), live(7), live(8), live(9),
        live(10), live(12), live(14), live(16), live(17)
    );

    assertEquals(LIVE_ACCOUNTS, ix.accounts().stream().map(meta -> meta.publicKey().toBase58()).toList(),
        "the accounts are the ones the program accepted");

    final byte[] data = ix.data();
    assertArrayEquals(OfferbookProgram.ESCROW_PROGRAMMABLE_NFT_WITHDRAW_DISCRIMINATOR.data(), data,
        "what the stale IDL declares: the discriminator alone");
    assertEquals(LIVE_DATA.length - 1, data.length, "the shortfall is exactly one byte");
    assertArrayEquals(Arrays.copyOf(LIVE_DATA, data.length), data, "the live call leads with the same discriminator");
    assertEquals(0, LIVE_DATA[data.length], "and ends with the Option tag for None");
  }
}

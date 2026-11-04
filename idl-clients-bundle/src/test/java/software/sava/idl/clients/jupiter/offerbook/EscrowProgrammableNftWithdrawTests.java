package software.sava.idl.clients.jupiter.offerbook;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.offerbook.gen.OfferbookProgram;
import software.sava.idl.clients.jupiter.offerbook.gen.OfferbookProgram.EscrowProgrammableNftWithdrawIxData;

import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/// Jupiter Offerbook's upgrade at slot 456376677 made `escrow_programmable_nft_withdraw` read a
/// trailing Borsh `Option<Vec<u8>>`, `authorization_data`. The program-metadata IDL did not declare
/// it until a Squads vault transaction rewrote that account at slot 464256975, so the builder
/// generated from the earlier document sent the discriminator alone, and the program rejected
/// every such call with `InstructionDidNotDeserialize` (102) before it checked an account.
///
/// This pins the regenerated builder to a call the program accepted, byte for byte, so a
/// regeneration that loses the argument again fails here instead of on chain.
/// [OfferbookOptionArgumentTests] pins the six instructions whose arguments the upgrade at slot
/// 456774686 extended: three to calls the program accepted, the other three to the widths the
/// program was simulated to read.
final class EscrowProgrammableNftWithdrawTests {

  private static final HexFormat HEX = HexFormat.of();

  /// A post-upgrade call the program accepted, top-level in
  /// `5R8AQypDqEfCCMRqPVRCoiohUTe5qNjUgaz2K3kCyNjer9w4cCBGb3U4JxPoazswpfYwAVWLG8fGLV5dy3hoboSs`
  /// (slot 456377081): its instruction data, the discriminator and then the Option tag for `None`,
  /// and its 18 accounts, in order.
  private static final byte[] LIVE_DATA = HEX.parseHex("1c9d15ea3bdeaab2" + "00");
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

  /// A call on the program deployed at slot 456774686, top-level in
  /// `WAqzKdMXNgRcNNLTWtBGLkCu3odnQXAdFaotorvzgnJrHANadrQnEd9EUKjS2YSqgbKQqxAazZHZLpkPHTFtu3j`
  /// (slot 464082563), with the same data, its 18 accounts and the flags the runtime handed the
  /// program. The only instruction sharing accounts with it is an associated token account create
  /// of the signer's NFT token account, and the withdraw's own transfer needs the signer and that
  /// account writable anyway. The call above pins no flags: a `repay_non_fungible_loan` in its
  /// transaction made `signer_user` and `authorization_rules` writable.
  private static final List<String> CURRENT_ACCOUNTS = List.of(
      "kAeh7DRXWRWvp1hm7vCkKK2nWGma9uk2BNRWipzDKSG",
      "GqTCAZAfj1amjsgZZN4YwUmKhSfTGKyD2FcKpF9E1w2B",
      "BwLxucF2ui4kCWWSaD1aHTYj9g9WxQnnQwn6mDkXUUA3",
      "4cwLp7vZjLuJh8uhX9ykSTWVrxwGWc4te1jKmeUWV1tm",
      "55GzFrfAabktHM8cNqsc6pc1fWABBKM2zKdh25GgfuGw",
      "BMHfk7cThwpwbF4Q8LoLBS4EQvoSk5N4AnKAueE7973V",
      "AyuVenwJhtZy4kXYWohG9817s2YTMPbkpSiGkMj5kKMw",
      "5HAZqBumR14BEwaJfddqoET6oR71iABjksw93qCdCCU7",
      "2RCVUpc3ZtJjkiXmfL8Kc76AhvsZgojveprSAQQjf4Ft",
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
  private static final String CURRENT_FLAGS = "WS R R W R W W W W R R R R R R R R R";

  private static PublicKey current(final int index) {
    return PublicKey.fromBase58Encoded(CURRENT_ACCOUNTS.get(index));
  }

  /// Each account's flags, `W` or `R` and then `S` for a signer, in order.
  private static String flagsOf(final Instruction ix) {
    return ix.accounts().stream()
        .map(meta -> (meta.write() ? "W" : "R") + (meta.signer() ? "S" : ""))
        .collect(Collectors.joining(" "));
  }

  @Test
  void theGeneratedBuilderReproducesTheLiveCall() {
    final var program = AccountMeta.createInvoked(live(17));
    final var ix = OfferbookProgram.escrowProgrammableNftWithdraw(
        program,
        SolanaAccounts.MAIN_NET,
        live(0), live(1), live(2), live(3), live(4), live(5), live(6), live(7), live(8), live(9),
        live(10), live(12), live(14), live(16), live(17),
        null
    );

    assertEquals(LIVE_ACCOUNTS, ix.accounts().stream().map(meta -> meta.publicKey().toBase58()).toList(),
        "the accounts are the ones the program accepted");
    assertArrayEquals(LIVE_DATA, ix.data(), "the discriminator, then the Option tag for None");
    assertNull(EscrowProgrammableNftWithdrawIxData.read(LIVE_DATA, 0).authorizationData(),
        "and the generated reader decodes the live call's argument as None");
  }

  @Test
  void theGeneratedBuilderReproducesACallOnTheCurrentProgram() {
    final var ix = OfferbookProgram.escrowProgrammableNftWithdraw(
        AccountMeta.createInvoked(current(17)),
        SolanaAccounts.MAIN_NET,
        current(0), current(1), current(2), current(3), current(4), current(5), current(6), current(7), current(8),
        current(9), current(10), current(12), current(14), current(16), current(17),
        null
    );

    assertEquals(CURRENT_ACCOUNTS, ix.accounts().stream().map(meta -> meta.publicKey().toBase58()).toList(),
        "the accounts are the ones the program accepted");
    assertEquals(CURRENT_FLAGS, flagsOf(ix), "with the flags the program received");
    assertArrayEquals(LIVE_DATA, ix.data(), "the discriminator, then the Option tag for None");
  }

  /// No live call in hand carries authorization data. `Some` is Borsh's: the tag `01`, the length
  /// as a little-endian `u32`, then the bytes, written out here by hand.
  @Test
  void someAuthorizationDataIsTheTagThenALengthPrefixedVector() {
    final var expected = HEX.parseHex("1c9d15ea3bdeaab2" + "01" + "03000000" + "010203");
    final var ix = OfferbookProgram.escrowProgrammableNftWithdraw(
        AccountMeta.createInvoked(live(17)), List.of(), new byte[]{1, 2, 3}
    );
    assertArrayEquals(expected, ix.data());
    assertArrayEquals(new byte[]{1, 2, 3}, EscrowProgrammableNftWithdrawIxData.read(expected, 0).authorizationData());
  }
}

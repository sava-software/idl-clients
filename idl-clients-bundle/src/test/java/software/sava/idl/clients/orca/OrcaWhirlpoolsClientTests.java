package software.sava.idl.clients.orca;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.orca.whirlpools.gen.types.OpenPositionBumps;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the Whirlpools client's convenience overloads.
///
/// The full-argument builders are thin delegations to the generated program, but
/// the `default` overloads exist to *derive* accounts the caller would otherwise
/// have to compute — the position PDA, its associated token account, and the
/// whirlpool's oracle. Those derivations are the part worth pinning: every one
/// produces a `PublicKey` that looks plausible in any slot, so a wrong seed or a
/// swapped argument yields an instruction that assembles cleanly and then
/// addresses the wrong account on-chain.
final class OrcaWhirlpoolsClientTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final OrcaAccounts ORCA_ACCOUNTS = OrcaAccounts.MAIN_NET;
  private static final OrcaWhirlpoolsClient CLIENT =
      OrcaWhirlpoolsClient.createClient(SOLANA_ACCOUNTS, ORCA_ACCOUNTS);

  private static final PublicKey FUNDER = key(0x11);
  private static final PublicKey OWNER = key(0x12);
  private static final PublicKey POSITION_MINT = key(0x13);
  private static final PublicKey WHIRLPOOL = key(0x14);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static List<PublicKey> keys(final Instruction ix) {
    return ix.accounts().stream().map(AccountMeta::publicKey).toList();
  }

  @Test
  void clientBindsItsIdentity() {
    assertEquals(SOLANA_ACCOUNTS, CLIENT.solanaAccounts());
    assertEquals(ORCA_ACCOUNTS, CLIENT.orcaAccounts());

    // the short factory defaults the Solana accounts to main net
    final var defaulted = OrcaWhirlpoolsClient.createClient(ORCA_ACCOUNTS);
    assertNotNull(defaulted);
    assertEquals(SolanaAccounts.MAIN_NET, defaulted.solanaAccounts());
    assertEquals(ORCA_ACCOUNTS, defaulted.orcaAccounts());
  }

  /// Each derivation must be deterministic, depend on every input, and stay
  /// distinct from its same-shaped neighbours.
  @Test
  void derivationsAreDeterministicAndInputDependent() {
    final var position = CLIENT.derivePositionKey(POSITION_MINT);
    final var oracle = CLIENT.deriveOracleKey(WHIRLPOOL);
    final var ata = CLIENT.deriveATA(OWNER, SOLANA_ACCOUNTS.tokenProgram(), POSITION_MINT);

    assertEquals(position, CLIENT.derivePositionKey(POSITION_MINT), "deterministic");
    assertEquals(oracle, CLIENT.deriveOracleKey(WHIRLPOOL), "deterministic");
    assertEquals(ata, CLIENT.deriveATA(OWNER, SOLANA_ACCOUNTS.tokenProgram(), POSITION_MINT));

    // every input participates
    assertNotEquals(position, CLIENT.derivePositionKey(key(0x21)));
    assertNotEquals(oracle, CLIENT.deriveOracleKey(key(0x21)));
    assertNotEquals(ata, CLIENT.deriveATA(key(0x21), SOLANA_ACCOUNTS.tokenProgram(), POSITION_MINT));
    assertNotEquals(ata, CLIENT.deriveATA(OWNER, SOLANA_ACCOUNTS.tokenProgram(), key(0x21)));
    assertNotEquals(ata, CLIENT.deriveATA(OWNER, SOLANA_ACCOUNTS.token2022Program(), POSITION_MINT));

    // the position PDA and the oracle take the same shape of input but are not
    // the same derivation
    assertNotEquals(CLIENT.derivePositionKey(WHIRLPOOL), CLIENT.deriveOracleKey(WHIRLPOOL));
    // and neither collides with the mint or pool it derives from
    assertNotEquals(POSITION_MINT, position);
    assertNotEquals(WHIRLPOOL, oracle);
  }

  /// `openPosition`'s short form supplies the position PDA, the owner's ATA for
  /// the position mint, and the token program — the explicit call must agree.
  @Test
  void openPositionShortFormDerivesThePositionAndItsAta() {
    final var bumps = new OpenPositionBumps(254);
    final var tokenProgram = SOLANA_ACCOUNTS.tokenProgram();

    final var shortForm = CLIENT.openPosition(
        FUNDER, OWNER, POSITION_MINT, WHIRLPOOL, bumps, -128, 128);
    final var explicit = CLIENT.openPosition(
        FUNDER,
        OWNER,
        CLIENT.derivePositionKey(POSITION_MINT),
        POSITION_MINT,
        CLIENT.deriveATA(OWNER, tokenProgram, POSITION_MINT),
        WHIRLPOOL,
        tokenProgram,
        bumps,
        -128,
        128);

    assertEquals(keys(explicit), keys(shortForm));
    assertArrayEquals(explicit.data(), shortForm.data());
    assertEquals(ORCA_ACCOUNTS.invokedWhirlpoolProgram(), shortForm.programId());

    // the derived accounts really are present, and are not the mint itself
    final var accounts = keys(shortForm);
    assertTrue(accounts.contains(CLIENT.derivePositionKey(POSITION_MINT)));
    assertTrue(accounts.contains(CLIENT.deriveATA(OWNER, tokenProgram, POSITION_MINT)));

    // the tick bounds are data: changing them must not move an account
    final var wider = CLIENT.openPosition(FUNDER, OWNER, POSITION_MINT, WHIRLPOOL, bumps, -256, 256);
    assertEquals(accounts, keys(wider));
    assertFalse(Arrays.equals(shortForm.data(), wider.data()));

    // funder and owner occupy their own slots
    assertNotEquals(accounts, keys(CLIENT.openPosition(
        OWNER, FUNDER, POSITION_MINT, WHIRLPOOL, bumps, -128, 128)));
  }

  /// `swap`'s short form supplies the whirlpool's oracle. Passing the whirlpool
  /// itself, or another same-shaped account, would still assemble.
  @Test
  void swapShortFormDerivesTheOracle() {
    final var tokenProgram = SOLANA_ACCOUNTS.tokenProgram();
    final var authority = key(0x21);
    final var ownerA = key(0x22);
    final var vaultA = key(0x23);
    final var ownerB = key(0x24);
    final var vaultB = key(0x25);
    final var tick0 = key(0x26);
    final var tick1 = key(0x27);
    final var tick2 = key(0x28);
    final var limit = BigInteger.ONE.shiftLeft(64);

    final var shortForm = CLIENT.swap(
        tokenProgram, authority, WHIRLPOOL, ownerA, vaultA, ownerB, vaultB,
        tick0, tick1, tick2, 1_000L, 900L, limit, true, true);
    final var explicit = CLIENT.swap(
        tokenProgram, authority, WHIRLPOOL, ownerA, vaultA, ownerB, vaultB,
        tick0, tick1, tick2, CLIENT.deriveOracleKey(WHIRLPOOL),
        1_000L, 900L, limit, true, true);

    assertEquals(keys(explicit), keys(shortForm));
    assertArrayEquals(explicit.data(), shortForm.data());

    final var accounts = keys(shortForm);
    assertTrue(accounts.contains(CLIENT.deriveOracleKey(WHIRLPOOL)), "the oracle is derived and passed");
    assertNotEquals(WHIRLPOOL, CLIENT.deriveOracleKey(WHIRLPOOL),
        "the oracle is a distinct account from the pool");

    // the tick arrays keep their order — all three are same-typed
    assertEquals(List.of(tick0, tick1, tick2),
        accounts.stream().filter(k -> k.equals(tick0) || k.equals(tick1) || k.equals(tick2)).toList());

    // amounts and direction flags are data
    final var reversed = CLIENT.swap(
        tokenProgram, authority, WHIRLPOOL, ownerA, vaultA, ownerB, vaultB,
        tick0, tick1, tick2, 1_000L, 900L, limit, true, false);
    assertEquals(accounts, keys(reversed));
    assertFalse(Arrays.equals(shortForm.data(), reversed.data()));

    assertEquals(ORCA_ACCOUNTS.invokedWhirlpoolProgram(), shortForm.programId());
  }
}

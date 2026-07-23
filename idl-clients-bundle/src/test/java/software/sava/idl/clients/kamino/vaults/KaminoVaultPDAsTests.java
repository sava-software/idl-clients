package software.sava.idl.clients.kamino.vaults;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.metaplex.MetaplexAccounts;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// Pins the vault PDA helpers offline: determinism, per-input sensitivity, and
/// separation between the same-shaped seed prefixes. Where the repo carries an
/// independent second implementation of the same derivation —
/// `KaminoAccounts.cTokenVault`/`kVaultGlobalConfig` and Metaplex's
/// `metadataPDA` — the two are cross-pinned, which anchors the seeds without
/// restating them.
final class KaminoVaultPDAsTests {

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static final PublicKey VAULT_STATE = key(0x11);
  private static final PublicKey RESERVE = key(0x12);
  private static final PublicKey PROGRAM = KaminoAccounts.MAIN_NET.kVaultsProgram();

  @Test
  void vaultDerivationsAreDeterministicSensitiveAndSeparated() {
    final var authority = KaminoVaultPDAs.baseVaultAuthority(VAULT_STATE, PROGRAM);
    final var tokenVault = KaminoVaultPDAs.tokenVault(VAULT_STATE, PROGRAM);
    final var sharesMint = KaminoVaultPDAs.sharesMint(VAULT_STATE, PROGRAM);

    // deterministic
    assertEquals(authority.publicKey(), KaminoVaultPDAs.baseVaultAuthority(VAULT_STATE, PROGRAM).publicKey());

    // the three per-vault derivations share the (prefix, vaultState) shape and
    // must not collapse into each other
    assertNotEquals(authority.publicKey(), tokenVault.publicKey());
    assertNotEquals(authority.publicKey(), sharesMint.publicKey());
    assertNotEquals(tokenVault.publicKey(), sharesMint.publicKey());

    // sensitive to the vault and to the program
    assertNotEquals(authority.publicKey(), KaminoVaultPDAs.baseVaultAuthority(key(0x13), PROGRAM).publicKey());
    assertNotEquals(authority.publicKey(), KaminoVaultPDAs.baseVaultAuthority(VAULT_STATE, key(0x14)).publicKey());
  }

  @Test
  void ctokenVaultAndGlobalConfigCrossPinTheKaminoAccountsDerivations() {
    assertEquals(
        KaminoAccounts.cTokenVault(VAULT_STATE, RESERVE, PROGRAM).publicKey(),
        KaminoVaultPDAs.ctokenVault(VAULT_STATE, RESERVE, PROGRAM).publicKey(),
        "two independent implementations of the ctoken vault must agree");
    // vault and reserve are same-typed; swapping them must move the address
    assertNotEquals(
        KaminoVaultPDAs.ctokenVault(VAULT_STATE, RESERVE, PROGRAM).publicKey(),
        KaminoVaultPDAs.ctokenVault(RESERVE, VAULT_STATE, PROGRAM).publicKey());

    assertEquals(
        KaminoAccounts.kVaultGlobalConfig(PROGRAM).publicKey(),
        KaminoVaultPDAs.globalConfig(PROGRAM).publicKey(),
        "two independent implementations of the global config must agree");
    assertNotEquals(
        KaminoVaultPDAs.globalConfig(PROGRAM).publicKey(),
        KaminoVaultPDAs.eventAuthority(PROGRAM).publicKey());

    // the event authority is the Anchor __event_authority the client constants
    // carry — cross-pin against the wired KaminoAccounts value
    assertEquals(
        KaminoAccounts.MAIN_NET.kVaultsEventAuthority(),
        KaminoVaultPDAs.eventAuthority(PROGRAM).publicKey());
  }

  @Test
  void whitelistedReserveProgramDataAndMetadata() {
    final var whitelisted = KaminoVaultPDAs.whitelistedReserve(RESERVE, PROGRAM);
    assertEquals(whitelisted.publicKey(), KaminoVaultPDAs.whitelistedReserve(RESERVE, PROGRAM).publicKey());
    assertNotEquals(whitelisted.publicKey(), KaminoVaultPDAs.whitelistedReserve(key(0x15), PROGRAM).publicKey());

    // programData is derived under the loader, keyed by the program id
    final var loader = key(0x16);
    final var programData = KaminoVaultPDAs.programData(PROGRAM, loader);
    assertNotEquals(programData.publicKey(), KaminoVaultPDAs.programData(key(0x17), loader).publicKey());
    assertNotEquals(programData.publicKey(), KaminoVaultPDAs.programData(PROGRAM, key(0x18)).publicKey());

    // the shares metadata is the Metaplex metadata PDA — cross-pin the
    // independent MetaplexAccounts implementation. The factory is called from
    // inside the test (never through the static MAIN_NET field alone): coverage
    // attributed to a static initializer is unstable under PIT, and the
    // factory's own wiring deserves a direct assertion anyway.
    final var metaplex = MetaplexAccounts.createAccounts(MetaplexAccounts.MAIN_NET.tokenMetadataProgram());
    assertEquals(MetaplexAccounts.MAIN_NET.tokenMetadataProgram(), metaplex.tokenMetadataProgram());
    final var byBase58 = MetaplexAccounts.createAccounts("metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s");
    assertEquals(metaplex.tokenMetadataProgram(), byBase58.tokenMetadataProgram(),
        "the String factory must decode to the same program");
    final var metadataProgram = metaplex.tokenMetadataProgram();
    final var sharesMint = key(0x19);
    assertEquals(
        MetaplexAccounts.metadataPDA(metadataProgram, sharesMint).publicKey(),
        KaminoVaultPDAs.sharesMetadata(sharesMint, metadataProgram).publicKey(),
        "the shares metadata must be the Metaplex metadata PDA");
    assertEquals(
        metaplex.metadataPDA(sharesMint).publicKey(),
        KaminoVaultPDAs.sharesMetadata(sharesMint, metadataProgram).publicKey(),
        "the instance default binds its own program");
  }
}

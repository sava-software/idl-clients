package software.sava.idl.clients.kamino.farms;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.KaminoAccounts;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// Pins the farm PDA helpers offline: determinism, per-input sensitivity, and
/// separation between the same-shaped vault prefixes (`tvault`/`fvault`/
/// `rvault` all derive from (prefix, state, mint)).
final class KaminoFarmAccountsTests {

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static final PublicKey FARM_STATE = key(0x11);
  private static final PublicKey MINT = key(0x12);
  private static final PublicKey OWNER = key(0x13);
  private static final PublicKey PROGRAM = KaminoAccounts.MAIN_NET.farmProgram();

  @Test
  void vaultPrefixesSeparateAndInputsMove() {
    final var treasury = KaminoFarmAccounts.treasuryVaultPDA(FARM_STATE, MINT, PROGRAM);
    final var farmVault = KaminoFarmAccounts.farmVaultPDA(FARM_STATE, MINT, PROGRAM);
    final var rewardVault = KaminoFarmAccounts.rewardVaultPDA(FARM_STATE, MINT, PROGRAM);

    // deterministic
    assertEquals(treasury.publicKey(), KaminoFarmAccounts.treasuryVaultPDA(FARM_STATE, MINT, PROGRAM).publicKey());

    // all three share the (prefix, state, mint) shape and must not collapse
    assertNotEquals(treasury.publicKey(), farmVault.publicKey());
    assertNotEquals(treasury.publicKey(), rewardVault.publicKey());
    assertNotEquals(farmVault.publicKey(), rewardVault.publicKey());

    // each input moves the address, and state/mint are not interchangeable
    assertNotEquals(farmVault.publicKey(), KaminoFarmAccounts.farmVaultPDA(key(0x14), MINT, PROGRAM).publicKey());
    assertNotEquals(farmVault.publicKey(), KaminoFarmAccounts.farmVaultPDA(FARM_STATE, key(0x15), PROGRAM).publicKey());
    assertNotEquals(farmVault.publicKey(), KaminoFarmAccounts.farmVaultPDA(MINT, FARM_STATE, PROGRAM).publicKey());
  }

  @Test
  void authoritiesAndUserState() {
    final var treasuryAuthority = KaminoFarmAccounts.treasuryAuthorityPDA(FARM_STATE, PROGRAM);
    final var farmAuthority = KaminoFarmAccounts.getFarmAuthorityPDA(FARM_STATE, PROGRAM);

    // both authorities share the ("authority", key) seed shape by design, so
    // for the same key they are the same address — pinned deliberately: a seed
    // change in either would break this equality and deserves a look
    assertEquals(treasuryAuthority.publicKey(), farmAuthority.publicKey());
    assertNotEquals(farmAuthority.publicKey(),
        KaminoFarmAccounts.getFarmAuthorityPDA(key(0x16), PROGRAM).publicKey());
    assertNotEquals(farmAuthority.publicKey(),
        KaminoFarmAccounts.getFarmAuthorityPDA(FARM_STATE, key(0x17)).publicKey());

    final var userState = KaminoFarmAccounts.userStatePDA(FARM_STATE, OWNER, PROGRAM);
    assertEquals(userState.publicKey(), KaminoFarmAccounts.userStatePDA(FARM_STATE, OWNER, PROGRAM).publicKey());
    assertNotEquals(userState.publicKey(), KaminoFarmAccounts.userStatePDA(FARM_STATE, key(0x18), PROGRAM).publicKey());
    assertNotEquals(userState.publicKey(), KaminoFarmAccounts.userStatePDA(OWNER, FARM_STATE, PROGRAM).publicKey(),
        "state and owner are not interchangeable");
    // and the user state does not collide with the same-shaped vault family
    assertNotEquals(userState.publicKey(), KaminoFarmAccounts.farmVaultPDA(FARM_STATE, OWNER, PROGRAM).publicKey());
  }
}

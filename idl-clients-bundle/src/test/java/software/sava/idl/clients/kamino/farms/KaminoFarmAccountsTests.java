package software.sava.idl.clients.kamino.farms;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.KaminoAccounts;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// Pins the farm PDA helpers: determinism, per-input sensitivity, separation
/// between the same-shaped vault prefixes (`tvault`/`fvault`/`rvault` all
/// derive from (prefix, state, mint)), and the addresses live accounts hold.
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

  /// Mainnet farm 2hbG59WP, read at slot 456605143: the addresses it stores are the program's own
  /// derivations, and its global config's treasury vault for the BONK reward is a live token
  /// account of that mint (slot 456605334).
  @Test
  void farmDerivationsReproduceALiveFarm() {
    final var farm = PublicKey.fromBase58Encoded("2hbG59WPGQL72vwNmmrrHQmtSfQdWY5CUi2EDUt22UJm");
    final var globalConfig = PublicKey.fromBase58Encoded("6UodrBjL2ZreDy7QdR4YV1oxqMBjVYSEyrFpctqqwGwL");
    final var stakeMint = PublicKey.fromBase58Encoded("AZo7YWPre83XySaVuwPVjeZPu9DnMsR1UEJcqkPjUtAh");
    final var rewardMint = PublicKey.fromBase58Encoded("DezXAZ8z7PnrnRJjz3wXBoRgixCa6xjnB7YaB1pPB263");

    assertEquals(PublicKey.fromBase58Encoded("Em8i9gn4ZHF3ibtPzGpZMoiroYQ3TwwEDL87C76bvLFu"),
        KaminoFarmAccounts.getFarmAuthorityPDA(farm, PROGRAM).publicKey());
    assertEquals(PublicKey.fromBase58Encoded("93Bzk3y6DNGNKNunXY92otTvx925wjv9qJZH68MEGNFu"),
        KaminoFarmAccounts.farmVaultPDA(farm, stakeMint, PROGRAM).publicKey());
    assertEquals(PublicKey.fromBase58Encoded("7M1Lzssao6LdgBEVK7CKrwDSs9wg1H76XS69pGUqzXAu"),
        KaminoFarmAccounts.rewardVaultPDA(farm, rewardMint, PROGRAM).publicKey());
    assertEquals(PublicKey.fromBase58Encoded("76Nzx5DqDre5SSP7wyNTHzYTvqeQaf1V9ZSCV7pofcH8"),
        KaminoFarmAccounts.treasuryVaultPDA(globalConfig, rewardMint, PROGRAM).publicKey());
  }

  /// A user state is seeded with its delegatee, which is its owner only on a farm that is not
  /// delegated. Two mainnet user states, read at slot 456605143: ANquwaRS on 2hbG59WP, not
  /// delegated, and 9q5mggCf on the delegated 3DY4Ch6v, whose owner derives a different address.
  @Test
  void userStatesAreSeededWithTheirDelegatee() {
    final var own = PublicKey.fromBase58Encoded("79nBgvQX8EdGxJbWmnVArrihTCZwSdsqLoyUzUHRJxuw");
    assertEquals(PublicKey.fromBase58Encoded("ANquwaRSA4GTpCCxaRWWmv7zBdTH9kigVs9xSDk4eoab"),
        KaminoFarmAccounts.userStatePDA(
            PublicKey.fromBase58Encoded("2hbG59WPGQL72vwNmmrrHQmtSfQdWY5CUi2EDUt22UJm"), own, PROGRAM
        ).publicKey());

    final var delegatedFarm = PublicKey.fromBase58Encoded("3DY4Ch6vhWuZmHKjgw2T2jB3T5vBmpPiBZJTFdgLWnZ2");
    final var delegatee = PublicKey.fromBase58Encoded("6DbmzaGBwnGXZpDVnsDQ48UrsrvRjmdvFYM8NYXCxC3H");
    final var owner = PublicKey.fromBase58Encoded("5Brmo6rmwKPBD5TSuJeygePfijLXEJgkPYJWumENhNPq");
    final var userState = PublicKey.fromBase58Encoded("9q5mggCfa8UJRc1vqQ6bEJoLxvQysHhGVJqgDNtK2ebc");
    assertEquals(userState, KaminoFarmAccounts.userStatePDA(delegatedFarm, delegatee, PROGRAM).publicKey());
    assertNotEquals(userState, KaminoFarmAccounts.userStatePDA(delegatedFarm, owner, PROGRAM).publicKey());
  }
}

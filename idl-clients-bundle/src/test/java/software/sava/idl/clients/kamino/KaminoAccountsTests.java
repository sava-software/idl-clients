package software.sava.idl.clients.kamino;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.scope.ScopeFeedAccounts;

import java.util.Arrays;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the Kamino address constants and PDA helpers.
///
/// Each PDA is derived from a seed prefix plus caller-supplied keys, so the
/// assertions here are the properties that hold regardless of the exact seed
/// encoding: a derivation is deterministic, every input actually participates
/// (change one, the address moves), and each `default` overload binds the
/// *correct* program id. That last one matters — the helpers on this interface
/// span three different programs, and `cTokenVault`/`kVaultGlobalConfig` bind
/// the kVaults program while their neighbours bind kLend. Deriving against the
/// wrong program yields an address that simply does not exist on-chain.
///
/// The seed encodings themselves are pinned against real mainnet accounts in
/// the per-program PDA tests, per AGENTS.md.
final class KaminoAccountsTests {

  private static final KaminoAccounts ACCOUNTS = KaminoAccounts.MAIN_NET;

  private static final PublicKey MARKET = key(0x11);
  private static final PublicKey MINT = key(0x12);
  private static final PublicKey USER = key(0x13);
  private static final PublicKey REFERRER = key(0x14);
  private static final PublicKey RESERVE = key(0x15);
  private static final PublicKey VAULT = key(0x16);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  /// Asserts a derivation is deterministic and that each listed variant — one
  /// input changed — moves the address.
  @SafeVarargs
  private static void assertDerivation(final String name,
                                       final Supplier<PublicKey> derive,
                                       final Supplier<PublicKey>... variants) {
    final var derived = derive.get();
    assertNotNull(derived, name);
    assertEquals(derived, derive.get(), name + " must be deterministic");
    for (int i = 0; i < variants.length; i++) {
      assertNotEquals(derived, variants[i].get(), name + " must depend on input " + i);
    }
  }

  @Test
  void isNullKeyRecognisesBothSentinels() {
    assertTrue(KaminoAccounts.isNullKey(null));
    assertTrue(KaminoAccounts.isNullKey(PublicKey.NONE));
    assertTrue(KaminoAccounts.isNullKey(KaminoAccounts.NULL_KEY));
    assertEquals("nu11111111111111111111111111111111111111111", KaminoAccounts.NULL_KEY.toBase58());
    // the two sentinels are genuinely different keys, so both checks are load bearing
    assertNotEquals(PublicKey.NONE, KaminoAccounts.NULL_KEY);
    assertFalse(KaminoAccounts.isNullKey(MARKET));
  }

  @Test
  void constantsAreWired() {
    assertEquals("KLend2g3cP87fffoy8q1mQqGKjrxjC8boSyAYavgmjD", ACCOUNTS.kLendProgram().toBase58());
    assertEquals("284iwGtA9X9aLy3KsyV8uT2pXLARhYbiSi5SiM2g47M2", ACCOUNTS.mainMarketLUT().toBase58());
    assertEquals("HFn8GnPADiny6XqUoWE8uRPPxb29ikn4yTuPa9MF2fWJ", ACCOUNTS.scopePricesProgram().toBase58());
    assertEquals("FarmsPZpWu9i7Kky8tPN37rs2TpmMrAZrC7S7vJa91Hr", ACCOUNTS.farmProgram().toBase58());
    assertEquals("6UodrBjL2ZreDy7QdR4YV1oxqMBjVYSEyrFpctqqwGwL", ACCOUNTS.farmsGlobalConfig().toBase58());
    assertEquals("KvauGMspG5k6rtzrqqn7WNn3oZdyKqLKwK2XWQ8FLjd", ACCOUNTS.kVaultsProgram().toBase58());

    // the invoked metas wrap their own program, not a neighbour's
    assertEquals(ACCOUNTS.kLendProgram(), ACCOUNTS.invokedKLendProgram().publicKey());
    assertEquals(ACCOUNTS.farmProgram(), ACCOUNTS.invokedFarmsProgram().publicKey());
    assertEquals(ACCOUNTS.kVaultsProgram(), ACCOUNTS.invokedKVaultsProgram().publicKey());
    assertEquals(ACCOUNTS.scopePricesProgram(), ACCOUNTS.invokedScopePricesProgram().publicKey());

    // the event authorities are derived from their own programs
    assertNotEquals(ACCOUNTS.kVaultsEventAuthority(), ACCOUNTS.scopeEventAuthority());

    assertEquals(ScopeFeedAccounts.SCOPE_MAINNET_HUBBLE_FEED, ACCOUNTS.scopeMainnetHubbleFeed());
    assertEquals(ScopeFeedAccounts.SCOPE_MAINNET_KLEND_FEED, ACCOUNTS.scopeMainnetKLendFeed());
  }

  /// The base58 factory decodes into the same slots as the PublicKey factory.
  @Test
  void base58FactoryMatchesTheKeyFactory() {
    final var fromKeys = KaminoAccounts.createAccounts(
        ACCOUNTS.kLendProgram(),
        ACCOUNTS.mainMarketLUT(),
        ACCOUNTS.scopePricesProgram(),
        ACCOUNTS.farmProgram(),
        ACCOUNTS.farmsGlobalConfig(),
        ACCOUNTS.kVaultsProgram());

    assertEquals(ACCOUNTS.kLendProgram(), fromKeys.kLendProgram());
    assertEquals(ACCOUNTS.mainMarketLUT(), fromKeys.mainMarketLUT());
    assertEquals(ACCOUNTS.scopePricesProgram(), fromKeys.scopePricesProgram());
    assertEquals(ACCOUNTS.farmProgram(), fromKeys.farmProgram());
    assertEquals(ACCOUNTS.farmsGlobalConfig(), fromKeys.farmsGlobalConfig());
    assertEquals(ACCOUNTS.kVaultsProgram(), fromKeys.kVaultsProgram());
    assertEquals(ACCOUNTS.kVaultsEventAuthority(), fromKeys.kVaultsEventAuthority());
  }

  @Test
  void scopeFeedLookupByOraclePrices() {
    final var hubble = ScopeFeedAccounts.SCOPE_MAINNET_HUBBLE_FEED;
    final var klend = ScopeFeedAccounts.SCOPE_MAINNET_KLEND_FEED;

    assertEquals(hubble, ACCOUNTS.scopeFeed(hubble.oraclePrices()));
    assertEquals(klend, ACCOUNTS.scopeFeed(klend.oraclePrices()));
    // keyed by oraclePrices, so an unrelated key resolves to nothing
    assertNull(ACCOUNTS.scopeFeed(MARKET));
  }

  // ---------------------------------------------------------------------------
  // kLend-bound PDAs
  // ---------------------------------------------------------------------------

  @Test
  void marketAndReservePDAsDependOnEveryInput() {
    final var other = key(0x21);
    final var kLend = ACCOUNTS.kLendProgram();

    assertDerivation("lendingMarketAuthPda",
        () -> KaminoAccounts.lendingMarketAuthPda(MARKET, kLend).publicKey(),
        () -> KaminoAccounts.lendingMarketAuthPda(other, kLend).publicKey(),
        () -> KaminoAccounts.lendingMarketAuthPda(MARKET, other).publicKey());

    assertDerivation("reserveLiqSupplyPda",
        () -> KaminoAccounts.reserveLiqSupplyPda(MARKET, MINT, kLend).publicKey(),
        () -> KaminoAccounts.reserveLiqSupplyPda(other, MINT, kLend).publicKey(),
        () -> KaminoAccounts.reserveLiqSupplyPda(MARKET, other, kLend).publicKey(),
        () -> KaminoAccounts.reserveLiqSupplyPda(MARKET, MINT, other).publicKey());

    assertDerivation("reserveFeeVaultPda",
        () -> KaminoAccounts.reserveFeeVaultPda(MARKET, MINT, kLend).publicKey(),
        () -> KaminoAccounts.reserveFeeVaultPda(other, MINT, kLend).publicKey(),
        () -> KaminoAccounts.reserveFeeVaultPda(MARKET, other, kLend).publicKey());

    assertDerivation("reserveCollateralMintPda",
        () -> KaminoAccounts.reserveCollateralMintPda(MARKET, MINT, kLend).publicKey(),
        () -> KaminoAccounts.reserveCollateralMintPda(other, MINT, kLend).publicKey(),
        () -> KaminoAccounts.reserveCollateralMintPda(MARKET, other, kLend).publicKey());

    assertDerivation("reserveCollateralSupplyPda",
        () -> KaminoAccounts.reserveCollateralSupplyPda(MARKET, MINT, kLend).publicKey(),
        () -> KaminoAccounts.reserveCollateralSupplyPda(other, MINT, kLend).publicKey(),
        () -> KaminoAccounts.reserveCollateralSupplyPda(MARKET, other, kLend).publicKey());

    // the five reserve-scoped PDAs share their (market, mint) inputs, so only a
    // distinct seed prefix separates them
    assertEquals(5, java.util.Set.of(
        KaminoAccounts.lendingMarketAuthPda(MARKET, kLend).publicKey(),
        KaminoAccounts.reserveLiqSupplyPda(MARKET, MINT, kLend).publicKey(),
        KaminoAccounts.reserveFeeVaultPda(MARKET, MINT, kLend).publicKey(),
        KaminoAccounts.reserveCollateralMintPda(MARKET, MINT, kLend).publicKey(),
        KaminoAccounts.reserveCollateralSupplyPda(MARKET, MINT, kLend).publicKey()).size());
  }

  @Test
  void userAndReferrerPDAs() {
    final var other = key(0x21);
    final var kLend = ACCOUNTS.kLendProgram();

    assertDerivation("userMetadataPda",
        () -> KaminoAccounts.userMetadataPda(USER, kLend).publicKey(),
        () -> KaminoAccounts.userMetadataPda(other, kLend).publicKey(),
        () -> KaminoAccounts.userMetadataPda(USER, other).publicKey());

    assertDerivation("referrerStatePda",
        () -> KaminoAccounts.referrerStatePda(REFERRER, kLend).publicKey(),
        () -> KaminoAccounts.referrerStatePda(other, kLend).publicKey());

    assertDerivation("referrerTokenStatePda",
        () -> KaminoAccounts.referrerTokenStatePda(REFERRER, RESERVE, kLend).publicKey(),
        () -> KaminoAccounts.referrerTokenStatePda(other, RESERVE, kLend).publicKey(),
        () -> KaminoAccounts.referrerTokenStatePda(REFERRER, other, kLend).publicKey());

    // no referrer -> no token state account at all, rather than one derived
    // from the all-zero key
    assertNull(KaminoAccounts.referrerTokenStatePda(PublicKey.NONE, RESERVE, kLend));
    assertNull(ACCOUNTS.referrerTokenStatePda(PublicKey.NONE, RESERVE));
    assertNotNull(KaminoAccounts.referrerTokenStatePda(REFERRER, RESERVE, kLend));

    assertDerivation("shortUrlPda",
        () -> KaminoAccounts.shortUrlPda("kamino", kLend).publicKey(),
        () -> KaminoAccounts.shortUrlPda("kamin0", kLend).publicKey(),
        () -> KaminoAccounts.shortUrlPda("kamino", other).publicKey());
  }

  // ---------------------------------------------------------------------------
  // kVaults- and scope-bound PDAs
  // ---------------------------------------------------------------------------

  @Test
  void vaultAndScopePDAs() {
    final var other = key(0x21);

    assertDerivation("cTokenVault",
        () -> KaminoAccounts.cTokenVault(VAULT, RESERVE, ACCOUNTS.kVaultsProgram()).publicKey(),
        () -> KaminoAccounts.cTokenVault(other, RESERVE, ACCOUNTS.kVaultsProgram()).publicKey(),
        () -> KaminoAccounts.cTokenVault(VAULT, other, ACCOUNTS.kVaultsProgram()).publicKey(),
        () -> KaminoAccounts.cTokenVault(VAULT, RESERVE, other).publicKey());

    assertDerivation("kVaultGlobalConfig",
        () -> KaminoAccounts.kVaultGlobalConfig(ACCOUNTS.kVaultsProgram()).publicKey(),
        () -> KaminoAccounts.kVaultGlobalConfig(other).publicKey());

    assertDerivation("scopeFeedConfiguration",
        () -> KaminoAccounts.scopeFeedConfiguration("hubble", ACCOUNTS.scopePricesProgram()).publicKey(),
        () -> KaminoAccounts.scopeFeedConfiguration("klend", ACCOUNTS.scopePricesProgram()).publicKey(),
        () -> KaminoAccounts.scopeFeedConfiguration("hubble", other).publicKey());

    assertDerivation("mintsToScopeChain",
        () -> KaminoAccounts.mintsToScopeChain(MARKET, MINT, 7L, ACCOUNTS.scopePricesProgram()).publicKey(),
        () -> KaminoAccounts.mintsToScopeChain(other, MINT, 7L, ACCOUNTS.scopePricesProgram()).publicKey(),
        () -> KaminoAccounts.mintsToScopeChain(MARKET, other, 7L, ACCOUNTS.scopePricesProgram()).publicKey(),
        () -> KaminoAccounts.mintsToScopeChain(MARKET, MINT, 8L, ACCOUNTS.scopePricesProgram()).publicKey(),
        () -> KaminoAccounts.mintsToScopeChain(MARKET, MINT, 7L, other).publicKey());

    // the seed id is a full u64: values differing only above 32 bits still separate
    assertNotEquals(
        KaminoAccounts.mintsToScopeChain(MARKET, MINT, 1L, ACCOUNTS.scopePricesProgram()).publicKey(),
        KaminoAccounts.mintsToScopeChain(MARKET, MINT, 1L << 32, ACCOUNTS.scopePricesProgram()).publicKey());
  }

  /// Every `default` overload must bind the program its on-chain account
  /// actually lives under — and the three programs are distinct, so binding
  /// the wrong one is observable.
  @Test
  void defaultOverloadsBindTheCorrectProgram() {
    final var kLend = ACCOUNTS.kLendProgram();
    final var kVaults = ACCOUNTS.kVaultsProgram();
    final var scope = ACCOUNTS.scopePricesProgram();
    assertEquals(3, java.util.Set.of(kLend, kVaults, scope).size(), "the three programs must differ");

    // kLend-bound
    assertEquals(KaminoAccounts.lendingMarketAuthPda(MARKET, kLend).publicKey(), ACCOUNTS.lendingMarketAuthPda(MARKET).publicKey());
    assertEquals(KaminoAccounts.reserveLiqSupplyPda(MARKET, MINT, kLend).publicKey(), ACCOUNTS.reserveLiqSupplyPda(MARKET, MINT).publicKey());
    assertEquals(KaminoAccounts.reserveFeeVaultPda(MARKET, MINT, kLend).publicKey(), ACCOUNTS.reserveFeeVaultPda(MARKET, MINT).publicKey());
    assertEquals(KaminoAccounts.reserveCollateralMintPda(MARKET, MINT, kLend).publicKey(), ACCOUNTS.reserveCollateralMintPda(MARKET, MINT).publicKey());
    assertEquals(KaminoAccounts.reserveCollateralSupplyPda(MARKET, MINT, kLend).publicKey(), ACCOUNTS.reserveCollateralSupplyPda(MARKET, MINT).publicKey());
    assertEquals(KaminoAccounts.userMetadataPda(USER, kLend).publicKey(), ACCOUNTS.userMetadataPda(USER).publicKey());
    assertEquals(KaminoAccounts.referrerStatePda(REFERRER, kLend).publicKey(), ACCOUNTS.referrerStatePda(REFERRER).publicKey());
    assertEquals(KaminoAccounts.referrerTokenStatePda(REFERRER, RESERVE, kLend).publicKey(), ACCOUNTS.referrerTokenStatePda(REFERRER, RESERVE).publicKey());
    assertEquals(KaminoAccounts.shortUrlPda("kamino", kLend).publicKey(), ACCOUNTS.shortUrlPda("kamino").publicKey());

    // kVaults-bound — deliberately a different program from the block above
    assertEquals(KaminoAccounts.cTokenVault(VAULT, RESERVE, kVaults).publicKey(), ACCOUNTS.cTokenVault(VAULT, RESERVE).publicKey());
    assertNotEquals(KaminoAccounts.cTokenVault(VAULT, RESERVE, kLend).publicKey(), ACCOUNTS.cTokenVault(VAULT, RESERVE).publicKey());
    assertEquals(KaminoAccounts.kVaultGlobalConfig(kVaults).publicKey(), ACCOUNTS.kVaultGlobalConfig().publicKey());
    assertNotEquals(KaminoAccounts.kVaultGlobalConfig(kLend).publicKey(), ACCOUNTS.kVaultGlobalConfig().publicKey());

    // scope-bound
    assertEquals(KaminoAccounts.scopeFeedConfiguration("hubble", scope).publicKey(), ACCOUNTS.scopeFeedConfiguration("hubble").publicKey());
    assertNotEquals(KaminoAccounts.scopeFeedConfiguration("hubble", kLend).publicKey(), ACCOUNTS.scopeFeedConfiguration("hubble").publicKey());
    assertEquals(KaminoAccounts.mintsToScopeChain(MARKET, MINT, 7L, scope).publicKey(), ACCOUNTS.mintsToScopeChain(MARKET, MINT, 7L).publicKey());
  }
}

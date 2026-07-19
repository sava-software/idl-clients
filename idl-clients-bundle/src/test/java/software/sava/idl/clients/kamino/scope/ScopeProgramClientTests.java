package software.sava.idl.clients.kamino.scope;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.kamino.scope.gen.types.OracleMappings;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;
import software.sava.idl.clients.kamino.scope.gen.types.TwapEnabledBitmask;
import software.sava.idl.clients.spl.SPLAccountClient;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class ScopeProgramClientTests {

  private static final SolanaAccounts ACCOUNTS = SolanaAccounts.MAIN_NET;

  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey FEE_PAYER = key(0x12);
  private static final PublicKey ORACLE_PRICES = key(0x21);
  private static final PublicKey ORACLE_MAPPINGS = key(0x22);
  private static final PublicKey ORACLE_TWAPS = key(0x23);

  private static final SPLAccountClient ACCOUNT_CLIENT =
      SPLAccountClient.createClient(ACCOUNTS, OWNER, AccountMeta.createFeePayer(FEE_PAYER));
  private static final ScopeProgramClient CLIENT = ScopeProgramClient.createClient(ACCOUNT_CLIENT);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  @Test
  void clientBindsItsIdentity() {
    assertEquals(ACCOUNTS, CLIENT.solanaAccounts());
    assertEquals(KaminoAccounts.MAIN_NET, CLIENT.kaminoAccounts(), "the single-arg factory defaults to main net");
    // the authority is the account client's owner; the fee payer is distinct
    assertEquals(OWNER, CLIENT.authority());
    assertEquals(FEE_PAYER, CLIENT.feePayer());
    assertNotEquals(CLIENT.authority(), CLIENT.feePayer());
  }

  @Test
  void refreshPriceListWiresTheOracleAccounts() {
    final int[] tokens = {3, 7};
    final var ix = CLIENT.refreshPriceList(ORACLE_PRICES, ORACLE_MAPPINGS, ORACLE_TWAPS, tokens);

    assertEquals(KaminoAccounts.MAIN_NET.invokedScopePricesProgram(), ix.programId());
    final var keys = ix.accounts().stream().map(AccountMeta::publicKey).toList();
    assertTrue(keys.contains(ORACLE_PRICES), "oracle prices");
    assertTrue(keys.contains(ORACLE_MAPPINGS), "oracle mappings");
    assertTrue(keys.contains(ORACLE_TWAPS), "oracle twaps");
    // the three oracle accounts occupy distinct slots
    assertEquals(3, keys.stream().filter(k -> k.equals(ORACLE_PRICES) || k.equals(ORACLE_MAPPINGS) || k.equals(ORACLE_TWAPS)).count());
  }

  /// The extra accounts for a refresh are the price info accounts of exactly
  /// the requested tokens, in request order, as read-only metas.
  @Test
  void refreshPriceListExtraAccountsSelectsTheRequestedTokens() {
    final var mappings = mappings();
    final var extras = ScopeProgramClient.refreshPriceListExtraAccounts(mappings, new int[]{2, 0});

    assertEquals(
        List.of(
            AccountMeta.createRead(mappings.priceInfoAccounts()[2]),
            AccountMeta.createRead(mappings.priceInfoAccounts()[0])
        ),
        extras);

    assertEquals(List.of(), ScopeProgramClient.refreshPriceListExtraAccounts(mappings, new int[0]));
  }

  /// Token types whose refresh needs asset mints cannot be refreshed through
  /// this path — a plain read meta would produce a failing on-chain refresh.
  @Test
  void refreshPriceListExtraAccountsRejectsMintDependentTypes() {
    for (final var type : new OracleType[]{
        OracleType.KToken, OracleType.KTokenToTokenA, OracleType.KTokenToTokenB,
        OracleType.JupiterLpFetch,
        OracleType.MeteoraDlmmAtoB, OracleType.MeteoraDlmmBtoA,
        OracleType.OrcaWhirlpoolAtoB, OracleType.OrcaWhirlpoolBtoA,
        OracleType.Securitize
    }) {
      final var mappings = mappings();
      mappings.priceTypes()[1] = (byte) type.ordinal();
      assertThrows(IllegalStateException.class,
          () -> ScopeProgramClient.refreshPriceListExtraAccounts(mappings, new int[]{1}),
          type.name());
    }
  }

  private static software.sava.idl.clients.kamino.scope.gen.types.Configuration configuration() {
    return new software.sava.idl.clients.kamino.scope.gen.types.Configuration(
        key(0x41), // _address
        null,
        key(0x42), // admin
        ORACLE_MAPPINGS,
        ORACLE_PRICES,
        key(0x43), // tokensMetadata
        ORACLE_TWAPS,
        key(0x44), // adminCached
        key(0x45), // emergencyCouncil
        new long[software.sava.idl.clients.kamino.scope.gen.types.Configuration.PADDING_LEN]
    );
  }

  private static List<PublicKey> keys(final software.sava.core.tx.Instruction ix) {
    return ix.accounts().stream().map(AccountMeta::publicKey).toList();
  }

  /// Regression: this overload used to pass `oracleMappings()` into the
  /// oraclePrices slot and vice versa, transposing the two accounts on-chain.
  /// The two fields are adjacent same-typed keys, so only a positional check
  /// can catch the swap.
  @Test
  void initializeFromConfigurationWiresEachAccountToItsSlot() {
    final var config = configuration();
    final var ix = CLIENT.initialize(config, "feed");

    assertEquals(
        CLIENT.initialize(
            config.admin(),
            config._address(),
            config.tokensMetadata(),
            config.oracleTwaps(),
            config.oraclePrices(),
            config.oracleMappings(),
            "feed"),
        ix);

    // and the explicit form itself puts each key in a distinct slot
    final var accounts = keys(ix);
    assertTrue(accounts.contains(config.oraclePrices()));
    assertTrue(accounts.contains(config.oracleMappings()));
    assertTrue(accounts.contains(config.tokensMetadata()));
    assertTrue(accounts.contains(config.oracleTwaps()));
    assertTrue(accounts.contains(config._address()));
    assertTrue(accounts.contains(config.admin()));
  }

  @Test
  void refreshPriceListFromConfiguration() {
    final var config = configuration();
    final int[] tokens = {1, 2};
    assertEquals(
        CLIENT.refreshPriceList(ORACLE_PRICES, ORACLE_MAPPINGS, ORACLE_TWAPS, tokens),
        CLIENT.refreshPriceList(config, tokens));

    // the mappings-aware overload appends the extra accounts
    final var withExtras = CLIENT.refreshPriceList(config, mappings(), tokens);
    final var plain = CLIENT.refreshPriceList(config, tokens);
    assertEquals(plain.accounts().size() + tokens.length, withExtras.accounts().size());
  }

  @Test
  void adminInstructionsWireTheirAccounts() {
    final var admin = key(0x51);
    final var config = key(0x52);

    final var resetTwap = CLIENT.resetTwap(admin, config, ORACLE_TWAPS, 7L, "feed");
    assertEquals(KaminoAccounts.MAIN_NET.invokedScopePricesProgram(), resetTwap.programId());
    assertTrue(keys(resetTwap).containsAll(List.of(admin, config, ORACLE_TWAPS)));

    final var setAdmin = CLIENT.setAdminCached(admin, config, key(0x53), "feed");
    assertTrue(keys(setAdmin).containsAll(List.of(admin, config)));

    final var approve = CLIENT.approveAdminCached(key(0x53), config, "feed");
    assertTrue(keys(approve).containsAll(List.of(key(0x53), config)));

    final var createMap = CLIENT.createMintMap(admin, config, key(0x54), key(0x55), 3L, 254, new int[][]{{1, 2, 0xFFFF, 0xFFFF}});
    assertTrue(keys(createMap).containsAll(List.of(admin, config, key(0x54))));

    final var closeMap = CLIENT.closeMintMap(admin, config, key(0x54));
    assertTrue(keys(closeMap).containsAll(List.of(admin, config, key(0x54))));
  }

  @Test
  void refreshVariantsWireTheirAccounts() {
    final var user = key(0x61);

    final var chainlink = CLIENT.refreshChainlinkPrice(
        user, ORACLE_PRICES, ORACLE_MAPPINGS, ORACLE_TWAPS,
        key(0x62), key(0x63), key(0x64), key(0x65),
        3, new byte[]{1, 2, 3});
    assertEquals(KaminoAccounts.MAIN_NET.invokedScopePricesProgram(), chainlink.programId());
    assertTrue(keys(chainlink).containsAll(List.of(user, ORACLE_PRICES, ORACLE_MAPPINGS, ORACLE_TWAPS)));

    final var lazer = CLIENT.refreshPythLazerPrice(
        user, ORACLE_PRICES, ORACLE_MAPPINGS, ORACLE_TWAPS,
        key(0x66), key(0x67), key(0x68),
        new int[]{1}, new byte[]{4, 5}, 1);
    assertTrue(keys(lazer).containsAll(List.of(user, ORACLE_PRICES, ORACLE_MAPPINGS, ORACLE_TWAPS)));

    final var initialize = CLIENT.initialize(
        key(0x69), key(0x6A), key(0x6B), ORACLE_TWAPS, ORACLE_PRICES, ORACLE_MAPPINGS, "feed");
    assertTrue(keys(initialize).containsAll(List.of(key(0x69), key(0x6A), key(0x6B), ORACLE_TWAPS, ORACLE_PRICES, ORACLE_MAPPINGS)));
  }

  /// Factories invoked inside the test rather than only via the static field,
  /// so a factory returning null is observed by the mutation run.
  @Test
  void factoriesProduceWiredClients() {
    final var explicit = ScopeProgramClient.createClient(ACCOUNT_CLIENT, KaminoAccounts.MAIN_NET);
    assertNotNull(explicit);
    assertEquals(OWNER, explicit.authority());

    final var defaulted = ScopeProgramClient.createClient(ACCOUNT_CLIENT);
    assertNotNull(defaulted);
    assertEquals(KaminoAccounts.MAIN_NET, defaulted.kaminoAccounts());

    final var feed = ScopeFeedAccounts.createAccounts(
        "3NJYftD5sjVfxSnUdZ1wVML8f3aC6mp1CXCL6L7TnU8C",
        "Chpu5ZgfWX5ZzVpUx9Xvv4WPM75Xd7zPJNDPsFnCpLpk",
        "AdTiP7QyjUyv6crF4H8z7fxJKU7Z5eCAGvJN1Y55cXxb");
    assertNotNull(feed);
    assertEquals(feed, ScopeFeedAccounts.SCOPE_MAINNET_HUBBLE_FEED);
    assertEquals("3NJYftD5sjVfxSnUdZ1wVML8f3aC6mp1CXCL6L7TnU8C", feed.oraclePrices().toBase58());
    assertEquals("Chpu5ZgfWX5ZzVpUx9Xvv4WPM75Xd7zPJNDPsFnCpLpk", feed.oracleMappings().toBase58());
    assertEquals("AdTiP7QyjUyv6crF4H8z7fxJKU7Z5eCAGvJN1Y55cXxb", feed.configuration().toBase58());
  }

  /// Every Configuration-taking convenience overload maps the same fields as
  /// its explicit form.
  @Test
  void configurationOverloadsMatchTheExplicitForms() {
    final var config = configuration();

    assertEquals(
        CLIENT.resetTwap(config.admin(), config._address(), config.oracleTwaps(), 7L, "feed"),
        CLIENT.resetTwap(config, 7L, "feed"));
    assertEquals(
        CLIENT.setAdminCached(config.admin(), config._address(), key(0x53), "feed"),
        CLIENT.setAdminCached(config, key(0x53), "feed"));
    assertEquals(
        CLIENT.approveAdminCached(config.adminCached(), config._address(), "feed"),
        CLIENT.approveAdminCached(config, "feed"));
    assertEquals(
        CLIENT.refreshChainlinkPrice(
            key(0x61), config.oraclePrices(), config.oracleMappings(), config.oracleTwaps(),
            key(0x62), key(0x63), key(0x64), key(0x65), 3, new byte[]{1}),
        CLIENT.refreshChainlinkPrice(
            key(0x61), config, key(0x62), key(0x63), key(0x64), key(0x65), 3, new byte[]{1}));
    assertEquals(
        CLIENT.refreshPythLazerPrice(
            key(0x61), config.oraclePrices(), config.oracleMappings(), config.oracleTwaps(),
            key(0x66), key(0x67), key(0x68), new int[]{1}, new byte[]{4}, 1),
        CLIENT.refreshPythLazerPrice(
            key(0x61), config, key(0x66), key(0x67), key(0x68), new int[]{1}, new byte[]{4}, 1));

    final int[][] chains = {{1, 2, 0xFFFF, 0xFFFF}};
    assertEquals(
        CLIENT.createMintMap(config.admin(), config._address(), config.oracleMappings(), key(0x55), 3L, 254, chains),
        CLIENT.createMintMap(config, key(0x55), 3L, 254, chains));
    assertEquals(
        CLIENT.closeMintMap(config.admin(), config._address(), config.oracleMappings()),
        CLIENT.closeMintMap(config));
  }

  private static OracleMappings mappings() {
    final int slots = OracleMappings.PRICE_INFO_ACCOUNTS_LEN;
    final var priceInfoAccounts = new PublicKey[slots];
    final var priceTypes = new byte[slots];
    final var tolerance = new int[slots];
    final var twapBitmasks = new TwapEnabledBitmask[slots];
    final var refPrice = new int[slots];
    final var generic = new byte[slots][20];
    for (int i = 0; i < slots; ++i) {
      priceInfoAccounts[i] = key(0x30 + (i & 0x0F));
      priceTypes[i] = (byte) OracleType.PythPull.ordinal();
      tolerance[i] = 0xFFFF;
      twapBitmasks[i] = new TwapEnabledBitmask(0);
      refPrice[i] = 0xFFFF;
    }
    return new OracleMappings(key(0x77), null, priceInfoAccounts, priceTypes, tolerance, twapBitmasks, refPrice, generic);
  }
}

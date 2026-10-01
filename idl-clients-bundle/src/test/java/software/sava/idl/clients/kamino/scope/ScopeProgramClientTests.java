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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

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

  /// Token types whose refresh consumes `extra_accounts` cannot be refreshed
  /// through this path — a plain read meta would produce a failing on-chain
  /// refresh.
  ///
  /// The list is every arm of the program's `refresh_prices` dispatch that pulls
  /// from `extra_accounts`. `SplBalance` is one of them: `spl_balance::get_price`
  /// takes a mint account, so a token of that type anywhere but last in the batch
  /// makes the handler read the *next* token's base account as its mint and fail
  /// the whole transaction. `KlendCTokenExchangeRate` pulls the klend program and
  /// lending market the same way, `Canary` the canary program it CPIs into, and
  /// `ExponentTranching` five accounts and then the market's `get_sy_state` list for
  /// its `update_market` CPI.
  @Test
  void refreshPriceListExtraAccountsRejectsTypesThatConsumeExtraAccounts() {
    for (final var type : EXTRA_ACCOUNT_CONSUMERS) {
      final var mappings = mappings();
      mappings.priceTypes()[1] = (byte) type.ordinal();
      assertThrows(IllegalStateException.class,
          () -> ScopeProgramClient.refreshPriceListExtraAccounts(mappings, new int[]{1}),
          type.name());
    }

    // Canary's one missing account is a constant, so the rejection names it
    final var canary = mappings();
    canary.priceTypes()[1] = (byte) OracleType.Canary.ordinal();
    final var ex = assertThrows(IllegalStateException.class,
        () -> ScopeProgramClient.refreshPriceListExtraAccounts(canary, new int[]{1}));
    assertTrue(ex.getMessage().contains("CanarFxHDSnbrPmrE79Qq6hL2p7ZMyyV4ZLTKQ6g7tpK"), ex.getMessage());

    // ExponentTranching's are mostly per market, so the rejection names the two
    // constants and the writability the CPI needs from the outer instruction, which
    // reaches past the fixed accounts: on 2026-10-01 all seven tranching markets on
    // mainnet flagged get_sy_state accounts writable
    final var tranching = mappings();
    tranching.priceTypes()[1] = (byte) OracleType.ExponentTranching.ordinal();
    final var tranchingEx = assertThrows(IllegalStateException.class,
        () -> ScopeProgramClient.refreshPriceListExtraAccounts(tranching, new int[]{1}));
    final var message = tranchingEx.getMessage();
    assertTrue(message.contains("XPTrnchoawiUc9iYJrpfchS8vgr8Y5X2QGBdHPXukty"), message);
    assertTrue(message.contains("3mBi7DRWMdTdDghA1cVLrwDKAgDo7UTDWoeik4GkXCsf"), message);
    assertTrue(message.contains("with the market, its return model storage and each get_sy_state account"
        + " the market flags writable passed writable"), message);

    // the kToken types take no mint at all, and Securitize takes one mint among three
    for (final var kToken : new OracleType[]{OracleType.KToken, OracleType.KTokenToTokenA, OracleType.KTokenToTokenB}) {
      final var mappings = mappings();
      mappings.priceTypes()[1] = (byte) kToken.ordinal();
      final var kTokenEx = assertThrows(IllegalStateException.class,
          () -> ScopeProgramClient.refreshPriceListExtraAccounts(mappings, new int[]{1}));
      assertTrue(kTokenEx.getMessage().contains(
          "the strategy's global config, collateral infos, pool, position and scope prices"), kTokenEx.getMessage());
    }
    final var securitize = mappings();
    securitize.priceTypes()[1] = (byte) OracleType.Securitize.ordinal();
    final var securitizeEx = assertThrows(IllegalStateException.class,
        () -> ScopeProgramClient.refreshPriceListExtraAccounts(securitize, new int[]{1}));
    assertTrue(securitizeEx.getMessage().contains(
        "the vault's share mint and asset vault and its RedStone price account"), securitizeEx.getMessage());
  }

  /// The complement of the list above: every other oracle type consumes its base
  /// account and nothing more, so it gets exactly one read meta for that account. The
  /// two together classify every type the generated enum declares, so a type moved
  /// between the arms of the switch fails here.
  @Test
  void refreshPriceListExtraAccountsGivesEveryOtherTypeItsOneAccount() {
    final var consumers = EnumSet.copyOf(Arrays.asList(EXTRA_ACCOUNT_CONSUMERS));
    final var others = EnumSet.complementOf(consumers);
    assertEquals(OracleType.values().length - EXTRA_ACCOUNT_CONSUMERS.length, others.size(),
        "the consumer list names a type twice");
    for (final var type : others) {
      final var mappings = mappings();
      mappings.priceTypes()[5] = (byte) type.ordinal();
      assertEquals(
          List.of(AccountMeta.createRead(mappings.priceInfoAccounts()[5])),
          ScopeProgramClient.refreshPriceListExtraAccounts(mappings, new int[]{5}),
          type.name());
    }
  }

  /// Every arm of the program's `refresh_prices` dispatch (Scope 0.43.0,
  /// `oracles/mod.rs::get_non_zero_price`) that takes from `extra_accounts`.
  private static final OracleType[] EXTRA_ACCOUNT_CONSUMERS = {
      OracleType.Canary,
      OracleType.ExponentTranching,
      OracleType.KToken, OracleType.KTokenToTokenA, OracleType.KTokenToTokenB,
      OracleType.JupiterLpFetch,
      OracleType.KlendCTokenExchangeRate,
      OracleType.MeteoraDlmmAtoB, OracleType.MeteoraDlmmBtoA,
      OracleType.OrcaWhirlpoolAtoB, OracleType.OrcaWhirlpoolBtoA,
      OracleType.Securitize,
      OracleType.SplBalance
  };

  /// Bit 7 of a `price_types` byte is the program's frozen flag, not part of the
  /// oracle type. Freezing an entry is a live admin/emergency-council action and
  /// does not change how many accounts the refresh consumes, so a frozen token
  /// must build exactly the meta its unfrozen self would.
  ///
  /// Read as a signed byte the flag makes the type negative, so an unmasked read
  /// indexes the enum out of bounds rather than mis-typing the entry.
  @Test
  void refreshPriceListExtraAccountsIgnoresTheFrozenFlag() {
    final var mappings = mappings();
    final byte plain = mappings.priceTypes()[1];
    mappings.priceTypes()[1] = (byte) (plain | 0x80);

    assertEquals(
        List.of(AccountMeta.createRead(mappings.priceInfoAccounts()[1])),
        ScopeProgramClient.refreshPriceListExtraAccounts(mappings, new int[]{1}));

    // and the frozen flag does not smuggle a mint-dependent type past the guard
    mappings.priceTypes()[1] = (byte) (OracleType.KToken.ordinal() | 0x80);
    assertThrows(IllegalStateException.class,
        () -> ScopeProgramClient.refreshPriceListExtraAccounts(mappings, new int[]{1}));
  }

  /// A deployed program can carry an oracle type newer than the IDL this client
  /// was generated from. That must be a diagnosable failure, not an
  /// ArrayIndexOutOfBoundsException from indexing the enum with a raw byte.
  @Test
  void refreshPriceListExtraAccountsRejectsAnUnknownOracleType() {
    final var mappings = mappings();
    mappings.priceTypes()[1] = (byte) 0x7E; // within the mask, beyond the enum
    final var ex = assertThrows(IllegalStateException.class,
        () -> ScopeProgramClient.refreshPriceListExtraAccounts(mappings, new int[]{1}));
    assertTrue(ex.getMessage().contains("0x7e"), ex.getMessage());

    // a frozen unknown type reports the whole byte, so the flag is visible too
    mappings.priceTypes()[1] = (byte) 0xFE;
    final var frozen = assertThrows(IllegalStateException.class,
        () -> ScopeProgramClient.refreshPriceListExtraAccounts(mappings, new int[]{1}));
    assertTrue(frozen.getMessage().contains("0xfe"), frozen.getMessage());
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
        key(0x46), // resumeAuthority
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

    // and the ExponentTranching-aware one appends what its static form builds
    final var mappings = mappings();
    mappings.priceTypes()[1] = (byte) OracleType.ExponentTranching.ordinal();
    mappings.priceInfoAccounts()[1] = TRANCHING_MARKET;
    final var markets = Map.of(TRANCHING_MARKET, tranchingAccounts());
    assertEquals(
        plain.extraAccounts(ScopeProgramClient.refreshPriceListExtraAccounts(mappings, tokens, markets)),
        CLIENT.refreshPriceList(config, mappings, tokens, markets));
  }

  private static final PublicKey TRANCHING_MARKET = key(0x6A);

  /// Stands in for what `ExponentTranchingMarket.refreshAccounts` builds, which its own
  /// tests pin against live markets: the market first, writable, then the rest.
  private static List<AccountMeta> tranchingAccounts() {
    return List.of(
        AccountMeta.createWrite(TRANCHING_MARKET),
        AccountMeta.createWrite(key(0x6B)),
        AccountMeta.createRead(key(0x6C))
    );
  }

  /// An ExponentTranching token takes the whole list given for its market in place of its
  /// one read meta. Scope's authors want such a token refreshed alone, but the program
  /// accepts it anywhere in a batch and as often as it is named, so the client composes
  /// that too, and the tokens around it keep their own accounts.
  @Test
  void refreshPriceListExtraAccountsTakesAnExponentTranchingTokensAccountsFromItsMarket() {
    final var mappings = mappings();
    mappings.priceTypes()[1] = (byte) OracleType.ExponentTranching.ordinal();
    mappings.priceInfoAccounts()[1] = TRANCHING_MARKET;
    final var tranching = tranchingAccounts();

    final var expected = new ArrayList<AccountMeta>();
    expected.add(AccountMeta.createRead(mappings.priceInfoAccounts()[2]));
    expected.addAll(tranching);
    expected.add(AccountMeta.createRead(mappings.priceInfoAccounts()[0]));
    expected.addAll(tranching);
    assertEquals(expected, ScopeProgramClient.refreshPriceListExtraAccounts(
        mappings, new int[]{2, 1, 0, 1}, Map.of(TRANCHING_MARKET, tranching)));

    // a market the map does not hold is the same rejection the two-argument form gives,
    // pointing at the builder
    final var missing = assertThrows(IllegalStateException.class, () -> ScopeProgramClient.refreshPriceListExtraAccounts(
        mappings, new int[]{1}, Map.of(key(0x6D), tranching)));
    assertTrue(missing.getMessage().contains("after market " + TRANCHING_MARKET), missing.getMessage());
    assertTrue(missing.getMessage().contains("build them with ExponentTranchingMarket.refreshAccounts"), missing.getMessage());
  }

  /// The only guard against a map keyed wrongly: the list must open with the token's own
  /// market, writable, as the builder makes it.
  @Test
  void refreshPriceListExtraAccountsRefusesAListBuiltForAnotherMarket() {
    final var mappings = mappings();
    mappings.priceTypes()[1] = (byte) OracleType.ExponentTranching.ordinal();
    mappings.priceInfoAccounts()[1] = TRANCHING_MARKET;
    final var rest = tranchingAccounts().subList(1, 3);
    for (final var accounts : List.of(
        List.<AccountMeta>of(),
        List.of(AccountMeta.createWrite(key(0x6D)), rest.get(0), rest.get(1)),
        List.of(AccountMeta.createRead(TRANCHING_MARKET), rest.get(0), rest.get(1))
    )) {
      assertThrows(IllegalArgumentException.class, () -> ScopeProgramClient.refreshPriceListExtraAccounts(
          mappings, new int[]{1}, Map.of(TRANCHING_MARKET, accounts)), accounts.toString());
    }
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

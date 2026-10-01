package software.sava.idl.clients.kamino.scope;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.kamino.scope.entries.ScopeReader;
import software.sava.idl.clients.kamino.scope.gen.types.Configuration;
import software.sava.idl.clients.kamino.scope.gen.types.OracleMappings;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;
import software.sava.idl.clients.spl.SPLAccountClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ScopeProgramClient {

  static ScopeProgramClient createClient(final SPLAccountClient splAccountClient, final KaminoAccounts kaminoAccounts) {
    return new ScopeProgramClientImpl(splAccountClient, kaminoAccounts);
  }

  static ScopeProgramClient createClient(final SPLAccountClient splAccountClient) {
    return createClient(splAccountClient, KaminoAccounts.MAIN_NET);
  }

  SolanaAccounts solanaAccounts();

  KaminoAccounts kaminoAccounts();

  PublicKey authority();

  PublicKey feePayer();

  Instruction initialize(final PublicKey adminKey,
                         final PublicKey configurationKey,
                         final PublicKey tokenMetadatasKey,
                         final PublicKey oracleTwapsKey,
                         final PublicKey oraclePricesKey,
                         final PublicKey oracleMappingsKey,
                         final String feedName);

  default Instruction initialize(final Configuration configuration,
                                 final String feedName) {
    return initialize(
        configuration.admin(),
        configuration._address(),
        configuration.tokensMetadata(),
        configuration.oracleTwaps(),
        configuration.oraclePrices(),
        configuration.oracleMappings(),
        feedName
    );
  }

  Instruction refreshPriceList(final PublicKey oraclePricesKey,
                               final PublicKey oracleMappingsKey,
                               final PublicKey oracleTwapsKey,
                               final int[] tokens);

  default Instruction refreshPriceList(final Configuration configuration, final int[] tokens) {
    return refreshPriceList(
        configuration.oraclePrices(),
        configuration.oracleMappings(),
        configuration.oracleTwaps(),
        tokens
    );
  }

  /// One read-only meta per requested token, in request order.
  ///
  /// The rejected types are every arm of the program's `refresh_prices` dispatch that
  /// pulls an account out of `extra_accounts`: a plain read meta for one of those makes
  /// the handler take the following token's base account as that type's first extra
  /// account — an asset mint for most, the strategy's global config for the kToken
  /// types, the vault's share mint for `Securitize`, the klend program for
  /// `KlendCTokenExchangeRate`, the canary program for `Canary`, or the market's return
  /// model storage for `ExponentTranching` — which fails the whole transaction rather
  /// than just that entry. An `ExponentTranching` token can be refreshed through
  /// [#refreshPriceListExtraAccounts(OracleMappings, int\[\], Map)] instead.
  static List<AccountMeta> refreshPriceListExtraAccounts(final OracleMappings oracleMappings, final int[] tokens) {
    return refreshPriceListExtraAccounts(oracleMappings, tokens, Map.of());
  }

  /// The accounts `refresh_price_list` consumes for the requested tokens, in request
  /// order: one read-only meta per token, except that an `ExponentTranching` token takes
  /// the list `exponentTranchingAccounts` holds for its market, which
  /// [ExponentTranchingMarket#refreshAccounts] builds from the market and its lookup
  /// table and which starts with the market itself, writable.
  ///
  /// Scope's authors require an entry whose refresh CPIs into another program, as an
  /// `ExponentTranching` entry's does, to be refreshed in its own single-entry call: a
  /// failed CPI aborts the whole transaction instead of skipping the entry. So pass such a
  /// token alone. A batch that names one anyway takes its market's list once per entry,
  /// as the program consumes it.
  ///
  /// The other types that consume extra accounts are rejected, as in
  /// [#refreshPriceListExtraAccounts(OracleMappings, int\[\])]. The switch has no
  /// `default`, so an oracle type a regeneration adds does not compile until it is
  /// placed in an arm: how many accounts a refresh consumes is per type and invisible to
  /// the IDL, and a type falling through to the plain read meta is exactly that failure.
  ///
  /// @param exponentTranchingAccounts keyed by market; may omit any market no requested
  ///                                  token prices
  /// @throws IllegalStateException    for a type whose extra accounts this cannot supply,
  ///                                  including an `ExponentTranching` token whose market
  ///                                  has no entry
  /// @throws IllegalArgumentException when the list for a market does not start with that
  ///                                  market, writable
  static List<AccountMeta> refreshPriceListExtraAccounts(final OracleMappings oracleMappings,
                                                        final int[] tokens,
                                                        final Map<PublicKey, List<AccountMeta>> exponentTranchingAccounts) {
    final var priceInfoAccounts = oracleMappings.priceInfoAccounts();
    final var priceTypes = oracleMappings.priceTypes();
    final var oracleTypeEnums = OracleType.values();
    final var accountMetas = new ArrayList<AccountMeta>(tokens.length);
    for (final int token : tokens) {
      // masked: a frozen entry sets bit 7 in place, and the refresh consumes the same
      // accounts whether or not it is frozen
      final var oracleType = ScopeReader.oracleType(oracleTypeEnums, priceTypes[token]);
      if (oracleType == null) {
        // the whole byte, frozen flag included, so the report is unambiguous
        throw new IllegalStateException("Unknown oracle type 0x"
            + Integer.toHexString(Byte.toUnsignedInt(priceTypes[token]))
            + " at token " + token + "; the deployed program is ahead of this IDL.");
      }
      final var priceAccount = priceInfoAccounts[token];
      final List<AccountMeta> consumed = switch (oracleType) {
        case JupiterLpFetch,
             MeteoraDlmmAtoB, MeteoraDlmmBtoA,
             OrcaWhirlpoolAtoB, OrcaWhirlpoolBtoA,
             SplBalance -> throw new IllegalStateException(oracleType + " requires asset mints as well.");
        case KToken, KTokenToTokenA, KTokenToTokenB -> throw new IllegalStateException(oracleType
            + " requires the strategy's global config, collateral infos, pool, position and scope prices as well.");
        case Securitize -> throw new IllegalStateException(oracleType
            + " requires the vault's share mint and asset vault and its RedStone price account as well.");
        case KlendCTokenExchangeRate ->
            throw new IllegalStateException(oracleType + " requires the klend program and lending market as well.");
        case Canary -> throw new IllegalStateException(oracleType
            + " requires the canary program (CanarFxHDSnbrPmrE79Qq6hL2p7ZMyyV4ZLTKQ6g7tpK on mainnet) as well.");
        case ExponentTranching -> exponentTranchingAccounts(token, priceAccount, exponentTranchingAccounts);
        case Unused,
             DeprecatedPlaceholder1, DeprecatedPlaceholder2, DeprecatedPlaceholder3, DeprecatedPlaceholder4,
             DeprecatedPlaceholder5, DeprecatedPlaceholder6, DeprecatedPlaceholder7,
             SplStake, MsolStake, StakedSolBalance, TotalMintSupply, Token2022Multiplier,
             ScopeTwap1h, ScopeTwap8h, ScopeTwap24h, ScopeTwap7d,
             RaydiumAmmV3AtoB, RaydiumAmmV3BtoA,
             PythPull, PythPullEMA, PythLazer, PythLazerEMA,
             SwitchboardOnDemand, RedStone, JitoRestaking, AdrenaLp, FlashtradeLp,
             Chainlink, ChainlinkRWA, ChainlinkNAV, ChainlinkX, ChainlinkExchangeRate,
             FixedPrice, DiscountToMaturity,
             MostRecentOf, CappedMostRecentOf, CappedFloored, MultiplicationChain, Conditional ->
            List.of(AccountMeta.createRead(priceAccount));
      };
      accountMetas.addAll(consumed);
    }
    return accountMetas;
  }

  private static List<AccountMeta> exponentTranchingAccounts(final int token,
                                                             final PublicKey market,
                                                             final Map<PublicKey, List<AccountMeta>> exponentTranchingAccounts) {
    final var accounts = exponentTranchingAccounts.get(market);
    if (accounts == null) {
      throw new IllegalStateException(OracleType.ExponentTranching + " at token " + token + " requires, after market "
          + market + ", its return model storage, address lookup table and SY program, the event authority ("
          + ExponentTranchingMarket.EVENT_AUTHORITY + "), the Exponent tranching program ("
          + ExponentTranchingMarket.PROGRAM_ID + ") and the market's get_sy_state accounts, with the market, its"
          + " return model storage and each get_sy_state account the market flags writable passed writable;"
          + " build them with ExponentTranchingMarket.refreshAccounts and pass them keyed by the market.");
    }
    final var first = accounts.isEmpty() ? null : accounts.getFirst();
    if (first == null || !first.write() || !market.equals(first.publicKey())) {
      throw new IllegalArgumentException("The accounts given for Exponent tranching market " + market
          + " start with " + first + "; ExponentTranchingMarket.refreshAccounts starts them with the market, writable.");
    }
    return accounts;
  }

  default Instruction refreshPriceList(final Configuration configuration,
                                       final OracleMappings oracleMappings,
                                       final int[] tokens) {
    return refreshPriceList(
        configuration.oraclePrices(),
        configuration.oracleMappings(),
        configuration.oracleTwaps(),
        tokens
    ).extraAccounts(refreshPriceListExtraAccounts(oracleMappings, tokens));
  }

  /// A refresh whose `ExponentTranching` token takes its accounts from
  /// `exponentTranchingAccounts`; pass such a token alone, as
  /// [#refreshPriceListExtraAccounts(OracleMappings, int\[\], Map)] explains.
  default Instruction refreshPriceList(final Configuration configuration,
                                       final OracleMappings oracleMappings,
                                       final int[] tokens,
                                       final Map<PublicKey, List<AccountMeta>> exponentTranchingAccounts) {
    return refreshPriceList(
        configuration.oraclePrices(),
        configuration.oracleMappings(),
        configuration.oracleTwaps(),
        tokens
    ).extraAccounts(refreshPriceListExtraAccounts(oracleMappings, tokens, exponentTranchingAccounts));
  }

  Instruction refreshChainlinkPrice(final PublicKey userKey,
                                    final PublicKey oraclePricesKey,
                                    final PublicKey oracleMappingsKey,
                                    final PublicKey oracleTwapsKey,
                                    final PublicKey verifierAccountKey,
                                    final PublicKey accessControllerKey,
                                    final PublicKey configAccountKey,
                                    final PublicKey verifierProgramIdKey,
                                    final int token,
                                    final byte[] serializedChainlinkReport);

  default Instruction refreshChainlinkPrice(final PublicKey userKey,
                                            final Configuration configuration,
                                            final PublicKey verifierAccountKey,
                                            final PublicKey accessControllerKey,
                                            final PublicKey configAccountKey,
                                            final PublicKey verifierProgramIdKey,
                                            final int token,
                                            final byte[] serializedChainlinkReport) {
    return refreshChainlinkPrice(
        userKey,
        configuration.oraclePrices(),
        configuration.oracleMappings(),
        configuration.oracleTwaps(),
        verifierAccountKey,
        accessControllerKey,
        configAccountKey,
        verifierProgramIdKey,
        token,
        serializedChainlinkReport
    );
  }

  Instruction refreshPythLazerPrice(final PublicKey userKey,
                                    final PublicKey oraclePricesKey,
                                    final PublicKey oracleMappingsKey,
                                    final PublicKey oracleTwapsKey,
                                    final PublicKey pythProgramKey,
                                    final PublicKey pythStorageKey,
                                    final PublicKey pythTreasuryKey,
                                    final int[] tokens,
                                    final byte[] serializedPythMessage,
                                    final int ed25519InstructionIndex);

  default Instruction refreshPythLazerPrice(final PublicKey userKey,
                                            final Configuration configuration,
                                            final PublicKey pythProgramKey,
                                            final PublicKey pythStorageKey,
                                            final PublicKey pythTreasuryKey,
                                            final int[] tokens,
                                            final byte[] serializedPythMessage,
                                            final int ed25519InstructionIndex) {
    return refreshPythLazerPrice(
        userKey,
        configuration.oraclePrices(),
        configuration.oracleMappings(),
        configuration.oracleTwaps(),
        pythProgramKey,
        pythStorageKey,
        pythTreasuryKey,
        tokens,
        serializedPythMessage,
        ed25519InstructionIndex
    );
  }

  Instruction resetTwap(final PublicKey adminKey,
                        final PublicKey configurationKey,
                        final PublicKey oracleTwapsKey,
                        final long token,
                        final String feedName);

  default Instruction resetTwap(final Configuration configuration,
                                final long token,
                                final String feedName) {
    return resetTwap(
        configuration.admin(),
        configuration._address(),
        configuration.oracleTwaps(),
        token,
        feedName
    );
  }

  Instruction setAdminCached(final PublicKey adminKey,
                             final PublicKey configurationKey,
                             final PublicKey newAdmin,
                             final String feedName);

  default Instruction setAdminCached(final Configuration configuration,
                                     final PublicKey newAdmin,
                                     final String feedName) {
    return setAdminCached(
        configuration.admin(),
        configuration._address(),
        newAdmin,
        feedName
    );
  }

  Instruction approveAdminCached(final PublicKey adminCachedKey,
                                 final PublicKey configurationKey,
                                 final String feedName);

  default Instruction approveAdminCached(final Configuration configuration,
                                         final String feedName) {
    return approveAdminCached(configuration.adminCached(), configuration._address(), feedName);
  }

  Instruction createMintMap(final PublicKey adminKey,
                            final PublicKey configurationKey,
                            final PublicKey mappingsKey,
                            final PublicKey seedPk,
                            final long seedId,
                            final int bump,
                            final int[][] scopeChains);

  default Instruction createMintMap(final Configuration configuration,
                                    final PublicKey seedPk,
                                    final long seedId,
                                    final int bump,
                                    final int[][] scopeChains) {
    return createMintMap(
        configuration.admin(),
        configuration._address(),
        configuration.oracleMappings(),
        seedPk,
        seedId,
        bump,
        scopeChains
    );
  }

  Instruction closeMintMap(final PublicKey adminKey,
                           final PublicKey configurationKey,
                           final PublicKey mappingsKey);

  default Instruction closeMintMap(final Configuration configuration) {
    return closeMintMap(
        configuration.admin(),
        configuration._address(),
        configuration.oracleMappings()
    );
  }
}

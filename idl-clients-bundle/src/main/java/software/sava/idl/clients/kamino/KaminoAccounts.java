package software.sava.idl.clients.kamino;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.kamino.lend.KaminoMarketPDAs;
import software.sava.idl.clients.kamino.lend.KaminoReservePDAs;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.kamino.scope.ScopeFeedAccounts;
import software.sava.idl.clients.kamino.vaults.gen.types.VaultState;
import software.sava.rpc.json.http.client.SolanaRpcClient;
import software.sava.rpc.json.http.response.AccountInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.US_ASCII;

public interface KaminoAccounts {

  PublicKey NULL_KEY = PublicKey.fromBase58Encoded("nu11111111111111111111111111111111111111111");

  static boolean isNullKey(final PublicKey key) {
    return key == null || key.equals(PublicKey.NONE) || key.equals(NULL_KEY);
  }

  // https://github.com/Kamino-Finance/klend-sdk/blob/master/examples/utils/constants.ts
  // https://github.com/Kamino-Finance/klend-sdk/blob/master/src/utils/seeds.ts
  // https://github.com/Kamino-Finance/klend/blob/master/programs/klend/src/utils/seeds.rs
  // https://github.com/Kamino-Finance/kvault/blob/master/programs/kvault/src/utils/consts.rs

  KaminoAccounts MAIN_NET = createAccounts(
      "KLend2g3cP87fffoy8q1mQqGKjrxjC8boSyAYavgmjD",
      "284iwGtA9X9aLy3KsyV8uT2pXLARhYbiSi5SiM2g47M2",
      "HFn8GnPADiny6XqUoWE8uRPPxb29ikn4yTuPa9MF2fWJ",
      "FarmsPZpWu9i7Kky8tPN37rs2TpmMrAZrC7S7vJa91Hr",
      // https://github.com/Kamino-Finance/klend-sdk/blob/d097dcb24478de3be2bce20723aa0b17c101b4cd/src/classes/farm_utils.ts#L26
      "6UodrBjL2ZreDy7QdR4YV1oxqMBjVYSEyrFpctqqwGwL",
      "KvauGMspG5k6rtzrqqn7WNn3oZdyKqLKwK2XWQ8FLjd"
  );

  static KaminoAccounts createAccounts(final PublicKey kLendProgram,
                                       final PublicKey mainMarketLUT,
                                       final PublicKey scopePricesProgram,
                                       final PublicKey farmProgram,
                                       final PublicKey farmsGlobalConfig,
                                       final PublicKey kVaultsProgram) {
    final var kVaultsEventAuthority = PublicKey.findProgramAddress(
        List.of("__event_authority".getBytes(US_ASCII)),
        kVaultsProgram
    ).publicKey();

    final var hubbleScopeFeedAccounts = ScopeFeedAccounts.SCOPE_MAINNET_HUBBLE_FEED;
    final var kaminoScopeFeedAccounts = ScopeFeedAccounts.SCOPE_MAINNET_KLEND_FEED;
    final var scopeFeeds = ScopeFeedAccounts.MAINNET_FEEDS.stream().collect(
        Collectors.toUnmodifiableMap(ScopeFeedAccounts::oraclePrices, Function.identity())
    );

    return new KaminoAccountsRecord(
        AccountMeta.createInvoked(kLendProgram),
        mainMarketLUT,
        scopePricesProgram,
        hubbleScopeFeedAccounts, kaminoScopeFeedAccounts,
        scopeFeeds,
        AccountMeta.createInvoked(farmProgram),
        farmsGlobalConfig,
        AccountMeta.createInvoked(kVaultsProgram),
        kVaultsEventAuthority
    );
  }

  static KaminoAccounts createAccounts(final String kLendProgram,
                                       final String mainMarketLUT,
                                       final String scopePricesProgram,
                                       final String farmProgram,
                                       final String farmsGlobalConfig,
                                       final String kVaultsProgram) {
    return createAccounts(
        PublicKey.fromBase58Encoded(kLendProgram),
        PublicKey.fromBase58Encoded(mainMarketLUT),
        PublicKey.fromBase58Encoded(scopePricesProgram),
        PublicKey.fromBase58Encoded(farmProgram),
        PublicKey.fromBase58Encoded(farmsGlobalConfig),
        PublicKey.fromBase58Encoded(kVaultsProgram)
    );
  }

  static ProgramDerivedAddress lendingMarketAuthPda(final PublicKey lendingMarket,
                                                    final PublicKey programId) {

    return PublicKey.findProgramAddress(
        List.of(
            "lma".getBytes(US_ASCII),
            lendingMarket.toByteArray()
        ),
        programId
    );
  }

  default ProgramDerivedAddress lendingMarketAuthPda(final PublicKey lendingMarket) {
    return lendingMarketAuthPda(lendingMarket, kLendProgram());
  }

  /// Seeded on the reserve itself, which is how the deployed program derives it.
  /// Only correct for a reserve being created: klend used to seed these on
  /// `[market, mint]`, and reserves created then keep those addresses — read an
  /// existing reserve's vaults off the account instead.
  static ProgramDerivedAddress reserveLiqSupplyPda(final PublicKey reserve, final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "reserve_liq_supply".getBytes(US_ASCII),
            reserve.toByteArray()
        ),
        programId
    );
  }

  default ProgramDerivedAddress reserveLiqSupplyPda(final PublicKey reserve) {
    return reserveLiqSupplyPda(reserve, kLendProgram());
  }

  /// Seeded on the reserve itself, which is how the deployed program derives it.
  /// Only correct for a reserve being created: klend used to seed these on
  /// `[market, mint]`, and reserves created then keep those addresses — read an
  /// existing reserve's vaults off the account instead.
  static ProgramDerivedAddress reserveFeeVaultPda(final PublicKey reserve, final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "fee_receiver".getBytes(US_ASCII),
            reserve.toByteArray()
        ),
        programId
    );
  }

  default ProgramDerivedAddress reserveFeeVaultPda(final PublicKey reserve) {
    return reserveFeeVaultPda(reserve, kLendProgram());
  }

  /// Seeded on the reserve itself, which is how the deployed program derives it.
  /// Only correct for a reserve being created: klend used to seed these on
  /// `[market, mint]`, and reserves created then keep those addresses — read an
  /// existing reserve's vaults off the account instead.
  static ProgramDerivedAddress reserveCollateralMintPda(final PublicKey reserve, final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "reserve_coll_mint".getBytes(US_ASCII),
            reserve.toByteArray()
        ),
        programId
    );
  }

  default ProgramDerivedAddress reserveCollateralMintPda(final PublicKey reserve) {
    return reserveCollateralMintPda(reserve, kLendProgram());
  }

  /// Seeded on the reserve itself, which is how the deployed program derives it.
  /// Only correct for a reserve being created: klend used to seed these on
  /// `[market, mint]`, and reserves created then keep those addresses — read an
  /// existing reserve's vaults off the account instead.
  static ProgramDerivedAddress reserveCollateralSupplyPda(final PublicKey reserve, final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "reserve_coll_supply".getBytes(US_ASCII),
            reserve.toByteArray()
        ),
        programId
    );
  }

  default ProgramDerivedAddress reserveCollateralSupplyPda(final PublicKey reserve) {
    return reserveCollateralSupplyPda(reserve, kLendProgram());
  }

  static ProgramDerivedAddress userMetadataPda(final PublicKey user,
                                               final PublicKey programId) {

    return PublicKey.findProgramAddress(
        List.of(
            "user_meta".getBytes(US_ASCII),
            user.toByteArray()
        ),
        programId
    );
  }

  default ProgramDerivedAddress userMetadataPda(final PublicKey user) {
    return userMetadataPda(user, kLendProgram());
  }

  static ProgramDerivedAddress referrerTokenStatePda(final PublicKey referrer,
                                                     final PublicKey reserve,
                                                     final PublicKey programId) {
    if (referrer.equals(PublicKey.NONE)) {
      return null;
    }
    return PublicKey.findProgramAddress(
        List.of(
            "referrer_acc".getBytes(US_ASCII),
            referrer.toByteArray(),
            reserve.toByteArray()
        ),
        programId
    );
  }

  default ProgramDerivedAddress referrerTokenStatePda(final PublicKey referrer, final PublicKey reserve) {
    return referrerTokenStatePda(referrer, reserve, kLendProgram());
  }

  static ProgramDerivedAddress referrerStatePda(final PublicKey referrer, final PublicKey programId) {

    return PublicKey.findProgramAddress(
        List.of(
            "ref_state".getBytes(US_ASCII),
            referrer.toByteArray()
        ),
        programId
    );
  }

  default ProgramDerivedAddress referrerStatePda(final PublicKey referrer) {
    return referrerStatePda(referrer, kLendProgram());
  }

  static ProgramDerivedAddress shortUrlPda(final String shortUrl, final PublicKey programId) {

    return PublicKey.findProgramAddress(
        List.of(
            "short_url".getBytes(US_ASCII),
            shortUrl.getBytes(US_ASCII)
        ),
        programId
    );
  }

  default ProgramDerivedAddress shortUrlPda(final String shortUrl) {
    return shortUrlPda(shortUrl, kLendProgram());
  }

  /// The vaults, mint, token program and market of an existing reserve, read off the
  /// account rather than derived — see [KaminoReservePDAs#createPDAs(PublicKey, Reserve)].
  default KaminoReservePDAs createReservePDAs(final Reserve reserve) {
    return KaminoReservePDAs.createPDAs(kLendProgram(), reserve);
  }

  /// The addresses a reserve being created will have. Wrong for most existing reserves.
  default KaminoReservePDAs createPDAsForNewReserve(final KaminoMarketPDAs marketPDAs,
                                                    final PublicKey reserve,
                                                    final PublicKey mint,
                                                    final PublicKey tokenProgram) {
    return KaminoReservePDAs.createPDAsForNewReserve(
        kLendProgram(),
        marketPDAs,
        reserve,
        mint,
        tokenProgram
    );
  }

  static ProgramDerivedAddress cTokenVault(final PublicKey vaultKey,
                                           final PublicKey reserveKey,
                                           final PublicKey programId) {

    return PublicKey.findProgramAddress(
        List.of(
            "ctoken_vault".getBytes(US_ASCII),
            vaultKey.toByteArray(),
            reserveKey.toByteArray()
        ),
        programId
    );
  }

  AccountMeta invokedKLendProgram();

  default PublicKey kLendProgram() {
    return invokedKLendProgram().publicKey();
  }

  default CompletableFuture<List<AccountInfo<byte[]>>> fetchVaults(final SolanaRpcClient rpcClient) {
    final var filters = List.of(VaultState.SIZE_FILTER, VaultState.DISCRIMINATOR_FILTER);
    return rpcClient.getProgramAccounts(kVaultsProgram(), filters);
  }

  PublicKey mainMarketLUT();

  AccountMeta invokedFarmsProgram();

  default PublicKey farmProgram() {
    return invokedFarmsProgram().publicKey();
  }

  PublicKey farmsGlobalConfig();

  AccountMeta invokedKVaultsProgram();

  default PublicKey kVaultsProgram() {
    return invokedKVaultsProgram().publicKey();
  }

  PublicKey kVaultsEventAuthority();

  default ProgramDerivedAddress cTokenVault(final PublicKey vaultKey, final PublicKey reserveKey) {
    return cTokenVault(vaultKey, reserveKey, kVaultsProgram());
  }

  static ProgramDerivedAddress kVaultGlobalConfig(final PublicKey programId) {

    return PublicKey.findProgramAddress(List.of("global_config".getBytes(US_ASCII)), programId);
  }

  default ProgramDerivedAddress kVaultGlobalConfig() {
    return kVaultGlobalConfig(kVaultsProgram());
  }

  PublicKey scopePricesProgram();

  default AccountMeta invokedScopePricesProgram() {
    return AccountMeta.createInvoked(scopePricesProgram());
  }

  ScopeFeedAccounts scopeMainnetHubbleFeed();

  ScopeFeedAccounts scopeMainnetKLendFeed();

  default PublicKey scopeEventAuthority() {
    return PublicKey.findProgramAddress(
        List.of("__event_authority".getBytes(US_ASCII)),
        scopePricesProgram()
    ).publicKey();
  }

  static ProgramDerivedAddress mintsToScopeChain(final PublicKey scopeOraclePrices,
                                                 final PublicKey seedKey,
                                                 final long seedId,
                                                 final PublicKey programId) {
    final byte[] seed = new byte[Long.BYTES];
    ByteUtil.putInt64LE(seed, 0, seedId);
    return PublicKey.findProgramAddress(
        List.of(
            "mints_to_scope_chains".getBytes(US_ASCII),
            scopeOraclePrices.toByteArray(),
            seedKey.toByteArray(),
            seed
        ),
        programId
    );
  }

  default ProgramDerivedAddress mintsToScopeChain(final PublicKey scopeOraclePrices,
                                                  final PublicKey seedKey,
                                                  final long seedId) {
    return mintsToScopeChain(scopeOraclePrices, seedKey, seedId, scopePricesProgram());
  }

  static ProgramDerivedAddress scopeFeedConfiguration(final String feedName, final PublicKey programId) {
    return PublicKey.findProgramAddress(
        List.of(
            "conf".getBytes(US_ASCII),
            feedName.getBytes(US_ASCII)
        ),
        programId
    );
  }

  default ProgramDerivedAddress scopeFeedConfiguration(final String feedName) {
    return scopeFeedConfiguration(feedName, scopePricesProgram());
  }

  /// The scope feed whose oracle-prices account is `priceFeed` — the key a reserve stores
  /// in `config.tokenInfo.scopeConfiguration.priceFeed`, and the account klend checks the
  /// passed `scopePrices` against.
  ///
  /// Covers every feed on mainnet ([ScopeFeedAccounts#MAINNET_FEEDS]), not only the two the
  /// scope SDK names. Returns null for anything else, including the null sentinel and the
  /// default key a reserve carries when its scope feed is disabled — around a fifth of live
  /// reserves — so callers must handle a null rather than treat it as a lookup failure.
  ///
  /// A handful of reserves store a scope *configuration* address here instead of an
  /// oracle-prices one. That is upstream configuration, not a gap in this map: klend only
  /// checks the key matches and then parses the account as `OraclePrices`, so those feeds
  /// cannot refresh at all.
  ScopeFeedAccounts scopeFeed(final PublicKey priceFeed);
}

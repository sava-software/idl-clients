package software.sava.idl.clients.kamino.lend;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.SolanaRpcCaptureTests;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.kamino.farms.gen.types.FarmState;
import software.sava.idl.clients.kamino.lend.gen.types.LendingMarket;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.kamino.lend.gen.types.ReferrerState;
import software.sava.idl.clients.kamino.lend.gen.types.ReferrerTokenState;
import software.sava.idl.clients.kamino.vaults.gen.types.VaultState;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.stream.Collectors;
import software.sava.core.rpc.Filter;

/// Covers the Kamino account scans against the capture harness. Every fetcher
/// selects by a size filter (several account types share the program, so the
/// size *is* the type selector), `fetchReserveAccounts`' market overload adds
/// the lending-market memcmp, and `fetchVaults` runs under the *vaults*
/// program with its discriminator — each pinned via the exact
/// `Filter.toJson()` the client must emit.
final class KaminoRpcFetcherTests extends SolanaRpcCaptureTests {

  private static final KaminoAccounts KAMINO_ACCOUNTS = KaminoAccounts.MAIN_NET;

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  /// The request's whole `filters` array as sava-rpc writes it, so that no other filter, a
  /// `dataSize` among them, is sent beside the ones named.
  private static String filterArray(final Filter... filters) {
    return Arrays.stream(filters).map(Filter::toJson).collect(Collectors.joining(",", "\"filters\":[", "]"));
  }

  @Test
  void lendScansSelectByAccountSize() {
    final var kLend = KAMINO_ACCOUNTS.kLendProgram();

    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchLendingMarkets(rpcClient(), kLend).join().isEmpty());
    assertLastRequestContains("getProgramAccounts", kLend.toBase58(), LendingMarket.SIZE_FILTER.toJson());

    // Referrer states come in two live lengths, so this scan selects by discriminator alone.
    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchReferrerStateAccounts(rpcClient(), kLend).join().isEmpty());
    assertLastRequestContains("getProgramAccounts", kLend.toBase58(), filterArray(ReferrerState.DISCRIMINATOR_FILTER));

    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchReferrerTokenStateAccounts(rpcClient(), kLend).join().isEmpty());
    assertLastRequestContains("getProgramAccounts", kLend.toBase58(), ReferrerTokenState.SIZE_FILTER.toJson());
  }

  /// Farm states belong to the Farms program. Scanned under kLend, as this fetcher's parameter
  /// once named it, the scan matches nothing: 0 accounts at slot 456604915, against 616 under
  /// the Farms program at slot 456604911. It selects by discriminator alone, since live farms
  /// come in more than one length.
  @Test
  void farmStateScanRunsUnderTheFarmsProgram() {
    final var farms = KAMINO_ACCOUNTS.farmProgram();
    assertEquals(PublicKey.fromBase58Encoded("FarmsPZpWu9i7Kky8tPN37rs2TpmMrAZrC7S7vJa91Hr"), farms);

    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchFarmStateAccounts(rpcClient(), farms).join().isEmpty());
    assertLastRequestContains("getProgramAccounts", farms.toBase58(), filterArray(FarmState.DISCRIMINATOR_FILTER));
  }

  /// The market-scoped reserve scan narrows by a memcmp on the reserve's
  /// stored lending market — a wrong offset would silently return every
  /// market's reserves.
  @Test
  void reserveScansSelectBySizeAndOptionallyMarket() {
    final var kLend = KAMINO_ACCOUNTS.kLendProgram();
    final var market = key(0x21);

    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchReserveAccounts(rpcClient(), kLend).join().isEmpty());
    assertLastRequestContains("getProgramAccounts", kLend.toBase58(), Reserve.SIZE_FILTER.toJson());

    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchReserveAccounts(rpcClient(), kLend, market).join().isEmpty());
    assertLastRequestContains(
        "getProgramAccounts",
        kLend.toBase58(),
        Reserve.SIZE_FILTER.toJson(),
        Reserve.createLendingMarketFilter(market).toJson());
  }

  /// `fetchVaults` is the odd one out: it scans the *vaults* program, not
  /// kLend, and pairs the size filter with the `VaultState` discriminator.
  @Test
  void vaultScanRunsUnderTheVaultsProgram() {
    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KAMINO_ACCOUNTS.fetchVaults(rpcClient()).join().isEmpty());
    assertLastRequestContains(
        "getProgramAccounts",
        KAMINO_ACCOUNTS.kVaultsProgram().toBase58(),
        VaultState.SIZE_FILTER.toJson(),
        VaultState.DISCRIMINATOR_FILTER.toJson());
  }
}
